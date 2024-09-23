/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.xbuilders.engine.ui.topMenu;

/*
 * Copyright LWJGL. All rights reserved.
 * License terms: https://www.lwjgl.org/license
 */

import com.xbuilders.engine.world.WorldInfo;
import com.xbuilders.engine.world.WorldsHandler;
import com.xbuilders.engine.MainWindow;
import com.xbuilders.engine.ui.Page;
import com.xbuilders.engine.ui.Theme;
import com.xbuilders.engine.utils.ErrorHandler;
import com.xbuilders.engine.utils.progress.ProgressData;
import com.xbuilders.window.nuklear.NKUtils;

import java.io.IOException;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.lwjgl.nuklear.*;
import org.lwjgl.system.*;

import static org.lwjgl.nuklear.Nuklear.*;

/**
 * Java port of
 * <a href="https://github.com/vurtun/nuklear/blob/master/demo/calculator.c">https://github.com/vurtun/nuklear/blob/master/demo/calculator.c</a>.
 */
public class LoadWorld implements MenuPage {

    public LoadWorld(NkContext ctx, MainWindow window, TopMenu menu) throws IOException {
        this.ctx = ctx;
        this.window = window;
        this.menu = menu;
        worlds = new ArrayList<>();


//        Texture texture = TextureUtils.loadTexture(ResourceUtils.RESOURCE_DIR + "\\icon.png", false);
//        image = new NkImage(texture.buffer);
//       image = nk_image_id(texture.id, image);
    }

    // Texture texture;
    NkContext ctx;
    TopMenu menu;
    MainWindow window;
    final int BOX_DEFAULT_WIDTH = 800;
    final int BOX_DEFAULT_HEIGHT = 550;
    ArrayList<WorldInfo> worlds;
    WorldInfo currentWorld;
//    NkImage image;

    @Override
    public void onOpen() {
        try {
            WorldsHandler.listWorlds(worlds);
            if (worlds.size() > 0) {
                currentWorld = worlds.get(0);
            }
        } catch (IOException ex) {
            Logger.getLogger(LoadWorld.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void layout(MemoryStack stack, NkRect windowDims, IntBuffer titleYEnd) {
        int boxWidth = (int) (BOX_DEFAULT_WIDTH * Theme.getScale());
        int boxHeight = (int) (BOX_DEFAULT_HEIGHT * Theme.getScale());

        nk_rect((window.getWidth() / 2) - (boxWidth / 2),
                titleYEnd.get(0),
                boxWidth, boxHeight, windowDims);
        nk_style_set_font(ctx, Theme.font_12);

        if (nk_begin(ctx, "Load World", windowDims, NK_WINDOW_BORDER | NK_WINDOW_TITLE)) {

            nk_layout_row_dynamic(ctx, BOX_DEFAULT_HEIGHT - 50, 2); // Adjust height as needed
            nk_group_begin(ctx, "Worlds", NK_WINDOW_TITLE);
            nk_style_set_font(ctx, Theme.font_10);
            nk_layout_row_dynamic(ctx, 30, 1);//this sets the height of the subsequent elements
            ctx.style().button().text_alignment(NK_TEXT_ALIGN_LEFT);

            for (int i = 0; i < worlds.size(); i++) { // Adjust the number of buttons
                if (nk_button_label(ctx, worlds.get(i).getName())) {
                    currentWorld = worlds.get(i);
                }
            }

            Theme.resetEntireButtonStyle(ctx);
            nk_group_end(ctx);

            nk_style_set_font(ctx, Theme.font_12);
            nk_group_begin(ctx, "Details", NK_WINDOW_TITLE);

            if (currentWorld != null) {
                nk_style_set_font(ctx, Theme.font_10);
                NKUtils.text(ctx, currentWorld.getDetails(), 10, NK_TEXT_ALIGN_LEFT);
                nk_layout_row_static(ctx, 40, 1, 1);
                nk_layout_row_dynamic(ctx, 40, 1);

                nk_style_set_font(ctx, Theme.font_12);

                if (!currentWorld.infoFile.isJoinedMultiplayerWorld) {
                    if (nk_button_label(ctx, "LOAD WORLD")) {
                        loadWorld(currentWorld, null);
                    }
                    if (nk_button_label(ctx, "HOST AS MULTIPLAYER")) {
                        menu.setPage(Page.HOST_MULTIPLAYER);
                    }
                }

                if (nk_button_label(ctx, "DELETE WORLD")) {
                    MainWindow.popupMessage.confirmation("Delete World",
                            "Are you sure you want to delete " + currentWorld.getName() + "?",
                            () -> deleteCurrentWorld());
                }
            }

            nk_layout_row_dynamic(ctx, 40, 1);
            if (nk_button_label(ctx, "BACK")) {
                menu.setPage(Page.HOME);
            }
            nk_group_end(ctx);
        }
        nk_end(ctx);

    }

    private void deleteCurrentWorld() {
        try {
            WorldsHandler.deleteWorld(currentWorld);
        } catch (IOException ex) {
            MainWindow.popupMessage.message("Error Deleting World", ex.getMessage());
        }
        try {
            WorldsHandler.listWorlds(worlds);
        } catch (IOException ex) {
            ErrorHandler.report(ex);
        }
        currentWorld = null;
    }

    public void loadWorld(final WorldInfo world, NetworkJoinRequest req) {

//        if (world.infoFile.isJoinedMultiplayerWorld) {
//            menu.popupMessage.message("Denied", "Cannot this world unless it has been joined as a multiplayer world");
//            return;
//        }

        String title = "Loading World...";
        ProgressData prog = new ProgressData(title);

        MainWindow.gameScene.startGame(world, req, prog);
        menu.progress.enable(prog,
                () -> {//update
                    MainWindow.gameScene.newGameUpdate();
                },
                () -> {//finished
                    MainWindow.goToGamePage();
                    menu.setPage(Page.HOME);
                },
                () -> {//canceled
                    System.out.println("Canceled");
                    menu.setPage(Page.HOME);
                });
    }


}
