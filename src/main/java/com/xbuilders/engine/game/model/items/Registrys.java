/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.xbuilders.engine.game.model.items;

import com.xbuilders.engine.game.model.items.block.Block;
import com.xbuilders.engine.game.model.items.block.BlockRegistry;
import com.xbuilders.engine.game.model.items.entity.Entity;
import com.xbuilders.engine.game.model.items.entity.EntitySupplier;
import com.xbuilders.engine.game.model.items.entity.EntityRegistry;
import com.xbuilders.engine.game.model.items.item.Item;
import com.xbuilders.engine.game.model.items.item.ItemRegistry;
import com.xbuilders.engine.utils.ResourceUtils;
import com.xbuilders.window.utils.texture.TextureUtils;

import java.io.IOException;
import java.util.List;

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
public class Registrys {
    private static int defaultIcon;
    public static final BlockRegistry blocks = new BlockRegistry();
    public static final EntityRegistry entities = new EntityRegistry();
    public static final ItemRegistry items = new ItemRegistry();

    public static void initialize(
            List<Block> blockList, List<EntitySupplier> entityList, List<Item> toolList) throws IOException {

        Registrys.defaultIcon = TextureUtils.loadTexture(ResourceUtils.DEFAULT_ICON.getAbsolutePath(), false).id;

        blocks.setup(blockList);
        entities.setup(entityList);
        items.setup(
                Registrys.defaultIcon,
                blocks.textures,

                blocks.getIdToBlockMap(),
                entities.getIdMap(),
                blocks.aliasToIDMap,
                entities.aliasToIDMap,
                toolList);
    }

    public static Block getBlock(short blockID) {
        return blocks.getBlock(blockID);
    }

    public static EntitySupplier getEntity(short blockID) {
        return entities.getItem(blockID);
    }

    public static Item getItem(String blockID) {
        return items.getItem(blockID);
    }

    public static Item getItem(Block block) {
        for (Item item : items.getList()) {
            if (item.getBlock() != null && item.getBlock().id == block.id) return item;
        }
        return null;
    }

    public static Item getItem(Entity entity) {
        for (Item item : items.getList()) {
            if (item.getEntity() != null && item.getEntity().id == entity.id) return item;
        }
        return null;
    }


    public static String formatAlias(String alias) {
        return alias.toLowerCase().trim().replaceAll("\\s+", "-");
    }

}
