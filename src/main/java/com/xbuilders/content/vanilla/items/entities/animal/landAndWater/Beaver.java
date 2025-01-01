/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.xbuilders.content.vanilla.items.entities.animal.landAndWater;

import com.xbuilders.engine.MainWindow;
import com.xbuilders.engine.client.visuals.rendering.entity.EntityMesh;
import com.xbuilders.engine.utils.ErrorHandler;
import com.xbuilders.engine.utils.ResourceUtils;
import com.xbuilders.content.vanilla.items.entities.animal.mobile.AnimalUtils;
import com.xbuilders.content.vanilla.items.entities.animal.mobile.Limb;
import com.xbuilders.window.utils.texture.TextureUtils;
import org.joml.Vector3f;

import java.io.IOException;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

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
    public void load(byte[] serializedBytes, AtomicInteger start) {
        super.load(serializedBytes, start);
        if (body == null) {
            body = new EntityMesh();
            head = new EntityMesh();
            tail = new EntityMesh();
            legs = new EntityMesh();
            try {
                bodyTexture = Objects.requireNonNull(TextureUtils.loadTexture(
                        ResourceUtils.resource("items\\entity\\animal\\beaver\\body.png").getAbsolutePath(),
                        false)).id;

                headTexture = Objects.requireNonNull(TextureUtils.loadTexture(
                        ResourceUtils.resource("items\\entity\\animal\\beaver\\head.png").getAbsolutePath(),
                        false)).id;

                tailTexture = Objects.requireNonNull(TextureUtils.loadTexture(
                        ResourceUtils.resource("items\\entity\\animal\\beaver\\tail.png").getAbsolutePath(),
                        false)).id;

                legsTexture = Objects.requireNonNull(TextureUtils.loadTexture(
                        ResourceUtils.resource("items\\entity\\animal\\beaver\\back leg.png").getAbsolutePath(),
                        false)).id;

                body.loadFromOBJ(ResourceUtils.resource("items\\entity\\animal\\beaver\\body.obj"));


                head.loadFromOBJ(ResourceUtils.resource("items\\entity\\animal\\beaver\\head.obj"));


                tail.loadFromOBJ(ResourceUtils.resource("items\\entity\\animal\\beaver\\tail.obj"));


                legs.loadFromOBJ(ResourceUtils.resource("items\\entity\\animal\\beaver\\legs.obj"));


            } catch (IOException ex) {
                ErrorHandler.report(ex);
            }
        }
    }

    public Beaver(int id, long uniqueIdentifier, MainWindow window) {
        super(id, uniqueIdentifier, window);
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

