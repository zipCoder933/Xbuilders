package com.xbuilders.engine.server.model.builtinMechanics.gravityBlock;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.JsonNode;
import com.xbuilders.engine.MainWindow;
import com.xbuilders.engine.server.model.GameScene;
import com.xbuilders.engine.server.model.items.Registrys;
import com.xbuilders.engine.server.model.items.block.Block;
import com.xbuilders.engine.server.model.items.entity.Entity;
import com.xbuilders.engine.client.visuals.rendering.entity.EntityMesh_ArrayTexture;
import com.xbuilders.engine.client.visuals.rendering.entity.block.BlockVertexSet;
import com.xbuilders.engine.client.visuals.rendering.entity.block.meshers.Block_NaiveMesher;
import com.xbuilders.engine.client.visuals.rendering.wireframeBox.Box;
import com.xbuilders.engine.utils.worldInteraction.collision.PositionHandler;
import com.xbuilders.engine.server.model.world.World;
import com.xbuilders.engine.server.model.world.chunk.ChunkVoxels;
import org.joml.Vector3i;
import org.lwjgl.system.MemoryStack;

public class GravityBlockEntity extends Entity {
    private final BlockVertexSet buffer = new BlockVertexSet();
    private final ChunkVoxels voxels = new ChunkVoxels(1, 1, 1);
    private EntityMesh_ArrayTexture mesh;


    Box box;
    PositionHandler positionHandler;
    Block block;
    long startTime;

    public GravityBlockEntity(long uniqueIdentifier, MainWindow window) {
        super(-1, uniqueIdentifier);
        positionHandler = new PositionHandler(window, GameScene.world, aabb, null);
        aabb.setOffsetAndSize(0, 0, 0, 1, 1, 1);
        frustumSphereRadius = 1;
    }

    @Override
    public void loadDefinitionData(boolean hasData, JsonParser parser, JsonNode node) {
        box = new Box();
        box.setColor(1, 0, 0, 1);
        box.setLineWidth(4);
        mesh = new EntityMesh_ArrayTexture();

        buffer.reset();
        voxels.setBlock(0, 0, 0, block.id);

        try (MemoryStack stack = MemoryStack.stackPush()) {
            Block_NaiveMesher mesher = new Block_NaiveMesher(voxels, new Vector3i(0, 0, 0), true);
            mesher.compute(buffer, buffer, stack, 1, false);
            buffer.makeVertexSet();
            buffer.sendToMesh(mesh);
        }
        startTime = System.currentTimeMillis();
    }

    public static final int WAIT_TIME = 200;

    private void instaPlant() {
        //Set the block at the bottom
        for (int y = (int) (worldPosition.y + 1); y < World.WORLD_BOTTOM_Y; y++) {
            Block blockBelow = GameScene.world.getBlock((int) worldPosition.x, y, (int) worldPosition.z);
            if (blockBelow.solid) {
                GameScene.setBlock(block.id, (int) worldPosition.x, y - 1, (int) worldPosition.z);
                destroy();
            }
        }
    }

    @Override
    public void draw() {
        if (inFrustum) {
            //There is actually something in the buffer
            arrayTextureShader.bind();//TODO: Allow better integration with an arrayTextureShader in Entity class
            arrayTextureShader.setSunAndTorch(sunValue, torchValue);
            arrayTextureShader.updateProjectionViewMatrix(GameScene.projection, GameScene.view);
            modelMatrix.update();
            modelMatrix.sendToShader(arrayTextureShader.getID(), arrayTextureShader.uniform_modelMatrix);
            mesh.draw(false, Registrys.blocks.textures.getTexture().id);

//            box.set(aabb.box);
//            box.draw(GameScene.projection, GameScene.view);
            if (System.currentTimeMillis() - startTime > WAIT_TIME) positionHandler.update(1);
        } else {
            instaPlant();
        }


        if (positionHandler.isFrozen() ||
                positionHandler.collisionHandler.collisionData.block_penPerAxes.y < 0) {
            GameScene.setBlock(block.id, (int) worldPosition.x, (int) worldPosition.y, (int) worldPosition.z);
            destroy();
        }


    }
}
