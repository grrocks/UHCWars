package me.determined.uhcwars.cmd;

import me.determined.uhcwars.UhcWars;
import me.determined.uhcwars.game.UhcGame;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

/**
 * Created by grai on 2017-03-31.
 */
public class UhcCmd implements CommandExecutor {

    private UhcWars main;

    public UhcCmd(UhcWars main){
        this.main = main;
    }

    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args){

        if(!sender.isOp()) {
            sender.sendMessage(getHelpMessage(sender));
            return false;
        }

        if (args.length == 1) {
           if(args[0].equalsIgnoreCase("start")) {
                if(main.getUhcGame() == null || main.getUhcGame().isDone()){

                    main.setUhcGame(new UhcGame(Bukkit.getOnlinePlayers()));
                    main.setCanPlayersJoin(false);
                    main.getUhcGame().start();
                    return false;
                }

           }
        }

        sender.sendMessage(getHelpMessage(sender));

        return false;
    }

    public String getHelpMessage(CommandSender sender) {
        StringBuilder msg = new StringBuilder();

        msg.append(ChatColor.translateAlternateColorCodes('&', "&7[&aUHC&7] ") + ChatColor.DARK_GRAY + "====== " + ChatColor.GREEN + "Help Menu" + ChatColor.DARK_GRAY + " ======\n");

        if (sender.isOp()) {
            msg.append(ChatColor.GREEN + "/uhc start - Start the UHC War" + "\n");
        } else return ChatColor.RED + "You don't have permission for admin commands";

        return msg.toString();
    }

}
