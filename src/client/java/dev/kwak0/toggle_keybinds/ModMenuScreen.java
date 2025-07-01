package dev.kwak0.toggle_keybinds;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.*;
import net.minecraft.client.util.InputUtil;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.List;

public class ModMenuScreen extends Screen {

    private final Screen parent;
    private ThreePartsLayoutWidget layout;
    private ToggleableKeybindingWidget list;
    private ToggleKey selectedKeyBinding;

    private final List<ToggleKey> newKeys;

    protected ModMenuScreen(Screen parent) {
        super(Text.translatable("toggle_keybinds.modmenuscreen.title"));
        this.parent = parent;
        this.newKeys = new ArrayList<>(ToggleKeys.getSavedKeys());
    }

    public ToggleKey getSelectedKey() {
        return selectedKeyBinding;
    }

    public void setSelectedKey(ToggleKey key) {
        selectedKeyBinding = key;
    }

    public List<ToggleKey> getNewKeys() {
        return newKeys;
    }

    @Override
    public void close() {
        assert this.client != null;
        ToggleKeys.saveKeys(newKeys);
        this.client.setScreen(this.parent);
    }

    public void cancel() {
        assert this.client != null;
        this.client.setScreen(this.parent);
    }

    public void saveKeys() {
        close();
    }

    public ToggleKey addKey() {
        ToggleKey key = new ToggleKey();
        newKeys.add(key);
        return key;
    }

    public void removeKey(ToggleKey key) {
        newKeys.remove(key);
    }


    //This needs to be overridden for resizing the window to work properly
    @Override
    protected void refreshWidgetPositions() {
        this.layout.refreshPositions();
        if (this.list != null) {
            this.list.position(this.width, this.layout);
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (this.selectedKeyBinding != null) {
            this.selectedKeyBinding.setKey(InputUtil.Type.MOUSE.createFromCode(button));
            this.selectedKeyBinding = null;
            this.list.update();
            return true;
        } else {
            return super.mouseClicked(mouseX, mouseY, button);
        }
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (selectedKeyBinding != null) {
            if (keyCode == GLFW.GLFW_KEY_ESCAPE) {
                selectedKeyBinding.setKey(InputUtil.UNKNOWN_KEY);
            } else {
                selectedKeyBinding.setKey(InputUtil.fromKeyCode(keyCode, scanCode));
            }
            selectedKeyBinding = null;
            list.update();
            return true;
        } else {
            return super.keyPressed(keyCode, scanCode, modifiers);
        }
    }

    @Override
    protected void init() {
        this.layout = new ThreePartsLayoutWidget(this);

        TextWidget title = new TextWidget(Text.translatable("toggle_keybinds.modmenuscreen.title"), this.textRenderer);
        layout.addHeader(title, Positioner::alignHorizontalCenter);

        list = layout.addBody(new ToggleableKeybindingWidget(this, this.client, this.width, layout.getContentHeight(),
                layout.getHeaderHeight(), 20));

        ButtonWidget saveButton = ButtonWidget
                .builder(Text.translatable("toggle_keybinds.modmenuscreen.save"), button -> saveKeys())
                .dimensions(0, 0, 100, 20)
                .build();
        ButtonWidget cancelButton = ButtonWidget
                .builder(Text.translatable("toggle_keybinds.modmenuscreen.cancel"), button -> cancel())
                .dimensions(0, 0, 100, 20)
                .build();
        DirectionalLayoutWidget footer = layout.addFooter(DirectionalLayoutWidget.horizontal().spacing(8));
        footer.add(saveButton);
        footer.add(cancelButton);

        this.layout.forEachChild(this::addDrawableChild);
        layout.refreshPositions();
    }
}
