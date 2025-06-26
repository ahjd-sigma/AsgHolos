package ahjd.asgHolos.api;

import ahjd.asgHolos.api.events.HologramCreateEvent;
import ahjd.asgHolos.api.events.HologramDeleteEvent;
import ahjd.asgHolos.data.Config;
import ahjd.asgHolos.data.HologramData;
import ahjd.asgHolos.data.HologramManager;
import ahjd.asgHolos.data.SaveFile;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Display.Billboard;
import org.bukkit.entity.Player;
import org.bukkit.entity.TextDisplay;
import org.bukkit.entity.TextDisplay.TextAlignment;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Implementation of the AsgHolos API
 */
public class AsgHolosAPIImpl implements AsgHolosAPI {
    
    private final HologramManager hologramManager;
    private final Config config;
    private final SaveFile saveFile;
    private final Plugin plugin;
    
    public AsgHolosAPIImpl(HologramManager hologramManager, Config config, SaveFile saveFile, Plugin plugin) {
        this.hologramManager = hologramManager;
        this.config = config;
        this.saveFile = saveFile;
        this.plugin = plugin;
    }
    
    @Override
    public HologramData createHologram(Location location, String text, String name, boolean persistent) {
        // Use default values for advanced settings
        return createHologram(location, text, name, persistent, true, false, Billboard.CENTER,
                0.0f, 0.0f, 1.0f, TextAlignment.CENTER, 255, 0x00000000, 64);
    }
    
    @Override
    public HologramData createHologram(Location location, String text, String name, boolean persistent,
                                     boolean shadowed, boolean seeThrough, Billboard billboard,
                                     float yaw, float pitch, float scale, TextAlignment textAlignment,
                                     int textOpacity, int backgroundColor, int viewDistance) {
        
        if (location == null || text == null) {
            return null;
        }
        
        // Check limits before creating
        if (persistent && wouldExceedPersistentLimit(1)) {
            return null;
        }
        
        if (!persistent && wouldExceedTempLimit(1)) {
            return null;
        }
        
        // Create hologram data
        HologramData hologramData = new HologramData(
            location.clone(),
            text,
            name,
            persistent,
            shadowed,
            seeThrough,
            billboard,
            yaw,
            pitch,
            scale,
            textAlignment,
            textOpacity,
            backgroundColor,
            viewDistance,
            null
        );
        
        // Spawn the hologram
         return hologramManager.spawnHologram(hologramData, null, HologramCreateEvent.CreationSource.API_CALL);
     }
     
     @Override
     public HologramData createHologram(HologramData hologramData) {
         return createHologram(hologramData, null);
     }
     
     @Override
     public HologramData createHologram(HologramData hologramData, Player creator) {
         if (hologramData == null) {
             return null;
         }
         
         // Check limits before creating
         if (hologramData.persistent() && wouldExceedPersistentLimit(1)) {
             return null;
         }
         
         if (!hologramData.persistent() && wouldExceedTempLimit(1)) {
             return null;
         }
         
         // Spawn the hologram
         return hologramManager.spawnHologram(hologramData, creator, HologramCreateEvent.CreationSource.API_CALL);
     }
    
    @Override
    public boolean deleteHologram(UUID hologramUUID) {
        if (hologramUUID == null) {
            return false;
        }
        
        // Try to find the hologram in saved holograms first
        HologramData savedHologram = saveFile.getHologramByUUID(hologramUUID);
        if (savedHologram != null) {
            return hologramManager.deleteHologram(savedHologram, null, HologramDeleteEvent.DeletionSource.API_CALL);
        }
        
        // If not found in saved, search in all worlds for temporary holograms
        for (World world : Bukkit.getWorlds()) {
            for (TextDisplay entity : world.getEntitiesByClass(TextDisplay.class)) {
                if (entity.getUniqueId().equals(hologramUUID) && 
                    hologramManager.isOwnedHologram(entity)) {
                    
                    // Create temporary hologram data for deletion
                    HologramData tempData = new HologramData(
                        entity.getLocation(),
                        entity.getText(),
                        "Temporary",
                        false,
                        entity.isShadowed(),
                        entity.isSeeThrough(),
                        entity.getBillboard(),
                        entity.getLocation().getYaw(),
                        entity.getLocation().getPitch(),
                        entity.getTransformation().getScale().x,
                        entity.getAlignment(),
                        entity.getTextOpacity(),
                        entity.getBackgroundColor().asARGB(),
                        (int) entity.getViewRange(),
                        entity.getUniqueId()
                    );
                    
                    return hologramManager.deleteHologram(tempData, null, HologramDeleteEvent.DeletionSource.API_CALL);
                }
            }
        }
        
        return false;
    }
    
