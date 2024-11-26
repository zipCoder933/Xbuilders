package com.xbuilders.engine.items.item;

import com.xbuilders.engine.items.Registrys;
import com.xbuilders.engine.items.block.BlockRegistry;

import java.util.Arrays;

public class ItemStack {
    public static final byte MAX_STACK_SIZE = 64;

    public final Item item;
    public int stackSize;
    public float durability;//The amount of damage the item has left
    public byte[] nbtData;//The NBT data of the item
    protected boolean destroy = false;

    public void destroy() {
        this.destroy = true;
    }


    public ItemStack(String item) {
        this.item = Registrys.getItem(item); //We must be able to guarantee that the item is not null
        if (this.item == null) throw new NullPointerException("Item in ItemStack cannot be null");

        durability = this.item.maxDurability;
        this.stackSize = 1;

    }

    public ItemStack(String item, int stackSize) {

        this.item = Registrys.getItem(item);
        if (this.item == null) throw new NullPointerException("Item in ItemStack cannot be null");

        this.durability = this.item.maxDurability; //We set the durability to the max durability
        this.stackSize = Math.min(this.item.maxStackSize, stackSize);

    }

    public ItemStack(Item item) {
        this.item = item; //We must be able to guarantee that the item is not null
        if (this.item == null) throw new NullPointerException("Item in ItemStack cannot be null");

        durability = item.maxDurability;
        this.stackSize = 1;

    }

    public ItemStack(Item item, int stackSize) {
        this.item = item;
        if (this.item == null) throw new NullPointerException("Item in ItemStack cannot be null");

        this.durability = item.maxDurability; //We set the durability to the max durability
        this.stackSize = Math.min(item.maxStackSize, stackSize);

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
