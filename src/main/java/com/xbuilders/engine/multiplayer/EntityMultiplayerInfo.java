package com.xbuilders.engine.multiplayer;

import com.xbuilders.engine.gameScene.GameScene;
import com.xbuilders.engine.items.entity.Entity;
import org.joml.Vector3f;

import java.util.Arrays;

public class EntityMultiplayerInfo {

    public long lastUpdateTime;
    private boolean stateChanged;
    Entity e;

    private final Vector3f lastMovementForChecking = new Vector3f();
    byte[] lastState;
    long lastStateCheckTime;
    long stateCheckInterval;

    public EntityMultiplayerInfo(Entity e) {
        this.e = e;
    }

    public boolean shouldImmediatelySendState() {
        //TODO: Should we be checking if WE are close to the entity or if the player we will send this to is?
        return e.distToPlayer < 8 || e.playerIsRidingThis();
    }

    public boolean sendState() {
        if (stateChanged && GameScene.server.isPlayingMultiplayer()) {
            stateChanged = false;
            lastUpdateTime = System.currentTimeMillis();
            GameScene.server.addEntityChange(e, GameServer.ENTITY_UPDATED, shouldImmediatelySendState());
            return false;
        }
        return false;
    }

    public void checkForStateChange() {
        if (lastMovementForChecking.distance(e.worldPosition) > 0.05f) { //We need a little space, because otherwise we will be sending the state all the time
            lastMovementForChecking.set(e.worldPosition);
            markStateChanged();
        }

        //Check if the state has changed by comparing the last state bytes to the current state bytes
        stateCheckInterval = shouldImmediatelySendState() ? 10 : 1000;
        if (
                !isStateChanged()  //If the state hasn't already been changed
                        && GameScene.server.isPlayingMultiplayer() //And we are in multiplayer
                        //We only check the state every X seconds for performance
                        && (System.currentTimeMillis() - lastStateCheckTime > stateCheckInterval)
        ) {
            lastStateCheckTime = System.currentTimeMillis();
            byte[] newState = e.stateToBytes();
            if (!Arrays.equals(lastState, newState)) markStateChanged();
            lastState = newState;
        }
    }

    public void markStateChanged() {
        stateChanged = true;
    }

    public boolean isStateChanged() {
        return stateChanged;
    }

    public void updateState(byte[] state, Vector3f newPosition) {
        if(e.playerIsRidingThis()) return;//Don't update the state if the entity is being controlled by the player
        e.loadState(state);
        e.worldPosition.set(newPosition);
    }
}
