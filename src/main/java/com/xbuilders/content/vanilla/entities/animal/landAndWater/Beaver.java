/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.xbuilders.content.vanilla.entities.animal.landAndWater;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.JsonNode;
import com.xbuilders.content.vanilla.entities.animal.mobile.LandAndWaterAnimal;
import com.xbuilders.engine.client.ClientWindow;
import com.xbuilders.engine.client.visuals.gameScene.rendering.entity.EntityMesh;
import com.xbuilders.engine.utils.ErrorHandler;
import com.xbuilders.content.vanilla.entities.animal.mobile.AnimalUtils;
import com.xbuilders.engine.server.entity.Limb;
import com.xbuilders.window.utils.texture.TextureUtils;
import org.joml.Vector3f;

import java.io.IOException;
import java.util.Objects;

/**
 * @author zipCoder933
 */


public class Beaver extends LandAndWaterAnimal {


    static EntityMesh body, head, tail, legs;
    static int bodyTexture, headTexture, tailTexture, legsTexture;

    //For now this is the only way to add offsets.
    //Blender and blockbench do not support this.
    //+Z is forward
    //+X is right
    final static Vector3f LIMB_HEAD_OFFSET = new Vector3f(0, -.5f, .3f);
    final static Vector3f LIMB_TAIL_OFFSET = new Vector3f(0, 0, 0);
    final static Vector3f LIMB_LEGS_OFFSET = new Vector3f(0, 0, 0);

    @Override
    public void loadDefinitionData(boolean hasData, JsonParser parser, JsonNode node) throws IOException {
        super.loadDefinitionData(hasData, parser, node);//Always call super!
        if (body == null) {
            body = new EntityMesh();
            head = new EntityMesh();
            tail = new EntityMesh();
            legs = new EntityMesh();
            try {
                bodyTexture = Objects.requireNonNull(TextureUtils.loadTextureFromResource(
                        "assets/xbuilders/entities/animal\\beaver\\body.png",
                        false)).id;

                headTexture = Objects.requireNonNull(TextureUtils.loadTextureFromResource(
                        "assets/xbuilders/entities/animal\\beaver\\head.png",
                        false)).id;

                tailTexture = Objects.requireNonNull(TextureUtils.loadTextureFromResource(
                        "assets/xbuilders/entities/animal\\beaver\\tail.png",
                        false)).id;

                legsTexture = Objects.requireNonNull(TextureUtils.loadTextureFromResource(
                        "assets/xbuilders/entities/animal\\beaver\\back leg.png",
                        false)).id;

                body.loadFromOBJ(resourceLoader.getResourceAsStream("assets/xbuilders/entities/animal\\beaver\\body.obj"));
                head.loadFromOBJ(resourceLoader.getResourceAsStream("assets/xbuilders/entities/animal\\beaver\\head.obj"));
                tail.loadFromOBJ(resourceLoader.getResourceAsStream("assets/xbuilders/entities/animal\\beaver\\tail.obj"));
                legs.loadFromOBJ(resourceLoader.getResourceAsStream("assets/xbuilders/entities/animal\\beaver\\legs.obj"));


            } catch (IOException ex) {
                ErrorHandler.report(ex);
            }
        }
    }

    public Beaver(long uniqueIdentifier, ClientWindow window) {
        super(uniqueIdentifier, window);
        aabb.setOffsetAndSize(.8f, 1f, .8f, true);
        frustumSphereRadius = 2;
        setMaxSpeed(0.1f);
        setActivity(0.5f);

        freezeMode = false; //Freeze mode

        limbs = new Limb[]{
                new Limb((limbMatrix) -> {
                    limbMatrix.translate(LIMB_HEAD_OFFSET);
                    if (distToPlayer < 6) {
                        AnimalUtils.rotateToFacePlayer(limbMatrix);
                    }
                    limbMatrix.updateAndSendToShader(shader.getID(), shader.uniform_modelMatrix);
                    head.draw(false, headTexture);
                }),
                new Limb((limbMatrix) -> {
                    tail.draw(false, tailTexture);
                }),
                new Limb((limbMatrix) -> {
                    legs.draw(false, legsTexture);
                }),
        };
    }

    @Override
    public void animal_drawBody() {
        modelMatrix.update();
        modelMatrix.sendToShader(shader.getID(), shader.uniform_modelMatrix);
        body.draw(false, bodyTexture);
    }
}

