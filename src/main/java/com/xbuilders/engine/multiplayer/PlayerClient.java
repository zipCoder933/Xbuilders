package com.xbuilders.engine.multiplayer;

import com.xbuilders.engine.player.Player;
import com.xbuilders.engine.player.UserControlledPlayer;
import com.xbuilders.engine.utils.network.server.NetworkSocket;
import com.xbuilders.game.Main;
import org.joml.Matrix4f;


/**
 * Player socket is a model of the other player.
 * The changes are changes that WE have made but are sending to the other player
 */
public class PlayerClient extends NetworkSocket {
    private Player player;
    public boolean wasWithinReach = false;
    public boolean isHost = false;
    //    public int playerChunkDistance; //So far this feature is not used
    public PendingBlockChanges blockChanges;
    public PendingEntityChanges entityChanges;


    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
        blockChanges = new PendingBlockChanges(this, player);
        entityChanges = new PendingEntityChanges(this, player);
    }

    public PlayerClient() {
    }

    public String getName() {
        String name = getHostAddress();
        if (getPlayer() != null && getPlayer().name != null) {
            name = getPlayer().name;
        }
        return name + (isHost ? " (host)" : "");
    }

    @Override
    public String toString() {
        return "PlayerSocket(" + getName() + '}';
    }

    public void update(UserControlledPlayer user, Matrix4f projection, Matrix4f view) {
        if (player == null) {
            return;
        }
        boolean inRange = getPlayer().isWithinReach(user);
        if (inRange) {
            getPlayer().update(projection, view);
        }

        if (blockChanges.periodicRangeSendCheck(2000)) { //Periodically send near changes
            blockChanges.sendNearBlockChanges();
            int c = entityChanges.sendNearEntityChanges();
            Main.printlnDev("Sent near changes ("+c+")");

        }else if(blockChanges.periodicSendAllCheck(30000)){ //If the player disconnects unexpectedly, we want to send all changes
            int c = blockChanges.sendAllChanges();
            Main.printlnDev(getName()+": all changes ("+c+")");
        }

        if (inRange != wasWithinReach) {
            Main.printlnDev("Player " + getName() + " " + (inRange ? "in" : "out") + " reach");
            wasWithinReach = inRange;
        }
    }
}
