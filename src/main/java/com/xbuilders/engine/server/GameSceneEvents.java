package com.xbuilders.engine.server;

import com.xbuilders.engine.server.world.data.WorldData;

public interface GameSceneEvents {

    public void gameModeChangedEvent(GameMode gameMode);

    public void startGameEvent(WorldData world);

    public void stopGameEvent();
}
