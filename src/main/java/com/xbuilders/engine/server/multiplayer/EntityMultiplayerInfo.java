package com.xbuilders.engine.server.multiplayer;

import com.xbuilders.engine.server.model.GameScene;
import com.xbuilders.engine.server.model.items.entity.Entity;
import org.joml.Vector3f;

import java.util.concurrent.atomic.AtomicInteger;

public class EntityMultiplayerInfo {

    public long lastUpdateTime;
    private boolean stateChanged;
    Entity e;
    public boolean controlMode;

    //Is a COMPLETELY decentralized way the best idea? If we can assign players to control entities they should be the only ones sending the state
    //When we have everyone sending states to each other, those states can contradict each other.
    //We have to find a way to take ownership and prioritize ownership  in certain circumstances,
    //For example, If I am riding and entity, that takes presence over being close to, or feeding the entity

    /**
     * We need a flag to determine if we should be the sole controller of this entity.
     * If we are, no other players are to send the state of it
     *
     * @return true if we are the sole controller
     */
    public boolean controlledByUs() {
        return e.playerIsRidingThis() || controlMode;
    }

    public boolean controlledByAnotherPlayer;

    public EntityMultiplayerInfo(Entity e) {
        this.e = e;
    }


//      We only want to mark the state as changed if WE moved the entity, not the other player
//      Previously, the other player would move the entity and this method would fire,

    public boolean checkAndSendState() {
        if (!GameScene.server.isPlayingMultiplayer() ||
                controlledByAnotherPlayer)  //We wont send state if we are controlled by another player
            return false;

//        if (lastWorldPos.distance(e.worldPosition) > 0.05f) { //We need a little space, because otherwise we will be sending the state all the time
//            lastWorldPos.set(e.worldPosition);
//            markStateChanged();
//        }
//        //Check if the state has changed by comparing the last state bytes to the current state bytes
//        stateCheckInterval = ownsThisEntityState() ? 20 : 1000;
//        if (
//                !isStateChanged()  //If the state hasn't already been changed
//                        && GameScene.server.isPlayingMultiplayer() //And we are in multiplayer
//                        //We only check the state every X seconds for performance
//                        && (System.currentTimeMillis() - lastStateCheckTime > stateCheckInterval)
//        ) {
//            lastStateCheckTime = System.currentTimeMillis();
//            byte[] newState = e.stateToBytes();
//            if (!Arrays.equals(lastState, newState)) markStateChanged();
//            lastState = newState;
//        }
////            wasChangedByThisPlayer = false;
////        }

        if (stateChanged) {
            boolean sendStateQuickly = e.distToPlayer < 8 || controlledByUs();

            stateChanged = false;
            lastUpdateTime = System.currentTimeMillis();
            GameScene.server.addEntityChange(e, GameServer.ENTITY_UPDATED, sendStateQuickly);
            return false;
        }
        return false;
    }


    public void markStateChanged() {
        stateChanged = true;
    }

    public boolean isStateChanged() {
        return stateChanged;
    }

    public void updateState(byte[] state, Vector3f newPosition, boolean controlledByAnotherPlayer) {
        this.controlledByAnotherPlayer = controlledByAnotherPlayer;

        if (controlledByUs()) return;

        e.entityState_read(state, new AtomicInteger(0));
        e.worldPosition.set(newPosition);
    }
}