package com.example.CHChests;

import net.minecraft.init.Blocks;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;

@Mod(modid = CHChests.MODID, version = CHChests.VERSION)
public class CHChests
{
    public static final String MODID = "CHChests";
    public static final String VERSION = "1.8.2";
    private KeyInputHandler keyInputHandler;


    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        keyInputHandler = new KeyInputHandler();
        MinecraftForge.EVENT_BUS.register(keyInputHandler);
        MinecraftForge.EVENT_BUS.register(new ClickHandler(keyInputHandler));

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




}
