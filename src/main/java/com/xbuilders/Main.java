package com.xbuilders;

import com.xbuilders.engine.client.LocalClient;

public class Main {
    public static final String GAME_VERSION = "1.6.0";
    public static LocalClient localClient;

    public static void main(String[] args) {
                
        localClient = new LocalClient(args, GAME_VERSION);
    }
}
