package net.tuples.captcha.client;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.layouts.GridLayout;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.tuples.captcha.Captcha;
import net.tuples.captcha.config.CaptchaConfig;
import net.tuples.captcha.config.CaptchaData;

import java.util.*;

public class CaptchaScreen extends Screen {
    // UI elements
    private final Map<CaptchaImageButton, Boolean> buttonStates = new LinkedHashMap<>();
    private final int cellSize = 32;
    private final int cellSpacing = 5;
    private int gridSize;
    private GridLayout gridDisplay;

    // Solving info
    private final Map<String, Boolean> captchaKey = new LinkedHashMap<>();
    private JsonObject captcha_data;
    private String captchaId;

    // Client and sounds
    private final Minecraft client;
    private SoundManager soundManager;
    private SoundInstance sound;

    private final Button confirm = Button.builder(
            Component.translatable("captcha.ui.button.confirm"), button -> {
                List<Boolean> correctValues = new ArrayList<>(captchaKey.values());
                boolean allMatch = true;
                int index = 0;
                for (Map.Entry<CaptchaImageButton, Boolean> entry : buttonStates.entrySet()) {
                    if (entry.getValue() != correctValues.get(index)) {
                        allMatch = false;
                        break;
                    }
                    index++;
                }
                if (allMatch) {
//                    if (Captcha.config.soundEffects)
                    soundManager.play(SimpleSoundInstance.forAmbientAddition(CaptchaSounds.get("button3")));
                    Minecraft.getInstance().setScreen(null);
                }
                else {
                    if (CaptchaConfig.soundEffects)
                        soundManager.play(SimpleSoundInstance.forAmbientAddition(CaptchaSounds.get("button10")));
                    refreshCaptcha();
                }
            })
            .size(80, 20)
            .tooltip(Tooltip.create(Component.literal(":)")))
            .build();

    public CaptchaScreen() {
        super(Component.translatable("captcha.narrator.title"));
        client = Minecraft.getInstance();
        soundManager = client.getSoundManager();
//        if (CaptchaConfig.soundEffects)
        soundManager.play(SimpleSoundInstance.forAmbientAddition(CaptchaSounds.get("blip1")));
        refreshCaptcha();
    }

    private void refreshCaptcha() {
        // Reset elements
        buttonStates.clear();
        gridDisplay = new GridLayout().spacing(cellSpacing);

        // Get random captcha data
        List<String> keys = new ArrayList<>(CaptchaData.captchas.keySet());

        // Remove previous captcha from possible selection
        if (captchaId != null) {
            keys.remove(captchaId);
        }

        int rand = new Random().nextInt(keys.size());
        captchaId = keys.get(rand);
        captcha_data = CaptchaData.captchas.get(captchaId).getAsJsonObject();

        // Update captcha key data for solving and image loading
        captchaKey.clear();
        for (Map.Entry<String, JsonElement> entry : captcha_data.get("images").getAsJsonObject().entrySet()) {
            captchaKey.put(entry.getKey(), entry.getValue().getAsBoolean());
        }

        // Play random spooky sound if on creep captcha
        if (captcha_data.get("creep").getAsBoolean()) { // && Captcha.config.sounds
            if (client.level != null && client.player != null) {
                if (soundManager.isActive(sound))
                    soundManager.stop(sound);
                sound = SimpleSoundInstance.forAmbientAddition(CaptchaSounds.randomWhisper());
                soundManager.play(sound);
            }
        } else {
            if (soundManager.isActive(sound))
                soundManager.stop(sound);
        }

        initializeButtons();
        updateElements();
    }

