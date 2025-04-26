package com.xbuilders.engine.client;

import com.xbuilders.engine.Client;
import com.xbuilders.engine.client.player.UserControlledPlayer;
import com.xbuilders.engine.client.settings.ClientSettings;
import com.xbuilders.engine.client.visuals.Page;
import com.xbuilders.engine.server.Game;
import com.xbuilders.engine.server.GameMode;
import com.xbuilders.engine.server.LocalServer;
import com.xbuilders.engine.server.Registrys;
import com.xbuilders.engine.server.item.blockIconRendering.BlockIconRenderer;
import com.xbuilders.engine.server.multiplayer.NetworkJoinRequest;
import com.xbuilders.engine.server.world.Terrain;
import com.xbuilders.engine.server.world.World;
import com.xbuilders.engine.server.world.WorldsHandler;
import com.xbuilders.engine.server.world.data.WorldData;
import com.xbuilders.engine.utils.ErrorHandler;
import com.xbuilders.engine.utils.progress.ProgressData;
import com.xbuilders.engine.utils.resource.ResourceUtils;
import com.xbuilders.window.developmentTools.FrameTester;
import com.xbuilders.window.developmentTools.MemoryGraph;
import org.lwjgl.glfw.GLFW;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;


public class LocalClient extends Client {
    public static final World world = new World();
    public static long GAME_VERSION;
    public static boolean LOAD_WORLD_ON_STARTUP = false;
    public static boolean FPS_TOOLS = false;
    public static boolean DEV_MODE = false;
    public static UserControlledPlayer userPlayer;
    public static LocalServer localServer;
    public boolean generateIcons = false;
    public final File blockIconsDirectory = ResourceUtils.file("items\\blocks\\icons");
    public static FrameTester frameTester = new FrameTester("Game frame tester");
    public static FrameTester dummyTester = new FrameTester("");
    static MemoryGraph memoryGraph; //Make this priviate because it is null by default
    public final ClientWindow window;


    public static long versionStringToNumber(String version) {
        String[] parts = version.split("\\.");
        int major = Integer.parseInt(parts[0]);
        int minor = Integer.parseInt(parts[1]);
        int patch = Integer.parseInt(parts[2]);
        // Combine parts into a single number by shifting bits or scaling by powers of 1000.
        return (major * 1_000_000L) + (minor * 1_000L) + patch;
    }

    public String title;

    public void consoleOut(String s) {
        window.gameScene.ui.infoBox.addToHistory(s);
    }

    public void pauseGame() {
        if (window.isFullscreen()) window.minimizeWindow();
        window.gameScene.ui.baseMenu.setOpen(true);
    }

    public LocalClient(String[] args, String gameVersion, Game game) throws Exception {
        LocalClient.GAME_VERSION = versionStringToNumber(gameVersion);
        System.out.println("XBuilders (" + GAME_VERSION + ") started on " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));

        //Process args
        System.out.println("args: " + Arrays.toString(args));
        String appDataDir = null;
        title = "XBuilders";

        for (String arg : args) {
            if (arg.equals("icons")) {
                generateIcons = true;
            } else if (arg.equals("devmode")) {
                DEV_MODE = true;
            } else if (arg.startsWith("appData")) {
                appDataDir = arg.split("=")[1];
            } else if (arg.startsWith("name")) {
                title = arg.split("=")[1];
            } else if (arg.equals("loadWorldOnStartup")) {
                LocalClient.LOAD_WORLD_ON_STARTUP = true;
            }
        }
        ResourceUtils.initialize(DEV_MODE, appDataDir);


        /**
         * Testers
         */
        if (!LocalClient.DEV_MODE) LocalClient.FPS_TOOLS = false;
        dummyTester.setEnabled(false);
        if (LocalClient.FPS_TOOLS) {
            frameTester.setEnabled(true);
            frameTester.setStarted(true);
            frameTester.setUpdateTimeMS(1000);
            memoryGraph = new MemoryGraph();
        } else {
            frameTester.setEnabled(false);
        }

        window = new ClientWindow(title);
        window.init(game, world, this);
    }

    public void firstTimeSetup() throws InterruptedException {
        //Minimize the window
        GLFW.glfwHideWindow(window.getWindow());
        createPopupWindow("First time setup",
                "XBuilders is setting up. Please standby...");
        BlockIconRenderer iconRenderer = new BlockIconRenderer(
                Registrys.blocks.textures,
                blockIconsDirectory);

        iconRenderer.saveAllIcons();//Generate all icons
        new ClientSettings().save();
        createPopupWindow("Finished",
                "XBuilders has finished setting up. Please restart the game to play.");
        Thread.sleep(5000);
        System.exit(0);
    }

    public static void createPopupWindow(String title, String str) {
        final JFrame parent = new JFrame();
        JLabel label = new JLabel("");
        label.setText("<html><body style='padding:5px;'>" + str.replace("\n", "<br>") + "</body></html>");
        label.setFont(label.getFont().deriveFont(12f));
        label.setVerticalAlignment(JLabel.TOP);
        parent.add(label);
        parent.pack();
        parent.getContentPane().setBackground(Color.white);
        parent.setVisible(true);
        parent.pack();
        parent.setTitle(title);
        parent.setLocationRelativeTo(null);
        parent.setAlwaysOnTop(true);
        parent.setVisible(true);
        parent.setSize(350, 200);
    }

    public boolean makeNewWorld(String name, int size, Terrain terrain, int seed, GameMode gameMode) {
        try {
            WorldData info = new WorldData();
            info.makeNew(name, size, terrain, seed);
            info.data.gameMode = gameMode;
            if (WorldsHandler.worldNameAlreadyExists(info.getName())) {
                ClientWindow.popupMessage.message("Error", "World name \"" + info.getName() + "\" Already exists!");
                return false;
            } else WorldsHandler.makeNewWorld(info);
        } catch (IOException ex) {
            ClientWindow.popupMessage.message("Error", ex.getMessage());
            return false;
        }
        return true;
    }

    public void loadWorld(final WorldData world, NetworkJoinRequest req) {
        String title = "Loading World...";
        ProgressData prog = new ProgressData(title);
        window.topMenu.progress.enable(prog, () -> {//update
            try {
                LocalClient.localServer.startGameUpdateEvent(world, prog, req);
            } catch (Exception ex) {
                ErrorHandler.report(ex);
                prog.abort();
            }
        }, () -> {//finished
            ClientWindow.goToGamePage();
            window.topMenu.setPage(Page.HOME);
        }, () -> {//canceled
            System.out.println("Canceled");
            LocalClient.localServer.stopGameEvent(); //Stop the game
            window.topMenu.setPage(Page.HOME);
        });
    }
}
