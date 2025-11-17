package com.tessera.engine.server;

import com.tessera.engine.server.world.data.WorldData;

public interface GameSceneEvents {

    public void gameModeChangedEvent(GameMode gameMode);

    public void startGameEvent(WorldData world);

    public void stopGameEvent();
}
