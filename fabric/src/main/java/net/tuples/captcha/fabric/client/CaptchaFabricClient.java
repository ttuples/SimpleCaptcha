package net.tuples.captcha.fabric.client;

import dev.architectury.event.events.client.ClientLifecycleEvent;
import net.fabricmc.api.ClientModInitializer;
import net.tuples.captcha.CaptchaClient;

public final class CaptchaFabricClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        ClientLifecycleEvent.CLIENT_STARTED.register(CaptchaClient::init);
    }
}
