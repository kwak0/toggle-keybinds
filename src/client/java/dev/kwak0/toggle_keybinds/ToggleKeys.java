package dev.kwak0.toggle_keybinds;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import static com.mojang.text2speech.Narrator.LOGGER;
import static dev.kwak0.toggle_keybinds.ToggleKeybindsClient.CONFIG_PATH;

public class ToggleKeys {

    private static final Path CONFIG_FILE_PATH = CONFIG_PATH.resolve("keybinds.json");
    private static final File CONFIG_FILE = CONFIG_FILE_PATH.toFile();

    private static List<ToggleKey> savedKeys = new ArrayList<>();

    public static void loadKeys() {
        if (!Files.exists(CONFIG_FILE_PATH)) {
            return;
        }
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(ToggleKey.class,
                        (JsonDeserializer<ToggleKey>) (json, type, context) -> {
                            JsonObject obj = json.getAsJsonObject();
                            return new ToggleKey(obj.get("slot1").getAsInt(), obj.get("slot2").getAsInt(), obj.get("keyCode").getAsInt());
                        })
                .create();
        try {
            savedKeys = gson.fromJson(new FileReader(CONFIG_FILE), new TypeToken<List<ToggleKey>>() {}.getType());
        } catch (FileNotFoundException e) {
            LOGGER.error("Could not read config file {}", CONFIG_FILE_PATH, e);
        }
        for (ToggleKey key : savedKeys) {
            for (ToggleKey otherKey : savedKeys) {
                if (!key.equals(otherKey) && key.getKeyCode() == otherKey.getKeyCode()) {
                    key.setDuplicate(true);
                }
            }
        }
        LOGGER.info("Loaded keys: {}", savedKeys);
    }

    public static void saveKeys(List<ToggleKey> newKeys) {
        savedKeys = newKeys;

        ToggleKeybindsClient.createConfigFolder();
        try (PrintWriter writer = new PrintWriter(CONFIG_FILE)) {
            Gson gson = new GsonBuilder()
                    .setPrettyPrinting()
                    .registerTypeAdapter(ToggleKey.class,
                            (JsonSerializer<ToggleKey>) (toggleKey, type, jsonSerializationContext) -> {
                                JsonObject obj = new JsonObject();
                                obj.addProperty("keyCode", toggleKey.getKeyCode());
                                obj.addProperty("slot1", toggleKey.getSlot1());
                                obj.addProperty("slot2", toggleKey.getSlot2());
                                return obj;
                            })
                    .create();
            gson.toJson(savedKeys, writer);
        } catch (FileNotFoundException e) {
            LOGGER.error("Could not write toggle keybinds to {}", CONFIG_FILE_PATH, e);
        }
        LOGGER.info("Saved keys: {}", savedKeys);
    }

    public static List<ToggleKey> getSavedKeys() {
        return savedKeys;
    }

    public static void attemptToggle(ToggleKey.KeyType type, int keyCode, int action) {
        for (ToggleKey savedKey : savedKeys) {
            if (savedKey.getKeyType() == type && keyCode == savedKey.getKeyCode()) {
                savedKey.toggle(action);
            }
        }
    }
}
