package com.xbuilders.game.blockTools.tools;

import com.xbuilders.engine.gameScene.GameScene;
import com.xbuilders.engine.items.BlockList;
import com.xbuilders.engine.items.ItemType;
import com.xbuilders.engine.items.block.Block;
import com.xbuilders.engine.utils.math.AABB;
import com.xbuilders.game.Main;
import com.xbuilders.game.blockTools.BlockTool;
import org.lwjgl.glfw.GLFW;

public class Tool_BoundarySetDelete extends BlockTool {
    public Tool_BoundarySetDelete() {
        super("Block Boundary", new int[]{GLFW.GLFW_KEY_2});
        useBlockBoundary = true;
    }

    @Override
    public void blockBoundarySetEvent(AABB aabb, boolean created) {
        if (Main.game.getSelectedItem() == null || Main.game.getSelectedItem().getType() != ItemType.BLOCK) return;
        Block block = (Block) Main.game.getSelectedItem();
        if (!created) block = BlockList.BLOCK_AIR;

        for (int x = (int) aabb.min.x; x < (int) aabb.max.x; x++) {
            for (int y = (int) aabb.min.y; y < (int) aabb.max.y; y++) {
                for (int z = (int) aabb.min.z; z < (int) aabb.max.z; z++) {
                    GameScene.player.setBlock(x, y, z, block);
                }
            }
        }
    }

    @Override
    public boolean keyEvent(int key, int scancode, int action, int mods) {
        if (action == GLFW.GLFW_RELEASE) {
            if (key == GLFW.GLFW_KEY_K) {
                GameScene.player.camera.cursorRay.boundary_useHitPos = !GameScene.player.camera.cursorRay.boundary_useHitPos;
                return true;
            }
        }
        return false;
    }
}
