/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.xbuilders.content.vanilla.ui;

import com.xbuilders.engine.server.GameMode;
import com.xbuilders.engine.server.Server;
import com.xbuilders.engine.server.items.item.Item;
import com.xbuilders.engine.server.items.recipes.RecipeDisplay;
import com.xbuilders.engine.server.items.recipes.RecipeList;
import com.xbuilders.engine.server.items.recipes.RecipeRegistry;
import com.xbuilders.engine.client.visuals.Theme;
import com.xbuilders.engine.client.visuals.gameScene.items.UI_ItemIndex;
import com.xbuilders.engine.client.visuals.gameScene.items.UI_ItemWindow;
import com.xbuilders.window.NKWindow;
import com.xbuilders.window.WindowEvents;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.nuklear.NkContext;
import org.lwjgl.nuklear.NkRect;
import org.lwjgl.nuklear.NkVec2;
import org.lwjgl.system.MemoryStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static org.lwjgl.nuklear.Nuklear.*;

/**
 * @author zipCoder933
 */
public class UI_RecipeIndex extends UI_ItemWindow implements WindowEvents {

    public static final int KEY_OPEN_RECIPE_INDEX = GLFW.GLFW_KEY_R;

    Item selectedItem;
    RecipeList selectedRecipeClass;
    HashMap<RecipeList, ArrayList<RecipeDisplay>> availableRecipes = new HashMap<>();

    public UI_RecipeIndex(NkContext ctx, Item[] itemList, NKWindow window) {
        super(ctx, window, "Recipe List");

        allItems = new UI_ItemIndex(this);
        allItems.setItemList(itemList);
        allItems.itemClickEvent = this::selectItem;

        menuDimensions.y = Allitems_Height + recipeView_Height + 100;

        nk_begin(ctx, title, NkRect.create(), windowFlags);
        nk_end(ctx);
        setOpen(false);
    }

    private void selectItem(Item item) {
        System.out.println("\nSelected item: " + item.name);
        availableRecipes.clear();
        selectedItem = item;
        for (RecipeList registry : RecipeRegistry.allRecipeLists) {
            ArrayList<RecipeDisplay> recipes = registry.getDisplayRecipesFromOutput(item);
            if (recipes.isEmpty()) continue;
            availableRecipes.put(registry, recipes);
        }
        if (!availableRecipes.isEmpty()) {
            //Get the first key and assign it to the recipe
            Map.Entry<RecipeList, ArrayList<RecipeDisplay>> entry = availableRecipes.entrySet().iterator().next();
            selectRecipeClass(entry.getKey());
        }
        System.out.println("Available recipes: " + availableRecipes);
    }

    final int Allitems_Height = 200; //total item list window size
    final int recipeView_Height = 240; //player inventory window size
    UI_ItemIndex allItems;


    public void onOpenEvent() {
        if (Server.getGameMode() == GameMode.SPECTATOR) setOpen(false);
    }

    public void onCloseEvent() {
    }

    @Override
    public void drawWindow(MemoryStack stack, NkRect windowDims2) {
        if (Server.getGameMode() == GameMode.SPECTATOR) {
            setOpen(false);
        }

        nk_layout_row_dynamic(ctx, recipeView_Height, 1);
        if (nk_group_begin(ctx, "Recipe view", 0)) {
            if (selectedItem != null && !availableRecipes.isEmpty()) {
                nk_layout_row_dynamic(ctx, 20, availableRecipes.size());

                /**
                 * Draw recipe classes (Crafting, smelting)
                 */
                for (RecipeList list : availableRecipes.keySet()) {
                    if (selectedRecipeClass != null && list == selectedRecipeClass) {
                        ctx.style().button().normal().data().color().set(Theme.color_lightGray);
                        ctx.style().button().border_color().set(Theme.color_white);
                    } else Theme.resetEntireButtonStyle(ctx);

                    if (nk_button_text(ctx, list.name)) {
                        selectRecipeClass(list);
                    }
                }
                Theme.resetEntireButtonStyle(ctx);

                /**
                 * Draw visible recipes
                 */
                if (selectedRecipeClass != null) {
                    availableRecipes.get(selectedRecipeClass).forEach((recipe) -> {
                        recipe.drawRecipe(ctx, recipeView_Height - 50);
                    });
                }
            }
        }
        nk_group_end(ctx);

        allItems.draw(ctx, stack, Allitems_Height);

        Theme.resetEntireButtonStyle(ctx);
    }

    private void selectRecipeClass(RecipeList list) {
        selectedRecipeClass = list;
        System.out.println("Switching to " + list.name);
    }


    @Override
    public void windowResizeEvent(int width, int height) {

    }

    public boolean keyEvent(int key, int scancode, int action, int mods) {
        if (Server.getGameMode() == GameMode.SPECTATOR) return false;

        if (allItems.keyEvent(key, scancode, action, mods)) return true;
        if (action == GLFW.GLFW_RELEASE && key == KEY_OPEN_RECIPE_INDEX) {
            setOpen(!isOpen());
            return true;
        }
        return false;
    }

    @Override
    public boolean mouseScrollEvent(NkVec2 scroll, double xoffset, double yoffset) {
        if (Server.getGameMode() == GameMode.SPECTATOR) return false;
        allItems.mouseScrollEvent(scroll, xoffset, yoffset);
        return true;
    }

}
