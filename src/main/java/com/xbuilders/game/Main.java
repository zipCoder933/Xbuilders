/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.xbuilders.game;

import com.xbuilders.engine.items.ItemList;
import com.xbuilders.engine.items.block.blockIconRendering.BlockIconRenderer;
import com.xbuilders.engine.settings.EngineSettings;
import com.xbuilders.engine.settings.EngineSettingsUtils;
import com.xbuilders.engine.ui.UIResources;
import com.xbuilders.engine.utils.ErrorHandler;
import com.xbuilders.engine.utils.ResourceUtils;
import com.xbuilders.engine.utils.math.MathUtils;
import com.xbuilders.window.developmentTools.MemoryGraph;
import com.xbuilders.window.developmentTools.MemoryProfiler;
import com.xbuilders.engine.gameScene.GameScene;
import com.xbuilders.engine.ui.topMenu.TopMenu;
import com.xbuilders.engine.utils.UserID;
import com.xbuilders.window.NKWindow;

import java.io.*;
import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.xbuilders.window.developmentTools.FrameTester;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.nuklear.NkVec2;

import javax.imageio.ImageIO;

public class Main extends NKWindow {

    public static String releaseVersion = "v1.0.0";

    public static void devPrintln(String message) {
        if(devMode) {
            System.out.println(message);
        }
    }



    public static FrameTester frameTester = new FrameTester("Game frame tester");
    public static FrameTester dummyTester = new FrameTester("");
    private static MemoryGraph memoryGraph; //Make this priviate because it is null by default


    // We can still have static variables, but we want to use dependency injection,
    // and make all classes ask for the object instead of directly acsessing it
    // In summary, we want ALL classes to be easily seprable, to make the code more
    // flexible.
    public static void goToGamePage() {
        isGameMode = true;
    }

    public static void goToMenuPage() {
        isGameMode = false;
        gameScene.closeGame(); //Close the entire game
    }

    public static EngineSettings settings;

    // We only need saving functionality to be public
    private final static EngineSettingsUtils settingsUtils = new EngineSettingsUtils();

    public static void saveSettings() {
        settingsUtils.save(settings);
    }

    private static boolean isGameMode = false;
    public static MyGame game;
    public static TopMenu topMenu;
    public static GameScene gameScene;
    public static UserID user;
    public static UIResources uiResources;

    File blockIconsDirectory = ResourceUtils.resource("items\\blocks\\icons");
    static boolean generateIcons = false;

    public static boolean fpsTools = false;
    public static boolean devMode = false;
    public static String name = "XBuilders";

