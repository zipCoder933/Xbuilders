/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.xbuilders.engine.player;

import com.xbuilders.engine.MainWindow;
import com.xbuilders.engine.gameScene.GameScene;
import com.xbuilders.engine.multiplayer.GameServer;
import com.xbuilders.engine.utils.worldInteraction.collision.EntityAABB;
import org.joml.Matrix4f;
import org.joml.Vector3f;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * @author zipCoder933
 */
public class Player {

    public boolean isKnown = false;
    private Skin skin;
    private int skinID = 0;

    public Skin getSkin() {
        return skin;
    }

    public void setSkin(int id) {
        this.skin = MainWindow.game.availableSkins.get(id).getSkin(this);
        this.skinID = id;
    }


    public String name;
    public final EntityAABB aabb;
    public final Vector3f worldPosition;
    public float pan, tilt;
    final int nameStart = 2;

    public byte[] infoToBytes() throws IOException {
        byte[] data = new byte[name.length() + nameStart];  // Assuming color is a byte
        data[0] = GameServer.PLAYER_INFO;
        data[1] = (byte) skinID;

        for (int i = 0; i < name.length(); i++) {
            data[i + nameStart] = (byte) name.charAt(i);  // Casting char to byte
        }
        return data;
    }

    public void loadInfoFromBytes(byte[] data) {
        if (data.length < nameStart) {
            throw new IllegalArgumentException("Invalid data length");
        }

        //Load the skin
        setSkin(data[1]);

        //Load the name
        int nameLength = data.length - nameStart; // Subtracting 2 for color and header
        byte[] nameBytes = new byte[nameLength];
        System.arraycopy(data, nameStart, nameBytes, 0, nameLength);
        name = new String(nameBytes, StandardCharsets.UTF_8);
    }


    public boolean isWithinReach(float worldX, float worldY, float worldZ) {
        return worldPosition.distance(worldX, worldY, worldZ) < GameScene.world.getViewDistance();
    }

    public boolean isWithinReach(Player otherPlayer) {
        return worldPosition.distance(otherPlayer.worldPosition) < GameScene.world.getViewDistance();
    }


    final static float PLAYER_HEIGHT = 1.5f;
    final static float PLAYER_WIDTH = 0.7f;

    private void initAABB() {
        aabb.size.set(PLAYER_WIDTH, PLAYER_HEIGHT, PLAYER_WIDTH);
        aabb.offset.set(-(PLAYER_WIDTH * 0.5f), -0.15f, -(PLAYER_WIDTH * 0.5f));
    }

    public Player() {
        name = null;
        aabb = new EntityAABB();
        initAABB();
        worldPosition = aabb.worldPosition;
    }

    public void update(Matrix4f projection, Matrix4f view) {
        if (skin != null) skin.super_render(projection, view);
    }

    @Override
    public String toString() {
        return "Player{" + "name=" + name + '}';
    }


}
