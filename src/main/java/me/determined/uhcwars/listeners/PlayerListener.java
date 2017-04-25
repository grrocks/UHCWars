package me.determined.uhcwars.listeners;

import me.determined.uhcwars.UhcWars;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

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
    public void onPlayerPreJoin(AsyncPlayerPreLoginEvent e){
        if(!main.isCanPlayersJoin() &! isOp(e.getUniqueId().toString()))
            e.disallow(AsyncPlayerPreLoginEvent.Result.KICK_OTHER, ChatColor.RED + "A game is already going on");
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        World world = Bukkit.getWorld("lobby");
        if(world == null)
            return;
        Location loc = new Location(world, 0, 62, 0);
        e.getPlayer().teleport(loc);
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent e){
        if(main.getUhcGame() == null || main.getUhcGame().isDone())
            return;
        if(main.getUhcGame().getAlivePlayers().contains(e.getEntity())){
            main.getUhcGame().getAlivePlayers().remove(e.getEntity());
            shouldFinish();
        }
        e.getEntity().kickPlayer(ChatColor.RED + "Thanks for playing! Better luck next time.");
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent e){
        if(main.getUhcGame() == null || main.getUhcGame().isDone())
            return;
        if(main.getUhcGame().getAlivePlayers().contains(e.getPlayer())){
            main.getUhcGame().getAlivePlayers().remove(e.getPlayer());
            shouldFinish();
        }
    }

    public boolean shouldFinish(){

        switch(main.getUhcGame().getAlivePlayers().size()){
            case 0:
                if(clearPlayers())
            case 1:

                break;
            default:
                return false;
        }
    }

    public boolean clearPlayers(){
        World world = Bukkit.getWorld("world");
        if(world == null)
            return false;
        Location loc = new Location(world, 0, 62, 0);
        for(Player p : world.getPlayers())
            p.teleport(loc);
        return true;
    }

    @EventHandler
    public void onBreakBlock(BlockPlaceEvent e){
        if(e.getBlock().getWorld().getName().equalsIgnoreCase("lobby"))
            if(!e.getPlayer().isOp()) e.setCancelled(true);
    }

    @EventHandler
    public void onPlaceBlock(BlockBreakEvent e){
        if(e.getBlock().getWorld().getName().equalsIgnoreCase("lobby"))
            if(!e.getPlayer().isOp()) e.setCancelled(true);
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
