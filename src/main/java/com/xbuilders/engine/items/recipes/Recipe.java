package com.xbuilders.engine.items.recipes;

import org.lwjgl.nuklear.NkContext;

public abstract class Recipe {
    public abstract void drawRecipe(NkContext ctx, int groupHeight);

    public abstract DisplayRecipe getDisplayRecipe();
}
