package com.xbuilders.engine.ui.topMenu;

import com.xbuilders.engine.settings.BoundedFloat;
import com.xbuilders.engine.settings.BoundedInt;
import com.xbuilders.engine.settings.EngineSettings;
import com.xbuilders.game.Main;
import com.xbuilders.window.nuklear.components.NumberBox;
import com.xbuilders.window.nuklear.components.TextBox;
import org.lwjgl.nuklear.NkContext;
import org.lwjgl.nuklear.NkRect;
import org.lwjgl.system.MemoryStack;

import java.lang.reflect.Field;

import static org.lwjgl.nuklear.Nuklear.*;

class SettingsField {
    Field field;
    String key;
    Class<?> type;
    Object value;

    TextBox textBox;
    NumberBox numberBox;

    private boolean isNumber() {
        return type.equals(BoundedInt.class) ||
                type.equals(BoundedFloat.class) ||
                type.equals(int.class) || type.equals(float.class) || type.equals(double.class) ||
                type.equals(long.class) || type.equals(short.class) || type.equals(byte.class);

    }

    public void layout(NkContext ctx, MemoryStack stack, NkRect windowDims) {
        nk_layout_row_dynamic(ctx, 30, 2);
        nk_label(ctx, key, NK_TEXT_ALIGN_LEFT);

        if (isNumber()) {
            numberBox.render(ctx);
        } else if (type.equals(String.class)) {
            textBox.render(ctx);
        } else if (type.equals(boolean.class)) {
            boolean b = (boolean) value;
            if (nk_button_label(ctx, b ? "ENABLED" : "DISABLED")) {
                setValue(!b);
                Main.saveSettings();
            }
        }
    }

    public String toString() {
        return ("SettingsField: " + this.key + ": " + this.value + " \t" + this.type);
    }

    public SettingsField(Field field) throws IllegalAccessException {
        this.field = field;
        this.key = formatKey(field.getName());
        this.type = field.getType();
        this.value = field.get(Main.settings);

        if (type.equals(BoundedInt.class)) {
            numberBox = new NumberBox(50);

            BoundedInt bound = ((BoundedInt) value);
            numberBox.setValueAsNumber(bound.value);
            numberBox.setMinValue(bound.min);
            numberBox.setMaxValue(bound.max);

            numberBox.setOnChangeEvent(() -> {
                BoundedInt b = ((BoundedInt) value);
                b.value = (int) numberBox.getValueAsNumber();
                value = b;
                setValue(b);
            });
        } else if (type.equals(BoundedFloat.class)) {
            numberBox = new NumberBox(50);
            BoundedFloat bound = ((BoundedFloat) value);
            numberBox.setValueAsNumber(bound.value);
            numberBox.setMinValue(bound.min);
            numberBox.setMaxValue(bound.max);

            numberBox.setOnChangeEvent(() -> {
                BoundedFloat b = ((BoundedFloat) value);
                b.value = (float) numberBox.getValueAsNumber();
                value = b;
                setValue(b);
            });
        } else if (type.equals(int.class)) {
            numberBox = new NumberBox(50);
            numberBox.setValueAsNumber((int) value);
            numberBox.setOnChangeEvent(() -> {
                value = (int) numberBox.getValueAsNumber();
                setValue((int) value);
            });
        } else if (type.equals(float.class)) {
            numberBox = new NumberBox(50);
            numberBox.setValueAsNumber((float) value);
            numberBox.setOnChangeEvent(() -> {
                value = (float) numberBox.getValueAsNumber();
                setValue((float) value);
            });
        } else if (type.equals(String.class)) {
            textBox = new TextBox(50);
            textBox.setValueAsString((String) value);
            textBox.setOnChangeEvent(() -> {
                value = textBox.getValueAsString();
                setValue(value);
            });
        } else if (type.equals(boolean.class)) {
            value = (boolean) value;
        } else throw new IllegalArgumentException("Unsupported type " + type);
    }

    private String formatKey(String key) {
        //Format the key from camelCase to normal text (every uppercae leter should be preceded by a space)
        key = key.replaceAll("([a-z])([A-Z])", "$1 $2");
        key = key.replaceAll("_", ", ");
        //capitalize the first letter of every word
        String[] words = key.split(" ");
        key = "";
        for (String word : words) {
            key += word.substring(0, 1).toUpperCase() + word.substring(1) + " ";
        }
        return key;
    }

    public void setValue(Object value) {
        try {
            field.set(Main.settings, value);
            this.value = value;
            Main.saveSettings();
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
}
