package ahjd.asgHolos.Listeners;

import ahjd.asgHolos.data.HologramData;
import ahjd.asgHolos.data.HologramManager;
import ahjd.asgHolos.guis.CreationGUI;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Display;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

public class CreationGUIListener implements Listener {

    private final HologramManager hologramManager;

    public CreationGUIListener(HologramManager hologramManager) {
        this.hologramManager = hologramManager;
    }


    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) return;
        String slabel = ChatColor.DARK_GRAY + "[" + ChatColor.YELLOW + "🔥 " + ChatColor.GOLD + "Sigma" + ChatColor.DARK_GRAY + "] ";

        if (!(event.getInventory().getHolder() instanceof CreationGUI)) return;

        event.setCancelled(true); // Prevent item movement

        int slot = event.getRawSlot();

        // Slot 22: "Create Hologram" button
        if (slot == 22) {
            player.closeInventory();

            Location loc = player.getLocation(); // or any desired location
            HologramData holo = new HologramData(
                    loc,
                    "Sample Text",
                    true,  // persistent
                    true,  // shadowed
                    true,  // seeThrough
                    Display.Billboard.CENTER,
                    null    // no UUID before spawn
            );
            HologramData savedHolo = hologramManager.spawnHologram(holo);

            if (savedHolo != null) {
                player.sendMessage(slabel+ ChatColor.GREEN +  "§aHologram created at your location.");
            } else {
                player.sendMessage(slabel+ ChatColor.RED +"§cFailed to create hologram.");
            }
        }

        // Slot 49: "Cancel"
        if (slot == 49) {
            player.closeInventory();
            player.sendMessage(slabel+ ChatColor.RED +"§cCreation cancelled.");
        }
    }
}