package com.xbuilders.game.blockTools.tools;

import com.xbuilders.engine.gameScene.GameScene;
import com.xbuilders.engine.items.BlockList;
import com.xbuilders.engine.items.ItemType;
import com.xbuilders.engine.items.block.Block;
import com.xbuilders.engine.player.camera.CursorRay;
import com.xbuilders.engine.utils.math.AABB;
import com.xbuilders.game.Main;
import com.xbuilders.game.blockTools.BlockTool;
import com.xbuilders.game.blockTools.BlockTools;
import org.lwjgl.glfw.GLFW;

public class BoundaryTool extends BlockTool {
    public BoundaryTool(BlockTools tools, CursorRay cursorRay) {
        super("Boundary", tools, cursorRay);
    }
//
//    public String toolDescription() {
//        return name + (GameScene.player.camera.cursorRay.boundary_lockToPlane ? " Plane" : "");
//    }

    @Override
    public boolean shouldActivate(int key, int scancode, int action, int mods) {
        if (key == GLFW.GLFW_KEY_2) return true;
        return false;
    }

    @Override
    public void activate() {
        GameScene.player.camera.cursorRay.enableBoundaryMode((aabb, created) -> {
            blockBoundarySetEvent(aabb, created);
        });
        GameScene.player.camera.cursorRay.boundary_lockToPlane = false;
    }


    private void blockBoundarySetEvent(AABB aabb, boolean created) {
        if (Main.game.getSelectedItem() == null || Main.game.getSelectedItem().getType() != ItemType.BLOCK) return;
        Block block = (Block) Main.game.getSelectedItem();
        if (!created) block = BlockList.BLOCK_AIR;

        for (int x = (int) aabb.min.x; x < (int) aabb.max.x; x++) {
            for (int y = (int) aabb.min.y; y < (int) aabb.max.y; y++) {
                for (int z = (int) aabb.min.z; z < (int) aabb.max.z; z++) {
                    GameScene.player.setBlock(block.id, x, y, z);
                }
            }
        }
    }

    @Override
    public boolean keyEvent(int key, int scancode, int action, int mods) {
        return false;
    }
}
