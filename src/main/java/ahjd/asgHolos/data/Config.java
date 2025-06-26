package ahjd.asgHolos.data;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

public class Config {
    private final JavaPlugin plugin;
    private FileConfiguration config;
    private Integer maxHolograms;
    private int maintenanceInterval;

    public Config(JavaPlugin plugin) {
        this.plugin = plugin;
        if (!plugin.getDataFolder().exists()) {
            plugin.getDataFolder().mkdirs();
        }

        plugin.saveDefaultConfig();
        this.config = plugin.getConfig();
        this.loadConfig();
    }

    private void loadConfig() {
        Object rawMaxHolograms = this.config.get("max_holograms");
        this.maxHolograms = rawMaxHolograms instanceof Integer ? (Integer)rawMaxHolograms : null;
        this.maintenanceInterval = this.config.getInt("maintenance_interval", 60);
    }

    public int getMaxHolograms() {
        return this.maxHolograms;
    }

    public int getMaintenanceInterval() {
        return this.maintenanceInterval;
    }

    public void reloadConfig() {
        this.plugin.reloadConfig();
        this.config = this.plugin.getConfig();
        this.loadConfig();
    }
}
