package com.xbuilders.game.items.tools;

import com.xbuilders.engine.items.Tool;

public class Hoe extends Tool {

    public Hoe() {
        super(3, "Hoe");
        setIcon("hoe.png");
    }

//    @Override
//    public boolean onPlace(int x, int y, int z) {
//        if (VoxelGame.getWorld().getBlock(x, y + 1, z) == GameItems.BLOCK_DIRT) {
//            GameItems.BLOCK_FARMLAND.set(x, y + 1, z);
//            return true;
//        }
//        return false;
//    }
}