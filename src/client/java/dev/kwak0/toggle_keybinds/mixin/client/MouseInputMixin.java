package dev.kwak0.toggle_keybinds.mixin.client;

import dev.kwak0.toggle_keybinds.ToggleKey;
import dev.kwak0.toggle_keybinds.ToggleKeys;
import net.minecraft.client.Mouse;
import net.minecraft.client.input.MouseInput;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Mouse.class)
public class MouseInputMixin {

    @Inject(method = "onMouseButton", at = @At("HEAD"))
    private void onMouseButton(long window, MouseInput input, int action, CallbackInfo ci) {
        ToggleKeys.attemptToggle(ToggleKey.KeyType.MOUSE, input.getKeycode(), action);
    }
}
