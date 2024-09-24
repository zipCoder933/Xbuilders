package com.xbuilders.engine.builtinMechanics.gravityBlock;

import com.xbuilders.engine.MainWindow;
import com.xbuilders.engine.gameScene.GameScene;
import com.xbuilders.engine.items.ItemList;
import com.xbuilders.engine.items.block.Block;
import com.xbuilders.engine.items.entity.Entity;
import com.xbuilders.engine.rendering.block.BlockMesh;
import com.xbuilders.engine.rendering.block.BlockShader;
import com.xbuilders.engine.rendering.block.BlockVertexSet;
import com.xbuilders.engine.rendering.block.meshers.Block_NaiveMesher;
import com.xbuilders.engine.rendering.wireframeBox.Box;
import com.xbuilders.engine.utils.worldInteraction.collision.PositionHandler;
import com.xbuilders.engine.world.chunk.ChunkVoxels;
import org.joml.Vector3i;
import org.lwjgl.system.MemoryStack;

public class GravityBlockEntity extends Entity {
    private final BlockVertexSet buffer = new BlockVertexSet();
    private final ChunkVoxels voxels = new ChunkVoxels(1, 1, 1);
    private BlockMesh mesh;
    private BlockShader blockShader;

    Box box;
    PositionHandler positionHandler;
    Block block;

    public GravityBlockEntity(MainWindow window) {
        super();
        positionHandler = new PositionHandler(window, GameScene.world, aabb, null, null);
        aabb.setOffsetAndSize(0, 0, 0, 1, 1, 1);
        frustumSphereRadius = 1;
    }

    @Override
    public void initializeOnDraw(byte[] bytes) {
        blockShader = new BlockShader();
        box = new Box();
        box.setColor(1, 0, 0, 1);
        box.setLineWidth(4);
        mesh = new BlockMesh();
        mesh.setTextureID(ItemList.blocks.textures.getTexture().id);
        buffer.reset();
        voxels.setBlock(0, 0, 0, block.id);

        try (MemoryStack stack = MemoryStack.stackPush()) {
            Block_NaiveMesher mesher = new Block_NaiveMesher(voxels, new Vector3i(0, 0, 0), true);
            mesher.compute(buffer, buffer, stack, 1, false);
            buffer.makeVertexSet();
            buffer.sendToMesh(mesh);
        }
    }

    @Override
    public void draw() {
        if (inFrustum) {
            //There is actually something in the buffer
            blockShader.bind();//TODO: Make block shader compatable with the entity shader

            blockShader.updateProjectionViewMatrix(GameScene.projection, GameScene.view);

            modelMatrix.identity().translate(worldPosition.x, worldPosition.y, worldPosition.z);
            modelMatrix.update();
            modelMatrix.sendToShader(blockShader.getID(), blockShader.uniform_modelMatrix);

            mesh.draw(true);

//            box.set(aabb.box);
//            box.draw(GameScene.projection, GameScene.view);
            positionHandler.update(1);
        } else if (MainWindow.frameCount % 5 == 0) {
            positionHandler.update(5);
        }

        if (positionHandler.isFrozen() ||
                positionHandler.collisionHandler.collisionData.block_penPerAxes.y < 0) {
            GameScene.player.setBlock(block.id, (int) worldPosition.x, (int) worldPosition.y, (int) worldPosition.z);
            destroy();
        }


    }
}
