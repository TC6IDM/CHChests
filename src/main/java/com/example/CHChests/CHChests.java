package com.example.CHChests;

import net.minecraft.init.Blocks;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@Mod(modid = CHChests.MODID, version = CHChests.VERSION)
public class CHChests
{
    public static final String MODID = "CHChests";
    public static final String VERSION = "1.5";

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        MinecraftForge.EVENT_BUS.register(new com.example.CHChests.KeyInputHandler());
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
