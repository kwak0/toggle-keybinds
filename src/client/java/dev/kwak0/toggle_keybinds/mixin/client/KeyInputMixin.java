package dev.kwak0.toggle_keybinds.mixin.client;

import dev.kwak0.toggle_keybinds.ToggleKey;
import dev.kwak0.toggle_keybinds.ToggleKeys;
import net.minecraft.client.Keyboard;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Keyboard.class)
public class KeyInputMixin {

    @Inject(method = "onKey", at = @At("HEAD"))
    private void onKey(long window, int keyCode, int scancode, int action, int modifiers, CallbackInfo ci) {
        ToggleKeys.attemptToggle(ToggleKey.KeyType.KEY, keyCode, action);
    }
}
