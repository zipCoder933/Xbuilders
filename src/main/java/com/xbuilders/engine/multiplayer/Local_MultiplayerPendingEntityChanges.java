package com.xbuilders.engine.multiplayer;

import com.xbuilders.engine.items.entity.EntityLink;
import com.xbuilders.engine.player.Player;
import com.xbuilders.engine.player.UserControlledPlayer;
import org.joml.Vector3f;

//This WILL be another class for 2 reasons
//1) It is less complicated this way
//2) the blockPipeline does not need to know anything aout syncing local entities
public class Local_MultiplayerPendingEntityChanges {
    UserControlledPlayer player;

    public Local_MultiplayerPendingEntityChanges(Player player) {

    }

    public void addEntityChange(int mode, EntityLink entity, long identifier, Vector3f currentPos, byte[] data) {
    }
}
