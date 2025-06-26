package ahjd.asgHolos.Listeners;

import ahjd.asgHolos.data.HologramData;
import ahjd.asgHolos.data.HologramManager;
import ahjd.asgHolos.guis.CreationGUI;
import ahjd.asgHolos.utils.ChatInput;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Display.Billboard;
import org.bukkit.entity.TextDisplay.TextAlignment;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;

public class CreationGUIListener implements Listener {
    private final Map<UUID, CreationGUIListener.HologramSettings> playerSettings = new HashMap();
    private final HologramManager hologramManager;
    private final Plugin plugin;
    private final String prefix;

    public CreationGUIListener(HologramManager hologramManager, Plugin plugin) {
        String var10001 = String.valueOf(ChatColor.DARK_GRAY);
        this.prefix = var10001 + "[" + String.valueOf(ChatColor.YELLOW) + "\ud83d\udd25 " + String.valueOf(ChatColor.GOLD) + "Sigma" + String.valueOf(ChatColor.DARK_GRAY) + "] ";
        this.hologramManager = hologramManager;
        this.plugin = plugin;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {
        HumanEntity var3 = e.getWhoClicked();
        if (var3 instanceof Player) {
            Player player = (Player)var3;
            if (e.getInventory().getHolder() instanceof CreationGUI) {
                e.setCancelled(true);
                UUID playerId = player.getUniqueId();
                CreationGUIListener.HologramSettings settings = this.getSettings(playerId);
                int slot = e.getRawSlot();
                boolean isShiftClick = e.isShiftClick();
                switch(slot) {
                    case 10:
                        this.handleShadowToggle(e, settings);
                        break;
                    case 11:
                        this.handlePersistenceToggle(e, settings);
                        break;
                    case 12:
                        this.handleBillboardCycle(e, settings);
                        break;
                    case 13:
                        this.handleSeeThoughToggle(e, settings);
                        break;
                    case 14:
                        this.handleScaleAdjust(e, settings, isShiftClick);
                        break;
                    case 15:
                        this.handleRotationAdjust(e, settings, isShiftClick);
                        break;
                    case 16:
                        this.handleAlignmentCycle(e, settings);
                    case 17:
                    case 18:
                    case 24:
                    case 25:
                    case 26:
                    case 27:
                    case 28:
                    case 29:
                    case 30:
                    case 32:
                    case 33:
                    case 34:
                    case 35:
                    case 36:
                    case 37:
                    case 38:
                    case 39:
                    case 40:
                    case 41:
                    case 42:
                    case 43:
                    case 44:
                    case 45:
                    case 46:
                    case 47:
                    case 48:
                    default:
                        break;
                    case 19:
                        this.handleTextOpacity(e, settings, isShiftClick);
                        break;
                    case 20:
                        this.handleBackgroundOpacity(e, settings, isShiftClick);
                        break;
                    case 21:
                        this.handleViewDistance(e, settings, isShiftClick);
                        break;
                    case 22:
                        this.handleTextInput(player, playerId);
                        break;
                    case 23:
                        this.handleNameInput(player, playerId);
                        break;
                    case 31:
                        this.createHologram(player, playerId, settings);
                        break;
                    case 49:
                        this.cancelCreation(player, playerId);
                }

                return;
            }
        }

    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent e) {
        HumanEntity var3 = e.getPlayer();
        if (var3 instanceof Player) {
            Player player = (Player)var3;
            if (e.getInventory().getHolder() instanceof CreationGUI) {
                UUID playerId = player.getUniqueId();
                if (!ChatInput.isNotAwaitingInput(playerId)) {
                    this.playerSettings.remove(playerId);
                }
            }
        }

    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent e) {
        this.playerSettings.remove(e.getPlayer().getUniqueId());
    }

    private CreationGUIListener.HologramSettings getSettings(UUID playerId) {
        return (CreationGUIListener.HologramSettings)this.playerSettings.computeIfAbsent(playerId, (k) -> {
            return new CreationGUIListener.HologramSettings();
        });
    }

    private void handleShadowToggle(InventoryClickEvent e, CreationGUIListener.HologramSettings settings) {
        settings.shadowed = !settings.shadowed;
        this.updateSimpleLore(e.getInventory().getItem(10), "Current State:", settings.shadowed ? String.valueOf(ChatColor.GREEN) + "Enabled" : String.valueOf(ChatColor.RED) + "Disabled");
    }

    private void handlePersistenceToggle(InventoryClickEvent e, CreationGUIListener.HologramSettings settings) {
        settings.persistence = !settings.persistence;
        this.updateSimpleLore(e.getInventory().getItem(11), "Current State:", settings.persistence ? String.valueOf(ChatColor.GREEN) + "Persistent" : String.valueOf(ChatColor.YELLOW) + "Temporary");
    }

    private void handleBillboardCycle(InventoryClickEvent e, CreationGUIListener.HologramSettings settings) {
        Billboard[] values = Billboard.values();
        int nextIndex = (settings.billboard.ordinal() + 1) % values.length;
        settings.billboard = values[nextIndex];
        ItemStack var10001 = e.getInventory().getItem(12);
        String var10003 = String.valueOf(ChatColor.GOLD);
        this.updateSimpleLore(var10001, "Current Mode:", var10003 + settings.billboard.name());
    }

    private void handleSeeThoughToggle(InventoryClickEvent e, CreationGUIListener.HologramSettings settings) {
        settings.seeThrough = !settings.seeThrough;
        this.updateSimpleLore(e.getInventory().getItem(13), "Current State:", settings.seeThrough ? String.valueOf(ChatColor.GREEN) + "Enabled" : String.valueOf(ChatColor.RED) + "Disabled");
    }

    private void handleScaleAdjust(InventoryClickEvent e, CreationGUIListener.HologramSettings settings, boolean isShiftClick) {
        float increment = isShiftClick ? 0.1F : 0.01F;
        if (e.isLeftClick()) {
            settings.scale = Math.min(settings.scale + increment, 5.0F);
        } else if (e.isRightClick()) {
            settings.scale = Math.max(settings.scale - increment, 0.01F);
        }

        this.updateScaleLore(e.getInventory().getItem(14), settings.scale);
    }

    private void handleRotationAdjust(InventoryClickEvent e, CreationGUIListener.HologramSettings settings, boolean isShiftClick) {
        float pitchIncrement;
        if (e.isLeftClick()) {
            pitchIncrement = isShiftClick ? 45.0F : 1.0F;
            settings.yaw = (settings.yaw + pitchIncrement) % 360.0F;
        } else if (e.isRightClick()) {
            pitchIncrement = isShiftClick ? 15.0F : 1.0F;
            settings.pitch += pitchIncrement;
            if (settings.pitch > 90.0F) {
                settings.pitch = -90.0F;
            }

            settings.pitch = Math.max(-90.0F, Math.min(90.0F, settings.pitch));
        }

        this.updateRotationLore(e.getInventory().getItem(15), settings);
    }

    private void handleAlignmentCycle(InventoryClickEvent e, CreationGUIListener.HologramSettings settings) {
        TextAlignment[] values = TextAlignment.values();
        int nextIndex = (settings.textAlignment.ordinal() + 1) % values.length;
        settings.textAlignment = values[nextIndex];
        ItemStack var10001 = e.getInventory().getItem(16);
        String var10003 = String.valueOf(ChatColor.GOLD);
        this.updateSimpleLore(var10001, "Current Alignment:", var10003 + settings.textAlignment.name());
    }

    private void handleTextOpacity(InventoryClickEvent e, CreationGUIListener.HologramSettings settings, boolean isShiftClick) {
        int increment = isShiftClick ? 10 : 1;
        settings.textOpacity = this.adjustValue(settings.textOpacity, e.isLeftClick(), increment, 0, 255);
        this.updateOpacityLore(e.getInventory().getItem(19), settings.textOpacity, "Text Opacity");
    }

    private void handleBackgroundOpacity(InventoryClickEvent e, CreationGUIListener.HologramSettings settings, boolean isShiftClick) {
        int increment = isShiftClick ? 10 : 1;
        settings.backgroundOpacity = this.adjustValue(settings.backgroundOpacity, e.isLeftClick(), increment, 0, 255);
        settings.backgroundColor = settings.backgroundOpacity << 24 | 0;
        this.updateBackgroundLore(e.getInventory().getItem(20), settings);
    }

    private void handleViewDistance(InventoryClickEvent e, CreationGUIListener.HologramSettings settings, boolean isShiftClick) {
        int increment = isShiftClick ? 16 : 1;
        settings.viewDistance = this.adjustValue(settings.viewDistance, e.isLeftClick(), increment, 1, 512);
        this.updateViewDistanceLore(e.getInventory().getItem(21), settings.viewDistance);
    }

    private void handleTextInput(Player player, UUID playerId) {
        ChatInput.requestInput(player, "Enter hologram text (supports &colors and {#hex}):", (inputText) -> {
            CreationGUIListener.HologramSettings settings = (CreationGUIListener.HologramSettings)this.playerSettings.get(playerId);
            if (settings == null) {
                String var10001 = this.prefix;
                player.sendMessage(var10001 + String.valueOf(ChatColor.RED) + "Settings not found. Please try again.");
            } else {
                settings.text = ChatInput.colorize(inputText);
                this.reopenGUI(player, playerId);
            }
        }, () -> {
            this.reopenGUI(player, playerId);
        });
    }

    private void handleNameInput(Player player, UUID playerId) {
        ChatInput.requestInput(player, "Enter hologram name (plain text only):", (inputName) -> {
            CreationGUIListener.HologramSettings settings = (CreationGUIListener.HologramSettings)this.playerSettings.get(playerId);
            if (settings == null) {
                String var10001 = this.prefix;
                player.sendMessage(var10001 + String.valueOf(ChatColor.RED) + "Settings not found. Please try again.");
            } else {
                settings.name = inputName;
                this.reopenGUI(player, playerId);
            }
        }, () -> {
            CreationGUIListener.HologramSettings settings = (CreationGUIListener.HologramSettings)this.playerSettings.get(playerId);
            if (settings != null) {
                settings.name = "Nameless";
            }

            this.reopenGUI(player, playerId);
        });
    }

    private void createHologram(Player player, UUID playerId, CreationGUIListener.HologramSettings settings) {
        player.closeInventory();
        Location loc = player.getLocation().clone().add(0.0D, 2.0D, 0.0D);
        HologramData holo = new HologramData(loc, settings.text, settings.name, settings.persistence, settings.shadowed, settings.seeThrough, settings.billboard, settings.yaw, settings.pitch, settings.scale, settings.textAlignment, settings.textOpacity, settings.backgroundColor, settings.viewDistance, (UUID)null);
        HologramData savedHolo = this.hologramManager.spawnHologram(holo);
        this.playerSettings.remove(playerId);
        String message = savedHolo != null ? String.valueOf(ChatColor.GREEN) + "Hologram created at your location!" : String.valueOf(ChatColor.RED) + "Failed to create hologram.";
        player.sendMessage(this.prefix + message);
    }

    private void cancelCreation(Player player, UUID playerId) {
        ChatInput.cancelInput(player.getUniqueId());
        player.closeInventory();
        this.playerSettings.remove(playerId);
        String var10001 = this.prefix;
        player.sendMessage(var10001 + String.valueOf(ChatColor.RED) + "Creation cancelled.");
    }

    private void reopenGUI(Player player, UUID playerId) {
        Bukkit.getScheduler().runTask(this.plugin, () -> {
            CreationGUIListener.HologramSettings settings = this.getSettings(playerId);
            CreationGUI gui = new CreationGUI(settings.text);
            gui.updateShadowToggle(settings.shadowed);
            gui.updatePersistenceToggle(settings.persistence);
            gui.updateBillboardCycle(settings.billboard);
            gui.updateSeeThroughToggle(settings.seeThrough);
            gui.updateScale(settings.scale);
            gui.updateRotation(settings.yaw, settings.pitch);
            gui.updateAlignmentCycle(settings.textAlignment);
            gui.updateTextOpacity(settings.textOpacity);
            gui.updateBackgroundOpacity(settings.backgroundOpacity);
            gui.updateViewDistance(settings.viewDistance);
            gui.updateName(settings.name);
            gui.open(player);
        });
    }

    private int adjustValue(int current, boolean increase, int step, int min, int max) {
        return increase ? Math.min(current + step, max) : Math.max(current - step, min);
    }

    private void updateSimpleLore(ItemStack item, String firstLine, String secondLine) {
        if (item != null && item.hasItemMeta()) {
            ItemMeta meta = item.getItemMeta();
            if (meta != null) {
                List<String> currentLore = meta.getLore();
                if (currentLore != null && !currentLore.isEmpty()) {
                    meta.setLore(List.of((String)currentLore.get(0), String.valueOf(ChatColor.GRAY) + firstLine, secondLine));
                } else {
                    meta.setLore(List.of(String.valueOf(ChatColor.GRAY) + firstLine, secondLine));
                }

                item.setItemMeta(meta);
            }
        }

    }

    private void updateScaleLore(ItemStack item, float scale) {
        if (item != null && item.hasItemMeta()) {
            ItemMeta meta = item.getItemMeta();
            if (meta != null) {
                meta.setLore(List.of(String.valueOf(ChatColor.DARK_GRAY) + "Size of the hologram", String.valueOf(ChatColor.GRAY) + "Current Scale:", String.valueOf(ChatColor.YELLOW) + String.format("%.2f", scale), "", String.valueOf(ChatColor.GREEN) + "Left Click: +0.01", String.valueOf(ChatColor.RED) + "Right Click: -0.01", String.valueOf(ChatColor.AQUA) + "Shift + Left: +0.1", String.valueOf(ChatColor.LIGHT_PURPLE) + "Shift + Right: -0.1"));
                item.setItemMeta(meta);
            }
        }

    }

    private void updateOpacityLore(ItemStack item, int opacity, String title) {
        if (item != null && item.hasItemMeta()) {
            ItemMeta meta = item.getItemMeta();
            if (meta != null) {
                int percentage = (int)((double)opacity / 255.0D * 100.0D);
                meta.setLore(List.of(String.valueOf(ChatColor.DARK_GRAY) + "Text transparency", String.valueOf(ChatColor.GRAY) + "Current Opacity:", String.valueOf(ChatColor.YELLOW) + opacity + " (" + percentage + "%)", "", String.valueOf(ChatColor.GREEN) + "Left Click: +1", String.valueOf(ChatColor.RED) + "Right Click: -1", String.valueOf(ChatColor.AQUA) + "Shift + Left: +10", String.valueOf(ChatColor.LIGHT_PURPLE) + "Shift + Right: -10"));
                item.setItemMeta(meta);
            }
        }

    }

    private void updateViewDistanceLore(ItemStack item, int distance) {
        if (item != null && item.hasItemMeta()) {
            ItemMeta meta = item.getItemMeta();
            if (meta != null) {
                meta.setLore(List.of(String.valueOf(ChatColor.DARK_GRAY) + "Maximum view distance", String.valueOf(ChatColor.GRAY) + "Current Distance:", String.valueOf(ChatColor.YELLOW) + distance + " blocks", "", String.valueOf(ChatColor.GREEN) + "Left Click: +1", String.valueOf(ChatColor.RED) + "Right Click: -1", String.valueOf(ChatColor.AQUA) + "Shift + Left: +16", String.valueOf(ChatColor.LIGHT_PURPLE) + "Shift + Right: -16"));
                item.setItemMeta(meta);
            }
        }

    }

    private void updateRotationLore(ItemStack item, CreationGUIListener.HologramSettings settings) {
        if (item != null && item.hasItemMeta()) {
            ItemMeta meta = item.getItemMeta();
            if (meta != null) {
                String direction = this.getDirectionFromYaw(settings.yaw);
                meta.setLore(List.of(String.valueOf(ChatColor.DARK_GRAY) + "Rotation angles", String.valueOf(ChatColor.GRAY) + "Current Yaw: " + String.valueOf(ChatColor.YELLOW) + String.format("%.1f° (%s)", settings.yaw, direction), String.valueOf(ChatColor.GRAY) + "Current Pitch: " + String.valueOf(ChatColor.YELLOW) + String.format("%.1f°", settings.pitch), "", String.valueOf(ChatColor.GREEN) + "Left Click: +1° Yaw", String.valueOf(ChatColor.RED) + "Right Click: +1° Pitch", String.valueOf(ChatColor.AQUA) + "Shift + Left: +45° Yaw", String.valueOf(ChatColor.LIGHT_PURPLE) + "Shift + Right: +15° Pitch"));
                item.setItemMeta(meta);
            }
        }

    }

    private void updateBackgroundLore(ItemStack item, CreationGUIListener.HologramSettings settings) {
        if (item != null && item.hasItemMeta()) {
            ItemMeta meta = item.getItemMeta();
            if (meta != null) {
                int percentage = (int)((double)settings.backgroundOpacity / 255.0D * 100.0D);
                meta.setLore(List.of(String.valueOf(ChatColor.DARK_GRAY) + "Background color and opacity", String.valueOf(ChatColor.GRAY) + "Current Color: " + String.valueOf(ChatColor.YELLOW) + String.format("0x%08X", settings.backgroundColor), String.valueOf(ChatColor.GRAY) + "Current Opacity: " + String.valueOf(ChatColor.YELLOW) + settings.backgroundOpacity + " (" + percentage + "%)", "", String.valueOf(ChatColor.GREEN) + "Left Click: +1 Opacity", String.valueOf(ChatColor.RED) + "Right Click: -1 Opacity", String.valueOf(ChatColor.AQUA) + "Shift + Left: +10 Opacity", String.valueOf(ChatColor.LIGHT_PURPLE) + "Shift + Right: -10 Opacity"));
                item.setItemMeta(meta);
            }
        }

    }

    private void updateNameLore(ItemStack item, String name) {
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

    private static class HologramSettings {
        Billboard billboard;
        boolean shadowed;
        boolean seeThrough;
        boolean persistence;
        String text;
        String name;
        float scale;
        float yaw;
        float pitch;
        TextAlignment textAlignment;
        int textOpacity;
        int backgroundColor;
        int backgroundOpacity;
        int viewDistance;

        private HologramSettings() {
            this.billboard = Billboard.CENTER;
            this.shadowed = false;
            this.seeThrough = false;
            this.persistence = true;
            this.text = "Hello vro";
            this.name = "Nameless";
            this.scale = 1.0F;
            this.yaw = 0.0F;
            this.pitch = 0.0F;
            this.textAlignment = TextAlignment.CENTER;
            this.textOpacity = 255;
            this.backgroundColor = 1073741824;
            this.backgroundOpacity = 64;
            this.viewDistance = 64;
        }
    }
}
