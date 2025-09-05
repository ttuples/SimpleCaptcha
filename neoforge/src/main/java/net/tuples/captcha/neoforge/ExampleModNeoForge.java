package net.tuples.captcha.neoforge;

import net.neoforged.fml.common.Mod;

import net.tuples.captcha.ExampleMod;

@Mod(ExampleMod.MOD_ID)
public final class ExampleModNeoForge {
    public ExampleModNeoForge() {
        // Run our common setup.
        ExampleMod.init();
    }
}
