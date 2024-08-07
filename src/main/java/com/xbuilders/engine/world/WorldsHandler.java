// 
// Decompiled by Procyon v0.5.36
// 
package com.xbuilders.engine.world;

import com.xbuilders.engine.utils.ResourceUtils;
import java.nio.file.Files;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class WorldsHandler {

    public static File worldFile(String name) {
        return new File(ResourceUtils.WORLDS_DIR, formatWorldName(name));
    }

    public static final void makeNewWorld(final WorldInfo info) throws IOException {
        info.getDirectory().mkdirs();
        info.save();
    }

    public static String formatWorldName(final String name) {
        return name.replaceAll("[^A-z\\s0-9_-]", "").replace("^", "").strip();
    }

    public static boolean worldNameAlreadyExists(final String name){
        return worldFile(name).exists();
    }

    public static void deleteWorld(WorldInfo info) throws IOException {
        if (info != null) {
            System.out.println("Deleting " + info.getDirectory().toString());
            for (final File file : info.getDirectory().listFiles()) {
                System.out.println("\tDeleting "+file.toString());
                file.delete();
            }
            Files.delete(info.getDirectory().toPath());
        }
    }

    public static ArrayList<WorldInfo> listWorlds(ArrayList<WorldInfo> worlds) throws IOException {
        worlds.clear();
        for (final File subDir : ResourceUtils.WORLDS_DIR.listFiles()) {
            if (subDir.isDirectory()) {
                WorldInfo info = new WorldInfo();
                try {
                    info.load(subDir);
                    worlds.add(info);
                } catch (IOException ex) {
                    System.out.println("Error loading world " + subDir);
//                    ex.printStackTrace();
                }
            }
        }
        return worlds;
    }
}
