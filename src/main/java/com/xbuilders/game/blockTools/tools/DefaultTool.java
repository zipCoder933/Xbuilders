package com.xbuilders.game.blockTools.tools;

import com.xbuilders.game.blockTools.BlockTool;
import org.lwjgl.glfw.GLFW;

public class DefaultTool extends BlockTool {
    public DefaultTool() {
        super("Default", new int[]{GLFW.GLFW_KEY_1});
    }
}
