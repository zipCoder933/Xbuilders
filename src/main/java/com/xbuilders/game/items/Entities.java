package com.xbuilders.game.items;

import com.xbuilders.engine.MainWindow;
import com.xbuilders.engine.items.entity.EntitySupplier;
import com.xbuilders.game.items.entities.Banner;
import com.xbuilders.game.items.entities.animal.CatLink;
import com.xbuilders.game.items.entities.animal.FoxLink;
import com.xbuilders.game.items.entities.animal.RabbitLink;
import com.xbuilders.game.items.entities.animal.fish.FishALink;
import com.xbuilders.game.items.entities.animal.fish.FishBLink;
import com.xbuilders.game.items.entities.animal.landAndWater.BeaverEntityLink;
import com.xbuilders.game.items.entities.animal.landAndWater.TurtleEntityLink;
import com.xbuilders.game.items.entities.animal.quadPedal.DogLink;
import com.xbuilders.game.items.entities.animal.quadPedal.HorseLink;
import com.xbuilders.game.items.entities.animal.quadPedal.MuleLink;
import com.xbuilders.game.items.entities.vehicle.Boat;
import com.xbuilders.game.items.entities.vehicle.Minecart;

import java.util.ArrayList;
import java.util.Arrays;

public class Entities {

    public static ArrayList<EntitySupplier> startup_getEntities(MainWindow window) {
        EntitySupplier[] entityArray = new EntitySupplier[]{
                //Banners
                new EntitySupplier(82, "Red Banner", () -> new Banner(82)),
                new EntitySupplier(81, "Orange Banner", () -> new Banner(81)),
                new EntitySupplier(72, "Yellow Banner", () -> new Banner(72)),
                new EntitySupplier(75, "Lime Banner", () -> new Banner(75)),
                new EntitySupplier(78, "Green Banner", () -> new Banner(78)),
                new EntitySupplier(73, "Blue Banner", () -> new Banner(73)),
                new EntitySupplier(77, "Gray Banner", () -> new Banner(77)),
                new EntitySupplier(76, "Pink Banner", () -> new Banner(76)),
                new EntitySupplier(84, "Purple Banner", () -> new Banner(84)),
                new EntitySupplier(74, "White Banner", () -> new Banner(74)),
                new EntitySupplier(83, "Xbuilders Banner", () -> new Banner(83)),
                new EntitySupplier(80, "Regal Banner", () -> new Banner(80)),
                new EntitySupplier(79, "Royal Banner", () -> new Banner(79)),


                //Boats
                new EntitySupplier(92, "Oak Boat", () -> new Boat(92, window)),
                new EntitySupplier(95, "Dark Oak Boat", () -> new Boat(95, window)),
                new EntitySupplier(96, "Spruce Boat", () -> new Boat(96, window)),
                new EntitySupplier(94, "Acacia Boat", () -> new Boat(94, window)),
                new EntitySupplier(93, "Jungle Boat", () -> new Boat(93, window)),
                new EntitySupplier(97, "Birch Boat", () -> new Boat(97, window)),

                //Minecarts
                new EntitySupplier(103, "Blue Minecart", () -> new Minecart(103, window)),
                new EntitySupplier(99, "Charcoal Minecart", () -> new Minecart(99, window)),
                new EntitySupplier(104, "Cyan Minecart", () -> new Minecart(104, window)),
                new EntitySupplier(101, "Green Minecart", () -> new Minecart(101, window)),
                new EntitySupplier(98, "Iron Minecart", () -> new Minecart(98, window)),
                new EntitySupplier(100, "Red Minecart", () -> new Minecart(100, window)),
                new EntitySupplier(102, "Yellow Minecart", () -> new Minecart(102, window)),

                //Foxes
                new FoxLink(window, 56, "Fox"),
//                new FoxLink(window, 56, "Red Fox", "red.png"),
//                new FoxLink(window, 1, "Gray Fox", "gray.png"),
//                new FoxLink(window, 2, "White Fox", "white.png"),
                //Cats
                new CatLink(window, 3, "Cat"),
//                new CatLink(window, 3, "Black Cat", "black.png"),
//                new CatLink(window, 4, "British Shorthair Cat", "british_shorthair.png"),
//                new CatLink(window, 5, "Calico Cat", "calico.png"),
//                new CatLink(window, 6, "Calico Cat", "calico2.png"),
//                new CatLink(window, 7, "Jellie Cat", "jellie.png"),
//                new CatLink(window, 8, "Ocelot", "ocelot.png"),
//                new CatLink(window, 9, "Persian Cat", "persian.png"),
//                new CatLink(window, 10, "Ragdoll Cat", "ragdoll.png"),
//                new CatLink(window, 11, "Red Cat", "red.png"),
//                new CatLink(window, 12, "Siamese Cat", "siamese.png"),
//                new CatLink(window, 13, "Tabby Cat", "tabby.png"),
//                new CatLink(window, 14, "White Cat", "white.png"),
                //Rabbits
                new RabbitLink(window, 15, "Rabbit"),
//                new RabbitLink(window, 15, "Black Rabbit", "black.png"),
//                new RabbitLink(window, 16, "White Rabbit", "white.png"),
//                new RabbitLink(window, 17, "Brown Rabbit", "brown.png"),
//                new RabbitLink(window, 18, "Caerbannog Rabbit", "caerbannog.png"),
//                new RabbitLink(window, 19, "Gold Rabbit", "gold.png"),
//                new RabbitLink(window, 20, "Salt Rabbit", "salt.png"),
//                new RabbitLink(window, 21, "Toast Rabbit", "toast.png"),
//                new RabbitLink(window, 22, "White Splotched Rabbit", "white_splotched.png"),

                //Horses
                new HorseLink(window, 23, "Horse"),
//                new HorseLink(window, 23, "Black Horse", "black.png"),
//                new HorseLink(window, 24, "Brown Horse", "brown.png"),
//                new HorseLink(window, 25, "Chestnut Horse", "chestnut.png"),
//                new HorseLink(window, 26, "Creamy Horse", "creamy.png"),
//                new HorseLink(window, 27, "Dark Brown Horse", "darkbrown.png"),
//                new HorseLink(window, 28, "White Horse", "white.png"),
//                new HorseLink(window, 29, "Gray Horse", "gray.png"),
                //Mules
                new MuleLink(window, 30, "Mule"), //TODO: Separate this so that it is 2 items, (Do this after instantiation of items)
//                new MuleLink(window, 30, "Mule", "mule.png"),
//                new MuleLink(window, 31, "Donkey", "donkey.png"),

                //Dogs
                new DogLink(window, 32, "Dog"),
//                new DogLink(window, 32, "Black Dog", "black.png"),
//                new DogLink(window, 33, "Brown Dog", "brown.png"),
//                new DogLink(window, 34, "Gold Dog", "gold.png"),
//                new DogLink(window, 35, "White Dog", "white.png"),

                //Turtles
                new TurtleEntityLink(window, 105, "Sea Turtle"),
//                new TurtleEntityLink(window, 105, "Green Sea Turtle", "big_sea_turtle.png"),
//                new TurtleEntityLink(window, 106, "Yellow Sea Turtle", "yellow_turtle.png"),

                //Beavers
                new BeaverEntityLink(window, 200, "Beaver"),

                //Fish
                new FishALink(window, 41, "Butterfly Fish"),
                new FishBLink(window, 42, "Angler Fish"),

//                new FishBLink(window, 50, "Angel Fish", "angel.png"),
//                new FishBLink(window, 51, "Blue Ring Angelfish", "blue_ring_angel.png"),
//                new FishBLink(window, 52, "Copperband Butterfly", "copperband_butterfly.png"),
//                new FishBLink(window, 53, "Gold Butterfly Fish", "gold.png"),
//                new FishBLink(window, 54, "Ornate Butterfly Fish", "ornate_butterfly.png"),
//                new FishBLink(window, 36, "Red Butterfly Fish", "red_butterfly.png"),
//                new FishBLink(window, 37, "Regal Tang Fish", "regal_tang.png"),
//                new FishBLink(window, 38, "Striped Butterfly Fish", "striped_butterfly.png"),
//                new FishBLink(window, 49, "Yellow Angelfish", "yellow_angel.png"),
//                new FishBLink(window, 48, "Gray Glitterfish", "gray_glitter.png"),
//                new FishALink(window, 41, "Bicolor Angelfish", "bicolor_angel.png"),
//                new FishALink(window, 40, "Clown Fish", "clown.png"),
//                new FishALink(window, 43, "Clown Loach", "clown_loach.png"),
//                new FishALink(window, 44, "Cotton Candy Betta", "cotton_candy_betta.png"),
//                new FishALink(window, 45, "Damsel Fish", "damsel.png"),
//                new FishALink(window, 46, "Emporer Angelfish", "emporer_angel.png"),
//                new FishALink(window, 39, "Orange Green Betta", "orange_green_betta.png"),
//                new FishALink(window, 47, "Royal Gramma Fish", "royal_gramma.png"),
//                new FishALink(window, 42, "Salamander Betta", "salamander_betta.png"),
//                new FishALink(window, 55, "Tri-Band Betta", "tri_band_betta.png"),
        };


        ArrayList<EntitySupplier> entityList = new ArrayList<>();
        entityList.addAll(Arrays.asList(entityArray));
        return entityList;
    }
}
