package com.xbuilders.game.UI;

import com.xbuilders.engine.ui.Theme;
import com.xbuilders.engine.ui.gameScene.GameUIElement;
import com.xbuilders.window.NKWindow;
import com.xbuilders.window.WindowEvents;
import org.lwjgl.nuklear.NkContext;
import org.lwjgl.nuklear.NkRect;
import org.lwjgl.nuklear.NkVec2;
import org.lwjgl.nuklear.Nuklear;
import org.lwjgl.system.MemoryStack;

import java.io.File;
import java.util.function.Consumer;

import static org.lwjgl.nuklear.Nuklear.nk_begin;
import static org.lwjgl.nuklear.Nuklear.nk_rect;

public class FileDialog extends GameUIElement implements WindowEvents {

    public FileDialog(NkContext ctx, NKWindow window) {
        super(ctx, window);
        releaseMouse = true;
    }

    public final int width = 400;
    public final int height = 300;

    Consumer<File> onSelectCallback;
    File baseDir;
    int mode = 0;

    public static final int MODE_OPEN = 0;
    public static final int MODE_SAVE = 1;

    @Override
    public boolean isOpen() {
        return baseDir != null;
    }

    public void show(File baseDir, int mode, Consumer<File> onSelectCallback) {
        this.onSelectCallback = onSelectCallback;
        this.baseDir = baseDir;
        this.mode = mode;
    }

    public void hide() {
        baseDir = null;
    }

    @Override
    public void draw(MemoryStack stack) {
        if (baseDir != null) {
            NkRect windowDims = NkRect.malloc(stack);
            Theme.resetWindowColor(ctx);
            Theme.resetWindowPadding(ctx);
            Theme.resetEntireButtonStyle(ctx);
            nk_rect(
                    window.getWidth() / 2 - (width / 2),
                    window.getHeight() / 2 - (height / 2), width, height, windowDims);

            if (nk_begin(ctx, "FileDialog", windowDims, Nuklear.NK_WINDOW_TITLE)) {
                Nuklear.nk_label(ctx, "Select File", Nuklear.NK_TEXT_LEFT);
                Nuklear.nk_layout_row_dynamic(ctx, 30, 1);
                for (File f : baseDir.listFiles()) {
                    if (Nuklear.nk_button_label(ctx, f.getName())) {
                        System.out.println(f.getAbsolutePath());
                    }
                }
                Nuklear.nk_end(ctx);
            }
        }
    }


    @Override
    public void windowResizeEvent(int width, int height) {

    }

    @Override
    public boolean keyEvent(int key, int scancode, int action, int mods) {
        return false;
    }

    @Override
    public void mouseButtonEvent(int button, int action, int mods) {

    }

    @Override
    public void mouseScrollEvent(NkVec2 scroll, double xoffset, double yoffset) {

    }
}
