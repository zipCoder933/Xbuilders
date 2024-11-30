package com.xbuilders.engine.items.recipes;

import java.util.Arrays;
import java.util.Objects;

public class CraftingRecipe {
    public String[] input = new String[9];
    public String output;
    public int amount = 1;


    public CraftingRecipe() {
    }

    public CraftingRecipe(String a, String b, String c,
                          String d, String e, String f,
                          String g, String h, String i,
                          String output, int amount) {
        this.input[0] = a;
        this.input[1] = b;
        this.input[2] = c;
        this.input[3] = d;
        this.input[4] = e;
        this.input[5] = f;
        this.input[6] = g;
        this.input[7] = h;
        this.input[8] = i;

        this.output = output;
        this.amount = amount;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CraftingRecipe that)) return false;
        return Objects.deepEquals(input, that.input);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(input);
    }

    public String toString() {
        return Arrays.toString(input)+" -> "+output;
    }
}
