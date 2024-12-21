package com.xbuilders.engine.game.model.players;

import com.xbuilders.engine.MainWindow;
import com.xbuilders.engine.client.player.UserControlledPlayer;
import com.xbuilders.engine.game.multiplayer.MultiplayerPendingBlockChanges;
import com.xbuilders.engine.game.multiplayer.MultiplayerPendingEntityChanges;
import com.xbuilders.engine.utils.network.server.NetworkSocket;
import org.joml.Matrix4f;


/**
 * the Player Client class is a model of the other player.
 */
public class PlayerClient extends NetworkSocket {
    public final Player player = new Player();
    public boolean wasWithinReach = false;
    public boolean isHost = false;
    boolean inRangeOfUser;

    /**
     * These changes are changes that WE have made but are sending to the other player
     */
    final public MultiplayerPendingBlockChanges model_blockChanges_ToBeSentToPlayer;
    final public MultiplayerPendingEntityChanges model_entityChanges_ToBeSentToPlayer;

    public PlayerClient() {
        model_blockChanges_ToBeSentToPlayer = new MultiplayerPendingBlockChanges(this, player);
        model_entityChanges_ToBeSentToPlayer = new MultiplayerPendingEntityChanges(this, player);
    }

    public String getName() {
        String name = getHostAddress();
        if (player.userInfo.name != null) {
            name = player.userInfo.name;
        }
        return name + (isHost ? " (host)" : "");
    }

    @Override
    public String toString() {
        return "PlayerSocket " + getName();
    }

    long allChangesSentTime;

    public void sendAllChanges() {
        int c = model_blockChanges_ToBeSentToPlayer.sendAllChanges();
        MainWindow.printlnDev("Sent all block changes (" + c + ")");
    }

    /**
     * Our model needs to send changes to the other models
     *
     */
    public void update(UserControlledPlayer user) {
        //Check if the player is in range
        inRangeOfUser = player.isWithinReach(user);
        if (inRangeOfUser != wasWithinReach) {
            MainWindow.printlnDev("Player " + getName() + " " + (inRangeOfUser ? "in" : "out") + " reach");
            wasWithinReach = inRangeOfUser;
        }

        if (model_blockChanges_ToBeSentToPlayer.periodicRangeSendCheck(2000)) { //Periodically send near changes
            int b = model_blockChanges_ToBeSentToPlayer.sendNearBlockChanges();
            MainWindow.printlnDev("Sent " + b + " near block changes");
            return;
        }
        if (model_entityChanges_ToBeSentToPlayer.periodicRangeSendCheck(3000)) { //Periodically send near changes
            int e = model_entityChanges_ToBeSentToPlayer.sendNearEntityChanges();
            MainWindow.printlnDev("Sent " + e + " near entity changes");
            return;
        }
        //Periodically send All changes
        if (System.currentTimeMillis() - allChangesSentTime > 30000) {
            allChangesSentTime = System.currentTimeMillis();
            sendAllChanges();
        }
    }

    public void drawPlayer(Matrix4f projection, Matrix4f view) {
        if (inRangeOfUser) {
            player.update(projection, view);
        }
    }
}
