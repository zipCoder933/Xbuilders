package com.xbuilders.engine.common.commands;

import com.xbuilders.engine.client.LocalClient;
import com.xbuilders.engine.server.Registrys;
import com.xbuilders.engine.server.item.Item;
import com.xbuilders.engine.server.item.ItemStack;

public class GiveCommand extends Command {
    public GiveCommand() {
        super("give",
                "Usage: give <item> <quantity (optional)>");

        requiresOP(true)
                .executesServerSide((parts) -> {
                    if (parts.length >= 1) {
                        try {
                            String itemID = formatGetItemID(parts[0]);
                            int quantity = parts.length >= 2 ? Integer.parseInt(parts[1].trim()) : 1;
                            Item item = Registrys.getItem(itemID);
                            if (item == null) return "Unknown item: " + itemID;
                            else LocalClient.userPlayer.inventory.acquireItem(new ItemStack(item, quantity));
                            return "Given " + quantity + " " + item.name;
                        } catch (Exception e) {
                            return "Invalid";
                        }
                    }
                    return null;
                });
    }

    private String formatGetItemID(String part) {
        part = part.toLowerCase().trim().replace(" ", "_");
        if (!part.startsWith("xbuilders:")) part = "xbuilders:" + part;
        return part;
    }
}
