/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.xbuilders.engine.server.model;

import com.xbuilders.engine.MainWindow;
import com.xbuilders.engine.server.model.players.SkinSupplier;
import com.xbuilders.engine.client.visuals.ui.gameScene.GameUI;
import com.xbuilders.engine.server.model.world.Terrain;
import com.xbuilders.engine.server.model.world.data.WorldData;
import org.lwjgl.nuklear.NkContext;
import org.lwjgl.nuklear.NkVec2;
import org.lwjgl.system.MemoryStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * @author zipCoder933
 */
public abstract class Game implements GameSceneEvents {

    public final MainWindow window;
    public final ArrayList<Terrain> terrainsList = new ArrayList<>();
    public final HashMap<Integer, SkinSupplier> availableSkins = new HashMap<>();

    public boolean releaseMouse() {
        return false;
    }


    public Game(MainWindow window) {
        this.window = window;
    }

    public abstract void setup(GameScene gameScene, NkContext ctx, GameUI gameUI) throws Exception;

    public final Terrain getTerrainFromInfo(WorldData info) {
        for (Terrain terrain : terrainsList) {
            if (terrain.name.equals(info.getTerrain())) {
                terrain.initForWorld(info.getSeed(), info.data.terrainOptions, info.data.terrainVersion);
                return terrain;
            }
        }
        return null;
    }

    public void uiInit(NkContext ctx, GameUI gameUI) {
    }

    public boolean uiDraw(MemoryStack stack) {
        return false;
    }


    public abstract boolean menusAreOpen();

    public abstract boolean uiMouseScrollEvent(NkVec2 scroll, double xoffset, double yoffset);

    public abstract boolean keyEvent(int key, int scancode, int action, int mods);

    public abstract boolean uiMouseButtonEvent(int button, int action, int mods);

    public abstract String handleCommand(String[] parts);

    public abstract Map<String, String> getCommandHelp();

}