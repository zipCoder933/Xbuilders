package com.xbuilders.engine.multiplayer;

import com.xbuilders.engine.player.Player;
import com.xbuilders.engine.player.pipeline.BlockHistory;
import com.xbuilders.engine.utils.ErrorHandler;
import com.xbuilders.engine.world.WorldInfo;
import org.joml.Vector3i;

import java.io.*;
import java.nio.file.Files;
import java.util.Iterator;
import java.util.Map;
import java.util.function.BiConsumer;

public class Local_PendingBlockChanges extends PendingBlockChanges {
    public boolean needsSaving = false;


    public int readApplicableChanges(BiConsumer<Vector3i, BlockHistory> changes) {
        int changesToBeSent = 0;
        if (this.blockChanges.isEmpty()) return 0;
        Iterator<Map.Entry<Vector3i, BlockHistory>> iterator = this.blockChanges.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<Vector3i, BlockHistory> entry = iterator.next();
            Vector3i worldPos = entry.getKey();
            BlockHistory change = entry.getValue();
            if (changeCanBeLoaded(player, worldPos)) {
                changes.accept(worldPos, change);
                iterator.remove(); // Remove it so we don't send it again
                changeEvent();
                changesToBeSent++;
            }
        }
        return changesToBeSent;
    }


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
        File file = new File(worldInfo.getDirectory(), blockChangeFile);
        if (file.exists()) {
            loadRecord(file);
        }
    }

    private void saveRecord(File file) {
        try (FileOutputStream fos = new FileOutputStream(file)) {
            for (Map.Entry<Vector3i, BlockHistory> entry : blockChanges.entrySet()) {
                Vector3i worldPos = entry.getKey();
                BlockHistory change = entry.getValue();
                blockChangeRecord(fos, worldPos, change);
                return;
            }
        } catch (IOException e) {
            ErrorHandler.report(e);
        }
    }

    private void loadRecord(File file) {
        try {
            byte[] bytes = Files.readAllBytes(file.toPath());
            readBlockChange(bytes, (worldPos, change) -> blockChanges.put(worldPos, change));
        } catch (IOException e) {
            ErrorHandler.report(e);
        }
    }

    final String blockChangeFile = "blockChanges.bin";
}
