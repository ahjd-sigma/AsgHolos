package ahjd.asgHolos.Listeners;

import ahjd.asgHolos.data.HologramData;
import ahjd.asgHolos.data.HologramManager;
import ahjd.asgHolos.data.SaveFile;
import ahjd.asgHolos.guis.ListGUI;
import java.util.List;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

public class ListGUIListener implements Listener {
    private final HologramManager hologramManager;

    public ListGUIListener(HologramManager hologramManager) {
        this.hologramManager = hologramManager;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        HumanEntity var3;
        if ((var3 = event.getWhoClicked()) instanceof Player) {
            Player player = (Player)var3;
            InventoryHolder var5;
            if ((var5 = event.getInventory().getHolder()) instanceof ListGUI) {
                ListGUI gui = (ListGUI)var5;
                String var10000 = String.valueOf(ChatColor.DARK_GRAY);
                String slabel = var10000 + "[" + String.valueOf(ChatColor.YELLOW) + "\ud83d\udd25 " + String.valueOf(ChatColor.GOLD) + "Sigma" + String.valueOf(ChatColor.DARK_GRAY) + "] ";
                event.setCancelled(true);
                int slot = event.getRawSlot();
                ItemStack clicked = event.getCurrentItem();
                if (clicked != null && clicked.hasItemMeta()) {
                    SaveFile saveFile = gui.getSaveFile();
                    int currentPage = gui.getPage();
                    boolean isShowingTemp = gui.isShowingTemporary();
                    if (slot == 47) {
                        boolean currentMode = ListGUI.isShowingTemporary(player);
                        ListGUI.setShowingTemporary(player, !currentMode);
                        (new ListGUI(saveFile, this.hologramManager, 0, !currentMode)).open(player);
                        player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 1.0F, 1.2F);
                    } else if (slot == 51 && isShowingTemp) {
                        (new ListGUI(saveFile, this.hologramManager, gui.getPage(), true)).open(player);
                        player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1.0F, 1.5F);
                        player.sendMessage(slabel + String.valueOf(ChatColor.GREEN) + "Refreshed temporary hologram list!");
                    } else if (slot == 45 && clicked.getType() == Material.ARROW) {
                        (new ListGUI(saveFile, this.hologramManager, currentPage - 1, isShowingTemp)).open(player);
                        player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 1.0F, 1.1F);
                    } else if (slot == 53 && clicked.getType() == Material.ARROW) {
                        (new ListGUI(saveFile, this.hologramManager, currentPage + 1, isShowingTemp)).open(player);
                        player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 1.0F, 1.1F);
                    } else if (slot == 49 && clicked.getType() == Material.BARRIER) {
                        player.closeInventory();
                        player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 1.0F, 0.8F);
                    } else {
                        List<HologramData> currentHolograms = gui.getHolograms();
                        int index = currentPage * 45 + slot;
                        if (index < currentHolograms.size() && slot < 45) {
                            HologramData target = (HologramData)currentHolograms.get(index);
                            String holoType;
                            if (event.getClick() == ClickType.LEFT) {
                                Location tpLoc = target.location();
                                if (tpLoc.getWorld() != null) {
                                    player.teleport(tpLoc);
                                    holoType = isShowingTemp ? "temporary hologram" : "hologram";
                                    player.sendMessage(slabel + String.valueOf(ChatColor.GREEN) + "Teleported to " + holoType + ": §f" + target.text());
                                    player.playSound(tpLoc, Sound.ENTITY_ENDERMAN_TELEPORT, 1.0F, 1.0F);
                                } else {
                                    player.sendMessage(slabel + String.valueOf(ChatColor.RED) + "Hologram world not found!");
                                }
                            } else if (event.getClick() == ClickType.RIGHT) {
                                boolean deleted = this.hologramManager.deleteHologram(target);
                                if (deleted) {
                                    (new ListGUI(saveFile, this.hologramManager, currentPage, isShowingTemp)).open(player);
                                    holoType = isShowingTemp ? "temporary hologram" : "hologram";
                                    player.sendMessage(slabel + String.valueOf(ChatColor.RED) + "Deleted " + holoType + ": §f" + target.displayName());
                                    player.playSound(player.getLocation(), Sound.ENTITY_ITEM_BREAK, 1.0F, 0.8F);
                                } else {
                                    player.sendMessage(slabel + String.valueOf(ChatColor.RED) + "Failed to delete hologram!");
                                    player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 1.0F, 1.0F);
                                }
                            }

                        }
                    }
                }
            }
        }
    }
}
