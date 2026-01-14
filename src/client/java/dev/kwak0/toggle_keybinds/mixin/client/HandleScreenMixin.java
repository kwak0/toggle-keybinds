package dev.kwak0.toggle_keybinds.mixin.client;

import com.llamalad7.mixinextras.sugar.Local;
import dev.kwak0.toggle_keybinds.ToggleKey;
import dev.kwak0.toggle_keybinds.ToggleKeys;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.input.KeyInput;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Optional;

@Mixin(HandledScreen.class)
public abstract class HandleScreenMixin<T extends ScreenHandler> {

    @Shadow
    protected Slot focusedSlot;
    @Final @Shadow
    protected T handler;
    @Shadow
    protected abstract void onMouseClick(Slot slot, int slotId, int button, SlotActionType actionType);

    @Inject(method = "handleHotbarKeyPressed", at = @At("TAIL"), cancellable = true)
    private void handleHotbarKeyPressed(CallbackInfoReturnable<Boolean> cir, @Local(argsOnly = true) KeyInput keyInput) {
        if (this.handler.getCursorStack().isEmpty() && this.focusedSlot != null) {
            Optional<ToggleKey> toggleKey = ToggleKeys.getSavedKeys().stream().filter(k -> k.isValid() && k.getKeyCode() == keyInput.getKeycode()).findFirst();
            toggleKey.ifPresent(key -> {
                this.onMouseClick(this.focusedSlot, this.focusedSlot.id, key.getSlot1(), SlotActionType.SWAP);
                cir.setReturnValue(true);
            });
        }
    }
}
