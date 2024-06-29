/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.xbuilders.game.items.entities;

import com.xbuilders.engine.gameScene.GameScene;
import com.xbuilders.engine.items.Entity;
import com.xbuilders.engine.items.EntityLink;
import com.xbuilders.engine.rendering.entity.EntityMesh;
import com.xbuilders.engine.utils.ErrorHandler;
import com.xbuilders.engine.utils.ResourceUtils;
import com.xbuilders.engine.world.chunk.XBFilterOutputStream;
import com.xbuilders.game.items.blocks.RenderType;
import org.joml.Matrix4f;

import java.io.IOException;
import java.util.ArrayList;

/**
 * @author zipCoder933
 */
public class BannerEntityLink extends EntityLink {
    EntityMesh body;
    String texturePath;

    public BannerEntityLink(int id, String name, String texturePath) {
        super(id, name);
        this.supplier = Banner::new;
        this.texturePath = texturePath;
        setIcon("banner.png");
        initializationCallback = (entity) -> {

            if (body == null) {
                try {
                    body = new EntityMesh();
                    body.loadFromOBJ(ResourceUtils.resource("items\\entity\\banner\\banner.obj"));
                    body.setTexture(ResourceUtils.resource("items\\entity\\banner\\" + texturePath));
                } catch (IOException ex) {
                    ErrorHandler.handleFatalError(ex);
                }
            }

        };
    }

    class Banner extends Entity {

        public Banner() {
            super();
            frustumSphereRadius = 2f;
        }

        final Matrix4f modelMatrix = new Matrix4f();
        int xzOrientation = 0;
        float seed = 0;
        boolean againstFencepost;


        @Override
        public void toBytes(XBFilterOutputStream fout) throws IOException {
            fout.write((byte) xzOrientation);
            fout.write((byte) (againstFencepost ? 1 : 0));
        }

        @Override
        public void initializeOnDraw(ArrayList<Byte> bytes) {
            if (bytes != null) {
                xzOrientation = bytes.get(0);
                againstFencepost = (bytes.get(1) == 1);
                seed = (float) (Math.random() * 10);
            }

            xzOrientation = GameScene.player.camera.simplifiedPanTilt.x;
            seed = (float) (Math.random() * 1000);

            int wx = (int) worldPosition.x;
            int wy = (int) worldPosition.y;
            int wz = (int) worldPosition.z;

            if (xzOrientation == 0) {
                againstFencepost = GameScene.world.getBlock(wx, wy, wz - 1)
                        .type == RenderType.FENCE;
            } else if (xzOrientation == 1) {
                againstFencepost = GameScene.world.getBlock(wx + 1, wy, wz)
                        .type == RenderType.FENCE;
            } else if (xzOrientation == 2) {
                againstFencepost = GameScene.world.getBlock(wx, wy, wz + 1)
                        .type == RenderType.FENCE;
            } else {
                againstFencepost = GameScene.world.getBlock(wx - 1, wy, wz)
                        .type == RenderType.FENCE;
            }

            aabb.setOffsetAndSize(0, 0, 0,
                    1, 2, 1);
        }

        int frameCount = 0;
        final float ONE_SIXTEENTH = 0.16666667f;

        @Override
        public void draw(Matrix4f projectionMatrix, Matrix4f viewMatrix) {
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
            mvp.update(projectionMatrix, viewMatrix, modelMatrix);
            mvp.sendToShader(shader.getID(), shader.mvpUniform);
            body.draw(false);
            frameCount++;
        }




    }
}
