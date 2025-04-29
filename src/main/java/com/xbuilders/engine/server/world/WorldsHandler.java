// 
// Decompiled by Procyon v0.5.36
// 
package com.xbuilders.engine.server.world;

import com.xbuilders.engine.common.utils.ErrorHandler;
import com.xbuilders.engine.common.utils.FileUtils;
import com.xbuilders.engine.common.resource.ResourceUtils;
import com.xbuilders.engine.server.world.data.WorldData;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class WorldsHandler {

    public static File worldFile(String name) {
        return new File(ResourceUtils.WORLDS_DIR, formatWorldName(name));
    }

    public static final void makeNewWorld(final WorldData info) throws IOException {
        info.getDirectory().mkdirs();
        info.save();
    }

    public static String formatWorldName(final String name) {
        return name.replaceAll("[^A-z\\s0-9_-]", "").replace("^", "").strip();
    }

    public static boolean worldNameAlreadyExists(final String name) {
        return worldFile(name).exists();
    }


    public static void deleteWorld(WorldData info) throws IOException {
        if (info != null) {
            FileUtils.moveDirectoryToTrash(info.getDirectory());
        }
    }

    public static ArrayList<WorldData> listWorlds(ArrayList<WorldData> worlds) throws IOException {
        worlds.clear();
        for (final File subDir : ResourceUtils.WORLDS_DIR.listFiles()) {
            if (subDir.isDirectory()) {
                WorldData info = new WorldData();
                try {
                    info.load(subDir);
                    worlds.add(info);
                } catch (IOException ex) {
                    ErrorHandler.report("World \"" + formatWorldName(subDir.getName()) + "\" could not be loaded", ex);
                }
            }
        }
        return worlds;
    }
}
