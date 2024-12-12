package com.xbuilders.engine.items.recipes;

import com.xbuilders.engine.MainWindow;
import com.xbuilders.engine.gameScene.GameScene;
import com.xbuilders.engine.items.item.Item;
import org.lwjgl.nuklear.NkContext;

public abstract class Recipe {
    public abstract void drawRecipe(NkContext ctx);
}
