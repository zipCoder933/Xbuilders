package com.xbuilders.engine.multiplayer;

import com.xbuilders.engine.gameScene.GameScene;
import com.xbuilders.engine.items.entity.Entity;
import com.xbuilders.game.Main;
import org.joml.Vector3f;

public class EntityMultiplayerInfo {

    public long lastUpdateTime;
    private boolean stateChanged;
    Entity e;
    public Vector3f lastPosition = new Vector3f(); //The last position is the last world position sent or received
    private Vector3f lastMovementForChecking = new Vector3f();

    public EntityMultiplayerInfo(Entity e) {
        this.e = e;
    }

    public boolean sendState() {
        if (stateChanged && GameScene.server.isPlayingMultiplayer()) {
            stateChanged = false;
            lastUpdateTime = System.currentTimeMillis();

            GameScene.server.addEntityChange(e, GameServer.ENTITY_UPDATED,
                    e.distToPlayer < 10 || e.playerIsRidingThis());
            Main.printlnDev("Entity update: " + e);
            return false;
        }
        return false;
    }

    public void checkForStateChange() {
//        if (lastMovementForChecking.distance(e.worldPosition) > 0.05f) {
//            lastMovementForChecking.set(e.worldPosition);
//            markStateChanged();
//        }
//        //Check if the state has changed
//        if (!isStateChanged() && GameScene.server.isPlayingMultiplayer()) {
//            if (e.hasStateChanged()) markStateChanged();
//        }
    }

    public void markStateChanged() {
        stateChanged = true;
    }

    public boolean isStateChanged() {
        return stateChanged;
    }

    public void updateState(byte[] state, Vector3f newPosition) {
        e.loadState(state);
        lastPosition.set(newPosition);
        e.worldPosition.set(newPosition);
    }
}
