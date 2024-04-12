package net.tuples.captcha;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;

import java.io.IOException;

public class CaptchaClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
        ClientLifecycleEvents.CLIENT_STARTED.register(client -> {
            try {
                CaptchaData.initializeCaptchas(client);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });

        ClientPlayNetworking.registerGlobalReceiver(Captcha.open_captcha, ((client, handler, buf, responseSender) -> client.execute(()-> client.setScreen(new CaptchaScreen()))));
	}
}