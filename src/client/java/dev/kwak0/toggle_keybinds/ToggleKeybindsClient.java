package dev.kwak0.toggle_keybinds;

import com.mojang.logging.LogUtils;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.text.Text;
import org.slf4j.Logger;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;

public class ToggleKeybindsClient implements ClientModInitializer {

	public static String MOD_ID = "toggle_keybinds";
	public static final Path CONFIG_PATH = FabricLoader.getInstance().getConfigDir().resolve(ToggleKeybindsClient.MOD_ID);
	public static final Logger LOGGER = LogUtils.getLogger();

	@Override
	public void onInitializeClient() {
		// This runs after GLFW is initialized
		ClientLifecycleEvents.CLIENT_STARTED.register(client -> ToggleKeys.loadKeys());

		generateReadMe();
	}

	private void generateReadMe() {
		Path path = CONFIG_PATH.resolve("README.txt");
		if (Files.exists(path)) {
			return;
		}
		createConfigFolder();
		try (PrintWriter writer = new PrintWriter(path.toFile())) {
			writer.println(Text.translatable("toggle_keybinds.readme"));
		} catch (FileNotFoundException e) {
            LOGGER.error("Could not create README.txt");
        }
    }

	public static void createConfigFolder() {
		if (!Files.exists(CONFIG_PATH) && !CONFIG_PATH.toFile().mkdirs()) {
			LOGGER.error("Could not create config folder");
		}
	}
}