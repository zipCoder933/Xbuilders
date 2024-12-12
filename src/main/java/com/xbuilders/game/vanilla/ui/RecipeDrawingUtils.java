package com.xbuilders.game.vanilla.ui;

import com.xbuilders.engine.items.Registrys;
import com.xbuilders.engine.items.item.Item;
import com.xbuilders.engine.items.recipes.crafting.CraftingRecipe;
import com.xbuilders.engine.items.recipes.smelting.SmeltingRecipe;
import com.xbuilders.engine.ui.gameScene.items.UI_ItemGrid;
import org.lwjgl.nuklear.NkContext;
import org.lwjgl.system.MemoryStack;

import static org.lwjgl.nuklear.Nuklear.nk_layout_row_dynamic;

public class RecipeDrawingUtils {
    public static UI_ItemGrid viewInputGrid, viewOutputGrid;

    static {
        viewInputGrid = new UI_ItemGrid("Grid", false);
        viewOutputGrid = new UI_ItemGrid("Grid", false);
    }

    public static void drawRecipe(NkContext ctx, CraftingRecipe recipe) {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            nk_layout_row_dynamic(ctx, 200, 2);

            Item item = Registrys.getItem(recipe.output);
            viewInputGrid.items.clear();
            viewOutputGrid.items.clear();

            for (int i = 0; i < recipe.input.length; i++) {
                viewInputGrid.items.add(Registrys.getItem(recipe.input[i]));
            }
            viewOutputGrid.items.add(item);
            viewOutputGrid.items.add(null);

            viewInputGrid.draw(stack, ctx, 3);
            viewOutputGrid.draw(stack, ctx, 1);
        }
    }

    public static void drawRecipe(NkContext ctx, SmeltingRecipe recipe) {
//        try (MemoryStack stack = MemoryStack.stackPush()) {
//            nk_layout_row_dynamic(ctx, 100, 2);
//
//            viewInputGrid.items.clear();
//            viewInputGrid.items.add(Registrys.getItem(recipe.input));
//            viewInputGrid.draw(stack, ctx, 1);
//
//            viewInputGrid.items.clear();
//            viewInputGrid.items.add(Registrys.getItem(recipe.output));
//            viewInputGrid.draw(stack, ctx, 1);
//        }
    }

}
