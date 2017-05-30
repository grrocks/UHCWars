package me.determined.uhcwars;

import me.determined.uhcwars.cmd.UhcCmd;
import me.determined.uhcwars.entity.DropItemStack;
import me.determined.uhcwars.game.UhcGame;
import me.determined.uhcwars.listeners.PlayerListener;
import net.minecraft.server.v1_8_R3.BiomeBase;
import org.bukkit.*;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public final class UhcWars extends JavaPlugin {

    private boolean canPlayersJoin = true;
    private UhcGame uhcGame;
    public static UhcWars main;
    public ArrayList<DropItemStack> dropItemStacks = new ArrayList<>();

    @Override
    public void onEnable() {
        main = this;
        saveDefaultConfig();
        registerCommands();
        registerListeners();
        setLobbyWorld();
        try {
            Field biomesField = BiomeBase.class.getDeclaredField("biomes");
            biomesField.setAccessible(true);

            if (biomesField.get(null) instanceof BiomeBase[]) {
                BiomeBase[] biomes = (BiomeBase[]) biomesField.get(null);
                biomes[BiomeBase.DEEP_OCEAN.id] = BiomeBase.PLAINS;
                biomes[BiomeBase.OCEAN.id] = BiomeBase.FOREST;
                biomes[BiomeBase.DESERT.id] = BiomeBase.BIRCH_FOREST;

                biomesField.set(biomesField.get(null), biomes);

            }
        } catch (NoSuchFieldException | IllegalAccessException e) {
//            e.printStackTrace();
//            System.out.print("Oceans will be present");
        }
        ConfigurationSection config = getConfig().getConfigurationSection("loot");

        for (String path : config.getKeys(false)) {
            Material mat = Material.valueOf(config.getString(path + ".material").toUpperCase());
            List<String> lore = config.getBoolean(path + ".haveLore") ? config.getStringList(path + ".lore") : null;
            List<String> enchantments = config.getBoolean(path + ".haveEnchantments") ? config.getStringList(path + ".enchantments") : null;
            dropItemStacks.add(new DropItemStack(createItem(mat, config.getString(path + ".displayName"), lore, enchantments,
                    Integer.parseInt(config.getString(path + ".amount"))), config.getInt(path + ".weightedRandom")));
        }
    }


    public ItemStack createItem(Material mat, String name, List<String> lore, List<String> enchantments, int amount) {
        ItemStack is = new ItemStack(mat, amount);
        ItemMeta im = is.getItemMeta();

        im.setDisplayName(color(name));

        if (lore != null) {
            List<String> newLore = new ArrayList<String>();
            for (String s : lore) {
                newLore.add(color(s));
            }
            im.setLore(newLore);
        }

        if (enchantments != null) {
            for (String s : enchantments) {
                String[] split = s.split(",");
                int level = Integer.parseInt(split[1]);
                im.addEnchant(Enchantment.getByName(split[0].toUpperCase()), level, true);
            }
        }

        is.setItemMeta(im);

        return is;
    }

    public String color(String s) {
        return ChatColor.translateAlternateColorCodes('&', s);
    }

    public DropItemStack getItemRandom() {

        DropItemStack[] items = dropItemStacks.toArray(new DropItemStack[dropItemStacks.size()]);
        double totalWeight = 0.0d;

        for (DropItemStack i : items) {
            totalWeight += i.getRandom();
        }
        int randomIndex = -1;
        double random = Math.random() * totalWeight;
        for (int i = 0; i < items.length; ++i) {
            random -= items[i].getRandom();
            if (random <= 0.0d) {
                randomIndex = i;
                break;
            }
        }
        DropItemStack randomItem = items[randomIndex];
        return randomItem;
    }

    public void setLobbyWorld(){
        World world = Bukkit.getWorld("world");
        world.setSpawnLocation(0, 62, 0);
        world.setPVP(false);
        world.setDifficulty(Difficulty.PEACEFUL);
        world.setMonsterSpawnLimit(0);
    }

    public void deleteWorlds(){
        deleteWorld("game");
//        deleteWorld("world_nether");
//        deleteWorld("world_the_end");
    }

    public boolean deleteWorld(String name) {
        File world = new File(name);
        Bukkit.unloadWorld(name, true);
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
        World world = Bukkit.getWorld("world");
        Location loc = new Location(world, 0, 62, 0);
        for(Player p : Bukkit.getOnlinePlayers())
            p.teleport(loc);
        deleteWorlds();
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
        deleteWorlds();
        WorldCreator worldCreator = new WorldCreator("game");
////        WorldCreator worldCreator1 = new WorldCreator("world_nether");
////        WorldCreator worldCreator2 = new WorldCreator("world_the_end");
        worldCreator.createWorld();
//        worldCreator1.createWorld();
//        worldCreator2.createWorld();
    }



}
