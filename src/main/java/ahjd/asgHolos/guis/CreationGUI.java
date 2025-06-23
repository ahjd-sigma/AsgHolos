package ahjd.asgHolos.guis;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import java.util.List;

public class CreationGUI implements InventoryHolder {

    private final Inventory inventory;

    public void open(Player player){
        player.openInventory(this.inventory);
    }

    @Override
    public Inventory getInventory() {
        return inventory;
    }

    public CreationGUI() {
        this.inventory = Bukkit.createInventory(this, 54, "Creation GUI");
        createItems();
    }

    private void createItems() {
        inventory.setItem(22, createGuiItem(Material.NAME_TAG, "§aCreate Hologram", List.of("Click to start creating a hologram")));
        inventory.setItem(49, createGuiItem(Material.BARRIER, "§cCancel", List.of("Close the creation menu")));
    }

    private ItemStack createGuiItem(Material material, String name, List<String> lore) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        if (meta == null) return item; // fallback: just return item without meta
        meta.setDisplayName(name);
        meta.setLore(lore);
        item.setItemMeta(meta);
        return item;
    }
}
