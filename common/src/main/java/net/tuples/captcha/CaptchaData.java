package net.tuples.captcha;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Optional;
import java.util.stream.Collectors;

public class CaptchaData {
    public static JsonObject captchas = new JsonObject();

    private static final ResourceLocation jsonId = ResourceLocation.fromNamespaceAndPath(Captcha.MOD_ID, "captcha.json");

    private static Minecraft client;

    public static void initializeCaptchas(Minecraft newClient) {
        client = newClient;
        captchas = getJsonObject();
    }

    private static JsonObject getJsonObject() {
        try {
            ResourceManager resourceManager = client.getResourceManager();

            Optional<Resource> optionalResource = resourceManager.getResource(jsonId);

            if (optionalResource.isPresent()) {
                Resource resource = optionalResource.get();

                try (InputStream inputStream = resource.open();
                        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
                    String jsonContent = reader.lines().collect(Collectors.joining("\n"));
                    return JsonParser.parseString(jsonContent).getAsJsonObject();
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return null;
    }
}
