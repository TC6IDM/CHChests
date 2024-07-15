package com.example.CHChests;

import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import org.lwjgl.input.Keyboard;

public class KeyBindings {
    public static KeyBinding findChests;
    public static KeyBinding clearChests;

    public static void init() {
        findChests = new KeyBinding("key.find_chests", Keyboard.KEY_K, "key.categories.CHChests");
        ClientRegistry.registerKeyBinding(findChests);

        clearChests = new KeyBinding("key.clear_chests", Keyboard.KEY_L, "key.categories.CHChests");
        ClientRegistry.registerKeyBinding(clearChests);
    }
}