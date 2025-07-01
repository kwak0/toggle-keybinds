package dev.kwak0.toggle_keybinds;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.InputUtil;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;

public class ToggleKey {
    private InputUtil.Key key;
    private int slot1;
    private int slot2;
    private boolean keyCooldown = false;
    private boolean duplicate;

    private ToggleKey(int slot1, int slot2, InputUtil.Key key) {
        this.slot1 = slot1;
        this.slot2 = slot2;
        this.key = key;
        this.duplicate = false;
    }

    public ToggleKey(int slot1, int slot2, int keyCode) {
        this(slot1, slot2, InputUtil.UNKNOWN_KEY);
        // Creating mouse keys with InputUtil.fromKeyCode() causes getLocalisedName() to not give the right name.
        if (getKeyType() == KeyType.MOUSE) {
            key = InputUtil.Type.MOUSE.createFromCode(keyCode);
        } else if (getKeyType() == KeyType.KEY) {
            key = InputUtil.fromKeyCode(keyCode, -1);
        }
    }

    public ToggleKey() {
        this(-1, -1, InputUtil.UNKNOWN_KEY);
    }

    public void toggle(int action) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (action == GLFW.GLFW_PRESS && isActive(client)  && client.player != null) {
            PlayerInventory inventory = client.player.getInventory();
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

    public void setKey(InputUtil.Key key) {
        this.key = key;
    }

    public void setDuplicate(boolean duplicate) {
        this.duplicate = duplicate;
    }

    public int getKeyCode() {
        return key.getCode();
    }

    public Text getKeyName() {
        return isBound() ? key.getLocalizedText() : Text.translatable("toggle_keybinds.key.not_bound");
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
        return key != InputUtil.UNKNOWN_KEY;
    }

    private boolean isActive(MinecraftClient client) {
        return isValid() && !keyCooldown && client.currentScreen == null;
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
