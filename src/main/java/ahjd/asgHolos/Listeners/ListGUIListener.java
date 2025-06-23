package ahjd.asgHolos.Listeners;

import ahjd.asgHolos.data.HologramData;
import ahjd.asgHolos.data.HologramManager;
import ahjd.asgHolos.data.SaveFile;
import ahjd.asgHolos.guis.ListGUI;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class ListGUIListener implements Listener {
    private final HologramManager hologramManager;

    public ListGUIListener(HologramManager hologramManager) {
        this.hologramManager = hologramManager;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) return;
        if (!(event.getInventory().getHolder() instanceof ListGUI gui)) return;
        String slabel = ChatColor.DARK_GRAY + "[" + ChatColor.YELLOW + "🔥 " + ChatColor.GOLD + "Sigma" + ChatColor.DARK_GRAY + "] ";

        event.setCancelled(true);
        int slot = event.getRawSlot();
        ItemStack clicked = event.getCurrentItem();
        if (clicked == null || !clicked.hasItemMeta()) return;

        SaveFile saveFile = gui.getSaveFile();
        int currentPage = gui.getPage();

        // Navigation: Previous / Next / Exit
        if (slot == 45 && clicked.getType() == org.bukkit.Material.ARROW) {
            new ListGUI(saveFile, currentPage - 1).open(player);
            player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 1, 1.1f);
            return;
        }
        if (slot == 53 && clicked.getType() == org.bukkit.Material.ARROW) {
            new ListGUI(saveFile, currentPage + 1).open(player);
            player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 1, 1.1f);
            return;
        }
        if (slot == 49 && clicked.getType() == org.bukkit.Material.BARRIER) {
            player.closeInventory();
            return;
        }

        // Hologram actions
        List<HologramData> all = saveFile.getSavedHolograms();
        int index = currentPage * 45 + slot;
        if (index >= all.size()) return;

        HologramData target = all.get(index);

        if (event.getClick() == ClickType.LEFT) {

            // Teleport
            Location tpLoc = target.location();
            if (tpLoc.getWorld() != null) {

                player.teleport(tpLoc);
                player.sendMessage(slabel + ChatColor.GREEN + "§aTeleported to hologram: §f" + target.text());
                player.playSound(tpLoc, Sound.ENTITY_ENDERMAN_TELEPORT, 1, 1);

            } else {
                player.sendMessage(slabel + ChatColor.RED + "§cHologram world not found!");
            }

        } else if (event.getClick() == ClickType.RIGHT) {
            hologramManager.deleteHologram(target);
            new ListGUI(hologramManager.getSaveFile(), currentPage).open(player);
            player.sendMessage(slabel + ChatColor.RED + "§cDeleted hologram: §f" + target.text());
            player.playSound(player.getLocation(), Sound.ENTITY_ITEM_BREAK, 1, 0.8f);
        }
    }
}
