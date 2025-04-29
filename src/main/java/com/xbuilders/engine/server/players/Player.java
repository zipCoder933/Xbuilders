/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.xbuilders.engine.server.players;

import com.xbuilders.engine.client.ClientWindow;
import com.xbuilders.engine.client.LocalClient;
import com.xbuilders.engine.client.player.UserControlledPlayer;
import com.xbuilders.engine.server.players.data.PlayerInfo;
import com.xbuilders.engine.common.network.old.server.NetworkSocket;
import com.xbuilders.engine.common.worldInteraction.collision.EntityAABB;
import org.joml.Matrix4f;
import org.joml.Vector3f;

/**
 * @author zipCoder933
 */
public class Player extends NetworkSocket {
    public final EntityAABB aabb;
    public final Vector3f worldPosition;
    public float pan, tilt;
    public final PlayerInfo userInfo;
    public boolean isKnown = false;

    public Player() {
        userInfo = new PlayerInfo(this);
        aabb = new EntityAABB();
        initAABB();
        worldPosition = aabb.worldPosition;
        //-------
        userInfo.setSkin(0);
    }

    public boolean isWithinReach(float worldX, float worldY, float worldZ) {
        return worldPosition.distance(worldX, worldY, worldZ) < LocalClient.world.getViewDistance();
    }

    public boolean isWithinReach(Player otherPlayer) {
        return worldPosition.distance(otherPlayer.worldPosition) < LocalClient.world.getViewDistance();
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



    public String getName() {
        String name = "Unknown";
        if (userInfo.name != null) {
            name = userInfo.name;
        } else if (getSocket() != null) name = getHostAddress();

        if (isHost) name += " (Host)";
        if (this == LocalClient.userPlayer) name += " (Me)";
        return name;
    }

    @Override
    public String toString() {
        return "Player " + getName();
    }


    public void drawPlayer(Matrix4f projection, Matrix4f view) {
        if (inRangeOfUser) {
            update(projection, view);
        }
    }

}
