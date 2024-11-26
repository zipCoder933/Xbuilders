package com.xbuilders.engine.items.recipes;

import java.util.Arrays;
import java.util.Objects;

public class CraftingRecipeInput {
    public final String[] recipeMap = new String[9];

    public CraftingRecipeInput(String[] recipeMap) {
        this.recipeMap[0] = recipeMap[0];
        this.recipeMap[1] = recipeMap[1];
        this.recipeMap[2] = recipeMap[2];
        this.recipeMap[3] = recipeMap[3];
        this.recipeMap[4] = recipeMap[4];
        this.recipeMap[5] = recipeMap[5];
        this.recipeMap[6] = recipeMap[6];
        this.recipeMap[7] = recipeMap[7];
        this.recipeMap[8] = recipeMap[8];
    }

    public CraftingRecipeInput(String a, String b, String c,
                               String d, String e, String f,
                               String g, String h, String i) {
        this.recipeMap[0] = a;
        this.recipeMap[1] = b;
        this.recipeMap[2] = c;
        this.recipeMap[3] = d;
        this.recipeMap[4] = e;
        this.recipeMap[5] = f;
        this.recipeMap[6] = g;
        this.recipeMap[7] = h;
        this.recipeMap[8] = i;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CraftingRecipeInput that)) return false;
        return Objects.deepEquals(recipeMap, that.recipeMap);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(recipeMap);
    }
}
