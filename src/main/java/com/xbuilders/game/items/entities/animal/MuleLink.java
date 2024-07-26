package com.xbuilders.game.items.entities.animal;

import com.xbuilders.engine.items.entity.Entity;
import com.xbuilders.window.BaseWindow;

import java.util.ArrayList;

public class MuleLink extends QuadPedalLandAnimalLink {

    public MuleLink(BaseWindow window, int id, String name, String textureName) {
        super(window, id, name, textureName);
        bodyPath = "items\\entity\\animal\\horse\\mule\\body.obj";
        legPath = "items\\entity\\animal\\horse\\mule\\leg.obj";
        texturePrePath = "items\\entity\\animal\\horse\\";
        setIcon("mule egg.png");
    }

    @Override
    public void initializeEntity(Entity e, byte[] loadBytes) {
        super.initializeEntity(e, loadBytes);
        QuadPedalEntity a = (QuadPedalEntity) e; //Cast the entity to a fox
        a.animalInit(this, loadBytes); //Initialize the fox by passing the link so that the entity has access to the link variables
        a.legXSpacing = 0.35f * a.SCALE;
        a.legZSpacing = 0.8f * a.SCALE;
        a.legYSpacing = -1f * a.SCALE;
    }
}