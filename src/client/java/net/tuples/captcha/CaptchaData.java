package net.tuples.captcha;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.minecraft.client.MinecraftClient;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.stream.Collectors;

public class CaptchaData {

    public static JsonObject captchas = new JsonObject();

    private static final Identifier jsonId = new Identifier(Captcha.MOD_ID, "captcha.json");

    private static MinecraftClient client;

    public static void initializeCaptchas(MinecraftClient newClient) {
        client = newClient;
        captchas = getJsonObject();
    }

    private static JsonObject getJsonObject() {
        try {
            ResourceManager resourceManager = client.getResourceManager();
            InputStream inputStream = resourceManager.getResource(jsonId).get().getInputStream();
            String jsonContent = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8)).lines().collect(Collectors.joining("\n"));
            return JsonParser.parseString(jsonContent).getAsJsonObject();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

//    private static void parseJsonData() {
//        try {
//            JsonObject json = getJsonObject();
//
//            for (Map.Entry<String, JsonElement> entry : json.entrySet()) {
//                String id = entry.getKey();
//                JsonObject data = entry.getValue().getAsJsonObject();
//
//                String name = data.get("text").getAsString();
//                boolean creep = data.get("creep").getAsBoolean();
//
//                // Skip adding creepy captchas if disabled in config
//                if (creep && !Captcha.config.enableCreepyCaptchas)
//                    continue;
//
//                Captcha.LOGGER.info("Loading captcha {}", id);
//
//                captchas.add(name, new JsonObject());
//
//                JsonObject imageData = data.get("images").getAsJsonObject();
//
//                for (Map.Entry<String, JsonElement> data_entry : imageData.entrySet()) {
//                    String path = data_entry.getKey();
//                    Boolean dataValue = data_entry.getValue().getAsBoolean();
//                    captchas.
//                }
//            }
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }
//    }
}
