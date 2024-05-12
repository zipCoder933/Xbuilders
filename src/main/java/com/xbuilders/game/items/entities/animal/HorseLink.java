package com.xbuilders.game.items.entities.animal;

import com.xbuilders.engine.items.Entity;
import com.xbuilders.engine.items.EntityLink;
import com.xbuilders.engine.rendering.entity.EntityMesh;
import com.xbuilders.engine.utils.ErrorHandler;
import com.xbuilders.engine.utils.ResourceUtils;
import com.xbuilders.game.items.entities.animal.mobile.LandAnimal;
import com.xbuilders.window.BaseWindow;
import com.xbuilders.window.render.MVP;
import com.xbuilders.window.utils.obj.OBJLoader;
import com.xbuilders.window.utils.texture.TextureUtils;
import org.joml.Matrix4f;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;

public class HorseLink extends EntityLink {

    EntityMesh body,saddle;
    String textureName;
    LegPair legs;
    protected String bodyModel = "items\\entity\\animal\\horse\\horse\\body.obj";
    protected String legModel = "items\\entity\\animal\\horse\\horse\\leg.obj";
    protected String texturePre = "items\\entity\\animal\\horse\\";


    public HorseLink(BaseWindow window, int id, String name, String textureName) {
        super(id, name, () -> new HorseMule(window));

        this.textureName = textureName;
        setIcon("horse egg.png");
        tags.add("animal");
    }

    private void initMesh(File bodyOBJ, File legOBJ) {
        /**
         * We only need 1 model. We can reuse the same model for each entity to
         * save vram!
         */
        if (body == null) {
            try {
                int texture = Objects.requireNonNull(TextureUtils.loadTexture(
                        ResourceUtils.resource(texturePre + textureName).getAbsolutePath(),
                        false)).id;

                body = new EntityMesh();
                body.loadFromOBJ(OBJLoader.loadModel(bodyOBJ));
                body.setTextureID(texture);

                EntityMesh legsModel = new EntityMesh();
                legsModel.loadFromOBJ(OBJLoader.loadModel(legOBJ));
                legsModel.setTextureID(texture);

                legs = new LegPair(legsModel);

            } catch (IOException ex) {
                ErrorHandler.handleFatalError(ex);
            }
        }
    }

    @Override
    public void initializeEntity(Entity e, ArrayList<Byte> loadBytes) {
        initMesh(ResourceUtils.resource(bodyModel)
                , ResourceUtils.resource(legModel));
        e.initialize(loadBytes); //Initialize the animal
        HorseMule a = (HorseMule) e; //Cast the entity to a fox
        a.animalInit(this, loadBytes); //Initialize the fox by passing the link so that the entity has access to the link variables
    }

    static class HorseMule extends LandAnimal {


        MVP mvp;
        final Matrix4f bodyMatrix = new Matrix4f();

        HorseLink link;

        public HorseMule(BaseWindow window) {
            super(window);
            setSize(1f, 1.5f, 1f, true);
//            freezeMode = true;
            frustumSphereRadius = 2;
        }


        public void animalInit(HorseLink link, ArrayList<Byte> bytes) {
            this.link = link;
            mvp = new MVP();
            goForwardCallback = amount -> {
                legMovement += amount;
            };
        }

        private long lastJumpTime = 0;
        private float legMovement = 0;
        public final float SCALE = 0.6f;
        protected float legXSpacing = 0.32f * SCALE;
        protected float legZSpacing = 0.9f * SCALE;
        protected float legYSpacing = -1.3f * SCALE;

        @Override
        public void draw(Matrix4f projection, Matrix4f view) {
            if (inFrustum) {
                move();

                // box.setToAABB(projection, view, aabb.box);
                // box.draw();
                bodyShader.bind();
                float rotationRadians = (float) Math.toRadians(yRotDegrees);
                bodyMatrix.identity().translate(worldPosition).rotateY(rotationRadians);

                mvp.update(projection, view, bodyMatrix);
                mvp.sendToShader(bodyShader.getID(), bodyShader.mvpUniform);
                link.body.draw(false);


                //Z is the directon of the horse
                link.legs.draw(projection, view, bodyMatrix, bodyShader, legXSpacing, legYSpacing, legZSpacing, legMovement);
                link.legs.draw(projection, view, bodyMatrix, bodyShader, legXSpacing, legYSpacing, -legZSpacing, legMovement);


                pos.update(projection, view);
//                if (Math.abs(pos.collisionHandler.collisionData.penPerAxes.x) > 0.02
//                        || Math.abs(pos.collisionHandler.collisionData.penPerAxes.z) > 0.02) {
//                    if (System.currentTimeMillis() - lastJumpTime > 2000) {
//                        lastJumpTime = System.currentTimeMillis();
//                        pos.jump();
//                    }
//                }
            }
        }
    }
}