    private void initializeButtons() {
        List<String> imagePaths = new ArrayList<>(captchaKey.keySet());
        gridSize = (int) Math.ceil(Math.sqrt(imagePaths.size()));

        for (int i = 0; i < imagePaths.size(); i++) {
            ResourceLocation image = ResourceLocation.fromNamespaceAndPath(Captcha.MOD_ID, imagePaths.get(i));
            CaptchaImageButton button = new CaptchaImageButton(0, 0, cellSize, cellSize, image, b -> {
                buttonStates.put((CaptchaImageButton) b, !buttonStates.getOrDefault(b, false));
            });
            buttonStates.put(button, false);
            gridDisplay.addChild(button, i / gridSize, i % gridSize);
        }
    }

    private void layoutGrid() {
        int index = 0;
        for (CaptchaImageButton button : buttonStates.keySet()) {
            int row = index / gridSize;
            int col = index % gridSize;
            int x = gridDisplay.getX() + col * (cellSize + cellSpacing);
            int y = gridDisplay.getY() + row * (cellSize + cellSpacing);
            button.setPosition(x, y);
            index++;
        }
    }

    private void updateElements() {
        int gridWidth = (cellSize * gridSize) + (cellSpacing * (gridSize - 1));
        int gridY = (int)(height * 0.2F);

        gridDisplay.setPosition((width / 2) - (gridWidth / 2), gridY);

        layoutGrid();

        buttonStates.keySet().forEach(button -> {
            if (!this.children().contains(button)) {
                addRenderableWidget(button);
            }
        });

        confirm.setPosition((width / 2) - 40, gridY + gridWidth + 10);
        if (!this.children().contains(confirm)) {
            addRenderableWidget(confirm);
        }
    }

    private void renderText(GuiGraphics guiGraphics, int mouseX, int mouseY, float delta) {
        guiGraphics.drawCenteredString(client.font, Component.translatable("captcha.ui.instruction"), width / 2, (int)(height * 0.05F), 0xffffffff);
        guiGraphics.drawCenteredString(client.font, Component.translatable(captcha_data.get("text").getAsString()), width / 2, (int)(height * 0.1F), 0xffffc321);
    }

    @Override
    protected void init() {
        updateElements();
    }

    @Override
    public boolean shouldCloseOnEsc() {
        return false;
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float delta) {
        super.render(guiGraphics, mouseX, mouseY, delta);
        this.renderText(guiGraphics, mouseX, mouseY, delta);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    // Custom image button object
    private class CaptchaImageButton extends Button {
        private static final int outlineMargin = 2;
        private static final int outlineColor = 0x80FFFFFF;
        private static final int selectedColor = 0xFFFFFFFF;

        private final ResourceLocation image;

        protected CaptchaImageButton(int x, int y, int width, int height, ResourceLocation image, OnPress onPress) {
            super(x, y, width, height, Component.empty(), onPress, (MutableText) -> Component.translatable("captcha.narrator.button"));
            this.image = image;
        }

        @Override
        protected void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float delta) {
            guiGraphics.blit(
                    image,
                    getX(),
                    getY(),
                    getX() + getWidth(),
                    getY() + getHeight(),
                    0f, 1f,
                    0f, 1f
            );

            // Determine outline states
            if (buttonStates.getOrDefault(this, false)) {
                drawBorder(
                        guiGraphics,
                        this.getX() - outlineMargin,
                        this.getY() - outlineMargin,
                        this.getWidth() + (outlineMargin * 2),
                        this.getHeight() + (outlineMargin * 2),
                        outlineColor
                );
            } else if (this.isHovered()) {
                drawBorder(
                        guiGraphics,
                        this.getX() - outlineMargin,
                        this.getY() - outlineMargin,
                        this.getWidth() + (outlineMargin * 2),
                        this.getHeight() + (outlineMargin * 2),
                        selectedColor
                );
            }
        }

        private void drawBorder(GuiGraphics guiGraphics, int x, int y, int width, int height, int color) {
            guiGraphics.fill(x, y, x + width, y + 1, color);
            guiGraphics.fill(x, y + height - 1, x + width, y + height, color);
            guiGraphics.fill(x, y, x + 1, y + height, color);
            guiGraphics.fill(x + width - 1, y, x + width, y + height, color);
        }
    }
}
