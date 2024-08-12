package com.example.CHChests;

import net.minecraft.init.Blocks;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;

@Mod(modid = CHChests.MODID, version = CHChests.VERSION)
public class CHChests
{
    public static final String MODID = "CHChests";
    public static final String VERSION = "1.9.3";
    private KeyInputHandler keyInputHandler;
    private MobTracker mobTracker;


    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        keyInputHandler = new KeyInputHandler();
        MinecraftForge.EVENT_BUS.register(keyInputHandler);
        MinecraftForge.EVENT_BUS.register(new ClickHandler(keyInputHandler));
        mobTracker = new MobTracker();
        MinecraftForge.EVENT_BUS.register(mobTracker);
        MinecraftForge.EVENT_BUS.register(this);
        KeyBindings.init();
    }

    @EventHandler
    public void init(FMLInitializationEvent event)
    {
        System.out.println("DIRT BLOCK >> " + Blocks.dirt.getUnlocalizedName());
    }

    @EventHandler
    public void postInit(FMLPostInitializationEvent event) {
    }

    @SubscribeEvent
    public void onKeyInput(InputEvent.KeyInputEvent event) {
        mobTracker.onKeyPress();
    }



}
