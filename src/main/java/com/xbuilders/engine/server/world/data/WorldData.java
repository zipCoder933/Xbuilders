// 
// Decompiled by Procyon v0.5.36
// 
package com.xbuilders.engine.server.world.data;

import com.xbuilders.engine.client.visuals.gameScene.GameScene;
import com.xbuilders.engine.server.GameMode;
import com.xbuilders.engine.utils.ErrorHandler;
import com.xbuilders.engine.server.world.Terrain;
import com.xbuilders.engine.server.world.WorldsHandler;
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

public class WorldData {

    public final int LATEST_TERRAIN_VERSION = 1;
    private File directory;
    final String IMAGE_FILE = "image.png";
    final String INFO_FILENAME = "info.json";
    Image image;
    private String name;
    private static final Gson gson = new JsonManager().gson_itemAdapter;
    public DataFile data;

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
        return this.data.terrainVersion;
    }

    public Vector3f getSpawnPoint() {
        if (this.data.spawnX == -1.0) {
            return null;
        }
        return new Vector3f(
                this.data.spawnX,
                this.data.spawnY,
                this.data.spawnZ);
    }

    public void setSpawnPoint(Vector3f playerPos) {
        this.data.spawnX = (int) playerPos.x;
        this.data.spawnY = (int) playerPos.y;
        this.data.spawnZ = (int) playerPos.z;
    }

    public int getSize() {
        return this.data.size;
    }

    public long getLastSaved() {
        return this.data.lastSaved;
    }

    public String getTerrain() {
        return this.data.terrain;
    }

    public int getSeed() {
        return this.data.seed;
    }


    public WorldData() {
        data = new DataFile();
    }

    public void load(final File directory) throws IOException {
        if (!directory.exists()) {
            throw new IOException("Directory does not exist");
        }
        this.directory = directory;
        this.name = directory.getName();
        try {
            this.data = gson.fromJson(Files.readString(new File(directory, INFO_FILENAME).toPath()), DataFile.class);
        } catch (Exception ex) {
            ErrorHandler.report(
                    "Failed to load world info for world \"" + name + "\"", ex);
        }
    }


    public String toJson() {
        return gson.toJson(data);
    }


    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");

    public void save() throws IOException {
        if (!getDirectory().exists()) {
            getDirectory().mkdirs();
        }
        data.timeOfDay = GameScene.background.getTimeOfDay();
        data.lastSaved = System.currentTimeMillis();
        String json = gson.toJson(data);
        Files.writeString(Paths.get(getDirectory() + "\\" + INFO_FILENAME), json);
    }

    public void makeNew(String name, int size, Terrain terrain, int seed) {
        this.name = name;
        this.data.size = size;
        this.data.terrain = terrain.name;
        this.data.terrainVersion = terrain.version;
        this.data.terrainOptions = new HashMap<>(terrain.options);
        this.data.seed = seed == 0 ? (int) (Math.random() * Integer.MAX_VALUE) : seed;
        this.directory = WorldsHandler.worldFile(name);
    }

    public void makeNew(String name, String json) {
        this.data = gson.fromJson(json, DataFile.class);
        this.name = name;
        this.directory = WorldsHandler.worldFile(name);
    }

    public String getDetails() {
        try {
            return "Name: " + name + "\n"
                    + (data.isJoinedMultiplayerWorld ? "(Joined World)" : "") + "\n"
                    + "\nType: " + data.terrain + "\n"
                    + "\nGame: " + GameMode.values()[data.gameMode] + "\n"
                    + "Last played:\n" + formatTime(getLastSaved()) + "\n"
                    + "Seed: " + data.seed;
        } catch (Exception ex) {
            return "Error getting world details";
        }
    }


    public class DataFile {
        public boolean isJoinedMultiplayerWorld;
        public int size;
        public float spawnX;
        public float spawnY;
        public float spawnZ;
        public int terrainVersion;
        public long lastSaved;
        public String terrain;
        public int seed;
        public int gameMode;
        public double timeOfDay;
        public HashMap<String, Boolean> terrainOptions = new HashMap<>();

        public DataFile() {
            this.spawnX = -1.0f;
            this.spawnY = -1.0f;
            this.spawnZ = -1.0f;
            this.terrainVersion = 0;
            this.gameMode = GameMode.FREEPLAY.ordinal();
            isJoinedMultiplayerWorld = false;
        }

    }

    public String toString() {
        return "WorldInfo:   name=" + name + "  json=" + toJson();
    }
}
