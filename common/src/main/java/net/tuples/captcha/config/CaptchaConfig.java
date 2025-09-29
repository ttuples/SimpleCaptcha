package net.tuples.captcha.config;


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class CaptchaConfig {
    public boolean soundEffects = true;
    public boolean creepySounds = true;

    public static final String CONFIG_FILE = "config/simple-captcha.json";

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static CaptchaConfig INSTANCE;

    public static CaptchaConfig getInstance() {
        if (INSTANCE == null) {
            INSTANCE = loadConfig(CONFIG_FILE, CaptchaConfig.class, new CaptchaConfig());
        }
        return INSTANCE;
    }

    public static void save() {
        if (INSTANCE != null) {
            saveConfig(CONFIG_FILE, INSTANCE);
        }
    }

    public static <T> T loadConfig(String fileName, Class<T> clazz, T defaultInstance) {
        try {
            java.nio.file.Path configPath = java.nio.file.Paths.get(fileName);
            if (java.nio.file.Files.exists(configPath)) {
                String json = new String(java.nio.file.Files.readAllBytes(configPath));
                return GSON.fromJson(json, clazz);
            } else {
                saveConfig(fileName, defaultInstance);
                return defaultInstance;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return defaultInstance;
        }
    }

    public static <T> void saveConfig(String fileName, T instance) {
        try {
            String json = GSON.toJson(instance);
            java.nio.file.Files.write(java.nio.file.Paths.get(fileName), json.getBytes());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
