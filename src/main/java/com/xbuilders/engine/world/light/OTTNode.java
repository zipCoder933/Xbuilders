package com.xbuilders.engine.world.light;

import com.xbuilders.engine.world.wcc.ChunkNode;

class OTTNode
{
    ChunkNode node;
    int lightVal;

    public OTTNode(final ChunkNode node, final int lightVal) {
        this.node = node;
        this.lightVal = lightVal;
    }
}