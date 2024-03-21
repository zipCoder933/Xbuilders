/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.xbuilders.engine.utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.Channel;

/**
 *
 * @author zipCoder933
 */
public class ResourceUtils {

    //files
    public static File RESOURCE_DIR;
    public static File APP_DATA_DIR;
    public static File WORLDS_DIR;
    public static File LOCAL_DIR;

    //Individual paths
    public static File BLOCK_ICON_DIR, DEFAULT_ICON, BLOCK_TEXTURE_DIR, ICONS_DIR;

    static {
        System.out.println("RESOURCES:");
        LOCAL_DIR = new File(System.getProperty("user.dir"));
        RESOURCE_DIR = new File(LOCAL_DIR, "res");
        APP_DATA_DIR = new File(System.getenv("LOCALAPPDATA"), "xbuilders3");

        RESOURCE_DIR.mkdirs();
        APP_DATA_DIR.mkdirs();

        System.out.println("\tLocal path: " + LOCAL_DIR);
        System.out.println("\tResource path: " + RESOURCE_DIR);
        System.out.println("\tApp Data path: " + APP_DATA_DIR);

        BLOCK_ICON_DIR = resource("items\\blocks\\icons");
        DEFAULT_ICON = resource("items\\defaultIcon.png");
        BLOCK_TEXTURE_DIR = new File(ResourceUtils.RESOURCE_DIR + "\\items\\blocks\\textures");
        ICONS_DIR = new File(ResourceUtils.RESOURCE_DIR + "\\items\\icons");
    }

    public static void initialize(boolean gameDevResources) {
        WORLDS_DIR = new File(APP_DATA_DIR, (gameDevResources ? "game_dev" : "game"));
        WORLDS_DIR.mkdirs();
        System.out.println("\tGame path: " + WORLDS_DIR);
    }

    public static File localResource(String path) {
        return new File(LOCAL_DIR, path);
    }

    public static File resource(String path) {
        return new File(RESOURCE_DIR, path);
    }

    public static File appDataResource(String path) {
        return new File(APP_DATA_DIR, path);
    }

    public static File gameResource(String path) {
        return new File(WORLDS_DIR, path);
    }

    public static boolean fileIsInUse(File file) {
        boolean used;
        Channel channel = null;
        try {
            channel = new RandomAccessFile(file, "rw").getChannel();
            used = false;
        } catch (FileNotFoundException ex) {
            used = true;
        } finally {
            if (channel != null) {
                try {
                    channel.close();
                } catch (IOException ex) {
                    // exception handling
                }
            }
        }
        return used;
    }

}
