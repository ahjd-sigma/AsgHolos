package ahjd.asgHolos.data;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.TextDisplay;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class HologramManager {

    private final SaveFile saveFile;
    private HologramMaintenanceTask maintenanceTask;
    private final Map<UUID, Entity> entityCache = new ConcurrentHashMap<>();

    public HologramManager(SaveFile saveFile) {
        this.saveFile = saveFile;
    }

    public void setMaintenanceTask(HologramMaintenanceTask maintenanceTask) {
        this.maintenanceTask = maintenanceTask;
    }

    public HologramData spawnHologram(HologramData data) {
        World world = data.location().getWorld();
        if (world == null) {
            return null;
        }

        try {
            Entity entity = world.spawnEntity(data.location(), EntityType.TEXT_DISPLAY);
            if (entity instanceof TextDisplay textDisplay) {
                // Configure the text display
                textDisplay.setInvulnerable(true);
                textDisplay.setCustomNameVisible(false);
                textDisplay.setText(data.text());
                textDisplay.setBillboard(data.billboard());
                textDisplay.setSeeThrough(data.seeThrough());
                textDisplay.setShadowed(data.shadowed());

                // Add to cache
                entityCache.put(textDisplay.getUniqueId(), textDisplay);

                // Notify maintenance task if available
                if (maintenanceTask != null) {
                    maintenanceTask.onHologramSpawned(textDisplay.getUniqueId());
                }

                // Create new data with spawned entity's UUID
                HologramData spawnedData = new HologramData(
                        data.location(),
                        data.text(),
                        data.persistent(),
                        data.shadowed(),
                        data.seeThrough(),
                        data.billboard(),
                        textDisplay.getUniqueId()
                );

                // Only save persistent holograms
                if (data.persistent()) {
                    saveFile.saveHologram(spawnedData);
                }

                return spawnedData;
            }
        } catch (Exception e) {
            Bukkit.getLogger().warning("Failed to spawn hologram: " + e.getMessage());
        }
        return null;
    }

    public boolean deleteHologram(HologramData data) {
        if (data == null || data.entityUUID() == null) return false;

        UUID entityUUID = data.entityUUID();
        boolean entityRemoved = false;

        // Try cache first for O(1) lookup
        Entity cachedEntity = entityCache.get(entityUUID);
        if (cachedEntity != null && cachedEntity.isValid() && cachedEntity instanceof TextDisplay) {
            cachedEntity.remove();
            entityCache.remove(entityUUID);
            entityRemoved = true;
        } else {
            // Fallback to world search if not in cache
            entityRemoved = searchAndDeleteInWorlds(entityUUID);
        }

        // Always try to remove from file regardless of entity removal result
        boolean fileRemoved = saveFile.removeHologramByUUID(entityUUID);

        // Notify maintenance task
        if (maintenanceTask != null) {
            maintenanceTask.onHologramRemoved(entityUUID);
        }

        return entityRemoved || fileRemoved;
    }

    private boolean searchAndDeleteInWorlds(UUID entityUUID) {
        for (World world : Bukkit.getWorlds()) {
            for (TextDisplay display : world.getEntitiesByClass(TextDisplay.class)) {
                if (display.getUniqueId().equals(entityUUID)) {
                    display.remove();
                    entityCache.remove(entityUUID);
                    return true;
                }
            }
        }
        return false;
    }

    public void populateCache() {
        entityCache.clear();
        int count = 0;
        for (World world : Bukkit.getWorlds()) {
            for (TextDisplay display : world.getEntitiesByClass(TextDisplay.class)) {
                entityCache.put(display.getUniqueId(), display);
                count++;
            }
        }
        cleanCache();
        Bukkit.getLogger().info("Populated cache with " + count + " TextDisplay entities");
    }

    public void cleanCache() {
        // Use AtomicInteger to avoid lambda effectively final issue
        AtomicInteger removed = new AtomicInteger(0);

        entityCache.entrySet().removeIf(entry -> {
            if (!entry.getValue().isValid()) {
                removed.incrementAndGet();
                return true;
            }
            return false;
        });

        if (removed.get() > 0) {
            Bukkit.getLogger().info("Cleaned " + removed.get() + " invalid entities from cache");
        }
    }

    public SaveFile getSaveFile() {
        return saveFile;
    }

    public Map<UUID, Entity> getEntityCache() {
        return entityCache;
    }
}