package com.xbuilders.engine.builtinMechanics.gravityBlock;

import com.xbuilders.engine.MainWindow;
import com.xbuilders.engine.gameScene.GameScene;
import com.xbuilders.engine.items.ItemList;
import com.xbuilders.engine.items.block.Block;
import com.xbuilders.engine.items.entity.Entity;
import com.xbuilders.engine.rendering.block.BlockMesh;
import com.xbuilders.engine.rendering.block.BlockVertexSet;
import com.xbuilders.engine.rendering.block.meshers.Block_NaiveMesher;
import com.xbuilders.engine.rendering.wireframeBox.Box;
import com.xbuilders.engine.utils.MiscUtils;
import com.xbuilders.engine.utils.math.AABB;
import com.xbuilders.engine.utils.worldInteraction.collision.PositionHandler;
import com.xbuilders.engine.world.World;
import com.xbuilders.engine.world.chunk.ChunkVoxels;
import com.xbuilders.game.MyGame;
import org.joml.Vector3i;
import org.lwjgl.system.MemoryStack;

public class GravityBlockEntity extends Entity {
    private final BlockVertexSet buffer = new BlockVertexSet();
    private BlockMesh mesh;
    private Block_NaiveMesher mesher;
    Box box;
    PositionHandler positionHandler;
    Block block;

    public GravityBlockEntity(MainWindow window) {
        super();
        positionHandler = new PositionHandler(window, GameScene.world, aabb, null, null);
        aabb.setOffsetAndSize(0,0,0,1,1,1);
    }

    @Override
    public void initializeOnDraw(byte[] bytes) {
        box = new Box();
        box.setColor(1, 0, 0, 1);
        box.setLineWidth(4);
        mesh = new BlockMesh();
        mesh.setTextureID(ItemList.blocks.textures.getTexture().id);
        ChunkVoxels voxels = new ChunkVoxels(1, 1, 1);
        voxels.setBlock(0, 0, 0, block.id);
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
//        modelMatrix.sendToShader(shader.getID(), shader.uniform_modelMatrix);
//        mesh.draw(true);

        box.set(aabb.box);
        box.draw(GameScene.projection, GameScene.view);
        positionHandler.update();
        if (positionHandler.collisionHandler.collisionData.totalPenPerAxes.y < 0) {
            destroy();
            //Set a block here
//            GameScene.player.setBlock(block.id, (int) worldPosition.x, (int) worldPosition.y, (int) worldPosition.z);
        }
    }
}
