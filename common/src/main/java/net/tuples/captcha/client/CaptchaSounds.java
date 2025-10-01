package net.tuples.captcha.client;

import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.tuples.captcha.Captcha;

import java.util.*;

public class CaptchaSounds {
    public static final DeferredRegister<SoundEvent> SOUNDS =
            DeferredRegister.create(Captcha.MOD_ID, Registries.SOUND_EVENT);

    private static final Map<String, RegistrySupplier<SoundEvent>> SOUND_MAP = new HashMap<>();
    private static final List<RegistrySupplier<SoundEvent>> WHISPERS = new ArrayList<>();
    private static final Random RANDOM = new Random();

    public static void initialize() {
        Captcha.LOGGER.info("Registering sounds for {}", Captcha.MOD_ID);

        // General sounds
        register("blip1");
        register("button3");
        register("button10");

        // Whisper sounds
        registerWhisper("whisper1");
        registerWhisper("whisper2");
        registerWhisper("whisper3");

        SOUNDS.register();
    }

    private static RegistrySupplier<SoundEvent> register(String name) {
        ResourceLocation id = new ResourceLocation(Captcha.MOD_ID, name);
        RegistrySupplier<SoundEvent> sound = SOUNDS.register(name,
                () -> SoundEvent.createVariableRangeEvent(id));
        SOUND_MAP.put(name, sound);
        return sound;
    }

    private static void registerWhisper(String name) {
        WHISPERS.add(register(name));
    }

    public static SoundEvent get(String name) {
        return SOUND_MAP.get(name).get();
    }

    public static SoundEvent randomWhisper() {
        return WHISPERS.get(RANDOM.nextInt(WHISPERS.size())).get();
    }
}