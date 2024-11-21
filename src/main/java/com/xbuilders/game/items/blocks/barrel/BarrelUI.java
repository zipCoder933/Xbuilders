package com.xbuilders.game.items.blocks.barrel;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.dataformat.smile.SmileFactory;
import com.fasterxml.jackson.dataformat.smile.SmileGenerator;
import com.xbuilders.engine.gameScene.GameScene;
import com.xbuilders.engine.items.Registrys;
import com.xbuilders.engine.items.StorageSpace;
import com.xbuilders.engine.items.item.Item;
import com.xbuilders.engine.items.item.ItemStack;
import com.xbuilders.engine.ui.items.UI_ItemStackGrid;
import com.xbuilders.engine.ui.items.UI_ItemWindow;
import com.xbuilders.engine.utils.json.fasterXML.ItemStackDeserializer;
import com.xbuilders.engine.utils.json.fasterXML.ItemStackSerializer;
import com.xbuilders.engine.world.chunk.BlockData;
import com.xbuilders.tests.fasterXML.smile.custom.RecordDeserializer;
import com.xbuilders.tests.fasterXML.smile.custom.RecordSerializer;
import com.xbuilders.tests.fasterXML.smile.smileObject;
import com.xbuilders.window.NKWindow;
import org.lwjgl.nuklear.NkContext;
import org.lwjgl.nuklear.NkRect;
import org.lwjgl.system.MemoryStack;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;

public class BarrelUI extends UI_ItemWindow {
    UI_ItemStackGrid barrelGrid, playerGrid;
    final StorageSpace barrelStorage;
    BlockData barrelData;
    final ObjectMapper objectMapper;

    public BarrelUI(NkContext ctx, NKWindow window, int slots) {
        super(ctx, window, "Barrel Grid");
        barrelStorage = new StorageSpace(slots);
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
    }

    @Override
    public void drawWindow(MemoryStack stack, NkRect windowDims2) {
        barrelGrid.draw(stack, ctx, maxColumns, 250);
        playerGrid.draw(stack, ctx, maxColumns, 250);
    }

    public void openUI(BlockData data) {
        barrelData = data;
        barrelStorage.clear();

        if (data != null) {
            try {
                // Deserialize the JSON string back into the object
                ItemStack[] deserializedObject = objectMapper.readValue(data.toByteArray(),
                        new TypeReference<ItemStack[]>() {
                        });

                for (int i = 0; i < deserializedObject.length; i++) {
                    barrelStorage.items[i] = deserializedObject[i];
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        setOpen(true);
    }

    public void onCloseEvent() {
        // Use ByteArrayOutputStream (byte-based)
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

        // Serialize the object to JSON using the ByteArrayOutputStream (byte-based)
        try {
            objectMapper.writeValue(byteArrayOutputStream, barrelStorage.items);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        barrelData.setByteArray(byteArrayOutputStream.toByteArray());
        barrelStorage.clear();
    }
}
