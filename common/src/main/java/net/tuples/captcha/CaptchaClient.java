package net.tuples.captcha;

import dev.architectury.networking.NetworkManager;
import net.minecraft.client.Minecraft;

public class CaptchaClient {
    public static void init(Minecraft client) {
        CaptchaData.initializeCaptchas(client);

        NetworkManager.registerReceiver(
            NetworkManager.Side.S2C,
            OpenCaptchaS2CPayload.TYPE,
            OpenCaptchaS2CPayload.CODEC,
            (payload, context) -> {
                context.queue(() -> client.setScreen(new CaptchaScreen()));
            }
        );
    }
}
