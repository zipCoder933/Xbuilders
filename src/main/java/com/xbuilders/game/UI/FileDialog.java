package com.xbuilders.game.UI;

import com.xbuilders.engine.ui.OpenHandler;
import com.xbuilders.engine.ui.Theme;
import com.xbuilders.engine.ui.gameScene.GameUIElement;
import com.xbuilders.engine.ui.topMenu.PopupMessage;
import com.xbuilders.window.NKWindow;
import com.xbuilders.window.WindowEvents;
import com.xbuilders.window.nuklear.components.TextBox;
import org.lwjgl.nuklear.NkContext;
import org.lwjgl.nuklear.NkRect;
import org.lwjgl.nuklear.NkVec2;
import org.lwjgl.nuklear.Nuklear;
import org.lwjgl.system.MemoryStack;

import java.io.File;
import java.util.function.Consumer;

import static org.lwjgl.nuklear.Nuklear.*;

public class FileDialog extends GameUIElement implements WindowEvents {

    public FileDialog(NkContext ctx, NKWindow window) {
        super(ctx, window);
        releaseMouse = true;
        prompt = new PopupMessage(ctx, window);
        fileNameBox = new TextBox(100);
        oh = new OpenHandler(ctx, WINDOW_NAME_ID, windowFlags);
    }

    final String WINDOW_NAME_ID = "FileDialog";
    final int windowFlags = Nuklear.NK_WINDOW_TITLE | Nuklear.NK_WINDOW_NO_SCROLLBAR | NK_WINDOW_CLOSABLE;

    String prefferedFileExtension;
    TextBox fileNameBox;
    public final int width = 550;
    public final int height = 420;

    PopupMessage prompt;
    Consumer<File> onSelectCallback;
    File baseDir, navDir, selectedFile;
    boolean saveMode;
    OpenHandler oh;

    @Override
    public boolean isOpen() {
        return oh.isOpen();
    }

    public void show(File baseDir,
                     boolean saveMode,
                     String prefferedFileExtension,
                     Consumer<File> onSelectCallback) {
        this.onSelectCallback = onSelectCallback;
        this.baseDir = baseDir;

        this.prefferedFileExtension = prefferedFileExtension.toLowerCase();
        if (!this.prefferedFileExtension.startsWith(".")) {
            this.prefferedFileExtension = "." + this.prefferedFileExtension;
        }

        oh.setOpen(true);
        fileNameBox.setValueAsString("");
        this.navDir = baseDir;
        this.saveMode = saveMode;
        nk_window_show(ctx, WINDOW_NAME_ID, windowFlags);
    }

    public void hide() {
        baseDir = null;
        oh.setOpen(false);
    }

