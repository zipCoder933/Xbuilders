package com.xbuilders.game.blockTools.tools;

import com.xbuilders.engine.player.camera.CursorRay;
import com.xbuilders.engine.utils.math.AABB;
import com.xbuilders.game.blockTools.BlockTool;
import com.xbuilders.game.blockTools.BlockTools;
import org.joml.Matrix4f;
import org.joml.Vector4f;
import org.lwjgl.glfw.GLFW;

public class PasteTool extends BlockTool {
    public PasteTool(BlockTools tools) {
        super("Paste", tools);
    }


    @Override
    public boolean shouldActivate(int key, int scancode, int action, int mods) {
        //Only activate with Ctrl+C
        if (key == GLFW.GLFW_KEY_V && (mods & GLFW.GLFW_MOD_CONTROL) != 0) {
            return true;
        }
        return false;
    }

    @Override
    public boolean drawCursor(CursorRay ray, Matrix4f proj, Matrix4f view) {
        ray.cursorBox.setPosAndSize(ray.getHitPos().x, ray.getHitPos().y, ray.getHitPos().z, 2, 2, 2);
        ray.cursorBox.setColor(new Vector4f(1, 0, 0, 1));
        ray.cursorBox.draw(proj, view);
        return true;
    }

    @Override
    public boolean keyEvent(int key, int scancode, int action, int mods) {
        return false;
    }
}
