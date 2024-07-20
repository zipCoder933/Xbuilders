package com.xbuilders.engine.settings;

import com.xbuilders.engine.world.World;
import com.xbuilders.engine.world.chunk.Chunk;
import com.xbuilders.game.Main;

public class EngineSettings {

    public static boolean shouldReset() {
        return false;
    }

    public boolean switchMouseButtons = false;
    public boolean fullscreen = true;
    public final BoundedFloat fullscreenSizeMultiplier = new BoundedFloat(0.95f);
    public int maxCursorRaycastDist = 50;
    public boolean largerUI = true;

    //TODO: If there are settings we dont want acesssable in UI, we can put them into a "hidden settings" class
    public boolean smallWindow = false;
    public final BoundedInt viewDistance = new BoundedInt(Chunk.WIDTH * 5);

    public EngineSettings initVariables() {
        if (Main.devMode) {
            fullscreen = false;
            largerUI = false;
            smallWindow = true;
        }

        viewDistance.setBounds(World.VIEW_DIST_MIN, World.VIEW_DIST_MAX);
        fullscreenSizeMultiplier.setBounds(0.5f, 1.0f);
        fullscreenSizeMultiplier.clamp();
        return this;
    }
}