    public static void main(String[] args) {
        System.out.println("XBuilders (" + releaseVersion + ") started on " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        try {
            System.out.println("args: " + Arrays.toString(args));

            String customAppData = null;

            for (String arg : args) {
                if (arg.equals("icons")) {
                    generateIcons = true;
                } else if (arg.equals("devmode")) {
                    devMode = true;
                    System.out.println("Dev mode enabled");
                } else if (arg.startsWith("appData")) {
                    customAppData = arg.split("=")[1];
                } else if (arg.startsWith("name")) {
                    name = arg.split("=")[1];
                }
            }

            dummyTester.setEnabled(false);
            if (fpsTools) {
                frameTester.setEnabled(true);
                frameTester.setStarted(true);
                frameTester.setUpdateTimeMS(1000);
                memoryGraph = new MemoryGraph();
            } else {
                frameTester.setEnabled(false);
            }
            ResourceUtils.initialize(devMode, customAppData);

            new Main();
        } catch (Exception ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public Main() throws Exception {
        super();
        settings = settingsUtils.load(devMode);
        user = new UserID(ResourceUtils.appDataResource("userID.txt"));
        System.out.println(user.toString());

        game = new MyGame();
        gameScene.setGame(game);
        topMenu = new TopMenu(this);
        gameScene = new GameScene(this);
        setMpfUpdateInterval(500);
        MemoryProfiler.setIntervalMS(500);

        //Create the window
        initGLFW();

        //Get the actual size of the screen
        int windowWidth = settings.smallWindow ? 600 : 920;
        int windowHeight = settings.smallWindow ? 650 : 720;

        if (settings.fullscreen) {
            int screenWidth = GLFW.glfwGetVideoMode(GLFW.glfwGetPrimaryMonitor()).width();
            int screenHeight = GLFW.glfwGetVideoMode(GLFW.glfwGetPrimaryMonitor()).height();
            windowWidth = (int) (screenWidth * MathUtils.clamp(settings.fullscreenSizeMultiplier, 0.4, 1));
            windowHeight = (int) (screenHeight * MathUtils.clamp(settings.fullscreenSizeMultiplier, 0.4, 1));
            System.out.println("FULLSCREEN MODE. Window size: " + windowWidth + "x" + windowHeight);
        }

        startWindow("XBuilders", settings.fullscreen, windowWidth, windowHeight);

//        GLFW.glfwSwapInterval(settings.vsync ? 1:0);//Disable vsync

        init();
        showWindow();
        System.out.println("Press 1 for System.GC()");

        while (!windowShouldClose()) {
            /* Input */
            beginScreenshot(); //If we want the frameTester to capture the entire frame length, we need to include startFrame() and endFrame()
            startFrame();

            frameTester.__startFrame();
            render();
            MemoryProfiler.update();
            if (memoryGraph != null) memoryGraph.update();
            frameTester.__endFrame();

            endFrame();//EndFrame takes the most time, becuase we have vsync turned on
            endScreenshot();

            if (devkeyF2_SystemCG) {
                System.out.println("System.GC()");
                System.gc();
                devkeyF2_SystemCG = false;
            }
        }
        terminate();
    }


    private void init() throws Exception {
        setIcon(ResourceUtils.resource("icon16.png").getAbsolutePath(),
                ResourceUtils.resource("icon32.png").getAbsolutePath(),
                ResourceUtils.resource("icon256.png").getAbsolutePath());
        ItemList.initialize();
        uiResources = new UIResources(this, ctx, settings.largerUI);
        game.initialize(this);

        gameScene.init(uiResources, game);
        topMenu.init(uiResources, GameScene.server.getIpAdress());

        if (generateIcons || !blockIconsDirectory.exists()) {
            firstTimeSetup();
        }
    }

    private void firstTimeSetup() throws InterruptedException {
        //Minimize the window
        GLFW.glfwHideWindow(getId());

        ErrorHandler.createPopupWindow("First time setup",
                "XBuilders is setting up. Please standby...");
        BlockIconRenderer iconRenderer = new BlockIconRenderer(
                ItemList.blocks.textures,
                blockIconsDirectory);

        iconRenderer.saveAllIcons();//Generate all icons

        settingsUtils.save(new EngineSettings(devMode));//Replace the old settings

        ErrorHandler.createPopupWindow("Finished",
                "XBuilders has finished setting up. Please restart the game to play.");
        Thread.sleep(5000);
        System.exit(0);
    }

    private void render() throws IOException {
        if (isGameMode) {
            gameScene.render();
        } else {
            topMenu.render();
        }
    }

    static DecimalFormat df = new DecimalFormat("####.00");

    private boolean screenshot = false;
    private boolean screenShotInitialized = false;

    protected void screenshot() {
        screenshot = true;
    }

    private void beginScreenshot() {
        if (screenshot) {
            screenShotInitialized = true;
        }
    }

    private void endScreenshot() {
        if (screenShotInitialized) {
            String formattedDateTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH-mm-ss"));
            File saveFile = ResourceUtils.appDataResource("screenshots\\" + formattedDateTime + ".png");
            System.out.println("Screenshot saved to: " + saveFile.getAbsolutePath());
            GameScene.alert("Screenshot saved to: " + saveFile.getAbsolutePath());
            try {
                saveFile.getParentFile().mkdirs();
                ImageIO.write(readPixelsOfWindow(), "png", saveFile);
            } catch (IOException e) {
                ErrorHandler.createPopupWindow("Error", "Could not save screenshot: " + e.getMessage());
            }
            screenShotInitialized = false;
            screenshot = false;
        }
    }

    @Override
    public void onMPFUpdate() {
        // Lower MPF is better. Since we are matching the FPS to the monitors refresh
        // rate, the FPS will not exceed 60fps.
        // Our goal is to get as close to 16.666 MPF (60 FPS) as possible
        String formattedNumber = df.format(getMsPerFrame());
        setTitle(name + "   mpf: " + formattedNumber + "    memory: " + MemoryProfiler.getMemoryUsageAsString());
    }


    @Override
    public void disposeEvent() {
        topMenu.disposeEvent();
    }

    @Override
    public void windowResizeEvent(int width, int height) {
        gameScene.windowResizeEvent(width, height);
    }

    public static boolean devkeyF3;
    public static boolean devkeyF4;
    public static boolean devkeyF2_SystemCG;
    public static boolean devkeyF1;
    public static boolean devkeyF12;

    @Override
    public void keyEvent(int key, int scancode, int action, int mods) {
        if (action == GLFW.GLFW_RELEASE) {
            if (devMode && key == GLFW.GLFW_KEY_F3) {
                devkeyF3 = !devkeyF3;
                System.out.println("Special mode (F3): " + devkeyF3);
            } else if (devMode && key == GLFW.GLFW_KEY_F4) {
                devkeyF4 = !devkeyF4;
                System.out.println("Special mode (F4): " + devkeyF4);
            } else if (devMode && key == GLFW.GLFW_KEY_F2) {
                devkeyF2_SystemCG = !devkeyF2_SystemCG;
                System.out.println("Special mode(F2): " + devkeyF2_SystemCG);
            } else if (devMode && key == GLFW.GLFW_KEY_F1) {
                devkeyF1 = !devkeyF1;
                System.out.println("Special mode (F1): " + devkeyF1);
            } else if (devMode && key == GLFW.GLFW_KEY_F12) {
                devkeyF12 = !devkeyF12;
                System.out.println("Light repropagation: " + devkeyF12);
            } else if (key == GLFW.GLFW_KEY_F11) {
                screenshot();
            }
        }
        if (isGameMode) {
            gameScene.keyEvent(key, scancode, action, mods);
        } else {
            topMenu.keyEvent();
        }
    }

    @Override
    public void mouseButtonEvent(int button, int action, int mods) {
        if (isGameMode) {
            gameScene.mouseButtonEvent(button, action, mods);
        }
    }

    @Override
    public void mouseScrollEvent(NkVec2 scroll, double xoffset, double yoffset) {
        if (isGameMode) {
            gameScene.mouseScrollEvent(scroll, xoffset, yoffset);
        }
    }
}
