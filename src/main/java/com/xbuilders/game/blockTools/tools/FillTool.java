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

public class FillTool extends BlockTool {
    public FillTool(BlockTools tools, CursorRay cursorRay) {
        super("Fill", tools, cursorRay);
        try {
            setIcon(ResourceUtils.resource("blockTools\\fill.png"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    @Override
    public boolean shouldActivate(int key, int scancode, int action, int mods) {
        if (key == GLFW.GLFW_KEY_5) return true;
        return false;
    }

    @Override
    public String toolDescription() {
        return "Fill (x" + (size * 2) + ")";
    }

    @Override
    public void activate() {
        GameScene.player.camera.cursorRay.disableBoundaryMode();
    }


    @Override
    public boolean setBlock(Block item, CursorRay ray, boolean isCreationMode) {
        setAABB(settingAABB, ray);

        Block block = isCreationMode ? getSelectedBlock() : BlockList.BLOCK_AIR;
        if (block == null) return false;

        //do A BFS from the center of the box
        ArrayList<Vector3i> queue = new ArrayList<>();
        queue.add(getStartingPos(ray));

        while (!queue.isEmpty()) {
            Vector3i pos = queue.remove(0);

            GameScene.player.setBlock(block.id, pos.x, pos.y, pos.z);

            propagate(pos.x + 1, pos.y, pos.z, block, queue);
            propagate(pos.x - 1, pos.y, pos.z, block, queue);
            propagate(pos.x, pos.y + 1, pos.z, block, queue);
            propagate(pos.x, pos.y - 1, pos.z, block, queue);
            propagate(pos.x, pos.y, pos.z + 1, block, queue);
            propagate(pos.x, pos.y, pos.z - 1, block, queue);
        }

        return true;
    }

    private void propagate(int x, int y, int z, Block block, ArrayList<Vector3i> queue) {
        if (x > settingAABB.max.x + 1 || x < settingAABB.min.x) return;
        if (y > settingAABB.max.y + 1 || y < settingAABB.min.y) return;
        if (z > settingAABB.max.z + 1 || z < settingAABB.min.z) return;

        Block b = GameScene.world.getBlock(x, y, z);
        if (!b.solid && b.id != block.id) {
//            System.out.println("\tPropagating neighbor: " + b + " this: " + block);
            GameScene.player.setBlock(block.id, x, y, z);
            queue.add(new Vector3i(x, y, z));
        }
    }

    public Vector3i getStartingPos(CursorRay ray) {
        Vector3i start = ray.getHitPosPlusNormal();
        if (placeOnHit) start = ray.getHitPos();
        return start;
    }

    private void setAABB(AABB aabb, CursorRay ray) {
        Vector3i start = getStartingPos(ray);
        Vector3i normal = ray.getHitNormalAsInt();

        aabb.min.set(start.x - size, start.y - size, start.z - size);
        aabb.max.set(start.x + size, start.y + size, start.z + size);

        if (normal.x != 0) {
            aabb.min.x = start.x;
            aabb.max.x = start.x;
        }
        if (normal.y != 0) {
            aabb.min.y = start.y;
            aabb.max.y = start.y;
        }
        if (normal.z != 0) {
            aabb.min.z = start.z;
            aabb.max.z = start.z;
        }
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
    boolean placeOnHit = false;

    public boolean keyEvent(int key, int scancode, int action, int mods) {
        //If K is pressed
        if (key == GLFW.GLFW_KEY_K && action == GLFW.GLFW_PRESS) {
            placeOnHit = true;
            return true;
        }//If K is released
        else if (key == GLFW.GLFW_KEY_K && action == GLFW.GLFW_RELEASE) {
            placeOnHit = false;
            return true;
        }
        return false;
    }

    @Override
    public boolean mouseScrollEvent(NkVec2 scroll, double xoffset, double yoffset) {
        size += (int) yoffset;
        size = MathUtils.clamp(size, 1, maxLength);
        return true;
    }

}
