package com.xbuilders.engine.player.data;

import com.xbuilders.engine.MainWindow;
import com.xbuilders.engine.multiplayer.GameServer;
import com.xbuilders.engine.player.Player;
import com.xbuilders.engine.player.Skin;
import com.xbuilders.engine.utils.ResourceUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

/**
 * User info encapsulates all information that the player needs to have to
 * 1. make a multiplayer connection
 * 2. identify the player
 * <p>
 * This information is independent of what world the player is in
 */
public class UserInfo {
    private Skin skin;
    private int skinID = 0;
    public String name;
    Player player;

    public UserInfo(Player player) {
        this.player = player;
        name = System.getProperty("user.name");
    }

    public Skin getSkin() {
        return skin;
    }

    public int getSkinID() {
        return skinID;
    }

    public void setSkin(int id) {
        this.skin = MainWindow.game.availableSkins.get(id).getSkin(player);
        this.skinID = id;
    }

    final int NAME_START = 2;


    public byte[] toBytes() throws IOException {
        byte[] data = new byte[name.length() + NAME_START];  // Assuming color is a byte
        data[0] = GameServer.PLAYER_INFO;
        data[1] = (byte) skinID;

        for (int i = 0; i < name.length(); i++) {
            data[i + NAME_START] = (byte) name.charAt(i);  // Casting char to byte
        }
        return data;
    }

    public void fromBytes(byte[] data) {
        if (data.length < NAME_START) {
            throw new IllegalArgumentException("Invalid data length");
        }

        //Load the skin
        setSkin(data[1]);

        //Load the name
        int nameLength = data.length - NAME_START; // Subtracting 2 for color and header
        byte[] nameBytes = new byte[nameLength];
        System.arraycopy(data, NAME_START, nameBytes, 0, nameLength);
        name = new String(nameBytes, StandardCharsets.UTF_8);
    }

    public void saveToDisk() {
        byte[] data = null;
        try {
            data = toBytes();
            Files.write(ResourceUtils.PLAYER_GLOBAL_INFO.toPath(), data);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void loadFromDisk() {
        if (ResourceUtils.PLAYER_GLOBAL_INFO.exists() == false) return;
        try {
            byte[] data = Files.readAllBytes(ResourceUtils.PLAYER_GLOBAL_INFO.toPath());
            fromBytes(data);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    //================================================================
}
