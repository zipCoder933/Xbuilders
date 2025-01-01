package com.xbuilders.engine.utils.json.fasterXML.itemStack;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.xbuilders.engine.server.model.items.item.Item;
import com.xbuilders.engine.server.model.items.item.ItemStack;

import java.io.IOException;
import java.util.HashMap;

public class ItemStackDeserializer extends StdDeserializer<ItemStack> {


    HashMap<String, Item> itemsRegistry = new HashMap<>();

    public ItemStackDeserializer(HashMap<String, Item> itemRegistry) {
        this(ItemStack.class);
        this.itemsRegistry = itemRegistry;
    }

    public ItemStackDeserializer(Class<?> vc) {
        super(vc);
    }

    @Override
    public ItemStack deserialize(JsonParser parser, DeserializationContext ctx)
            throws IOException, JacksonException {
        JsonNode node = parser.getCodec().readTree(parser);
        String itemID = node.get("item").asText();
        Item item = itemsRegistry.get(itemID); //We get the item from the registry
        ItemStack obj = new ItemStack(item); //We create the ItemStack
        obj.durability = (float) node.get("durability").asInt();
        obj.stackSize = node.get("stackSize").asInt();

        if (node.has("data")) {
            obj.nbtData = node.get("data").asText().getBytes();
        }
        return obj;
    }
}