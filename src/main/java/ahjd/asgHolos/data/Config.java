package ahjd.asgHolos.data;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

public class Config {
    private final JavaPlugin plugin;
    private FileConfiguration config;
    private Integer maxTempHolograms;
    private Integer maxPersistentHolograms;
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
        Object rawMaxTempHolograms = this.config.get("max_temp_holograms");
        this.maxTempHolograms = rawMaxTempHolograms instanceof Integer ? (Integer)rawMaxTempHolograms : null;
        
        Object rawMaxPersistentHolograms = this.config.get("max_persistent_holograms");
        this.maxPersistentHolograms = rawMaxPersistentHolograms instanceof Integer ? (Integer)rawMaxPersistentHolograms : null;
        
        this.maintenanceInterval = this.config.getInt("maintenance_interval", 60);
    }

    public Integer getMaxTempHolograms() {
        return this.maxTempHolograms;
    }
    
    public Integer getMaxPersistentHolograms() {
        return this.maxPersistentHolograms;
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
