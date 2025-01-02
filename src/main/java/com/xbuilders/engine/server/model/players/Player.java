/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.xbuilders.engine.server.model.players;

import com.xbuilders.engine.MainWindow;
import com.xbuilders.engine.client.player.UserControlledPlayer;
import com.xbuilders.engine.server.model.GameScene;
import com.xbuilders.engine.server.model.players.data.UserInfo;
import com.xbuilders.engine.server.multiplayer.MultiplayerPendingBlockChanges;
import com.xbuilders.engine.server.multiplayer.MultiplayerPendingEntityChanges;
import com.xbuilders.engine.utils.network.server.NetworkSocket;
import com.xbuilders.engine.utils.worldInteraction.collision.EntityAABB;
import org.joml.Matrix4f;
import org.joml.Vector3f;

/**
 * @author zipCoder933
 */
public class Player extends NetworkSocket {
    public final EntityAABB aabb;
    public final Vector3f worldPosition;
    public float pan, tilt;
    public final UserInfo userInfo;
    public boolean isKnown = false;

    public Player() {
        userInfo = new UserInfo(this);
        aabb = new EntityAABB();
        initAABB();
        worldPosition = aabb.worldPosition;
        //-------
        userInfo.setSkin(0);
        //-------
        model_blockChanges_ToBeSentToPlayer = new MultiplayerPendingBlockChanges(this, this);
        model_entityChanges_ToBeSentToPlayer = new MultiplayerPendingEntityChanges(this, this);
    }

    public boolean isWithinReach(float worldX, float worldY, float worldZ) {
        return worldPosition.distance(worldX, worldY, worldZ) < GameScene.world.getViewDistance();
    }

    public boolean isWithinReach(Player otherPlayer) {
        return worldPosition.distance(otherPlayer.worldPosition) < GameScene.world.getViewDistance();
    }


    public final static float PLAYER_HEIGHT = 1.5f;
    public final static float PLAYER_WIDTH = 0.7f;

    private void initAABB() {
        aabb.size.set(PLAYER_WIDTH, PLAYER_HEIGHT, PLAYER_WIDTH);
        aabb.offset.set(-(PLAYER_WIDTH * 0.5f), -0.15f, -(PLAYER_WIDTH * 0.5f));
    }


    public void update(Matrix4f projection, Matrix4f view) {
        if (userInfo.getSkin() != null) userInfo.getSkin().super_render(projection, view);
    }


    /* Network related stuff ----------------------------------------------------------- */
    public boolean wasWithinReach = false;
    public boolean isHost = false;
    boolean inRangeOfUser;

    /**
     * These changes are changes that WE have made but are sending to the other player
     */
    final public MultiplayerPendingBlockChanges model_blockChanges_ToBeSentToPlayer;
    final public MultiplayerPendingEntityChanges model_entityChanges_ToBeSentToPlayer;


    public String getName() {
        String name = "Unknown";
        if (userInfo.name != null) {
            name = userInfo.name;
        } else if (getSocket() != null) name = getHostAddress();

        if (isHost) name += " (Host)";
        if (this == GameScene.userPlayer) name += " (Me)";
        return name;
    }

    @Override
    public String toString() {
        return "Player " + getName();
    }

    long allChangesSentTime;

    public void sendAllChanges() {
        int c = model_blockChanges_ToBeSentToPlayer.sendAllChanges();
        MainWindow.printlnDev("Sent all block changes (" + c + ")");
    }

    /**
     * Our model needs to send changes to the other models
     */
    public void update(UserControlledPlayer user) {
        //Check if the player is in range
        inRangeOfUser = isWithinReach(user);
        if (inRangeOfUser != wasWithinReach) {
            MainWindow.printlnDev("Player " + getName() + " " + (inRangeOfUser ? "in" : "out") + " reach");
            wasWithinReach = inRangeOfUser;
        }

        if (model_blockChanges_ToBeSentToPlayer.periodicRangeSendCheck(2000)) { //Periodically send near changes
            int b = model_blockChanges_ToBeSentToPlayer.sendNearBlockChanges();
            MainWindow.printlnDev("Sent " + b + " near block changes");
            return;
        }
        if (model_entityChanges_ToBeSentToPlayer.periodicRangeSendCheck(3000)) { //Periodically send near changes
            int e = model_entityChanges_ToBeSentToPlayer.sendNearEntityChanges();
            MainWindow.printlnDev("Sent " + e + " near entity changes");
            return;
        }
        //Periodically send All changes
        if (System.currentTimeMillis() - allChangesSentTime > 30000) {
            allChangesSentTime = System.currentTimeMillis();
            sendAllChanges();
        }
    }

    public void drawPlayer(Matrix4f projection, Matrix4f view) {
        if (inRangeOfUser) {
            update(projection, view);
        }
    }

}
