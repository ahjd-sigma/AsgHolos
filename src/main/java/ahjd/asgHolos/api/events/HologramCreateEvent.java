package ahjd.asgHolos.api.events;

import ahjd.asgHolos.data.HologramData;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * Event fired when a hologram is about to be created
 * This event can be cancelled to prevent hologram creation
 */
public class HologramCreateEvent extends Event implements Cancellable {
    
    private static final HandlerList HANDLERS = new HandlerList();
    private boolean cancelled = false;
    
    private final HologramData hologramData;
    private final Player creator;
    private final CreationSource source;
    
    public enum CreationSource {
        PLAYER_COMMAND,
        PLAYER_GUI,
        API_CALL,
        MAINTENANCE_TASK,
        PLUGIN_RELOAD
    }
    
    /**
     * Constructor for HologramCreateEvent
     * @param hologramData The hologram data that will be created
     * @param creator The player who created the hologram (can be null for API calls)
     * @param source The source of the creation
     */
    public HologramCreateEvent(HologramData hologramData, Player creator, CreationSource source) {
        this.hologramData = hologramData;
        this.creator = creator;
        this.source = source;
    }
    
    /**
     * Gets the hologram data that will be created
     * @return The hologram data
     */
    public HologramData getHologramData() {
        return hologramData;
    }
    
    /**
     * Gets the player who created the hologram
     * @return The creator player, or null if created via API
     */
    public Player getCreator() {
        return creator;
    }
    
    /**
     * Gets the source of the hologram creation
     * @return The creation source
     */
    public CreationSource getSource() {
        return source;
    }
    
    /**
     * Checks if the hologram is persistent
     * @return true if the hologram will be saved to file
     */
    public boolean isPersistent() {
        return hologramData.persistent();
    }
    
    /**
     * Checks if the hologram is temporary
     * @return true if the hologram will not be saved to file
     */
    public boolean isTemporary() {
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