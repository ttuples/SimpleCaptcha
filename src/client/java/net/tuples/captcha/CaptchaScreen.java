package net.tuples.captcha;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.GridWidget;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.client.sound.SoundInstance;
import net.minecraft.client.sound.SoundManager;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.tuples.captcha.sounds.CaptchaSounds;

import java.util.*;

public class CaptchaScreen extends Screen {

    private GridWidget gridDisplay;
    private final Map<CaptchaImageButton, Boolean> buttonStates = new LinkedHashMap<>();
    private final int cellSize = 32;
    private final int cellSpacing = 5;
    private int gridSize;

    private Map<String, Boolean> captchaKey;
    private String captchaText;

    private final MinecraftClient client;
    private final SoundManager soundManager;
    private SoundInstance sound;

    public CaptchaScreen() {
        super(Text.literal("Captcha Screen"));
        client = MinecraftClient.getInstance();
        soundManager = client.getSoundManager();
        newCaptcha();
    }

    private void newCaptcha() {
        buttonStates.keySet().forEach(this::remove);
        buttonStates.clear();

        gridDisplay = new GridWidget().setSpacing(cellSpacing);

        // Get random captcha data
        List<String> keys = new ArrayList<>(CaptchaData.captchas.keySet());
        if (captchaText != null) {
            keys.remove(captchaText);
        }
        int rand = new Random().nextInt(keys.size());
        captchaText = keys.get(rand);
        captchaKey = CaptchaData.captchas.get(captchaText);

        boolean playSound = captchaKey.getOrDefault("sound", false);
        if (playSound) {
            if (client.world != null && client.player != null) {
                int randSound = new Random().nextInt(CaptchaSounds.whisper_sounds.size());
                if (soundManager.isPlaying(sound))
                    soundManager.stop(sound);
                sound = PositionedSoundInstance.master(CaptchaSounds.whisper_sounds.get(randSound), 1.0F, 0.3F);
                soundManager.play(sound);
            }
            captchaKey.remove("sound");
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
                    confirm.setMessage(Text.literal("Confirm"));
                } else {
                    confirm.setMessage(Text.literal("Skip"));
                }
            });
            buttonStates.put(button, false);
            gridDisplay.add(button, i / gridSize, i % gridSize);
        }
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
                    this.close();
                }
                else {
                    newCaptcha();
                }
            })
            .dimensions(0, 0, 80, 20)
            .tooltip(Tooltip.of(Text.literal(":)")))
            .build();

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

        context.drawCenteredTextWithShadow(textRenderer, Text.literal("Select all images that contain"), width / 2, (int)(height * 0.05F), 0xffffff);
        context.drawCenteredTextWithShadow(textRenderer, Text.literal(captchaText), width / 2, (int)(height * 0.1F), 0xffc321);
        context.drawCenteredTextWithShadow(textRenderer, Text.literal("If there is none click skip"), width / 2, (int)(height * 0.15F), 0xffffff);
    }

    private class CaptchaImageButton extends ButtonWidget {
        private static final int outlineMargin = 2;
        private static final int outlineColor = 0x80FFFFFF;
        private static final int selectedColor = 0xFFFFFFFF;
        private final Identifier image;

        protected CaptchaImageButton(int x, int y, int width, int height, Identifier image, PressAction onPress) {
            super(x, y, width, height, Text.empty(), onPress, (MutableText) -> Text.literal("Captcha Image Button"));
            this.image = image;
        }

        @Override
        protected void renderButton(DrawContext context, int mouseX, int mouseY, float delta) {
            context.drawTexture(image, this.getX(), this.getY(), 0, 0, this.getWidth(), this.getHeight(), this.getWidth(), this.getHeight());

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
