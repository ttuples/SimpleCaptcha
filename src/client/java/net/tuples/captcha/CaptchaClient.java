package net.tuples.captcha;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;

public class CaptchaClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
        ClientLifecycleEvents.CLIENT_STARTED.register(CaptchaData::initializeCaptchas);

        ClientPlayNetworking.registerGlobalReceiver(OpenCaptchaS2CPayload.ID, ((payload, context) -> context.client().execute(()-> context.client().setScreen(new CaptchaScreen()))));
	}
}