package com.xbuilders.game.vanilla.items;

import com.xbuilders.engine.MainWindow;
import com.xbuilders.engine.items.ItemUtils;
import com.xbuilders.engine.items.Registrys;
import com.xbuilders.engine.items.block.Block;
import com.xbuilders.engine.items.item.Item;
import com.xbuilders.engine.utils.ResourceUtils;
import com.xbuilders.game.vanilla.items.items.*;

import java.util.ArrayList;

public class Items {

    public static final Item TOOL_ANIMAL_FEED = new AnimalFeed();

    public static Item getBlockVariant(Item original, int... validVariantTypes) {
        Block originalBlock = original.getBlock();
        if (originalBlock == null) return null;
        //Get the block variant
        for (Item i : Registrys.items.getList()) {
            if (i.getBlock() != null
                    && i.getBlock().texture.equals(originalBlock.texture)) {
                for (int rt : validVariantTypes) {
                    if (i.getBlock().renderType == rt) {
                        return i;
                    }
                }
            }
        }
        return null;
    }

    public static ArrayList<Item> startup_getItems() {
        ArrayList<Item> itemList = new ArrayList<>();
        itemList.add(new Shovel("wooden", 100));
        itemList.add(new Pickaxe("wooden", 100, 1.5f));
        itemList.add(new Axe("wooden", 100));

        itemList.add(new Shovel("stone", 200));
        itemList.add(new Pickaxe("stone", 200, 2));
        itemList.add(new Axe("stone", 200));

        itemList.add(new Shovel("iron", 300));
        itemList.add(new Pickaxe("iron", 300, 3));
        itemList.add(new Axe("iron", 300));

        itemList.add(new Shovel("golden", 400));
        itemList.add(new Pickaxe("golden", 400, 4));
        itemList.add(new Axe("golden", 400));

        itemList.add(new Shovel("diamond", 500));
        itemList.add(new Pickaxe("diamond", 500, 5));
        itemList.add(new Axe("diamond", 500));
//        itemList.add(new Sword("wooden"));
//        itemList.add(new Sword("stone"));
//        itemList.add(new Sword("iron"));
//        itemList.add(new Sword("golden"));
//        itemList.add(new Sword("diamond"));
//        itemList.add(new Sword("netherite"));
//        itemList.add(new Sword("enderite"));
//        itemList.add(new Sword("obsidian"));

        itemList.add(new Saddle());
        itemList.add(new Hoe());
        itemList.add(new Flashlight());
        itemList.add(new Camera());
        itemList.add(TOOL_ANIMAL_FEED);

        ItemUtils.getAllJsonItems(ResourceUtils.resource("items\\items\\json")).forEach(itemList::add);

        return itemList;
    }


    public static void editItems(MainWindow window) {
        Item item = Registrys.getItem("xbuilders:bread");
        if (item != null) {
            item.hungerSaturation = 1;
        }
        item = Registrys.getItem("xbuilders:apple");
        if (item != null) {
            item.hungerSaturation = 0.5f;
        }
    }
}
