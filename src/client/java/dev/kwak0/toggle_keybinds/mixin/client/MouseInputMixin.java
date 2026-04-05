package dev.kwak0.toggle_keybinds.mixin.client;

import dev.kwak0.toggle_keybinds.ToggleKey;
import dev.kwak0.toggle_keybinds.ToggleKeys;
import net.minecraft.client.MouseHandler;
import net.minecraft.client.input.MouseButtonInfo;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MouseHandler.class)
public class MouseInputMixin {

    @Inject(method = "onButton", at = @At("HEAD"))
    private void onMouseButton(long handle, MouseButtonInfo rawButtonInfo, int action, CallbackInfo ci) {
        ToggleKeys.attemptToggle(ToggleKey.KeyType.MOUSE, rawButtonInfo.input(), action);
    }
}
