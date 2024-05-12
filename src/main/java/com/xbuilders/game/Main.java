/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.xbuilders.game;

import com.xbuilders.engine.items.ItemList;
import com.xbuilders.engine.items.block.Block;
import com.xbuilders.engine.items.block.blockIconRendering.BlockIconRenderer;
import com.xbuilders.engine.settings.EngineSettings;
import com.xbuilders.engine.settings.EngineSettingsUtils;
import com.xbuilders.engine.ui.UIResources;
import com.xbuilders.engine.utils.ErrorHandler;
import com.xbuilders.engine.utils.ResourceUtils;
import com.xbuilders.engine.utils.preformance.MemoryProfiler;
import com.xbuilders.engine.gameScene.GameScene;
import com.xbuilders.engine.ui.topMenu.TopMenu;
import com.xbuilders.engine.utils.UserID;
import com.xbuilders.game.items.blocks.RenderType;
import com.xbuilders.window.NKWindow;

import java.io.*;
import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.nuklear.NkVec2;

import javax.imageio.ImageIO;

public class Main extends NKWindow {

    // We can still have static variables, but we want to use dependency injection,
    // and make all classes ask for the object instead of directly acsessing it
    // In summary, we want ALL classes to be easily seprable, to make the code more
    // flexible.
    public static void goToGamePage() {
        isGameMode = true;
    }

    public static void goToMenuPage() {
        isGameMode = false;
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
    UIResources uiResources;
    
    File blockIconsDirectory = ResourceUtils.resource("items\\blocks\\icons");
    static boolean generateIcons = false;

    public static boolean devMode = false;

    public static void main(String[] args) {
        try {
            for (String arg : args) {
                if (arg.equals("icons")) {
                    generateIcons = true;
                } else if (arg.equals("devmode")) {
                    devMode = true;
                    System.out.println("Dev mode enabled");
                }
            }
            ResourceUtils.initialize(devMode);

            new Main().run();
        } catch (Exception ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public Main() throws IOException, Exception {

        settings = settingsUtils.load();
        user = new UserID(ResourceUtils.appDataResource("userID.txt"));
        System.out.println(user.toString());

        game = new MyGame();
        topMenu = new TopMenu(this);
        gameScene = new GameScene(this);
        setMpfUpdateInterval(200);
        MemoryProfiler.setIntervalMS(500);

    }

    private void init() throws Exception {
        setIcon(ResourceUtils.resource("icon16.png").getAbsolutePath(),
                ResourceUtils.resource("icon32.png").getAbsolutePath(),
                ResourceUtils.resource("icon256.png").getAbsolutePath());
        ItemList.initialize();
        uiResources = new UIResources(this, ctx);
        game.initialize(this);

        topMenu.init(uiResources);

        gameScene.init(uiResources, game);

        if (generateIcons || !blockIconsDirectory.exists()) {
            BlockIconRenderer iconRenderer = new BlockIconRenderer(
                    ItemList.blocks.textures,
                    blockIconsDirectory);
            iconRenderer.saveAllIcons();
            System.exit(0);
        }

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
        setTitle("Xbuilders   mpf: " + formattedNumber + "    memory: " + MemoryProfiler.getMemoryUsageAsString());
    }

    private void run() throws Exception {
        initGLFW();
        startWindow("TEST WINDOW", 850, 650);
        init();
        showWindow();
        System.out.println("Press 1 for System.GC()");

        while (!windowShouldClose()) {
            /* Input */

            beginScreenshot();
            startFrame();
            render();
            MemoryProfiler.update();
            endFrame();
            endScreenshot();

            if (devkeyF2_SystemCG) {
                System.out.println("System.GC()");
                System.gc();
                devkeyF2_SystemCG = false;
            }
        }
        terminate();
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
    public static boolean devkeyF2_SystemCG;
    public static boolean devkeyF1;
    public static boolean devkeyF12;

    @Override
    public void keyEvent(int key, int scancode, int action, int mods) {
        if (action == GLFW.GLFW_RELEASE) {
            if (key == GLFW.GLFW_KEY_F3) {
                devkeyF3 = !devkeyF3;
                System.out.println("Special mode (F3): " + devkeyF3);
            } else if (key == GLFW.GLFW_KEY_F2) {
                devkeyF2_SystemCG = !devkeyF2_SystemCG;
                System.out.println("Special mode(F2): " + devkeyF2_SystemCG);
            } else if (key == GLFW.GLFW_KEY_F1) {
                devkeyF1 = !devkeyF1;
                System.out.println("Special mode (F1): " + devkeyF1);
            } else if (key == GLFW.GLFW_KEY_F11) {
                screenshot();
            } else if (key == GLFW.GLFW_KEY_F12) {
                devkeyF12 = !devkeyF12;
                System.out.println("Light repropagation: " + devkeyF12);
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
