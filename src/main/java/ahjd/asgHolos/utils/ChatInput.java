package ahjd.asgHolos.utils;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitTask;

public class ChatInput implements Listener {
    private static final Map<UUID, Consumer<String>> awaitingInput = new HashMap();
    private static final Map<UUID, String> inputPrompts = new HashMap();
    private static final Map<UUID, BukkitTask> inputTimers = new HashMap();
    private static final Map<UUID, Runnable> timeoutCallbacks = new HashMap();
    private static final Map<UUID, Boolean> inputLocked = new HashMap();
    private static Plugin pluginInstance;
    private static final Pattern HEX_PATTERN = Pattern.compile("\\{#([A-Fa-f0-9]{6})\\}");
    private static final Pattern LEGACY_PATTERN = Pattern.compile("&([0-9a-fk-or])");

    public static void initialize(Plugin plugin) {
        pluginInstance = plugin;
    }

    public static void requestInput(Player player, String prompt, Consumer<String> callback, Runnable timeoutCallback, int timeoutSeconds) {
        UUID playerId = player.getUniqueId();
        if ((Boolean)inputLocked.getOrDefault(playerId, false)) {
            player.sendMessage(String.valueOf(ChatColor.RED) + "✗ Another input request is already active. Please wait or cancel it first.");
        } else {
            cancelInput(playerId);
            inputLocked.put(playerId, true);
            awaitingInput.put(playerId, callback);
            inputPrompts.put(playerId, prompt);
            timeoutCallbacks.put(playerId, timeoutCallback);
            BukkitTask timer = Bukkit.getScheduler().runTaskLater(pluginInstance, () -> {
                if (isNotAwaitingInput(playerId)) {
                    handleTimeout(playerId);
                }

            }, (long)timeoutSeconds * 20L);
            inputTimers.put(playerId, timer);
            player.closeInventory();
            player.sendMessage("");
            String var10001 = String.valueOf(ChatColor.GOLD);
            player.sendMessage(var10001 + "\ud83d\udcdd " + prompt);
            var10001 = String.valueOf(ChatColor.GRAY);
            player.sendMessage(var10001 + "Type in chat or 'cancel' to abort (" + timeoutSeconds + "s timeout)");
            player.sendMessage("");
        }
    }

    public static void requestInput(Player player, String prompt, Consumer<String> callback, Runnable timeoutCallback) {
        requestInput(player, prompt, callback, timeoutCallback, 60);
    }

    public static boolean isNotAwaitingInput(UUID playerId) {
        return awaitingInput.containsKey(playerId);
    }

    public static void cancelInput(UUID playerId) {
        BukkitTask timer = (BukkitTask)inputTimers.remove(playerId);
        if (timer != null) {
            timer.cancel();
        }

        inputLocked.remove(playerId);
        awaitingInput.remove(playerId);
        inputPrompts.remove(playerId);
        timeoutCallbacks.remove(playerId);
    }

    private static void handleTimeout(UUID playerId) {
        Player player = Bukkit.getPlayer(playerId);
        Runnable timeoutCallback = (Runnable)timeoutCallbacks.get(playerId);
        cancelInput(playerId);
        if (player != null && player.isOnline()) {
            player.sendMessage(String.valueOf(ChatColor.RED) + "⏰ Input timed out.");
        }

        if (timeoutCallback != null) {
            timeoutCallback.run();
        }

    }

    public static String getPrompt(UUID playerId) {
        return (String)inputPrompts.get(playerId);
    }

    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent e) {
        Player player = e.getPlayer();
        UUID playerId = player.getUniqueId();
        if (isNotAwaitingInput(playerId)) {
            e.setCancelled(true);
            String message = e.getMessage().trim();
            Consumer<String> callback = (Consumer)awaitingInput.get(playerId);
            if (message.equalsIgnoreCase("cancel")) {
                cancelInput(playerId);
                if (player.isOnline()) {
                    player.sendMessage(String.valueOf(ChatColor.RED) + "✗ Cancelled.");
                }

            } else if (message.isEmpty()) {
                player.sendMessage(String.valueOf(ChatColor.RED) + "✗ Empty input. Try again or 'cancel'.");
            } else if (message.length() > 100) {
                player.sendMessage(String.valueOf(ChatColor.RED) + "✗ Too long (max 100). Try again or 'cancel'.");
            } else {
                cancelInput(playerId);
                if (callback != null) {
                    Bukkit.getScheduler().runTask(pluginInstance, () -> {
                        callback.accept(message);
                    });
                }

            }
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent e) {
        UUID playerId = e.getPlayer().getUniqueId();
        cancelInput(playerId);
    }

    public static void cleanup() {
        Iterator var0 = inputTimers.values().iterator();

        while(var0.hasNext()) {
            BukkitTask timer = (BukkitTask)var0.next();
            if (timer != null) {
                timer.cancel();
            }
        }

        awaitingInput.clear();
        inputPrompts.clear();
        inputTimers.clear();
        timeoutCallbacks.clear();
    }

    public static String colorize(String text) {
        if (text == null) {
            return null;
        } else {
            String hexCode;
            for(Matcher hexMatcher = HEX_PATTERN.matcher(text); hexMatcher.find(); text = text.replace("{#" + hexCode + "}", net.md_5.bungee.api.ChatColor.of("#" + hexCode).toString())) {
                hexCode = hexMatcher.group(1);
            }

            Matcher legacyMatcher = LEGACY_PATTERN.matcher(text);
            text = legacyMatcher.replaceAll("§$1");
            return text;
        }
    }
}
