/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.xbuilders.content.vanilla.items.entities;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.JsonNode;
import com.xbuilders.engine.server.model.GameScene;
import com.xbuilders.engine.server.model.items.entity.Entity;
import com.xbuilders.engine.client.visuals.rendering.entity.EntityMesh;
import com.xbuilders.engine.utils.ErrorHandler;
import com.xbuilders.engine.utils.ResourceUtils;
import com.xbuilders.content.vanilla.items.blocks.RenderType;
import com.xbuilders.window.utils.texture.TextureUtils;

import java.io.IOException;

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
    public void serializeDefinitionData(JsonGenerator generator) throws IOException {
        super.serializeDefinitionData(generator);//Always call super!
        generator.writeNumberField("XZ", xzOrientation);
        generator.writeBooleanField("fencepost", againstFencepost);
    }

    @Override
    public void loadDefinitionData(boolean hasData, JsonParser parser, JsonNode node) throws IOException {
        super.loadDefinitionData(hasData, parser, node);//Always call super!
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
        if (hasData) {
            if (node.has("XZ")) xzOrientation = node.get("XZ").asInt();
            if (node.has("fencepost")) againstFencepost = node.get("fencepost").asBoolean();
        } else {
            xzOrientation = GameScene.userPlayer.camera.simplifiedPanTilt.x;
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

