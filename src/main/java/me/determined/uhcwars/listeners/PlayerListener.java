package me.determined.uhcwars.listeners;

import me.determined.uhcwars.UhcWars;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;

import java.io.*;
import java.util.Iterator;

/**
 * Created by grai on 2017-03-30.
 */
public class PlayerListener implements Listener {

    private UhcWars main;

    public PlayerListener(UhcWars main){
        this.main = main;
    }

    @EventHandler
    public void onPlayerJoin(AsyncPlayerPreLoginEvent e){
        if(!main.isCanPlayersJoin() &! isOp(e.getUniqueId().toString()))
            e.disallow(AsyncPlayerPreLoginEvent.Result.KICK_OTHER, ChatColor.RED + "A game is already going on");
    }

    public boolean isOp(String uuid){
        BufferedReader reader;
        try {
            reader = new BufferedReader(new FileReader("ops.json"));
        } catch(FileNotFoundException e){
            System.out.println("No op file found!");
            return false;
        }
        StringBuilder lines = new StringBuilder();
        Iterator<String> iterator = reader.lines().iterator();

        while(iterator.hasNext())
            lines.append(" " + iterator.next());

        return lines.toString().contains(uuid);
    }

}
