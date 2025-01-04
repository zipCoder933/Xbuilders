package com.xbuilders.content.vanilla.items;

import com.xbuilders.engine.MainWindow;
import com.xbuilders.engine.server.model.items.entity.EntitySupplier;
import com.xbuilders.content.vanilla.items.entities.Banner;
import com.xbuilders.content.vanilla.items.entities.animal.Cat;
import com.xbuilders.content.vanilla.items.entities.animal.Fox;
import com.xbuilders.content.vanilla.items.entities.animal.Rabbit;
import com.xbuilders.content.vanilla.items.entities.animal.fish.FishA;
import com.xbuilders.content.vanilla.items.entities.animal.fish.FishB;
import com.xbuilders.content.vanilla.items.entities.animal.landAndWater.Beaver;
import com.xbuilders.content.vanilla.items.entities.animal.landAndWater.Turtle;
import com.xbuilders.content.vanilla.items.entities.animal.quadPedal.Dog;
import com.xbuilders.content.vanilla.items.entities.animal.quadPedal.Horse;
import com.xbuilders.content.vanilla.items.entities.animal.quadPedal.Mule;
import com.xbuilders.content.vanilla.items.entities.vehicle.Boat;
import com.xbuilders.content.vanilla.items.entities.vehicle.Minecart;

import java.util.ArrayList;
import java.util.Arrays;

public class Entities {

    public static ArrayList<EntitySupplier> startup_getEntities(MainWindow window) {

        EntitySupplier[] entityArray = new EntitySupplier[]{
                //Banners
                new EntitySupplier(82, (uniqueIdentifier) -> new Banner(82, uniqueIdentifier)),
                new EntitySupplier(81, (uniqueIdentifier) -> new Banner(81, uniqueIdentifier)),
                new EntitySupplier(72, (uniqueIdentifier) -> new Banner(72, uniqueIdentifier)),
                new EntitySupplier(75, (uniqueIdentifier) -> new Banner(75, uniqueIdentifier)),
                new EntitySupplier(78, (uniqueIdentifier) -> new Banner(78, uniqueIdentifier)),
                new EntitySupplier(73, (uniqueIdentifier) -> new Banner(73, uniqueIdentifier)),
                new EntitySupplier(77, (uniqueIdentifier) -> new Banner(77, uniqueIdentifier)),
                new EntitySupplier(76, (uniqueIdentifier) -> new Banner(76, uniqueIdentifier)),
                new EntitySupplier(84, (uniqueIdentifier) -> new Banner(84, uniqueIdentifier)),
                new EntitySupplier(74, (uniqueIdentifier) -> new Banner(74, uniqueIdentifier)),
                new EntitySupplier(83, (uniqueIdentifier) -> new Banner(83, uniqueIdentifier)),
                new EntitySupplier(80, (uniqueIdentifier) -> new Banner(80, uniqueIdentifier)),
                new EntitySupplier(79, (uniqueIdentifier) -> new Banner(79, uniqueIdentifier)),


                //Boats
                new EntitySupplier(92, (uniqueIdentifier) -> new Boat(92, window, uniqueIdentifier, "boat_oak")),
                new EntitySupplier(95, (uniqueIdentifier) -> new Boat(95, window, uniqueIdentifier, "boat_darkoak")),
                new EntitySupplier(96, (uniqueIdentifier) -> new Boat(96, window, uniqueIdentifier, "boat_spruce")),
                new EntitySupplier(94, (uniqueIdentifier) -> new Boat(94, window, uniqueIdentifier, "boat_acacia")),
                new EntitySupplier(93, (uniqueIdentifier) -> new Boat(93, window, uniqueIdentifier, "boat_jungle")),
                new EntitySupplier(97, (uniqueIdentifier) -> new Boat(97, window, uniqueIdentifier, "boat_birch")),

                //Minecarts
                new EntitySupplier(103, (uniqueIdentifier) -> new Minecart(103, window, uniqueIdentifier, "blue")),
                new EntitySupplier(99, (uniqueIdentifier) -> new Minecart(99, window, uniqueIdentifier, "charcoal")),
                new EntitySupplier(104, (uniqueIdentifier) -> new Minecart(104, window, uniqueIdentifier, "cyan")),
                new EntitySupplier(101, (uniqueIdentifier) -> new Minecart(101, window, uniqueIdentifier, "green")),
                new EntitySupplier(98, (uniqueIdentifier) -> new Minecart(98, window, uniqueIdentifier, "iron")),
                new EntitySupplier(100, (uniqueIdentifier) -> new Minecart(100, window, uniqueIdentifier, "red")),
                new EntitySupplier(102, (uniqueIdentifier) -> new Minecart(102, window, uniqueIdentifier, "yellow")),

                //Foxes
                new EntitySupplier(56, (uniqueIdentifier) -> new Fox(56, uniqueIdentifier, window)),
                //Cats
                new EntitySupplier(3, (uniqueIdentifier) -> new Cat(3, uniqueIdentifier, window)),
                //Rabbits
                new EntitySupplier(15, (uniqueIdentifier) -> new Rabbit(15, window, uniqueIdentifier)),

                //Horses
                new EntitySupplier(23, (uniqueIdentifier) -> new Horse(23, uniqueIdentifier, window)),
                //Mules
                new EntitySupplier(30, (uniqueIdentifier) -> new Mule(30, uniqueIdentifier, window)),

                //Dogs
                new EntitySupplier(32, (uniqueIdentifier) -> new Dog(32, uniqueIdentifier, window)),

                //Turtles
                new EntitySupplier(105, (uniqueIdentifier) -> new Turtle(105, uniqueIdentifier, window)),

                //Beavers
                new EntitySupplier(200, (uniqueIdentifier) -> new Beaver(200, uniqueIdentifier, window)),

                //Fish
                new EntitySupplier(41, (uniqueIdentifier) -> new FishA(41, uniqueIdentifier, window)),
                new EntitySupplier(42, (uniqueIdentifier) -> new FishB(42, uniqueIdentifier, window))
        };


        ArrayList<EntitySupplier> entityList = new ArrayList<>();
        entityList.addAll(Arrays.asList(entityArray));
        return entityList;
    }
}
