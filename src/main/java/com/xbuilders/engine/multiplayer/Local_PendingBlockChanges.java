package com.xbuilders.engine.multiplayer;

import com.xbuilders.engine.player.Player;
import com.xbuilders.engine.player.pipeline.BlockHistory;
import com.xbuilders.engine.utils.ErrorHandler;
import com.xbuilders.engine.world.WorldInfo;
import org.joml.Vector3i;

import java.io.*;
import java.nio.file.Files;
import java.util.Map;

public class Local_PendingBlockChanges extends PendingBlockChanges {
    public boolean needsSaving = false;

    public Local_PendingBlockChanges(Player player) {
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
            for (Map.Entry<Vector3i, BlockHistory> entry : blockChanges.entrySet()) {
                Vector3i worldPos = entry.getKey();
                BlockHistory change = entry.getValue();
                blockChangeRecord(fos, worldPos, change);
                return;
            }
        } catch (IOException e) {
            ErrorHandler.report(e);
        } finally {
            readLock.unlock();
        }
    }

    private void loadRecord(File file) {
        readLock.lock();
        try {
            byte[] bytes = Files.readAllBytes(file.toPath());
            readBlockChange(bytes, (worldPos, change) -> blockChanges.put(worldPos, change));
        } catch (IOException e) {
            ErrorHandler.report(e);
        } finally {
            readLock.unlock();
        }
    }


    final String blockChangeFile = "blockChanges.bin";


}
