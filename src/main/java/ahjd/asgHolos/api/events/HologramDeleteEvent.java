package ahjd.asgHolos.api.events;

import ahjd.asgHolos.data.HologramData;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * Event fired when a hologram is about to be deleted
 * This event can be cancelled to prevent hologram deletion
 */
public class HologramDeleteEvent extends Event implements Cancellable {
    
    private static final HandlerList HANDLERS = new HandlerList();
    private boolean cancelled = false;
    
    private final HologramData hologramData;
    private final Player deleter;
    private final DeletionSource source;
    
    public enum DeletionSource {
        PLAYER_COMMAND,
        PLAYER_GUI,
        API_CALL,
        MAINTENANCE_TASK,
        PLUGIN_DISABLE,
        WORLD_UNLOAD
    }
    
    /**
     * Constructor for HologramDeleteEvent
     * @param hologramData The hologram data that will be deleted
     * @param deleter The player who deleted the hologram (can be null for API calls)
     * @param source The source of the deletion
     */
    public HologramDeleteEvent(HologramData hologramData, Player deleter, DeletionSource source) {
        this.hologramData = hologramData;
        this.deleter = deleter;
        this.source = source;
    }
    
    /**
     * Gets the hologram data that will be deleted
     * @return The hologram data
     */
    public HologramData getHologramData() {
        return hologramData;
    }
    
    /**
     * Gets the player who deleted the hologram
     * @return The deleter player, or null if deleted via API
     */
    public Player getDeleter() {
        return deleter;
    }
    
    /**
     * Gets the source of the hologram deletion
     * @return The deletion source
     */
    public DeletionSource getSource() {
        return source;
    }
    
    /**
     * Checks if the hologram was persistent
     * @return true if the hologram was saved to file
     */
    public boolean wasPersistent() {
        return hologramData.persistent();
    }
    
    /**
     * Checks if the hologram was temporary
     * @return true if the hologram was not saved to file
     */
    public boolean wasTemporary() {
        return !hologramData.persistent();
    }
    
    @Override
    public boolean isCancelled() {
        return cancelled;
    }
    
    @Override
    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }
    
    @Override
    public HandlerList getHandlers() {
        return HANDLERS;
    }
    
    public static HandlerList getHandlerList() {
        return HANDLERS;
    }
}