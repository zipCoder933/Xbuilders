package com.xbuilders.engine.items.item;

import com.xbuilders.engine.items.block.BlockRegistry;

public class ItemStack {
    public final Item item;
    public byte stackSize;
    public int durability;//The amount of damage the item has left
    public byte[] nbtData;//The NBT data of the item

    public ItemStack(Item item) {
        this.item = item;
    }

    public ItemStack(Item item, byte stackSize) {
        this.item = item;
        this.stackSize = stackSize;
    }
}
