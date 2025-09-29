package net.tuples.captcha.client.ui;

import net.minecraft.client.gui.components.Checkbox;
import net.minecraft.network.chat.Component;

import java.util.function.Consumer;

public class ConfigCheckbox extends Checkbox {
    private final Consumer<Boolean> onToggle;

    public ConfigCheckbox(int x, int y, int width, int height, Component message, boolean selected, Consumer<Boolean> onToggle) {
        super(x, y, width, height, message, selected, true);
        this.onToggle = onToggle;
    }

    @Override
    public void onPress() {
        super.onPress();
        if (onToggle != null) {
            onToggle.accept(this.selected());
        }
    }
}
