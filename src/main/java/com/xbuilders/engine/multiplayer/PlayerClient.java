package com.xbuilders.engine.multiplayer;

import com.xbuilders.engine.MainWindow;
import com.xbuilders.engine.player.Player;
import com.xbuilders.engine.player.UserControlledPlayer;
import com.xbuilders.engine.utils.network.server.NetworkSocket;
import org.joml.Matrix4f;


/**
 * Player socket is a model of the other player.
 * The changes are changes that WE have made but are sending to the other player
 */
public class PlayerClient extends NetworkSocket {
    public final Player player = new Player();
    public boolean wasWithinReach = false;
    public boolean isHost = false;
    //    public int playerChunkDistance; //So far this feature is not used
    public PendingBlockChanges blockChanges;
    public PendingEntityChanges entityChanges;

    public void initPlayer(byte[] receivedData) {
        player.loadInfoFromBytes(receivedData);
        blockChanges = new PendingBlockChanges(this, player);
        entityChanges = new PendingEntityChanges(this, player);
    }

    public PlayerClient() {
    }

    public String getName() {
        String name = getHostAddress();
        if (player.name != null) {
            name = player.name;
        }
        return name + (isHost ? " (host)" : "");
    }

    @Override
    public String toString() {
        return "PlayerSocket(" + getName() + '}';
    }

    public void update(UserControlledPlayer user, Matrix4f projection, Matrix4f view) {
        boolean inRange = player.isWithinReach(user);
        if (inRange) {
            player.update(projection, view);
        }

        if (blockChanges.periodicRangeSendCheck(2000)) { //Periodically send near changes
            int b = blockChanges.sendNearBlockChanges();
            MainWindow.printlnDev("Sent " + b + " near block changes");
        } else if (blockChanges.periodicSendAllCheck(30000)) { //If the player disconnects unexpectedly, we want to send all changes
            int c = blockChanges.sendAllChanges();
            MainWindow.printlnDev("Sent all block changes (" + c + ")");
        }

        if (entityChanges.periodicRangeSendCheck(3000)) { //Periodically send near changes
            int e = entityChanges.sendNearEntityChanges();
            MainWindow.printlnDev("Sent " + e + " near entity changes");
        }

        if (inRange != wasWithinReach) {
            MainWindow.printlnDev("Player " + getName() + " " + (inRange ? "in" : "out") + " reach");
            wasWithinReach = inRange;
        }
    }

    long pingSendTime;

    public void ping() {
        pingSendTime = System.currentTimeMillis();
    }
}
