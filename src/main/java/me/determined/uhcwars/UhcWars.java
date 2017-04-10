package me.determined.uhcwars;

import me.determined.uhcwars.cmd.UhcCmd;
import me.determined.uhcwars.game.UhcGame;
import me.determined.uhcwars.listeners.PlayerListener;
import net.minecraft.server.v1_8_R3.BiomeBase;
import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.lang.reflect.Field;

public final class UhcWars extends JavaPlugin {

    private boolean canPlayersJoin = true;
    private UhcGame uhcGame;
    public static UhcWars main;

    @Override
    public void onEnable() {
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
            System.out.print("Oceans will be present");
        }
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


}
