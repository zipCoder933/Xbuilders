package com.xbuilders.engine.server.model.items.recipes;

import com.xbuilders.engine.client.ClientWindow;
import com.xbuilders.engine.client.visuals.Theme;
import org.lwjgl.nuklear.NkContext;
import org.lwjgl.nuklear.Nuklear;

import java.util.ArrayList;

/**
 * Used to render a recipe to render recipes with tags
 * Some recipes have multiple possible recipes that can be displayed, for example items with a #wood tag accept any wood item
 */
public class RecipeDisplay extends ArrayList<Recipe> {
    public RecipeDisplay() {
        super();
    }

    private int renderIndex = 0;

    public void drawRecipe(NkContext ctx, int height) {
        if (size() > 5) {
            Nuklear.nk_layout_row_dynamic(ctx, 10, 1);
            Nuklear.nk_style_set_font(ctx, Theme.font_9);
            Nuklear.nk_text(ctx, "(" + size() + "x)", Nuklear.NK_TEXT_ALIGN_LEFT);
        }
        get(renderIndex).drawRecipe(ctx, height);
        if (ClientWindow.frameCount % 50 == 0) {
            renderIndex = (renderIndex + 1) % size();
        }
    }

    public String toString() {
        return "FormattedRecipe{ size=" + size() + " recipes=" + super.toString() + '}';
    }
}