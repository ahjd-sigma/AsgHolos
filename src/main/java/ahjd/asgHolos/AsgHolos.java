package ahjd.asgHolos;

import ahjd.asgHolos.Listeners.CreationGUIListener;
import ahjd.asgHolos.Listeners.ListGUIListener;
import ahjd.asgHolos.cmds.HoloCMD;
import ahjd.asgHolos.cmds.TabCompletion;
import ahjd.asgHolos.data.HologramManager;
import ahjd.asgHolos.data.SaveFile;
import ahjd.asgHolos.data.HologramMaintenanceTask;
import org.bukkit.plugin.java.JavaPlugin;

public final class AsgHolos extends JavaPlugin {

    private SaveFile saveFile;
    private HologramManager hologramManager;
    private HologramMaintenanceTask maintenanceTask;

    @Override
    public void onEnable() {
        // Initialize components
        this.saveFile = new SaveFile(this);
        this.hologramManager = new HologramManager(saveFile);
        this.maintenanceTask = new HologramMaintenanceTask(this, hologramManager);

        // Set the maintenance task reference in hologram manager
        this.hologramManager.setMaintenanceTask(maintenanceTask);

        // Populate cache with existing entities
        this.hologramManager.populateCache();

        hologramManager.cleanCache();

        // Register events
        getServer().getPluginManager().registerEvents(new CreationGUIListener(hologramManager), this);
        getServer().getPluginManager().registerEvents(new ListGUIListener(hologramManager), this);

        // Register commands
        getCommand("hologram").setExecutor(new HoloCMD(hologramManager, saveFile));
        getCommand("hologram").setTabCompleter(new TabCompletion());

        // Start maintenance task
        maintenanceTask.start();

        getLogger().info("AsgHolos enabled successfully!");
    }

    @Override
    public void onDisable() {
        if (maintenanceTask != null) {
            maintenanceTask.stop();
        }

        // Clean up cache
        if (hologramManager != null) {
            hologramManager.getEntityCache().clear();
        }
        getLogger().info("AsgHolos disabled successfully!");
    }
}