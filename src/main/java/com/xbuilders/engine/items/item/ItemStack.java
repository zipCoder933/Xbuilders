package com.xbuilders.engine.items.item;

import com.xbuilders.engine.items.block.BlockRegistry;

public class ItemStack {
    public static final byte MAX_STACK_SIZE = 64;

    public final Item item;
    public int stackSize;
    public int durability;//The amount of damage the item has left
    public byte[] nbtData;//The NBT data of the item

    public ItemStack(Item item) {
        this.item = item; //We must be able to guarantee that the item is not null
        if (this.item == null) throw new NullPointerException("Item in ItemStack cannot be null");
    }

    public ItemStack(Item item, int stackSize) {
        this.item = item;
        this.stackSize = (byte) Math.min(item.maxStackSize, stackSize);
    }
}
