package com.example.CHChests;

import net.minecraft.block.Block;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.util.*;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;

import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class KeyInputHandler {

    private static class BlockInfo {
        BlockPos pos;
        String blockName;

        BlockInfo(BlockPos pos, String blockName) {
            this.pos = pos;
            this.blockName = blockName;
        }
    }

    private Map<BlockPos, String> blockTextMap = new HashMap<BlockPos, String>();
    private Set<BlockPos> processedPositions = new HashSet<BlockPos>();
    private List<BlockInfo> structureBlocks = new ArrayList<BlockInfo>();
    private List<BlockPos> structureChests = new ArrayList<BlockPos>();
    @SubscribeEvent
    public void onKeyInput(InputEvent.KeyInputEvent event) throws IOException {
        if (KeyBindings.clearChests.isPressed()) {blockTextMap.clear(); processedPositions.clear();}
        if (KeyBindings.addBlock.isPressed()) addBlock();
        if (KeyBindings.addChest.isPressed()) addChest();
        if (KeyBindings.done.isPressed()) done();
        if(!KeyBindings.findChests.isPressed()) return;

        System.out.println("LOOKING FOR CHESTS");
        EntityPlayer player = Minecraft.getMinecraft().thePlayer;
        World world = player.worldObj;

        BlockPos playerPos = player.getPosition();
        int searchRadius = 100;
        int searchRadiusSquared = searchRadius * searchRadius;

        //jungle bottom
        BlockPos startPos = new BlockPos(202, 31, 202);
        //precursor top
        BlockPos endPos = new BlockPos(823, 188, 823);

//        blockTextMap.clear();

        BlockPos ignoreStartPos = new BlockPos(463, 60, 462); //nuc jungle bottom: 463 60 462
        BlockPos ignoreEndPos = new BlockPos(564, 188, 565); //nuc prec top: 564 188 565

        for (int x = startPos.getX(); x <= endPos.getX(); x++) {
            for (int y = startPos.getY(); y <= endPos.getY(); y++) {
                for (int z = startPos.getZ(); z <= endPos.getZ(); z++) {

                    double dx = x - playerPos.getX();
                    double dy = y - playerPos.getY();
                    double dz = z - playerPos.getZ();
                    double distanceSquared = dx * dx + dy * dy + dz * dz;
                    if (distanceSquared > searchRadiusSquared) continue;

                    if (x >= ignoreStartPos.getX() && x <= ignoreEndPos.getX() &&
                            y >= ignoreStartPos.getY() && y <= ignoreEndPos.getY() &&
                            z >= ignoreStartPos.getZ() && z <= ignoreEndPos.getZ()) continue;

                    BlockPos pos = new BlockPos(x, y, z);
                    if (world.getBlockState(pos).getBlock() == Blocks.stone) continue;
                    if (world.getBlockState(pos).getBlock() == Blocks.air) continue;

                    if (processedPositions.contains(pos)) {
                        continue;
                    }

                    processedPositions.add(pos);

                    //King structure
                    if (world.getBlockState(pos).getBlock() == Blocks.wool &&
                            world.getBlockState(new BlockPos(x - 1, y, z)).getBlock() == Blocks.wool &&
                            world.getBlockState(new BlockPos(x + 1, y, z)).getBlock() == Blocks.wool &&
                            world.getBlockState(new BlockPos(x - 2, y, z)).getBlock() == Blocks.log2 &&
                            world.getBlockState(new BlockPos(x + 2, y, z)).getBlock() == Blocks.log2 &&
                            world.getBlockState(new BlockPos(x - 2, y + 1, z)).getBlock() == Blocks.dark_oak_stairs &&
                            world.getBlockState(new BlockPos(x + 2, y + 1, z)).getBlock() == Blocks.dark_oak_stairs){

                        BlockPos pos1 = new BlockPos(x, y - 8, z + 1);
                        BlockPos pos2 = new BlockPos(x, y + 8, z + 1);

                        blockTextMap.put(pos1,"Goblin King Tower 1");
                        blockTextMap.put(pos2,"Goblin King Tower 2");
                    }

                    //Queen structure
                    if (world.getBlockState(pos).getBlock() == Blocks.cauldron &&
                            world.getBlockState(new BlockPos(x - 19, y - 1, z - 7)).getBlock() == Blocks.cauldron){

                        BlockPos pos1 = new BlockPos(x - 10, y + 1, z + 6);
                        BlockPos pos2 = new BlockPos(x - 40, y - 18, z + 29);

                        blockTextMap.put(pos1,"Goblin Queen's Den 1");
                        blockTextMap.put(pos2,"Goblin Queen's Den 2");
                    }

                    //Goblin Sewer Camp
                    if (world.getBlockState(pos).getBlock() == Blocks.iron_bars &&
                            world.getBlockState(new BlockPos(x, y + 1,  z)).getBlock() == Blocks.iron_bars &&
                            world.getBlockState(new BlockPos(x-1, y,  z)).getBlock() == Blocks.iron_bars &&
                            world.getBlockState(new BlockPos(x-1, y + 1, z)).getBlock() == Blocks.iron_bars &&
                            world.getBlockState(new BlockPos(x, y - 1, z)).getBlock() == Blocks.soul_sand &&
                            world.getBlockState(new BlockPos(x - 1, y - 1, z)).getBlock() == Blocks.soul_sand &&
                            world.getBlockState(new BlockPos(x - 2, y, z)).getBlock() == Blocks.stonebrick &&
                            world.getBlockState(new BlockPos(x - 2, y + 1, z)).getBlock() == Blocks.stonebrick) {

                        BlockPos pos1 = new BlockPos(x + 2, y, z + 1);
                        blockTextMap.put(pos1,"Goblin Sewer Camp 1");

                    }

                    //Deep Goblin Lair
                    if (world.getBlockState(pos).getBlock() == Blocks.trapdoor && // 402 103 731
                            world.getBlockState(new BlockPos(x, y + 1,  z)).getBlock() == Blocks.planks &&
                            world.getBlockState(new BlockPos(x, y + 2,  z)).getBlock() == Blocks.planks &&
                            world.getBlockState(new BlockPos(x, y + 1, z - 1)).getBlock() == Blocks.planks &&
                            world.getBlockState(new BlockPos(x, y + 1, z + 1)).getBlock() == Blocks.planks &&
                            world.getBlockState(new BlockPos(x, y + 2, z - 1)).getBlock() == Blocks.dark_oak_stairs &&
                            world.getBlockState(new BlockPos(x, y + 2, z + 1)).getBlock() == Blocks.dark_oak_stairs &&
                            world.getBlockState(new BlockPos(x, y, z - 1)).getBlock() == Blocks.dark_oak_stairs &&
                            world.getBlockState(new BlockPos(x, y, z + 1)).getBlock() == Blocks.dark_oak_stairs){

                        BlockPos pos1 = new BlockPos(x - 42, y + 54, z - 23); // 360 157 708
                        BlockPos pos2 = new BlockPos(x , y - 2, z + 6); //402 101 737
                        BlockPos pos3 = new BlockPos(x - 25, y - 17, z - 35); //377 86 696

                        blockTextMap.put(pos1,"Deep Goblin Lair 1");
                        blockTextMap.put(pos2,"Deep Goblin Lair 2");
                        blockTextMap.put(pos3,"Deep Goblin Lair 3");
                    }

                    //Tiny Hut
                    if ((world.getBlockState(pos).getBlock() == Blocks.daylight_detector || world.getBlockState(pos).getBlock() == Blocks.daylight_detector_inverted) &&
                            world.getBlockState(new BlockPos(x + 7, y + 1,  z + 4)).getBlock() == Blocks.brick_stairs &&
                            world.getBlockState(new BlockPos(x + 7, y - 1,  z + 4)).getBlock() == Blocks.brick_stairs) {

                        BlockPos pos1 = new BlockPos(x - 5, y - 1, z - 1);
                        blockTextMap.put(pos1,"Tiny Hut 1");
                    }

                    //Dragon Skull
                    if (world.getBlockState(pos).getBlock() == Blocks.glowstone &&
                            world.getBlockState(new BlockPos(x, y + 1,  z)).getBlock() == Blocks.carpet &&
                            world.getBlockState(new BlockPos(x - 5, y + 1,  z)).getBlock() == Blocks.quartz_stairs &&
                            world.getBlockState(new BlockPos(x - 6, y + 1,  z)).getBlock() == Blocks.quartz_block &&
                            world.getBlockState(new BlockPos(x - 6, y + 2,  z)).getBlock() == Blocks.stone_slab) {

                        BlockPos pos1 = new BlockPos(x - 4, y + 1,  z);
                        BlockPos pos2 = new BlockPos(x - 9, y - 6,  z - 5);

                        blockTextMap.put(pos1,"Dragon Skull 1");
                        blockTextMap.put(pos2,"Dragon Skull 2");
                    }

                    //Precursor Trapped Stair

                    if (world.getBlockState(pos).getBlock() == Blocks.spruce_fence &&
                            world.getBlockState(new BlockPos(x, y + 1,  z)).getBlock() == Blocks.torch &&
                            world.getBlockState(new BlockPos(x + 1, y,  z + 9)).getBlock() == Blocks.dispenser &&
                            world.getBlockState(new BlockPos(x + 1, y + 2,z + 9)).getBlock() == Blocks.dispenser &&
                            world.getBlockState(new BlockPos(x, y + 1, z+9)).getBlock() == Blocks.dispenser) {

                        BlockPos pos1 = new BlockPos(x + 1, y + 1,  z - 3);
                        BlockPos pos2 = new BlockPos(x - 48, y + 34,  z - 7);
                        BlockPos pos3 = new BlockPos(x - 37, y + 52,  z + 33);

                        blockTextMap.put(pos1,"Precursor Trapped Stair 1");
                        blockTextMap.put(pos2,"Precursor Trapped Stair 2");
                        blockTextMap.put(pos3,"Precursor Trapped Stair 3");
                    }


                    //Precursor Diorite Corridor (FIX THIS)
                    if (world.getBlockState(pos).getBlock() == Blocks.wooden_slab &&
                            world.getBlockState(new BlockPos(x, y,  z + 2)).getBlock() == Blocks.double_stone_slab &&
                            world.getBlockState(new BlockPos(x, y,  z - 1)).getBlock() == Blocks.wooden_slab &&
                            world.getBlockState(new BlockPos(x, y - 1,  z + 1)).getBlock() == Blocks.planks &&
                            world.getBlockState(new BlockPos(x + 1, y - 1,  z)).getBlock() == Blocks.planks &&
                            world.getBlockState(new BlockPos(x + 1, y - 1,  z - 1)).getBlock() == Blocks.planks &&
                            world.getBlockState(new BlockPos(x + 2, y - 1,  z - 2)).getBlock() == Blocks.planks) {

                        BlockPos pos1 = new BlockPos(x + 5, y,  z + 1);

                        blockTextMap.put(pos1,"Precursor Diorite Corridor 1");
                    }

                    //Underground Office
                    if (world.getBlockState(pos).getBlock() == Blocks.lever &&
                            world.getBlockState(new BlockPos(x, y,  z - 3)).getBlock() == Blocks.lever &&
                            world.getBlockState(new BlockPos(x , y - 1,  z)).getBlock() == Blocks.spruce_stairs &&
                            world.getBlockState(new BlockPos(x , y - 1,  z - 1)).getBlock() == Blocks.log2 &&
                            world.getBlockState(new BlockPos(x - 1, y - 1,  z - 1)).getBlock() == Blocks.tripwire_hook &&
                            world.getBlockState(new BlockPos(x + 1, y - 1,  z - 1)).getBlock() == Blocks.tripwire_hook &&
                            world.getBlockState(new BlockPos(x , y - 1,  z - 2)).getBlock() == Blocks.log2 &&
                            world.getBlockState(new BlockPos(x - 1, y - 1,  z - 2)).getBlock() == Blocks.tripwire_hook &&
                            world.getBlockState(new BlockPos(x + 1, y - 1,  z - 2)).getBlock() == Blocks.tripwire_hook &&
                            world.getBlockState(new BlockPos(x , y - 1,  z - 3)).getBlock() == Blocks.spruce_stairs) {

                        BlockPos pos1 = new BlockPos(x, y - 1,  z + 2);
                        BlockPos pos2 = new BlockPos(x - 15, y,  z + 5);
                        blockTextMap.put(pos1,"Underground Office 1");
                        blockTextMap.put(pos2,"Underground Office 2");

                    }

                    //Precursor Throne Hall

                    if (world.getBlockState(pos).getBlock() == Blocks.cobblestone_wall &&
                            world.getBlockState(new BlockPos(x+2, y, z)).getBlock() == Blocks.cobblestone_wall &&
                            world.getBlockState(new BlockPos(x, y, z + 1)).getBlock() == Blocks.cobblestone_wall &&
                            world.getBlockState(new BlockPos(x+2 , y,  z + 1)).getBlock() == Blocks.cobblestone_wall &&
                            world.getBlockState(new BlockPos(x+2 , y,  z + 2)).getBlock() == Blocks.double_stone_slab &&
                            world.getBlockState(new BlockPos(x+2 , y + 1,  z + 2)).getBlock() == Blocks.double_stone_slab &&
                            world.getBlockState(new BlockPos(x+1 , y,  z + 2)).getBlock() == Blocks.double_stone_slab &&
                            world.getBlockState(new BlockPos(x+1 , y + 1,  z + 2)).getBlock() == Blocks.double_stone_slab &&
                            world.getBlockState(new BlockPos(x+1 , y + 2,  z + 2)).getBlock() == Blocks.double_stone_slab &&
                            world.getBlockState(new BlockPos(x+1 , y + 3,  z + 2)).getBlock() == Blocks.stone_slab &&
                            world.getBlockState(new BlockPos(x , y,  z + 2)).getBlock() == Blocks.double_stone_slab &&
                            world.getBlockState(new BlockPos(x , y + 1,  z + 2)).getBlock() == Blocks.double_stone_slab &&
                            world.getBlockState(new BlockPos(x, y - 1,  z)).getBlock() == Blocks.stone_slab &&
                            world.getBlockState(new BlockPos(x + 1, y - 1,  z)).getBlock() == Blocks.stone_slab &&
                            world.getBlockState(new BlockPos(x + 2, y - 1,  z)).getBlock() == Blocks.stone_slab &&
                            world.getBlockState(new BlockPos(x, y - 1,  z + 1)).getBlock() == Blocks.stone_stairs &&
                            world.getBlockState(new BlockPos(x + 1, y - 1,  z + 1)).getBlock() == Blocks.double_stone_slab &&
                            world.getBlockState(new BlockPos(x + 2, y - 1,  z + 1)).getBlock() == Blocks.stone_stairs) {

                        BlockPos pos1 = new BlockPos(x - 25, y - 32,  z - 30);
                        BlockPos pos2 = new BlockPos(x + 4, y - 3,  z - 43);
                        blockTextMap.put(pos1,"Precursor Throne Hall 1");
                        blockTextMap.put(pos2,"Precursor Throne Hall 2");

                    }

                    //Using Generator.py

                    //Ruins
                    if (world.getBlockState(pos).getBlock() == Blocks.cauldron &&
                            world.getBlockState(new BlockPos(x, y, z + -7)).getBlock() == Blocks.cauldron &&
                            world.getBlockState(new BlockPos(x + -6, y + -1, z + -12)).getBlock() == Blocks.cauldron){
                        BlockPos pos1 = new BlockPos(x + -6, y + -1, z + 5);
                        blockTextMap.put(pos1, "Ruins 1");
                        BlockPos pos2 = new BlockPos(x + -16, y + 28, z + 5);
                        blockTextMap.put(pos2, "Ruins 2");
                    }

                    //Precursor Tall Pillars 659 130 578
                    if (world.getBlockState(pos).getBlock() == Blocks.sea_lantern &&
                            world.getBlockState(new BlockPos(x + 2, y + -1, z + 13)).getBlock() == Blocks.sea_lantern &&
                            world.getBlockState(new BlockPos(x + -20, y + 6, z + 33)).getBlock() == Blocks.sea_lantern &&
                            world.getBlockState(new BlockPos(x + -1, y + 8, z + 42)).getBlock() == Blocks.sea_lantern){
                        BlockPos pos1 = new BlockPos(x + -6, y + -1, z + 1);
                        blockTextMap.put(pos1, "Precursor Tall Pillars 1");
                        BlockPos pos2 = new BlockPos(x + -25, y + 19, z + 41);
                        blockTextMap.put(pos2, "Precursor Tall Pillars 2");
                    }

                    //Colored Skull Puzzle 584 108 728
                    if (world.getBlockState(pos).getBlock() == Blocks.lapis_block &&
                            world.getBlockState(new BlockPos(x + 0, y + 0, z + 2)).getBlock() == Blocks.bedrock &&
                            world.getBlockState(new BlockPos(x + 0, y + 0, z + 4)).getBlock() == Blocks.redstone_block &&
                            world.getBlockState(new BlockPos(x + 0, y + 1, z + 2)).getBlock() == Blocks.gold_block &&
                            world.getBlockState(new BlockPos(x + 0, y + 2, z + 2)).getBlock() == Blocks.bedrock &&
                            world.getBlockState(new BlockPos(x + 0, y + 3, z + 2)).getBlock() == Blocks.quartz_block){
                        BlockPos pos1 = new BlockPos(x + 6, y + -1, z + 2);
                        blockTextMap.put(pos1, "Colored Skull Puzzle 1");
                    }

                    //Corleone Lakefront 624 98 455
                    if (world.getBlockState(pos).getBlock() == Blocks.iron_bars &&
                            world.getBlockState(new BlockPos(x + 0, y + 1, z + 0)).getBlock() == Blocks.cobblestone_wall &&
                            world.getBlockState(new BlockPos(x + 0, y + -1, z + 0)).getBlock() == Blocks.cobblestone_wall &&
                            world.getBlockState(new BlockPos(x + -4, y + 8, z + 0)).getBlock() == Blocks.cobblestone_wall &&
                            world.getBlockState(new BlockPos(x + -4, y + 7, z + 2)).getBlock() == Blocks.log &&
                            world.getBlockState(new BlockPos(x + -4, y + 7, z + -2)).getBlock() == Blocks.log){
                        BlockPos pos1 = new BlockPos(x + 1, y + -3, z + -1);
                        blockTextMap.put(pos1, "Corleone Lakefront 1");
                        BlockPos pos2 = new BlockPos(x + 36, y + -14, z + 25);
                        blockTextMap.put(pos2, "Corleone Lakefront 2");
                    }

                    //Colosseum 615 139 466
                    if (world.getBlockState(pos).getBlock() == Blocks.noteblock &&
                            world.getBlockState(new BlockPos(x + 0, y + 0, z + -5)).getBlock() == Blocks.piston &&
                            world.getBlockState(new BlockPos(x + 1, y + 0, z + -6)).getBlock() == Blocks.piston &&
                            world.getBlockState(new BlockPos(x + 1, y + 1, z + -6)).getBlock() == Blocks.piston &&
                            world.getBlockState(new BlockPos(x + 9, y + 0, z + 4)).getBlock() == Blocks.cauldron){
                        BlockPos pos1 = new BlockPos(x + 13, y + 0, z + 1);
                        blockTextMap.put(pos1, "Colosseum 1");
                    }

                    //Crystal Train 668 124 515
                    if (world.getBlockState(pos).getBlock() == Blocks.dark_oak_fence_gate &&
                            world.getBlockState(new BlockPos(x + 7, y + 0, z + 0)).getBlock() == Blocks.dark_oak_fence_gate &&
                            world.getBlockState(new BlockPos(x + 7, y + -2, z + 0)).getBlock() == Blocks.wooden_slab &&
                            world.getBlockState(new BlockPos(x + 8, y + -2, z + 0)).getBlock() == Blocks.carpet){
                        BlockPos pos1 = new BlockPos(x + 3, y + 1, z + 0);
                        blockTextMap.put(pos1, "Crystal Train 1");
                        BlockPos pos2 = new BlockPos(x + -4, y + 1, z + 0);
                        blockTextMap.put(pos2, "Crystal Train 2");
                    }

                    //Big Automaton 672 163 635
                    if (world.getBlockState(pos).getBlock() == Blocks.dropper &&
                            world.getBlockState(new BlockPos(x + 1, y + 0, z + 0)).getBlock() == Blocks.dispenser &&
                            world.getBlockState(new BlockPos(x + 0, y + -1, z + 0)).getBlock() == Blocks.hopper &&
                            world.getBlockState(new BlockPos(x + 1, y + -1, z + 0)).getBlock() == Blocks.hopper &&
                            world.getBlockState(new BlockPos(x + -3, y + 1, z + 1)).getBlock() == Blocks.tripwire_hook){
                        BlockPos pos1 = new BlockPos(x + -3, y + 0, z + 0);
                        blockTextMap.put(pos1, "Big Automaton 1");
                        BlockPos pos2 = new BlockPos(x + 18, y + -13, z + 21);
                        blockTextMap.put(pos2, "Big Automaton 2");
                    }

                    //Mansion 582 92 547
                    if (world.getBlockState(pos).getBlock() == Blocks.sea_lantern &&
                            world.getBlockState(new BlockPos(x + 0, y + 0, z + -1)).getBlock() == Blocks.double_stone_slab &&
                            world.getBlockState(new BlockPos(x + 0, y + 0, z + 1)).getBlock() == Blocks.double_stone_slab &&
                            world.getBlockState(new BlockPos(x + 1, y + 0, z + 0)).getBlock() == Blocks.double_stone_slab &&
                            world.getBlockState(new BlockPos(x + -1, y + 0, z + 0)).getBlock() == Blocks.double_stone_slab &&
                            world.getBlockState(new BlockPos(x + -4, y + 0, z + 7)).getBlock() == Blocks.cobblestone_wall &&
                            world.getBlockState(new BlockPos(x + 2, y + 0, z + -7)).getBlock() == Blocks.cobblestone_wall){
                        BlockPos pos1 = new BlockPos(x + 0, y + 2, z + 0);
                        blockTextMap.put(pos1, "Mansion 1");
                        BlockPos pos2 = new BlockPos(x + 0, y + -20, z + 13);
                        blockTextMap.put(pos2, "Mansion 2");
                    }

                    //Precursor Trapped Dungeon 602 84 580
                    if (world.getBlockState(pos).getBlock() == Blocks.iron_bars &&
                            world.getBlockState(new BlockPos(x + -1, y + 0, z + 1)).getBlock() == Blocks.cobblestone_wall &&
                            world.getBlockState(new BlockPos(x + -1, y + 3, z + 2)).getBlock() == Blocks.hopper &&
                            world.getBlockState(new BlockPos(x + 4, y + 3, z + 2)).getBlock() == Blocks.hopper &&
                            world.getBlockState(new BlockPos(x + 3, y + 0, z + 0)).getBlock() == Blocks.iron_bars){
                        BlockPos pos1 = new BlockPos(x + 2, y + 0, z + -4);
                        blockTextMap.put(pos1, "Precursor Trapped Dungeon 1");
                    }

                    //Precursor Tower 611 102 655
                    if (world.getBlockState(pos).getBlock() == Blocks.sea_lantern &&
                            world.getBlockState(new BlockPos(x + -3, y + 1, z + 10)).getBlock() == Blocks.anvil &&
                            world.getBlockState(new BlockPos(x + -3, y + 2, z + 10)).getBlock() == Blocks.anvil &&
                            world.getBlockState(new BlockPos(x + -3, y + 3, z + 10)).getBlock() == Blocks.cauldron &&
                            world.getBlockState(new BlockPos(x + 3, y + 1, z + 10)).getBlock() == Blocks.anvil &&
                            world.getBlockState(new BlockPos(x + 3, y + 2, z + 10)).getBlock() == Blocks.anvil &&
                            world.getBlockState(new BlockPos(x + 3, y + 3, z + 10)).getBlock() == Blocks.cauldron){
                        BlockPos pos1 = new BlockPos(x + 3, y + 1, z + 2);
                        blockTextMap.put(pos1, "Precursor Tower 1");
                        BlockPos pos2 = new BlockPos(x + 6, y + -16, z + 13);
                        blockTextMap.put(pos2, "Precursor Tower 2");
                    }

                    //NEW AUTOMATED SCRIPT INGAME

                    //Square 648 116 675
                    if (world.getBlockState(pos).getBlock() == Blocks.stone_stairs &&
                            world.getBlockState(new BlockPos(x + 0, y + -1, z + 1)).getBlock() == Blocks.stone_stairs &&
                            world.getBlockState(new BlockPos(x + 0, y + -1, z + 2)).getBlock() == Blocks.stone_slab &&
                            world.getBlockState(new BlockPos(x + 0, y + 4, z + 3)).getBlock() == Blocks.stone_brick_stairs &&
                            world.getBlockState(new BlockPos(x + 0, y + 5, z + 1)).getBlock() == Blocks.stone_brick_stairs &&
                            world.getBlockState(new BlockPos(x + 0, y + 5, z + 0)).getBlock() == Blocks.stone_brick_stairs &&
                            world.getBlockState(new BlockPos(x + 0, y + 5, z + -1)).getBlock() == Blocks.stone_brick_stairs &&
                            world.getBlockState(new BlockPos(x + 1, y + 5, z + -1)).getBlock() == Blocks.stone_brick_stairs &&
                            world.getBlockState(new BlockPos(x + 1, y + 5, z + 0)).getBlock() == Blocks.stone_brick_stairs) {
                        BlockPos pos1 = new BlockPos(x + 1, y + 5, z + 1);
                        blockTextMap.put(pos1, "Square 1");
                    }
                    /*
                    /setblock 648 116 675 minecraft:stone_stairs
                    /setblock 648 115 676 minecraft:stone_stairs
                    /setblock 648 115 677 minecraft:stone_slab
                    /setblock 648 120 678 minecraft:stone_brick_stairs
                    /setblock 648 121 676 minecraft:stone_brick_stairs
                    /setblock 648 121 675 minecraft:stone_brick_stairs
                    /setblock 648 121 674 minecraft:stone_brick_stairs
                    /setblock 649 121 674 minecraft:stone_brick_stairs
                    /setblock 649 121 675 minecraft:stone_brick_stairs
                    /setblock 649 121 676 minecraft:chest
                    */


//                    //NOTGIVEN 361 72 359
//                    if (world.getBlockState(pos).getBlock() == Blocks.stone_stairs &&
//                            world.getBlockState(new BlockPos(x + 0, y + -1, z + 0)).getBlock() == Blocks.cobblestone &&
//                            world.getBlockState(new BlockPos(x + 0, y + -1, z + -1)).getBlock() == Blocks.stone_slab) {
//                        BlockPos pos1 = new BlockPos(x + -1, y + -1, z + 0);
//                        blockTextMap.put(pos1, "NOTGIVEN 1");
//                    }
                }
            }
        }
    }


//    @SubscribeEvent
//    public void onRenderTick(TickEvent.RenderTickEvent event){
//        Minecraft mc = Minecraft.getMinecraft();
//        FontRenderer fRenderer = mc.fontRendererObj;
//        fRenderer.drawString("your text", 50,50, 255);
//    }
    private String getBlockRegistryName(Block block) {
        ResourceLocation registryName = Block.blockRegistry.getNameForObject(block);
        return registryName != null ? registryName.toString().replace("minecraft:", "Blocks.") : "Unknown";
    }

    private BlockPos getBlockLookingAt(EntityPlayer player, double reachDistance) {
        Vec3 startVec = player.getPositionEyes(1.0F);
        Vec3 lookVec = player.getLook(1.0F);
        Vec3 endVec = startVec.addVector(lookVec.xCoord * reachDistance, lookVec.yCoord * reachDistance, lookVec.zCoord * reachDistance);
        MovingObjectPosition mop = player.worldObj.rayTraceBlocks(startVec, endVec);

        if (mop == null) {
            return null;
        }

        return mop.getBlockPos();
    }

    public void addBlock(){
        EntityPlayer player = Minecraft.getMinecraft().thePlayer;
        World world = player.worldObj;
        BlockPos pos = getBlockLookingAt(player,5.0);
        if (pos == null) return;
        String block = getBlockRegistryName(world.getBlockState(pos).getBlock());
        String message = "[CHChests]: Coords: " + pos.getX() + " " + pos.getY() + " " + pos.getZ() + " Name: " + block;
        System.out.println(message);
        ChatComponentText chatMessage = new ChatComponentText(message);
        player.addChatMessage(chatMessage);
        structureBlocks.add(new BlockInfo(pos, block));
    }

    public void addChest(){
        EntityPlayer player = Minecraft.getMinecraft().thePlayer;
        BlockPos pos = getBlockLookingAt(player,5.0);
        if (pos == null) return;
        String message = "[CHChests]: Coords: " + pos.getX() + " " + pos.getY() + " " + pos.getZ() + " CHEST!";
        System.out.println(message);
        ChatComponentText chatMessage = new ChatComponentText(message);
        player.addChatMessage(chatMessage);
        structureChests.add(pos);
    }

    public void done() throws IOException {
        if (structureBlocks.isEmpty() || structureChests.isEmpty()) {
            System.out.println("No blocks added to the structure.");
            return;
        }
        EntityPlayer player = Minecraft.getMinecraft().thePlayer;
        String message = "[CHChests]: Creating Structure";
        System.out.println(message);
        ChatComponentText chatMessage = new ChatComponentText(message);
        player.addChatMessage(chatMessage);

        String structName = "NOTGIVEN";

        BlockPos start = structureBlocks.get(0).pos; // Get the first block position
        StringBuilder fullText = new StringBuilder();
        StringBuilder debugText = new StringBuilder();

        // Initialize fullText with the first block
        BlockInfo firstBlock = structureBlocks.get(0);
        fullText.append("//").append(structName).append(" ").append(firstBlock.pos.getX()).append(" ").append(firstBlock.pos.getY()).append(" ").append(firstBlock.pos.getZ()).append("\n")
                .append("if (world.getBlockState(pos).getBlock() == ").append(firstBlock.blockName).append(" &&\n");

        debugText.append("/*\n/setblock ").append(firstBlock.pos.getX()).append(" ").append(firstBlock.pos.getY()).append(" ").append(firstBlock.pos.getZ()).append(" minecraft:").append(firstBlock.blockName.replace("Blocks.", "")).append("\n");

        // Process remaining blocks
        for (int i = 1; i < structureBlocks.size(); i++) {
            BlockInfo blockInfo = structureBlocks.get(i);
            BlockPos pos = blockInfo.pos;
            fullText.append("world.getBlockState(new BlockPos(x + ").append(pos.getX() - start.getX()).append(", y + ").append(pos.getY() - start.getY()).append(", z + ").append(pos.getZ() - start.getZ()).append(")).getBlock() == ").append(blockInfo.blockName).append(" &&\n");
            debugText.append("/setblock ").append(pos.getX()).append(" ").append(pos.getY()).append(" ").append(pos.getZ()).append(" minecraft:").append(blockInfo.blockName.replace("Blocks.", "")).append("\n");
        }

        // Remove the last "&&\n" and close the if statement
        fullText.setLength(fullText.length() - 4);
        fullText.append(") {\n");

        // Process chest positions
        int chestIndex = 1;
        for (BlockPos chestPos : structureChests) {
            String chestInStructure = structName + " " + chestIndex;
            fullText.append("BlockPos pos").append(chestIndex).append(" = new BlockPos(x + ").append(chestPos.getX() - start.getX()).append(", y + ").append(chestPos.getY() - start.getY()).append(", z + ").append(chestPos.getZ() - start.getZ()).append(");\n")
                    .append("blockTextMap.put(pos").append(chestIndex).append(", \"").append(chestInStructure).append("\");\n");
            debugText.append("/setblock ").append(chestPos.getX()).append(" ").append(chestPos.getY()).append(" ").append(chestPos.getZ()).append(" minecraft:chest\n");
            chestIndex++;
        }

        fullText.append("}");
        debugText.append("*/");
        // Print the generated code
        System.out.println(fullText.toString());
        System.out.println("\n\n\n\n");
        System.out.println(debugText.toString());

        // Write the generated code to a file
        FileWriter writer = new FileWriter("C:\\Users\\Owner\\Desktop\\Forge Mod\\Struct.txt");
        writer.write(fullText.toString());
        writer.write("\n\n");
        writer.write(debugText.toString());
        writer.close();
        structureChests.clear();
        structureBlocks.clear();

    }
    @SubscribeEvent
    public void onRenderWorldLast(RenderWorldLastEvent event) {
        if (!blockTextMap.isEmpty()) {
            EntityPlayer player = Minecraft.getMinecraft().thePlayer;
            double playerX = player.lastTickPosX + (player.posX - player.lastTickPosX) * event.partialTicks;
            double playerY = player.lastTickPosY + (player.posY - player.lastTickPosY) * event.partialTicks;
            double playerZ = player.lastTickPosZ + (player.posZ - player.lastTickPosZ) * event.partialTicks;

            GL11.glPushMatrix();
            GL11.glTranslated(-playerX, -playerY, -playerZ);
            GL11.glDisable(GL11.GL_TEXTURE_2D);
            GL11.glDisable(GL11.GL_DEPTH_TEST);
            GL11.glEnable(GL11.GL_BLEND);
            GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
            GL11.glLineWidth(2.0F);

            for (BlockPos pos : blockTextMap.keySet()) {
                AxisAlignedBB box = new AxisAlignedBB(pos.getX(), pos.getY(), pos.getZ(), pos.getX() + 1, pos.getY() + 1, pos.getZ() + 1);
                RenderGlobal.drawOutlinedBoundingBox(box, 0, 0, 255, 255);
            }

            GL11.glDisable(GL11.GL_BLEND);
            GL11.glEnable(GL11.GL_DEPTH_TEST);
            GL11.glEnable(GL11.GL_TEXTURE_2D);
            GL11.glPopMatrix();

            GL11.glPushMatrix();
            GL11.glTranslated(-playerX, -playerY, -playerZ);
            GL11.glDisable(GL11.GL_TEXTURE_2D);
            GL11.glDisable(GL11.GL_DEPTH_TEST);
            GL11.glEnable(GL11.GL_BLEND);
            GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
            GL11.glLineWidth(2.0F);

            for (BlockPos pos : blockTextMap.keySet()){
                double x = pos.getX() + 0.5;
                double y = pos.getY() + 1.5;
                double z = pos.getZ() + 0.5;
                String textToRender = blockTextMap.get(pos);
                renderFloatingText(textToRender, x, y, z, event.partialTicks);
            }

            GL11.glDisable(GL11.GL_BLEND);
            GL11.glEnable(GL11.GL_DEPTH_TEST);
            GL11.glEnable(GL11.GL_TEXTURE_2D);
            GL11.glPopMatrix();
        }
    }

    private void renderFloatingText(String text, double x, double y, double z, float partialTicks) {
        Minecraft mc = Minecraft.getMinecraft();
        RenderManager renderManager = mc.getRenderManager();

        // Calculate distance from player to text
        EntityPlayer player = mc.thePlayer;
        double distance = player.getDistance(x, y, z);

        // Adjust scale based on distance
        float scale = (float) (0.005F * distance);

        GL11.glPushMatrix();
        GL11.glTranslatef((float) x, (float) y, (float) z);
        GL11.glNormal3f(0.0F, 1.0F, 0.0F);
        GL11.glRotatef(-renderManager.playerViewY, 0.0F, 1.0F, 0.0F);
        GL11.glRotatef(renderManager.playerViewX, 1.0F, 0.0F, 0.0F);
        GL11.glScalef(-scale, -scale, scale);
        GL11.glDisable(GL11.GL_LIGHTING);
        GL11.glDepthMask(false);
        GL11.glDisable(GL11.GL_DEPTH_TEST); // Disable depth test to keep the text color consistent
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer worldrenderer = tessellator.getWorldRenderer();

        int i = 0;
        int j = mc.fontRendererObj.getStringWidth(text) / 2;

        // Render the main text in light green
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        mc.fontRendererObj.drawString(text, -mc.fontRendererObj.getStringWidth(text) / 2, i, 0x00FF00); // Light green color

        // Render the distance in light blue below the main text
        String distanceText = String.format("Distance: %.1fm", distance);
        mc.fontRendererObj.drawString(distanceText, -mc.fontRendererObj.getStringWidth(distanceText) / 2, i + 10, 0x017AFF); // Light blue color

        GL11.glEnable(GL11.GL_DEPTH_TEST); // Re-enable depth test after rendering the text
        GL11.glDepthMask(true);
        GL11.glEnable(GL11.GL_LIGHTING);
        GL11.glDisable(GL11.GL_BLEND);
        GL11.glPopMatrix();
    }
}