package net.tuples.captcha;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.GridWidget;
import net.minecraft.client.input.KeyCodes;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.client.sound.SoundInstance;
import net.minecraft.client.sound.SoundManager;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.tuples.captcha.sounds.CaptchaSounds;

import java.util.*;

public class CaptchaScreen extends Screen {

    // UI elements
    private GridWidget gridDisplay;
    private final Map<CaptchaImageButton, Boolean> buttonStates = new LinkedHashMap<>();
    private final int cellSize = 32;
    private final int cellSpacing = 5;
    private int gridSize;

    // Solving info
    private JsonObject captcha;     // JsonObject with captcha data
    private String captchaId;       // Current captcha id for tracking previous captchas
    private final Map<String, Boolean> captchaKey = new LinkedHashMap<>(); // Map of image paths and their answer key

    // Client and sounds
    private final MinecraftClient client;
    private SoundManager soundManager;
    private SoundInstance sound;

    public CaptchaScreen() {
        super(Text.translatable("captcha.narrator.title"));
        client = MinecraftClient.getInstance();
        soundManager = client.getSoundManager();
        if (Captcha.config.soundEffects)
            soundManager.play(PositionedSoundInstance.master(CaptchaSounds.sounds.get("blip1"), 1.0F, 0.6F));
        newCaptcha();
    }

    private final ButtonWidget confirm = ButtonWidget.builder(
            Text.literal("Skip"), button -> {
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
                    if (Captcha.config.soundEffects)
                        soundManager.play(PositionedSoundInstance.master(CaptchaSounds.sounds.get("button3"), 1.0F, 0.6F));
                    this.close();
                }
                else {
                    if (Captcha.config.soundEffects)
                        soundManager.play(PositionedSoundInstance.master(CaptchaSounds.sounds.get("button10"), 1.0F, 0.6F));
                    newCaptcha();
                }
            })
            .dimensions(0, 0, 80, 20)
            .tooltip(Tooltip.of(Text.literal(":)")))
            .build();

    private void newCaptcha() {
        // Reset elements
        confirm.setMessage(Text.translatable("captcha.ui.button.confirm"));
        buttonStates.keySet().forEach(this::remove);
        buttonStates.clear();
        gridDisplay = new GridWidget().setSpacing(cellSpacing);

        // Get random captcha data
        List<String> keys = new ArrayList<>(CaptchaData.captchas.keySet());

        // Remove previous captcha from possible selection
        if (captchaId != null) {
            keys.remove(captchaId);
        }

        int rand = new Random().nextInt(keys.size());
        captchaId = keys.get(rand);
        captcha = CaptchaData.captchas.get(captchaId).getAsJsonObject();

        // Update captcha key data for solving and image loading
        captchaKey.clear();
        for (Map.Entry<String, JsonElement> entry : captcha.get("images").getAsJsonObject().entrySet()) {
            captchaKey.put(entry.getKey(), entry.getValue().getAsBoolean());
        }

        // Play random spooky sound if on creep captcha
        if (captcha.get("creep").getAsBoolean()) {
            if (client.world != null && client.player != null) {
                int randSound = new Random().nextInt(CaptchaSounds.whisper_sounds.size());
                if (soundManager.isPlaying(sound))
                    soundManager.stop(sound);
                sound = PositionedSoundInstance.master(CaptchaSounds.whisper_sounds.get(randSound), 1.0F, 0.3F);
                soundManager.play(sound);
            }
        } else {
            if (soundManager.isPlaying(sound))
                soundManager.stop(sound);
        }

        initializeButtons();
        updateElements();
    }

    private void initializeButtons() {
        List<String> imagePaths = new ArrayList<>(captchaKey.keySet());
        gridSize = (int) Math.ceil(Math.sqrt(imagePaths.size()));

        for (int i = 0; i < imagePaths.size(); i++) {
            Identifier image = new Identifier(imagePaths.get(i));
            CaptchaImageButton button = new CaptchaImageButton(0, 0, cellSize, cellSize, image, b -> {
                buttonStates.put((CaptchaImageButton) b, !buttonStates.getOrDefault(b, false));
                if (buttonStates.containsValue(true)) {
                    confirm.setMessage(Text.translatable("captcha.ui.button.confirm"));
                } else {
                    confirm.setMessage(Text.translatable("captcha.ui.button.skip"));
                }
            });
            buttonStates.put(button, false);
            gridDisplay.add(button, i / gridSize, i % gridSize);
        }
    }

    @Override
    protected void init() {
        updateElements();
    }

    private void updateElements() {
        int gridWidth = (cellSize * gridSize) + (cellSpacing * (gridSize - 1));
        int gridY = (int)(height * 0.2F);

        gridDisplay.setPosition((width / 2) - (gridWidth / 2), gridY);
        gridDisplay.refreshPositions();

        buttonStates.keySet().forEach(button -> {
            if (!this.children().contains(button)) {
                addDrawableChild(button);
            }
        });

        confirm.setPosition((width / 2) - 40, gridY + gridWidth + 10);
        if (!this.children().contains(confirm)) {
            addDrawableChild(confirm);
        }
    }

    @Override
    public boolean shouldCloseOnEsc() {
        return false;
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        this.renderBackground(context);
        super.render(context, mouseX, mouseY, delta);

        context.drawCenteredTextWithShadow(textRenderer, Text.translatable("captcha.ui.instruction"), width / 2, (int)(height * 0.05F), 0xffffff);
        context.drawCenteredTextWithShadow(textRenderer, Text.translatable(captcha.get("text").getAsString()), width / 2, (int)(height * 0.1F), 0xffc321);
        context.drawCenteredTextWithShadow(textRenderer, Text.translatable("captcha.ui.skip"), width / 2, (int)(height * 0.15F), 0xffffff);
    }

    // Custom image button object
    private class CaptchaImageButton extends ButtonWidget {
        private static final int outlineMargin = 2;
        private static final int outlineColor = 0x80FFFFFF;
        private static final int selectedColor = 0xFFFFFFFF;
        private final Identifier image;

        protected CaptchaImageButton(int x, int y, int width, int height, Identifier image, PressAction onPress) {
            super(x, y, width, height, Text.empty(), onPress, (MutableText) -> Text.translatable("captcha.narrator.button"));
            this.image = image;
        }

        @Override
        protected void renderButton(DrawContext context, int mouseX, int mouseY, float delta) {
            context.drawTexture(image, this.getX(), this.getY(), 0, 0, this.getWidth(), this.getHeight(), this.getWidth(), this.getHeight());

            // Determine outline states
            if (buttonStates.getOrDefault(this, false)) {
                context.drawBorder(
                        this.getX() - outlineMargin,
                        this.getY() - outlineMargin,
                        this.getWidth() + (outlineMargin * 2),
                        this.getHeight() + (outlineMargin * 2),
                        outlineColor);
            } else if (this.isHovered()) {
                context.drawBorder(
                        this.getX() - outlineMargin,
                        this.getY() - outlineMargin,
                        this.getWidth() + (outlineMargin * 2),
                        this.getHeight() + (outlineMargin * 2),
                        selectedColor);
            }
        }
    }
}
