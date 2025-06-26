package ahjd.asgHolos;

import ahjd.asgHolos.Listeners.CreationGUIListener;
import ahjd.asgHolos.Listeners.ListGUIListener;
import ahjd.asgHolos.api.AsgHolosAPI;
import ahjd.asgHolos.api.AsgHolosAPIImpl;
import ahjd.asgHolos.cmds.HoloCMD;
import ahjd.asgHolos.cmds.TabCompletion;
import ahjd.asgHolos.data.Config;
import ahjd.asgHolos.data.HologramMaintenanceTask;
import ahjd.asgHolos.data.HologramManager;
import ahjd.asgHolos.data.SaveFile;
import ahjd.asgHolos.utils.ChatInput;
import org.bukkit.plugin.java.JavaPlugin;

public final class AsgHolos extends JavaPlugin {
    private static AsgHolos instance;
    private SaveFile saveFile;
    private HologramManager hologramManager;
    private HologramMaintenanceTask maintenanceTask;
    private AsgHolosAPI api;
    public Config config;

    public void onEnable() {
        instance = this;
        this.config = new Config(this);
        this.saveFile = new SaveFile(this);
        this.hologramManager = new HologramManager(this.saveFile, this);
        this.maintenanceTask = new HologramMaintenanceTask(this, this.hologramManager, this.config.getMaintenanceInterval());
        
        // Initialize API
        this.api = new AsgHolosAPIImpl(this.hologramManager, this.config, this.saveFile, this);
        
        ChatInput.initialize(this);
        this.hologramManager.setMaintenanceTask(this.maintenanceTask);
        this.hologramManager.populateCache();
        this.hologramManager.cleanCache();
        this.getServer().getPluginManager().registerEvents(new CreationGUIListener(this.hologramManager, this), this);
        this.getServer().getPluginManager().registerEvents(new ListGUIListener(this.hologramManager), this);
        this.getServer().getPluginManager().registerEvents(new ChatInput(), this);
        this.getCommand("hologram").setExecutor(new HoloCMD(this.hologramManager, this.saveFile, this.config));
        this.getCommand("hologram").setTabCompleter(new TabCompletion());
        this.maintenanceTask.start();
        this.getLogger().info("AsgHolos enabled successfully!");
        this.getLogger().info("AsgHolos API is now available for external plugins!");
    }

    public static AsgHolos getInstance() {
        return instance;
    }

    public HologramManager getHologramManager() {
        return this.hologramManager;
    }

    public Config getPluginConfig() {
        return this.config;
    }
    
    /**
     * Gets the AsgHolos API for external plugin integration
     * @return The AsgHolos API instance
     */
    public AsgHolosAPI getAPI() {
        return this.api;
    }

    public void onDisable() {
        if (this.maintenanceTask != null) {
            this.maintenanceTask.stop();
        }

        if (this.config != null) {
            this.config.reloadConfig();
        }

        if (this.hologramManager != null) {
            // Log current hologram counts before shutdown
            this.getLogger().info("Current hologram counts before shutdown - Temporary: " + 
                               this.hologramManager.getActiveTempHolograms() + 
                               ", Persistent: " + this.hologramManager.getActivePersistentHolograms());
            
            // Clean up temporary holograms to prevent ghost holograms on restart
            this.getLogger().info("Cleaning up temporary holograms before shutdown...");
            this.hologramManager.cleanupTemporaryHolograms();
            
            // Clear entity cache
            this.hologramManager.getEntityCache().clear();
        }

        ChatInput.cleanup();
        this.getLogger().info("AsgHolos disabled successfully!");
    }
}
