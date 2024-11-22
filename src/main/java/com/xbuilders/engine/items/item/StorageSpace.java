package com.xbuilders.engine.items.item;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class StorageSpace {
    public final ItemStack[] items;

    public StorageSpace(int size) {
        items = new ItemStack[size];
    }

    public int acquireItem(ItemStack stack) {
        deleteEmptyItems();
        for (int i = 0; i < items.length; i++) {
            if (items[i] != null && items[i].item.equals(stack.item)
                    && items[i].stackSize + stack.stackSize <= items[i].item.maxStackSize) {
                items[i].stackSize += stack.stackSize;
                return i;
            }
        }
        for (int i = 0; i < items.length; i++) {
            if (items[i] == null) {
                items[i] = stack;
                return i;
            }
        }
        return -1;
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
        sortItems();
        boolean hasMerged = true;
        while (hasMerged) {
            hasMerged = false;
            for (int i = 0; i < items.length - 1; i++) {
                if (items[i] != null && items[i + 1] != null && items[i].item.equals(items[i + 1].item)) {
                    items[i].stackSize += items[i + 1].stackSize;


                    if (items[i].stackSize > items[i].item.maxStackSize) {
                        // Handle overflow by keeping the excess in the next slot
                        items[i + 1].stackSize = items[i].stackSize - items[i].item.maxStackSize;
                        items[i].stackSize = items[i].item.maxStackSize;
                    } else {
                        items[i + 1] = null; // Fully combined, clear the next slot
                        hasMerged = true; // Mark as combined so we continue looping
                    }
                }
            }
            sortItems(); // Bring non-null items to the beginning
        }

        deleteEmptyItems(); // Clean up any leftover empty spaces
    }

    // Move non-null items to the start of the array
    private void sortItems() {
        // Sort items based on the custom comparator
        Arrays.sort(items, (item1, item2) -> {
            if (item1 == null && item2 == null) return 0;
            if (item1 == null) return 1;
            if (item2 == null) return -1;
            return item1.item.equals(item2.item) ? 0 : (item1.item.hashCode() - item2.item.hashCode());
        });
    }


    public void clear() {
        for (int i = 0; i < size(); i++) {
            items[i] = null;
        }
    }

    public void deleteEmptyItems() {
        Set<ItemStack> seenItems = new HashSet<>();

        for (int i = 0; i < size(); i++) {
            ItemStack currentItem = get(i);

            if (currentItem != null) {
                // Remove empty or marked-for-destruction items
                if (currentItem.stackSize <= 0 || currentItem.destroy) {
                    set(i, null);
                    continue;
                }

                // Check for duplicate references (same object in memory)
                boolean isDuplicate = false;
                for (ItemStack item : seenItems) {
                    if (item == currentItem) { // Reference equality
                        isDuplicate = true;
                        break;
                    }
                }

                if (isDuplicate) {
                    set(i, null); // Remove duplicate reference
                } else {
                    seenItems.add(currentItem); // Track unique reference
                }
            }
        }
    }


}
