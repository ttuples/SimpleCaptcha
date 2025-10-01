package net.tuples.captcha.forge;

import dev.architectury.platform.forge.EventBuses;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

import net.tuples.captcha.Captcha;

@Mod(Captcha.MOD_ID)
public final class CaptchaForge {
    public CaptchaForge() {
        // Submit our event bus to let Architectury API register our content on the right time.
        EventBuses.registerModEventBus(Captcha.MOD_ID, FMLJavaModLoadingContext.get().getModEventBus());

        // Run our common setup.
        Captcha.init();
    }
}
