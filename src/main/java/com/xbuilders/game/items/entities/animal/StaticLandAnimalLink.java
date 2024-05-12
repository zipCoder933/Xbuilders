package com.xbuilders.game.items.entities.animal;

import com.xbuilders.engine.items.Entity;
import com.xbuilders.engine.items.EntityLink;
import com.xbuilders.engine.rendering.entity.EntityMesh;
import com.xbuilders.engine.rendering.entity.EntityShader;
import com.xbuilders.engine.utils.ErrorHandler;
import com.xbuilders.engine.utils.ResourceUtils;
import com.xbuilders.game.items.entities.animal.mobile.LandAnimal;
import com.xbuilders.window.BaseWindow;
import com.xbuilders.window.render.MVP;
import com.xbuilders.window.utils.obj.OBJ;
import com.xbuilders.window.utils.obj.OBJLoader;
import com.xbuilders.window.utils.texture.TextureUtils;
import org.joml.Matrix4f;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;
import java.util.function.Consumer;

public class StaticLandAnimalLink extends EntityLink {

    EntityMesh body;
    String textureName;
    String modelName;

    public StaticLandAnimalLink(BaseWindow window, int id, String name, String modelName, String textureName) {
        super(id, name, () -> new StaticLandAnimal(window));
        //"\\items\\entity\\animal\\fox\\" +
        //
        this.textureName = textureName;
        this.modelName = modelName;
        setIcon("egg.png");
        tags.add("animal");
    }

    private void initMesh() {
        /**
         * We only need 1 model. We can reuse the same model for each entity to
         * save vram!
         */
        if (body == null) {
            try {
                body = new EntityMesh();
                OBJ loadModel = OBJLoader.loadModel(ResourceUtils.resource(modelName));
                int texture = Objects.requireNonNull(TextureUtils.loadTexture(
                        ResourceUtils.resource(textureName).getAbsolutePath(),
                        false)).id;
                body.loadFromOBJ(loadModel);
                body.setTextureID(texture);
            } catch (IOException ex) {
                ErrorHandler.handleFatalError(ex);
            }
        }
    }

    @Override
    public void initializeEntity(Entity e, ArrayList<Byte> loadBytes) {
        initMesh();
        e.initialize(loadBytes); //Initialize the animal
        StaticLandAnimal a = (StaticLandAnimal) e; //Cast the entity to a fox
        a.animalInit(this, loadBytes); //Initialize the fox by passing the link so that the entity has access to the link variables
    }

    static class StaticLandAnimal extends LandAnimal {
        
        Matrix4f bodyMatrix;
        StaticLandAnimalLink link;

        public StaticLandAnimal(BaseWindow window) {
            super(window);
            setSize(0.8f, 0.9f, 0.8f,true);
            bodyMatrix = new Matrix4f();
            frustumSphereRadius = 2;
        }

        public void animalInit(StaticLandAnimalLink link, ArrayList<Byte> bytes) {
            this.link = link;
        }

        private long lastJumpTime = 0;

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

                pos.update(projection, view);
                if (Math.abs(pos.collisionHandler.collisionData.penPerAxes.x) > 0.01
                        || Math.abs(pos.collisionHandler.collisionData.penPerAxes.z) > 0.01) {
                    if (System.currentTimeMillis() - lastJumpTime > 1000) {
                        lastJumpTime = System.currentTimeMillis();
                        pos.jump();
                    }
                }
            }
        }

    }

}
