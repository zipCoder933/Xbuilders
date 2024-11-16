/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.xbuilders.engine;

import com.xbuilders.engine.gameScene.GameScene;
import com.xbuilders.engine.items.Registrys;
import com.xbuilders.engine.items.item.blockIconRendering.BlockIconRenderer;
import com.xbuilders.engine.settings.EngineSettings;
import com.xbuilders.engine.ui.Theme;
import com.xbuilders.engine.ui.topMenu.PopupMessage;
import com.xbuilders.engine.ui.topMenu.TopMenu;
import com.xbuilders.engine.utils.ErrorHandler;
import com.xbuilders.engine.utils.ResourceUtils;
import com.xbuilders.game.XbuildersGame;
import com.xbuilders.window.GLFWWindow;
import com.xbuilders.window.NKWindow;
import com.xbuilders.window.developmentTools.FrameTester;
import com.xbuilders.window.developmentTools.MemoryGraph;
import com.xbuilders.window.developmentTools.MemoryProfiler;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWWindowFocusCallback;
import org.lwjgl.nuklear.NkVec2;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;

import static com.xbuilders.Main.GAME_VERSION;

public class MainWindow extends NKWindow {

    //==================================================================================================================
    //==================================================================================================================
    //==================================================================================================================
    //==================================================================================================================
    //==================================================================================================================
    //==================================================================================================================

    public static long numericalVersion;

    public static long versionStringToNumber(String version) {
        String[] parts = version.split("\\.");
        int major = Integer.parseInt(parts[0]);
        int minor = Integer.parseInt(parts[1]);
        int patch = Integer.parseInt(parts[2]);

        // Combine parts into a single number by shifting bits or scaling by powers of 1000.
        return (major * 1_000_000L) + (minor * 1_000L) + patch;
    }

    static {
        numericalVersion = versionStringToNumber(GAME_VERSION);
    }

    public static boolean loadWorldOnStartup = false;
    public static boolean fpsTools = false;
    public static int frameCount = 0; //TODO: replace with game tick

