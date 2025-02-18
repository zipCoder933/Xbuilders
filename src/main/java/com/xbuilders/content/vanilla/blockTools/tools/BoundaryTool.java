package com.xbuilders.content.vanilla.blockTools.tools;

import com.xbuilders.engine.client.visuals.gameScene.GameScene;
import com.xbuilders.engine.server.Server;
import com.xbuilders.engine.server.block.BlockRegistry;
import com.xbuilders.engine.server.block.Block;
import com.xbuilders.engine.client.player.raycasting.CursorRay;
import com.xbuilders.engine.utils.ResourceUtils;
import com.xbuilders.engine.utils.math.AABB;
import com.xbuilders.engine.server.world.chunk.Chunk;
import com.xbuilders.engine.server.world.wcc.WCCi;
import com.xbuilders.content.vanilla.blockTools.BlockTool;
import com.xbuilders.content.vanilla.blockTools.BlockTools;
import org.lwjgl.glfw.GLFW;

import java.io.IOException;
import java.util.HashSet;

public class BoundaryTool extends BlockTool {
    public BoundaryTool(BlockTools tools, CursorRay cursorRay) {
        super("Boundary", tools, cursorRay);
        try {
            setIcon(ResourceUtils.file("blockTools\\boundary.png"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


//
//    public String toolDescription() {
//        return name + (GameScene.player.camera.cursorRay.boundary_lockToPlane ? " Plane" : "");
//    }

    @Override
    public boolean activationKey(int key, int scancode, int action, int mods) {
        if (key == GLFW.GLFW_KEY_2) return true;
        return false;
    }

    @Override
    public void activate() {
        GameScene.userPlayer.camera.cursorRay.enableBoundaryMode((aabb, created) -> {
            blockBoundarySetEvent(aabb, created);
        });
        GameScene.userPlayer.camera.cursorRay.boundary_lockToPlane = false;
    }


    private void blockBoundarySetEvent(AABB aabb, boolean created) {
        if (getSelectedBlock() == null) return;

        Block block = BlockRegistry.BLOCK_AIR;
        if (created) {
            block = getSelectedBlock();
        }

        HashSet<Chunk> foundChunks = new HashSet<Chunk>();
        WCCi wcc = new WCCi();

        for (int x = (int) aabb.min.x; x < (int) aabb.max.x; x++) {
            for (int y = (int) aabb.min.y; y < (int) aabb.max.y; y++) {
                for (int z = (int) aabb.min.z; z < (int) aabb.max.z; z++) {
                    Server.setBlock(block.id, x, y, z);
                    foundChunks.add(wcc.set(x, y, z).getChunk(Server.world));
                }
            }
        }
        //Deleate all entities within aabb
        //We should delete entities within boundary wether we are creating or not
//        for (Chunk chunk : foundChunks) { //TODO: Fix this
//            for (Entity entity : chunk.entities.list) {
//                if (entity.worldPosition.x >= aabb.min.x && entity.worldPosition.x <= aabb.max.x
//                        && entity.worldPosition.y >= aabb.min.y && entity.worldPosition.y <= aabb.max.y
//                        && entity.worldPosition.z >= aabb.min.z && entity.worldPosition.z <= aabb.max.z) {
//                    entity.destroy();
//                }
//            }
//        }
    }

    @Override
    public boolean keyEvent(int key, int scancode, int action, int mods) {
        return false;
    }
}
