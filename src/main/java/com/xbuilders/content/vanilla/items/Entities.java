package com.xbuilders.content.vanilla.items;

import com.xbuilders.engine.client.ClientWindow;
import com.xbuilders.engine.server.entity.EntitySupplier;
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

    public static ArrayList<EntitySupplier> startup_getEntities(ClientWindow window) {

        EntitySupplier[] entityArray = new EntitySupplier[]{
                //Banners
                new EntitySupplier("xbuilders:red_banner", (uniqueIdentifier) -> new Banner(uniqueIdentifier, "red")),
                new EntitySupplier("xbuilders:orange_banner", (uniqueIdentifier) -> new Banner(uniqueIdentifier, "orange")),
                new EntitySupplier("xbuilders:yellow_banner", (uniqueIdentifier) -> new Banner(uniqueIdentifier, "yellow")),
                new EntitySupplier("xbuilders:lime_banner", (uniqueIdentifier) -> new Banner(uniqueIdentifier, "lime")),
                new EntitySupplier("xbuilders:green_banner", (uniqueIdentifier) -> new Banner(uniqueIdentifier, "green")),
                new EntitySupplier("xbuilders:blue_banner", (uniqueIdentifier) -> new Banner(uniqueIdentifier, "blue")),
                new EntitySupplier("xbuilders:gray_banner", (uniqueIdentifier) -> new Banner(uniqueIdentifier, "gray")),
                new EntitySupplier("xbuilders:pink_banner", (uniqueIdentifier) -> new Banner(uniqueIdentifier, "pink")),
                new EntitySupplier("xbuilders:purple_banner", (uniqueIdentifier) -> new Banner(uniqueIdentifier, "purple")),
                new EntitySupplier("xbuilders:white_banner", (uniqueIdentifier) -> new Banner(uniqueIdentifier, "white")),
                new EntitySupplier("xbuilders:xbuilders_banner", (uniqueIdentifier) -> new Banner(uniqueIdentifier, "blue_logo")),
                new EntitySupplier("xbuilders:regal_banner", (uniqueIdentifier) -> new Banner(uniqueIdentifier, "regal")),
                new EntitySupplier("xbuilders:royal_banner", (uniqueIdentifier) -> new Banner(uniqueIdentifier, "royal")),


                //Boats
                new EntitySupplier("xbuilders:oak_boat", (uniqueIdentifier) -> new Boat(window, uniqueIdentifier, "boat_oak")),
                new EntitySupplier("xbuilders:dark_oak_boat", (uniqueIdentifier) -> new Boat(window, uniqueIdentifier, "boat_darkoak")),
                new EntitySupplier("xbuilders:spruce_boat", (uniqueIdentifier) -> new Boat(window, uniqueIdentifier, "boat_spruce")),
                new EntitySupplier("xbuilders:acacia_boat", (uniqueIdentifier) -> new Boat(window, uniqueIdentifier, "boat_acacia")),
                new EntitySupplier("xbuilders:jungle_boat", (uniqueIdentifier) -> new Boat(window, uniqueIdentifier, "boat_jungle")),
                new EntitySupplier("xbuilders:birch_boat", (uniqueIdentifier) -> new Boat(window, uniqueIdentifier, "boat_birch")),

                //Minecarts
                new EntitySupplier("xbuilders:blue_minecart", (uniqueIdentifier) -> new Minecart(window, uniqueIdentifier, "blue")),
                new EntitySupplier("xbuilders:charcoal_minecart", (uniqueIdentifier) -> new Minecart(window, uniqueIdentifier, "charcoal")),
                new EntitySupplier("xbuilders:cyan_minecart", (uniqueIdentifier) -> new Minecart(window, uniqueIdentifier, "cyan")),
                new EntitySupplier("xbuilders:green_minecart", (uniqueIdentifier) -> new Minecart(window, uniqueIdentifier, "green")),
                new EntitySupplier("xbuilders:iron_minecart", (uniqueIdentifier) -> new Minecart(window, uniqueIdentifier, "iron")),
                new EntitySupplier("xbuilders:red_minecart", (uniqueIdentifier) -> new Minecart(window, uniqueIdentifier, "red")),
                new EntitySupplier("xbuilders:yellow_minecart", (uniqueIdentifier) -> new Minecart(window, uniqueIdentifier, "yellow")),

                //Foxes
                new EntitySupplier("xbuilders:fox", (uniqueIdentifier) -> new Fox(uniqueIdentifier, window)),
                //Cats
                new EntitySupplier("xbuilders:cat", (uniqueIdentifier) -> new Cat(uniqueIdentifier, window)),
                //Rabbits
                new EntitySupplier("xbuilders:rabbit", (uniqueIdentifier) -> new Rabbit(window, uniqueIdentifier)),

                //Horses
                new EntitySupplier("xbuilders:horse", (uniqueIdentifier) -> new Horse(uniqueIdentifier, window)),
                //Mules
                new EntitySupplier("xbuilders:mule", (uniqueIdentifier) -> new Mule(uniqueIdentifier, window)),

                //Dogs
                new EntitySupplier("xbuilders:dog", (uniqueIdentifier) -> new Dog(uniqueIdentifier, window)),

                //Turtles
                new EntitySupplier("xbuilders:turtle", (uniqueIdentifier) -> new Turtle(uniqueIdentifier, window)),

                //Beavers
                new EntitySupplier("xbuilders:beaver", (uniqueIdentifier) -> new Beaver(uniqueIdentifier, window)),

                //Fish
                new EntitySupplier("xbuilders:angler_fish", (uniqueIdentifier) -> new FishA(uniqueIdentifier, window)),
                new EntitySupplier("xbuilders:butterfly_fish", (uniqueIdentifier) -> new FishB(uniqueIdentifier, window))
        };


        ArrayList<EntitySupplier> entityList = new ArrayList<>();
        entityList.addAll(Arrays.asList(entityArray));
        return entityList;
    }
}
