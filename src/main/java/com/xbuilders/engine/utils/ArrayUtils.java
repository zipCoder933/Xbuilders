/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.xbuilders.engine.utils;

import com.xbuilders.engine.items.block.Block;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.List;

/**
 *
 * @author zipCoder933
 */
public class ArrayUtils {

    public static byte[] ListByteToArray(List<Byte> list) {
        byte[] arr = new byte[list.size()];
        for (int i = 0; i < list.size(); i++) {
            arr[i] = list.get(i);
        }
        return arr;
    }

    public static <T> T[] convert3Dto1D(T[][][] arr3D) {
        T[] flatBlocks = Arrays.stream(arr3D)
                .flatMap(Arrays::stream)
                .flatMap(Arrays::stream)
                .toArray(size -> (T[]) Array.newInstance(arr3D.getClass().getComponentType().getComponentType(), size));
        return flatBlocks;
    }

    public static Byte[] convert3Dto1D(Byte[][][] arr3D) {
        int size = arr3D.length * arr3D[0].length * arr3D[0][0].length;
        Byte[] arr1D = new Byte[size];
        int index = 0;
        for (Byte[][] subArr2D : arr3D) {
            for (Byte[] subArr1D : subArr2D) {
                System.arraycopy(subArr1D, 0, arr1D, index, subArr1D.length);
                index += subArr1D.length;
            }
        }
        return arr1D;
    }

    public static byte[] convert3Dto1D(byte[][][] arr3D) {
        int size = arr3D.length * arr3D[0].length * arr3D[0][0].length;
        byte[] arr1D = new byte[size];
        int index = 0;
        for (byte[][] subArr2D : arr3D) {
            for (byte[] subArr1D : subArr2D) {
                System.arraycopy(subArr1D, 0, arr1D, index, subArr1D.length);
                index += subArr1D.length;
            }
        }
        return arr1D;
    }


    public static <T> T[] concatenateArrays(T[] array1, T[] array2) {
        T[] concatenatedArray = Arrays.copyOf(array1, array1.length + array2.length);
        System.arraycopy(array2, 0, concatenatedArray, array1.length, array2.length);
        return concatenatedArray;
    }

 
}
