package dev.kwak0.toggle_keybinds.mixin.client;

import dev.kwak0.toggle_keybinds.ToggleKeybindsClient;
import net.minecraft.client.resource.language.LanguageManager;
import net.minecraft.resource.ResourceManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LanguageManager.class)
public class LanguageLoadedMixin {
    @Inject(method = "reload", at = @At("TAIL"))
    void reload(ResourceManager manager, CallbackInfo ci) {
        ToggleKeybindsClient.generateReadMe();
    }
}
