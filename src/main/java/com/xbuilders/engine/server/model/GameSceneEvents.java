package com.xbuilders.engine.server.model;

import com.xbuilders.engine.server.model.world.data.WorldData;

public interface GameSceneEvents {

    public void gameModeChangedEvent(GameMode gameMode);

    public void startGameEvent(WorldData world);

    public void stopGameEvent();
}
