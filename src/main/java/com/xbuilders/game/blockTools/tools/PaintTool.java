package com.xbuilders.game.blockTools.tools;

import com.xbuilders.engine.gameScene.GameScene;
import com.xbuilders.engine.items.BlockList;
import com.xbuilders.engine.items.block.Block;
import com.xbuilders.engine.player.camera.CursorRay;
import com.xbuilders.engine.utils.ResourceUtils;
import com.xbuilders.engine.utils.math.AABB;
import com.xbuilders.engine.utils.math.MathUtils;
import com.xbuilders.game.blockTools.BlockTool;
import com.xbuilders.game.blockTools.BlockTools;
import org.joml.Matrix4f;
import org.joml.Vector3i;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.nuklear.NkVec2;

import java.io.IOException;
import java.util.ArrayList;

public class PaintTool extends BlockTool {
    public PaintTool(BlockTools tools, CursorRay cursorRay) {
        super("Paint", tools, cursorRay);
        try {
            setIcon(ResourceUtils.resource("blockTools\\paint.png"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    @Override
    public boolean shouldActivate(int key, int scancode, int action, int mods) {
        if (key == GLFW.GLFW_KEY_6) return true;
        return false;
    }

    @Override
    public String toolDescription() {
        return "Paint (x" + (size * 2) + ")";
    }

    @Override
    public void activate() {
        GameScene.player.camera.cursorRay.disableBoundaryMode();
    }


    @Override
    public boolean setBlock(Block item, CursorRay ray, boolean isCreationMode) {
        setAABB(settingAABB, ray);

        //Get block at cursor hit position
        Block replaceBlock = GameScene.world.getBlock(ray.getHitPos().x, ray.getHitPos().y, ray.getHitPos().z);
        Block newBlock = isCreationMode ? getSelectedBlock() : BlockList.BLOCK_AIR;
        if (newBlock == null || newBlock == replaceBlock) return false;

        //do A BFS from the center of the box
        ArrayList<Vector3i> queue = new ArrayList<>();
        queue.add(getStartingPos(ray));

        while (!queue.isEmpty()) {
            Vector3i pos = queue.remove(0);

            GameScene.player.setBlock(newBlock.id, pos.x, pos.y, pos.z);

            propagate(pos.x + 1, pos.y, pos.z, newBlock, replaceBlock, queue);
            propagate(pos.x - 1, pos.y, pos.z, newBlock, replaceBlock, queue);
            propagate(pos.x, pos.y + 1, pos.z, newBlock, replaceBlock, queue);
            propagate(pos.x, pos.y - 1, pos.z, newBlock, replaceBlock, queue);
            propagate(pos.x, pos.y, pos.z + 1, newBlock, replaceBlock, queue);
            propagate(pos.x, pos.y, pos.z - 1, newBlock, replaceBlock, queue);
        }

        return true;
    }

    private void propagate(int x, int y, int z, Block newBlock, Block replaceBlock, ArrayList<Vector3i> queue) {
        if (x > settingAABB.max.x || x < settingAABB.min.x) return;
        if (y > settingAABB.max.y || y < settingAABB.min.y) return;
        if (z > settingAABB.max.z || z < settingAABB.min.z) return;

        Block b = GameScene.world.getBlock(x, y, z);
        if (b.id == replaceBlock.id) {
//            System.out.println("\tPropagating neighbor: " + b + " this: " + newBlock);
            GameScene.player.setBlock(newBlock.id, x, y, z);
            queue.add(new Vector3i(x, y, z));
        }
    }

    public Vector3i getStartingPos(CursorRay ray) {
        Vector3i start = ray.getHitPos();
        return start;
    }

    private void setAABB(AABB aabb, CursorRay ray) {
        Vector3i start = getStartingPos(ray);

        aabb.min.set(start.x - size, start.y - size, start.z - size);
        aabb.max.set(start.x + size, start.y + size, start.z + size);
    }

    @Override
    public boolean drawCursor(CursorRay ray, Matrix4f proj, Matrix4f view) {
        setAABB(renderingAABB, ray);
        renderingAABB.max.add(1, 1, 1);

        ray.cursorBox.set(renderingAABB);
        ray.cursorBox.draw(proj, view);
        return true;
    }

    final AABB renderingAABB = new AABB();
    final AABB settingAABB = new AABB();
    int size;
    final int maxLength = 25;

    public boolean keyEvent(int key, int scancode, int action, int mods) {
        return false;
    }

    @Override
    public boolean mouseScrollEvent(NkVec2 scroll, double xoffset, double yoffset) {
        size += (int) yoffset;
        size = MathUtils.clamp(size, 1, maxLength);
        return true;
    }

}
