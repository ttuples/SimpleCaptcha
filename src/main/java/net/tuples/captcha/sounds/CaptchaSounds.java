package net.tuples.captcha.sounds;

import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;
import net.tuples.captcha.Captcha;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CaptchaSounds {
    public static final Map<String,SoundEvent> sounds = new HashMap<>();
    public static final List<SoundEvent> whisper_sounds = new ArrayList<>();

    public static void initializeSounds() {
        Captcha.LOGGER.info("initializing sounds for mod " + Captcha.MOD_ID);

        registerSound("blip1");
        registerSound("button3");
        registerSound("button10");
        registerWhisperSound("whisper1");
        registerWhisperSound("whisper2");
        registerWhisperSound("whisper3");
    }

    private static void registerSound(String name) {
        Identifier id = new Identifier(Captcha.MOD_ID, name);
        SoundEvent sound = SoundEvent.of(id);
        sounds.put(name, sound);
        Registry.register(Registries.SOUND_EVENT, id, sound);
    }

    private static void registerWhisperSound(String name) {
        Identifier id = new Identifier(Captcha.MOD_ID, name);
        SoundEvent sound = SoundEvent.of(id);
        sounds.put(name, sound);
        whisper_sounds.add(sound);
        Registry.register(Registries.SOUND_EVENT, id, sound);
    }
}
