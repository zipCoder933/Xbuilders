package com.xbuilders.engine.server.players.data;

import com.xbuilders.Main;
import com.xbuilders.engine.client.ClientWindow;
import com.xbuilders.engine.client.player.Skin;
import com.xbuilders.engine.server.players.Player;

/**
 * User info encapsulates all information that the player needs to have to
 * 1. make a multiplayer connection
 * 2. identify the player
 * <p>
 * This information is independent of what world the player is in
 */
public class PlayerInfo {
    private Skin skin;
    private int skinID = 0;
    public String name;//Every name MUST be unique
    Player player;

    public PlayerInfo(Player player) {
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
        this.skin = Main.skins.get(id).get(player);
        this.skinID = id;
    }

    public void saveToDisk() {
        ClientWindow.settings.internal_playerName = name;//Save name in settings for
        ClientWindow.settings.internal_skinID = skinID;//Save name in settings for
    }

    public void loadFromDisk() {
        name = ClientWindow.settings.internal_playerName;
        skinID = ClientWindow.settings.internal_skinID;
    }
    //================================================================
}
