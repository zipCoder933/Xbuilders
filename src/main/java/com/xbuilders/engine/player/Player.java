/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.xbuilders.engine.player;

import com.xbuilders.engine.gameScene.GameScene;
import com.xbuilders.engine.utils.ResourceUtils;
import com.xbuilders.engine.utils.UserID;
import com.xbuilders.engine.multiplayer.GameServer;
import com.xbuilders.engine.utils.worldInteraction.collision.EntityAABB;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

import com.xbuilders.game.Main;
import org.joml.Matrix4f;
import org.joml.Vector3f;

/**
 * @author zipCoder933
 */
public class Player {

    public final static boolean simulatedServer = true;
    public Skin skin;
    private static File playerModelFile;
    public String name;
    public byte color;
    public final EntityAABB aabb;
    public final Vector3f worldPosition;
    public float pan, tilt;
    public boolean initialized = false;


    final int nameStart = 2;

    public byte[] infoToBytes() throws IOException {
        byte[] data = new byte[name.length() + nameStart];  // Assuming color is a byte
        data[0] = GameServer.PLAYER_INFO;
        data[1] = color;

        for (int i = 0; i < name.length(); i++) {
            data[i + nameStart] = (byte) name.charAt(i);  // Casting char to byte
        }
        return data;
    }

    public void loadInfoFromBytes(byte[] data) {
        if (data.length < nameStart) {
            throw new IllegalArgumentException("Invalid data length");
        }
        color = data[1];

        //Load the name
        int nameLength = data.length - nameStart; // Subtracting 2 for color and header
        byte[] nameBytes = new byte[nameLength];
        System.arraycopy(data, nameStart, nameBytes, 0, nameLength);
        name = new String(nameBytes, StandardCharsets.UTF_8);
    }

    public void saveModel() throws IOException {
        Files.write(playerModelFile.toPath(), infoToBytes());
    }

    public boolean isWithinReach(float worldX, float worldY, float worldZ) {
        return worldPosition.distance(worldX, worldY, worldZ) < GameScene.world.getViewDistance();
    }

    public boolean isWithinReach(Player otherPlayer) {
        return worldPosition.distance(otherPlayer.worldPosition) < GameScene.world.getViewDistance();
    }

    public void init() {
        skin = Main.game.availableSkins.get(0).get(this);
        initialized = true;
    }

    final static float PLAYER_HEIGHT = 1.5f;
    final static float PLAYER_WIDTH = 0.8f;

    public Player() {
        name = null;
        aabb = new EntityAABB();
        aabb.size.set(PLAYER_WIDTH, PLAYER_HEIGHT, PLAYER_WIDTH);
        aabb.offset.set(-(PLAYER_WIDTH * 0.5f), 0, -(PLAYER_WIDTH * 0.5f));

        worldPosition = aabb.worldPosition;
    }

    public Player(UserID user) throws IOException {
        aabb = new EntityAABB();
        aabb.size.set(PLAYER_WIDTH, PLAYER_HEIGHT, PLAYER_WIDTH);
        aabb.offset.set(-(PLAYER_WIDTH * 0.5f), 0, -(PLAYER_WIDTH * 0.5f));

        worldPosition = aabb.worldPosition;
        if (playerModelFile == null) {
            playerModelFile = ResourceUtils.appDataResource("playerModel.bin");
        }

        if (playerModelFile.exists()) {
            loadInfoFromBytes(Files.readAllBytes(playerModelFile.toPath()));
        } else {
            name = user.userName;
            color = 0;
            saveModel();
        }
    }

    public void update(Matrix4f projection, Matrix4f view) {
        if (!initialized) {
            init();
        }
        skin.init(projection, view);
        skin.render();
    }

    @Override
    public String toString() {
        return "Player{" + "name=" + name
                + ", color=" + color;
    }


}
