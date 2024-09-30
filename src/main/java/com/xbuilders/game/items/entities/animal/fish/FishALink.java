/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.xbuilders.game.items.entities.animal.fish;

import com.xbuilders.engine.MainWindow;
import com.xbuilders.engine.items.entity.Entity;
import com.xbuilders.engine.items.entity.EntityLink;
import com.xbuilders.engine.rendering.entity.EntityMesh;
import com.xbuilders.engine.utils.ErrorHandler;
import com.xbuilders.engine.utils.ResourceUtils;
import com.xbuilders.window.utils.texture.TextureUtils;

import java.io.IOException;
import java.util.Objects;


/**
 * @author zipCoder933
 */
public class FishALink extends EntityLink {

    public FishALink(MainWindow window, int id, String name, String textureFile) {
        super(id, name);
        supplier = () -> new FishObject(window);
        setIcon("fish egg.png");
        tags.add("animal");
        tags.add("fish");
        this.textureFile = textureFile;
    }

    EntityMesh body;

    @Override
    public void initializeEntity(Entity e, byte[] loadBytes) {
        if (body == null) {
            body = new EntityMesh();
            try {
                int texture = Objects.requireNonNull(TextureUtils.loadTexture(
                        ResourceUtils.resource("items\\entity\\animal\\fish\\textures\\fish_A\\" + textureFile).getAbsolutePath(),
                        false)).id;
                body.loadFromOBJ(ResourceUtils.resource("items\\entity\\animal\\fish\\fish_A.obj"));
                body.setTextureID(texture);
            } catch (IOException ex) {
                ErrorHandler.report(ex);
            }
        }
        super.initializeEntity(e, loadBytes); //we MUST ensure this is called
    }

    public String textureFile;


    public class FishObject extends FishAnimal {

        public FishObject(MainWindow window) {
            super(window);
        }

        @Override
        public final void renderFish() {
            body.draw(false);
        }
    }
}