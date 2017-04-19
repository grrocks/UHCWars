package me.determined.uhcwars.game;

import me.determined.uhcwars.UhcWars;
import org.bukkit.*;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Created by grai on 2017-03-30.
 */

public class UhcGame {

    private int radiusX = 500;
    private Collection<? extends Player> players;
    private Collection<? extends Player> alivePlayers;
    private boolean isDone;
    private String world = "World";
    private Long startTime;

    public UhcGame(Collection<? extends Player> players){
        this.players = players;
        this.alivePlayers = players;
        this.isDone = false;
    }

    public boolean isDone() {
        return isDone;
    }

    public void setDone(boolean done) {
        isDone = done;
    }

    public Collection<? extends Player> getPlayers() {
        return players;
    }

    public Collection<? extends Player> getAlivePlayers() {
        return alivePlayers;
    }

    public int getRadiusX() {
        return radiusX;
    }

    public void spawnChest(){

    }

    public void start() {
        startTime = System.currentTimeMillis();
        getWorld().getWorldBorder().setCenter(0, 0);
        getWorld().getWorldBorder().setSize(radiusX*2);
        randomizePlayers();
        Bukkit.broadcastMessage(ChatColor.AQUA + "Game has started!\nYou have 5 minutes before the border starts to close in!");
        new BukkitRunnable(){
            @Override
            public void run(){
                Bukkit.broadcastMessage(ChatColor.RED + "Border is now closing in 250 blocks!");
                getWorld().getWorldBorder().setSize(radiusX, 60 * 5);
                new BukkitRunnable(){
                    @Override
                    public void run(){
                        Bukkit.broadcastMessage(ChatColor.AQUA + "You have 5 minutes before the border starts to close in!");
                        new BukkitRunnable(){
                            @Override
                            public void run(){
                                Bukkit.broadcastMessage(ChatColor.RED + "Border is now closing in 150 blocks!");
                                getWorld().getWorldBorder().setSize(200, 60 * 5);
                            }
                        }.runTaskLater(UhcWars.main, 300 * 20);
                    }
                }.runTaskLater(UhcWars.main, 300 * 20);
            }
        }.runTaskLater(UhcWars.main, 20 * 300);

        new BukkitRunnable(){
            @Override
            public void run(){
                for(Player p : Bukkit.getOnlinePlayers()){
                    if(!isLocationInCuboid(p.getLocation()))
                        p.damage(1.5);
                }
          }
        }.runTaskTimer(UhcWars.main, 0, 40);
    }

    public World getWorld(){
        return Bukkit.getWorld(world);
    }

    public boolean isLocationInCuboid(Location location) {
        double size = getWorld().getWorldBorder().getSize() / 2 - 0.4d;
        Location location1 = new Location(getWorld(), -(size), 0, -(size));
        Location location2 = new Location(getWorld(), size, 258, size);
        boolean x = location.getX() > Math.min(location1.getX(), location2.getX()) && location.getX() < Math.max(location1.getX(), location2.getX());
        boolean y = location.getY() > Math.min(location1.getY(), location2.getY()) && location.getY() < Math.max(location1.getY(), location2.getY());
        boolean z = location.getZ() > Math.min(location1.getZ(), location2.getZ()) && location.getZ() < Math.max(location1.getZ(), location2.getZ());
        return x && y && z;
    }

    public void randomizePlayers(){
        ArrayList<Location> currentLocs = new ArrayList<>();

        for(Player p : players){
            if(currentLocs.size() == 0){
//                Location loc = null;
//                boolean goodLoc = true;
//                int failedTimes = 0;
//                do {
//                    loc = getRandomLocation();
//                    if (loc.getBlock().getRelative(BlockFace.DOWN).isLiquid()) {
//                        goodLoc = false;
//                        failedTimes++;
//                        break;
//                    }
//                } while(!goodLoc && (failedTimes < 200));
                Location loc = getRandomLocation();
                p.teleport(loc);
                currentLocs.add(loc);
            } else {
                Location loc = null;
                boolean goodLoc = true;
                int failedTimes = 0;
                do {
                    for (Location cLoc : currentLocs) {
                        loc = getRandomLocation();
                        if (cLoc.distanceSquared(loc) < 25) {
                            goodLoc = false;
                            failedTimes++;
                            break;
                        }
                        if(loc.getBlock().getRelative(BlockFace.DOWN).isLiquid()){
                            goodLoc = false;
                            failedTimes++;
                            break;
                        }
                        goodLoc = true;
                        cLoc.add(loc);
                    }
                } while(!goodLoc && (failedTimes < 200));

                if(loc != null) {
                    p.teleport(loc);
                    p.getInventory().addItem(new ItemStack(Material.COOKED_BEEF, 10));
                    p.getInventory().addItem(new ItemStack(Material.LEATHER_BOOTS, 1));
                    p.getInventory().addItem(new ItemStack(Material.LEATHER_LEGGINGS, 1));
                    p.getInventory().addItem(new ItemStack(Material.LEATHER_CHESTPLATE, 1));
                    p.getInventory().addItem(new ItemStack(Material.LEATHER_HELMET, 1));
                    p.getInventory().addItem(new ItemStack(Material.WOOD_AXE, 1));
                    //p.getInventory().addItem(new ItemStack(Material.GOLDEN_APPLE, 1));
                }
                else {
                    p.kickPlayer("There was not enough space for you on the map!");
                }

            }
        }

    }

    public Location getRandomLocation(){
        double x = Math.random() * (getSize() - 25) * (Math.random() > 0.5 ? -1 : 1);
        double z = Math.random() * (getSize() / 2 - 25) * (Math.random() > 0.5 ? -1 : 1);

        Location loc = new Location(Bukkit.getWorld(world), x, 0, z);

        loc.setY(loc.getWorld().getHighestBlockYAt(loc));
        return loc;
    }

    public double getSize(){
        return getWorld().getWorldBorder().getSize() / 2;
    }
}
