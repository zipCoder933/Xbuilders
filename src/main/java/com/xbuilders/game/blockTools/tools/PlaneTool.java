package com.xbuilders.game.blockTools.tools;

import com.xbuilders.engine.MainWindow;
import com.xbuilders.engine.gameScene.GameScene;
import com.xbuilders.engine.items.BlockList;
import com.xbuilders.engine.items.ItemType;
import com.xbuilders.engine.items.block.Block;
import com.xbuilders.engine.player.camera.CursorRay;
import com.xbuilders.engine.utils.ResourceUtils;
import com.xbuilders.engine.utils.math.AABB;
import com.xbuilders.game.blockTools.BlockTool;
import com.xbuilders.game.blockTools.BlockTools;
import org.lwjgl.glfw.GLFW;

import java.io.IOException;

public class PlaneTool extends BlockTool {
    public PlaneTool(BlockTools tools, CursorRay cursorRay) {
        super("Plane", tools, cursorRay);
        try {
            setIcon(ResourceUtils.resource("blockTools\\plane.png"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
//
//    public String toolDescription() {
//        return "Plane";
//    }

    @Override
    public boolean activationKey(int key, int scancode, int action, int mods) {
        if (key == GLFW.GLFW_KEY_3) return true;
        return false;
    }

    @Override
    public void activate() {
        GameScene.player.camera.cursorRay.enableBoundaryMode((aabb, created) -> {
            blockBoundarySetEvent(aabb, created);
        });
        GameScene.player.camera.cursorRay.boundary_lockToPlane = true;
    }


    private void blockBoundarySetEvent(AABB aabb, boolean created) {
        Block block = BlockList.BLOCK_AIR;
        if (created) {
            if (MainWindow.game.getSelectedItem() == null || MainWindow.game.getSelectedItem().getType() != ItemType.BLOCK) return;
            block = (Block) MainWindow.game.getSelectedItem();
        }

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
