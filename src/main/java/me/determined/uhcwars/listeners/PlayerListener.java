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
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.*;
import org.bukkit.scheduler.BukkitRunnable;
import org.spigotmc.event.player.PlayerSpawnLocationEvent;

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
        new BukkitRunnable(){
            @Override
            public void run(){
                World world = Bukkit.getWorld("world");
                if(world == null){
                    Bukkit.broadcastMessage("null");
                    return;
                }
                Location loc = new Location(world, 0, 62, 0);
                e.getPlayer().teleport(loc);
            }
        }.runTaskLater(main, 7);
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent e){
        if(main.getUhcGame() == null || main.getUhcGame().isDone())
            return;
        if(main.getUhcGame().getAlivePlayers().contains(e.getEntity())){
            main.getUhcGame().removePlayer(e.getEntity());
            shouldFinish();
        }
        e.getEntity().kickPlayer(ChatColor.RED + "Thanks for playing! Better luck next time.");
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent e){
        if(main.getUhcGame() == null || main.getUhcGame().isDone())
            return;
        new BukkitRunnable(){
            @Override
            public void run(){
                if(main.getUhcGame().getAlivePlayers().contains(e.getPlayer())){
                    main.getUhcGame().removePlayer(e.getPlayer());
                    shouldFinish();
                }
            }
        }.runTaskLater(main, 120);
    }

    public boolean shouldFinish(){

        switch(main.getUhcGame().getAlivePlayers().size()){
            case 0:
                clearPlayers();
                main.getUhcGame().end();
                main.restartGame();
                return true;
            case 1:
                clearPlayers();
                Player winner = ((Player) main.getUhcGame().getAlivePlayers().toArray()[0]);
                winner.sendMessage(ChatColor.GREEN + "You won!!!!!!! You beat "
                        + main.getUhcGame().getPlayers().size() + " players!");
                Bukkit.broadcastMessage(ChatColor.AQUA + winner.getName() + " has won the UHC game!");
                main.getUhcGame().end();
                main.restartGame();
                return true;
            default:
                return false;
        }
    }

    public boolean clearPlayers(){
        World world = Bukkit.getWorld("world");
        if(world == null)
            return false;
        Location loc = new Location(world, 0, 62, 0);
        for(Player p : Bukkit.getOnlinePlayers())
            p.teleport(loc);
        return true;
    }

    @EventHandler
    public void onBreakBlock(BlockPlaceEvent e){
        if(e.getBlock().getWorld().getName().equalsIgnoreCase("world"))
            if(!e.getPlayer().isOp()) e.setCancelled(true);
    }

    @EventHandler
    public void onPlaceBlock(BlockBreakEvent e){
        if(e.getBlock().getWorld().getName().equalsIgnoreCase("world"))
            if(!e.getPlayer().isOp()) e.setCancelled(true);
    }

    //Disallow portal use (then we dont have to worry about other worlds being accessed)
    @EventHandler
    public void onPlayerTeleport(PlayerTeleportEvent e){
        if(e.getCause() == PlayerTeleportEvent.TeleportCause.END_PORTAL){
            e.getPlayer().sendMessage(ChatColor.RED + "You cannot use an end portal!");
            e.setCancelled(true);
        }
        if(e.getCause() == PlayerTeleportEvent.TeleportCause.NETHER_PORTAL){
            e.getPlayer().sendMessage(ChatColor.RED + "You cannot use a nether portal!");
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerRespawn(PlayerRespawnEvent e){
        World world = Bukkit.getWorld("world");
        if(world == null){
            Bukkit.broadcastMessage("null");
            return;
        }
        Location loc = new Location(world, 0, 62, 0);
        e.setRespawnLocation(loc);
    }

    @EventHandler
    public void onMobSpawn(CreatureSpawnEvent e){
        if(e.getSpawnReason() != CreatureSpawnEvent.SpawnReason.SPAWNER)
            e.setCancelled(true);
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