    public static void printlnDev(String message) {
        if (devMode) {
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
        gameScene.gameClosedEvent(); //Close the entire game
    }

    public static boolean isInGamePage() {
        return isGameMode;
    }

    public static EngineSettings settings;

    public void saveAndApplySettings() {
        settings.save();
        //Set vsync
        if (settings.video_vsync) {
            GLFW.glfwSwapInterval(1);
        } else {
            GLFW.glfwSwapInterval(0);
        }
        if (settings.video_fullscreen) {
            enableFullscreen(settings.video_fullscreenSize.value);
        } else disableFullscreen();

//        Theme.setUIScale(settings.video_largerUI);
    }

    private static boolean isGameMode = false;
    public static XbuildersGame game;
    public static TopMenu topMenu;
    public static GameScene gameScene;
    public static PopupMessage popupMessage;

    File blockIconsDirectory = ResourceUtils.resource("items\\blocks\\icons");
    static boolean generateIcons = false;


    public static boolean devMode = false;
    public static String title = "XBuilders";

    public MainWindow(String args[]) {
        super();
        System.out.println("XBuilders (" + numericalVersion + ") started on " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        System.out.println("args: " + Arrays.toString(args));

        String customAppData = null;

        //Process args
        for (String arg : args) {
            if (arg.equals("icons")) {
                generateIcons = true;
            } else if (arg.equals("devmode")) {
                devMode = true;
                System.out.println("Dev mode enabled");
            } else if (arg.startsWith("appData")) {
                customAppData = arg.split("=")[1];
            } else if (arg.startsWith("name")) {
                title = arg.split("=")[1];
            } else if (arg.equals("loadWorldOnStartup")) {
                loadWorldOnStartup = true;
            }
        }
        if (!devMode) fpsTools = false;

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

        try {
            init();
            if (settings.video_fullscreen) {
                enableFullscreen(settings.video_fullscreenSize.value);
            }
            saveAndApplySettings();   //DO THIS LAST Apply settings just in case the settings were not already applied
            showWindow();

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
            }
        } catch (Exception e) {
            ErrorHandler.createPopupWindow(title + " has crashed",
                    title + " has crashed: \"" + (e.getMessage() != null ? e.getMessage() : "unknown error") + "\"\n\n" +
                            "Stack trace:\n" +
                            String.join("\n", Arrays.toString(e.getStackTrace()).split(",")) +
                            "\n\n Log saved to clipboard.");
            ErrorHandler.log(e, "Fatal Error");
        } finally {
            destroyWindow();
        }
    }

    private void init() throws Exception {
        GLFWWindow.initGLFW();
        settings = EngineSettings.load();

        game = new XbuildersGame(this);
        popupMessage = new PopupMessage(ctx, this);
        topMenu = new TopMenu(this);
        gameScene = new GameScene(this, game);

        setMpfUpdateInterval(1000);
        MemoryProfiler.setIntervalMS(500);

        //Get the actual size of the screen
        int windowWidth = settings.internal_smallWindow ? 680 : 920;
        int windowHeight = settings.internal_smallWindow ? 600 : 720;


        createWindow("XBuilders", windowWidth, windowHeight);


        GLFW.glfwSwapInterval(settings.video_vsync ? 1 : 0);

        //If a fullscreen window is created, we need to set the focus callback so that the user can exit fullscreen if they lose focus
        // Get the current GLFW window handle
        long windowHandle = GLFW.glfwGetCurrentContext();
        // Create a new window focus callback
        GLFWWindowFocusCallback focusCallback = new GLFWWindowFocusCallback() {
            @Override
            public void invoke(long window, boolean focused) {
                if (!focused) {
                    windowUnfocusEvent();
                }
            }
        };
        GLFW.glfwSetWindowFocusCallback(windowHandle, focusCallback);


        setIcon(ResourceUtils.resource("icon16.png").getAbsolutePath(),
                ResourceUtils.resource("icon32.png").getAbsolutePath(),
                ResourceUtils.resource("icon256.png").getAbsolutePath());

        Theme.initialize(ctx);
        gameScene.initialize(this);

        topMenu.init(GameScene.server.getIpAdress());

        if (generateIcons || !blockIconsDirectory.exists()) {
            firstTimeSetup();
        }
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

    private void firstTimeSetup() throws InterruptedException {
        //Minimize the window
        GLFW.glfwHideWindow(getWindow());

        createPopupWindow("First time setup",
                "XBuilders is setting up. Please standby...");
        BlockIconRenderer iconRenderer = new BlockIconRenderer(
                Registrys.blocks.textures,
                blockIconsDirectory);

        iconRenderer.saveAllIcons();//Generate all icons

        new EngineSettings().save();

        createPopupWindow("Finished",
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
        popupMessage.draw();
        frameCount++;//It just wraps around when it reaches max value
    }

    static DecimalFormat df = new DecimalFormat("####.00");

    private static boolean screenshot = false;
    private static boolean screenShotInitialized = false;

    public static void takeScreenshot() {
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
            GameScene.alert("Screenshot saved to: " + saveFile.getAbsolutePath());
            try {
                saveFile.getParentFile().mkdirs();
                ImageIO.write(readPixelsOfWindow(), "png", saveFile);
            } catch (IOException e) {
                ErrorHandler.report("Could not save screenshot", e);
            }
            screenShotInitialized = false;
            screenshot = false;
        }
    }

    public static String mfpAndMemory = "";

    @Override
    public void onMPFUpdate() {
        // Lower MPF is better. Since we are matching the FPS to the monitors refresh
        // rate, the FPS will not exceed 60fps.
        // Our goal is to get as close to 16.666 MPF (60 FPS) as possible
        String formattedNumber = df.format(getMsPerFrame());
        mfpAndMemory = "mpf: " + formattedNumber + "    memory: " + MemoryProfiler.getMemoryUsageAsString();

        String playerName = "";
        try {
            playerName = " (" + GameScene.player.userInfo.name + ") ";
        } finally {
        }
        setTitle(title + playerName + (MainWindow.devMode ? "   " + mfpAndMemory : ""));
    }

    @Override
    public void framebufferResizeEvent(int width, int height) {
        gameScene.windowResizeEvent(width, height);
    }

    public void minimizeWindow() {
        long windowHandle = GLFW.glfwGetCurrentContext();
        if (isGameMode) {
            gameScene.windowUnfocusEvent();
        }
        if (isFullscreen()) {
            GLFW.glfwIconifyWindow(windowHandle);
        }
    }

    public static void restoreWindow() {
        long windowHandle = GLFW.glfwGetCurrentContext();
        GLFW.glfwRestoreWindow(windowHandle);
    }

    private void windowUnfocusEvent() {
        long windowHandle = GLFW.glfwGetCurrentContext();
        if (isGameMode) {
            gameScene.windowUnfocusEvent();
        }
        if (isFullscreen()) {
            GLFW.glfwIconifyWindow(windowHandle);
        }
    }

    public static boolean devkeyF3;
    public static boolean devkeyF4;
    public static boolean devkeyF1;
    public static boolean devkeyF12;

    @Override
    public void keyEvent(int key, int scancode, int action, int mods) {
        if (action == GLFW.GLFW_RELEASE) {
            if (devMode && key == GLFW.GLFW_KEY_F2) {
                System.out.println("System.GC()");
                System.gc();
            } else if (devMode && key == GLFW.GLFW_KEY_F3) {
                devkeyF3 = !devkeyF3;
                System.out.println("Special mode (F3): " + devkeyF3);
            } else if (devMode && key == GLFW.GLFW_KEY_F4) {
                devkeyF4 = !devkeyF4;
                System.out.println("Special mode (F4): " + devkeyF4);
            } else if (devMode && key == GLFW.GLFW_KEY_F1) {
                devkeyF1 = !devkeyF1;
                System.out.println("Special mode (F1): " + devkeyF1);
            } else if (devMode && key == GLFW.GLFW_KEY_F12) {
                devkeyF12 = !devkeyF12;
                System.out.println("Light repropagation: " + devkeyF12);
            } else if (key == GLFW.GLFW_KEY_F11) {
                takeScreenshot();
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
