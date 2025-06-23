package ahjd.asgHolos.data;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.TextDisplay;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class HologramMaintenanceTask {

    private final JavaPlugin plugin;
    private final HologramManager hologramManager;
    private BukkitTask task;

    // Cache to track alive entities and avoid duplicate spawning
    private final Set<UUID> aliveEntities = ConcurrentHashMap.newKeySet();
    private final Set<UUID> pendingSpawns = ConcurrentHashMap.newKeySet();

    private static final long TICKS_BETWEEN_RUNS = 20L * 60 * 5; // 5 minutes

    public HologramMaintenanceTask(JavaPlugin plugin, HologramManager hologramManager) {
        this.plugin = plugin;
        this.hologramManager = hologramManager;
    }

    public void start() {
        if (task != null) return; // Already running

        // Initial cache population
        populateAliveEntitiesCache();

        task = Bukkit.getScheduler().runTaskTimer(plugin, this::runMaintenance, 0L, TICKS_BETWEEN_RUNS);
    }

    public void stop() {
        if (task != null) {
            task.cancel();
            task = null;
        }
        aliveEntities.clear();
        pendingSpawns.clear();
    }

    private void populateAliveEntitiesCache() {
        aliveEntities.clear();
        for (World world : Bukkit.getWorlds()) {
            world.getEntitiesByClass(TextDisplay.class).forEach(display ->
                    aliveEntities.add(display.getUniqueId())
            );
        }
    }

    private void runMaintenance() {
        // Run async to avoid blocking main thread
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            List<HologramData> savedHolograms = hologramManager.getSaveFile().getSavedHolograms();

            if (savedHolograms.isEmpty()) return;
            hologramManager.cleanCache();

            // Collect saved UUIDs (filter out nulls)
            Set<UUID> savedUUIDs = savedHolograms.stream()
                    .map(HologramData::entityUUID)
                    .filter(Objects::nonNull)
                    .collect(Collectors.toSet());

            // Switch back to main thread for entity operations
            Bukkit.getScheduler().runTask(plugin, () -> {
                Set<UUID> currentlyAlive = scanAndCleanupEntities(savedUUIDs);
                spawnMissingHolograms(savedHolograms, currentlyAlive);
            });
        });
    }

    private Set<UUID> scanAndCleanupEntities(Set<UUID> savedUUIDs) {
        Set<UUID> currentlyAlive = new HashSet<>();
        int orphansRemoved = 0;

        for (World world : Bukkit.getWorlds()) {
            Iterator<TextDisplay> displayIterator = world.getEntitiesByClass(TextDisplay.class).iterator();

            while (displayIterator.hasNext()) {
                TextDisplay display = displayIterator.next();
                UUID displayUUID = display.getUniqueId();

                if (savedUUIDs.contains(displayUUID)) {
                    // This is a valid hologram
                    currentlyAlive.add(displayUUID);
                } else {
                    // This is an orphan - remove it
                    display.remove();
                    orphansRemoved++;
                    aliveEntities.remove(displayUUID);
                }
            }
        }

        if (orphansRemoved > 0) {
            plugin.getLogger().info("Removed " + orphansRemoved + " orphan TextDisplay entities");
        }

        // Update cache
        aliveEntities.retainAll(currentlyAlive);
        aliveEntities.addAll(currentlyAlive);

        return currentlyAlive;
    }

    private void spawnMissingHolograms(List<HologramData> savedHolograms, Set<UUID> currentlyAlive) {
        List<HologramData> toSpawn = new ArrayList<>();

        for (HologramData holo : savedHolograms) {
            UUID uuid = holo.entityUUID();

            // Only spawn if UUID exists but entity is not alive
            if (uuid != null && !currentlyAlive.contains(uuid) && !pendingSpawns.contains(uuid)) {
                toSpawn.add(holo);
            }
        }

        if (toSpawn.isEmpty()) return;

        plugin.getLogger().info("Spawning " + toSpawn.size() + " missing holograms");
        spawnHologramsInBatches(toSpawn);
    }

    private void spawnHologramsInBatches(List<HologramData> toSpawn) {
        final int BATCH_SIZE = 5;
        final int TICKS_BETWEEN_BATCHES = 2;

        for (int i = 0; i < toSpawn.size(); i += BATCH_SIZE) {
            final int startIndex = i;
            final int endIndex = Math.min(i + BATCH_SIZE, toSpawn.size());
            final List<HologramData> batch = toSpawn.subList(startIndex, endIndex);

            Bukkit.getScheduler().runTaskLater(plugin, () -> {
                int spawned = 0;
                for (HologramData holo : batch) {
                    UUID oldUUID = holo.entityUUID();
                    if (oldUUID != null) {
                        pendingSpawns.add(oldUUID);
                    }

                    try {
                        // IMPORTANT: Don't save again during maintenance spawn!
                        // Create non-persistent version to avoid double-saving
                        HologramData tempData = new HologramData(
                                holo.location(), holo.text(), false, // Set persistent to false!
                                holo.shadowed(), holo.seeThrough(), holo.billboard(), oldUUID
                        );

                        HologramData spawnedHolo = hologramManager.spawnHologram(tempData);
                        if (spawnedHolo != null) {
                            // Update the saved hologram with the new UUID
                            HologramData updatedData = new HologramData(
                                    holo.location(), holo.text(), holo.persistent(),
                                    holo.shadowed(), holo.seeThrough(), holo.billboard(),
                                    spawnedHolo.entityUUID()
                            );
                            hologramManager.getSaveFile().removeHologramByUUID(oldUUID);
                            hologramManager.getSaveFile().saveHologram(updatedData);

                            aliveEntities.add(spawnedHolo.entityUUID());
                            spawned++;
                        }
                    } catch (Exception e) {
                        plugin.getLogger().warning("Failed to spawn hologram during maintenance: " + e.getMessage());
                    } finally {
                        if (oldUUID != null) {
                            pendingSpawns.remove(oldUUID);
                        }
                    }
                }

                if (spawned > 0) {
                    plugin.getLogger().info("Maintenance spawned " + spawned + " holograms");
                }
            }, (long) (i / BATCH_SIZE) * TICKS_BETWEEN_BATCHES);
        }
    }

    // Public methods to update cache when holograms are created/removed elsewhere
    public void onHologramSpawned(UUID entityUUID) {
        if (entityUUID != null) {
            aliveEntities.add(entityUUID);
        }
    }

    public void onHologramRemoved(UUID entityUUID) {
        if (entityUUID != null) {
            aliveEntities.remove(entityUUID);
        }
    }

    // Method to manually refresh cache if needed
    public void refreshCache() {
        Bukkit.getScheduler().runTask(plugin, this::populateAliveEntitiesCache);
    }
}