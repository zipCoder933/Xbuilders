//package com.xbuilders.game.blockTools.tools;
//
//import com.xbuilders.engine.MainWindow;
//import com.xbuilders.engine.gameScene.GameScene;
//import com.xbuilders.engine.items.BlockList;
//import com.xbuilders.engine.items.ItemType;
//import com.xbuilders.engine.items.block.Block;
//import com.xbuilders.engine.player.CursorRay;
//import com.xbuilders.engine.utils.ResourceUtils;
//import com.xbuilders.engine.utils.math.AABB;
//import com.xbuilders.engine.world.World;
//import com.xbuilders.engine.world.chunk.Chunk;
//import com.xbuilders.engine.world.wcc.WCCi;
//import com.xbuilders.game.blockTools.BlockTool;
//import com.xbuilders.game.blockTools.BlockTools;
//import org.joml.Vector3i;
//import org.lwjgl.glfw.GLFW;
//
//import java.io.IOException;
//import java.util.HashSet;
//
//public class LightFixTool extends BlockTool {
//    public LightFixTool(BlockTools tools, CursorRay cursorRay) {
//        super("Light Fix Tool", tools, cursorRay);
//        try {
//            setIcon(ResourceUtils.resource("blockTools\\boundary.png"));
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }
//    }
//
//
//    @Override
//    public boolean activationKey(int key, int scancode, int action, int mods) {
//        if (key == GLFW.GLFW_KEY_8) return true;
//        return false;
//    }
//
//    @Override
//    public void activate() {
//        GameScene.player.camera.cursorRay.enableBoundaryMode((aabb, created) -> {
//            blockBoundarySetEvent(aabb, created);
//        });
//        GameScene.player.camera.cursorRay.boundary_lockToPlane = false;
//    }
//
//
//    private void blockBoundarySetEvent(AABB aabb, boolean created) {
//        System.out.println("Light Fix Tool");
//        HashSet<Chunk> foundChunks = new HashSet<Chunk>();
//        for (int x = (int) aabb.min.x; x < (int) aabb.max.x; x++) {
//            for (int z = (int) aabb.min.z; z < (int) aabb.max.z; z++) {
//                for (int y = (int) GameScene.world.WORLD_TOP_Y; y < (int) aabb.max.y; y++) {
//                    WCCi wcc = new WCCi();
//                    wcc.set(x, y, z);
//                    Chunk chunk = wcc.getChunk(GameScene.world);
//                    GameScene.world.getChunk(new Vector3i()World.TOP_Y_CHUNK)
//                    if (chunk == null) continue;
//                    foundChunks.add(chunk);
//                    chunk.data.setSun(wcc.chunkVoxel.x, wcc.chunkVoxel.y, wcc.chunkVoxel.z, 0);
//                }
//            }
//        }
//
//        for (Chunk chunk : foundChunks) {
//            chunk.generateMesh();
//        }
//    }
//
//    @Override
//    public boolean keyEvent(int key, int scancode, int action, int mods) {
//        return false;
//    }
//}
