package com.example.CHChests;

import net.minecraft.init.Blocks;
import net.minecraftforge.client.ClientCommandHandler;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import com.example.CHChests.command.ExampleCommand;
import com.example.CHChests.config.TestConfig;
import cc.polyfrost.oneconfig.events.event.InitializationEvent;
import net.minecraftforge.fml.common.Mod;
import cc.polyfrost.oneconfig.utils.commands.CommandManager;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;

/**
 * The entrypoint of the Example Mod that initializes it.
 *
 * @see Mod
 * @see InitializationEvent
 */
@Mod(modid = CHChests.MODID, name = CHChests.NAME, version = CHChests.VERSION)
public class CHChests {

    // Sets the variables from `gradle.properties`. See the `blossom` config in `build.gradle.kts`.
    public static final String MODID = "@ID@";
    public static final String NAME = "@NAME@";
    public static final String VERSION = "@VER@";
    @Mod.Instance(MODID)
    public static CHChests INSTANCE; // Adds the instance of the mod, so we can access other variables.
    public static TestConfig config;
    private KeyInputHandler keyInputHandler;
    private MobTracker mobTracker;

    // Register the config and commands.
    @Mod.EventHandler
    public void onInit(FMLInitializationEvent event) {
        config = new TestConfig();
        CommandManager.INSTANCE.registerCommand(new ExampleCommand());
    }


    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        keyInputHandler = new KeyInputHandler();
        MinecraftForge.EVENT_BUS.register(keyInputHandler);
        MinecraftForge.EVENT_BUS.register(new ClickHandler(keyInputHandler));
        mobTracker = new MobTracker();
        MinecraftForge.EVENT_BUS.register(mobTracker);
        MinecraftForge.EVENT_BUS.register(this);
        KeyBindings.init();

        CommandCHCLocateClient newcmd = new CommandCHCLocateClient();
        ClientCommandHandler.instance.registerCommand(newcmd);
        MinecraftForge.EVENT_BUS.register(newcmd);

    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event)
    {
        System.out.println("DIRT BLOCK >> " + Blocks.dirt.getUnlocalizedName());
    }

    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent event) {
    }

    @SubscribeEvent
    public void onKeyInput(InputEvent.KeyInputEvent event) {
        mobTracker.onKeyPress();
    }


}
