package net.tuples.captcha;

import dev.architectury.event.events.client.ClientLifecycleEvent;
import net.minecraft.resources.ResourceLocation;
import net.tuples.captcha.client.CaptchaClient;
import net.tuples.captcha.client.CaptchaSounds;
import net.tuples.captcha.commands.CaptchaCommands;
import net.tuples.captcha.config.CaptchaConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class Captcha {
    public static final String MOD_ID = "captcha";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    public static final ResourceLocation OPEN_CAPTCHA = new ResourceLocation(Captcha.MOD_ID, "open_captcha");

    public static void init() {
        LOGGER.info("Initializing Simple Captcha");

        CaptchaConfig.getInstance();
        CaptchaSounds.initialize();
        CaptchaCommands.initialize();

        ClientLifecycleEvent.CLIENT_STARTED.register(CaptchaClient::init);
    }
}
