package ahjd.asgHolos.cmds;

import ahjd.asgHolos.data.Config;
import ahjd.asgHolos.data.HologramManager;
import ahjd.asgHolos.data.SaveFile;
import ahjd.asgHolos.guis.CreationGUI;
import ahjd.asgHolos.guis.ListGUI;
import ahjd.asgHolos.utils.ChatInput;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class HoloCMD implements CommandExecutor {
    private final HologramManager hologramManager;
    private final SaveFile saveFile;
    private final Config config;

    public HoloCMD(HologramManager hologramManager, SaveFile saveFile, Config config) {
        this.hologramManager = hologramManager;
        this.saveFile = saveFile;
        this.config = config;
    }

    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player)sender;
            String var10000 = String.valueOf(ChatColor.DARK_GRAY);
            String slabel = var10000 + "[" + String.valueOf(ChatColor.YELLOW) + "\ud83d\udd25 " + String.valueOf(ChatColor.GOLD) + "Sigma" + String.valueOf(ChatColor.DARK_GRAY) + "] ";
            if (args.length == 0) {
                player.sendMessage(slabel + String.valueOf(ChatColor.GOLD) + "=== " + String.valueOf(ChatColor.BOLD) + "AsgHolos Commands" + String.valueOf(ChatColor.GOLD) + " ===");
                player.sendMessage(slabel + String.valueOf(ChatColor.YELLOW) + "/hologram create" + String.valueOf(ChatColor.GRAY) + " - Open hologram creation GUI");
                player.sendMessage(slabel + String.valueOf(ChatColor.YELLOW) + "/hologram list" + String.valueOf(ChatColor.GRAY) + " - Open the hologram list GUI");
                player.sendMessage(slabel + String.valueOf(ChatColor.YELLOW) + "/hologram help" + String.valueOf(ChatColor.GRAY) + " - Show this help message");
                return true;
            } else {
                String var7 = args[0].toLowerCase();
                byte var8 = -1;
                switch(var7.hashCode()) {
                    case -1352294148:
                        if (var7.equals("create")) {
                            var8 = 1;
                        }
                        break;
                    case 3198785:
                        if (var7.equals("help")) {
                            var8 = 2;
                        }
                        break;
                    case 3322014:
                        if (var7.equals("list")) {
                            var8 = 0;
                        }
                        break;
                    case 102976443:
                        if (var7.equals("limit")) {
                            var8 = 3;
                        }
                }

                switch(var8) {
                    case 0:
                        ListGUI listGui = new ListGUI(this.saveFile, 0);
                        listGui.open(player);
                        player.sendMessage(slabel + String.valueOf(ChatColor.GREEN) + "✓ Opening the hologram list...");
                        break;
                    case 1:
                        ChatInput.cancelInput(player.getUniqueId());
                        (new CreationGUI()).open(player);
                        player.sendMessage(slabel + String.valueOf(ChatColor.GREEN) + "✓ Opening hologram creator...");
                        break;
                    case 2:
                        player.sendMessage(slabel + String.valueOf(ChatColor.GOLD) + "=== " + String.valueOf(ChatColor.BOLD) + "AsgHolos Commands" + String.valueOf(ChatColor.GOLD) + " ===");
                        player.sendMessage(slabel + String.valueOf(ChatColor.YELLOW) + "/hologram create" + String.valueOf(ChatColor.GRAY) + " - Open hologram creation GUI");
                        player.sendMessage(slabel + String.valueOf(ChatColor.YELLOW) + "/hologram list" + String.valueOf(ChatColor.GRAY) + " - Open the hologram list GUI");
                        player.sendMessage(slabel + String.valueOf(ChatColor.YELLOW) + "/hologram limit" + String.valueOf(ChatColor.GRAY) + " - Show current hologram limit status");
                        player.sendMessage(slabel + String.valueOf(ChatColor.YELLOW) + "/hologram help" + String.valueOf(ChatColor.GRAY) + " - Show this help message");
                        break;
                    case 3:
                        int currentHolograms = this.hologramManager.getActiveHolograms();
                        Integer maxHolograms = this.config.getMaxHolograms();
                        String limitStatus = maxHolograms == null ? "Unlimited" : maxHolograms + " holograms";
                        player.sendMessage(slabel + String.valueOf(ChatColor.YELLOW) + "Current holograms: " + currentHolograms);
                        player.sendMessage(slabel + String.valueOf(ChatColor.YELLOW) + "Maximum allowed: " + limitStatus);
                        break;
                    default:
                        player.sendMessage(slabel + String.valueOf(ChatColor.RED) + "Unknown command. Use /hologram help for assistance.");
                }

                return true;
            }
        } else {
            sender.sendMessage(String.valueOf(ChatColor.RED) + "Only players can use this command.");
            return true;
        }
    }
}
