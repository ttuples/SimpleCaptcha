package net.tuples.captcha;

import com.google.gson.JsonElement;
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
import java.util.*;
import java.util.stream.Collectors;

public class CaptchaData {

    public static final Map<String, Map<String, Boolean>> captchas = new LinkedHashMap<>();

    public static void initializeCaptchas(MinecraftClient client) throws IOException {
        Identifier jsonId = new Identifier(Captcha.MOD_ID, "captcha.json");
        ResourceManager resourceManager = client.getResourceManager();
        InputStream inputStream = resourceManager.getResource(jsonId).get().getInputStream();
        String jsonContent = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8)).lines().collect(Collectors.joining("\n"));
        JsonObject json = JsonParser.parseString(jsonContent).getAsJsonObject();

        for (Map.Entry<String, JsonElement> entry : json.entrySet()) {
            String message = entry.getKey();
            JsonObject images = entry.getValue().getAsJsonObject();

            Map<String, Boolean> data = new LinkedHashMap<>();
            for (Map.Entry<String, JsonElement> data_entry : images.entrySet()) {
                String path = data_entry.getKey();
                boolean value = data_entry.getValue().getAsBoolean();
                data.put(path, value);
            }

            captchas.put(message, data);
        }
    }
}
