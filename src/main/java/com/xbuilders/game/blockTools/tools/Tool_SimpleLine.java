package com.xbuilders.game.blockTools.tools;

import com.xbuilders.game.blockTools.BlockTool;
import com.xbuilders.window.KeyCombination;
import org.lwjgl.glfw.GLFW;

public class Tool_SimpleLine extends BlockTool {
    public Tool_SimpleLine() {
        super("Simple Line", new int[]{
                GLFW.GLFW_KEY_3//TODO: Add key combinations
        });
    }
}
