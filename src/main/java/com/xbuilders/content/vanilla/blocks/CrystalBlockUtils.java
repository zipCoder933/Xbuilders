package com.xbuilders.content.vanilla.blocks;

import com.xbuilders.Main;
import com.xbuilders.engine.client.LocalClient;
import com.xbuilders.engine.server.LocalServer;
import com.xbuilders.engine.server.Registrys;
import com.xbuilders.engine.server.block.Block;

import static com.xbuilders.content.vanilla.Blocks.*;

public class CrystalBlockUtils {

    private static Block.RandomTickEvent randomTickEvent(short crystal) {
        return (x, y, z) -> {
            Block above = LocalClient.world.getBlock(x, y - 1, z);
            if (above.isAir() || above.isLiquid()) {
                Main.getServer().setBlock(crystal, x, y - 1, z);
                return true;
            }
            return false;
        };
    }

    public static void init() {
        //Crystals
        Block crystalBlock = Registrys.getBlock("xbuilders:amethyst_block");
        if (crystalBlock != null) crystalBlock.randomTickEvent = randomTickEvent(BLOCK_AMETHYST_CRYSTAL);

        crystalBlock = Registrys.getBlock("xbuilders:aquamarine_block");
        if (crystalBlock != null) crystalBlock.randomTickEvent = randomTickEvent(BLOCK_AQUAMARINE_CRYSTAL);

        crystalBlock = Registrys.getBlock("xbuilders:jade_block");
        if (crystalBlock != null) crystalBlock.randomTickEvent = randomTickEvent(BLOCK_JADE_CRYSTAL);

        crystalBlock = Registrys.getBlock("xbuilders:ruby_block");
        if (crystalBlock != null) crystalBlock.randomTickEvent = randomTickEvent(BLOCK_RUBY_CRYSTAL);
    }
}
