package ahjd.asgHolos.data;

import ahjd.asgHolos.AsgHolos;
import java.lang.ref.WeakReference;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
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
    private final AtomicInteger activeHolograms = new AtomicInteger(0);
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
        World world = data.location().getWorld();
        if (world == null) {
            return null;
        } else {
            int currentHolograms = this.activeHolograms.get();
            Integer maxHolograms = this.config.getMaxHolograms();
            if (maxHolograms != null && currentHolograms >= maxHolograms) {
                this.plugin.getLogger().warning("Maximum holograms limit reached (" + maxHolograms + "). Cannot spawn new hologram.");
                return null;
            } else {
                try {
                    Entity entity = world.spawnEntity(data.location(), EntityType.TEXT_DISPLAY);
                    if (entity instanceof TextDisplay) {
                        TextDisplay textDisplay = (TextDisplay)entity;
                        textDisplay.setInvulnerable(true);
                        textDisplay.setCustomNameVisible(false);
                        textDisplay.setText(data.text());
                        textDisplay.setBillboard(data.billboard());
                        textDisplay.setSeeThrough(data.seeThrough());
                        textDisplay.setShadowed(data.shadowed());
                        textDisplay.setTextOpacity((byte)data.textOpacity());
                        textDisplay.setViewRange((float)data.viewDistance());
                        textDisplay.setAlignment(data.textAlignment());
                        textDisplay.setBackgroundColor(Color.fromARGB(data.backgroundColor()));
                        if (data.scale() != 1.0F) {
                            Vector3f scale = new Vector3f(data.scale(), data.scale(), data.scale());
                            Transformation transform = new Transformation(new Vector3f(0.0F, 0.0F, 0.0F), new Quaternionf(), scale, new Quaternionf());
                            textDisplay.setTransformation(transform);
                        }

                        if (data.billboard() == Billboard.FIXED) {
                            textDisplay.setRotation(data.yaw(), data.pitch());
                        }

                        textDisplay.setMetadata("isHologram", new FixedMetadataValue(this.plugin, true));
                        this.entityCache.put(textDisplay.getUniqueId(), new WeakReference(textDisplay));
                        this.activeHolograms.incrementAndGet();
                        if (this.maintenanceTask != null) {
                            this.maintenanceTask.onHologramSpawned(textDisplay.getUniqueId());
                        }

                        HologramData spawnedData = new HologramData(data.location(), data.text(), data.name(), data.persistent(), data.shadowed(), data.seeThrough(), data.billboard(), data.yaw(), data.pitch(), data.scale(), data.textAlignment(), data.textOpacity(), data.backgroundColor(), data.viewDistance(), textDisplay.getUniqueId());
                        if (data.persistent()) {
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
    }

    public boolean deleteHologram(HologramData data) {
        if (data != null && data.entityUUID() != null) {
            UUID entityUUID = data.entityUUID();
            boolean entityRemoved = false;
            WeakReference<Entity> ref = (WeakReference)this.entityCache.get(entityUUID);
            if (ref != null) {
                Entity cachedEntity = (Entity)ref.get();
                if (cachedEntity != null && cachedEntity.isValid() && cachedEntity instanceof TextDisplay) {
                    cachedEntity.remove();
                    this.entityCache.remove(entityUUID);
                    entityRemoved = true;
                }
            } else {
                entityRemoved = this.searchAndDeleteInWorlds(entityUUID);
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

    private boolean searchAndDeleteInWorlds(UUID entityUUID) {
        Iterator var2 = Bukkit.getWorlds().iterator();

        while(var2.hasNext()) {
            World world = (World)var2.next();
            Iterator var4 = world.getEntitiesByClass(TextDisplay.class).iterator();

            while(var4.hasNext()) {
                TextDisplay display = (TextDisplay)var4.next();
                if (display.getUniqueId().equals(entityUUID)) {
                    display.remove();
                    this.entityCache.remove(entityUUID);
                    return true;
                }
            }
        }

        return false;
    }

    public void populateCache() {
        this.entityCache.clear();
        int count = 0;
        Iterator var2 = Bukkit.getWorlds().iterator();

        while(var2.hasNext()) {
            World world = (World)var2.next();
            Iterator var4 = world.getEntitiesByClass(TextDisplay.class).iterator();

            while(var4.hasNext()) {
                TextDisplay display = (TextDisplay)var4.next();
                if (display.hasMetadata("isHologram")) {
                    this.entityCache.put(display.getUniqueId(), new WeakReference(display));
                    ++count;
                }
            }
        }

        this.cleanCache();
        Bukkit.getLogger().info("Populated cache with " + count + " hologram entities");
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
                    this.entityCache.remove(uuid);
                    this.saveFile.removeHologramByUUID(uuid);
                    entity.remove();
                    this.activeHolograms.decrementAndGet();
                    return true;
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
        return this.activeHolograms.get();
    }

    public Config getConfig() {
        return this.config;
    }

    public boolean isOwnedHologram(TextDisplay display) {
        return display == null ? false : display.hasMetadata("isHologram");
    }
}
