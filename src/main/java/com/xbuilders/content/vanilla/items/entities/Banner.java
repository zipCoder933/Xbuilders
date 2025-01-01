/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.xbuilders.content.vanilla.items.entities;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.xbuilders.engine.server.model.GameScene;
import com.xbuilders.engine.server.model.items.entity.Entity;
import com.xbuilders.engine.client.visuals.rendering.entity.EntityMesh;
import com.xbuilders.engine.utils.ErrorHandler;
import com.xbuilders.engine.utils.ResourceUtils;
import com.xbuilders.content.vanilla.items.blocks.RenderType;
import com.xbuilders.window.utils.texture.TextureUtils;

import java.io.IOException;
import java.util.Arrays;

/**
 * @author zipCoder933
 */
public class Banner extends Entity {
    static EntityMesh body;
    static int texture;

    public Banner(int id, long uniqueIdentifier) {
        super(id, uniqueIdentifier);
        frustumSphereRadius = 2f;
        aabb.isSolid = false;
    }

    int xzOrientation = 0;
    float seed = 0;
    boolean againstFencepost;


    @Override
    public void serializeDefinitionData(Output output, Kryo kyro) throws IOException {
        super.serializeDefinitionData(output, kyro);//Always call super!
        kyro.writeObject(output, (byte) xzOrientation);
        kyro.writeObject(output, againstFencepost);
        System.out.println("\t (pre) Entity bytes: " + Arrays.toString(output.toBytes()));
    }

    @Override
    public void loadDefinitionData(Input input, Kryo kyro) throws IOException {
        super.loadDefinitionData(input, kyro);//Always call super!

        if (body == null) {
            try {
                body = new EntityMesh();
                body.loadFromOBJ(ResourceUtils.resource("items\\entity\\banner\\banner.obj"));
                texture = TextureUtils.loadTexture(
                        ResourceUtils.resource("items\\entity\\banner\\blue.png").getAbsolutePath(), false).id;

            } catch (IOException ex) {
                ErrorHandler.report(ex);
            }
        }


        if (input.available() > 0) {
            xzOrientation = kyro.readObject(input, byte.class);
            againstFencepost = kyro.readObject(input, boolean.class);
        } else {
            xzOrientation = GameScene.player.camera.simplifiedPanTilt.x;
            int wx = (int) worldPosition.x;
            int wy = (int) worldPosition.y;
            int wz = (int) worldPosition.z;

            if (xzOrientation == 0) {
                againstFencepost = GameScene.world.getBlock(wx, wy, wz - 1)
                        .renderType == RenderType.FENCE;
            } else if (xzOrientation == 1) {
                againstFencepost = GameScene.world.getBlock(wx + 1, wy, wz)
                        .renderType == RenderType.FENCE;
            } else if (xzOrientation == 2) {
                againstFencepost = GameScene.world.getBlock(wx, wy, wz + 1)
                        .renderType == RenderType.FENCE;
            } else {
                againstFencepost = GameScene.world.getBlock(wx - 1, wy, wz)
                        .renderType == RenderType.FENCE;
            }
        }

        seed = (float) (Math.random() * 1000);
        aabb.setOffsetAndSize(0, 0, 0,
                1, 2, 1);
    }

    int frameCount = 0;
    final float ONE_SIXTEENTH = 0.16666667f;

    @Override
    public void draw() {
        modelMatrix.identity().translate(worldPosition);
        if (xzOrientation == 0) {
            modelMatrix.translate(0, 0, 1);
            modelMatrix.rotateY((float) (Math.PI / 2));

        } else if (xzOrientation == 2) {
            modelMatrix.translate(1, 0, 0);
            modelMatrix.rotateY((float) -(Math.PI / 2));
        } else if (xzOrientation == 3) {
            modelMatrix.translate(1, 0, 1);
            modelMatrix.rotateY((float) Math.PI);
        }

        if (againstFencepost) {
            modelMatrix.translate(0.4f, 0, 0);
        }
        modelMatrix.translate(1f - (ONE_SIXTEENTH * 2), 0, 0.5f);

        modelMatrix.rotateZ((float) (Math.sin((frameCount * 0.05) + seed) * 0.1) + 0.1f);
        modelMatrix.update();
        modelMatrix.sendToShader(shader.getID(), shader.uniform_modelMatrix);
        body.draw(false, texture);
        frameCount++;
    }


}

