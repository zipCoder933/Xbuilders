package com.xbuilders.game.blockTools.tools;

import com.xbuilders.engine.gameScene.GameScene;
import com.xbuilders.engine.items.BlockList;
import com.xbuilders.engine.items.ItemType;
import com.xbuilders.engine.items.block.Block;
import com.xbuilders.engine.player.camera.CursorRay;
import com.xbuilders.engine.utils.math.AABB;
import com.xbuilders.engine.world.chunk.ChunkVoxels;
import com.xbuilders.game.Main;
import com.xbuilders.game.blockTools.BlockTool;
import com.xbuilders.game.blockTools.BlockTools;
import org.lwjgl.glfw.GLFW;

public class CopyTool extends BlockTool {
    public CopyTool(BlockTools tools, CursorRay cursorRay) {
        super("Copy", tools, cursorRay);
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
    public void activate() {
        cursorRay.enableBoundaryMode((aabb, aBoolean) -> blockBoundarySetEvent(aabb, aBoolean));
        cursorRay.boundary_lockToPlane = false;
    }


    private void blockBoundarySetEvent(AABB aabb, boolean created) {
        PasteTool.clipboard = new ChunkVoxels(
                (int) (aabb.getXLength() + 1),
                (int) (aabb.getYLength() + 1),
                (int) (aabb.getZLength() + 1));

        for (int x = (int) aabb.min.x; x < (int) aabb.max.x; x++) {
            for (int y = (int) aabb.min.y; y < (int) aabb.max.y; y++) {
                for (int z = (int) aabb.min.z; z < (int) aabb.max.z; z++) {
                    PasteTool.clipboard.setBlock(
                            (int) (x - aabb.min.x),
                            (int) (y - aabb.min.y),
                            (int) (z - aabb.min.z),
                            GameScene.world.getBlockID(x, y, z));
                }
            }
        }
        PasteTool.mesh.compute(PasteTool.clipboard);
        PasteTool.mesh.sendToGPU();
    }


    @Override
    public boolean keyEvent(int key, int scancode, int action, int mods) {
        return false;
    }
}
