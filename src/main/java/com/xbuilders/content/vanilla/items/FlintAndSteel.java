package com.xbuilders.content.vanilla.items;

import com.xbuilders.Main;
import com.xbuilders.engine.server.LocalServer;
import com.xbuilders.engine.server.Registrys;
import com.xbuilders.engine.server.item.Item;

public class FlintAndSteel extends Item {
    public FlintAndSteel() {
        super("xbuilders:flint_and_steel", "Flint and Steel");
        setIcon("flint_and_steel.png");
        maxDurability = 200;
        createClickEvent = (ray, stack) -> {
            Main.getServer().setBlock(
                    Registrys.getBlock("xbuilders:fire").id,
                    ray.getHitPosPlusNormal().x,
                    ray.getHitPosPlusNormal().y,
                    ray.getHitPosPlusNormal().z);
            stack.durability -= 0.2f;
            return true;
        };

        destroyClickEvent = createClickEvent;
    }


}
