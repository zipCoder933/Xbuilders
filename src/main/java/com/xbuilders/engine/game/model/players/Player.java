/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.xbuilders.engine.game.model.players;

import com.xbuilders.engine.game.model.GameScene;
import com.xbuilders.engine.game.model.players.data.UserInfo;
import com.xbuilders.engine.utils.worldInteraction.collision.EntityAABB;
import org.joml.Matrix4f;
import org.joml.Vector3f;

/**
 * @author zipCoder933
 */
public class Player {
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

    @Override
    public String toString() {
        return "Player{" + "name=" + userInfo.name + '}';
    }


}
