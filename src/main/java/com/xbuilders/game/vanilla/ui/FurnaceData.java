package com.xbuilders.game.vanilla.ui;

import com.xbuilders.engine.items.item.ItemStack;
import com.xbuilders.engine.items.item.StorageSpace;

public class FurnaceData {
    public ItemStack inputGrid, fuelGrid, outputGrid;
    public long lastSmeltTime = 0;
    public float fuel = 0;


    public FurnaceData() {
    }

    public String toString() {
        return "inputGrid: " + inputGrid + " fuelGrid: " + fuelGrid + " outputGrid: " + outputGrid + " lastSmeltTime: " + lastSmeltTime;
    }

}
