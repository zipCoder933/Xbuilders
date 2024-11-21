package com.xbuilders.engine.items.item;

import com.xbuilders.engine.items.block.BlockRegistry;

import java.util.Arrays;

public class ItemStack {
    public static final byte MAX_STACK_SIZE = 64;

    public final Item item;
    public int stackSize;
    public float durability;//The amount of damage the item has left
    public byte[] nbtData;//The NBT data of the item

    public ItemStack(Item item) {
        this.item = item; //We must be able to guarantee that the item is not null
        durability = item.maxDurability;
        if (this.item == null) throw new NullPointerException("Item in ItemStack cannot be null");
    }

    public ItemStack(Item item, int stackSize) {
        this.item = item;
        this.durability = item.maxDurability; //We set the durability to the max durability
        this.stackSize = (byte) Math.min(item.maxStackSize, stackSize);
    }


    @Override
    public String toString() {
        return "ItemStack{" +
                "item=" + item +
                ", stackSize=" + stackSize +
                ", durability=" + durability +
                (nbtData == null ? "" : ", nbtData=" + Arrays.toString(nbtData)) +
                '}';
    }
}
