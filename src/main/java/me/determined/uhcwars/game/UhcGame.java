package me.determined.uhcwars.game;

import me.determined.uhcwars.UhcWars;
import org.bukkit.*;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Created by grai on 2017-03-30.
 */

public class UhcGame {

    private int radiusX = 500;
    private Collection<? extends Player> players;
    private ArrayList<Player> alivePlayers = new ArrayList<>();
    private boolean isDone;
    private String world = "game";
    private Long startTime;
    private ArrayList<BukkitTask> tasks = new ArrayList<>();

    public UhcGame(Collection<? extends Player> players){
        this.players = players;
        alivePlayers.addAll(players);
        this.isDone = false;
    }

    public boolean isDone() {
        return isDone;
    }

    public void end() {
        tasks.forEach((t) -> t.cancel());
        isDone = true;
    }

    public Collection<? extends Player> getPlayers() {
        return players;
    }

    public Collection<? extends Player> getAlivePlayers() {
        return alivePlayers;
    }

    public synchronized void removePlayer(Player p){
        if(getAlivePlayers().contains(p))
            getAlivePlayers().remove(p);
    }

    public int getRadiusX() {
        return radiusX;
    }

    public void spawnDrop(){
        Location loc = getRandomLocation();
        Bukkit.broadcastMessage("There is a drop at: x: " + loc.getBlockX() + ", z: " + loc.getBlockZ());
        loc.getWorld().dropItem(loc, UhcWars.main.getItemRandom().getItemStack());
    }

    public void start() {
        if(getWorld() == null){
            new WorldCreator("game").createWorld();
        }
        startTime = System.currentTimeMillis();
        getWorld().getWorldBorder().setCenter(0, 0);
        getWorld().getWorldBorder().setSize(radiusX*2);
        randomizePlayers();
        getWorld().setTime(600L);
        Bukkit.broadcastMessage(ChatColor.AQUA + "Game has started!\nYou have 5 minutes before the border starts to close in!");
        tasks.add(new BukkitRunnable(){
            @Override
            public void run(){
                Bukkit.broadcastMessage(ChatColor.RED + "Border is now closing in 250 blocks!");
                getWorld().getWorldBorder().setSize(radiusX, 60 * 5);
                tasks.add(new BukkitRunnable(){
                    @Override
                    public void run(){
                        Bukkit.broadcastMessage(ChatColor.AQUA + "You have 5 minutes before the border starts to close in!");
                        tasks.add(new BukkitRunnable(){
                            @Override
                            public void run(){
                                Bukkit.broadcastMessage(ChatColor.RED + "Border is now closing in 150 blocks!");
                                getWorld().getWorldBorder().setSize(200, 60 * 5);
                            }
                        }.runTaskLater(UhcWars.main, 300 * 20));
                    }
                }.runTaskLater(UhcWars.main, 300 * 20));
            }
        }.runTaskLater(UhcWars.main, 20 * 300));

        //Spawn drops
        tasks.add(new BukkitRunnable(){
            @Override
            public void run() {
                spawnDrop();
            }
        }.runTaskTimer(UhcWars.main, 60 * 20, 45 * 20));

        tasks.add(new BukkitRunnable(){
            @Override
            public void run(){
                for(Player p : Bukkit.getOnlinePlayers()){
                    if(!isLocationInCuboid(p.getLocation()))
                        p.damage(1.5);
                }
          }
        }.runTaskTimer(UhcWars.main, 0, 40));
    }

    public World getWorld(){
        return Bukkit.getWorld(world);
    }

    public boolean isLocationInCuboid(Location location) {
        double size = getWorld().getWorldBorder().getSize() / 2 - 0.3d;
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
                setupPlayer(p);
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
                    setupPlayer(p);
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

    public void setupPlayer(final Player p){
        new BukkitRunnable(){
            @Override
            public void run() {
                p.getInventory().setArmorContents(new ItemStack[4]);
                p.getInventory().setContents(new ItemStack[27]);
                p.getInventory().addItem(new ItemStack(Material.COOKED_BEEF, 10));
                p.getInventory().addItem(new ItemStack(Material.LEATHER_BOOTS, 1));
                p.getInventory().addItem(new ItemStack(Material.LEATHER_LEGGINGS, 1));
                p.getInventory().addItem(new ItemStack(Material.LEATHER_CHESTPLATE, 1));
                p.getInventory().addItem(new ItemStack(Material.LEATHER_HELMET, 1));
                p.getInventory().addItem(new ItemStack(Material.WOOD_AXE, 1));
                p.getInventory().addItem(new ItemStack(Material.COAL, 4));
                p.giveExpLevels(5);
                p.setFoodLevel(20);
                p.setHealth(20);
                p.getInventory().addItem(new ItemStack(Material.GOLDEN_APPLE, 1));
            }
        }.runTaskLater(UhcWars.main, 3);
    }

    public double getSize(){
        return getWorld().getWorldBorder().getSize() / 2;
    }
}
