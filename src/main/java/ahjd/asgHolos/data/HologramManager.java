package ahjd.asgHolos.data;

import ahjd.asgHolos.AsgHolos;
import ahjd.asgHolos.api.events.HologramCreateEvent;
import ahjd.asgHolos.api.events.HologramDeleteEvent;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.TextDisplay;
import org.bukkit.entity.Display.Billboard;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Transformation;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public class HologramManager {
    private final SaveFile saveFile;
    private HologramMaintenanceTask maintenanceTask;
    private final Map<UUID, WeakReference<Entity>> entityCache = new ConcurrentHashMap();
    private final AtomicInteger activeTempHolograms = new AtomicInteger(0);
    private final AtomicInteger activePersistentHolograms = new AtomicInteger(0);
    private final JavaPlugin plugin;
    private final Config config;

    public HologramManager(SaveFile saveFile, JavaPlugin plugin) {
        this.saveFile = saveFile;
        this.plugin = plugin;
        this.config = ((AsgHolos)plugin).config;
    }

    public void setMaintenanceTask(HologramMaintenanceTask maintenanceTask) {
        this.maintenanceTask = maintenanceTask;
    }

    public HologramData spawnHologram(HologramData data) {
        return spawnHologram(data, null, HologramCreateEvent.CreationSource.PLAYER_COMMAND);
    }
    
    public HologramData spawnHologram(HologramData data, Player creator, HologramCreateEvent.CreationSource source) {
        // Fire HologramCreateEvent
        HologramCreateEvent createEvent = new HologramCreateEvent(data, creator, source);
        Bukkit.getPluginManager().callEvent(createEvent);
        
        // Check if event was cancelled
        if (createEvent.isCancelled()) {
            return null;
        }
        
        // Use potentially modified data from event
        HologramData finalData = createEvent.getHologramData();
        
        World world = finalData.location().getWorld();
        if (world == null) {
            return null;
        } else {
            // Check limits based on hologram type
            if (finalData.persistent()) {
                int currentPersistent = this.activePersistentHolograms.get();
                Integer maxPersistent = this.config.getMaxPersistentHolograms();
                if (maxPersistent != null && currentPersistent >= maxPersistent) {
                    this.plugin.getLogger().warning("Maximum persistent holograms limit reached (" + maxPersistent + "). Cannot spawn new hologram.");
                    return null;
                }
            } else {
                int currentTemp = this.activeTempHolograms.get();
                Integer maxTemp = this.config.getMaxTempHolograms();
                if (maxTemp != null && currentTemp >= maxTemp) {
                    this.plugin.getLogger().warning("Maximum temporary holograms limit reached (" + maxTemp + "). Cannot spawn new hologram.");
                    return null;
                }
            }
                try {
                    Entity entity = world.spawnEntity(finalData.location(), EntityType.TEXT_DISPLAY);
                    if (entity instanceof TextDisplay) {
                        TextDisplay textDisplay = (TextDisplay)entity;
                        textDisplay.setInvulnerable(true);
                        textDisplay.setCustomNameVisible(false);
                        textDisplay.setText(finalData.text());
                        textDisplay.setBillboard(finalData.billboard());
                        textDisplay.setSeeThrough(finalData.seeThrough());
                        textDisplay.setShadowed(finalData.shadowed());
                        textDisplay.setTextOpacity((byte)finalData.textOpacity());
                        textDisplay.setViewRange((float)finalData.viewDistance());
                        textDisplay.setAlignment(finalData.textAlignment());
                        textDisplay.setBackgroundColor(Color.fromARGB(finalData.backgroundColor()));
                        if (finalData.scale() != 1.0F) {
                            Vector3f scale = new Vector3f(finalData.scale(), finalData.scale(), finalData.scale());
                            Transformation transform = new Transformation(new Vector3f(0.0F, 0.0F, 0.0F), new Quaternionf(), scale, new Quaternionf());
                            textDisplay.setTransformation(transform);
                        }

                        if (finalData.billboard() == Billboard.FIXED) {
                            textDisplay.setRotation(finalData.yaw(), finalData.pitch());
                        }

                        textDisplay.setMetadata("isHologram", new FixedMetadataValue(this.plugin, true));
                        this.entityCache.put(textDisplay.getUniqueId(), new WeakReference(textDisplay));
                        
                        // Increment appropriate counter
                        if (finalData.persistent()) {
                            this.activePersistentHolograms.incrementAndGet();
                        } else {
                            this.activeTempHolograms.incrementAndGet();
                        }
                        if (this.maintenanceTask != null) {
                            this.maintenanceTask.onHologramSpawned(textDisplay.getUniqueId());
                        }

                        HologramData spawnedData = new HologramData(finalData.location(), finalData.text(), finalData.name(), finalData.persistent(), finalData.shadowed(), finalData.seeThrough(), finalData.billboard(), finalData.yaw(), finalData.pitch(), finalData.scale(), finalData.textAlignment(), finalData.textOpacity(), finalData.backgroundColor(), finalData.viewDistance(), textDisplay.getUniqueId());
                        if (finalData.persistent()) {
                            this.saveFile.saveHologram(spawnedData);
                        }

                        return spawnedData;
                    }
                } catch (Exception var9) {
                    Bukkit.getLogger().warning("Failed to spawn hologram: " + var9.getMessage());
                }

                return null;
            }
        }


    public boolean deleteHologram(HologramData data) {
        return deleteHologram(data, null, HologramDeleteEvent.DeletionSource.PLAYER_COMMAND);
    }
    
    public boolean deleteHologram(HologramData data, Player deleter, HologramDeleteEvent.DeletionSource source) {
        if (data != null && data.entityUUID() != null) {
            // Fire HologramDeleteEvent
            HologramDeleteEvent deleteEvent = new HologramDeleteEvent(data, deleter, source);
            Bukkit.getPluginManager().callEvent(deleteEvent);
            
            // Check if event was cancelled
            if (deleteEvent.isCancelled()) {
                return false;
            }
            
            UUID entityUUID = data.entityUUID();
            boolean entityRemoved = false;
            WeakReference<Entity> ref = (WeakReference)this.entityCache.get(entityUUID);
            if (ref != null) {
                Entity cachedEntity = (Entity)ref.get();
                if (cachedEntity != null && cachedEntity.isValid() && cachedEntity instanceof TextDisplay) {
                    cachedEntity.remove();
                    this.entityCache.remove(entityUUID);
                    entityRemoved = true;
                    
                    // Decrement appropriate counter
                    if (data.persistent()) {
                        this.activePersistentHolograms.decrementAndGet();
                    } else {
                        this.activeTempHolograms.decrementAndGet();
                    }
                }
            } else {
                entityRemoved = this.searchAndDeleteInWorlds(entityUUID, data.persistent());
            }

            boolean fileRemoved = this.saveFile.removeHologramByUUID(entityUUID);
            if (this.maintenanceTask != null) {
                this.maintenanceTask.onHologramRemoved(entityUUID);
            }

            return entityRemoved || fileRemoved;
        } else {
            return false;
        }
    }

    private boolean searchAndDeleteInWorlds(UUID entityUUID, boolean isPersistent) {
        Iterator var2 = Bukkit.getWorlds().iterator();

        while(var2.hasNext()) {
            World world = (World)var2.next();
            Iterator var4 = world.getEntitiesByClass(TextDisplay.class).iterator();

            while(var4.hasNext()) {
                TextDisplay display = (TextDisplay)var4.next();
                if (display.getUniqueId().equals(entityUUID)) {
                    display.remove();
                    this.entityCache.remove(entityUUID);
                    
                    // Decrement appropriate counter
                    if (isPersistent) {
                        this.activePersistentHolograms.decrementAndGet();
                    } else {
                        this.activeTempHolograms.decrementAndGet();
                    }
                    return true;
                }
            }
        }

        return false;
    }

    public void populateCache() {
        this.entityCache.clear();
        this.activeTempHolograms.set(0);
        this.activePersistentHolograms.set(0);
        
        int totalCount = 0;
        int persistentCount = 0;
        int tempCount = 0;
        
        // First, get all saved holograms to check against
        List<HologramData> savedHolograms = this.saveFile.getSavedHolograms();
        Set<UUID> savedUUIDs = new java.util.HashSet<>();
        for (HologramData data : savedHolograms) {
            if (data.entityUUID() != null) {
                savedUUIDs.add(data.entityUUID());
            }
        }
        
        Bukkit.getLogger().info("Found " + savedUUIDs.size() + " saved hologram UUIDs");
        
        Iterator var2 = Bukkit.getWorlds().iterator();

        while(var2.hasNext()) {
            World world = (World)var2.next();
            Iterator var4 = world.getEntitiesByClass(TextDisplay.class).iterator();

            while(var4.hasNext()) {
                TextDisplay display = (TextDisplay)var4.next();
                if (display.hasMetadata("isHologram")) {
                    UUID uuid = display.getUniqueId();
                    this.entityCache.put(uuid, new WeakReference(display));
                    
                    // Check if this hologram is persistent or temporary
                    boolean isPersistent = savedUUIDs.contains(uuid);
                    if (isPersistent) {
                        this.activePersistentHolograms.incrementAndGet();
                        persistentCount++;
                    } else {
                        this.activeTempHolograms.incrementAndGet();
                        tempCount++;
                    }
                    totalCount++;
                }
            }
        }

        this.cleanCache();
        Bukkit.getLogger().info("Populated cache with " + totalCount + " hologram entities (" + persistentCount + " persistent, " + tempCount + " temporary)");
    }

    public Entity getEntity(UUID uuid) {
        if (uuid == null) {
            return null;
        } else {
            WeakReference<Entity> ref = (WeakReference)this.entityCache.get(uuid);
            if (ref == null) {
                return null;
            } else {
                Entity entity = (Entity)ref.get();
                return entity != null && entity.isValid() ? entity : null;
            }
        }
    }

    public void cleanCache() {
        AtomicInteger removed = new AtomicInteger(0);
        this.entityCache.entrySet().removeIf((entry) -> {
            Entity entity = (Entity)((WeakReference)entry.getValue()).get();
            if (entity != null && entity.isValid()) {
                return false;
            } else {
                removed.incrementAndGet();
                return true;
            }
        });
        if (removed.get() > 0) {
            Bukkit.getLogger().info("Cleaned " + removed.get() + " invalid entities from cache");
        }

    }
    
    /**
     * Cleans up all temporary holograms from the world and updates counters.
     * This should be called during plugin shutdown to prevent ghost holograms on restart.
     */
    public void cleanupTemporaryHolograms() {
        int removedCount = 0;
        List<UUID> temporaryHologramsToRemove = new ArrayList<>();
        
        // First identify all temporary holograms
        for (Map.Entry<UUID, WeakReference<Entity>> entry : this.entityCache.entrySet()) {
            Entity entity = entry.getValue().get();
            if (entity != null && entity.isValid() && entity instanceof TextDisplay) {
                // Check if this is a temporary hologram (not in saved holograms)
                HologramData savedData = this.saveFile.getHologramByUUID(entity.getUniqueId());
                if (savedData == null || !savedData.persistent()) {
                    temporaryHologramsToRemove.add(entry.getKey());
                }
            }
        }
        
        // Then remove them all
        for (UUID uuid : temporaryHologramsToRemove) {
            WeakReference<Entity> ref = this.entityCache.get(uuid);
            if (ref != null) {
                Entity entity = ref.get();
                if (entity != null && entity.isValid()) {
                    entity.remove();
                    removedCount++;
                }
                this.entityCache.remove(uuid);
            }
        }
        
        // Reset temporary hologram counter
        this.activeTempHolograms.set(0);
        
        Bukkit.getLogger().info("Cleaned up " + removedCount + " temporary holograms");
    }

    public boolean deleteHologram(UUID uuid) {
        if (uuid == null) {
            return false;
        } else {
            WeakReference<Entity> ref = (WeakReference)this.entityCache.get(uuid);
            if (ref == null) {
                return false;
            } else {
                Entity entity = (Entity)ref.get();
                if (entity != null && entity.isValid()) {
                    HologramData hologramData = this.saveFile.getHologramByUUID(uuid);
                    if (hologramData != null) {
                        // Use event-aware deletion for proper event firing
                        return deleteHologram(hologramData, null, HologramDeleteEvent.DeletionSource.MAINTENANCE_TASK);
                    } else {
                        // Fallback for cases where hologram data is not found
                        this.entityCache.remove(uuid);
                        this.saveFile.removeHologramByUUID(uuid);
                        entity.remove();
                        // Default to persistent if unknown
                        this.activePersistentHolograms.decrementAndGet();
                        return true;
                    }
                } else {
                    return false;
                }
            }
        }
    }

    public SaveFile getSaveFile() {
        return this.saveFile;
    }

    public Map<UUID, Entity> getEntityCache() {
        Map<UUID, Entity> strongCache = new ConcurrentHashMap();
        this.entityCache.forEach((uuid, ref) -> {
            Entity entity = (Entity)ref.get();
            if (entity != null && entity.isValid()) {
                strongCache.put(uuid, entity);
            }

        });
        return strongCache;
    }

    public int getActiveHolograms() {
        return this.activeTempHolograms.get() + this.activePersistentHolograms.get();
    }
    
    public int getActiveTempHolograms() {
        return this.activeTempHolograms.get();
    }
    
    /**
     * Resets the hologram counters to zero.
     * This should be called during maintenance after all holograms are deleted.
     */
    public void resetCounters() {
        this.activeTempHolograms.set(0);
        this.activePersistentHolograms.set(0);
        Bukkit.getLogger().info("Reset hologram counters to zero");
    }
    
    public int getActivePersistentHolograms() {
        return this.activePersistentHolograms.get();
    }
    


    public Config getConfig() {
        return this.config;
    }

    public boolean isOwnedHologram(TextDisplay display) {
        return display == null ? false : display.hasMetadata("isHologram");
    }
}
