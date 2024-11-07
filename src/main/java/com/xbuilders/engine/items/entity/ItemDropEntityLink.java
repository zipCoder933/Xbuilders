package com.xbuilders.engine.items.entity;

import com.xbuilders.engine.MainWindow;
import com.xbuilders.engine.gameScene.GameScene;
import com.xbuilders.engine.items.block.BlockRegistry;
import com.xbuilders.engine.items.item.Item;
import com.xbuilders.engine.items.Registrys;
import com.xbuilders.engine.items.item.ItemType;
import com.xbuilders.engine.items.block.Block;
import com.xbuilders.engine.rendering.wireframeBox.Box;
import com.xbuilders.engine.utils.ByteUtils;

import java.io.IOException;
import java.util.Arrays;

public class ItemDropEntityLink extends EntityLink {

    public ItemDropEntityLink() {
        super(0, "item drop", () -> new ItemDrop(0));
    }

    public final static int DROP_LIVE_TIME = 1000;

//   public static byte[] toBytes(Item item, int lifetime) {
//        if (item == null) return null;
//        byte[] id = ByteUtils.shortToBytes(item.id);
//        byte[] lifetimeBytes = ByteUtils.intToBytes(lifetime);
//        return new byte[]{
//                id[0], id[1],
//                lifetimeBytes[0], lifetimeBytes[1], lifetimeBytes[2], lifetimeBytes[3]};
//    }


    static class ItemDrop extends Entity {

        Box box;
        Item item;
        double rand;
        int lifetime;

        public ItemDrop(int id) {
            super(id);
            aabb.isSolid = false;
        }


        @Override
        public void initializeOnDraw(byte[] bytes) {
            rand = Math.random();


            box = new Box();
            box.setLineWidth(3);
            box.setColor(0, 0.5f, 1, 1);
            box.setSize(.2f, .2f, .2f);

            if (bytes == null) {
                destroy();
            } else {
//                System.out.println(Arrays.toString(bytes));
//                item = Registrys.getItem((short) ByteUtils.bytesToShort(bytes[0], bytes[1]));
//                lifetime = ByteUtils.bytesToInt(bytes[3], bytes[4], bytes[5], bytes[6]);
            }
        }

//        @Override
//        public byte[] toBytes() throws IOException {
//            return ItemDropEntityLink.toBytes(item, lifetime);
//        }

        private boolean blockIsClear(Block camBlock, int x, int y, int z) {
            Block block = GameScene.world.getBlock(
                    x,
                    y,
                    z);
            return block.id == BlockRegistry.BLOCK_AIR.id || block == camBlock;
        }

        @Override
        public void draw() {
            if(box == null) return;
            if (MainWindow.frameCount % 10 != 0) {
                if (lifetime-- <= 0) {
                    destroy();
                }

                //Get the block at this position
                Block camBlock = GameScene.player.getBlockAtCameraPos();

                int x = (int) Math.floor(worldPosition.x);
                int y = (int) Math.floor(worldPosition.y);
                int z = (int) Math.floor(worldPosition.z);

                if (!blockIsClear(camBlock, x, y, z)) {
                    if (blockIsClear(camBlock, x, y - 1, z)) {
                        worldPosition.set(x, y - 1, z);
                    } else if (blockIsClear(camBlock, x - 1, y, z)) {
                        worldPosition.set(x - 1, y, z);
                    } else if (blockIsClear(camBlock, x + 1, y, z)) {
                        worldPosition.set(x + 1, y, z);
                    } else if (blockIsClear(camBlock, x, y, z - 1)) {
                        worldPosition.set(x, y, z - 1);
                    } else if (blockIsClear(camBlock, x, y, z + 1)) {
                        worldPosition.set(x, y, z + 1);
                    }
                } else if (blockIsClear(camBlock, x, y + 1, z)) {
                    worldPosition.set(x, y + 1, z);
                }
            }
            float bob = (float) (Math.sin((MainWindow.frameCount * 0.1) + rand) - 0.5) * 0.1f;
            box.setPosition(
                    worldPosition.x + 0.5f - (box.getSize().x / 2),
                    worldPosition.y + 0.5f - (box.getSize().y / 2) + bob,
                    worldPosition.z + 0.5f - (box.getSize().z / 2));
            box.getModelMatrix().rotateY((float) MainWindow.frameCount * 0.01f);
            box.draw(GameScene.projection, GameScene.view);
        }
    }
}
