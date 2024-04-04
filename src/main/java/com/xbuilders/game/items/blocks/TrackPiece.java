
package com.xbuilders.game.items.blocks;

import com.xbuilders.engine.items.block.Block;
import com.xbuilders.engine.items.block.construction.BlockTexture;

class TrackPiece extends Block {

    public TrackPiece(int id, String name, BlockTexture texture) {
        super(id, name, texture);
        solid = false;
        opaque = false;
        type = RenderType.FLOOR;
    }
}