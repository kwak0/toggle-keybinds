package dev.kwak0.toggle_keybinds;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.Selectable;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.ElementListWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.input.CharInput;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class ToggleableKeybindingWidget extends ElementListWidget<ToggleableKeybindingWidget.Entry> {

    private final AddBindingEntry addBindingEntry;
    private final ModMenuScreen screen;

    public ToggleableKeybindingWidget(ModMenuScreen screen, MinecraftClient client, int width, int height, int y, int itemHeight) {
        super(client, width, height, y, itemHeight);
        this.screen = screen;
        this.addBindingEntry = new AddBindingEntry();
        ToggleKeys.getSavedKeys().forEach(k -> addEntry(new BindingEntry(k)));
        addEntry(addBindingEntry);
        update();
    }

    public void update() {
        children().forEach(Entry::update);
    }

    private void addBinding() {
        removeEntryWithoutScrolling(addBindingEntry);
        ToggleKey key = screen.addKey();
        addEntry(new BindingEntry(key));
        addEntry(addBindingEntry);
    }

    @Override
    public int getRowWidth() {
        return 340;
    }

    public abstract static class Entry extends ElementListWidget.Entry<Entry> {
        protected abstract void update();
    }

    private class AddBindingEntry extends Entry {

        private final ButtonWidget addButton;

        public AddBindingEntry() {
            this.addButton = ButtonWidget
                    .builder(Text.translatable("toggle_keybinds.modmenuscreen.binding.add"), this::buttonAction)
                    .dimensions(0, 0, 40, 20)
                    .tooltip(Tooltip.of(Text.translatable("toggle_keybinds.modmenuscreen.binding.add.tooltip")))
                    .build();
        }

        private void buttonAction(ButtonWidget button) {
            addBinding();
        }

        @Override
        public List<? extends Element> children() {
            return List.of(this.addButton);
        }

        @Override
        public List<? extends Selectable> selectableChildren() {
            return List.of(this.addButton);
        }

        @Override
        public void render(DrawContext context, int mouseX, int mouseY, boolean hovered, float deltaTicks) {
            addButton.setPosition(getContentX() + (getRowWidth() - addButton.getWidth()) / 2, getContentY() + 3);
            addButton.render(context, mouseX, mouseY, deltaTicks);
        }

        @Override
        protected void update() {

        }
    }

    private class BindingEntry extends Entry {

        private final InventorySlotTextFieldWidget slot1TextField;
        private final InventorySlotTextFieldWidget slot2TextField;
        private final ButtonWidget editButton;
        private final ButtonWidget removeButton;

        private final ToggleKey key;

        public BindingEntry(ToggleKey key) {
            this.key = key;
            this.slot1TextField = new InventorySlotTextFieldWidget(key::getSlot1, key::setSlot1);
            this.slot2TextField = new InventorySlotTextFieldWidget(key::getSlot2, key::setSlot2);
            this.editButton = ButtonWidget.builder(
                    key.getKeyName(),
                    (button) -> {
                        screen.setSelectedKey(key);
                        update();
                    }).dimensions(0, 0, 75, 20).build();
            this.removeButton = ButtonWidget.builder(
                    Text.translatable("toggle_keybinds.modmenuscreen.binding.remove"),
                    button -> {
                        screen.removeKey(key);
                        removeEntry(this);
                        refreshScroll();
                        ToggleableKeybindingWidget.this.update();
                    }).dimensions(0, 0, 50, 20)
                    .build();
        }

        @Override
        public List<? extends Element> children() {
            return List.of(this.slot1TextField, this.slot2TextField, editButton, removeButton);
        }

        @Override
        public List<? extends Selectable> selectableChildren() {
            return List.of(this.slot1TextField, this.slot2TextField, editButton, removeButton);
        }

        @Override
        public void render(DrawContext context, int mouseX, int mouseY, boolean hovered, float deltaTicks) {
            context.drawTextWithShadow(client.textRenderer, Text.translatable("toggle_keybinds.modmenuscreen.binding.slot1"),
                    getContentX(), getContentY() + getContentHeight() / 2 - 4, -1);
            slot1TextField.setPosition(getContentX() + 40, getContentY() - 2);
            slot1TextField.render(context, mouseX, mouseY, deltaTicks);
            context.drawTextWithShadow(client.textRenderer, Text.translatable("toggle_keybinds.modmenuscreen.binding.slot2"),
                    slot1TextField.getX() + slot1TextField.getWidth() + 10, getContentY() + getContentHeight() / 2 - 4, -1);
            slot2TextField.setPosition(slot1TextField.getX() + slot1TextField.getWidth() + 50, getContentY() -2);
            slot2TextField.render(context, mouseX, mouseY, deltaTicks);
            removeButton.setPosition(getScrollbarX() - 10 - editButton.getWidth(), getContentY() - 2);
            removeButton.render(context,  mouseX, mouseY, deltaTicks);
            editButton.setPosition(removeButton.getX() - 5 - editButton.getWidth(), getContentY() -2);
            editButton.render(context,  mouseX, mouseY, deltaTicks);
        }

        @Override
        protected void update() {
            editButton.setMessage(key.getKeyName());

            boolean duplicate = false;
            if (key.isBound()) {
                for (ToggleKey otherKey : screen.getNewKeys()) {
                    if (key != otherKey && key.getKeyCode() == otherKey.getKeyCode()) {
                        duplicate = true;
                    }
                }
            }

            if (duplicate) {
                editButton.setMessage(Text.literal("[ ").append(editButton.getMessage().copy().formatted(Formatting.WHITE)).append(" ]").formatted(Formatting.RED));
                key.setDuplicate(true);
            } else {
                key.setDuplicate(false);
            }

            if (screen.getSelectedKey() == this.key) {
                this.editButton.setMessage(Text.literal("> ").append(this.editButton.getMessage().copy().formatted(
                        Formatting.WHITE, Formatting.UNDERLINE)).append(" <").formatted(Formatting.YELLOW));
            }
        }

        private class InventorySlotTextFieldWidget extends TextFieldWidget {

            public InventorySlotTextFieldWidget(Supplier<Integer> slotGetter, Consumer<Integer> slotSetter) {
                super(client.textRenderer, 20, 20, Text.empty());
                setChangedListener(s -> slotSetter.accept(s.isEmpty() ? -1 : Integer.parseInt(s) - 1));
                setText(key.isSlotValid(slotGetter.get()) ? Integer.toString(slotGetter.get() + 1) : "");
            }

            @Override
            public boolean charTyped(CharInput input) {
                return getCursor() == 0 && input.asString().matches("[1-9]") && super.charTyped(input);
            }
        }
    }
}