package com.xbuilders;

import com.xbuilders.content.vanilla.XbuildersGame;
import com.xbuilders.engine.client.Client;
import com.xbuilders.engine.SkinRegistry;
import com.xbuilders.engine.server.Server;
import com.xbuilders.engine.common.utils.ErrorHandler;
import com.xbuilders.engine.common.resource.ResourceLister;

import java.util.Arrays;

/**
 * The client contains everything ONLY on the client
 * The server contains everything ONLY on the server
 * the Main class contains everything shared by both
 */
public class Main {


    public static final String VERSION = "1.7.2";

    private static Client localClient;
    private static Server localServer;

    public static XbuildersGame game;
    public static SkinRegistry skins;


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
        System.out.println("Client started: " + VERSION);
        ResourceLister.init();//This takes almost 10s, so it mind as well come first

        skins = new SkinRegistry();
        game = new XbuildersGame();

        localClient = new Client(args, VERSION, game);



        try {
            getClient().window.startWindowThread();
        } catch (Exception e) {
            ErrorHandler.createPopupWindow(
                    getClient().title + " has crashed",
                    getClient().title + " has crashed: \"" + (e.getMessage() != null ? e.getMessage() : "unknown error") + "\"\n\n" +
                            "Stack trace:\n" +
                            String.join("\n", Arrays.toString(e.getStackTrace()).split(",")) +
                            "\n\n Log saved to clipboard.");
            ErrorHandler.log(e, "Fatal Error");
        } finally {
            getClient().window.destroyWindow();
        }

    }

}
