/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.xbuilders.engine.items;

import com.xbuilders.engine.items.block.Block;
import com.xbuilders.engine.items.entity.EntityLink;
import com.xbuilders.engine.player.raycasting.Ray;
import com.xbuilders.engine.utils.ByteUtils;
import com.xbuilders.engine.utils.ResourceUtils;
import com.xbuilders.engine.world.chunk.BlockData;
import com.xbuilders.window.utils.texture.TextureUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * I have chosen to make ItemList fully static. There will only ever be one, and
 * I don't need to use multiple instances of BlockList to test code.
 * <p>
 * There are several subgroups that are mostly non-static, however I will still
 * try to make these as static as possible and I will also be instantiating the
 * methods of these groups into this class as static methods.
 * <p>
 * Number of Instances: If you only need one instance of the BlockList
 * throughout your program and it doesn't hold any state that needs to vary
 * between instances, making it static could be appropriate.
 * <p>
 * Encapsulation: If the BlockList class encapsulates data that needs to be
 * accessed globally, making it static might provide a convenient way to access
 * this information without creating instances.
 * <p>
 * <p>
 * <p>
 * <p>
 * Block list as a static class + have all internal elements static causes the
 * list to be available and makes it so that only one instance is possible - No
 * way to instantiate static methods if using interface or class
 * <p>
 * Block list as non-static but statically owned - if testing from another
 * platform, all classes used the statically owned block list, so creating
 * another one would be useless and problematic - It may be initialized on
 * another platform thus increasing complexity
 *
 * @author zipCoder933
 */
public class ItemList {

    /**
     * @return the itemList
     */
    public static Item[] getAllItems() {
        return allItems;
    }

    private static int defaultIcon;
    public static final BlockList blocks = new BlockList();
    public static final EntityList entities = new EntityList();
    public static final ToolList tools = new ToolList();
    private static Item[] allItems;

    public static void initialize() throws IOException {
        File blockTextures = ResourceUtils.BLOCK_TEXTURE_DIR;
        File blockIconDirectory = ResourceUtils.BLOCK_ICON_DIR;
        File iconDirectory = ResourceUtils.ICONS_DIR;

        ItemList.defaultIcon = TextureUtils.loadTexture(ResourceUtils.DEFAULT_ICON.getAbsolutePath(), false).id;
        blocks.init(blockTextures, blockIconDirectory, iconDirectory, ItemList.defaultIcon);
        entities.init(iconDirectory, ItemList.defaultIcon);
        tools.init(iconDirectory, ItemList.defaultIcon);
    }

    public static void setAllItems(Block[] blockList, EntityLink[] entityList, Item[] toolList) {
        blocks.setItems(blockList);
        entities.setItems(entityList);
        tools.setItems(toolList);

        allItems = concatArrays(
                entities.getList(), tools.getList(), blocks.getList()
        );
    }

    public static BlockData getInitialBlockData(Block block, Ray ray) {
        return null;
    }

    public static Item getItem(short id, ItemType itemType) {
        if (null == itemType) {
            return null;
        } else {
            return switch (itemType) {
                case BLOCK -> ItemList.getBlock(id);
                case ENTITY_LINK -> ItemList.getEntity(id);
                case TOOL -> ItemList.getTool(id);
                default -> null;
            };
        }
    }

    public static Block getBlock(short blockID) {
        return blocks.getItem(blockID);
    }

    public static EntityLink getEntity(short blockID) {
        return entities.getItem(blockID);
    }

    public static Item getTool(short blockID) {
        return tools.getItem(blockID);
    }

    private static Item[] concatArrays(Item[]... arrays) {
        int totalLength = 0;
        for (Item[] array : arrays) {
            totalLength += array.length;
        }
        Item[] resultArray = new Item[totalLength];
        int currentIndex = 0;

        for (Item[] array : arrays) {
            for (Item value : array) {
                resultArray[currentIndex] = value;
                currentIndex++;
            }
        }
        return resultArray;
    }


}
