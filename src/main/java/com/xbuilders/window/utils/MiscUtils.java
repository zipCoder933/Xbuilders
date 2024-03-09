/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.xbuilders.window.utils;

import org.lwjgl.BufferUtils;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import static org.lwjgl.opengl.GL11.GL_NEAREST;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL12.GL_BGR;

/**
 *
 * @author Patron
 */
public class MiscUtils {


    public static int[] hsvToRgb(float h, float s, float v) {
        h = Math.max(0, Math.min(h, 360));
        s = Math.max(0, Math.min(s, 1));
        v = Math.max(0, Math.min(v, 1));

        if (s == 0) {
            int gray = (int) (v * 255);
            return new int[]{gray, gray, gray};
        }

        h /= 60;
        int i = (int) Math.floor(h);
        float f = h - i;
        float p = v * (1 - s);
        float q = v * (1 - s * f);
        float t = v * (1 - s * (1 - f));

        switch (i) {
            case 0:
                return new int[]{(int) (v * 255), (int) (t * 255), (int) (p * 255)};
            case 1:
                return new int[]{(int) (q * 255), (int) (v * 255), (int) (p * 255)};
            case 2:
                return new int[]{(int) (p * 255), (int) (v * 255), (int) (t * 255)};
            case 3:
                return new int[]{(int) (p * 255), (int) (q * 255), (int) (v * 255)};
            case 4:
                return new int[]{(int) (t * 255), (int) (p * 255), (int) (v * 255)};
            default: // case 5:
                return new int[]{(int) (v * 255), (int) (p * 255), (int) (q * 255)};
        }
    }
}
