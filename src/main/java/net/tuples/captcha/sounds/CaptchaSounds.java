package net.tuples.captcha.sounds;

import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;
import net.tuples.captcha.Captcha;

import java.util.ArrayList;
import java.util.List;

public class CaptchaSounds {
    public static final Identifier button3_ID = new Identifier(Captcha.MOD_ID, "button3");
    public static final SoundEvent button3 = SoundEvent.of(button3_ID);

    public static final Identifier whisper1_ID = new Identifier(Captcha.MOD_ID, "whisper1");
    public static final SoundEvent whisper1 = SoundEvent.of(whisper1_ID);

    public static final Identifier whisper2_ID = new Identifier(Captcha.MOD_ID, "whisper2");
    public static final SoundEvent whisper2 = SoundEvent.of(whisper1_ID);

    public static final Identifier whisper3_ID = new Identifier(Captcha.MOD_ID, "whisper3");
    public static final SoundEvent whisper3 = SoundEvent.of(whisper1_ID);

    public static List<SoundEvent> whisper_sounds = new ArrayList<>();

    public static void initializeSounds() {
        Captcha.LOGGER.info("initializing sounds for mod " + Captcha.MOD_ID);
        Registry.register(Registries.SOUND_EVENT, button3_ID, button3);
        Registry.register(Registries.SOUND_EVENT, whisper1_ID, whisper1);
        Registry.register(Registries.SOUND_EVENT, whisper2_ID, whisper2);
        Registry.register(Registries.SOUND_EVENT, whisper3_ID, whisper3);

        whisper_sounds.add(whisper1);
        whisper_sounds.add(whisper2);
        whisper_sounds.add(whisper3);
    }
}
