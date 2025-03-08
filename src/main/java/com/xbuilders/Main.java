package com.xbuilders;

import com.xbuilders.engine.client.ClientWindow;
import com.xbuilders.engine.utils.ResourceLoader;

import java.io.IOException;

public class Main {
    public static final String GAME_VERSION = "1.6.0";

    public static void main(String[] args) {
        ResourceLoader resourceLoader = new ResourceLoader();
        if (resourceLoader.getResourceAsStream("/icon16.png") == null) {
            System.out.println("NO RESOURCES");
            return;
        }else System.out.println("RESOURCES");
        try {
            System.out.println("JAR RESOURCE: \"" + new String(resourceLoader.readResource("/data/xbuilders/items/"))+"\"");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        System.out.println("JAR LIST FILES: " + resourceLoader.listResourceFiles("/"));
        new ClientWindow(args);
    }
}
