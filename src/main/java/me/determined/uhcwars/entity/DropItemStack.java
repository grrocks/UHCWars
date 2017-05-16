package me.determined.uhcwars.entity;

import org.bukkit.inventory.ItemStack;

/**
 * Created by grai on 2017-05-16.
 */
public class DropItemStack {

    private int random;
    private ItemStack is;

    public DropItemStack(ItemStack is, int random){
        this.random = random;
        this.is = is;
    }

    public int getRandom() {
        return random;
    }

    public ItemStack getItemStack() {
        return is;
    }
}
