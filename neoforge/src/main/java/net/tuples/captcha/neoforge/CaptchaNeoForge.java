package net.tuples.captcha.neoforge;

import net.neoforged.fml.common.Mod;

import net.tuples.captcha.Captcha;

@Mod(Captcha.MOD_ID)
public final class CaptchaNeoForge {
    public CaptchaNeoForge() {
        // Run our common setup.
        Captcha.init();
    }
}
