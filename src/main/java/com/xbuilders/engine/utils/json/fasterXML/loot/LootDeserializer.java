package com.xbuilders.engine.utils.json.fasterXML.loot;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.xbuilders.engine.items.item.Item;
import com.xbuilders.engine.items.item.ItemStack;
import com.xbuilders.engine.items.loot.Loot;

import java.io.IOException;
import java.util.HashMap;

public class LootDeserializer extends StdDeserializer<Loot> {

//    HashMap<String, Item> itemsRegistry = new HashMap<>();

    public LootDeserializer(/*HashMap<String, Item> itemRegistry*/) {
        this(Loot.class);
//        this.itemsRegistry = itemRegistry;
    }

    public LootDeserializer(Class<?> vc) {
        super(vc);
    }

    @Override
    public Loot deserialize(JsonParser parser, DeserializationContext ctx)
            throws IOException {
        JsonNode node = parser.getCodec().readTree(parser);
        String itemID = node.get("item").asText();
//        Item item = itemsRegistry.get(itemID); //We get the item from the registry

        Loot obj = new Loot(
                () -> new ItemStack(itemID),
                (float) node.get("chance").asDouble(),
                node.get("maxItems").asInt());

        return obj;
    }
}