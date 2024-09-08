package com.example.CHChests.config;

import com.example.CHChests.CHChests;
import com.example.CHChests.hud.TestHud;
import cc.polyfrost.oneconfig.config.Config;
import cc.polyfrost.oneconfig.config.annotations.Dropdown;
import cc.polyfrost.oneconfig.config.annotations.HUD;
import cc.polyfrost.oneconfig.config.annotations.Slider;
import cc.polyfrost.oneconfig.config.annotations.Switch;
import cc.polyfrost.oneconfig.config.data.Mod;
import cc.polyfrost.oneconfig.config.data.ModType;
import cc.polyfrost.oneconfig.config.data.OptionSize;

/**
 * The main Config entrypoint that extends the Config type and inits the config options.
 * See <a href="https://docs.polyfrost.cc/oneconfig/config/adding-options">this link</a> for more config Options
 */
public class TestConfig extends Config {
    @HUD(
            name = "Example HUD"
    )
//    public TestHud hud = new TestHud();

    @Switch(
            name = "Mob Tracker",
            size = OptionSize.SINGLE // Optional
    )
    public static boolean trackerEnabled = false; // The default value for the boolean Switch.

    @Slider(
            name = "Crystal Hollows Chest Search Distance",
            min = 0f, max = 500f, // Minimum and maximum values for the slider.
            step = 10 // The amount of steps that the slider should have.
    )
    public static float CHRadius = 200f; // The default value for the float Slider.

    @Slider(
            name = "Dwarven Mines Titanium Search Distance",
            min = 0f, max = 500f, // Minimum and maximum values for the slider.
            step = 10 // The amount of steps that the slider should have.
    )
    public static float TitaniumRadius = 200f; // The default value for the float Slider.

//    @Dropdown(
//            name = "Example Dropdown", // Name of the Dropdown
//            options = {"Option 1", "Option 2", "Option 3", "Option 4"} // Options available.
//    )
//    public static int exampleDropdown = 1; // Default option (in this case "Option 2")

    public TestConfig() {
        super(new Mod(CHChests.NAME, ModType.UTIL_QOL), CHChests.MODID + ".json");
        initialize();
    }
}

