package com.xbuilders.game.blockTools.tools;

import com.xbuilders.game.blockTools.BlockTool;
import com.xbuilders.game.blockTools.BlockTools;
import org.lwjgl.glfw.GLFW;

public class DefaultTool extends BlockTool {
    public DefaultTool(BlockTools tools) {
        super("Default",tools);
    }

    @Override
    public boolean shouldActivate(int key, int scancode, int action, int mods) {
        if (key == GLFW.GLFW_KEY_1) return true;
        return false;
    }
}
