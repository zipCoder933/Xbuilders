/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.xbuilders.engine.client.visuals.topMenu;

/*
 * Copyright LWJGL. All rights reserved.
 * License terms: https://www.lwjgl.org/license
 */

import com.xbuilders.engine.client.ClientWindow;
import com.xbuilders.engine.server.GameMode;
import com.xbuilders.engine.client.visuals.Theme;
import com.xbuilders.engine.server.world.Terrain;
import com.xbuilders.engine.server.world.data.WorldData;
import com.xbuilders.engine.server.world.WorldsHandler;
import com.xbuilders.engine.client.visuals.Page;
import com.xbuilders.window.nuklear.components.TextBox;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import org.lwjgl.nuklear.*;
import org.lwjgl.system.*;

import static com.xbuilders.engine.client.visuals.topMenu.TopMenu.row;
import static org.lwjgl.nuklear.Nuklear.*;

/**
 * Java port of
 * <a href="https://github.com/vurtun/nuklear/blob/master/demo/calculator.c">https://github.com/vurtun/nuklear/blob/master/demo/calculator.c</a>.
 */
public class NewWorld implements MenuPage {

    public NewWorld(NkContext ctx, ClientWindow window, TopMenu menu) {
        this.ctx = ctx;
        this.window = window;
        this.menu = menu;
        name = new TextBox(20);
        terrainSelector = new TerrainSelector(ClientWindow.game.terrainsList, ctx);
    }

    TextBox name;
    NkContext ctx;
    TopMenu menu;
    ClientWindow window;
    TerrainSelector terrainSelector;

    final int boxWidth = menu.WIDTH_2;
    final int boxHeight = 500;
    GameMode gameMode = GameMode.ADVENTURE;


    public static boolean labeledButton(NkContext ctx, String label, String button) {
        nk_layout_row_static(ctx, 20, 1, 1);
        nk_layout_row_dynamic(ctx, 10, 1);
        nk_style_set_font(ctx, Theme.font_10);
        nk_text(ctx, label, NK_TEXT_ALIGN_LEFT);
        nk_layout_row_dynamic(ctx, 40, 1);
        nk_style_set_font(ctx, Theme.font_12);
        return nk_button_label(ctx, button);
    }

    @Override
    public void layout(MemoryStack stack, NkRect windowDims, IntBuffer titleYEnd) {
        nk_style_set_font(ctx, Theme.font_12);
        nk_rect((window.getWidth() / 2) - (boxWidth / 2), titleYEnd.get(0),
                boxWidth, boxHeight, windowDims);

        if (nk_begin(ctx, "New World", windowDims, NK_WINDOW_BORDER | NK_WINDOW_TITLE)) {
            nk_style_set_font(ctx, Theme.font_10);
            nk_layout_row_static(ctx, 20, 1, 1);
            row(ctx, "World Name", 1);
            nk_layout_row_dynamic(ctx, 30, 1);
            name.render(ctx);

            if (labeledButton(ctx, "Game Mode:", gameMode.toString())) {
                //Go to the next game mode
                int index = gameMode.ordinal() + 1;
                index = index % GameMode.values().length;
                gameMode = GameMode.values()[index];
            }
            terrainSelector.draw();


            nk_layout_row_static(ctx, 20, 1, 1);


            Terrain terrain = terrainSelector.getSelectedTerrain();

            if (!terrain.options.isEmpty()) { //Start the terrain properties
                row(ctx, "World Options", 1);
                nk_layout_row_dynamic(ctx, 20, 1);
                terrain.options.forEach((key, value) -> {
                    ByteBuffer active = stack.malloc(1);
                    active.put(0, value ? (byte) 0 : 1); //For some reason the boolean needs to be flipped
                    if (nk_checkbox_label(ctx, " " + key, active)) {
                        terrain.options.put(key, !value);
                        System.out.println(terrain.options);
                    }
                });
            }

            nk_style_set_font(ctx, Theme.font_12);
            nk_layout_row_static(ctx, 20, 1, 1);
            nk_layout_row_dynamic(ctx, 40, 1);
            if (nk_button_label(ctx, "CREATE")) {
                if (makeNewWorld(name.getValueAsString(), 0, terrain, 0)) {
                    menu.setPage(Page.LOAD_WORLD);
                }
            }
            if (nk_button_label(ctx, "BACK")) {
                menu.setPage(Page.HOME);
            }
        }
        nk_end(ctx);
    }


    @Override
    public void onOpen(Page lastPage) {
        name.setValueAsString("New World");
        terrainSelector.reset();
    }

    private boolean makeNewWorld(String name, int size, Terrain terrain, int seed) {
        try {
            WorldData info = new WorldData();
            info.makeNew(name, size, terrain, seed);
            info.data.gameMode = gameMode.ordinal();
            if (WorldsHandler.worldNameAlreadyExists(info.getName())) {
                ClientWindow.popupMessage.message("Error", "World name \"" + info.getName() + "\" Already exists!");
                return false;
            } else WorldsHandler.makeNewWorld(info);
        } catch (IOException ex) {
            ClientWindow.popupMessage.message("Error", ex.getMessage());
            return false;
        }
        return true;
    }

}