    @Override
    public boolean deleteHologram(HologramData hologramData) {
        if (hologramData == null) {
            return false;
        }
        
        return hologramManager.deleteHologram(hologramData, null, HologramDeleteEvent.DeletionSource.API_CALL);
    }
    
    @Override
    public boolean deleteHologram(HologramData hologramData, Player deleter) {
        if (hologramData == null) {
            return false;
        }
        
        return hologramManager.deleteHologram(hologramData, deleter, HologramDeleteEvent.DeletionSource.API_CALL);
    }
    
    @Override
    public HologramData getHologram(UUID hologramUUID) {
        if (hologramUUID == null) {
            return null;
        }
        
        // Check saved holograms first
        HologramData savedHologram = saveFile.getHologramByUUID(hologramUUID);
        if (savedHologram != null) {
            return savedHologram;
        }
        
        // Search in all worlds for temporary holograms
        for (World world : Bukkit.getWorlds()) {
            for (TextDisplay entity : world.getEntitiesByClass(TextDisplay.class)) {
                if (entity.getUniqueId().equals(hologramUUID) && 
                    hologramManager.isOwnedHologram(entity)) {
                    
                    return new HologramData(
                        entity.getLocation(),
                        entity.getText(),
                        "Temporary",
                        false,
                        entity.isShadowed(),
                        entity.isSeeThrough(),
                        entity.getBillboard(),
                        entity.getLocation().getYaw(),
                        entity.getLocation().getPitch(),
                        entity.getTransformation().getScale().x,
                        entity.getAlignment(),
                        entity.getTextOpacity(),
                        entity.getBackgroundColor().asARGB(),
                        (int) entity.getViewRange(),
                        entity.getUniqueId()
                    );
                }
            }
        }
        
        return null;
    }
    
    @Override
    public List<HologramData> getAllPersistentHolograms() {
        return new ArrayList<>(saveFile.getSavedHolograms());
    }
    
    @Override
    public List<HologramData> getAllTemporaryHolograms() {
        List<HologramData> tempHolograms = new ArrayList<>();
        
        for (World world : Bukkit.getWorlds()) {
            for (TextDisplay entity : world.getEntitiesByClass(TextDisplay.class)) {
                if (hologramManager.isOwnedHologram(entity) && 
                    !saveFile.hasHologram(entity.getUniqueId())) {
                    
                    HologramData tempData = new HologramData(
                        entity.getLocation(),
                        entity.getText(),
                        "Temporary",
                        false,
                        entity.isShadowed(),
                        entity.isSeeThrough(),
                        entity.getBillboard(),
                        entity.getLocation().getYaw(),
                        entity.getLocation().getPitch(),
                        entity.getTransformation().getScale().x,
                        entity.getAlignment(),
                        entity.getTextOpacity(),
                        entity.getBackgroundColor().asARGB(),
                        (int) entity.getViewRange(),
                        entity.getUniqueId()
                    );
                    
                    tempHolograms.add(tempData);
                }
            }
        }
        
        return tempHolograms;
    }

    @Override
    public List<HologramData> getAllHolograms() {
        List<HologramData> allHolograms = new ArrayList<>();
        allHolograms.addAll(getAllPersistentHolograms());
        allHolograms.addAll(getAllTemporaryHolograms());
        return allHolograms;
    }
    
    @Override
    public int getActiveTempHologramCount() {
        return hologramManager.getActiveTempHolograms();
    }
    
    @Override
    public int getActivePersistentHologramCount() {
        return hologramManager.getActivePersistentHolograms();
    }
    
    @Override
    public int getTotalActiveHologramCount() {
        return hologramManager.getActiveHolograms();
    }
    
    @Override
    public int getMaxTempHolograms() {
        return config.getMaxTempHolograms();
    }
    
    @Override
    public int getMaxPersistentHolograms() {
        return config.getMaxPersistentHolograms();
    }
    
    @Override
    public boolean wouldExceedTempLimit(int count) {
        return (hologramManager.getActiveTempHolograms() + count) > config.getMaxTempHolograms();
    }
    
    @Override
    public boolean wouldExceedPersistentLimit(int count) {
        return (hologramManager.getActivePersistentHolograms() + count) > config.getMaxPersistentHolograms();
    }
    
    @Override
    public void reloadConfig() {
        // Reload config
        config.reloadConfig();
        
        // Reset and repopulate hologram cache to ensure accurate counting
        hologramManager.populateCache();
        
        plugin.getLogger().info("AsgHolos API: Config reloaded and hologram cache refreshed");
        plugin.getLogger().info("Current hologram counts - Temporary: " + hologramManager.getActiveTempHolograms() + 
                              ", Persistent: " + hologramManager.getActivePersistentHolograms());
    }
    
    @Override
    public String getPluginVersion() {
        return plugin.getDescription().getVersion();
    }
}