package ahjd.asgHolos.data;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Display.Billboard;
import org.bukkit.entity.TextDisplay.TextAlignment;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

public class SaveFile {
    private final Plugin plugin;
    private final File file;
    private final FileConfiguration config;
    private final Map<UUID, HologramData> savedHolograms = new ConcurrentHashMap();
    private boolean isDirty = false;

    public SaveFile(JavaPlugin plugin) {
        this.file = new File(plugin.getDataFolder(), "holograms.yml");
        this.config = YamlConfiguration.loadConfiguration(this.file);
        this.plugin = plugin;
        this.loadFromFile();
    }

    public void saveHologram(HologramData data) {
        if (data != null && data.entityUUID() != null) {
            this.savedHolograms.put(data.entityUUID(), data);
            this.saveHologramToConfig(data);
            this.saveConfigToFile();
            this.plugin.getLogger().info("Saved hologram with UUID: " + String.valueOf(data.entityUUID()));
        }
    }

    public boolean removeHologram(HologramData data) {
        return data != null && data.entityUUID() != null ? this.removeHologramByUUID(data.entityUUID()) : false;
    }

    public boolean removeHologramByUUID(UUID uuid) {
        if (uuid == null) {
            return false;
        } else {
            HologramData removed = (HologramData)this.savedHolograms.remove(uuid);
            if (removed != null) {
                this.removeHologramFromConfig(uuid);
                this.saveConfigToFile();
                this.plugin.getLogger().info("Removed hologram with UUID: " + String.valueOf(uuid));
                return true;
            } else {
                return false;
            }
        }
    }

    public List<HologramData> getSavedHolograms() {
        return new ArrayList(this.savedHolograms.values());
    }

    public HologramData getHologramByUUID(UUID uuid) {
        return (HologramData)this.savedHolograms.get(uuid);
    }

    public boolean hasHologram(UUID uuid) {
        return this.savedHolograms.containsKey(uuid);
    }

    public int getHologramCount() {
        return this.savedHolograms.size();
    }

    private void saveHologramToConfig(HologramData holo) {
        String path = "holograms." + holo.entityUUID().toString();
        this.config.set(path + ".text", holo.text());
        this.config.set(path + ".name", holo.name());
        this.config.set(path + ".persistent", holo.persistent());
        this.config.set(path + ".shadowed", holo.shadowed());
        this.config.set(path + ".seeThrough", holo.seeThrough());
        this.config.set(path + ".billboard", holo.billboard().name());
        Location loc = holo.location();
        this.config.set(path + ".world", loc.getWorld().getName());
        this.config.set(path + ".x", loc.getX());
        this.config.set(path + ".y", loc.getY());
        this.config.set(path + ".z", loc.getZ());
        this.config.set(path + ".yaw", holo.yaw());
        this.config.set(path + ".pitch", holo.pitch());
        this.config.set(path + ".scale", holo.scale());
        this.config.set(path + ".textAlignment", holo.textAlignment().name());
        this.config.set(path + ".textOpacity", holo.textOpacity());
        this.config.set(path + ".backgroundColor", holo.backgroundColor());
        this.config.set(path + ".viewDistance", holo.viewDistance());
        this.isDirty = true;
    }

    private void removeHologramFromConfig(UUID uuid) {
        String path = "holograms." + uuid.toString();
        this.config.set(path, (Object)null);
        this.isDirty = true;
    }

    private void saveConfigToFile() {
        if (this.isDirty) {
            try {
                this.config.save(this.file);
                this.isDirty = false;
                this.plugin.getLogger().fine("Config saved to file");
            } catch (IOException var2) {
                this.plugin.getLogger().severe("Failed to save holograms: " + var2.getMessage());
            }

        }
    }

    public void forceSave() {
        this.config.set("holograms", (Object)null);
        Iterator var2 = this.savedHolograms.values().iterator();

        while(var2.hasNext()) {
            HologramData holo = (HologramData)var2.next();
            this.saveHologramToConfig(holo);
        }

        this.saveConfigToFile();
        this.plugin.getLogger().info("Force saved " + this.savedHolograms.size() + " holograms to file");
    }

    private void loadFromFile() {
        this.savedHolograms.clear();
        if (!this.config.isConfigurationSection("holograms")) {
            this.plugin.getLogger().info("No holograms section found in config");
        } else {
            int loaded = 0;
            Iterator var3 = this.config.getConfigurationSection("holograms").getKeys(false).iterator();

            while(var3.hasNext()) {
                String uuidStr = (String)var3.next();
                String base = "holograms." + uuidStr;

                UUID uuid;
                try {
                    uuid = UUID.fromString(uuidStr);
                } catch (IllegalArgumentException var32) {
                    this.plugin.getLogger().warning("Invalid UUID in config: " + uuidStr);
                    continue;
                }

                String text = this.config.getString(base + ".text");
                String name = this.config.getString(base + ".name", "");
                boolean persistent = this.config.getBoolean(base + ".persistent");
                boolean shadowed = this.config.getBoolean(base + ".shadowed");
                boolean seeThrough = this.config.getBoolean(base + ".seeThrough");
                String billboardName = this.config.getString(base + ".billboard");
                if (text != null && billboardName != null) {
                    String worldName = this.config.getString(base + ".world");
                    double x = this.config.getDouble(base + ".x");
                    double y = this.config.getDouble(base + ".y");
                    double z = this.config.getDouble(base + ".z");
                    float yaw = (float)this.config.getDouble(base + ".yaw");
                    float pitch = (float)this.config.getDouble(base + ".pitch");
                    float scale = (float)this.config.getDouble(base + ".scale", 1.0D);
                    String textAlignmentName = this.config.getString(base + ".textAlignment", "CENTER");
                    int textOpacity = this.config.getInt(base + ".textOpacity", 255);
                    int backgroundColor = this.config.getInt(base + ".backgroundColor", 1073741824);
                    int viewDistance = this.config.getInt(base + ".viewDistance", 64);
                    if (worldName != null && Bukkit.getWorld(worldName) != null) {
                        TextAlignment textAlignment;
                        try {
                            textAlignment = TextAlignment.valueOf(textAlignmentName);
                        } catch (IllegalArgumentException var31) {
                            textAlignment = TextAlignment.CENTER;
                            this.plugin.getLogger().warning("Invalid text alignment for hologram " + uuidStr + ": " + textAlignmentName);
                        }

                        Billboard billboard;
                        try {
                            billboard = Billboard.valueOf(billboardName);
                        } catch (IllegalArgumentException var30) {
                            billboard = Billboard.CENTER;
                            this.plugin.getLogger().warning("Invalid billboard for hologram " + uuidStr + ": " + billboardName);
                        }

                        new Location(Bukkit.getWorld(worldName), x, y, z);
                        HologramData holo = new HologramData(new Location(Bukkit.getWorld(worldName), x, y, z), text, name, persistent, shadowed, seeThrough, Billboard.valueOf(billboardName), yaw, pitch, scale, TextAlignment.valueOf(textAlignmentName), textOpacity, backgroundColor, viewDistance, uuid);
                        this.savedHolograms.put(uuid, holo);
                        ++loaded;
                    } else {
                        this.plugin.getLogger().warning("World not found for hologram " + uuidStr + ": " + worldName);
                    }
                } else {
                    this.plugin.getLogger().warning("Skipping invalid hologram entry: " + uuidStr);
                }
            }

            this.plugin.getLogger().info("Loaded " + loaded + " holograms from file");
            this.isDirty = false;
        }
    }
}
