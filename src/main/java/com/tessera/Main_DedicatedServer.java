package com.tessera;

import com.tessera.content.vanilla.TesseraGame;
import com.tessera.engine.server.Server;

import static com.tessera.Main.VERSION;

public class Main_DedicatedServer {

    public static Server localServer;
    public static TesseraGame game;

    public static void main(String[] args) {
        System.out.println("Server started: " + VERSION);
        game = new TesseraGame();
//        localServer = new LocalServer(game, new World(), LocalClient.userPlayer);
    }
}
