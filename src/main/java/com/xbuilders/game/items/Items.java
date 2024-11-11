package com.xbuilders.game.items;

import com.xbuilders.engine.items.ItemUtils;
import com.xbuilders.engine.items.block.Block;
import com.xbuilders.engine.items.entity.EntitySupplier;
import com.xbuilders.engine.items.item.Item;
import com.xbuilders.engine.utils.IntMap;
import com.xbuilders.engine.utils.ResourceUtils;
import com.xbuilders.game.items.tools.*;

import java.util.ArrayList;

public class Items {

    public static final Item TOOL_ANIMAL_FEED = new AnimalFeed();

    public static ArrayList<Item> startup_getItems() {
        ArrayList<Item> itemList = new ArrayList<>();
        itemList.add(new Saddle());
        itemList.add(new Hoe());
        itemList.add(new Flashlight());
        itemList.add(new Camera());
        itemList.add(TOOL_ANIMAL_FEED);

        ItemUtils.getAllJsonItems(ResourceUtils.resource("items\\items\\json")).forEach(itemList::add);

        return itemList;
    }
}
