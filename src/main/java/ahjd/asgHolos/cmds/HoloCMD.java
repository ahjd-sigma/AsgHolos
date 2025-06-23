package ahjd.asgHolos.cmds;

import ahjd.asgHolos.data.HologramManager;
import ahjd.asgHolos.data.SaveFile;
import ahjd.asgHolos.guis.CreationGUI;
import ahjd.asgHolos.guis.ListGUI;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class HoloCMD implements CommandExecutor {

    private final HologramManager hologramManager;
    private final SaveFile saveFile;

    public HoloCMD(HologramManager hologramManager, SaveFile saveFile) {
        this.hologramManager = hologramManager;
        this.saveFile = saveFile;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (!(sender instanceof Player player)) {
            sender.sendMessage(ChatColor.RED + "Only players can use this command.");
            return true;
        }
        String slabel = ChatColor.DARK_GRAY + "[" + ChatColor.YELLOW + "🔥 " + ChatColor.GOLD + "Sigma" + ChatColor.DARK_GRAY + "] ";

        if (args.length == 0) {
            player.sendMessage(slabel + ChatColor.GOLD + "=== " + ChatColor.BOLD + "AsgHolos Commands" + ChatColor.GOLD + " ===");
            player.sendMessage(slabel + ChatColor.YELLOW + "/hologram create" + ChatColor.GRAY + " - Open hologram creation GUI");
            player.sendMessage(slabel + ChatColor.YELLOW + "/hologram list" + ChatColor.GRAY + " - Open the hologram list GUI");
            player.sendMessage(slabel + ChatColor.YELLOW + "/hologram help" + ChatColor.GRAY + " - Show this help message");
            return true;
        }

        switch (args[0].toLowerCase()) {

            case "list" ->{
                ListGUI listGui = new ListGUI(saveFile, 0);
                listGui.open(player);
                player.sendMessage(slabel + ChatColor.GREEN + "✓ Opening the hologram list...");
        }

            case "create" -> {
                new CreationGUI().open(player);
                player.sendMessage(slabel + ChatColor.GREEN + "✓ Opening hologram creator...");
            }

            case "help" -> {
                player.sendMessage(slabel + ChatColor.GOLD + "=== " + ChatColor.BOLD + "AsgHolos Commands" + ChatColor.GOLD + " ===");
                player.sendMessage(slabel + ChatColor.YELLOW + "/hologram create" + ChatColor.GRAY + " - Open hologram creation GUI");
                player.sendMessage(slabel + ChatColor.YELLOW + "/hologram list" + ChatColor.GRAY + " - Open the hologram list GUI");
                player.sendMessage(slabel + ChatColor.YELLOW + "/hologram help" + ChatColor.GRAY + " - Show this help message");
            }
            default ->
                player.sendMessage(slabel + ChatColor.RED + "Unknown command. Use /hologram help for assistance.");

        }

        return true;
    }
}