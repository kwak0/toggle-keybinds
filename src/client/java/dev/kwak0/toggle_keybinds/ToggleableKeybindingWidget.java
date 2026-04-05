package dev.kwak0.toggle_keybinds;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.ContainerObjectSelectionList;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.input.CharacterEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.ChatFormatting;
import org.jspecify.annotations.NonNull;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class ToggleableKeybindingWidget extends ContainerObjectSelectionList<ToggleableKeybindingWidget.Entry> {

    private final AddBindingEntry addBindingEntry;
    private final ModMenuScreen screen;

    public ToggleableKeybindingWidget(ModMenuScreen screen, Minecraft client, int width, int height, int y, int itemHeight) {
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
        removeEntryFromTop(addBindingEntry);
        ToggleKey key = screen.addKey();
        addEntry(new BindingEntry(key));
        addEntry(addBindingEntry);
    }

    @Override
    public int getRowWidth() {
        return 340;
    }

    public abstract static class Entry extends ContainerObjectSelectionList.Entry<Entry> {
        protected abstract void update();
    }

    private class AddBindingEntry extends Entry {

        private final Button addButton;

        public AddBindingEntry() {
            this.addButton = Button
                    .builder(Component.translatable("toggle_keybinds.modmenuscreen.binding.add"), this::buttonAction)
                    .bounds(0, 0, 40, 20)
                    .tooltip(Tooltip.create(Component.translatable("toggle_keybinds.modmenuscreen.binding.add.tooltip")))
                    .build();
        }

        private void buttonAction(Button button) {
            addBinding();
        }

        @Override
        public @NonNull List<? extends GuiEventListener> children() {
            return List.of(this.addButton);
        }

        @Override
        public @NonNull List<? extends NarratableEntry> narratables() {
            return List.of(this.addButton);
        }

        @Override
        public void extractContent(@NonNull GuiGraphicsExtractor context, int mouseX, int mouseY, boolean hovered, float deltaTicks) {
            addButton.setPosition(getContentX() + (getRowWidth() - addButton.getWidth()) / 2, getContentY() + 3);
            addButton.extractRenderState(context, mouseX, mouseY, deltaTicks);
        }

        @Override
        protected void update() {

        }
    }

    private class BindingEntry extends Entry {

        private final InventorySlotTextFieldWidget slot1TextField;
        private final InventorySlotTextFieldWidget slot2TextField;
        private final Button editButton;
        private final Button removeButton;

        private final ToggleKey key;

        public BindingEntry(ToggleKey key) {
            this.key = key;
            this.slot1TextField = new InventorySlotTextFieldWidget(key::getSlot1, key::setSlot1);
            this.slot2TextField = new InventorySlotTextFieldWidget(key::getSlot2, key::setSlot2);
            this.editButton = Button.builder(
                    key.getKeyName(),
                    (button) -> {
                        screen.setSelectedKey(key);
                        update();
                    }).bounds(0, 0, 75, 20).build();
            this.removeButton = Button.builder(
                    Component.translatable("toggle_keybinds.modmenuscreen.binding.remove"),
                    button -> {
                        screen.removeKey(key);
                        removeEntry(this);
                        refreshScrollAmount();
                        ToggleableKeybindingWidget.this.update();
                    }).bounds(0, 0, 50, 20)
                    .build();
        }

        @Override
        public @NonNull List<? extends GuiEventListener> children() {
            return List.of(this.slot1TextField, this.slot2TextField, editButton, removeButton);
        }

        @Override
        public @NonNull List<? extends NarratableEntry> narratables() {
            return List.of(this.slot1TextField, this.slot2TextField, editButton, removeButton);
        }

        @Override
        public void extractContent(GuiGraphicsExtractor context, int mouseX, int mouseY, boolean hovered, float deltaTicks) {
            context.text(minecraft.font, Component.translatable("toggle_keybinds.modmenuscreen.binding.slot1"),
                    getContentX(), getContentY() + getContentHeight() / 2 - 4, -1);
            slot1TextField.setPosition(getContentX() + 40, getContentY() - 2);
            slot1TextField.extractRenderState(context, mouseX, mouseY, deltaTicks);
            context.text(minecraft.font, Component.translatable("toggle_keybinds.modmenuscreen.binding.slot2"),
                    slot1TextField.getX() + slot1TextField.getWidth() + 10, getContentY() + getContentHeight() / 2 - 4, -1);
            slot2TextField.setPosition(slot1TextField.getX() + slot1TextField.getWidth() + 50, getContentY() -2);
            slot2TextField.extractRenderState(context, mouseX, mouseY, deltaTicks);
            removeButton.setPosition(scrollBarX() - 10 - editButton.getWidth(), getContentY() - 2);
            removeButton.extractRenderState(context,  mouseX, mouseY, deltaTicks);
            editButton.setPosition(removeButton.getX() - 5 - editButton.getWidth(), getContentY() -2);
            editButton.extractRenderState(context,  mouseX, mouseY, deltaTicks);
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
                editButton.setMessage(Component.literal("[ ").append(editButton.getMessage().copy().withStyle(
                        ChatFormatting.WHITE)).append(" ]").withStyle(ChatFormatting.RED));
                key.setDuplicate(true);
            } else {
                key.setDuplicate(false);
            }

            if (screen.getSelectedKey() == this.key) {
                this.editButton.setMessage(Component.literal("> ").append(this.editButton.getMessage().copy().withStyle(
                        ChatFormatting.WHITE, ChatFormatting.UNDERLINE)).append(" <").withStyle(ChatFormatting.YELLOW));
            }
        }

        private class InventorySlotTextFieldWidget extends EditBox {

            public InventorySlotTextFieldWidget(Supplier<Integer> slotGetter, Consumer<Integer> slotSetter) {
                super(minecraft.font, 20, 20, Component.empty());
                setResponder(s -> slotSetter.accept(s.isEmpty() ? -1 : Integer.parseInt(s) - 1));
                setValue(key.isSlotValid(slotGetter.get()) ? Integer.toString(slotGetter.get() + 1) : "");
            }

            @Override
            public boolean charTyped(@NonNull CharacterEvent input) {
                return getCursorPosition() == 0 && input.codepointAsString().matches("[1-9]") && super.charTyped(input);
            }
        }
    }
}