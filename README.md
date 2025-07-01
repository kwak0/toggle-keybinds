# Toggle Keybinds Mod

This mod allows you to keybinding that let you switch between 2 hotbar slots with one button.
Useful if you want to navigate your hotbar quickly but don't like to use a lot of hotkeys.

## Installation

### Required Mods

 - [Fabric API](https://modrinth.com/mod/fabric-api/)
 - [Mod Menu](https://modrinth.com/mod/modmenu)

### Configuration

Keybinds can be configured on the config screen from the mods menu.  
(Esc -> Mods -> Toggle Keybinds -> [config icon in top right])

Because the bindings are saved in "keybinds.json" in the mod's config folder, it is also possible to edit them directly (although I don't recommend this).
They are stored in the following format:

    [
        {
        "keyCode": 70,
        "slot1": 0,
        "slot2": 8
        }
    ]

This keybind binds the "F" key to toggle between the first and the last slot.
Keycodes can be found here:
- [Mouse buttons](https://www.glfw.org/docs/latest/group__buttons.html)
- [Keyboard keys](https://www.glfw.org/docs/latest/group__keys.html)