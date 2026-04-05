package dev.kwak0.toggle_keybinds.mixin.client;

import com.llamalad7.mixinextras.sugar.Local;
import dev.kwak0.toggle_keybinds.ToggleKey;
import dev.kwak0.toggle_keybinds.ToggleKeys;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.inventory.ContainerInput;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
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
    private void handleHotbarKeyPressed(CallbackInfoReturnable<Boolean> cir, @Local(argsOnly = true) KeyEvent event) {
        if (this.menu.getCarried().isEmpty() && this.hoveredSlot != null) {
            Optional<ToggleKey> toggleKey = ToggleKeys.getSavedKeys().stream().filter(k -> k.isValid() && k.getKeyCode() == event.input()).findFirst();
            toggleKey.ifPresent(key -> {
                this.slotClicked(this.hoveredSlot, this.hoveredSlot.index, key.getSlot1(), ContainerInput.SWAP);
                cir.setReturnValue(true);
            });
        }
    }
}
