package com.xbuilders.content.vanilla.items.blocks;

import com.xbuilders.engine.server.Server;
import com.xbuilders.engine.server.items.block.Block;
import com.xbuilders.engine.server.items.block.construction.BlockTexture;
import com.xbuilders.engine.server.items.item.StorageSpace;
import com.xbuilders.engine.utils.ErrorHandler;
import com.xbuilders.engine.server.world.chunk.BlockData;
import org.joml.Vector3f;

import java.io.IOException;

public class BlockFlag extends Block {
    public BlockFlag(short id) {
        super(id, "xbuilders:flag_block",
                new BlockTexture(
                        "symbols/flag top",
                        "symbols/flag top",
                        "symbols/flag",
                        "symbols/flag",
                        "symbols/flag",
                        "symbols/flag"));
        setBlockEvent(false, (x, y, z) -> {
            try {
                byte[] bytes = Server.userPlayer.inventory.writeToJson();
                BlockData data = new BlockData(bytes);
                System.out.println("Flag placed " + new String(bytes));
                Server.setBlockData(data, x, y, z);
                Server.userPlayer.inventory.clear();
            } catch (IOException e) {
                ErrorHandler.report(e);
            }
        });

        removeBlockEvent(false, (x, y, z, hist) -> {
            try {
                BlockData data = hist.previousBlockData;
                if (data == null) return;
                System.out.println("Flag removed " + new String(data.toByteArray()));
                StorageSpace storage = new StorageSpace(Server.userPlayer.inventory.size());
                storage.loadFromJson(data.toByteArray());


                //Add items to inventory.
                for (int i = 0; i < storage.size(); i++) {
                    if (storage.get(i) != null) {
                        if (Server.userPlayer.inventory.acquireItem(storage.get(i)) == -1) {
                            System.out.println("\tDropped " + storage.get(i));
                            //Drop item if inventory is full.
                            Server.placeItemDrop(new Vector3f(x, y, z), storage.get(i), false);
                        }
                    }
                }
            } catch (IOException e) {
                ErrorHandler.report(e);
            }
        });
        torchlightStartingValue = 15;
        solid = false;
        opaque = false;
    }

}
