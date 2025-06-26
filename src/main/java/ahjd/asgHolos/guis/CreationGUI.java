package ahjd.asgHolos.guis;

import ahjd.asgHolos.AsgHolos;
import ahjd.asgHolos.data.Config;
import ahjd.asgHolos.data.HologramManager;
import ahjd.asgHolos.utils.ChatInput;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.entity.Display.Billboard;
import org.bukkit.entity.TextDisplay.TextAlignment;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class CreationGUI implements InventoryHolder {
    private final Inventory inventory;
    private String currentText = "Hello World!";
    private String currentName = "";

    public void open(Player player) {
        player.openInventory(this.inventory);
    }

    public Inventory getInventory() {
        return this.inventory;
    }

    public CreationGUI() {
        this.inventory = Bukkit.createInventory(this, 54, "Hologram Creation");
        this.createItems();
    }

    public CreationGUI(String customText) {
        this.currentText = customText;
        this.currentName = "";
        this.inventory = Bukkit.createInventory(this, 54, "Hologram Creation");
        this.createItems();
    }

    private void createItems() {
        this.fillGUI(this.inventory);
        this.updateCreateButton(true); // Default to persistent
        this.inventory.setItem(49, this.createGuiItem(Material.BARRIER, "§cCancel", List.of(String.valueOf(ChatColor.DARK_GRAY) + "Close the creation menu")));
        Inventory var10000 = this.inventory;
        Material var10003 = Material.NAME_TAG;
        String var10005 = String.valueOf(ChatColor.DARK_GRAY) + "Click to enter text via chat";
        String var10006 = String.valueOf(ChatColor.GRAY);
        var10006 = var10006 + "Preview: " + ChatInput.colorize(this.currentText);
        String var10007 = String.valueOf(ChatColor.GRAY);
        var10000.setItem(22, this.createGuiItem(var10003, "§bText Input", List.of(var10005, var10006, var10007 + "Raw: " + String.valueOf(ChatColor.WHITE) + this.currentText, "", String.valueOf(ChatColor.YELLOW) + "→ Supports &colors and {#hex}", String.valueOf(ChatColor.YELLOW) + "→ Click to change")));
        var10000 = this.inventory;
        var10003 = Material.BLACK_DYE;
        var10005 = String.valueOf(ChatColor.DARK_GRAY) + "Click to enter name via chat";
        var10006 = String.valueOf(ChatColor.GRAY);
        var10000.setItem(23, this.createGuiItem(var10003, "§bHologram Name", List.of(var10005, var10006 + "Current Name: " + String.valueOf(ChatColor.WHITE) + (this.currentName.isEmpty() ? "Nameless" : this.currentName), "", String.valueOf(ChatColor.YELLOW) + "→ Click to change")));
        this.inventory.setItem(10, this.createGuiItem(Material.WITHER_ROSE, "§9Shadowed Text", List.of(String.valueOf(ChatColor.DARK_GRAY) + "Add shadow behind text", String.valueOf(ChatColor.GRAY) + "Current State:", String.valueOf(ChatColor.RED) + "Disabled")));
        this.inventory.setItem(11, this.createGuiItem(Material.FIRE_CHARGE, "§6Persistence", List.of(String.valueOf(ChatColor.DARK_GRAY) + "Temporary/Persistent hologram", String.valueOf(ChatColor.GRAY) + "Current State:", String.valueOf(ChatColor.GREEN) + "Persistent")));
        this.inventory.setItem(12, this.createGuiItem(Material.PAINTING, "§eBillboard", List.of(String.valueOf(ChatColor.DARK_GRAY) + "The type of billboard rotation", String.valueOf(ChatColor.GRAY) + "Current Mode:", String.valueOf(ChatColor.GOLD) + "CENTER")));
        this.inventory.setItem(13, this.createGuiItem(Material.TINTED_GLASS, "§3See Through", List.of(String.valueOf(ChatColor.DARK_GRAY) + "Visibility through blocks", String.valueOf(ChatColor.GRAY) + "Current State:", String.valueOf(ChatColor.RED) + "Disabled")));
        this.inventory.setItem(14, this.createGuiItem(Material.MAGMA_CREAM, "§dScale", List.of(String.valueOf(ChatColor.DARK_GRAY) + "Size of the hologram", String.valueOf(ChatColor.GRAY) + "Current Scale:", String.valueOf(ChatColor.YELLOW) + "1.00", "", String.valueOf(ChatColor.GREEN) + "Left Click: +0.01", String.valueOf(ChatColor.RED) + "Right Click: -0.01", String.valueOf(ChatColor.AQUA) + "Shift + Left: +0.1", String.valueOf(ChatColor.LIGHT_PURPLE) + "Shift + Right: -0.1")));
        this.inventory.setItem(15, this.createGuiItem(Material.COMPASS, "§cYaw/Pitch", List.of(String.valueOf(ChatColor.DARK_GRAY) + "Rotation angles", String.valueOf(ChatColor.GRAY) + "Current Yaw: " + String.valueOf(ChatColor.YELLOW) + "0.0° (North)", String.valueOf(ChatColor.GRAY) + "Current Pitch: " + String.valueOf(ChatColor.YELLOW) + "0.0°", "", String.valueOf(ChatColor.GREEN) + "Left Click: +1° Yaw", String.valueOf(ChatColor.RED) + "Right Click: +1° Pitch", String.valueOf(ChatColor.AQUA) + "Shift + Left: +45° Yaw", String.valueOf(ChatColor.LIGHT_PURPLE) + "Shift + Right: +15° Pitch")));
        this.inventory.setItem(16, this.createGuiItem(Material.ITEM_FRAME, "§aText Alignment", List.of(String.valueOf(ChatColor.DARK_GRAY) + "Text alignment mode", String.valueOf(ChatColor.GRAY) + "Current Alignment:", String.valueOf(ChatColor.GOLD) + "CENTER")));
        this.inventory.setItem(19, this.createGuiItem(Material.GLASS, "§fText Opacity", List.of(String.valueOf(ChatColor.DARK_GRAY) + "Text transparency", String.valueOf(ChatColor.GRAY) + "Current Opacity:", String.valueOf(ChatColor.YELLOW) + "255 (100%)", "", String.valueOf(ChatColor.GREEN) + "Left Click: +1", String.valueOf(ChatColor.RED) + "Right Click: -1", String.valueOf(ChatColor.AQUA) + "Shift + Left: +10", String.valueOf(ChatColor.LIGHT_PURPLE) + "Shift + Right: -10")));
        this.inventory.setItem(20, this.createGuiItem(Material.BLACK_STAINED_GLASS, "§8Background", List.of(String.valueOf(ChatColor.DARK_GRAY) + "Background color and opacity", String.valueOf(ChatColor.GRAY) + "Current Color: " + String.valueOf(ChatColor.YELLOW) + "0x40000000", String.valueOf(ChatColor.GRAY) + "Current Opacity: " + String.valueOf(ChatColor.YELLOW) + "64 (25%)", "", String.valueOf(ChatColor.GREEN) + "Left Click: +1 Opacity", String.valueOf(ChatColor.RED) + "Right Click: -1 Opacity", String.valueOf(ChatColor.AQUA) + "Shift + Left: +10 Opacity", String.valueOf(ChatColor.LIGHT_PURPLE) + "Shift + Right: -10 Opacity")));
        this.inventory.setItem(21, this.createGuiItem(Material.SPYGLASS, "§bView Distance", List.of(String.valueOf(ChatColor.DARK_GRAY) + "Maximum view distance", String.valueOf(ChatColor.GRAY) + "Current Distance:", String.valueOf(ChatColor.YELLOW) + "64 blocks", "", String.valueOf(ChatColor.GREEN) + "Left Click: +1", String.valueOf(ChatColor.RED) + "Right Click: -1", String.valueOf(ChatColor.AQUA) + "Shift + Left: +16", String.valueOf(ChatColor.LIGHT_PURPLE) + "Shift + Right: -16")));
        var10000 = this.inventory;
        var10003 = Material.PAPER;
        var10005 = String.valueOf(ChatColor.DARK_GRAY) + "Entity UUID will be assigned";
        var10006 = String.valueOf(ChatColor.DARK_GRAY) + "automatically upon creation";
        var10007 = String.valueOf(ChatColor.GRAY);
        var10000.setItem(8, this.createGuiItem(var10003, "§7UUID Info", List.of(var10005, var10006, var10007 + "Preview: " + String.valueOf(ChatColor.YELLOW) + UUID.randomUUID().toString().substring(0, 8) + "...")));
    }

    private ItemStack createGuiItem(Material material, String name, List<String> lore) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        if (meta == null) {
            return item;
        } else {
            meta.setDisplayName(name);
            meta.setLore(lore);
            item.setItemMeta(meta);
            return item;
        }
    }

    private void fillGUI(Inventory inventory) {
        ItemStack filler = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
        ItemMeta fillerMeta = filler.getItemMeta();
        if (fillerMeta != null) {
            fillerMeta.setDisplayName(" ");
            filler.setItemMeta(fillerMeta);
        }

        for(int i = 0; i < 54; ++i) {
            inventory.setItem(i, filler);
        }

    }

    public void updateShadowToggle(boolean enabled) {
        ItemStack item = this.inventory.getItem(10);
        if (item != null && item.hasItemMeta()) {
            ItemMeta meta = item.getItemMeta();
            if (meta != null) {
                meta.setLore(List.of(String.valueOf(ChatColor.DARK_GRAY) + "Add shadow behind text", String.valueOf(ChatColor.GRAY) + "Current State:", enabled ? String.valueOf(ChatColor.GREEN) + "Enabled" : String.valueOf(ChatColor.RED) + "Disabled"));
                item.setItemMeta(meta);
            }
        }

    }

    public void updatePersistenceToggle(boolean persistent) {
        ItemStack item = this.inventory.getItem(11);
        if (item != null && item.hasItemMeta()) {
            ItemMeta meta = item.getItemMeta();
            if (meta != null) {
                meta.setLore(List.of(String.valueOf(ChatColor.DARK_GRAY) + "Temporary/Persistent hologram", String.valueOf(ChatColor.GRAY) + "Current State:", persistent ? String.valueOf(ChatColor.GREEN) + "Persistent" : String.valueOf(ChatColor.YELLOW) + "Temporary"));
                item.setItemMeta(meta);
            }
        }
        
        // Update create button when persistence changes
        this.updateCreateButton(persistent);
    }
    
    public void updateCreateButton(boolean isPersistent) {
        HologramManager hologramManager = AsgHolos.getInstance().getHologramManager();
        Config config = AsgHolos.getInstance().getPluginConfig();
        
        int currentHolograms;
        Integer maxHolograms;
        String hologramType;
        
        if (isPersistent) {
            currentHolograms = hologramManager.getActivePersistentHolograms();
            maxHolograms = config.getMaxPersistentHolograms();
            hologramType = "Persistent";
        } else {
            currentHolograms = hologramManager.getActiveTempHolograms();
            maxHolograms = config.getMaxTempHolograms();
            hologramType = "Temporary";
        }
        
        boolean canCreate = maxHolograms == null || currentHolograms < maxHolograms;
        String limitStatus = maxHolograms == null ? "§aUnlimited" : "§6" + currentHolograms + "§7/" + maxHolograms;
        String buttonName = canCreate ? "§aCreate Hologram" : "§c" + hologramType + " Hologram Limit Reached";
        
        List<String> lore = new ArrayList<>();
        lore.add(String.valueOf(ChatColor.DARK_GRAY) + "Click to spawn the hologram");
        lore.add("");
        lore.add(String.valueOf(ChatColor.GRAY) + hologramType + " Holograms: " + limitStatus);
        
        if (!canCreate) {
            lore.add("");
            lore.add(String.valueOf(ChatColor.RED) + "§c" + hologramType + " hologram limit reached!");
            lore.add(String.valueOf(ChatColor.YELLOW) + "Use /holo limit for more info");
        }
        
        this.inventory.setItem(31, this.createGuiItem(Material.NETHER_STAR, buttonName, lore));
    }

    public void updateSeeThroughToggle(boolean enabled) {
        ItemStack item = this.inventory.getItem(13);
        if (item != null && item.hasItemMeta()) {
            ItemMeta meta = item.getItemMeta();
            if (meta != null) {
                meta.setLore(List.of(String.valueOf(ChatColor.DARK_GRAY) + "Visibility through blocks", String.valueOf(ChatColor.GRAY) + "Current State:", enabled ? String.valueOf(ChatColor.GREEN) + "Enabled" : String.valueOf(ChatColor.RED) + "Disabled"));
                item.setItemMeta(meta);
            }
        }

    }

    public void updateScale(float scale) {
        ItemStack item = this.inventory.getItem(14);
        if (item != null && item.hasItemMeta()) {
            ItemMeta meta = item.getItemMeta();
            if (meta != null) {
                meta.setLore(List.of(String.valueOf(ChatColor.DARK_GRAY) + "Size of the hologram", String.valueOf(ChatColor.GRAY) + "Current Scale:", String.valueOf(ChatColor.YELLOW) + String.format("%.2f", scale), "", String.valueOf(ChatColor.GREEN) + "Left Click: +0.01", String.valueOf(ChatColor.RED) + "Right Click: -0.01", String.valueOf(ChatColor.AQUA) + "Shift + Left: +0.1", String.valueOf(ChatColor.LIGHT_PURPLE) + "Shift + Right: -0.1"));
                item.setItemMeta(meta);
            }
        }

    }

    public void updateRotation(float yaw, float pitch) {
        ItemStack item = this.inventory.getItem(15);
        if (item != null && item.hasItemMeta()) {
            ItemMeta meta = item.getItemMeta();
            if (meta != null) {
                String direction = this.getDirectionFromYaw(yaw);
                meta.setLore(List.of(String.valueOf(ChatColor.DARK_GRAY) + "Rotation angles", String.valueOf(ChatColor.GRAY) + "Current Yaw: " + String.valueOf(ChatColor.YELLOW) + String.format("%.1f° (%s)", yaw, direction), String.valueOf(ChatColor.GRAY) + "Current Pitch: " + String.valueOf(ChatColor.YELLOW) + String.format("%.1f°", pitch), "", String.valueOf(ChatColor.GREEN) + "Left Click: +1° Yaw", String.valueOf(ChatColor.RED) + "Right Click: +1° Pitch", String.valueOf(ChatColor.AQUA) + "Shift + Left: +45° Yaw", String.valueOf(ChatColor.LIGHT_PURPLE) + "Shift + Right: +15° Pitch"));
                item.setItemMeta(meta);
            }
        }

    }

    private String getDirectionFromYaw(float yaw) {
        yaw = (yaw % 360.0F + 360.0F) % 360.0F;
        if (!(yaw >= 315.0F) && !(yaw < 45.0F)) {
            if (yaw >= 45.0F && yaw < 135.0F) {
                return "East";
            } else {
                return yaw >= 135.0F && yaw < 225.0F ? "South" : "West";
            }
        } else {
            return "North";
        }
    }

    public void updateTextOpacity(int opacity) {
        ItemStack item = this.inventory.getItem(19);
        if (item != null && item.hasItemMeta()) {
            ItemMeta meta = item.getItemMeta();
            if (meta != null) {
                int percentage = (int)((double)opacity / 255.0D * 100.0D);
                meta.setLore(List.of(String.valueOf(ChatColor.DARK_GRAY) + "Text transparency", String.valueOf(ChatColor.GRAY) + "Current Opacity:", String.valueOf(ChatColor.YELLOW) + opacity + " (" + percentage + "%)", "", String.valueOf(ChatColor.GREEN) + "Left Click: +1", String.valueOf(ChatColor.RED) + "Right Click: -1", String.valueOf(ChatColor.AQUA) + "Shift + Left: +10", String.valueOf(ChatColor.LIGHT_PURPLE) + "Shift + Right: -10"));
                item.setItemMeta(meta);
            }
        }

    }

    public void updateBackgroundOpacity(int opacity) {
        ItemStack item = this.inventory.getItem(20);
        if (item != null && item.hasItemMeta()) {
            ItemMeta meta = item.getItemMeta();
            if (meta != null) {
                int percentage = (int)((double)opacity / 255.0D * 100.0D);
                meta.setLore(List.of(String.valueOf(ChatColor.DARK_GRAY) + "Background color and opacity", String.valueOf(ChatColor.GRAY) + "Current Color: " + String.valueOf(ChatColor.YELLOW) + String.format("0x%08X", opacity << 24 | 0), String.valueOf(ChatColor.GRAY) + "Current Opacity: " + String.valueOf(ChatColor.YELLOW) + String.valueOf(opacity) + " (" + percentage + "%)", "", String.valueOf(ChatColor.GREEN) + "Left Click: +1 Opacity", String.valueOf(ChatColor.RED) + "Right Click: -1 Opacity", String.valueOf(ChatColor.AQUA) + "Shift + Left: +10 Opacity", String.valueOf(ChatColor.LIGHT_PURPLE) + "Shift + Right: -10 Opacity"));
                item.setItemMeta(meta);
            }
        }

    }

    public void updateBillboardCycle(Billboard billboard) {
        ItemStack item = this.inventory.getItem(12);
        if (item != null && item.hasItemMeta()) {
            ItemMeta meta = item.getItemMeta();
            if (meta != null) {
                String var10001 = String.valueOf(ChatColor.DARK_GRAY) + "The type of billboard rotation";
                String var10002 = String.valueOf(ChatColor.GRAY) + "Current Mode:";
                String var10003 = String.valueOf(ChatColor.GOLD);
                meta.setLore(List.of(var10001, var10002, var10003 + billboard.name()));
                item.setItemMeta(meta);
            }
        }

    }

    public void updateAlignmentCycle(TextAlignment alignment) {
        ItemStack item = this.inventory.getItem(16);
        if (item != null && item.hasItemMeta()) {
            ItemMeta meta = item.getItemMeta();
            if (meta != null) {
                String var10001 = String.valueOf(ChatColor.DARK_GRAY) + "Text alignment mode";
                String var10002 = String.valueOf(ChatColor.GRAY) + "Current Alignment:";
                String var10003 = String.valueOf(ChatColor.GOLD);
                meta.setLore(List.of(var10001, var10002, var10003 + alignment.name()));
                item.setItemMeta(meta);
            }
        }

    }

    public void updateViewDistance(int distance) {
        ItemStack item = this.inventory.getItem(21);
        if (item != null && item.hasItemMeta()) {
            ItemMeta meta = item.getItemMeta();
            if (meta != null) {
                meta.setLore(List.of(String.valueOf(ChatColor.DARK_GRAY) + "Maximum view distance", String.valueOf(ChatColor.GRAY) + "Current Distance:", String.valueOf(ChatColor.YELLOW) + distance + " blocks", "", String.valueOf(ChatColor.GREEN) + "Left Click: +1", String.valueOf(ChatColor.RED) + "Right Click: -1", String.valueOf(ChatColor.AQUA) + "Shift + Left: +16", String.valueOf(ChatColor.LIGHT_PURPLE) + "Shift + Right: -16"));
                item.setItemMeta(meta);
            }
        }

    }

    public void updateName(String name) {
        ItemStack item = this.inventory.getItem(23);
        if (item != null && item.hasItemMeta()) {
            ItemMeta meta = item.getItemMeta();
            if (meta != null) {
                String var10001 = String.valueOf(ChatColor.DARK_GRAY);
                var10001 = var10001 + "Click to enter name via chat";
                String var10002 = String.valueOf(ChatColor.GRAY);
                meta.setLore(List.of(var10001, var10002 + "Current Name: " + String.valueOf(ChatColor.WHITE) + (name.isEmpty() ? "Nameless" : name), "", String.valueOf(ChatColor.YELLOW) + "→ Click to change"));
                item.setItemMeta(meta);
            }
        }

    }
}
