package com.xbuilders.engine.multiplayer;

import com.xbuilders.engine.player.Player;
import com.xbuilders.engine.player.UserControlledPlayer;
import com.xbuilders.engine.utils.network.server.NetworkSocket;
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
    public PlayerBlockPendingChanges blockChanges;


    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
        blockChanges = new PlayerBlockPendingChanges(this, player);
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
            int c = blockChanges.sendNearBlockChanges();
            System.out.println(getName()+": range changes (" + c + ")");
        }else if(blockChanges.periodicSendAllCheck(30000)){ //If the player disconnects unexpectedly, we want to send all changes
            int c = blockChanges.sendAllChanges();
            System.out.println(getName()+": all changes ("+c+")");
        }


        if (inRange != wasWithinReach) {
            System.out.println("Player " + getName() + " " + (inRange ? "in" : "out") + " reach");
            wasWithinReach = inRange;
        }
    }
}
