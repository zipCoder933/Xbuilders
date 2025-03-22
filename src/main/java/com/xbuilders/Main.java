package com.xbuilders;

import com.xbuilders.engine.client.LocalClient;
import com.xbuilders.engine.server.LocalServer;

import java.io.IOException;
import java.net.URISyntaxException;

public class Main {
    public static final String GAME_VERSION = "1.6.0";
    public static LocalClient localClient;
    public static LocalServer localServer;

    public static void main(String[] args) throws IOException, URISyntaxException {
        System.out.println(GAME_VERSION);
        localServer = new LocalServer();
        localClient = new LocalClient(args, GAME_VERSION);
    }

}
