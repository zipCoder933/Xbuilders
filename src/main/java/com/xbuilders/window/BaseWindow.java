/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.xbuilders.window;

import com.xbuilders.window.utils.ValueSmoother;
import com.xbuilders.window.utils.IOUtil;
import com.xbuilders.window.utils.texture.TextureUtils;

import java.awt.image.BufferedImage;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import org.joml.Vector2d;
import org.lwjgl.BufferUtils;
import org.lwjgl.glfw.*;

import org.lwjgl.opengl.*;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.ARBDebugOutput.GL_DEBUG_SEVERITY_LOW_ARB;
import static org.lwjgl.opengl.ARBDebugOutput.GL_DEBUG_SOURCE_API_ARB;
import static org.lwjgl.opengl.ARBDebugOutput.GL_DEBUG_TYPE_OTHER_ARB;
import static org.lwjgl.opengl.ARBDebugOutput.glDebugMessageControlARB;
import static org.lwjgl.opengl.GL11.glViewport;

import org.lwjgl.system.Callback;
import org.lwjgl.system.MemoryStack;

import static org.lwjgl.system.MemoryStack.stackPush;

import org.lwjgl.system.MemoryUtil;

import org.lwjgl.glfw.GLFWImage;

import static org.lwjgl.stb.STBImage.*;
import static org.lwjgl.system.MemoryUtil.*;

import org.lwjgl.system.Platform;

/**
 * @author Patron
 */
public abstract class BaseWindow {

    protected int width, height, display_width, display_height;
    protected Vector2d cursor;
    protected long id;
    protected GLFWFramebufferSizeCallback framebufferSizeCallback;
    protected GLCapabilities capabilities;
    private static Callback debugProc;

