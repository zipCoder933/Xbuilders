package com.xbuilders.engine.player.data;

import com.xbuilders.engine.items.item.Item;
import com.xbuilders.engine.items.item.ItemStack;

import java.util.HashSet;

public class PlayerStuff {
    public final ItemStack[] items;

    public PlayerStuff(int size) {
        items = new ItemStack[size];
    }

    public void freeplay_getItem(Item item) {
        for (int i = 0; i < items.length; i++) {
            if (items[i] == null) {
                items[i] = new ItemStack(item);
                return;
            }
        }
        items[0] = new ItemStack(item);
    }
    
    public ItemStack get(int index) {
        return items[index];
    }
    
    public void set(int index, ItemStack item) {
        items[index] = item;
    }
    
    public int size() {
        return items.length;
    }

    public void organize() {
        HashSet<ItemStack> newBackpack = new HashSet();
        for (int i = 0; i < items.length; i++) {
            if (items[i] != null) {
                newBackpack.add(items[i]);
            }
            items[i] = null;
        }
        int index = 0;
        for (ItemStack item : newBackpack) {
            items[index] = item;
            index++;
        }
    }

}