
package com.xbuilders.game.items.blocks;

import com.xbuilders.engine.gameScene.GameScene;
import com.xbuilders.engine.items.block.Block;
import com.xbuilders.engine.items.block.construction.BlockTexture;
import com.xbuilders.engine.world.chunk.BlockData;

public class TrackPiece extends Block {

    public TrackPiece(int id, String name, BlockTexture texture) {
        super(id, name, texture);
        solid = false;
        opaque = false;
        type = RenderType.FLOOR;

        setBlockEvent(false, ( x,  y,  z,  data)->{
            //There isnt really an issue here.
            GameScene.player.setBlock(this, x - 1, y, z);
            System.out.println("Set track piece at " + x + " " + y + " " + z);
        });
    }

    public TrackPiece(int id, String name, BlockTexture texture, int type) {
        super(id, name, texture);
        solid = false;
        opaque = false;
        this.type = type;
    }
}