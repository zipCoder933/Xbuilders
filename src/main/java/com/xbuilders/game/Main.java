/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.xbuilders.game;

import com.xbuilders.engine.items.ItemList;
import com.xbuilders.engine.items.block.Block;
import com.xbuilders.engine.items.block.blockIconRendering.BlockIconRenderer;
import com.xbuilders.engine.ui.UIResources;
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

import com.xbuilders.window.utils.texture.TextureUtils;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.nuklear.NkVec2;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;
import org.lwjgl.opengl.GL32;

import javax.imageio.ImageIO;

public class Main extends NKWindow {

    //We can still have static variables, but we want to use dependency injection,
    //and make all classes ask for the object instead of directly acsessing it
    //In summary, we want ALL classes to be easily seprable, to make the code more flexible.
    public static void goToGamePage() {
        isGameMode = true;
    }

    public static void goToMenuPage() {
        isGameMode = false;
    }

    private static boolean isGameMode = false;
    public static MyGame game;
    public static TopMenu topMenu;
    public static GameScene gameScene;
    public static UserID user;
    UIResources uiResources;
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
        user = new UserID(ResourceUtils.appDataResource("userID.txt"));
        System.out.println(user.toString());

        game = new MyGame();
        topMenu = new TopMenu(this);
        gameScene = new GameScene(this);
        setMpfUpdateInterval(200);
        MemoryProfiler.setIntervalMS(500);
    }

    private void init() throws Exception {
        uiResources = new UIResources(this, ctx);
        screenshotFramebuffer = GL30.glGenFramebuffers();
        game.initialize();
        topMenu.init(uiResources);
        gameScene.init(uiResources, game);


        if (generateIcons) {
            BlockIconRenderer iconRenderer = new BlockIconRenderer(
                    ItemList.blocks.textures,
                    ResourceUtils.resource("items\\blocks\\icons")) {
                @Override
                public boolean shouldMakeIcon(Block block) {
                    return block.type != RenderType.SPRITE && block.type != RenderType.FLOOR && block.type != RenderType.WALL_ITEM;
                }
            };
            iconRenderer.saveAllIcons();
            System.exit(0);
        }


    }

    boolean screenshot = false;
    private int screenshotFramebuffer;
    private int textureID;

    private void render() throws IOException {
        if (screenshot) {
            textureID = BlockIconRenderer.makeBlankTexture(getWidth(), getHeight());
            GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, screenshotFramebuffer);
            GL32.glFramebufferTexture(GL30.GL_FRAMEBUFFER, GL30.GL_COLOR_ATTACHMENT0, textureID, 0);
        }

        if (isGameMode) {
            gameScene.render();
        } else {
            topMenu.render();
        }

        if (screenshot) {
            LocalDateTime currentDateTime = LocalDateTime.now();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH-mm-ss");
            String formattedDateTime = currentDateTime.format(formatter);
            File saveFile = ResourceUtils.appDataResource("screenshots\\" + formattedDateTime + ".png");
            System.out.println("Screenshot saved to: " + saveFile.getAbsolutePath());
            ImageIO.write(TextureUtils.getTextureAsBufferedImage(textureID), "png", saveFile);
            GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, 0);
            GL11.glDeleteTextures(textureID);
            screenshot = false;
        }
    }

    static DecimalFormat df = new DecimalFormat("####.00");

    @Override
    public void onMPFUpdate() {
        //Lower MPF is better. Since we are matching the FPS to the monitors refresh rate, the FPS will not exceed 60fps.
        //Our goal is to get as close to 16.666 MPF (60 FPS) as possible
        String formattedNumber = df.format(getMsPerFrame());
        setTitle("Xbuilders   mpf: " + formattedNumber + "    memory: " + MemoryProfiler.getMemoryUsageAsString());
    }

    private void run() throws Exception {
        initGLFW();
        startWindow("TEST WINDOW", 800, 600);
        init();
        showWindow();
        System.out.println("Press 1 for System.GC()");
        while (!windowShouldClose()) {
            /* Input */
            startFrame();
            render();
            MemoryProfiler.update();
            endFrame();
            if (specialMode1) {
                System.out.println("System.GC()");
                System.gc();
                specialMode1 = false;
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

    public static boolean specialMode3;
    public static boolean specialMode1;
    public static boolean specialMode2;

    @Override
    public void keyEvent(int key, int scancode, int action, int mods) {
        if (action == GLFW.GLFW_RELEASE) {
            if (key == GLFW.GLFW_KEY_3) {
                specialMode3 = !specialMode3;
                System.out.println("Special mode (3): " + specialMode3);
            } else if (key == GLFW.GLFW_KEY_1) {
                specialMode1 = !specialMode1;
                System.out.println("Special mode(1): " + specialMode1);
            } else if (key == GLFW.GLFW_KEY_2) {
                specialMode2 = !specialMode2;
                System.out.println("Special mode (2): " + specialMode2);
            } else if (key == GLFW.GLFW_KEY_F11) {
                screenshot = true;
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
