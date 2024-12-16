package com.xbuilders.game.vanilla.ui;

import com.xbuilders.engine.items.Registrys;
import com.xbuilders.engine.items.item.Item;
import com.xbuilders.engine.items.recipes.crafting.CraftingRecipe;
import com.xbuilders.engine.items.recipes.smelting.SmeltingRecipe;
import com.xbuilders.engine.ui.gameScene.items.UI_ItemGrid;
import org.lwjgl.nuklear.NkContext;
import org.lwjgl.nuklear.Nuklear;
import org.lwjgl.system.MemoryStack;

import static org.lwjgl.nuklear.Nuklear.*;

public class RecipeDrawingUtils {
    public static UI_ItemGrid viewInputGrid, viewOutputGrid;

    static {
        viewInputGrid = new UI_ItemGrid("Grid", false);
        viewOutputGrid = new UI_ItemGrid("Grid", false);
    }

    public static void drawRecipe(NkContext ctx, CraftingRecipe recipe, int height) {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            nk_layout_row_dynamic(ctx, 170, 2);

            Item item = Registrys.getItem(recipe.output);
            viewInputGrid.items.clear();
            viewOutputGrid.items.clear();

            for (int i = 0; i < recipe.input.length; i++) {
                viewInputGrid.items.add(Registrys.getItem(recipe.input[i]));
            }
            viewOutputGrid.items.add(item);

            viewInputGrid.draw(stack, ctx, 3);
            viewOutputGrid.draw(stack, ctx, 1);

            nk_layout_row_dynamic(ctx, 10, 1);
            Nuklear.nk_label(ctx, "X" + recipe.amount, Nuklear.NK_TEXT_ALIGN_LEFT);
        }
    }

    public static void drawRecipe(NkContext ctx, SmeltingRecipe recipe, int height) {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            nk_layout_row_dynamic(ctx, 70, 2);

            viewInputGrid.items.clear();
            viewInputGrid.items.add(Registrys.getItem(recipe.input));
            viewInputGrid.draw(stack, ctx, 1);

            viewInputGrid.items.clear();
            viewInputGrid.items.add(Registrys.getItem(recipe.output));
            viewInputGrid.draw(stack, ctx, 1);

            nk_layout_row_dynamic(ctx, 10, 1);
            Nuklear.nk_label(ctx, "X" + recipe.amount, Nuklear.NK_TEXT_ALIGN_LEFT);
        }
    }

}
