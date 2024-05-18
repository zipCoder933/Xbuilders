package com.xbuilders.game.items.entities.animal;

import com.xbuilders.engine.items.Entity;
import com.xbuilders.window.BaseWindow;

import java.util.ArrayList;

public class DogLink extends HorseLink {

    public DogLink(BaseWindow window, int id, String name, String textureName) {
        super(window, id, name, textureName);
        bodyModel = "items\\entity\\animal\\dog\\large\\body.obj";
        sittingModel = "items\\entity\\animal\\dog\\large\\sitting.obj";
        legModel = "items\\entity\\animal\\dog\\large\\leg.obj";
        texturePre = "items\\entity\\animal\\dog\\";
        setIcon("egg.png");
    }

    @Override
    public void initializeEntity(Entity e, ArrayList<Byte> loadBytes) {
        super.initializeEntity(e, loadBytes);
        HorseMule a = (HorseMule) e; //Cast the entity to a fox
//        a.freezeMode = true;
        a.setActivity(0.7f);
        a.animalInit(this, loadBytes); //Initialize the fox by passing the link so that the entity has access to the link variables
        //Z is the direciton of the animal
        a.legXSpacing = 0.30f * a.SCALE;
        a.legZSpacing = 0.82f * a.SCALE;
        a.legYSpacing = -1.15f * a.SCALE; //negative=higher, positive=lower
    }
}