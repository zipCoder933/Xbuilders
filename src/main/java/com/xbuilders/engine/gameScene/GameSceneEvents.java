package com.xbuilders.engine.gameScene;

import com.xbuilders.engine.world.data.WorldData;

public interface GameSceneEvents {

    public void gameModeChangedEvent(GameMode gameMode);

    public void startGameEvent(WorldData world);

    public void stopGameEvent();
}
