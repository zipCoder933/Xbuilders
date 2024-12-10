package com.xbuilders.game.vanilla.items.blocks;

import com.xbuilders.engine.MainWindow;
import com.xbuilders.engine.gameScene.GameScene;
import com.xbuilders.engine.items.block.Block;
import com.xbuilders.engine.items.block.construction.BlockTexture;
import com.xbuilders.engine.utils.ErrorHandler;
import com.xbuilders.engine.world.chunk.BlockData;
import com.xbuilders.engine.world.chunk.Chunk;
import com.xbuilders.engine.world.wcc.WCCi;
import org.lwjgl.openal.SOFTGainClampEx;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class BlockFlag extends Block {
    public BlockFlag() {
        super(231, "xbuilders:flag_block",
                new BlockTexture(
                        "symbols/flag top",
                        "symbols/flag top",
                        "symbols/flag",
                        "symbols/flag",
                        "symbols/flag",
                        "symbols/flag"));
        setBlockEvent(false, (x, y, z) -> {
            GameScene.player.status_spawnPosition.set(x, y, z);
            try {
                byte[] bytes = GameScene.player.inventory.writeToJson();
                BlockData data = new BlockData(bytes);
                System.out.println("Flag placed "+new String(bytes));
                GameScene.setBlockData(data, x, y, z);

//                BlockData data = GameScene.world.getBlockData(x, y, z);
            } catch (IOException e) {
                ErrorHandler.report(e);
            }
        });

        removeBlockEvent(false, (x, y, z, hist) -> {
            try {
                BlockData data = GameScene.world.getBlockData(x, y, z);
                System.out.println("Flag removed data is null");
                if (data == null) return;
                System.out.println("Flag removed "+new String(data.toByteArray()));
                GameScene.player.inventory.loadFromJson(data.toByteArray());
            } catch (IOException e) {
                ErrorHandler.report(e);
            }
        });
        torchlightStartingValue = 15;
        solid = false;
        opaque = false;
    }

}
