/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.xbuilders.engine.server;

import com.xbuilders.engine.client.ClientWindow;
import com.xbuilders.engine.server.players.SkinSupplier;
import com.xbuilders.engine.client.visuals.gameScene.GameUI;
import com.xbuilders.engine.server.world.Terrain;
import com.xbuilders.engine.server.world.data.WorldData;
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

    public final ClientWindow window;
    public final ArrayList<Terrain> terrainsList = new ArrayList<>();
    public final HashMap<Integer, SkinSupplier> availableSkins = new HashMap<>();

    public boolean releaseMouse() {
        return false;
    }

    public Game(ClientWindow window) {
        this.window = window;
    }

    public abstract void setup(Server gameScene, NkContext ctx, GameUI gameUI) throws Exception;

    public final Terrain getTerrainFromInfo(WorldData info) {
        for (Terrain terrain : terrainsList) {
            if (terrain.name.equals(info.getTerrain())) {
                terrain.initForWorld(info.getSeed(), info.data.terrainOptions, info.data.terrainVersion);
                return terrain;
            }
        }
        return null;
    }

    public boolean uiDraw(MemoryStack stack) {
        return false;
    }

    public abstract boolean menusAreOpen();

    public abstract boolean uiMouseScrollEvent(NkVec2 scroll, double xoffset, double yoffset);

    public abstract boolean keyEvent(int key, int scancode, int action, int mods);

    public abstract boolean uiMouseButtonEvent(int button, int action, int mods);

}