    // From
    // https://gamedev.stackexchange.com/questions/105555/setting-window-icon-using-glfw-lwjgl-3
    public void setIcon(String icon16Path, String icon32Path, String icon256Path) throws Exception {
        ByteBuffer icon16, icon32, icon256;
        try {
            icon16 = IOUtil.ioResourceToByteBuffer(icon16Path, 2048);
            icon32 = IOUtil.ioResourceToByteBuffer(icon32Path, 4096);
            icon256 = IOUtil.ioResourceToByteBuffer(icon256Path, 1262144);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        IntBuffer w = memAllocInt(1);
        IntBuffer h = memAllocInt(1);
        IntBuffer comp = memAllocInt(1);

        try (GLFWImage.Buffer icons = GLFWImage.malloc(2)) {
            ByteBuffer pixels16 = stbi_load_from_memory(icon16, w, h, comp, 4);
            icons.position(0)
                    .width(w.get(0))
                    .height(h.get(0))
                    .pixels(pixels16);

            ByteBuffer pixels32 = stbi_load_from_memory(icon32, w, h, comp, 4);
            icons.position(1)
                    .width(w.get(0))
                    .height(h.get(0))
                    .pixels(pixels32);

            ByteBuffer pixels256 = stbi_load_from_memory(icon256, w, h, comp, 4);
            icons.position(2)
                    .width(w.get(0))
                    .height(h.get(0))
                    .pixels(pixels256);

            icons.position(0);
            GLFW.glfwSetWindowIcon(id, icons);

            stbi_image_free(pixels32);
            stbi_image_free(pixels16);
        }
        // Display.setIcon(new ByteBuffer[] {
        // new ImageIOImageData().imageToByteBuffer(ImageIO.read(new
        // File("res/game/gameIcon.png")), false, false, null),
        // new ImageIOImageData().imageToByteBuffer(ImageIO.read(new
        // File("res/game/gameIcon.png")), false, false, null)
        // });

        // IntBuffer w = MemoryUtil.memAllocInt(1);
        // IntBuffer h = MemoryUtil.memAllocInt(1);
        // IntBuffer comp = MemoryUtil.memAllocInt(1);

        // File image = new File(path);
        // if(!image.exists()) {
        // throw new RuntimeException("File not found: " + path);
        // }

        // // Icons
        // {
        // ByteBuffer icon16;
        // ByteBuffer icon32;
        // try {
        // icon16 = MiscUtils.ioResourceToByteBuffer(path, 2048);
        // icon32 = MiscUtils.ioResourceToByteBuffer(path, 4096);
        // } catch (Exception e) {
        // throw new RuntimeException(e);
        // }

        // try (GLFWImage.Buffer icons = GLFWImage.malloc(2)) {
        // ByteBuffer pixels16 = STBImage.stbi_load_from_memory(icon16, w, h, comp, 4);
        // icons
        // .position(0)
        // .width(w.get(0))
        // .height(h.get(0))
        // .pixels(pixels16);

        // ByteBuffer pixels32 = STBImage.stbi_load_from_memory(icon32, w, h, comp, 4);
        // icons
        // .position(1)
        // .width(w.get(0))
        // .height(h.get(0))
        // .pixels(pixels32);

        // icons.position(0);
        // GLFW.glfwSetWindowIcon(id, icons);

        // STBImage.stbi_image_free(pixels32);
        // STBImage.stbi_image_free(pixels16);
        // }
        // }

        // MemoryUtil.memFree(comp);
        // MemoryUtil.memFree(h);
        // MemoryUtil.memFree(w);
    }

    public boolean windowIsFocused() {
        return glfwGetWindowAttrib(id, GLFW_FOCUSED) == GLFW_TRUE;
    }

    public BufferedImage readPixelsOfWindow() {
        int WIDTH = getWidth();
        int HEIGHT = getHeight();

        ByteBuffer buffer = BufferUtils.createByteBuffer(WIDTH * HEIGHT * 4);

        // Read the pixel data from the framebuffer
        GL11.glReadPixels(0, 0, WIDTH, HEIGHT, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, buffer);

        // Convert the raw pixel data to an image
        BufferedImage image = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
        for (int y = 0; y < HEIGHT; y++) {
            for (int x = 0; x < WIDTH; x++) {
                int i = (x + WIDTH * y) * 4;
                int r = buffer.get(i) & 0xFF;
                int g = buffer.get(i + 1) & 0xFF;
                int b = buffer.get(i + 2) & 0xFF;
                int rgb = (r << 16) | (g << 8) | b;
                image.setRGB(x, HEIGHT - y - 1, rgb); // Flip the image vertically
            }
        }
        return image;
    }

    public BaseWindow() {
        cursor = new Vector2d();
    }

    // <editor-fold defaultstate="collapsed" desc="glfw implentations / random
    // methods">
    public void setTitle(String title) {
        GLFW.glfwSetWindowTitle(getId(), title);
    }

    public void setWindowPos(final int x, final int y) {
        GLFW.glfwSetWindowPos(getId(), x, y);
    }

    public void getWindowPos(IntBuffer xpos, IntBuffer ypos) {
        GLFW.glfwGetWindowPos(getId(), xpos, ypos);
    }

    public void centerWindow() {
        // vidmode gets the info about the monitor
        GLFWVidMode vidmode = GLFW.glfwGetVideoMode(glfwGetPrimaryMonitor());
        try (MemoryStack stack = stackPush()) {
            IntBuffer pWidth = stack.mallocInt(1);
            IntBuffer pHeight = stack.mallocInt(1);
            GLFW.glfwGetWindowSize(getId(), pWidth, pHeight);
            GLFW.glfwSetWindowPos(getId(),
                    (vidmode.width() - pWidth.get(0)) / 2,
                    (vidmode.height() - pHeight.get(0)) / 2);
        }
    }

    /**
     * @param key A key ID from the org.lwjgl.glfw.GLFW package
     * @return
     */
    public boolean isKeyPressed(int key) {
        return GLFW.glfwGetKey(getId(), key) == GLFW.GLFW_PRESS;
    }

    /**
     * @param key A key ID from the org.lwjgl.glfw.GLFW package
     * @return
     */
    public boolean isMouseButtonPressed(int key) {
        return GLFW.glfwGetMouseButton(getId(), key) == GLFW.GLFW_PRESS;
    }

    public void showWindow() {
        GLFW.glfwShowWindow(getId());
    }

    public void hideWindow() {
        GLFW.glfwHideWindow(getId());
    }

    public boolean windowShouldClose() {
        return GLFW.glfwWindowShouldClose(getId());
    }
    // </editor-fold>

    // //<editor-fold defaultstate="collapsed" desc="variables">

    /**
     * @return the id
     */
    public long getId() {
        return id;
    }

    /**
     * @return the width
     */
    public int getWidth() {
        return width;
    }

    /**
     * @return the height
     */
    public int getHeight() {
        return height;
    }

    /**
     * @return the display_width
     */
    public int getDisplay_width() {
        return display_width;
    }

    /**
     * @return the display_height
     */
    public int getDisplay_height() {
        return display_height;
    }

    private void setWindowSizeVariables() {
        try (MemoryStack stack = stackPush()) {
            IntBuffer w = stack.mallocInt(1);
            IntBuffer h = stack.mallocInt(1);

            glfwGetWindowSize(getId(), w, h);
            width = w.get(0);
            height = h.get(0);

            glfwGetFramebufferSize(getId(), w, h);
            display_width = w.get(0);
            display_height = h.get(0);
        }
    }

    public double getCursorPosX() {
        return cursor.x;
    }

    public double getCursorPosY() {
        return cursor.y;
    }

    public Vector2d getCursorVector() {
        return cursor;
    }
    //// </editor-fold>

    public static final Object windowCreateLock = new Object();

    protected void startWindow(String title, boolean fullscreen, int width, int height) {
        glfwDefaultWindowHints();
        glfwWindowHint(GLFW_AUTO_ICONIFY, GLFW_FALSE); //Important hint for fullscreen resolution
        glfwWindowHint(GLFW.GLFW_VISIBLE, GLFW.GLFW_FALSE);
        glfwWindowHint(GLFW.GLFW_RESIZABLE, GLFW.GLFW_TRUE);

        // Set opengl version (You can change this to your desired opengl version)
        glfwWindowHint(GLFW.GLFW_CONTEXT_VERSION_MAJOR, 3);
        glfwWindowHint(GLFW.GLFW_CONTEXT_VERSION_MINOR, 3);

        glfwWindowHint(GLFW.GLFW_OPENGL_PROFILE, GLFW.GLFW_OPENGL_CORE_PROFILE);
        if (Platform.get() == Platform.MACOSX) {
            glfwWindowHint(GLFW.GLFW_OPENGL_FORWARD_COMPAT, GLFW.GLFW_TRUE);
        }
        glfwWindowHint(GLFW.GLFW_OPENGL_DEBUG_CONTEXT, GLFW.GLFW_TRUE);

        synchronized (windowCreateLock) {
            if (fullscreen) {
                // Get the primary monitor
                long monitor = GLFW.glfwGetPrimaryMonitor();
                // Set low resolution window size (initial size)
                int low_res_x = width; // example value
                int low_res_y = height; // example value
                // Create the window
                id = GLFW.glfwCreateWindow(low_res_x, low_res_y, title, monitor, NULL);
            } else id = glfwCreateWindow(width, height, title, NULL, NULL);

            if (getId() == NULL) {
                throw new RuntimeException("Failed to create the GLFW window \"" + title + "\"");
            }
        }
        // all subsequent operations are directed to this window
        glfwMakeContextCurrent(getId());
        // Init framebufferSizeCallback event
        initCallbacks();

//        if (fullscreen) { //For some reason, resizing the window causes a crash
//            // Set the window size to 1920x1080
//            GLFW.glfwSetWindowSize(id, width, height);
//        }

        // Enable V-sync (synchronizes the fps to the monitor update time,
        // this can cap the fps to the monitors refresh rate (e.g. 60fps))
        // but it removes artifacts like "screen tearing"
        GLFW.glfwSwapInterval(1);// to disable vsync GLFW.glfwSwapInterval(0); (1 = on, 0 = off)

        centerWindow();
        setWindowSizeVariables();

        // create and initialize capabilites
        capabilities = GL.createCapabilities();

        // <editor-fold defaultstate="collapsed" desc="Init debugs">
        if (debugProc == null) {
            debugProc = GLUtil.setupDebugMessageCallback();
            if (capabilities.OpenGL43) {
                GL43.glDebugMessageControl(GL43.GL_DEBUG_SOURCE_API, GL43.GL_DEBUG_TYPE_OTHER,
                        GL43.GL_DEBUG_SEVERITY_NOTIFICATION, (IntBuffer) null, false);
            } else if (capabilities.GL_KHR_debug) {
                KHRDebug.glDebugMessageControl(
                        KHRDebug.GL_DEBUG_SOURCE_API,
                        KHRDebug.GL_DEBUG_TYPE_OTHER,
                        KHRDebug.GL_DEBUG_SEVERITY_NOTIFICATION,
                        (IntBuffer) null,
                        false);
            } else if (capabilities.GL_ARB_debug_output) {
                glDebugMessageControlARB(GL_DEBUG_SOURCE_API_ARB, GL_DEBUG_TYPE_OTHER_ARB, GL_DEBUG_SEVERITY_LOW_ARB,
                        (IntBuffer) null, false);
            }
            GL43.glDebugMessageCallback(BaseWindow::debugCallback, NULL);
        }
        // </editor-fold>

        startMPF();
    }
    // Callback method to handle debug messages

    static boolean enableDebugMessages = true;

    /**
     * @param enabled if we should print debug messages
     */
    public static void printDebugsEnabled(boolean enabled) {
        enableDebugMessages = enabled;
    }

    private static void debugCallback(int source, int type, int id, int severity, int length, long message,
                                      long userParam) {
        if (enableDebugMessages) {
            ByteBuffer buffer = MemoryUtil.memByteBuffer(message, length);
            String debugMessage = MemoryUtil.memASCII(buffer);

            System.err.println("OpenGL Debug Message:");
            System.err.println("  Source: " + source);
            System.err.println("  Type: " + type);
            System.err.println("  ID: " + id);
            System.err.println("  Severity: " + severity);
            System.err.println("  Message: " + debugMessage);
        }
    }

    private void initCallbacks() {
        framebufferSizeCallback = new GLFWFramebufferSizeCallback() {
            @Override
            public void invoke(long window, final int width, final int height) {
                glfwMakeContextCurrent(window);
                setWindowSizeVariables();
                glViewport(0, 0, width, height);
                windowResizeEvent(width, height);
                // glfwMakeContextCurrent(NULL);
            }
        };
        GLFW.glfwSetFramebufferSizeCallback(getId(), framebufferSizeCallback);

    }

    /**
     * called when the window is resized
     *
     * @param width
     * @param height
     */
    public abstract void windowResizeEvent(final int width, final int height);

    // <editor-fold defaultstate="collapsed" desc="MPF">
    private double lastTime;
    private int nbFrames;
    private double updateIntervalSec = 1.0;
    private int updateIntervalMS = 1000;
    private double msPerFrame;
    long timer = System.nanoTime();


    public void setMpfUpdateInterval(int milliseconds) {
        updateIntervalMS = milliseconds;
        updateIntervalSec = (double) updateIntervalMS / 1000;
    }

    private void startMPF() {
        lastTime = glfwGetTime();
        nbFrames = 0;
        timer = System.nanoTime();
        frameDeltaSec = 1f;
    }

    /**
     * @return the amount of time it takes (in ms) to render 1 frame
     */
    public double getMsPerFrame() {
        return msPerFrame;
    }


    public void onMPFUpdate() {
        // System.out.println(getMsPerFrame() + " ms/frame\n");
    }

    /**
     * This code displays the time, in milliseconds, needed to draw a frame
     * instead of how many frame were drawn in the last second. This is actually
     * much better. Don’t rely on FPS. Never.
     * <br>
     * <br>
     * this is an inverse relationship, and we humans suck at understanding this
     * kind of relationship. Let’s take an example. You write a great rendering
     * function that runs at 1000 FPS ( 1ms/frame ). But you forgot a little
     * computation in a shader, which adds an extra cost of 0.1ms. And bam,
     * 1/0.0011 = 900. You just lost 100FPS. Morality : never use FPS for
     * performance analysis. If you intend to make a 60fps game, your target
     * will be 16.6666ms ; If you intend to make a 30fps game, your target will
     * be 33.3333ms. That’s all you need to know.
     */

    public float frameDeltaSec = 1f;
    public float smoothFrameDeltaSec = 1f;

    private ValueSmoother smoothed = new ValueSmoother(20);
    /**
     * Delta smoothign helps the movement maintain a constant speed
     * Here's a basic example in Java:
     *
     * Copy
     * public class GameLoop {
     *
     *     private static final int SMOOTHING_FACTOR = 5; // Number of frame times to consider for smoothing
     *     private static List<Double> frameTimes = new ArrayList<>();
     *
     *     public static void main(String[] args) {
     *         while (true) {
     *             long currentTime = System.nanoTime();
     *             double frameTime = (currentTime - previousTime) / 1_000_000_000.0;
     *             previousTime = currentTime;
     *
     *             frameTimes.add(frameTime);
     *             if (frameTimes.size() > SMOOTHING_FACTOR) {
     *                 frameTimes.remove(0); // Keep only the last N frame times
     *             }
     *
     *             double smoothedDeltaTime = calculateSmoothedDeltaTime();
     *             update(smoothedDeltaTime);
     *             render();
     *         }
     *     }
     *
     *     private static double calculateSmoothedDeltaTime() {
     *         double sum = 0;
     *         for (double time : frameTimes) {
     *             sum += time;
     *         }
     *         return sum / frameTimes.size();
     *     }
     */

    protected void tickMPF() {
        frameDeltaSec = (System.nanoTime() - timer) / 1000000000f;
        timer = System.nanoTime();
        smoothed.add(frameDeltaSec);
        smoothFrameDeltaSec = smoothed.getAverage();

        // do {// Measure speed
        double currentTime = glfwGetTime();
        nbFrames++;
        if (currentTime - lastTime >= updateIntervalSec) {// If last prinf() was more than 1 sec ago
            // printf and reset timer
            msPerFrame = updateIntervalMS / ((double) nbFrames);
            onMPFUpdate();
            nbFrames = 0;
            lastTime += updateIntervalSec;
        }
        // }
    }
    // </editor-fold>

    /**
     * Closes and cleans up the current window
     */
    public void terminate() {
        Callbacks.glfwFreeCallbacks(getId());
        GLFW.glfwDestroyWindow(getId());
    }

    /**
     * start glfw in the program
     */
    public static final void initGLFW() {
        if (!glfwInit()) {
            throw new IllegalStateException("Unable to initialize glfw");
        }
    }

    /**
     * end the program
     */
    public static final void endGLFW() {
        GLFW.glfwSetErrorCallback(null).free();
        if (debugProc != null) {
            debugProc.free();
        }
        TextureUtils.deleteAllTextures();
        GLFW.glfwTerminate();
    }

    /*
     * // GLFW key event action constants
     * int GLFW_PRESS = 1;
     * int GLFW_RELEASE = 0;
     * int GLFW_REPEAT = 2;
     */
}
