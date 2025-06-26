package ahjd.asgHolos.guis;

import ahjd.asgHolos.data.HologramData;
import ahjd.asgHolos.data.HologramManager;
import ahjd.asgHolos.data.SaveFile;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.entity.TextDisplay;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class ListGUI implements InventoryHolder {
    private final Inventory inventory;
    private final SaveFile saveFile;
    private final HologramManager hologramManager;
    private final List<HologramData> holograms;
    private final int page;
    private final boolean showingTemporary;
    private static final Map<UUID, Integer> pageTracker = new HashMap();
    private static final Map<UUID, Boolean> modeTracker = new HashMap();

    public ListGUI(SaveFile saveFile, HologramManager hologramManager, int page, boolean showingTemporary) {
        this.saveFile = saveFile;
        this.hologramManager = hologramManager;
        this.page = page;
        this.showingTemporary = showingTemporary;
        if (showingTemporary) {
            this.holograms = this.getTemporaryHolograms();
            this.inventory = Bukkit.createInventory(this, 54, "Temporary Holograms (Page " + (page + 1) + ")");
        } else {
            this.holograms = saveFile.getSavedHolograms();
            this.inventory = Bukkit.createInventory(this, 54, "Persistent Holograms (Page " + (page + 1) + ")");
        }

        this.populate();
    }

    public ListGUI(SaveFile saveFile, int page) {
        this(saveFile, (HologramManager)null, page, false);
    }

    private List<HologramData> getTemporaryHolograms() {
        List<HologramData> tempHolograms = new ArrayList();
        Set<UUID> persistentUUIDs = (Set)this.saveFile.getSavedHolograms().stream().map(HologramData::entityUUID).filter(Objects::nonNull).collect(Collectors.toSet());
        Iterator var4 = Bukkit.getWorlds().iterator();

        while(var4.hasNext()) {
            World world = (World)var4.next();
            Iterator var6 = world.getEntitiesByClass(new Class[]{TextDisplay.class, TextDisplay.class}).iterator();

            while(var6.hasNext()) {
                TextDisplay display = (TextDisplay)var6.next();
                UUID uuid = display.getUniqueId();
                if (!persistentUUIDs.contains(uuid) && this.hologramManager.isOwnedHologram(display)) {
                    tempHolograms.add(new HologramData(display.getLocation(), display.getText(), display.getText(), false, display.isShadowed(), display.isSeeThrough(), display.getBillboard(), display.getLocation().getYaw(), display.getLocation().getPitch(), display.getTransformation().getScale().x(), display.getAlignment(), display.getTextOpacity(), display.getBackgroundColor().asARGB(), (int)display.getViewRange(), uuid));
                }
            }
        }

        tempHolograms.sort((a, b) -> {
            int worldCompare = a.location().getWorld().getName().compareTo(b.location().getWorld().getName());
            if (worldCompare != 0) {
                return worldCompare;
            } else {
                int yCompare = Double.compare(b.location().getY(), a.location().getY());
                if (yCompare != 0) {
                    return yCompare;
                } else {
                    int xCompare = Double.compare(a.location().getX(), b.location().getX());
                    return xCompare != 0 ? xCompare : Double.compare(a.location().getZ(), b.location().getZ());
                }
            }
        });
        return tempHolograms;
    }

    private void populate() {
        String toggleText;
        if (this.holograms.isEmpty()) {
            this.inventory.clear();
            String noHologramsMessage = this.showingTemporary ? "§7No temporary holograms present" : "§7No persistent holograms present";
            String subtitle = this.showingTemporary ? "§8Temporary holograms are not saved to file" : "§8Create a hologram to see it here";
            Inventory var10000 = this.inventory;
            Material var10003 = Material.NAME_TAG;
            String var10005 = String.valueOf(ChatColor.RED);
            var10000.setItem(22, this.createGuiItem(var10003, noHologramsMessage, List.of(var10005 + subtitle)));

            for(int i = 45; i < 54; ++i) {
                this.inventory.setItem(i, this.createFiller());
            }

            toggleText = this.showingTemporary ? "§7Click to view saved holograms" : "§7Click to view temporary holograms";
            this.inventory.setItem(47, this.createGuiItem(Material.COMPASS, toggleText, List.of(toggleText)));
            this.inventory.setItem(49, this.createGuiItem(Material.BARRIER, "§cExit", List.of("§7Close the menu")));
            if (this.showingTemporary) {
                this.inventory.setItem(51, this.createGuiItem(Material.EMERALD, "§aRefresh", List.of("§7Refresh the temporary hologram list")));
            }

        } else {
            int start = this.page * 45;
            int end = Math.min(start + 45, this.holograms.size());
            List<HologramData> currentPage = this.holograms.subList(start, end);

            int i;
            for(i = 0; i < currentPage.size(); ++i) {
                HologramData data = (HologramData)currentPage.get(i);
                this.inventory.setItem(i, this.createHoloItem(data));
            }

            for(i = 45; i < 54; ++i) {
                this.inventory.setItem(i, this.createFiller());
            }

            toggleText = this.showingTemporary ? "§aPersistent Holograms" : "§eTemprary Holograms";
            String toggleLore = this.showingTemporary ? "§7Click to view saved holograms" : "§7Click to view temporary holograms";
            this.inventory.setItem(47, this.createGuiItem(Material.COMPASS, toggleText, List.of(toggleLore)));
            this.inventory.setItem(49, this.createGuiItem(Material.BARRIER, "§cExit", List.of("§7Close the menu")));
            if (this.showingTemporary) {
                this.inventory.setItem(51, this.createGuiItem(Material.EMERALD, "§aRefresh", List.of("§7Refresh the temporary hologram list")));
            }

            if (this.page > 0) {
                this.inventory.setItem(45, this.createGuiItem(Material.ARROW, "§e◀ Previous Page", List.of()));
            }

            if (this.holograms.size() > (this.page + 1) * 45) {
                this.inventory.setItem(53, this.createGuiItem(Material.ARROW, "§eNext Page ▶", List.of()));
            }

        }
    }

    private ItemStack createHoloItem(HologramData data) {
        ItemStack item = new ItemStack(Material.NAME_TAG);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            String displayName = this.showingTemporary ? "§e" + data.text() + " §7(Temp)" : "§a" + data.text();
            meta.setDisplayName(displayName);
            meta.addItemFlags(new ItemFlag[]{ItemFlag.HIDE_ADDITIONAL_TOOLTIP});
            List<String> lore = new ArrayList();
            lore.add("§7World: §f" + data.location().getWorld().getName());
            lore.add("§7X: " + data.location().getBlockX());
            lore.add("§7Y: " + data.location().getBlockY());
            lore.add("§7Z: " + data.location().getBlockZ());
            lore.add("§8Left-click: Teleport");
            lore.add("§8Right-click: Delete");
            if (this.showingTemporary) {
                lore.add("§c§oTemporary - Not saved to file");
            }

            meta.setLore(lore);
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
            meta.addItemFlags(new ItemFlag[]{ItemFlag.HIDE_ADDITIONAL_TOOLTIP});
            item.setItemMeta(meta);
        }

        return item;
    }

    private ItemStack createFiller() {
        ItemStack item = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(" ");
            meta.addItemFlags(new ItemFlag[]{ItemFlag.HIDE_ATTRIBUTES});
            item.setItemMeta(meta);
        }

        return item;
    }

    public void open(Player player) {
        player.openInventory(this.inventory);
        pageTracker.put(player.getUniqueId(), this.page);
        modeTracker.put(player.getUniqueId(), this.showingTemporary);
    }

    public static boolean isShowingTemporary(Player player) {
        return (Boolean)modeTracker.getOrDefault(player.getUniqueId(), false);
    }

    public static void setShowingTemporary(Player player, boolean showingTemporary) {
        modeTracker.put(player.getUniqueId(), showingTemporary);
    }

    public Inventory getInventory() {
        return this.inventory;
    }

    public SaveFile getSaveFile() {
        return this.saveFile;
    }

    public HologramManager getHologramManager() {
        return this.hologramManager;
    }

    public int getPage() {
        return this.page;
    }

    public boolean isShowingTemporary() {
        return this.showingTemporary;
    }

    public List<HologramData> getHolograms() {
        return this.holograms;
    }
}
