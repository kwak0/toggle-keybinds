package dev.kwak0.toggle_keybinds;

import net.minecraft.client.Minecraft;
import net.minecraft.client.input.KeyEvent;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.network.chat.Component;
import org.lwjgl.glfw.GLFW;

public class ToggleKey {
    private InputConstants.Key key;
    private int slot1;
    private int slot2;
    private boolean keyCooldown = false;
    private boolean duplicate;

    private ToggleKey(int slot1, int slot2, InputConstants.Key key) {
        this.slot1 = slot1;
        this.slot2 = slot2;
        this.key = key;
        this.duplicate = false;
    }

    public ToggleKey(int slot1, int slot2, int keyCode) {
        this.slot1 = slot1;
        this.slot2 = slot2;
        this.duplicate = false;
        if (keyCode == -1) {
            this.key = InputConstants.UNKNOWN;
            return;
        }
        this.key = InputConstants.getKey(new KeyEvent(keyCode, -1, -1));
        // Creating mouse keys with InputConstants.getKey() causes getLocalisedName() to not return the right name.
        if (getKeyType() == KeyType.MOUSE) {
            this.key = InputConstants.Type.MOUSE.getOrCreate(keyCode);
        }
    }

    public ToggleKey() {
        this(-1, -1, InputConstants.UNKNOWN);
    }

    public void toggle(int action) {
        Minecraft client = Minecraft.getInstance();
        if (action == GLFW.GLFW_PRESS && isActive(client)  && client.player != null) {
            Inventory inventory = client.player.getInventory();
            if (inventory.getSelectedSlot() != slot1) {
                inventory.setSelectedSlot(slot1);
            } else {
                inventory.setSelectedSlot(slot2);
            }
            keyCooldown = true;
        } else {
            keyCooldown = false;
        }
    }

    public void setSlot1(int slot1) {
        this.slot1 = slot1;
    }

    public void setSlot2(int slot2) {
        this.slot2 = slot2;
    }

    public void setKey(InputConstants.Key key) {
        this.key = key;
    }

    public void setDuplicate(boolean duplicate) {
        this.duplicate = duplicate;
    }

    public int getKeyCode() {
        return key.getValue();
    }

    public Component getKeyName() {
        return isBound() ? key.getDisplayName() : Component.translatable("toggle_keybinds.key.not_bound");
    }

    public int getSlot1() {
        return slot1;
    }

    public int getSlot2() {
        return slot2;
    }

    public boolean isSlotValid(int slot) {
        return slot >= 0 && slot <= 8;
    }

    public boolean isValid() {
        return isSlotValid(slot1) && isSlotValid(slot2) && isBound() && !duplicate;
    }

    public boolean isBound() {
        return key != InputConstants.UNKNOWN;
    }

    private boolean isActive(Minecraft client) {
        return isValid() && !keyCooldown && client.screen == null;
    }

    public KeyType getKeyType() {
        KeyType type;
        if (isBound()) {
            type = getKeyCode() <= 7 ? KeyType.MOUSE : KeyType.KEY;
        } else {
            type = KeyType.UNBOUND;
        }
        return type;
    }

    @Override
    public String toString() {
        return "ToggleKey{" +
                "key=" + getKeyName().getString() +
                ", slot1=" + slot1 +
                ", slot2=" + slot2 +
                '}';
    }

    public enum KeyType {
        KEY, MOUSE, UNBOUND
    }
}
