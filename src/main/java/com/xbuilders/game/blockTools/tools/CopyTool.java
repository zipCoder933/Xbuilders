package com.xbuilders.game.blockTools.tools;

import com.xbuilders.engine.items.block.Block;
import com.xbuilders.engine.utils.math.AABB;
import com.xbuilders.game.blockTools.BlockTool;
import com.xbuilders.game.blockTools.BlockTools;
import org.lwjgl.glfw.GLFW;

public class CopyTool extends BlockTool {
    public CopyTool(BlockTools tools) {
        super("Copy",tools);
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
    public boolean keyEvent(int key, int scancode, int action, int mods) {
        return false;
    }
}
