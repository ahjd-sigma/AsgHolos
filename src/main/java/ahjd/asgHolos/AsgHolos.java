package ahjd.asgHolos;

import ahjd.asgHolos.Listeners.CreationGUIListener;
import ahjd.asgHolos.Listeners.ListGUIListener;
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
    public Config config;

    public void onEnable() {
        instance = this;
        this.config = new Config(this);
        this.saveFile = new SaveFile(this);
        this.hologramManager = new HologramManager(this.saveFile, this);
        this.maintenanceTask = new HologramMaintenanceTask(this, this.hologramManager, this.config.getMaintenanceInterval());
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

    public void onDisable() {
        if (this.maintenanceTask != null) {
            this.maintenanceTask.stop();
        }

        if (this.config != null) {
            this.config.reloadConfig();
        }

        if (this.hologramManager != null) {
            this.hologramManager.getEntityCache().clear();
        }

        ChatInput.cleanup();
        this.getLogger().info("AsgHolos disabled successfully!");
    }
}
