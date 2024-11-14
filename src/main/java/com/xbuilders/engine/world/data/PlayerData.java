package com.xbuilders.engine.world.data;

/**
 * Information about the player in the current world that they are in.
 * For example, this contains the player position and inventory
 */
public class PlayerData {

    public final PlayerStuff playerStuff;

    public PlayerData() {
        playerStuff = new PlayerStuff(33);
    }
}
