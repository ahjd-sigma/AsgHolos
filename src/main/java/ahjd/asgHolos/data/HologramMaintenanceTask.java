package ahjd.asgHolos.data;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.TextDisplay;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

public class HologramMaintenanceTask {
    private final JavaPlugin plugin;
    private final HologramManager hologramManager;
    private BukkitTask task;
    private final Set<UUID> aliveEntities = ConcurrentHashMap.newKeySet();
    private final Set<UUID> pendingSpawns = ConcurrentHashMap.newKeySet();
    private final long ticksBetweenRuns;

    public HologramMaintenanceTask(JavaPlugin plugin, HologramManager hologramManager, int intervalSeconds) {
        this.plugin = plugin;
        this.hologramManager = hologramManager;
        this.ticksBetweenRuns = (long)intervalSeconds * 20L;
    }

    public void start() {
        if (this.task == null) {
            this.populateAliveEntitiesCache();
            this.task = Bukkit.getScheduler().runTaskTimer(this.plugin, this::runMaintenance, 0L, this.ticksBetweenRuns);
        }
    }

    public void stop() {
        if (this.task != null) {
            this.task.cancel();
            this.task = null;
        }

        this.aliveEntities.clear();
        this.pendingSpawns.clear();
    }

    private void populateAliveEntitiesCache() {
        this.aliveEntities.clear();
        Iterator var1 = Bukkit.getWorlds().iterator();

        while(var1.hasNext()) {
            World world = (World)var1.next();
            world.getEntitiesByClass(TextDisplay.class).forEach((display) -> {
                this.aliveEntities.add(display.getUniqueId());
            });
        }

    }

    private void runMaintenance() {
        Bukkit.getScheduler().runTaskAsynchronously(this.plugin, () -> {
            List<HologramData> savedHolograms = this.hologramManager.getSaveFile().getSavedHolograms();
            this.hologramManager.cleanCache();
            Bukkit.getScheduler().runTask(this.plugin, () -> {
                this.deleteAllTextDisplays();
                this.spawnAllPersistentHolograms(savedHolograms);
            });
        });
    }

    private void deleteAllTextDisplays() {
        int totalDeleted = 0;
        Iterator var2 = Bukkit.getWorlds().iterator();

        while(var2.hasNext()) {
            World world = (World)var2.next();
            Collection<TextDisplay> displays = world.getEntitiesByClass(TextDisplay.class);
            int worldDeleted = 0;
            Iterator var6 = displays.iterator();

            while(var6.hasNext()) {
                TextDisplay display = (TextDisplay)var6.next();
                if (display.hasMetadata("isHologram")) {
                    display.remove();
                    ++worldDeleted;
                }
            }

            if (worldDeleted > 0) {
                totalDeleted += worldDeleted;
                this.plugin.getLogger().info("Deleted " + worldDeleted + " hologram entities in world: " + world.getName());
            }
        }

        if (totalDeleted > 0) {
            this.plugin.getLogger().info("Maintenance deleted " + totalDeleted + " total hologram entities");
        }

        this.aliveEntities.clear();
        this.pendingSpawns.clear();
    }

    private void spawnAllPersistentHolograms(List<HologramData> savedHolograms) {
        if (savedHolograms.isEmpty()) {
            this.plugin.getLogger().info("No persistent holograms to spawn");
        } else {
            int currentHolograms = this.hologramManager.getActiveHolograms();
            int maxHolograms = this.hologramManager.getConfig().getMaxHolograms();
            int totalHolograms = currentHolograms + savedHolograms.size();
            if (totalHolograms > maxHolograms) {
                this.plugin.getLogger().warning("Cannot spawn all saved holograms - would exceed max limit");
                this.plugin.getLogger().warning("Current: " + currentHolograms + ", Saved: " + savedHolograms.size() + ", Max: " + maxHolograms);
                int hologramsToSpawn = maxHolograms - currentHolograms;
                if (hologramsToSpawn > 0) {
                    List<HologramData> spawnable = savedHolograms.subList(0, hologramsToSpawn);
                    this.plugin.getLogger().info("Spawning " + hologramsToSpawn + " out of " + savedHolograms.size() + " saved holograms due to max limit");
                    this.spawnHologramsInBatches(spawnable);
                } else {
                    this.plugin.getLogger().info("No holograms will be spawned due to max limit being reached");
                }
            } else {
                this.plugin.getLogger().info("Spawning " + savedHolograms.size() + " persistent holograms");
                this.spawnHologramsInBatches(savedHolograms);
            }

        }
    }

    private void spawnHologramsInBatches(List<HologramData> toSpawn) {

        for(int i = 0; i < toSpawn.size(); i += 5) {
            int endIndex = Math.min(i + 5, toSpawn.size());
            List<HologramData> batch = toSpawn.subList(i, endIndex);
            Bukkit.getScheduler().runTaskLater(this.plugin, () -> {
                int spawned = 0;
                Iterator var3 = batch.iterator();

                while(var3.hasNext()) {
                    HologramData holo = (HologramData)var3.next();
                    UUID oldUUID = holo.entityUUID();
                    if (oldUUID != null) {
                        this.pendingSpawns.add(oldUUID);
                    }

                    try {
                        HologramData tempData = new HologramData(holo.location(), holo.text(), holo.name(), holo.persistent(), holo.shadowed(), holo.seeThrough(), holo.billboard(), holo.yaw(), holo.pitch(), holo.scale(), holo.textAlignment(), holo.textOpacity(), holo.backgroundColor(), holo.viewDistance(), (UUID)null);
                        HologramData spawnedHolo = this.hologramManager.spawnHologram(tempData);
                        if (spawnedHolo != null) {
                            if (oldUUID != null) {
                                this.hologramManager.getSaveFile().removeHologramByUUID(oldUUID);
                            }

                            this.hologramManager.getSaveFile().saveHologram(spawnedHolo);
                            this.aliveEntities.add(spawnedHolo.entityUUID());
                            ++spawned;
                        }
                    } catch (Exception var11) {
                        this.plugin.getLogger().warning("Failed to spawn hologram during maintenance: " + var11.getMessage());
                    } finally {
                        if (oldUUID != null) {
                            this.pendingSpawns.remove(oldUUID);
                        }

                    }
                }

                if (spawned > 0) {
                    this.plugin.getLogger().info("Maintenance spawned " + spawned + " holograms in batch");
                }

            }, (long)(i / 5) * 2L);
        }

    }

    public int getActiveHolograms() {
        return this.hologramManager.getActiveHolograms();
    }

    public Config getConfig() {
        return this.hologramManager.getConfig();
    }

    public void onHologramSpawned(UUID entityUUID) {
        if (entityUUID != null) {
            this.aliveEntities.add(entityUUID);
        }

    }

    public void onHologramRemoved(UUID entityUUID) {
        if (entityUUID != null) {
            this.aliveEntities.remove(entityUUID);
        }

    }

    public void refreshCache() {
        Bukkit.getScheduler().runTask(this.plugin, this::populateAliveEntitiesCache);
    }
}
