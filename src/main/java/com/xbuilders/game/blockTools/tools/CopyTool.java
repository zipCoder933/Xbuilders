package com.xbuilders.game.blockTools.tools;

import com.xbuilders.engine.utils.math.AABB;
import com.xbuilders.game.blockTools.BlockTool;
import org.lwjgl.glfw.GLFW;

public class CopyTool extends BlockTool {
    public CopyTool() {
        super("Copy");
    }

    @Override
    public boolean shouldActivate(int key, int scancode, int action, int mods) {
        //Only activate with Ctrl+C
        if (key == GLFW.GLFW_KEY_C && (mods & GLFW.GLFW_MOD_CONTROL) != 0) {
            return true;
        }
        return false;
    }

    @Override
    public void blockBoundarySetEvent(AABB aabb, boolean created) {
//        if (Main.game.getSelectedItem() == null || Main.game.getSelectedItem().getType() != ItemType.BLOCK) return;
//        Block block = (Block) Main.game.getSelectedItem();
//        if (!created) block = BlockList.BLOCK_AIR;
//
//        for (int x = (int) aabb.min.x; x < (int) aabb.max.x; x++) {
//            for (int y = (int) aabb.min.y; y < (int) aabb.max.y; y++) {
//                for (int z = (int) aabb.min.z; z < (int) aabb.max.z; z++) {
//                    GameScene.player.setBlock(block.id, x, y, z);
//                }
//            }
//        }
    }

    @Override
    public boolean keyEvent(int key, int scancode, int action, int mods) {
        return false;
    }
}
