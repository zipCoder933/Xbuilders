package com.xbuilders.game.vanilla.blockTools;

import com.xbuilders.engine.MainWindow;
import com.xbuilders.engine.gameScene.GameMode;
import com.xbuilders.engine.gameScene.GameScene;
import com.xbuilders.engine.items.block.Block;
import com.xbuilders.engine.items.block.BlockRegistry;
import com.xbuilders.engine.items.item.ItemStack;
import com.xbuilders.engine.player.CursorRay;
import com.xbuilders.engine.ui.Theme;
import com.xbuilders.engine.ui.gameScene.UI_GameMenu;
import com.xbuilders.game.vanilla.blockTools.tools.*;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.nuklear.*;
import org.lwjgl.system.MemoryStack;

import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.nuklear.Nuklear.*;

public class BlockTools extends UI_GameMenu {

    public BlockTools(NkContext ctx, MainWindow window, CursorRay cursorRay) {
        super(ctx, window);
        tools.add(new DefaultTool(this, cursorRay));
        tools.add(new PlaneTool(this, cursorRay));
        tools.add(new BoundaryTool(this, cursorRay));
        tools.add(new LineTool(this, cursorRay));
        tools.add(new CircleTool(this, cursorRay));
        tools.add(new SphereTool(this, cursorRay));
        tools.add(new PaintTool(this, cursorRay));
        tools.add(new CopyTool(this, cursorRay));
        tools.add(new PasteTool(this, cursorRay));
        tools.add(new LightFixTool(this, cursorRay));
        pallete = new BlockToolPallete(ctx, window, tools, this);
    }

    public final List<BlockTool> tools = new ArrayList<BlockTool>();
    BlockToolPallete pallete;
    int selectedTool;

    public void addTool(BlockTool tool) {
        tools.add(tool);
    }

    int menuWidth = 400;
    int menuHeight = 30;

    @Override
    public void draw(MemoryStack stack) {
        if (GameScene.getGameMode() == GameMode.FREEPLAY) {
            NkRect windowDims = NkRect.malloc(stack);

            Theme.resetEntireButtonStyle(ctx);
            Theme.resetWindowColor(ctx);
            Theme.resetWindowPadding(ctx);
            nk_style_set_font(ctx, Theme.font_10);

            nk_rect(window.getWidth() / 2 - (menuWidth / 2),
                    0,
                    menuWidth, menuHeight, windowDims);

            if (nk_begin(ctx, "Block Tools", windowDims, Nuklear.NK_WINDOW_NO_SCROLLBAR)) {
                nk_layout_row_dynamic(ctx, menuHeight, 1);
                Nuklear.nk_text(ctx, tools.get(selectedTool).toolDescription(), Nuklear.NK_TEXT_ALIGN_CENTERED);
            }
            nk_end(ctx);
            pallete.draw(stack);
        }
    }

    @Override
    public boolean isOpen() {
        return pallete.isOpen();
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
        if (GameScene.getGameMode() == GameMode.FREEPLAY) {
            if (pallete.keyEvent(key, scancode, action, mods)) {
            } else {
                if (action == GLFW.GLFW_RELEASE || action == GLFW.GLFW_PRESS) {
                    for (int i = 0; i < tools.size(); i++) {
                        if (tools.get(i).activationKey(key, scancode, action, mods)) {
                            selectTool(i);
                            return true;
                        }
                    }
                }
            }

            return tools.get(selectedTool).keyEvent(key, scancode, action, mods);
        }
        return false;
    }

    public void selectTool(int i) {
        tools.get(selectedTool).deactivate();
        selectedTool = i;
        GameScene.player.camera.cursorRay.disableBoundaryMode();
        tools.get(selectedTool).activate();
    }

    public boolean clickEvent(CursorRay ray, boolean isCreationMode) {
        Block block = BlockTool.getSelectedBlock();
        if (GameScene.getGameMode() != GameMode.FREEPLAY || block == null) return false;
        return getSelectedTool().setBlock(block, ray, isCreationMode);
    }

    public boolean UIMouseButtonEvent(int button, int action, int mods) {
        if (GameScene.getGameMode() != GameMode.FREEPLAY) return false;
        return tools.get(selectedTool).mouseButtonEvent(button, action, mods);
    }

    public BlockTool getSelectedTool() {
        if (tools.get(selectedTool) == null) return tools.get(0);
        return tools.get(selectedTool);
    }

    public boolean releaseMouse() {
        return pallete.isOpen();
    }

    public void reset() {
        selectTool(0);
    }
}
