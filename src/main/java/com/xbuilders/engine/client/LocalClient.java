package com.xbuilders.engine.client;

import com.xbuilders.engine.Client;
import com.xbuilders.engine.utils.resource.ResourceLister;
import com.xbuilders.engine.utils.resource.ResourceUtils;
import com.xbuilders.window.developmentTools.FrameTester;
import com.xbuilders.window.developmentTools.MemoryGraph;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;

public class LocalClient extends Client {
    public static long GAME_VERSION;
    public static boolean LOAD_WORLD_ON_STARTUP = false;
    public static boolean FPS_TOOLS = false;
    public static boolean DEV_MODE = false;
    static boolean generateIcons = false;

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

    public LocalClient(String[] args, String gameVersion) {
        ResourceLister.init();//This takes almost 10s, so it mind as well come first

        LocalClient.GAME_VERSION = versionStringToNumber(gameVersion);
        System.out.println("XBuilders (" + GAME_VERSION + ") started on " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));

        //Process args
        System.out.println("args: " + Arrays.toString(args));
        String appDataDir = null;
        String windowTitleBar = "XBuilders";

        for (String arg : args) {
            if (arg.equals("icons")) {
                generateIcons = true;
            } else if (arg.equals("devmode")) {
                DEV_MODE = true;
            } else if (arg.startsWith("appData")) {
                appDataDir = arg.split("=")[1];
            } else if (arg.startsWith("name")) {
                windowTitleBar = arg.split("=")[1];
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

        window = new ClientWindow(windowTitleBar);
    }
}
