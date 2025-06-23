package ahjd.asgHolos.guis;

import ahjd.asgHolos.data.HologramData;
import ahjd.asgHolos.data.SaveFile;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class ListGUI implements InventoryHolder {

    private final Inventory inventory;
    private final SaveFile saveFile;
    private final List<HologramData> allHolograms;
    private final int page;

    private static final Map<UUID, Integer> pageTracker = new HashMap<>();

    public ListGUI(SaveFile saveFile, int page) {
        this.saveFile = saveFile;
        this.allHolograms = saveFile.getSavedHolograms();
        this.page = page;
        this.inventory = Bukkit.createInventory(this, 54, "Hologram List (Page " + (page + 1) + ")");

        populate();
    }

    private void populate() {
        int start = page * 45;
        int end = Math.min(start + 45, allHolograms.size());
        List<HologramData> currentPage = allHolograms.subList(start, end);

        for (int i = 0; i < currentPage.size(); i++) {
            HologramData data = currentPage.get(i);
            inventory.setItem(i, createHoloItem(data));
        }

        // Bottom row: slots 45-53
        for (int i = 45; i < 54; i++) {
            inventory.setItem(i, createFiller());
        }

        // Exit button in center
        inventory.setItem(49, createGuiItem(Material.BARRIER, "§cExit", List.of("§7Close the menu")));

        // Prev page (slot 45) and Next page (slot 53)
        if (page > 0) {
            inventory.setItem(45, createGuiItem(Material.ARROW, "§e◀ Previous Page", List.of()));
        }

        if (allHolograms.size() > (page + 1) * 45) {
            inventory.setItem(53, createGuiItem(Material.ARROW, "§eNext Page ▶", List.of()));
        }
    }

    private ItemStack createHoloItem(HologramData data) {
        ItemStack item = new ItemStack(Material.NAME_TAG);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName("§a" + data.text());
            meta.setLore(List.of(
                    "§7World: §f" + data.location().getWorld().getName(),
                    "§7X: " + data.location().getBlockX(),
                    "§7Y: " + data.location().getBlockY(),
                    "§7Z: " + data.location().getBlockZ(),
                    "§8Left-click: Teleport",
                    "§8Right-click: Delete"
            ));
            item.setItemMeta(meta);
        }
        return item;
    }

    private ItemStack createGuiItem(Material material, String name, List<String> lore) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(name);
            meta.setLore(lore);
            item.setItemMeta(meta);
        }
        return item;
    }

    private ItemStack createFiller() {
        return createGuiItem(Material.GRAY_STAINED_GLASS_PANE, " ", List.of());
    }

    public void open(Player player) {
        player.openInventory(inventory);
        pageTracker.put(player.getUniqueId(), page);
    }

    public static int getPage(Player player) {
        return pageTracker.getOrDefault(player.getUniqueId(), 0);
    }

    public static void setPage(Player player, int page) {
        pageTracker.put(player.getUniqueId(), page);
    }

    @Override
    public Inventory getInventory() {
        return inventory;
    }

    public SaveFile getSaveFile() {
        return saveFile;
    }

    public int getPage() {
        return page;
    }
}