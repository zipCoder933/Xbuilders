/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.xbuilders.engine.utils;

import com.xbuilders.engine.world.chunk.MeshBundle;
import java.util.Scanner;
import java.util.concurrent.Future;
import org.joml.Vector2d;
import org.joml.Vector2f;
import org.joml.Vector2i;
import org.joml.Vector3d;
import org.joml.Vector3f;
import org.joml.Vector3i;
import org.joml.Vector4d;
import org.joml.Vector4f;
import org.joml.Vector4i;

/**
 *
 * @author zipCoder933
 */
public class MiscUtils {

    // Define the characters for encoding
    private final static String characters = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
// Base of the encoding system
    private final static int base = 42;

    public static String encodeNumber(long number) {
        StringBuilder encodedString = new StringBuilder();
        // Ensure the number is non-negative
        number = Math.abs(number);

        // Encode the number
        while (number > 0) {
            long remainder = number % base;
            encodedString.insert(0, characters.charAt((int) remainder));
            number = number / base;
        }
        return encodedString.toString();
    }

    public static boolean isBlackCube(int x, int y, int z) {
        // Assume that the checkerboard has a size of 1 unit by 1 unit by 1 unit, and
        // that the origin (0, 0, 0) is black
        // To determine the color of any point (x, y, z), you can use the formula: (x +
        // y + z) % 2 == 0
        // This means that if the sum of the coordinates is even, the point is black;
        // otherwise, it is white
        return (x + y + z) % 2 == 0;
    }

    // Print Vector methods
    public static String printVector(Vector2f vec) {
        return vec.x + "," + vec.y;
    }

    public static String printVector(Vector2i vec) {
        return vec.x + "," + vec.y;
    }

    public static String printVector(Vector2d vec) {
        return vec.x + "," + vec.y;
    }

    public static String printVector(Vector3f vec) {
        return vec.x + "," + vec.y + "," + vec.z;
    }

    public static String printVector(Vector3i vec) {
        return vec.x + "," + vec.y + "," + vec.z;
    }

    public static String printVector(Vector3d vec) {
        return vec.x + "," + vec.y + "," + vec.z;
    }

    public static String printVector(Vector4f vec) {
        return vec.x + "," + vec.y + "," + vec.z + "," + vec.w;
    }

    public static String printVector(Vector4i vec) {
        return vec.x + "," + vec.y + "," + vec.z + "," + vec.w;
    }

    public static String printVector(Vector4d vec) {
        return vec.x + "," + vec.y + "," + vec.z + "," + vec.w;
    }
}
