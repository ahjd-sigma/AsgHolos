package ahjd.asgHolos.cmds;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

public class TabCompletion implements TabCompleter {
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        List<String> completions = new ArrayList();
        if (args.length == 1) {
            List<String> options = List.of("create", "list");
            Iterator var8 = options.iterator();

            while(var8.hasNext()) {
                String option = (String)var8.next();
                if (option.toLowerCase().startsWith(args[0].toLowerCase())) {
                    completions.add(option);
                }
            }
        }

        return completions;
    }
}
