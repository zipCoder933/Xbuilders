package com.xbuilders.engine.server;

import com.xbuilders.engine.common.world.WorldData;

public interface GameSceneEvents {

    public void gameModeChangedEvent(GameMode gameMode);

    public void startGameEvent(WorldData world);

    public void stopGameEvent();
}
