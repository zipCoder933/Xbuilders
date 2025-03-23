package com.xbuilders;

import com.xbuilders.engine.client.LocalClient;
import com.xbuilders.engine.server.LocalServer;
import com.xbuilders.engine.utils.resource.ResourceLister;

import java.io.IOException;
import java.net.URISyntaxException;

public class Main {
    public static final String GAME_VERSION = "1.7.2";
    public static LocalClient localClient;
    public static LocalServer localServer;

    public static void main(String[] args) throws IOException, URISyntaxException {
        System.out.println(GAME_VERSION);
        ResourceLister.init();//This takes almost 10s, so it mind as well come first
        localServer = new LocalServer();
        localClient = new LocalClient(args, GAME_VERSION);
    }

}
