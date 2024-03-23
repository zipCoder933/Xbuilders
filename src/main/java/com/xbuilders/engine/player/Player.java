/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.xbuilders.engine.player;

import com.xbuilders.engine.utils.ResourceUtils;
import com.xbuilders.engine.utils.UserID;
import com.xbuilders.engine.utils.math.AABB;
import com.xbuilders.engine.utils.network.PlayerServer;
import com.xbuilders.engine.utils.worldInteraction.collision.EntityAABB;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

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

    public byte[] infoToBytes() throws IOException {
        byte[] data = new byte[name.length() + 2];  // Assuming color is a byte
        data[0] = PlayerServer.PLAYER_INFO;
        data[1] = color;
        for (int i = 0; i < name.length(); i++) {
            data[i + 2] = (byte) name.charAt(i);  // Casting char to byte
        }
        return data;
    }

    public void loadInfoFromBytes(byte[] data) {
        if (data.length < 2) {
            throw new IllegalArgumentException("Invalid data length");
        }

        int nameLength = data.length - 2; // Subtracting 2 for color and header
        byte[] nameBytes = new byte[nameLength];
        System.arraycopy(data, 2, nameBytes, 0, nameLength);

        name = new String(nameBytes, StandardCharsets.UTF_8);
        color = data[1];
    }

    public void saveModel() throws IOException {
        Files.write(playerModelFile.toPath(), infoToBytes());
    }

    public Player() {
        name = null;
        aabb = new EntityAABB();
        worldPosition = aabb.worldPosition;
    }

    public Player(UserID user) throws IOException {
        aabb = new EntityAABB();
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

    @Override
    public String toString() {
        return "Player{" + "name=" + name + ", color=" + color + '}';
    }
}
