package com.xbuilders.game.blockTools.tools;

import com.xbuilders.engine.gameScene.GameScene;
import com.xbuilders.engine.items.BlockList;
import com.xbuilders.engine.items.Entity;
import com.xbuilders.engine.items.ItemType;
import com.xbuilders.engine.items.block.Block;
import com.xbuilders.engine.player.camera.CursorRay;
import com.xbuilders.engine.utils.math.AABB;
import com.xbuilders.engine.world.chunk.Chunk;
import com.xbuilders.engine.world.wcc.WCCi;
import com.xbuilders.game.Main;
import com.xbuilders.game.blockTools.BlockTool;
import com.xbuilders.game.blockTools.BlockTools;
import org.lwjgl.glfw.GLFW;

import java.util.HashSet;

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

        Block block = BlockList.BLOCK_AIR;
        if (created){
            if (Main.game.getSelectedItem() == null || Main.game.getSelectedItem().getType() != ItemType.BLOCK) return;
            block = (Block) Main.game.getSelectedItem();
        }

        HashSet<Chunk> foundChunks = new HashSet<Chunk>();
        WCCi wcc = new WCCi();

        for (int x = (int) aabb.min.x; x < (int) aabb.max.x; x++) {
            for (int y = (int) aabb.min.y; y < (int) aabb.max.y; y++) {
                for (int z = (int) aabb.min.z; z < (int) aabb.max.z; z++) {
                    GameScene.player.setBlock(block.id, x, y, z);
                    foundChunks.add(wcc.set(x, y, z).getChunk(GameScene.world));
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
