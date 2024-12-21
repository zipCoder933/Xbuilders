package com.xbuilders.engine.game.multiplayer;

import com.xbuilders.engine.MainWindow;
import com.xbuilders.engine.game.model.player.Player;
import com.xbuilders.engine.game.model.player.pipeline.BlockHistory;
import com.xbuilders.engine.utils.ErrorHandler;
import com.xbuilders.engine.game.model.world.data.WorldData;
import org.joml.Vector3i;

import java.io.*;
import java.nio.file.Files;
import java.util.Iterator;
import java.util.Map;
import java.util.function.BiConsumer;

public class Local_MultiplayerPendingBlockChanges extends MultiplayerPendingBlockChanges {


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


    public Local_MultiplayerPendingBlockChanges(Player player) {
        super(null, player);
    }

    protected void changeEvent() {
        needsSaving = true;
    }


    private boolean needsSaving = false;


    public void save(WorldData worldInfo) {
        if (needsSaving) {
            MainWindow.printlnDev("Saving mpbc... Changes " + blockChanges.size());
            File file = new File(worldInfo.getDirectory(), CHANGE_FILE);
            try (FileOutputStream fos = new FileOutputStream(file)) {
                for (Map.Entry<Vector3i, BlockHistory> entry : blockChanges.entrySet()) {
                    Vector3i worldPos = entry.getKey();
                    BlockHistory change = entry.getValue();
                    blockChangeRecord(fos, worldPos, change); //Save the record
                }
            } catch (IOException e) {
                ErrorHandler.report(e);
            }

            needsSaving = false;
        }

    }

    final String CHANGE_FILE = "blockChanges.bin";

    public void load(WorldData worldInfo) {
        clear();
        File file = new File(worldInfo.getDirectory(), CHANGE_FILE);
        if (file.exists()) {
            try {
                byte[] bytes = Files.readAllBytes(file.toPath());
                readBlockChange(bytes, blockChanges::put);
            } catch (IOException e) {
                ErrorHandler.report(e);
            }
        }
    }


}
