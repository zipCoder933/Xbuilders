package com.xbuilders;

import com.xbuilders.content.vanilla.XbuildersGame;
import com.xbuilders.engine.client.LocalClient;
import com.xbuilders.engine.SkinRegistry;
import com.xbuilders.engine.server.LocalServer;
import com.xbuilders.engine.utils.ErrorHandler;
import com.xbuilders.engine.utils.resource.ResourceLister;

import java.util.Arrays;

/**
 * The client contains everything ONLY on the client
 * The server contains everything ONLY on the server
 * the Main class contains everything shared by both
 */
public class Main {


    public static final String GAME_VERSION = "1.7.2";

    public static LocalClient localClient;
    public static LocalServer localServer;
    public static XbuildersGame game;
    public static SkinRegistry skins;

    public static void main(String[] args) throws Exception {
        System.out.println(GAME_VERSION);
        ResourceLister.init();//This takes almost 10s, so it mind as well come first

        skins = new SkinRegistry();
        game = new XbuildersGame();

        localClient = new LocalClient(args, GAME_VERSION, game);
        localServer = new LocalServer(game, localClient.world, LocalClient.userPlayer);

        if (localClient.generateIcons || !localClient.blockIconsDirectory.exists()) {
            localClient.firstTimeSetup();
        }


        try {
            localClient.window.startWindowThread();
        } catch (Exception e) {
            ErrorHandler.createPopupWindow(
                    localClient.title + " has crashed",
                    localClient.title + " has crashed: \"" + (e.getMessage() != null ? e.getMessage() : "unknown error") + "\"\n\n" +
                            "Stack trace:\n" +
                            String.join("\n", Arrays.toString(e.getStackTrace()).split(",")) +
                            "\n\n Log saved to clipboard.");
            ErrorHandler.log(e, "Fatal Error");
        } finally {
            localClient.window.destroyWindow();
        }

    }

}
