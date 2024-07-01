// 
// Decompiled by Procyon v0.5.36
// 
package com.xbuilders.engine.world;

import org.joml.Vector3f;

import java.nio.file.Files;
import java.io.IOException;

import com.google.gson.Gson;
import com.xbuilders.engine.utils.json.JsonManager;

import java.awt.Image;
import java.io.File;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;

import org.joml.Vector3i;

public class WorldInfo {

    public final int LATEST_TERRAIN_VERSION = 1;
    private File directory;
    final String IMAGE_FILE = "image.png";
    final String INFO_FILENAME = "info.json";
    Image image;
    private String name;
    private static final Gson gson = new JsonManager().gson;
    private InfoFile infoFile;

    public File getChunkFile(Vector3i position) {
        return new File(this.directory.getAbsolutePath(), "chunk " + position.x + " " + position.y + " " + position.z);
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

    public String getLastSaved() {
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
        this.infoFile = gson.fromJson(Files.readString(new File(directory, INFO_FILENAME).toPath()), InfoFile.class);
    }

    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");

    public void save() throws IOException {
        if (!getDirectory().exists()) {
            getDirectory().mkdirs();
        }
        LocalDateTime now = LocalDateTime.now();
        infoFile.lastSaved = now.format(formatter);
        String json = gson.toJson(infoFile);
        Files.writeString(Paths.get(getDirectory() + "\\" + INFO_FILENAME), json);
    }

    public void makeNew(String name, int size, String terrain, int seed) {
        this.name = name;
        this.infoFile.size = size;
        this.infoFile.terrain = terrain;
        this.infoFile.seed = seed == 0 ? (int) (Math.random() * Integer.MAX_VALUE) : seed;
        this.directory = WorldsHandler.worldFile(name);
    }

    public void makeNew(String name, String json) {
        this.name = name;
        loadInfoFileAsJson(json);
    }

    public String getDetails() {
        return "Name: " + name + "\n"
                + "Type: " + infoFile.terrain + "   " + infoFile.terrainOptions.toString() + "\n"
                + "Size: " + getSize() + "\n"
                + "Last saved:\n" + getLastSaved() + "\n"
                + "Seed: " + infoFile.seed;
    }

    public String getInfoFileAsJson() {
        return gson.toJson(infoFile);
    }

    public void loadInfoFileAsJson(String json) {
        gson.fromJson(json, InfoFile.class);
    }

    public class InfoFile {

        public int size;
        public float spawnX;
        public float spawnY;
        public float spawnZ;
        public int terrainVersion;
        public String lastSaved;
        public String terrain;
        public int seed;
        public HashMap<String, Boolean> terrainOptions = new HashMap<>();

        public InfoFile() {
            this.spawnX = -1.0f;
            this.spawnY = -1.0f;
            this.spawnZ = -1.0f;
            this.terrainVersion = 0;
        }

    }
}
