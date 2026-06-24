package dev.kwak0.toggle_keybinds.mixin.client;

import com.llamalad7.mixinextras.sugar.Local;
import dev.kwak0.toggle_keybinds.ToggleKey;
import dev.kwak0.toggle_keybinds.ToggleKeys;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.input.InputWithModifiers;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.inventory.ContainerInput;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Optional;

@Mixin(AbstractContainerScreen.class)
public abstract class HandleScreenMixin<T extends AbstractContainerMenu> {

    @Shadow
    protected Slot hoveredSlot;
    @Final @Shadow
    protected T menu;
    @Shadow
    protected abstract void slotClicked(Slot slot, int slotId, int buttonNum, ContainerInput containerInput);

    @Inject(method = "checkHotbarKeyPressed", at = @At("TAIL"), cancellable = true)
    private void handleHotbarKeyPressed(CallbackInfoReturnable<Boolean> cir, @Local(argsOnly = true, name = "event") KeyEvent event) {
        checkHotbar(event, cir);
    }

    @Inject(method = "checkHotbarMouseClicked", at = @At("TAIL"))
    private void checkHotbarMouseClicked(CallbackInfo ci, @Local(argsOnly = true, name = "event") MouseButtonEvent event) {
        checkHotbar(event, null);
    }

    @Unique
    private void checkHotbar(InputWithModifiers event, CallbackInfoReturnable<Boolean> cir) {
        if (this.menu.getCarried().isEmpty() && this.hoveredSlot != null) {
            Optional<ToggleKey> toggleKey = ToggleKeys.getSavedKeys().stream().filter(k -> k.isValid() && k.getKeyCode() == event.input()).findFirst();
            toggleKey.ifPresent(key -> {
                this.slotClicked(
                        this.hoveredSlot, this.hoveredSlot.index,
                        hoveredSlot.index - 36 != key.getSlot1() ? key.getSlot1() : key.getSlot2(), //hoveredSlot.index 36-44 correspond to hotbar slots 0-8
                        ContainerInput.SWAP);
                if (cir != null) {
                    cir.setReturnValue(true);
                }
            });
        }
    }
}
