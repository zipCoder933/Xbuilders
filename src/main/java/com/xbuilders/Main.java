package com.xbuilders;

import com.xbuilders.content.vanilla.XbuildersGame;
import com.xbuilders.engine.client.Client;
import com.xbuilders.engine.SkinRegistry;
import com.xbuilders.engine.server.Server;
import com.xbuilders.engine.common.utils.LoggingUtils;
import com.xbuilders.engine.common.resource.ResourceLister;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

/**
 * The client contains everything ONLY on the client
 * The server contains everything ONLY on the server
 * the Main class contains everything shared by both
 */
public class Main {


    private static Client localClient;
    private static Server localServer;

    public static XbuildersGame game;
    public static SkinRegistry skins;


    public static final Logger LOGGER = Logger.getLogger(Client.class.getName());

    static {
        try {
            FileHandler fileHandler = new FileHandler("latest.log", true);
            fileHandler.setFormatter(new SimpleFormatter());
            LOGGER.addHandler(fileHandler);
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error setting up file handler", e);
        }
    }


    public static Client getClient() {
        return localClient;
    }

    public static Server getServer() {
        return localServer; //TODO: If on a dedicated server, use dedicated server object instead
    }

    public static void setServer(Server server) {
        localServer = server;
    }


    public static void main(String[] args) throws Exception {
        ResourceLister.init();//This takes almost 10s, so it mind as well come first

        skins = new SkinRegistry();
        game = new XbuildersGame();

        localClient = new Client(args, game, LOGGER);


        try {
            getClient().window.startWindowThread();
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "XBuilders has crashed", e);
        } finally {
            getClient().window.destroyWindow();
        }

    }


    public static long versionStringToNumber(String version) {
        String[] parts = version.split("\\.");
        int major = Integer.parseInt(parts[0]);
        int minor = Integer.parseInt(parts[1]);
        int patch = Integer.parseInt(parts[2]);
        // Combine parts into a single number by shifting bits or scaling by powers of 1000.
        return (major * 1_000_000L) + (minor * 1_000L) + patch;
    }

}
