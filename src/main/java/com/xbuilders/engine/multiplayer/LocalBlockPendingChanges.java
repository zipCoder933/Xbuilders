package com.xbuilders.engine.multiplayer;

import com.xbuilders.engine.player.Player;
import com.xbuilders.engine.player.pipeline.BlockHistory;
import com.xbuilders.engine.utils.ErrorHandler;
import com.xbuilders.engine.world.WorldInfo;
import org.joml.Vector3i;

import java.io.*;
import java.nio.file.Files;
import java.util.Map;

public class LocalBlockPendingChanges extends PlayerBlockPendingChanges {
    public boolean needsSaving = false;

    public LocalBlockPendingChanges(Player player) {
        super(null, player);
    }

    protected void changeEvent() {
        needsSaving = true;
    }

    public void save(WorldInfo worldInfo) {
        saveRecord(new File(worldInfo.getDirectory(), blockChangeFile));
    }

    public void load(WorldInfo worldInfo) {
        clear();
        File changeGile = new File(worldInfo.getDirectory(), blockChangeFile);
        if (changeGile.exists()) {
            loadRecord(changeGile);
        }
    }

    private void saveRecord(File file) {
        readLock.lock();
        try (FileOutputStream fos = new FileOutputStream(file)) {
            for (Map.Entry<Vector3i, BlockHistory> entry : record.entrySet()) {
                Vector3i worldPos = entry.getKey();
                BlockHistory change = entry.getValue();
                record(fos, worldPos, change);
                return;
            }
        } catch (IOException e) {
            ErrorHandler.handleFatalError(e);
        } finally {
            readLock.unlock();
        }
    }

    private void loadRecord(File file) {
        readLock.lock();
        try {
            byte[] bytes = Files.readAllBytes(file.toPath());
            readBlockChange(bytes, (worldPos, change) -> record.put(worldPos, change));
        } catch (IOException e) {
            ErrorHandler.handleFatalError(e);
        } finally {
            readLock.unlock();
        }
    }


    final String blockChangeFile = "blockChanges.bin";


}