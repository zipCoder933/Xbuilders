package com.xbuilders.engine.builtinMechanics.gravityBlock;

import com.xbuilders.engine.items.ItemList;
import com.xbuilders.engine.items.entity.Entity;
import com.xbuilders.engine.rendering.block.BlockMesh;
import com.xbuilders.engine.rendering.block.BlockVertexSet;
import com.xbuilders.engine.rendering.block.meshers.Block_NaiveMesher;
import com.xbuilders.engine.world.chunk.ChunkVoxels;
import com.xbuilders.game.MyGame;
import org.joml.Vector3i;
import org.lwjgl.system.MemoryStack;

public class GravityBlockEntity extends Entity {
    private final BlockVertexSet buffer = new BlockVertexSet();
    private BlockMesh mesh;
    private Block_NaiveMesher mesher;

    public GravityBlockEntity() {
        super();
    }

    @Override
    public void initializeOnDraw(byte[] bytes) {
        mesh = new BlockMesh();
        mesh.setTextureID(ItemList.blocks.textures.getTexture().id);
        ChunkVoxels voxels = new ChunkVoxels(1, 1, 1);
        voxels.setBlock(0, 0, 0, MyGame.BLOCK_SAND);


        try (MemoryStack stack = MemoryStack.stackPush()) {
            buffer.reset();
            mesher = new Block_NaiveMesher(voxels, new Vector3i(0, 0, 0), true);
            mesher.compute(buffer, buffer, stack, 1, false);

            if (buffer.size() != 0) {
                buffer.makeVertexSet();
            }
            buffer.sendToMesh(mesh);
        }
    }

    @Override
    public void draw() {
        mesh.draw(true);
    }
}
