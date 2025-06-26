package ahjd.asgHolos.api;

import ahjd.asgHolos.data.HologramData;
import org.bukkit.Location;
import org.bukkit.entity.Display.Billboard;
import org.bukkit.entity.Player;
import org.bukkit.entity.TextDisplay.TextAlignment;

import java.util.List;
import java.util.UUID;

/**
 * Public API for AsgHolos plugin
 * Allows external plugins to interact with the hologram system
 */
public interface AsgHolosAPI {
    
    /**
     * Creates a new hologram at the specified location
     * @param location The location where the hologram should be spawned
     * @param text The text content of the hologram
     * @param name The name of the hologram (can be null)
     * @param persistent Whether the hologram should be saved to file
     * @return The created HologramData or null if creation failed
     */
    HologramData createHologram(Location location, String text, String name, boolean persistent);
    
    /**
     * Creates a new hologram with advanced settings
     * @param location The location where the hologram should be spawned
     * @param text The text content of the hologram
     * @param name The name of the hologram (can be null)
     * @param persistent Whether the hologram should be saved to file
     * @param shadowed Whether the hologram has shadow
     * @param seeThrough Whether the hologram is see-through
     * @param billboard The billboard mode
     * @param yaw The yaw rotation
     * @param pitch The pitch rotation
     * @param scale The scale of the hologram
     * @param textAlignment The text alignment
     * @param textOpacity The text opacity (0-255)
     * @param backgroundColor The background color (ARGB format)
     * @param viewDistance The view distance in blocks
     * @return The created HologramData or null if creation failed
     */
    HologramData createHologram(Location location, String text, String name, boolean persistent,
                               boolean shadowed, boolean seeThrough, Billboard billboard,
                               float yaw, float pitch, float scale, TextAlignment textAlignment,
                               int textOpacity, int backgroundColor, int viewDistance);
    
    /**
     * Creates a hologram from HologramData
     * @param hologramData The hologram data to create
     * @return The created HologramData or null if creation failed
     */
    HologramData createHologram(HologramData hologramData);
    
    /**
     * Creates a hologram from HologramData with a specific creator (for event tracking)
     * @param hologramData The hologram data to create
     * @param creator The player who is creating the hologram (can be null)
     * @return The created HologramData or null if creation failed
     */
    HologramData createHologram(HologramData hologramData, Player creator);
    
    /**
     * Deletes a hologram by its UUID
     * @param hologramUUID The UUID of the hologram to delete
     * @return true if the hologram was successfully deleted, false otherwise
     */
    boolean deleteHologram(UUID hologramUUID);
    
    /**
     * Deletes a hologram by its data
     * @param hologramData The hologram data to delete
     * @return true if the hologram was successfully deleted, false otherwise
     */
    boolean deleteHologram(HologramData hologramData);
    
    /**
     * Deletes a hologram by its data with a specific deleter (for event tracking)
     * @param hologramData The hologram data to delete
     * @param deleter The player who is deleting the hologram (can be null)
     * @return true if the hologram was successfully deleted, false otherwise
     */
    boolean deleteHologram(HologramData hologramData, Player deleter);
    
    /**
     * Gets a hologram by its UUID
     * @param hologramUUID The UUID of the hologram
     * @return The HologramData or null if not found
     */
    HologramData getHologram(UUID hologramUUID);
    
    /**
     * Gets a hologram by its UUID (alias for getHologram)
     * @param uuid The UUID of the hologram
     * @return The HologramData or null if not found
     */
    default HologramData getHologramByUUID(UUID uuid) {
        return getHologram(uuid);
    }
    
    /**
     * Gets all persistent holograms
     * @return List of all persistent holograms
     */
    List<HologramData> getAllPersistentHolograms();
    
    /**
     * Gets all persistent holograms (alias for getAllPersistentHolograms)
     * @return List of all persistent holograms
     */
    default List<HologramData> getPersistentHolograms() {
        return getAllPersistentHolograms();
    }
    
    /**
     * Gets all temporary holograms
     * @return List of all temporary holograms
     */
    List<HologramData> getAllTemporaryHolograms();
    
    /**
     * Gets all temporary holograms (alias for getAllTemporaryHolograms)
     * @return List of all temporary holograms
     */
    default List<HologramData> getTempHolograms() {
        return getAllTemporaryHolograms();
    }
    
    /**
     * Gets all holograms (both persistent and temporary)
     * @return List of all holograms
     */
    List<HologramData> getAllHolograms();
    
    /**
     * Gets the current count of active temporary holograms
     * @return The number of active temporary holograms
     */
    int getActiveTempHologramCount();
    
    /**
     * Gets the current count of active temporary holograms (alias for getActiveTempHologramCount)
     * @return The number of active temporary holograms
     */
    default int getTempHologramCount() {
        return getActiveTempHologramCount();
    }
    
    /**
     * Gets the current count of active persistent holograms
     * @return The number of active persistent holograms
     */
    int getActivePersistentHologramCount();
    
    /**
     * Gets the current count of active persistent holograms (alias for getActivePersistentHologramCount)
     * @return The number of active persistent holograms
     */
    default int getPersistentHologramCount() {
        return getActivePersistentHologramCount();
    }
    
    /**
     * Gets the total count of active holograms
     * @return The total number of active holograms
     */
    int getTotalActiveHologramCount();
    
    /**
     * Gets the total count of active holograms (alias for getTotalActiveHologramCount)
     * @return The total number of active holograms
     */
    default int getTotalHologramCount() {
        return getTotalActiveHologramCount();
    }
    
    /**
     * Gets the maximum allowed temporary holograms
     * @return The maximum temporary hologram limit
     */
    int getMaxTempHolograms();
    
    /**
     * Gets the maximum allowed persistent holograms
     * @return The maximum persistent hologram limit
     */
    int getMaxPersistentHolograms();
    
    /**
     * Checks if the temporary hologram limit would be exceeded
     * @param count The number of holograms to add
     * @return true if the limit would be exceeded
     */
    boolean wouldExceedTempLimit(int count);
    
    /**
     * Checks if a temporary hologram can be created
     * @return true if a temporary hologram can be created
     */
    default boolean canCreateTempHologram() {
        return !wouldExceedTempLimit(1);
    }
    
    /**
     * Checks if the persistent hologram limit would be exceeded
     * @param count The number of holograms to add
     * @return true if the limit would be exceeded
     */
    boolean wouldExceedPersistentLimit(int count);
    
    /**
     * Checks if a persistent hologram can be created
     * @return true if a persistent hologram can be created
     */
    default boolean canCreatePersistentHologram() {
        return !wouldExceedPersistentLimit(1);
    }
    
    /**
     * Reloads the plugin configuration
     */
    void reloadConfig();
    
    /**
     * Gets the plugin version
     * @return The plugin version string
     */
    String getPluginVersion();
    
    /**
     * Gets the plugin version (alias for getPluginVersion)
     * @return The plugin version string
     */
    default String getVersion() {
        return getPluginVersion();
    }
}