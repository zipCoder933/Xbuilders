/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.xbuilders.game.items.entities.animal.landAndWater;

import com.xbuilders.engine.items.entity.Entity;
import com.xbuilders.engine.items.entity.EntityLink;
import com.xbuilders.engine.rendering.entity.EntityMesh;
import com.xbuilders.engine.utils.ErrorHandler;
import com.xbuilders.engine.utils.ResourceUtils;
import com.xbuilders.game.items.entities.animal.mobile.AnimalUtils;
import com.xbuilders.game.items.entities.animal.mobile.Limb;
import com.xbuilders.window.BaseWindow;
import com.xbuilders.window.utils.texture.TextureUtils;
import org.joml.Vector3f;

import java.io.IOException;
import java.util.Objects;

/**
 * @author zipCoder933
 */
public class BeaverEntityLink extends EntityLink {

    public BeaverEntityLink(BaseWindow window, int id, String name) {
        super(id, name);
        supplier = () -> new Beaver(window, this);
        setIcon("beaver egg.png");
        tags.add("animal");
        tags.add("beaver");
    }

    public EntityMesh body, head, tail, legs;


    @Override
    public void initializeEntity(Entity e, byte[] loadBytes) {
        if (body == null) {
            body = new EntityMesh();
            head = new EntityMesh();
            tail = new EntityMesh();
            legs = new EntityMesh();
            try {
                int bodyTexture = Objects.requireNonNull(TextureUtils.loadTexture(
                        ResourceUtils.resource("items\\entity\\animal\\beaver\\body.png").getAbsolutePath(),
                        false)).id;

                int headTexture = Objects.requireNonNull(TextureUtils.loadTexture(
                        ResourceUtils.resource("items\\entity\\animal\\beaver\\head.png").getAbsolutePath(),
                        false)).id;

                int tailTexture = Objects.requireNonNull(TextureUtils.loadTexture(
                        ResourceUtils.resource("items\\entity\\animal\\beaver\\tail.png").getAbsolutePath(),
                        false)).id;

                int legsTexture = Objects.requireNonNull(TextureUtils.loadTexture(
                        ResourceUtils.resource("items\\entity\\animal\\beaver\\back leg.png").getAbsolutePath(),
                        false)).id;

                body.loadFromOBJ(ResourceUtils.resource("items\\entity\\animal\\beaver\\body.obj"));
                body.setTextureID(bodyTexture);

                head.loadFromOBJ(ResourceUtils.resource("items\\entity\\animal\\beaver\\head.obj"));
                head.setTextureID(headTexture);

                tail.loadFromOBJ(ResourceUtils.resource("items\\entity\\animal\\beaver\\tail.obj"));
                tail.setTextureID(tailTexture);

                legs.loadFromOBJ(ResourceUtils.resource("items\\entity\\animal\\beaver\\legs.obj"));
                legs.setTextureID(legsTexture);

            } catch (IOException ex) {
                ErrorHandler.report(ex);
            }
        }
        super.initializeEntity(e, loadBytes); //we MUST ensure this is called
    }


    public static class Beaver<T extends BeaverEntityLink> extends LandAndWaterAnimal {
        T link;
        final static int LIMB_HEAD = 0;
        final static int LIMB_TAIL = 1;
        final static int LIMB_LEGS = 2;

        //For now this is the only way to add offsets.
        //Blender and blockbench do not support this.
        //+Z is forward
        //+X is right
        final static Vector3f LIMB_HEAD_OFFSET = new Vector3f(0, -.5f, .3f);
        final static Vector3f LIMB_TAIL_OFFSET = new Vector3f(0, 0, 0);
        final static Vector3f LIMB_LEGS_OFFSET = new Vector3f(0, 0, 0);

        public Beaver(BaseWindow window, T link) {
            super(window);
            this.link = link;
            aabb.setOffsetAndSize(.8f, 1f, .8f, true);
            frustumSphereRadius = 2;
            setMaxSpeed(0.1f);
            setActivity(0.5f);

            freezeMode = false; //Freeze mode

            limbs = new Limb[]{
                    new Limb((limbMatrix) -> {
                        limbMatrix.translate(LIMB_HEAD_OFFSET);
                        if (distToPlayer < 5) {
                            AnimalUtils.rotateToFacePlayer(limbMatrix);
                        }
                        limbMatrix.updateAndSendToShader(shader.getID(), shader.uniform_modelMatrix);
                        link.head.draw(false);
                    }),
                    new Limb((limbMatrix) -> {
                        link.tail.draw(false);
                    }),
                    new Limb((limbMatrix) -> {
                        link.legs.draw(false);
                    }),
            };
        }

        @Override
        public void animal_drawBody() {
            modelMatrix.update();
            modelMatrix.sendToShader(shader.getID(), shader.uniform_modelMatrix);
            link.body.draw(false);
        }
    }
}
