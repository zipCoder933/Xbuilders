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
import com.xbuilders.engine.utils.math.MathUtils;
import com.xbuilders.game.Main;
import com.xbuilders.window.BaseWindow;
import com.xbuilders.window.utils.texture.TextureUtils;
import org.joml.Matrix4f;

import java.io.IOException;

/**
 * @author zipCoder933
 */
public class TurtleEntityLink extends EntityLink {

    public TurtleEntityLink(BaseWindow window, int id, String name, String textureFile) {
        super(id, name);
        supplier = () -> new Turtle(window, this);
        setIcon("turtle egg.png");
        this.textureFile = textureFile;
        tags.add("animal");
        tags.add("turtle");
    }

    public EntityMesh fin1, fin2, back_fin1, back_fin2, body;
    public String textureFile;


    @Override
    public void initializeEntity(Entity e, byte[] loadBytes) {
        if (body == null) {
            body = new EntityMesh();
            fin1 = new EntityMesh();
            fin2 = new EntityMesh();
            try {
                int texture = TextureUtils.loadTexture(
                        ResourceUtils.resource("items\\entity\\animal\\turtle\\" + textureFile).getAbsolutePath(),
                        false).id;

                body.loadFromOBJ(ResourceUtils.resource("items\\entity\\animal\\turtle\\body.obj"));
                fin1.loadFromOBJ(ResourceUtils.resource("items\\entity\\animal\\turtle\\left_fin.obj"));
                fin2.loadFromOBJ(ResourceUtils.resource("items\\entity\\animal\\turtle\\right_fin.obj"));
                body.setTextureID(texture);
                fin1.setTextureID(texture);
                fin2.setTextureID(texture);
            } catch (IOException ex) {
                ErrorHandler.report(ex);
            }
        }
        super.initializeEntity(e, loadBytes); //we MUST ensure this is called
    }


    public static class Turtle<T extends TurtleEntityLink> extends LandAndWaterAnimal {

        T link;

        public Turtle(BaseWindow window, T link) {
            super(window);
            this.link = link;
            aabb.setOffsetAndSize(1f, 1f, 1f, true);
            setMaxSpeed(0.1f);
            setActivity(0.5f);
        }

        @Override
        public void draw() {
            move();
            if (inFrustum) {
                float animationTarget = 0f;
                if (getWalkAmt() > 0) {
                    animationTarget = MathUtils.map(getWalkAmt(), 0, getMaxSpeed(), 0, 0.5f);
                }
                float rotationRadians = (float) Math.toRadians(getRotationYDeg());
                modelMatrix.rotateY(rotationRadians);
                modelMatrix.update();
                modelMatrix.sendToShader(shader.getID(), shader.uniform_modelMatrix);
                link.body.draw(false);

                drawFin(link.fin2, 0, 0, ONE_SIXTEENTH * 7,
                        animationTarget, 0.0f, 0.4f);

                drawFin(link.fin1, 0, 0, ONE_SIXTEENTH * 7,
                        animationTarget, 1.5f, 0.4f);

//                drawFin(link.back_fin1,
//                        ONE_SIXTEENTH * -4, ONE_SIXTEENTH * -1, ONE_SIXTEENTH * -10,
//                        finAnimation, 0.0f,  0.15f);
//
//                drawFin( link.back_fin2,
//                        ONE_SIXTEENTH * 4, ONE_SIXTEENTH * -1, ONE_SIXTEENTH * -10,
//                        -finAnimation, 0.0f,  0.15f);
            }
        }

        private static final float ONE_SIXTEENTH = (float) 1 / 16;
        Matrix4f finModelMatrix = new Matrix4f();

        private void drawFin(EntityMesh fin,
                             float x, float y, float z,
                             float animationSpeed, float animationAdd, float multiplier) {

            finModelMatrix.set(modelMatrix).translate(x, y, z);
            float rot = (float) Math.sin((Main.frameCount * animationSpeed) + animationAdd) * multiplier;

            if (animationSpeed != 0) {
                finModelMatrix.rotateY(rot);
            }

            modelMatrix.update(finModelMatrix);
            modelMatrix.sendToShader(shader.getID(), shader.uniform_modelMatrix);
            fin.draw(false);
        }

    }
}
