package com.xbuilders.engine.player.data;

import com.xbuilders.engine.items.item.Item;
import com.xbuilders.engine.items.item.ItemStack;

import java.util.Arrays;

public class StorageSpace {
    public final ItemStack[] items;

    public StorageSpace(int size) {
        items = new ItemStack[size];
    }

    public int acquireItem(ItemStack stack) {
        for (int i = 0; i < items.length; i++) {
            if (items[i] == null) {
                items[i] = stack;
                return i;
            }
        }
        items[0] = stack;
        return 0;
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
        Arrays.sort(items, (item1, item2) -> {
            // Use equals() to compare the two ItemStack objects
            if (item1 == null && item2 == null) return 0;
            if (item1 == null) return 1;
            if (item2 == null) return -1;
            return item1.item.equals(item2) ? 0 : (item1.item.hashCode() - item2.item.hashCode());
        });
    }
}
