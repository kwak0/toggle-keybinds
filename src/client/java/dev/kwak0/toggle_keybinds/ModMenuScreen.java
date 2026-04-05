package dev.kwak0.toggle_keybinds;

import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.layouts.HeaderAndFooterLayout;
import net.minecraft.client.gui.components.StringWidget;
import net.minecraft.client.gui.layouts.LayoutSettings;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.layouts.LinearLayout;
import net.minecraft.client.input.KeyEvent;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.network.chat.Component;
import org.jspecify.annotations.NonNull;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.List;

public class ModMenuScreen extends Screen {

    private final Screen parent;
    private HeaderAndFooterLayout layout;
    private ToggleableKeybindingWidget list;
    private ToggleKey selectedKeyBinding;

    private final List<ToggleKey> newKeys;

    protected ModMenuScreen(Screen parent) {
        super(Component.translatable("toggle_keybinds.modmenuscreen.title"));
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
    public void onClose() {
        ToggleKeys.saveKeys(newKeys);
        this.minecraft.setScreen(this.parent);
    }

    public void cancel() {
        this.minecraft.setScreen(this.parent);
    }

    public void saveKeys() {
        onClose();
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
    protected void repositionElements() {
        this.layout.arrangeElements();
        if (this.list != null) {
            this.list.updateSize(this.width, this.layout);
        }
    }

    @Override
    public boolean mouseClicked(@NonNull MouseButtonEvent click, boolean doubled) {
        if (this.selectedKeyBinding != null) {
            this.selectedKeyBinding.setKey(InputConstants.Type.MOUSE.getOrCreate(click.input()));
            this.selectedKeyBinding = null;
            this.list.update();
            return true;
        } else {
            return super.mouseClicked(click, doubled);
        }
    }

    @Override
    public boolean keyPressed(@NonNull KeyEvent input) {
        if (selectedKeyBinding != null) {
            if (input.input() == GLFW.GLFW_KEY_ESCAPE) {
                selectedKeyBinding.setKey(InputConstants.UNKNOWN);
            } else {
                selectedKeyBinding.setKey(InputConstants.getKey(input));
            }
            selectedKeyBinding = null;
            list.update();
            return true;
        } else {
            return super.keyPressed(input);
        }
    }

    @Override
    protected void init() {
        this.layout = new HeaderAndFooterLayout(this);

        StringWidget title = new StringWidget(Component.translatable("toggle_keybinds.modmenuscreen.title"), this.font);
        layout.addToHeader(title, LayoutSettings::alignHorizontallyCenter);

        list = layout.addToContents(new ToggleableKeybindingWidget(this, this.minecraft, this.width, layout.getContentHeight(),
                layout.getHeaderHeight(), 20));

        Button saveButton = Button
                .builder(Component.translatable("toggle_keybinds.modmenuscreen.save"), button -> saveKeys())
                .bounds(0, 0, 100, 20)
                .build();
        Button cancelButton = Button
                .builder(Component.translatable("toggle_keybinds.modmenuscreen.cancel"), button -> cancel())
                .bounds(0, 0, 100, 20)
                .build();
        LinearLayout footer = layout.addToFooter(LinearLayout.horizontal().spacing(8));
        footer.addChild(saveButton);
        footer.addChild(cancelButton);

        this.layout.visitWidgets(this::addRenderableWidget);
        layout.arrangeElements();
    }
}
