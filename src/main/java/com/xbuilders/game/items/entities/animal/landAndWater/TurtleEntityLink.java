/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.xbuilders.game.items.entities.animal.landAndWater;

import com.xbuilders.engine.MainWindow;
import com.xbuilders.engine.items.entity.Entity;
import com.xbuilders.engine.items.entity.EntityLink;
import com.xbuilders.engine.rendering.entity.EntityMesh;
import com.xbuilders.engine.utils.ErrorHandler;
import com.xbuilders.engine.utils.ResourceUtils;
import com.xbuilders.engine.utils.math.MathUtils;
import com.xbuilders.window.render.MVP;
import com.xbuilders.window.utils.texture.TextureUtils;

import java.io.IOException;
import java.util.Objects;

/**
 * @author zipCoder933
 */
public class TurtleEntityLink extends EntityLink {

    public TurtleEntityLink(MainWindow window, int id, String name, String textureFile) {
        super(id, name);
        supplier = () -> new Turtle(window, this);
        setIcon("turtle egg.png");
        this.textureFile = textureFile;
        tags.add("animal");
        tags.add("turtle");
    }

    public EntityMesh left_fin, right_fin, left_back_fin, right_back_fin, body;
    public String textureFile;


    @Override
    public void initializeEntity(Entity e, byte[] loadBytes) {
        if (body == null) {
            body = new EntityMesh();
            left_fin = new EntityMesh();
            right_fin = new EntityMesh();
            left_back_fin = new EntityMesh();
            right_back_fin = new EntityMesh();
            try {
                int texture = Objects.requireNonNull(TextureUtils.loadTexture(
                        ResourceUtils.resource("items\\entity\\animal\\turtle\\" + textureFile).getAbsolutePath(),
                        false)).id;

                body.loadFromOBJ(ResourceUtils.resource("items\\entity\\animal\\turtle\\body.obj"));
                left_fin.loadFromOBJ(ResourceUtils.resource("items\\entity\\animal\\turtle\\left_fin.obj"));
                right_fin.loadFromOBJ(ResourceUtils.resource("items\\entity\\animal\\turtle\\right_fin.obj"));

                left_back_fin.loadFromOBJ(ResourceUtils.resource("items\\entity\\animal\\turtle\\left_back_fin.obj"));
                right_back_fin.loadFromOBJ(ResourceUtils.resource("items\\entity\\animal\\turtle\\right_back_fin.obj"));

                body.setTextureID(texture);
                left_fin.setTextureID(texture);
                right_fin.setTextureID(texture);
                left_back_fin.setTextureID(texture);
                right_back_fin.setTextureID(texture);
            } catch (IOException ex) {
                ErrorHandler.report(ex);
            }
        }
        super.initializeEntity(e, loadBytes); //we MUST ensure this is called
    }


    public static class Turtle<T extends TurtleEntityLink> extends LandAndWaterAnimal {

        T link;

        public Turtle(MainWindow window, T link) {
            super(window);
            this.link = link;
            aabb.setOffsetAndSize(1f, 1f, 1f, true);
            frustumSphereRadius = 3;
            setMaxSpeed(0.1f);
            setActivity(0.5f);
        }

        @Override
        public void animal_drawBody() {
            modelMatrix.update();
            modelMatrix.sendToShader(shader.getID(), shader.uniform_modelMatrix);
            link.body.draw(false);

            float animationTarget = 0f;
            if (getWalkAmt() > 0) {
                animationTarget = MathUtils.map(getWalkAmt(), 0, getMaxSpeed(), 0, 0.4f);
            }
            drawFin(link.right_fin, 0, 0, ONE_SIXTEENTH * 7,
                    animationTarget, 0.0f, 0.4f);

            drawFin(link.left_fin, 0, 0, ONE_SIXTEENTH * 7,
                    animationTarget, 1.5f, 0.4f);

            drawFin(link.left_back_fin,
                    0, 0, ONE_SIXTEENTH * -4,
                    animationTarget, 0.0f, 0.05f);

            drawFin(link.right_back_fin,
                    0, 0, ONE_SIXTEENTH * -4,
                    animationTarget, 0.0f, -0.05f);
        }

        private static final float ONE_SIXTEENTH = (float) 1 / 16;
        MVP finModelMatrix = new MVP();

        private void drawFin(EntityMesh fin,
                             float x, float y, float z,
                             float animationSpeed, float animationAdd, float multiplier) {

            finModelMatrix.set(modelMatrix).translate(x, y, z);
            if (animationSpeed != 0) {
                float rot = (float) Math.sin((MainWindow.frameCount * animationSpeed) + animationAdd) * multiplier;
                finModelMatrix.rotateY(rot);
            }
            finModelMatrix.update();
            finModelMatrix.sendToShader(shader.getID(), shader.uniform_modelMatrix);
            fin.draw(false);
        }

    }
}
