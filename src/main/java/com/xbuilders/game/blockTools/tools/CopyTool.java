package com.xbuilders.game.blockTools.tools;

import com.xbuilders.engine.gameScene.GameScene;
import com.xbuilders.engine.player.camera.CursorRay;
import com.xbuilders.engine.utils.ResourceUtils;
import com.xbuilders.engine.utils.math.AABB;
import com.xbuilders.engine.world.chunk.ChunkVoxels;
import com.xbuilders.game.blockTools.BlockTool;
import com.xbuilders.game.blockTools.BlockTools;
import org.lwjgl.glfw.GLFW;

import java.io.IOException;

public class CopyTool extends BlockTool {
    public CopyTool(BlockTools tools, CursorRay cursorRay) {
        super("Copy", tools, cursorRay);
        try {
            setIcon(ResourceUtils.resource("blockTools\\copy.png"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
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
                (int) (aabb.getXLength()),
                (int) (aabb.getYLength()),
                (int) (aabb.getZLength()));

        for (int x = (int) aabb.min.x; x < (int) aabb.max.x; x++) {
            for (int y = (int) aabb.min.y; y < (int) aabb.max.y; y++) {
                for (int z = (int) aabb.min.z; z < (int) aabb.max.z; z++) {
                    PasteTool.clipboard.setBlock(
                            (int) (x - aabb.min.x),
                            (int) (y - aabb.min.y),
                            (int) (z - aabb.min.z),
                            GameScene.world.getBlockID(x, y, z));
                    //Set block data
                    if (GameScene.world.getBlockData(x, y, z) != null) {
                        PasteTool.clipboard.setBlockData(
                                (int) (x - aabb.min.x),
                                (int) (y - aabb.min.y),
                                (int) (z - aabb.min.z),
                                GameScene.world.getBlockData(x, y, z));
                    }
                }
            }
        }
        PasteTool.updateMesh();
    }


    @Override
    public boolean keyEvent(int key, int scancode, int action, int mods) {
//        if (action == GLFW.GLFW_RELEASE) {
//            if (key == GLFW.GLFW_KEY_S) {
//                System.out.println("Saving clipboard");
//                GameScene.pauseGame();
//                PrefabUtils.savePrefabToFileDialog(PasteTool.clipboard);
//                return true;
//            } else if (key == GLFW.GLFW_KEY_L) {
//                System.out.println("Loading clipboard");
//                GameScene.pauseGame();
//                PrefabUtils.loadPrefabFromFileDialog((file) -> {
//                    if(file != null) {
//                        try {
//                            PasteTool.clipboard = PrefabUtils.loadPrefabFromFile(file);
//                        } catch (IOException e) {
//                            throw new RuntimeException(e);
//                        }
//                    }
//                });
//                return true;
//            }
//        }
        return false;
    }
}
