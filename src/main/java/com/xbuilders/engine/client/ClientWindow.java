/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.xbuilders.engine.client;

import com.xbuilders.Main;
import com.xbuilders.engine.client.visuals.gameScene.GameScene;
import com.xbuilders.engine.server.Game;
import com.xbuilders.engine.server.Registrys;
import com.xbuilders.engine.server.item.blockIconRendering.BlockIconRenderer;
import com.xbuilders.engine.client.settings.ClientSettings;
import com.xbuilders.engine.client.visuals.Theme;
import com.xbuilders.engine.client.visuals.topMenu.PopupMessage;
import com.xbuilders.engine.client.visuals.topMenu.TopMenu;
import com.xbuilders.engine.server.world.World;
import com.xbuilders.engine.utils.ErrorHandler;
import com.xbuilders.engine.utils.resource.ResourceLoader;
import com.xbuilders.engine.utils.resource.ResourceUtils;
import com.xbuilders.window.GLFWWindow;
import com.xbuilders.window.NKWindow;
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

public class ClientWindow extends NKWindow {
    public static int frameCount = 0;

    //==================================================================================================================
    //==================================================================================================================
    //==================================================================================================================
    //==================================================================================================================
    //==================================================================================================================
    //==================================================================================================================


    public static void printlnDev(String message) {
        if (LocalClient.DEV_MODE) {
            System.out.println(message);
        }
    }


    // We can still have static variables, but we want to use dependency injection,
    // and make all classes ask for the object instead of directly acsessing it
    // In summary, we want ALL classes to be easily seprable, to make the code more
    // flexible.
    public static void goToGamePage() {
        isGameMode = true;
    }

    public static void goToMenuPage() {
        isGameMode = false;
        Main.localServer.stopGameEvent(); //Close the entire game
    }

    public static boolean isInGamePage() {
        return isGameMode;
    }

    public static ClientSettings settings;

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

    public static TopMenu topMenu;
    public static GameScene gameScene;

    public static PopupMessage popupMessage;
    public static final ResourceLoader resourceLoader = new ResourceLoader();
    File blockIconsDirectory = ResourceUtils.file("items\\blocks\\icons");
    String title;

    public ClientWindow(String title) {
        super();
        this.title = title;
    }

    public void startWindowThread() throws IOException {
        while (!windowShouldClose()) {
            /* Input */
            beginScreenshot(); //If we want the frameTester to capture the entire frame length, we need to include startFrame() and endFrame()
            startFrame();
            LocalClient.frameTester.__startFrame();
            render();
            MemoryProfiler.update();
            if (LocalClient.memoryGraph != null) LocalClient.memoryGraph.update();
            LocalClient.frameTester.__endFrame();

            endFrame();//EndFrame takes the most time, becuase we have vsync turned on
            endScreenshot();
        }
    }

    public void init(Game game, World world) throws Exception {
        GLFWWindow.initGLFW();
        settings = ClientSettings.load();

        setMpfUpdateInterval(1000);
        MemoryProfiler.setIntervalMS(500);

        /**
         * Create the window
         */
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


        setIcon(resourceLoader.getResourceAsStream("builtin/icon16.png"),
                resourceLoader.getResourceAsStream("builtin/icon32.png"),
                resourceLoader.getResourceAsStream("builtin/icon256.png"));

        /**
         * Anything involving OpenGL must be done after the window has been created
         */

        Theme.initialize(ctx);
        gameScene = new GameScene(this, game, world);
        game.setupClient(this, ctx, gameScene.ui);
        popupMessage = new PopupMessage(ctx, this);
        topMenu = new TopMenu(this);


        Main.localServer.initialize(gameScene.userPlayer);


        if (LocalClient.generateIcons || !blockIconsDirectory.exists()) {
            firstTimeSetup();
        }

        if (settings.video_fullscreen) {
            enableFullscreen(settings.video_fullscreenSize.value);
        }
        saveAndApplySettings();   //DO THIS LAST Apply settings just in case the settings were not already applied
        showWindow();
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

        new ClientSettings().save();

        createPopupWindow("Finished",
                "XBuilders has finished setting up. Please restart the game to play.");
        Thread.sleep(5000);
        System.exit(0);
    }

    private void render() throws IOException {
        if (isGameMode) {
            Main.localServer.update();
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
            File saveFile = ResourceUtils.appDataFile("screenshots\\" + formattedDateTime + ".png");
            LocalClient.alertClient("Screenshot saved to: " + saveFile.getAbsolutePath());
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
            playerName = " (" + GameScene.userPlayer.userInfo.name + ") ";
        } finally {
        }
        setTitle(title + playerName + (LocalClient.DEV_MODE ? "   " + mfpAndMemory : ""));
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
            if (LocalClient.DEV_MODE && key == GLFW.GLFW_KEY_F2) {
                System.out.println("System.GC()");
                System.gc();
            } else if (LocalClient.DEV_MODE && key == GLFW.GLFW_KEY_F3) {
                devkeyF3 = !devkeyF3;
                System.out.println("Special mode (F3): " + devkeyF3);
            } else if (LocalClient.DEV_MODE && key == GLFW.GLFW_KEY_F4) {
                devkeyF4 = !devkeyF4;
                System.out.println("Special mode (F4): " + devkeyF4);
            } else if (LocalClient.DEV_MODE && key == GLFW.GLFW_KEY_F1) {
                devkeyF1 = !devkeyF1;
                System.out.println("Special mode (F1): " + devkeyF1);
            } else if (LocalClient.DEV_MODE && key == GLFW.GLFW_KEY_F12) {
                devkeyF12 = !devkeyF12;
                System.out.println("Light repropagation: " + devkeyF12);
            } else if (LocalClient.DEV_MODE && key == GLFW.GLFW_KEY_F10) {
                System.out.println("Forced devmode crash! (F10) key pressed");
                System.out.println(10 / 0);
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
