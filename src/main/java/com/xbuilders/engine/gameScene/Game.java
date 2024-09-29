/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.xbuilders.engine.gameScene;

import com.xbuilders.engine.MainWindow;
import com.xbuilders.engine.items.Item;
import com.xbuilders.engine.items.block.Block;
import com.xbuilders.engine.player.SkinLink;
import com.xbuilders.engine.ui.gameScene.GameUI;
import com.xbuilders.engine.world.Terrain;
import com.xbuilders.engine.world.WorldInfo;
import org.lwjgl.nuklear.NkContext;
import org.lwjgl.nuklear.NkVec2;
import org.lwjgl.system.MemoryStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * @author zipCoder933
 */
public abstract class Game {

    public final MainWindow window;
    public final ArrayList<Terrain> terrainsList = new ArrayList<>();
    public final HashMap<Integer, SkinLink> availableSkins = new HashMap<>();

    public boolean releaseMouse() {
        return false;
    }

    long lastSaved;


    protected void update() {
        if (System.currentTimeMillis() - lastSaved > 10000) {
            lastSaved = System.currentTimeMillis();
            saveState();
        }
    }

    public Game(MainWindow window) {
        this.window = window;
    }

    public abstract void startGame(WorldInfo worldInfo);

    public abstract void initialize(GameScene gameScene) throws Exception;

    public final Terrain getTerrainFromInfo(WorldInfo info) {
        for (Terrain terrain : terrainsList) {
            if (terrain.name.equals(info.getTerrain())) {
                terrain.initForWorld(info.getSeed(), info.infoFile.terrainOptions, info.infoFile.terrainVersion);
                return terrain;
            }
        }
        return null;
    }

    public void uiInit(NkContext ctx, GameUI gameUI) {
    }

    public void uiDraw(MemoryStack stack) {

    }

    public boolean includeBlockIcon(Block block) {
        return false;
    }

    public Item getSelectedItem() {
        return null;
    }

    public abstract void saveState();

    public abstract boolean menusAreOpen();

    public abstract boolean uiMouseScrollEvent(NkVec2 scroll, double xoffset, double yoffset);

    public abstract boolean keyEvent(int key, int scancode, int action, int mods);

    public abstract boolean uiMouseButtonEvent(int button, int action, int mods);

    public abstract String handleCommand(String[] parts);

    public abstract Map<String, String> getCommandHelp();
}
