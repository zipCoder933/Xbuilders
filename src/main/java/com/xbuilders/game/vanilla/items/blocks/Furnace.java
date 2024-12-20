package com.xbuilders.game.vanilla.items.blocks;

import com.xbuilders.engine.MainWindow;
import com.xbuilders.engine.gameScene.GameScene;
import com.xbuilders.engine.items.block.Block;
import com.xbuilders.engine.items.block.construction.BlockTexture;
import com.xbuilders.engine.world.chunk.BlockData;
import com.xbuilders.engine.world.chunk.Chunk;
import com.xbuilders.engine.world.wcc.WCCi;
import org.joml.Vector3i;

public class Furnace extends Block {
    public Furnace(short id) {
        super(id, "xbuilders:furnace", new BlockTexture(
                "furnace_top.png",
                "furnace_top.png",
                "furnace_side.png",
                "furnace_side.png",
                "furnace_side.png",
                "furnace_front.png"));
    }
}
