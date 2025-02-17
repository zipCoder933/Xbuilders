package com.xbuilders.content.vanilla.items;

import com.xbuilders.engine.client.ClientWindow;
import com.xbuilders.engine.server.Server;
import com.xbuilders.engine.server.builtinMechanics.liquid.LiquidPropagationTask;
import com.xbuilders.engine.server.items.ItemUtils;
import com.xbuilders.engine.server.items.Registrys;
import com.xbuilders.engine.server.items.block.Block;
import com.xbuilders.engine.server.item.Item;
import com.xbuilders.engine.server.item.ItemStack;
import com.xbuilders.engine.client.player.raycasting.CursorRay;
import com.xbuilders.engine.utils.ResourceUtils;
import com.xbuilders.content.vanilla.items.items.*;

import java.util.ArrayList;
import java.util.Objects;
import java.util.function.Predicate;

public class Items {

    public static final Item TOOL_ANIMAL_FEED = new AnimalFeed();

    public static Item getBlockWithSharedTexture(
            Predicate<Block> customPredicate, Item original, int... validVariantTypes) {
        Block originalBlock = original.getBlock();
        if (originalBlock == null) return null;
        //Get the block variant
        for (Item i : Registrys.items.getList()) {
            if (i.getBlock() != null
                    && i.getBlock().texture.equals(originalBlock.texture)) {
                for (int rt : validVariantTypes) {
                    if (i.getBlock().renderType == rt && customPredicate.test(i.getBlock())) {
                        return i;
                    }
                }
            }
        }
        return null;
    }

    public static Item getBlockWithSharedName(
            Predicate<Block> customPredicate,
            Item originalItem, String[] invalidIdMatches, int... validBlockTypes) {
        //Get the block variant
        for (Item i : Registrys.items.getList()) {

            if (i.getBlock() == null) continue;
            String thisId = originalItem.id;
            String blockId = i.getBlock().alias;

            String commonWorld = getCommonWord(thisId, blockId, "_");
            if (commonWorld.isEmpty()) continue;
            boolean foundInvalidMatch = false;
            for (String invalidMatch : invalidIdMatches) {
                if (commonWorld.equalsIgnoreCase(invalidMatch)) {
                    foundInvalidMatch = true;
                    break;
                }
            }
            if (foundInvalidMatch) continue;

//            System.out.println("this: " + thisId + " \t other: " + blockId + " \t common: " + commonWorld);
            for (int rt : validBlockTypes) {
                if (i.getBlock().renderType == rt && customPredicate.test(i.getBlock())) {
                    return i;
                }
            }

        }
        return null;
    }

    public static String getCommonWord(String s1, String s2, String delimiter) {
        String[] words1 = s1.split(delimiter);
        String[] words2 = s2.split(delimiter);
        for (String word1 : words1) {
            for (String word2 : words2) {
                if (word1.equals(word2)) {
                    return word1;
                }
            }
        }
        return "";
    }

    public static ArrayList<Item> startup_getItems() {
        ArrayList<Item> itemList = new ArrayList<>();
        itemList.add(new Shovel("wooden", 25));
        itemList.add(new Pickaxe("wooden", 25, 1.5f));
        itemList.add(new Axe("wooden", 25));

        itemList.add(new Shovel("stone", 100));
        itemList.add(new Pickaxe("stone", 100, 2));
        itemList.add(new Axe("stone", 100));

        itemList.add(new Shovel("iron", 200));
        itemList.add(new Pickaxe("iron", 200, 3));
        itemList.add(new Axe("iron", 200));

        itemList.add(new Shovel("golden", 400));
        itemList.add(new Pickaxe("golden", 400, 7));
        itemList.add(new Axe("golden", 400));

        itemList.add(new Shovel("diamond", 4000));
        itemList.add(new Pickaxe("diamond", 4000, 6));
        itemList.add(new Axe("diamond", 4000));
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


    public static void editItems(ClientWindow window) {
        Item item = Registrys.getItem("xbuilders:bread");
        if (item != null) {
            item.hungerSaturation = 1;
        }
        item = Registrys.getItem("xbuilders:apple");
        if (item != null) {
            item.hungerSaturation = 0.5f;
        }
        item = Registrys.getItem("xbuilders:bucket");
        if (item != null) {
            item.createClickEvent = (ray, stack) -> {
                fillBucket(ray, stack);
                return true;
            };
            item.destroyClickEvent = (ray, stack) -> {
                fillBucket(ray, stack);
                return true;
            };
        }
        item = Registrys.getItem("xbuilders:water_bucket");
        if (item != null) {
            item.createClickEvent = (ray, stack) -> {
                emptyBucket(ray, stack);
                return true;
            };
            item.destroyClickEvent = (ray, stack) -> {
                emptyBucket(ray, stack);
                return true;
            };
        }
        item = Registrys.getItem("xbuilders:lava_bucket");
        if (item != null) {
            item.createClickEvent = (ray, stack) -> {
                emptyBucket(ray, stack);
                return true;
            };
            item.destroyClickEvent = (ray, stack) -> {
                emptyBucket(ray, stack);
                return true;
            };
        }
        item = Registrys.getItem("xbuilders:animal_apple");
        if (item != null) {
            AnimalFood.makeAnimalFood(item, null);
            item.hungerSaturation = 0.2f;
        }
    }


    private static void fillBucket(CursorRay ray, ItemStack stack) {
        int x = ray.getHitPos().x;
        int y = ray.getHitPos().y;
        int z = ray.getHitPos().z;

        Block hitPos = Server.world.getBlock(x, y, z);
        System.out.println("Hit: " + hitPos);
        if (hitPos.isLiquid()) {
            int flow = LiquidPropagationTask.getFlow(Server.world.getBlockData(x, y, z), 0);
            if (flow >= hitPos.liquidMaxFlow + 1) {
                Server.setBlock(Blocks.BLOCK_AIR, null, x, y, z);

                if (hitPos.id == Blocks.BLOCK_WATER) {
                    stack.item = Objects.requireNonNull(Registrys.getItem("xbuilders:water_bucket"));
                } else if (hitPos.id == Blocks.BLOCK_LAVA) {
                    stack.item = Objects.requireNonNull(Registrys.getItem("xbuilders:lava_bucket"));
                }
            }
        }
    }

    private static void emptyBucket(CursorRay ray, ItemStack stack) {
        int x = ray.getHitPos().x;
        int y = ray.getHitPos().y;
        int z = ray.getHitPos().z;
        if (!Server.world.getBlock(x, y, z).getRenderType().replaceOnSet) {
            x = ray.getHitPosPlusNormal().x;
            y = ray.getHitPosPlusNormal().y;
            z = ray.getHitPosPlusNormal().z;
        }

        Server.setBlock(stack.item.getBlock().id, x, y, z);
        stack.item = Objects.requireNonNull(Registrys.getItem("xbuilders:bucket"));
    }

}
