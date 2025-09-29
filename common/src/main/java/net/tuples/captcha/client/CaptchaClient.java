package net.tuples.captcha.client;

import dev.architectury.networking.NetworkManager;
import net.minecraft.client.Minecraft;
import net.tuples.captcha.Captcha;
import net.tuples.captcha.client.ui.CaptchaScreen;
import net.tuples.captcha.config.CaptchaData;

public class CaptchaClient {
    public static void init(Minecraft client) {
        CaptchaData.initializeCaptchas(client);

        NetworkManager.registerReceiver(
            NetworkManager.Side.S2C,
            Captcha.OPEN_CAPTCHA,
            (payload, context) -> {
                context.queue(() -> {
                    Minecraft.getInstance().setScreen(new CaptchaScreen());
                });
            }
        );
    }
}
