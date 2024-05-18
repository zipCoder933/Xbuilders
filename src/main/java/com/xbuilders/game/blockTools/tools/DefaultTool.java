package com.xbuilders.game.blockTools.tools;

import com.xbuilders.game.blockTools.BlockTool;
import org.lwjgl.glfw.GLFW;

public class DefaultTool extends BlockTool {
    public DefaultTool() {
        super("Default");
    }

    @Override
    public boolean shouldActivate(int key, int scancode, int action, int mods) {
        if (key == GLFW.GLFW_KEY_1) return true;
        return false;
    }
}
