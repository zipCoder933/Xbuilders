package com.xbuilders.engine.items.entity;

import com.xbuilders.engine.MainWindow;
import com.xbuilders.engine.gameScene.GameScene;
import com.xbuilders.engine.items.Registrys;
import com.xbuilders.engine.items.block.BlockRegistry;
import com.xbuilders.engine.items.item.Item;
import com.xbuilders.engine.items.block.Block;
import com.xbuilders.engine.items.item.ItemStack;
import com.xbuilders.engine.rendering.wireframeBox.Box;
import com.xbuilders.engine.utils.math.MathUtils;
import org.joml.Vector3f;

public class ItemDrop extends Entity {
    public final static int DROP_LIVE_TIME = 10000;
    Box box;
    Item item;
    float seed;
    int lifetime;
    final Vector3f animatedPos = new Vector3f();
    final Vector3f playerHeadPos = new Vector3f();

    public ItemDrop(int id, long uniqueId) {
        super(id, uniqueId);
        aabb.isSolid = false;
    }


    @Override
    public void initializeOnDraw(byte[] bytes) {
        seed = (float) Math.random();
        box = new Box();
        box.setLineWidth(3);


        if (bytes == null || bytes.length == 0) {
            destroy();
            return;
        }

        String itemID = new String(bytes);
        box.setColor(0, 0.5f, 1, 1);
        box.setSize(.2f, .2f, .2f);

        item = Registrys.getItem(itemID);
        lifetime = DROP_LIVE_TIME;
        animatedPos.set(worldPosition.x, worldPosition.y + 0.5f, worldPosition.z);
    }


    private boolean blockIsClear(Block camBlock, int x, int y, int z) {
        Block block = GameScene.world.getBlock(
                x,
                y,
                z);
        return block.id == BlockRegistry.BLOCK_AIR.id || block == camBlock;
    }

    @Override
    public void draw() {
        if (box == null) return;
        //TODO: Make a simplified way to pinpoint player head and feet
        playerHeadPos.set(GameScene.player.aabb.worldPosition).add(GameScene.player.aabb.offset).add(0, 0.5f, 0);

        if (MainWindow.frameCount % 10 != 0) {
            if (lifetime-- <= 0) {
                destroy();
            }
            if (distToPlayer < 2) {
                worldPosition.set(playerHeadPos);
            } else {
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
        }
        double sin = Math.sin((MainWindow.frameCount * 0.1) + seed);
        float bob = (float) (sin - 0.5) * 0.1f;

        float animationSpeed = .1f;
        if (distToPlayer < 2) {
            animationSpeed = .35f;
        }

        animatedPos.set(
                (float) MathUtils.curve(animatedPos.x, worldPosition.x, animationSpeed),
                (float) MathUtils.curve(animatedPos.y, worldPosition.y, animationSpeed),
                (float) MathUtils.curve(animatedPos.z, worldPosition.z, animationSpeed));

        if (animatedPos.distance(playerHeadPos) < 0.1) {
            GameScene.player.inventory.acquireItem(new ItemStack(item));
            destroy();
        }

        box.setPosition(
                animatedPos.x + 0.5f - (box.getSize().x / 2),
                animatedPos.y + 0.5f - (box.getSize().y / 2) + bob,
                animatedPos.z + 0.5f - (box.getSize().z / 2));

        box.getModelMatrix().rotateY((MainWindow.frameCount * 0.01f) + seed);
        box.draw(GameScene.projection, GameScene.view);
    }
}

