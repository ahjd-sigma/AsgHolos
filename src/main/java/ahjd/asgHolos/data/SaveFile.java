package ahjd.asgHolos.data;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class SaveFile {
    private final Plugin plugin;
    private final File file;
    private final FileConfiguration config;
    private final Map<UUID, HologramData> savedHolograms = new ConcurrentHashMap<>(); // Use Map instead of List

    public SaveFile(JavaPlugin plugin) {
        this.file = new File(plugin.getDataFolder(), "holograms.yml");
        this.config = YamlConfiguration.loadConfiguration(file);
        this.plugin = plugin;
        loadFromFile();
    }

    public void saveHologram(HologramData data) {
        if (data == null || data.entityUUID() == null) return;

        savedHolograms.put(data.entityUUID(), data);
        saveToFile();
        plugin.getLogger().info("Saved hologram with UUID: " + data.entityUUID());
    }

    public boolean removeHologram(HologramData data) {
        if (data == null || data.entityUUID() == null) return false;

        HologramData removed = savedHolograms.remove(data.entityUUID());
        if (removed != null) {
            saveToFile();
            plugin.getLogger().info("Removed hologram with UUID: " + data.entityUUID());
            return true;
        }
        return false;
    }

    // Remove by UUID - more reliable
    public boolean removeHologramByUUID(UUID uuid) {
        if (uuid == null) return false;

        HologramData removed = savedHolograms.remove(uuid);
        if (removed != null) {
            saveToFile();
            plugin.getLogger().info("Removed hologram with UUID: " + uuid);
            return true;
        }
        return false;
    }

    public List<HologramData> getSavedHolograms() {
        return new ArrayList<>(savedHolograms.values());
    }

    public HologramData getHologramByUUID(UUID uuid) {
        return savedHolograms.get(uuid);
    }

    public boolean hasHologram(UUID uuid) {
        return savedHolograms.containsKey(uuid);
    }

    public int getHologramCount() {
        return savedHolograms.size();
    }

    private void saveToFile() {
        config.set("holograms", null); // Clear old entries

        int index = 0;
        for (HologramData holo : savedHolograms.values()) {
            String base = "holograms." + index;
            config.set(base + ".text", holo.text());
            config.set(base + ".persistent", holo.persistent());
            config.set(base + ".shadowed", holo.shadowed());
            config.set(base + ".seeThrough", holo.seeThrough());
            config.set(base + ".billboard", holo.billboard().name());
            config.set(base + ".uuid", holo.entityUUID().toString());

            Location loc = holo.location();
            config.set(base + ".world", loc.getWorld().getName());
            config.set(base + ".x", loc.getX());
            config.set(base + ".y", loc.getY());
            config.set(base + ".z", loc.getZ());

            index++;
        }

        try {
            config.save(file);
            plugin.getLogger().info("Saved " + savedHolograms.size() + " holograms to file");
        } catch (IOException e) {
            plugin.getLogger().severe("Failed to save holograms: " + e.getMessage());
        }
    }

    private void loadFromFile() {
        savedHolograms.clear();
        if (!config.isConfigurationSection("holograms")) {
            plugin.getLogger().info("No holograms section found in config");
            return;
        }

        int loaded = 0;
        for (String key : config.getConfigurationSection("holograms").getKeys(false)) {
            String base = "holograms." + key;

            String text = config.getString(base + ".text");
            boolean persistent = config.getBoolean(base + ".persistent");
            boolean shadowed = config.getBoolean(base + ".shadowed");
            boolean seeThrough = config.getBoolean(base + ".seeThrough");
            String billboardName = config.getString(base + ".billboard");
            String uuidStr = config.getString(base + ".uuid");

            // Skip if essential data is missing
            if (text == null || billboardName == null || uuidStr == null || uuidStr.isEmpty()) {
                plugin.getLogger().warning("Skipping invalid hologram entry: " + key);
                continue;
            }

            UUID uuid = null;
            try {
                uuid = UUID.fromString(uuidStr);
            } catch (IllegalArgumentException e) {
                plugin.getLogger().warning("Invalid UUID for hologram " + key + ": " + uuidStr);
                continue;
            }

            String worldName = config.getString(base + ".world");
            double x = config.getDouble(base + ".x");
            double y = config.getDouble(base + ".y");
            double z = config.getDouble(base + ".z");

            if (worldName == null || Bukkit.getWorld(worldName) == null) {
                plugin.getLogger().warning("World not found for hologram " + key + ": " + worldName);
                continue;
            }

            Location location = new Location(Bukkit.getWorld(worldName), x, y, z);
            HologramData holo = new HologramData(location, text, persistent, shadowed, seeThrough,
                    org.bukkit.entity.Display.Billboard.valueOf(billboardName), uuid);

            savedHolograms.put(uuid, holo);
            loaded++;
        }

        plugin.getLogger().info("Loaded " + loaded + " holograms from file");
    }
}
