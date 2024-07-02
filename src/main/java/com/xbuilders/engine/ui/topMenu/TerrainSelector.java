/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.xbuilders.engine.ui.topMenu;

import com.xbuilders.engine.world.Terrain;
import java.util.ArrayList;
import org.lwjgl.nuklear.NkContext;
import static org.lwjgl.nuklear.Nuklear.nk_button_label;

/**
 *
 * @author zipCoder933
 */
public class TerrainSelector {

    public TerrainSelector(ArrayList<Terrain> terrainList, NkContext ctx) {
        this.terrainList = terrainList;
        this.ctx = ctx;
    }

    private NkContext ctx;
    private ArrayList<Terrain> terrainList;
    private int selectedTerrain = 0;

    public Terrain getSelectedTerrain() {
        return terrainList.get(selectedTerrain);
    }

    public void reset() {
        selectedTerrain = 0;
    }

    public void draw() {
        if (nk_button_label(ctx, terrainList.get(selectedTerrain).name)) {
            selectedTerrain++;
            if (selectedTerrain >= terrainList.size()) {
                selectedTerrain = 0;
            }
        }
    }
}
