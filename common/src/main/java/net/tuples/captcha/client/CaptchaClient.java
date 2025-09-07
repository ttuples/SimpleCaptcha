package net.tuples.captcha.client;

import dev.architectury.networking.NetworkManager;
import net.minecraft.client.Minecraft;
import net.tuples.captcha.config.CaptchaData;
import net.tuples.captcha.commands.OpenCaptchaS2CPayload;

public class CaptchaClient {
    public static void init(Minecraft client) {
        CaptchaData.initializeCaptchas(client);

        NetworkManager.registerReceiver(
            NetworkManager.Side.S2C,
            OpenCaptchaS2CPayload.TYPE,
            OpenCaptchaS2CPayload.CODEC,
            (payload, context) -> {
                context.queue(() -> {
                    Minecraft.getInstance().setScreen(new CaptchaScreen());
                });
            }
        );
    }
}
