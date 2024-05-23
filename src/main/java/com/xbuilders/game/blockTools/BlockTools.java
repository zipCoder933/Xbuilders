package com.xbuilders.game.blockTools;

import com.xbuilders.engine.gameScene.GameScene;
import com.xbuilders.engine.ui.Theme;
import com.xbuilders.engine.ui.UIResources;
import com.xbuilders.engine.ui.gameScene.GameUIElement;
import com.xbuilders.game.MyGame;
import com.xbuilders.game.blockTools.tools.CopyTool;
import com.xbuilders.game.blockTools.tools.DefaultTool;
import com.xbuilders.game.blockTools.tools.PasteTool;
import com.xbuilders.game.blockTools.tools.Tool_BoundarySetDelete;
import com.xbuilders.window.NKWindow;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.nuklear.NkContext;
import org.lwjgl.nuklear.NkRect;
import org.lwjgl.nuklear.NkVec2;
import org.lwjgl.nuklear.Nuklear;
import org.lwjgl.system.MemoryStack;

import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.nuklear.Nuklear.*;

public class BlockTools extends GameUIElement {

    public BlockTools(NkContext ctx, NKWindow window, UIResources uires) {
        super(ctx, window, uires);
        tools.add(new DefaultTool(this));
        tools.add(new Tool_BoundarySetDelete(this));
        tools.add(new CopyTool(this));
        tools.add(new PasteTool(this));
    }

    public final List<BlockTool> tools = new ArrayList<BlockTool>();

    int selectedTool = 0;

    public void addTool(BlockTool tool) {
        tools.add(tool);
    }

    int menuWidth = 400;
    int menuHeight = 30;

    @Override
    public void draw(MemoryStack stack) {
        NkRect windowDims = NkRect.malloc(stack);

        Theme.resetEntireButtonStyle(ctx);
        Theme.resetWindowColor(ctx);
        Theme.resetWindowPadding(ctx);
        nk_style_set_font(ctx, uires.font_8);

        nk_rect(window.getWidth() / 2 - (menuWidth / 2),
                0,
                menuWidth, menuHeight, windowDims);

        if (nk_begin(ctx, "Block Tools", windowDims, Nuklear.NK_WINDOW_NO_SCROLLBAR)) {
            nk_layout_row_dynamic(ctx, menuHeight, 1);
            Nuklear.nk_text(ctx, tools.get(selectedTool).toolDescription(), Nuklear.NK_TEXT_ALIGN_CENTERED);
        }
    }

    /**
     * @param scroll
     * @param xoffset
     * @param yoffset
     * @return true if the event was consumed
     */
    public boolean mouseScrollEvent(NkVec2 scroll, double xoffset, double yoffset) {
        boolean allowToolParameters = true;
        if (allowToolParameters) {
            return tools.get(selectedTool).mouseScrollEvent(scroll, xoffset, yoffset);
        }
        return false;
    }

    /**
     * @param key
     * @param scancode
     * @param action
     * @param mods
     * @return true if the event was consumed
     */
    public boolean keyEvent(int key, int scancode, int action, int mods) {
        boolean allowToolParameters = true;
        if (action == GLFW.GLFW_RELEASE) {
            for (int i = 0; i < tools.size(); i++) {
                if (tools.get(i).shouldActivate(key, scancode, action, mods)) {
                    selectTool(i);
                    allowToolParameters = false;
                    break;
                }
            }
        } else if (action == GLFW.GLFW_PRESS) {
        }
        if (allowToolParameters) {
            return tools.get(selectedTool).keyEvent(key, scancode, action, mods);
        }

        return false;
    }

    private void selectTool(int i) {
        tools.get(selectedTool).deactivate();
        selectedTool = i;
        tools.get(selectedTool).activate();
    }

    public boolean mouseButtonEvent(int button, int action, int mods) {
        return tools.get(selectedTool).mouseButtonEvent(button, action, mods);
    }

    public BlockTool getSelectedTool() {
        return tools.get(selectedTool);
    }
}
