package com.xbuilders.engine.game.model;

import com.xbuilders.engine.game.model.world.data.WorldData;

public interface GameSceneEvents {

    public void gameModeChangedEvent(GameMode gameMode);

    public void startGameEvent(WorldData world);

    public void stopGameEvent();
}
