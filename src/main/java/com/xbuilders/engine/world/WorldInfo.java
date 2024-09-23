// 
// Decompiled by Procyon v0.5.36
// 
package com.xbuilders.engine.world;

import com.xbuilders.engine.utils.ErrorHandler;
import org.joml.Vector3f;

import java.nio.file.Files;
import java.io.IOException;

import com.google.gson.Gson;
import com.xbuilders.engine.utils.json.JsonManager;

import java.awt.Image;
import java.io.File;
import java.nio.file.Paths;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;

import org.joml.Vector3i;

import static com.xbuilders.engine.utils.MiscUtils.formatTime;

public class WorldInfo {

    public final int LATEST_TERRAIN_VERSION = 1;
    private File directory;
    final String IMAGE_FILE = "image.png";
    final String INFO_FILENAME = "info.json";
    Image image;
    private String name;
    private static final Gson gson = new JsonManager().gson;
    public InfoFile infoFile;

    public File getChunkFile(Vector3i position) {
        return new File(this.directory.getAbsolutePath(), "chunk " + position.x + " " + position.y + " " + position.z);
    }

    public boolean isChunkFile(File file) {
        return (file.getName().startsWith("chunk"));
    }

    public Vector3i getPositionOfChunkFile(File file) {
        if (isChunkFile(file)) {
            String[] split = file.getName().split(" ");
            return new Vector3i(Integer.parseInt(split[1]), Integer.parseInt(split[2]), Integer.parseInt(split[3]));
        }
        return null;
    }


    public File getDirectory() {
        return this.directory;
    }

    public String getName() {
        return this.name;
    }

    public Image getImage() {
        return this.image;
    }

    public boolean hasImage() {
        return new File(this.getDirectory(), IMAGE_FILE).exists();
    }

    public int getTerrainVersion() {
        return this.infoFile.terrainVersion;
    }

    public Vector3f getSpawnPoint() {
        if (this.infoFile.spawnX == -1.0) {
            return null;
        }
        return new Vector3f(
                this.infoFile.spawnX,
                this.infoFile.spawnY,
                this.infoFile.spawnZ);
    }

    public void setSpawnPoint(Vector3f playerPos) {
        this.infoFile.spawnX = (int) playerPos.x;
        this.infoFile.spawnY = (int) playerPos.y;
        this.infoFile.spawnZ = (int) playerPos.z;
    }

    public int getSize() {
        return this.infoFile.size;
    }

    public long getLastSaved() {
        return this.infoFile.lastSaved;
    }

    public String getTerrain() {
        return this.infoFile.terrain;
    }

    public int getSeed() {
        return this.infoFile.seed;
    }

    public void setSeed(final int seed) throws IOException {
        this.infoFile.seed = seed;
    }

    public WorldInfo() {
        infoFile = new InfoFile();
    }

    public void load(final File directory) throws IOException {
        if (!directory.exists()) {
            throw new IOException("Directory does not exist");
        }
        this.directory = directory;
        this.name = directory.getName();
        try {
            this.infoFile = gson.fromJson(Files.readString(new File(directory, INFO_FILENAME).toPath()), InfoFile.class);
        } catch (Exception ex) {
            ErrorHandler.report(
                    "Failed to load world info for world \"" + name + "\"", ex);
        }
    }


    public String toJson() {
        return gson.toJson(infoFile);
    }


    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");

    public void save() throws IOException {
        if (!getDirectory().exists()) {
            getDirectory().mkdirs();
        }
        infoFile.lastSaved = System.currentTimeMillis();
        String json = gson.toJson(infoFile);
        Files.writeString(Paths.get(getDirectory() + "\\" + INFO_FILENAME), json);
    }

    public void makeNew(String name, int size, Terrain terrain, int seed) {
        this.name = name;
        this.infoFile.size = size;
        this.infoFile.terrain = terrain.name;
        this.infoFile.terrainVersion = terrain.version;
        this.infoFile.terrainOptions = new HashMap<>(terrain.options);
        this.infoFile.seed = seed == 0 ? (int) (Math.random() * Integer.MAX_VALUE) : seed;
        this.directory = WorldsHandler.worldFile(name);
    }

    public void makeNew(String name, String json) {
        this.infoFile = gson.fromJson(json, InfoFile.class);
        this.name = name;
        this.directory = WorldsHandler.worldFile(name);
    }

    public String getDetails() {
        return "Name: " + name + "\n"
                + (infoFile.isJoinedMultiplayerWorld ? "(Joined World)" : "") + "\n"
                + "\nType: " + infoFile.terrain + "\n"
                + "Last saved:\n" + formatTime(getLastSaved()) + "\n"
                + "Seed: " + infoFile.seed;
    }


    public class InfoFile {

        public boolean isJoinedMultiplayerWorld;
        public int size;
        public float spawnX;
        public float spawnY;
        public float spawnZ;
        public int terrainVersion;
        public long lastSaved;
        public String terrain;
        public int seed;
        public HashMap<String, Boolean> terrainOptions = new HashMap<>();

        public InfoFile() {
            this.spawnX = -1.0f;
            this.spawnY = -1.0f;
            this.spawnZ = -1.0f;
            this.terrainVersion = 0;
            isJoinedMultiplayerWorld = false;
        }

    }

    public String toString() {
        return "WorldInfo:   name=" + name + "  json=" + toJson();
    }
}
