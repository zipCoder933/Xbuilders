package com.xbuilders.engine.settings;

import com.xbuilders.engine.world.World;
import com.xbuilders.engine.world.chunk.Chunk;

public class EngineSettings {
    public boolean game_switchMouseButtons = false;
    public int game_cursorRayDist = 128;
    public boolean video_fullscreen = true;
    public final BoundedFloat video_fullscreenSize = new BoundedFloat(0.95f);
    public boolean video_vsync = true;
    public boolean video_largerUI = true;
    public boolean game_fixLiquidMesh= false;

    //TODO: If there are settings we dont want acesssable in UI, we can put them into a "hidden settings" class
    public boolean internal_smallWindow = false;
    public final BoundedInt internal_viewDistance = new BoundedInt(Chunk.WIDTH * 5);

    public EngineSettings initVariables() {
        internal_viewDistance.setBounds(World.VIEW_DIST_MIN, World.VIEW_DIST_MAX);
        video_fullscreenSize.setBounds(0.5f, 1.0f);
        video_fullscreenSize.clamp();
        return this;
    }
}
