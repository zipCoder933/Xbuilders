/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.xbuilders.window;

import org.lwjgl.glfw.GLFW;
import static org.lwjgl.glfw.GLFW.glfwSetCursorPosCallback;

/**
 *
 * @author zipCoder933
 */
public abstract class Window extends BaseWindow {

    @Override
    public void startWindow(String title, boolean fullscreen,int width, int height) {
        super.startWindow(title,fullscreen, width, height);
        glfwSetCursorPosCallback(getId(), (window, xpos, ypos) -> {
            cursor.x = xpos;
            cursor.y = ypos;
        });
    }

    public void newFrame() {
        GLFW.glfwSwapBuffers(getId());
        GLFW.glfwPollEvents();
        tickMPF();
    }
}
