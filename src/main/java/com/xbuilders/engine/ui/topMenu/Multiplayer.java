/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.xbuilders.engine.ui.topMenu;

/*
 * Copyright LWJGL. All rights reserved.
 * License terms: https://www.lwjgl.org/license
 */

import com.xbuilders.engine.player.UserControlledPlayer;
import com.xbuilders.engine.world.WorldInfo;
import com.xbuilders.engine.ui.Page;
import com.xbuilders.game.Main;
import com.xbuilders.window.NKWindow;
import com.xbuilders.window.nuklear.NKUtils;
import com.xbuilders.window.nuklear.components.NumberBox;
import com.xbuilders.window.nuklear.components.TextBox;

import java.nio.IntBuffer;

import org.lwjgl.nuklear.*;
import org.lwjgl.system.*;

import static org.lwjgl.nuklear.Nuklear.*;

/**
 * Java portBox of
 * <a href="https://github.com/vurtun/nuklear/blob/master/demo/calculator.c">https://github.com/vurtun/nuklear/blob/master/demo/calculator.c</a>.
 */
public class Multiplayer implements MenuPage {

    /**
     * @param world the world to set
     */
    public void setWorld(WorldInfo world) {
        this.world = world;
    }

    final String ipAdress;
    LoadWorld loadWorld;

    public Multiplayer(NkContext ctx, NKWindow window, TopMenu menu,
                       UserControlledPlayer player, boolean hosting,
                       String ipAdress, LoadWorld loadWorld) {
        this.loadWorld = loadWorld;
        this.ipAdress = ipAdress;
        this.ctx = ctx;
        this.window = window;
        this.menu = menu;
        portBox = new NumberBox(4, 0);
        fromPortBox = new NumberBox(4, 0);
        nameBox = new TextBox(20);
        ipAdressBox = new TextBox(20);

        fromPortBox.setValueAsNumber(8080);
        portBox.setValueAsNumber(8080);

        if(Main.devMode){
            if(hosting){
                fromPortBox.setValueAsNumber(8081);
                nameBox.setValueAsString("host");
            }else{
                portBox.setValueAsNumber(8081);
            }
        }

        this.player = player;
        this.hosting = hosting;
    }

    boolean hosting;
    UserControlledPlayer player;
    NkContext ctx;
    TopMenu menu;
    NKWindow window;
    NumberBox fromPortBox, portBox;
    TextBox nameBox, ipAdressBox;
    private WorldInfo world;

    final int boxWidth = 550;
    final int boxHeight = 450;

    @Override
    public void layout(MemoryStack stack, NkRect windowDims, IntBuffer titleYEnd) {
        nk_style_set_font(ctx, menu.uires.font_12);
        nk_rect((window.getWidth() / 2) - (boxWidth / 2), titleYEnd.get(0),
                boxWidth, boxHeight, windowDims);

        if (nk_begin(ctx, (hosting ? "Host" : "Join") + " Multiplayer World", windowDims, NK_WINDOW_BORDER | NK_WINDOW_TITLE)) {
            nk_style_set_font(ctx, menu.uires.font_10);
            nk_layout_row_dynamic(ctx, 20, 1);
            NKUtils.text(ctx, "All computers wanting to join must enter " + (hosting ? "this" : "the host's") + "\n"
                    + "IP adress and port to proceed:", 10, NK_TEXT_ALIGN_LEFT);
            nk_layout_row_static(ctx, 40, 1, 1);

            row("IP Adress:");
            if (hosting) {
                nk_label(ctx, ipAdressBox.getValueAsString(), NK_TEXT_ALIGN_LEFT);
            } else {
                ipAdressBox.render(ctx);
            }

            if (Main.devMode) {
                row("From Port:");
                fromPortBox.render(ctx);
            }

            row("Port:");
            portBox.render(ctx);

            nk_layout_row_static(ctx, 20, 1, 1);

            row("My Name:");
            nameBox.render(ctx);

//            row("Player Type:");//TODO: Add this feature later
//            nk_button_label(ctx, "Default");

            nk_layout_row_static(ctx, 75, 1, 1);
            nk_layout_row_dynamic(ctx, 40, 2);

            if (nk_button_label(ctx, "BACK")) {
                menu.setPage(Page.LOAD_WORLD);
            }
            if (nk_button_label(ctx, "CONTINUE")) {
                int fromPortVal = (int) fromPortBox.getValueAsNumber();
                int portVal = (int) portBox.getValueAsNumber();

                if (!Main.devMode) fromPortVal = portVal;

                String playerName = nameBox.getValueAsString();
                String ipAdress = this.ipAdressBox.getValueAsString();

                player.name = playerName; //Assign player name

                NetworkJoinRequest req = new NetworkJoinRequest(hosting, fromPortVal, portVal, playerName, ipAdress);
                System.out.println(req.toString());
                loadWorld.loadWorld(loadWorld.currentWorld, req);
            }
        }
        nk_end(ctx);
    }

    @Override
    public void onOpen() {
        nameBox.setValueAsString(player.name);
        if (hosting) {
            ipAdressBox.setValueAsString(ipAdress);
        } else {
            if (Main.devMode) {
                ipAdressBox.setValueAsString(ipAdress);
            } else ipAdressBox.setValueAsString("");
        }
    }

    public void row(String text) {
        nk_layout_row_dynamic(ctx, 30, 2);
//        Nuklear.nk_layout_
        nk_label(ctx, text, NK_TEXT_ALIGN_LEFT);
    }

}
