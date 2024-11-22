package com.xbuilders.game.items.blocks.barrel;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.dataformat.smile.SmileFactory;
import com.fasterxml.jackson.dataformat.smile.SmileGenerator;
import com.xbuilders.engine.gameScene.GameScene;
import com.xbuilders.engine.items.Registrys;
import com.xbuilders.engine.items.item.StorageSpace;
import com.xbuilders.engine.items.item.ItemStack;
import com.xbuilders.engine.ui.gameScene.items.UI_ItemStackGrid;
import com.xbuilders.engine.ui.gameScene.items.UI_ItemWindow;
import com.xbuilders.engine.utils.json.fasterXML.ItemStackDeserializer;
import com.xbuilders.engine.utils.json.fasterXML.ItemStackSerializer;
import com.xbuilders.engine.world.chunk.BlockData;
import com.xbuilders.engine.world.chunk.Chunk;
import com.xbuilders.window.NKWindow;
import org.lwjgl.nuklear.NkContext;
import org.lwjgl.nuklear.NkRect;
import org.lwjgl.system.MemoryStack;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class BarrelUI extends UI_ItemWindow {
    UI_ItemStackGrid barrelGrid, playerGrid;
    final StorageSpace barrelStorage;
    BlockData barrelData;
    final ObjectMapper objectMapper;
    Chunk chunk;

    public BarrelUI(NkContext ctx, NKWindow window) {
        super(ctx, window, "Barrel Grid");
        barrelStorage = new StorageSpace(33);
        menuDimensions.y = 550;
        barrelGrid = new UI_ItemStackGrid(window, "Barrel", barrelStorage, this);
        playerGrid = new UI_ItemStackGrid(window, "Player", GameScene.player.inventory, this);

        SmileFactory smileFactory = new SmileFactory();
        //set flags
        smileFactory.enable(SmileGenerator.Feature.ENCODE_BINARY_AS_7BIT);
        smileFactory.enable(SmileGenerator.Feature.CHECK_SHARED_STRING_VALUES);

        //create new object mapper based on factory
        objectMapper = new ObjectMapper(smileFactory);
        // Create a module to register custom serializer and deserializer
        SimpleModule module = new SimpleModule();
        module.addSerializer(ItemStack.class, new ItemStackSerializer()); // Register the custom serializer
        module.addDeserializer(ItemStack.class, new ItemStackDeserializer(Registrys.items.idMap)); // Register the custom deserializer
        objectMapper.registerModule(module);
    }

    @Override
    public void drawWindow(MemoryStack stack, NkRect windowDims2) {
        barrelGrid.draw(stack, ctx, maxColumns, 250);
        playerGrid.draw(stack, ctx, maxColumns, 250);
    }

    public void openUI(BlockData data, Chunk chunk) {
        barrelData = data;
        barrelStorage.clear();
        this.chunk = chunk;
        if (data != null) {
            try {
                // Deserialize the JSON string back into the object
//                System.out.println("Deserializing " + printSmileData(data.toByteArray()));
                ItemStack[] deserializedObject = objectMapper.readValue(data.toByteArray(),
                        new TypeReference<ItemStack[]>() {
                        });

                for (int i = 0; i < deserializedObject.length; i++) {
                    barrelStorage.items[i] = deserializedObject[i];
                }
            } catch (IOException e) {
                System.out.println("Error deserializing JSON, Making storage empty: " + e.getMessage());
            }
        }
        setOpen(true);
    }

    public void onCloseEvent() {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            objectMapper.writeValue(baos, barrelStorage.items);
            barrelData.setByteArray(baos.toByteArray());
            chunk.markAsModified();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        barrelStorage.clear();
    }
}
