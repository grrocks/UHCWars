package me.determined.uhcwars;

import me.determined.uhcwars.cmd.UhcCmd;
import me.determined.uhcwars.game.UhcGame;
import me.determined.uhcwars.listeners.PlayerListener;
import net.minecraft.server.v1_8_R3.BiomeBase;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.lang.reflect.Field;

public final class UhcWars extends JavaPlugin {

    private boolean canPlayersJoin = true;
    private UhcGame uhcGame;
    public static UhcWars main;

    @Override
    public void onEnable() {
        deleteWorlds();
        registerCommands();
        registerListeners();
        main = this;
        try {
            Field biomesField = BiomeBase.class.getDeclaredField("biomes");
            biomesField.setAccessible(true);

            if (biomesField.get(null) instanceof BiomeBase[]) {
                BiomeBase[] biomes = (BiomeBase[]) biomesField.get(null);
                biomes[BiomeBase.DEEP_OCEAN.id] = BiomeBase.PLAINS;
                biomes[BiomeBase.OCEAN.id] = BiomeBase.FOREST;

                biomesField.set(null, biomes);
            }
        } catch(NoSuchFieldException | IllegalAccessException e){
            e.printStackTrace();
            System.out.print("Oceans will be present");
        }
    }

    public void deleteWorlds(){
        deleteWorld("world");
        deleteWorld("world_nether");
        deleteWorld("world_the_end");
    }

    public boolean deleteWorld(String name) {
        File world = new File(name);
        World w = Bukkit.getWorld(name);
        Bukkit.unloadWorld(name, false);
        if (world.exists()) {
            File files[] = world.listFiles();
            for (int i = 0; i < files.length; i++) {
                if (files[i].isDirectory()) deleteDir(world);
                else files[i].delete();
            }
        }
        return world.delete();
    }

    public boolean deleteDir(File path) {
        if (path.exists()) {
            File files[] = path.listFiles();
            for (int i = 0; i < files.length; i++) {
                if (files[i].isDirectory()) {
                    deleteDir(files[i]);
                } else {
                    files[i].delete();
                }
            }
        }
        return (path.delete());
    }

    @Override
    public void onDisable() {

    }

    public void registerListeners(){
        PluginManager pm = Bukkit.getPluginManager();

        pm.registerEvents(new PlayerListener(this), this);
    }

    public void registerCommands(){
        getCommand("uhc").setExecutor(new UhcCmd(this));
    }

    public boolean isCanPlayersJoin() {
        return canPlayersJoin;
    }

    public UhcGame getUhcGame() {
        return uhcGame;
    }

    public void setUhcGame(UhcGame uhcGame) {
        this.uhcGame = uhcGame;
    }

    public void setCanPlayersJoin(boolean canPlayersJoin) {
        this.canPlayersJoin = canPlayersJoin;
    }

    public void restartGame(){
        World world = Bukkit.getWorld("lobby");
        if(world == null)
            return;
        Location loc = new Location(world, 0, 62, 0);

    }

}
