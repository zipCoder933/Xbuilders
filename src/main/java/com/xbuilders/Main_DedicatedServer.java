package com.xbuilders;

import com.xbuilders.content.vanilla.XbuildersGame;
import com.xbuilders.engine.server.Server;

import static com.xbuilders.Main.VERSION;

public class Main_DedicatedServer {

    public static Server localServer;
    public static XbuildersGame game;

    public static void main(String[] args) {
        System.out.println("Server started: " + VERSION);
        game = new XbuildersGame();
//        localServer = new LocalServer(game, new World(), LocalClient.userPlayer);
    }
}
