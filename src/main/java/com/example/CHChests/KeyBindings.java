package com.example.CHChests;

import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import org.lwjgl.input.Keyboard;

public class KeyBindings {
    public static KeyBinding findChests;
    public static KeyBinding clearChests;
    public static KeyBinding addBlock;
    public static KeyBinding done;
    public static KeyBinding findTitanium;
    public static KeyBinding toggleTracker;


    public static void init() {
        findChests = new KeyBinding("key.find_chests", Keyboard.KEY_K, "key.categories.CHChests");
        ClientRegistry.registerKeyBinding(findChests);

        clearChests = new KeyBinding("key.clear_chests", Keyboard.KEY_L, "key.categories.CHChests");
        ClientRegistry.registerKeyBinding(clearChests);

        addBlock = new KeyBinding("key.addBlock", Keyboard.KEY_DIVIDE, "key.categories.CHChests");
        ClientRegistry.registerKeyBinding(addBlock);

        done = new KeyBinding("key.debug", Keyboard.KEY_SUBTRACT, "key.categories.CHChests");
        ClientRegistry.registerKeyBinding(done);

        findTitanium = new KeyBinding("key.find_titanium", Keyboard.KEY_ADD, "key.categories.CHChests");
        ClientRegistry.registerKeyBinding(findTitanium);

        toggleTracker = new KeyBinding("key.toggleTracker", Keyboard.KEY_MULTIPLY, "key.categories.tracker");
        ClientRegistry.registerKeyBinding(toggleTracker);
    }
}