    @Override
    public void draw(MemoryStack stack) {
        if (isOpen()) {
            NkRect windowDims = NkRect.malloc(stack);
            Theme.resetWindowColor(ctx);
            Theme.resetWindowPadding(ctx);
            Theme.resetEntireButtonStyle(ctx);

            nk_rect(
                    window.getWidth() / 2 - (width / 2),
                    window.getHeight() / 2 - (height / 2), width, height, windowDims);

            String title = (saveMode ? "Save File" : "Open File");

            if (nk_begin(ctx, WINDOW_NAME_ID, windowDims, windowFlags)) {
                oh.autoClose();
                nk_style_set_font(ctx, Theme.font_9);


                Nuklear.nk_layout_row_dynamic(ctx, 25, 1);

                String dir = navDir.getPath();
                //Remove base dir from path
                dir = dir.substring(baseDir.getPath().length());
                Nuklear.nk_label(ctx, dir, NK_TEXT_RIGHT);

                if (canGoUpOneDir()) {
                    ctx.style().button().active().data().color().set(Theme.blue);
                } else {
                    ctx.style().button().active().data().color().set(Theme.gray);
                }
                if (Nuklear.nk_button_label(ctx, "^- Up Directory")) {
                    if (canGoUpOneDir()) {
                        navigateToFolder(navDir.getParentFile());
                    }
                }


                Nuklear.nk_layout_row_dynamic(ctx, height - 145 - 30, 1);
                ctx.style().window().background().set(Theme.gray);
                ctx.style().button().hover().data().color().set(Theme.blue);
                ctx.style().button().border(0);

                filesGroup();

                Theme.resetEntireButtonStyle(ctx);

                Nuklear.nk_layout_row_dynamic(ctx, 30, 1);
                fileNameBox.render(ctx);

                Nuklear.nk_layout_row_dynamic(ctx, 30, 3);
                if (Nuklear.nk_button_label(ctx, "New Folder")) {
                    if (navDir != null && !fileNameBox.getValueAsString().isBlank()) {
                        File newDir = new File(navDir, fileNameBox.getValueAsString());
                        newDir.mkdirs();
                        navigateToFolder(newDir);
                        fileNameBox.setValueAsString("");
                    }
                }
                if (Nuklear.nk_button_label(ctx, "Delete")) {
                    if (selectedFile != null && !selectedFile.isDirectory()) {
                        prompt.message("Delete file?",
                                "Are you sure you want to delete " + selectedFile.getName() + "?",
                                () -> {
                                    System.out.println("Deleting " + selectedFile.getPath());
                                    selectedFile.delete();
                                });
                    }
                }
                if (saveMode) {
                    if (Nuklear.nk_button_label(ctx, "Save")) {
                        saveFile(fileNameBox.getValueAsString());
                        fileNameBox.setValueAsString("");
                    }
                } else {
                    if (Nuklear.nk_button_label(ctx, "Select")) {
                        if (selectedFile != null && !selectedFile.isDirectory()) {
                            onSelectCallback.accept(selectedFile);
                            hide();
                        }
                    }
                }
                Nuklear.nk_end(ctx);
            } else {
                oh.setOpen(false);
            }
            oh.autoClose();
        }
        prompt.draw(stack);
        oh.autoClose();
    }

    private boolean canGoUpOneDir() {
        return !navDir.equals(baseDir) && navDir.getParentFile() != null;
    }

    private void navigateToFolder(File newDir) {
        if (newDir != null) {
            navDir = newDir;
            selectedFile = null;
        }
    }

    private void filesGroup() {
        if (Nuklear.nk_group_begin(ctx, "Files", 0)) {
            Nuklear.nk_layout_row_dynamic(ctx, 25, 1);
            for (File f : navDir.listFiles()) {

                if(!hasFileExtension(f)) continue;

                if (selectedFile != null && selectedFile.equals(f)) {
                    ctx.style().button().normal().data().color().set(Theme.blue);
                } else {
                    ctx.style().button().normal().data().color().set(Theme.gray);
                }

                if (Nuklear.nk_button_label(ctx,
                        f.getName()
                                + (f.isDirectory() ? "\\" : ""))) {
                    if (f.isDirectory()) {
                        navDir = f;
                    } else {
                        selectedFile = f;
                    }
                }
            }
            Nuklear.nk_group_end(ctx);
        }
    }


    private void saveFile(String filename) {
        if (filename.isBlank()) return;

        File newFile = new File(navDir, fileNameBox.getValueAsString());


        if (newFile.exists()) {
            if (newFile.isDirectory()) return;

            prompt.message("File Exists",
                    "Do you want to overwrite " + newFile.getName() + "?",
                    () -> {
                        System.out.println("Overwriting " + newFile.getName());
                        acceptFile(newFile);
                    });
        } else {
            System.out.println("Creating " + newFile.getName());
            acceptFile(newFile);
        }
    }

    public boolean hasFileExtension(File file) {
        if (prefferedFileExtension == null) return true;
        return file.getName().toLowerCase().endsWith(prefferedFileExtension);
    }

    private void acceptFile(File file) {
        if (!hasFileExtension(file))
            prompt.message("Invalid File Type",
                    "File type must be " + prefferedFileExtension);

        onSelectCallback.accept(file);
        hide();
    }


    @Override
    public void windowResizeEvent(int width, int height) {

    }

    @Override
    public boolean keyEvent(int key, int scancode, int action, int mods) {
        return false;
    }

    @Override
    public boolean mouseButtonEvent(int button, int action, int mods) {
        return false;
    }

    @Override
    public boolean mouseScrollEvent(NkVec2 scroll, double xoffset, double yoffset) {
        return false;
    }
}
