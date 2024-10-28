package com.xbuilders.engine.multiplayer;

import com.xbuilders.engine.gameScene.GameScene;
import com.xbuilders.engine.items.entity.Entity;
import com.xbuilders.engine.items.entity.EntityLink;
import com.xbuilders.engine.player.Player;
import com.xbuilders.engine.player.UserControlledPlayer;
import com.xbuilders.engine.player.pipeline.BlockHistory;
import com.xbuilders.engine.utils.ErrorHandler;
import com.xbuilders.engine.utils.network.server.NetworkSocket;
import com.xbuilders.engine.world.WorldInfo;
import org.joml.Vector3f;
import org.joml.Vector3i;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Map;

//This WILL be another class for 2 reasons
//1) It is less complicated this way
//2) the blockPipeline does not need to know anything aout syncing local entities
public class Local_MultiplayerPendingEntityChanges extends MultiplayerPendingEntityChanges {

    private boolean needsSaving = false;

    public Local_MultiplayerPendingEntityChanges(Player player) {
        super(null, player);

    }

    protected void changeEvent() {
        needsSaving=true;
    }

    public void addEntityChange(int mode, EntityLink entity, long identifier, Vector3f currentPos, byte[] data) {
    }

    public void save(WorldInfo worldInfo) {
//        if (needsSaving) {
//            System.out.println("Saving mpec... Changes " + blockChanges.size());
//            File file = new File(worldInfo.getDirectory(), CHANGE_FILE);
//            try (FileOutputStream fos = new FileOutputStream(file)) {
//                for (Map.Entry<Vector3i, BlockHistory> entry : blockChanges.entrySet()) {
//                    Vector3i worldPos = entry.getKey();
//                    BlockHistory change = entry.getValue();
//                    blockChangeRecord(fos, worldPos, change); //Save the record
//                }
//            } catch (IOException e) {
//                ErrorHandler.report(e);
//            }
//
//            needsSaving = false;
//        }
    }

    final String CHANGE_FILE = "entityChanges.bin";

    public void load(WorldInfo worldInfo) {
//        clear();
//        File file = new File(worldInfo.getDirectory(), CHANGE_FILE);
//        if (file.exists()) {
//            try {
//                byte[] bytes = Files.readAllBytes(file.toPath());
//
//
//                MultiplayerPendingEntityChanges.readEntityChange(bytes, (
//                        mode, entity, identifier, currentPos, data, isControlledByAnotherPlayer) -> {
//
//                       addEntityChange(mode, entity, identifier, currentPos, data);
//
//                });
//
//
//            } catch (IOException e) {
//                ErrorHandler.report(e);
//            }
//        }
    }
}
