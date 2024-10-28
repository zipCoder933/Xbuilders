package com.xbuilders.engine.multiplayer;

import com.xbuilders.engine.MainWindow;
import com.xbuilders.engine.player.Player;
import com.xbuilders.engine.player.UserControlledPlayer;
import com.xbuilders.engine.utils.network.server.NetworkSocket;
import org.joml.Matrix4f;


/**
 * the Player Client class is a model of the other player.
 *
 */
public class PlayerClient extends NetworkSocket {
    public final Player player = new Player();
    public boolean wasWithinReach = false;
    public boolean isHost = false;

    /**
     * These changes are changes that WE have made but are sending to the other player
     */
    final public MultiplayerPendingBlockChanges blockChangesForPlayer;
    final public MultiplayerPendingEntityChanges entityChangesForPlayer;

    public PlayerClient() {
        blockChangesForPlayer = new MultiplayerPendingBlockChanges(this, player);
        entityChangesForPlayer = new MultiplayerPendingEntityChanges(this, player);
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

    long pingSendTime;
    long allChangesSentTime;

    public void sendAllChanges() {
        int c = blockChangesForPlayer.sendAllChanges();
        MainWindow.printlnDev("Sent all block changes (" + c + ")");
    }

    public void update(UserControlledPlayer user, Matrix4f projection, Matrix4f view) {
        //Check if the player is in range
        boolean inRange = player.isWithinReach(user);
        if (inRange) {
            player.update(projection, view);
        }
        if (inRange != wasWithinReach) {
            MainWindow.printlnDev("Player " + getName() + " " + (inRange ? "in" : "out") + " reach");
            wasWithinReach = inRange;
        }

        if (blockChangesForPlayer.periodicRangeSendCheck(2000)) { //Periodically send near changes
            int b = blockChangesForPlayer.sendNearBlockChanges();
            MainWindow.printlnDev("Sent " + b + " near block changes");
            return;
        }
        if (entityChangesForPlayer.periodicRangeSendCheck(3000)) { //Periodically send near changes
            int e = entityChangesForPlayer.sendNearEntityChanges();
            MainWindow.printlnDev("Sent " + e + " near entity changes");
            return;
        }
        //Periodically send All changes
        if (System.currentTimeMillis() - allChangesSentTime > 30000) {
            allChangesSentTime = System.currentTimeMillis();
            sendAllChanges();
        }
    }


    public void ping() {
        pingSendTime = System.currentTimeMillis();
    }
}
