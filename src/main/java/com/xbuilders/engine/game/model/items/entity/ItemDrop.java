package com.xbuilders.engine.game.model.items.entity;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.dataformat.smile.SmileFactory;
import com.fasterxml.jackson.dataformat.smile.SmileGenerator;
import com.xbuilders.engine.MainWindow;
import com.xbuilders.engine.game.model.GameScene;
import com.xbuilders.engine.game.model.items.Registrys;
import com.xbuilders.engine.game.model.items.block.BlockRegistry;
import com.xbuilders.engine.game.model.items.block.Block;
import com.xbuilders.engine.game.model.items.item.ItemStack;
import com.xbuilders.engine.client.visuals.rendering.wireframeBox.Box;
import com.xbuilders.engine.utils.ErrorHandler;
import com.xbuilders.engine.utils.json.fasterXML.itemStack.ItemStackDeserializer;
import com.xbuilders.engine.utils.json.fasterXML.itemStack.ItemStackSerializer;
import com.xbuilders.engine.utils.math.MathUtils;
import org.joml.Vector3f;

import java.io.IOException;

public class ItemDrop extends Entity {
    public final static int DROP_LIVE_TIME = 10000;
    private static Box box;
    private final static SmileFactory smileFactory = new SmileFactory();
    public final static ObjectMapper objectMapper = new ObjectMapper(smileFactory);

    private byte seed;
    private boolean droppedFromPlayer;
    private int timeSinceDropped;
    private final Vector3f animatedPos = new Vector3f();
    private final Vector3f playerHeadPos = new Vector3f();
    private ItemStack stack;
    boolean canGet;


    static {
        smileFactory.enable(SmileGenerator.Feature.ENCODE_BINARY_AS_7BIT);
        smileFactory.enable(SmileGenerator.Feature.CHECK_SHARED_STRING_VALUES);
        SimpleModule module = new SimpleModule();
        module.addSerializer(ItemStack.class, new ItemStackSerializer()); // Register the custom serializer
        module.addDeserializer(ItemStack.class, new ItemStackDeserializer(Registrys.items.idMap)); // Register the custom deserializer
        objectMapper.registerModule(module);
    }


    public ItemDrop(int id, long uniqueId) {
        super(id, uniqueId);
        aabb.isSolid = false;
    }

    final int BYTES_BEGINNING_DATA_SIZE = 1;

    @Override
    public void initializeOnDraw(byte[] bytes) {
        seed = (byte) (Math.random() * 255);
        if (box == null) {
            box = new Box();
            box.setLineWidth(3);
            box.setColor(0, 0.5f, 1, 1);
            box.setSize(.2f, .2f, .2f);
        }

        canGet = false;
        if (bytes == null || bytes.length == 0) {
            System.out.println("EMPTY ITEM DROP");
            destroy();
            return;
        }
        try {
            droppedFromPlayer = bytes[0] == 1;
            //Make a new list without the beginnning data
            byte[] bytes2 = new byte[bytes.length - BYTES_BEGINNING_DATA_SIZE];
            for (int i = 0; i < bytes2.length; i++) {
                bytes2[i] = bytes[i + BYTES_BEGINNING_DATA_SIZE];
            }
            stack = objectMapper.readValue(bytes2, ItemStack.class);
            System.out.println("READING STACK: " + stack.toString() + " Dropped From Player: " + droppedFromPlayer);
        } catch (IOException e) {
            ErrorHandler.log(e);
        }
        timeSinceDropped = 0;
        animatedPos.set(worldPosition.x, worldPosition.y + 0.5f, worldPosition.z);
    }


    private boolean blockIsClear(Block camBlock, int x, int y, int z) {
        Block block = GameScene.world.getBlock(x, y, z);
        return block.id == BlockRegistry.BLOCK_AIR.id || block == camBlock;
    }

    @Override
    public void draw() {
        if (box == null) return;
        playerHeadPos.set(GameScene.player.aabb.worldPosition).add(GameScene.player.aabb.offset).add(0, 0.5f, 0);
        if (MainWindow.frameCount % 20 != 0) { //Update every 20 frames
            timeSinceDropped++;
            if (timeSinceDropped > DROP_LIVE_TIME) {
                System.out.println("TIMEOUT, DELETING ITEM DROP");
                destroy();
            } else if (stack == null) {
                System.out.println("STACK IS NULL, DELETING ITEM DROP");
                destroy();
            }
            canGet = (timeSinceDropped > 100 || !droppedFromPlayer) && GameScene.player.inventory.hasRoomForItem(stack);
//            if (distToPlayer < 5) {
//                System.out.println("item: " + stack + " DIST TO PLAYER: " + distToPlayer + " CAN GET: " + canGet + " TIME SINCE DROPPED: " + timeSinceDropped + " hasRoomForItem: " + GameScene.player.inventory.hasRoomForItem(stack));
//            }
            if (distToPlayer < 2 && canGet) {
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
        double sin = Math.sin((MainWindow.frameCount * 0.1) + ((double) seed / 255));
        float bob = (float) (sin - 0.5) * 0.1f;

        float animationSpeed = 0.1f;
        if (distToPlayer < 2 && canGet) {
            animationSpeed = .35f;
        }

        animatedPos.set(
                (float) MathUtils.curve(animatedPos.x, worldPosition.x, animationSpeed),
                (float) MathUtils.curve(animatedPos.y, worldPosition.y, animationSpeed),
                (float) MathUtils.curve(animatedPos.z, worldPosition.z, animationSpeed));

        if (animatedPos.distance(playerHeadPos) < 0.1 && canGet) {
            System.out.println("CONSUMED BY: " + GameScene.player.userInfo.name);
            GameScene.player.inventory.acquireItem(stack);
            System.out.println("DELETING ITEM DROP");
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

