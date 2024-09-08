package com.example.CHChests;

import com.example.CHChests.config.TestConfig;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import org.lwjgl.opengl.GL11;

import java.io.IOException;
import java.util.*;

public class CHChestsFinder {
    private TestConfig config;

    public CHChestsFinder(TestConfig c) {
        config = c;
    }

    private Map<BlockPos, String> blockTextMap = new HashMap<BlockPos, String>();

    public Map<BlockPos, String> getBlockTextMap() {
        return blockTextMap;
    }

    private Set<BlockPos> processedPositions = new HashSet<BlockPos>();
    private Map<String, Integer> blockCountMap = new HashMap<String, Integer>();

    @SubscribeEvent
    public void onKeyInput(InputEvent.KeyInputEvent event) throws IOException {
        if (KeyBindings.clearChests.isPressed()) {
            blockTextMap.clear();
            processedPositions.clear();
        }
        if (KeyBindings.findChests.isPressed()) findChests();
    }

    private String getBlockRegistryName(Block block) {
        ResourceLocation registryName = Block.blockRegistry.getNameForObject(block);
        return registryName != null ? registryName.toString().replace("minecraft:", "Blocks.") : "Unknown";
    }

    private void findChests() {
        System.out.println("LOOKING FOR CHESTS");
        EntityPlayer player = Minecraft.getMinecraft().thePlayer;
        World world = player.worldObj;

        BlockPos playerPos = player.getPosition();
        int searchRadius = (int) config.CHRadius;
//        int searchRadiusSquared = searchRadius * searchRadius;

//        //jungle bottom
//        BlockPos includeStartPos = new BlockPos(202, 31, 202);
//        //precursor top
//        BlockPos includeEndPos = new BlockPos(823, 188, 823);

        BlockPos startPos = playerPos.add(-searchRadius, -searchRadius, -searchRadius);
        BlockPos endPos = playerPos.add(searchRadius, searchRadius, searchRadius);
//        blockTextMap.clear();

//        BlockPos ignoreStartPos = new BlockPos(463, 60, 462); //nuc jungle bottom: 463 60 462
//        BlockPos ignoreEndPos = new BlockPos(564, 188, 565); //nuc prec top: 564 188 565

        List<Block> ignoreBlocks = new ArrayList<Block>();
        Collections.addAll(ignoreBlocks, Blocks.stone, Blocks.air, Blocks.bedrock, Blocks.coal_ore, Blocks.iron_ore, Blocks.prismarine, Blocks.stained_glass, Blocks.stained_glass_pane, Blocks.dirt, Blocks.stained_hardened_clay, Blocks.wool, Blocks.cobblestone, Blocks.redstone_ore, Blocks.gold_ore, Blocks.lapis_ore, Blocks.log, Blocks.clay, Blocks.emerald_ore, Blocks.planks, Blocks.leaves, Blocks.gold_block, Blocks.diamond_ore, Blocks.spruce_stairs, Blocks.stonebrick);


        for (int x = startPos.getX(); x <= endPos.getX(); x++) {
            for (int y = startPos.getY(); y <= endPos.getY(); y++) {
                for (int z = startPos.getZ(); z <= endPos.getZ(); z++) {

//                    double dx = x - playerPos.getX();
//                    double dy = y - playerPos.getY();
//                    double dz = z - playerPos.getZ();
//                    double distanceSquared = dx * dx + dy * dy + dz * dz;
//                    if (distanceSquared > searchRadiusSquared) continue;

//                    if (x >= ignoreStartPos.getX() && x <= ignoreEndPos.getX() &&
//                            y >= ignoreStartPos.getY() && y <= ignoreEndPos.getY() &&
//                            z >= ignoreStartPos.getZ() && z <= ignoreEndPos.getZ()) continue;

                    BlockPos pos = new BlockPos(x, y, z);

//                    if (isInRange(pos, ignoreStartPos, ignoreEndPos)) continue; //nucleus
//                    if (!isInRange(pos, includeStartPos, includeEndPos)) continue; //outside ch

                    if (ignoreBlocks.contains(world.getBlockState(pos).getBlock())) continue;
                    if (processedPositions.contains(pos)) continue;

                    String blockName = getBlockRegistryName(world.getBlockState(pos).getBlock());
                    processedPositions.add(pos);

                    if (blockCountMap.containsKey(blockName)) {
                        blockCountMap.put(blockName, blockCountMap.get(blockName) + 1);
                    } else {
                        blockCountMap.put(blockName, 1);
                    }

                    //Goblin King Tower 385 90 572
                    if (world.getBlockState(pos).getBlock() == Blocks.dark_oak_stairs &&
                            world.getBlockState(new BlockPos(x + 2, y + 0, z + 0)).getBlock() == Blocks.dark_oak_stairs &&
                            world.getBlockState(new BlockPos(x + 2, y + 1, z + 0)).getBlock() == Blocks.dark_oak_stairs &&
                            world.getBlockState(new BlockPos(x + 0, y + 1, z + 0)).getBlock() == Blocks.dark_oak_stairs &&
                            world.getBlockState(new BlockPos(x + 0, y + 2, z + 0)).getBlock() == Blocks.dark_oak_stairs &&
                            world.getBlockState(new BlockPos(x + -1, y + 0, z + 1)).getBlock() == Blocks.dark_oak_stairs &&
                            world.getBlockState(new BlockPos(x + 3, y + 0, z + 1)).getBlock() == Blocks.dark_oak_stairs &&
                            world.getBlockState(new BlockPos(x + 3, y + -1, z + 2)).getBlock() == Blocks.log2 &&
                            world.getBlockState(new BlockPos(x + -1, y + -1, z + 2)).getBlock() == Blocks.log2 &&
                            world.getBlockState(new BlockPos(x + 1, y + -1, z + 0)).getBlock() == Blocks.wool &&
                            world.getBlockState(new BlockPos(x + 2, y + -1, z + 0)).getBlock() == Blocks.wool &&
                            world.getBlockState(new BlockPos(x + 2, y + -1, z + 1)).getBlock() == Blocks.wool &&
                            world.getBlockState(new BlockPos(x + 1, y + -1, z + 1)).getBlock() == Blocks.wool &&
                            world.getBlockState(new BlockPos(x + 0, y + -1, z + 1)).getBlock() == Blocks.wool) {
                        BlockPos pos1 = new BlockPos(x + 1, y + 7, z + 2);
                        blockTextMap.put(pos1, "Goblin King Tower 1");
                        BlockPos pos2 = new BlockPos(x + 1, y + -9, z + 2);
                        blockTextMap.put(pos2, "Goblin King Tower 2");
                    }

/*
/setblock 385 90 572 minecraft:dark_oak_stairs
/setblock 387 90 572 minecraft:dark_oak_stairs
/setblock 387 91 572 minecraft:dark_oak_stairs
/setblock 385 91 572 minecraft:dark_oak_stairs
/setblock 385 92 572 minecraft:dark_oak_stairs
/setblock 384 90 573 minecraft:dark_oak_stairs
/setblock 388 90 573 minecraft:dark_oak_stairs
/setblock 388 89 574 minecraft:log2
/setblock 384 89 574 minecraft:log2
/setblock 386 89 572 minecraft:wool
/setblock 387 89 572 minecraft:wool
/setblock 387 89 573 minecraft:wool
/setblock 386 89 573 minecraft:wool
/setblock 385 89 573 minecraft:wool
/setblock 386 97 574 minecraft:chest
/setblock 386 81 574 minecraft:chest
*/

                    //Queen structure
                    if (world.getBlockState(pos).getBlock() == Blocks.cauldron &&
                            world.getBlockState(new BlockPos(x - 19, y - 1, z - 7)).getBlock() == Blocks.cauldron) {

                        BlockPos pos1 = new BlockPos(x - 10, y + 1, z + 6);
                        BlockPos pos2 = new BlockPos(x - 40, y - 18, z + 29);

                        blockTextMap.put(pos1, "Goblin Queen's Den 1");
                        blockTextMap.put(pos2, "Goblin Queen's Den 2");
                    }

                    //Goblin Sewer Camp
                    if (world.getBlockState(pos).getBlock() == Blocks.iron_bars &&
                            world.getBlockState(new BlockPos(x, y + 1, z)).getBlock() == Blocks.iron_bars &&
                            world.getBlockState(new BlockPos(x - 1, y, z)).getBlock() == Blocks.iron_bars &&
                            world.getBlockState(new BlockPos(x - 1, y + 1, z)).getBlock() == Blocks.iron_bars &&
                            world.getBlockState(new BlockPos(x, y - 1, z)).getBlock() == Blocks.soul_sand &&
                            world.getBlockState(new BlockPos(x - 1, y - 1, z)).getBlock() == Blocks.soul_sand &&
                            world.getBlockState(new BlockPos(x - 2, y, z)).getBlock() == Blocks.stonebrick &&
                            world.getBlockState(new BlockPos(x - 2, y + 1, z)).getBlock() == Blocks.stonebrick) {

                        BlockPos pos1 = new BlockPos(x + 2, y, z + 1);
                        blockTextMap.put(pos1, "Goblin Sewer Camp 1");

                    }

                    //Deep Goblin Lair
                    if (world.getBlockState(pos).getBlock() == Blocks.trapdoor && // 402 103 731
                            world.getBlockState(new BlockPos(x, y + 1, z)).getBlock() == Blocks.planks &&
                            world.getBlockState(new BlockPos(x, y + 2, z)).getBlock() == Blocks.planks &&
                            world.getBlockState(new BlockPos(x, y + 1, z - 1)).getBlock() == Blocks.planks &&
                            world.getBlockState(new BlockPos(x, y + 1, z + 1)).getBlock() == Blocks.planks &&
                            world.getBlockState(new BlockPos(x, y + 2, z - 1)).getBlock() == Blocks.dark_oak_stairs &&
                            world.getBlockState(new BlockPos(x, y + 2, z + 1)).getBlock() == Blocks.dark_oak_stairs &&
                            world.getBlockState(new BlockPos(x, y, z - 1)).getBlock() == Blocks.dark_oak_stairs &&
                            world.getBlockState(new BlockPos(x, y, z + 1)).getBlock() == Blocks.dark_oak_stairs) {

                        BlockPos pos1 = new BlockPos(x - 42, y + 54, z - 23); // 360 157 708
                        BlockPos pos2 = new BlockPos(x, y - 2, z + 6); //402 101 737
                        BlockPos pos3 = new BlockPos(x - 25, y - 17, z - 35); //377 86 696

                        blockTextMap.put(pos1, "Deep Goblin Lair 1");
                        blockTextMap.put(pos2, "Deep Goblin Lair 2");
                        blockTextMap.put(pos3, "Deep Goblin Lair 3");
                    }

                    //Tiny Hut
                    if ((world.getBlockState(pos).getBlock() == Blocks.daylight_detector || world.getBlockState(pos).getBlock() == Blocks.daylight_detector_inverted) &&
                            world.getBlockState(new BlockPos(x + 7, y + 1, z + 4)).getBlock() == Blocks.brick_stairs &&
                            world.getBlockState(new BlockPos(x + 7, y - 1, z + 4)).getBlock() == Blocks.brick_stairs) {

                        BlockPos pos1 = new BlockPos(x - 5, y - 1, z - 1);
                        blockTextMap.put(pos1, "Tiny Hut 1");
                    }

                    //Dragon Skull
                    if (world.getBlockState(pos).getBlock() == Blocks.glowstone &&
                            world.getBlockState(new BlockPos(x, y + 1, z)).getBlock() == Blocks.carpet &&
                            world.getBlockState(new BlockPos(x - 5, y + 1, z)).getBlock() == Blocks.quartz_stairs &&
                            world.getBlockState(new BlockPos(x - 6, y + 1, z)).getBlock() == Blocks.quartz_block &&
                            world.getBlockState(new BlockPos(x - 6, y + 2, z)).getBlock() == Blocks.stone_slab) {

                        BlockPos pos1 = new BlockPos(x - 4, y + 1, z);
                        BlockPos pos2 = new BlockPos(x - 9, y - 6, z - 5);

                        blockTextMap.put(pos1, "Dragon Skull 1");
                        blockTextMap.put(pos2, "Dragon Skull 2");
                    }

                    //Precursor Trapped Stair

                    if (world.getBlockState(pos).getBlock() == Blocks.spruce_fence &&
                            world.getBlockState(new BlockPos(x, y + 1, z)).getBlock() == Blocks.torch &&
                            world.getBlockState(new BlockPos(x + 1, y, z + 9)).getBlock() == Blocks.dispenser &&
                            world.getBlockState(new BlockPos(x + 1, y + 2, z + 9)).getBlock() == Blocks.dispenser &&
                            world.getBlockState(new BlockPos(x, y + 1, z + 9)).getBlock() == Blocks.dispenser) {

                        BlockPos pos1 = new BlockPos(x + 1, y + 1, z - 3);
                        BlockPos pos2 = new BlockPos(x - 48, y + 34, z - 7);
                        BlockPos pos3 = new BlockPos(x - 37, y + 52, z + 33);

                        blockTextMap.put(pos1, "Precursor Trapped Stair 1");
                        blockTextMap.put(pos2, "Precursor Trapped Stair 2");
                        blockTextMap.put(pos3, "Precursor Trapped Stair 3");
                    }


                    //Precursor Diorite Corridor (FIX THIS)
                    if (world.getBlockState(pos).getBlock() == Blocks.wooden_slab &&
                            world.getBlockState(new BlockPos(x, y, z + 2)).getBlock() == Blocks.double_stone_slab &&
                            world.getBlockState(new BlockPos(x, y, z - 1)).getBlock() == Blocks.wooden_slab &&
                            world.getBlockState(new BlockPos(x, y - 1, z + 1)).getBlock() == Blocks.planks &&
                            world.getBlockState(new BlockPos(x + 1, y - 1, z)).getBlock() == Blocks.planks &&
                            world.getBlockState(new BlockPos(x + 1, y - 1, z - 1)).getBlock() == Blocks.planks &&
                            world.getBlockState(new BlockPos(x + 2, y - 1, z - 2)).getBlock() == Blocks.planks) {

                        BlockPos pos1 = new BlockPos(x + 5, y, z + 1);

                        blockTextMap.put(pos1, "Precursor Diorite Corridor 1");
                    }

                    //Underground Office
                    if (world.getBlockState(pos).getBlock() == Blocks.lever &&
                            world.getBlockState(new BlockPos(x, y, z - 3)).getBlock() == Blocks.lever &&
                            world.getBlockState(new BlockPos(x, y - 1, z)).getBlock() == Blocks.spruce_stairs &&
                            world.getBlockState(new BlockPos(x, y - 1, z - 1)).getBlock() == Blocks.log2 &&
                            world.getBlockState(new BlockPos(x - 1, y - 1, z - 1)).getBlock() == Blocks.tripwire_hook &&
                            world.getBlockState(new BlockPos(x + 1, y - 1, z - 1)).getBlock() == Blocks.tripwire_hook &&
                            world.getBlockState(new BlockPos(x, y - 1, z - 2)).getBlock() == Blocks.log2 &&
                            world.getBlockState(new BlockPos(x - 1, y - 1, z - 2)).getBlock() == Blocks.tripwire_hook &&
                            world.getBlockState(new BlockPos(x + 1, y - 1, z - 2)).getBlock() == Blocks.tripwire_hook &&
                            world.getBlockState(new BlockPos(x, y - 1, z - 3)).getBlock() == Blocks.spruce_stairs) {

                        BlockPos pos1 = new BlockPos(x, y - 1, z + 2);
                        BlockPos pos2 = new BlockPos(x - 15, y, z + 5);
                        blockTextMap.put(pos1, "Underground Office 1");
                        blockTextMap.put(pos2, "Underground Office 2");

                    }

                    //Precursor Throne Hall

                    if (world.getBlockState(pos).getBlock() == Blocks.cobblestone_wall &&
                            world.getBlockState(new BlockPos(x + 2, y, z)).getBlock() == Blocks.cobblestone_wall &&
                            world.getBlockState(new BlockPos(x, y, z + 1)).getBlock() == Blocks.cobblestone_wall &&
                            world.getBlockState(new BlockPos(x + 2, y, z + 1)).getBlock() == Blocks.cobblestone_wall &&
                            world.getBlockState(new BlockPos(x + 2, y, z + 2)).getBlock() == Blocks.double_stone_slab &&
                            world.getBlockState(new BlockPos(x + 2, y + 1, z + 2)).getBlock() == Blocks.double_stone_slab &&
                            world.getBlockState(new BlockPos(x + 1, y, z + 2)).getBlock() == Blocks.double_stone_slab &&
                            world.getBlockState(new BlockPos(x + 1, y + 1, z + 2)).getBlock() == Blocks.double_stone_slab &&
                            world.getBlockState(new BlockPos(x + 1, y + 2, z + 2)).getBlock() == Blocks.double_stone_slab &&
                            world.getBlockState(new BlockPos(x + 1, y + 3, z + 2)).getBlock() == Blocks.stone_slab &&
                            world.getBlockState(new BlockPos(x, y, z + 2)).getBlock() == Blocks.double_stone_slab &&
                            world.getBlockState(new BlockPos(x, y + 1, z + 2)).getBlock() == Blocks.double_stone_slab &&
                            world.getBlockState(new BlockPos(x, y - 1, z)).getBlock() == Blocks.stone_slab &&
                            world.getBlockState(new BlockPos(x + 1, y - 1, z)).getBlock() == Blocks.stone_slab &&
                            world.getBlockState(new BlockPos(x + 2, y - 1, z)).getBlock() == Blocks.stone_slab &&
                            world.getBlockState(new BlockPos(x, y - 1, z + 1)).getBlock() == Blocks.stone_stairs &&
                            world.getBlockState(new BlockPos(x + 1, y - 1, z + 1)).getBlock() == Blocks.double_stone_slab &&
                            world.getBlockState(new BlockPos(x + 2, y - 1, z + 1)).getBlock() == Blocks.stone_stairs) {

                        BlockPos pos1 = new BlockPos(x - 25, y - 32, z - 30);
                        BlockPos pos2 = new BlockPos(x + 4, y - 3, z - 43);
                        blockTextMap.put(pos1, "Precursor Throne Hall 1");
                        blockTextMap.put(pos2, "Precursor Throne Hall 2");

                    }

                    //Using Generator.py

                    //Ruins
                    if (world.getBlockState(pos).getBlock() == Blocks.cauldron &&
                            world.getBlockState(new BlockPos(x, y, z + -7)).getBlock() == Blocks.cauldron &&
                            world.getBlockState(new BlockPos(x + -6, y + -1, z + -12)).getBlock() == Blocks.cauldron) {
                        BlockPos pos1 = new BlockPos(x + -6, y + -1, z + 5);
                        blockTextMap.put(pos1, "Ruins 1");
                        BlockPos pos2 = new BlockPos(x + -16, y + 28, z + 5);
                        blockTextMap.put(pos2, "Ruins 2");
                    }

                    //Precursor Tall Pillars 659 130 578
                    if (world.getBlockState(pos).getBlock() == Blocks.sea_lantern &&
                            world.getBlockState(new BlockPos(x + 2, y + -1, z + 13)).getBlock() == Blocks.sea_lantern &&
                            world.getBlockState(new BlockPos(x + -20, y + 6, z + 33)).getBlock() == Blocks.sea_lantern &&
                            world.getBlockState(new BlockPos(x + -1, y + 8, z + 42)).getBlock() == Blocks.sea_lantern) {
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
                            world.getBlockState(new BlockPos(x + 0, y + 3, z + 2)).getBlock() == Blocks.quartz_block) {
                        BlockPos pos1 = new BlockPos(x + 6, y + -1, z + 2);
                        blockTextMap.put(pos1, "Colored Skull Puzzle 1");
                    }

                    //Corleone Lakefront 624 98 455
                    if (world.getBlockState(pos).getBlock() == Blocks.iron_bars &&
                            world.getBlockState(new BlockPos(x + 0, y + 1, z + 0)).getBlock() == Blocks.cobblestone_wall &&
                            world.getBlockState(new BlockPos(x + 0, y + -1, z + 0)).getBlock() == Blocks.cobblestone_wall &&
                            world.getBlockState(new BlockPos(x + -4, y + 8, z + 0)).getBlock() == Blocks.cobblestone_wall &&
                            world.getBlockState(new BlockPos(x + -4, y + 7, z + 2)).getBlock() == Blocks.log &&
                            world.getBlockState(new BlockPos(x + -4, y + 7, z + -2)).getBlock() == Blocks.log) {
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
                            world.getBlockState(new BlockPos(x + 9, y + 0, z + 4)).getBlock() == Blocks.cauldron) {
                        BlockPos pos1 = new BlockPos(x + 13, y + 0, z + 1);
                        blockTextMap.put(pos1, "Colosseum 1");
                    }

                    //Crystal Train 668 124 515
                    if (world.getBlockState(pos).getBlock() == Blocks.dark_oak_fence_gate &&
                            world.getBlockState(new BlockPos(x + 7, y + 0, z + 0)).getBlock() == Blocks.dark_oak_fence_gate &&
                            world.getBlockState(new BlockPos(x + 7, y + -2, z + 0)).getBlock() == Blocks.wooden_slab &&
                            world.getBlockState(new BlockPos(x + 8, y + -2, z + 0)).getBlock() == Blocks.carpet) {
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
                            world.getBlockState(new BlockPos(x + -3, y + 1, z + 1)).getBlock() == Blocks.tripwire_hook) {
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
                            world.getBlockState(new BlockPos(x + 2, y + 0, z + -7)).getBlock() == Blocks.cobblestone_wall) {
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
                            world.getBlockState(new BlockPos(x + 3, y + 0, z + 0)).getBlock() == Blocks.iron_bars) {
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
                            world.getBlockState(new BlockPos(x + 3, y + 3, z + 10)).getBlock() == Blocks.cauldron) {
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

                    //precursor Colosseum (NOT ON WIKI) 562 122 683
                    if (world.getBlockState(pos).getBlock() == Blocks.hopper &&
                            world.getBlockState(new BlockPos(x + -6, y + 0, z + 0)).getBlock() == Blocks.hopper &&
                            world.getBlockState(new BlockPos(x + 0, y + -3, z + 0)).getBlock() == Blocks.stone_brick_stairs &&
                            world.getBlockState(new BlockPos(x + -6, y + -3, z + 0)).getBlock() == Blocks.stone_brick_stairs &&
                            world.getBlockState(new BlockPos(x + -16, y + 0, z + -10)).getBlock() == Blocks.hopper &&
                            world.getBlockState(new BlockPos(x + -16, y + 0, z + -16)).getBlock() == Blocks.hopper &&
                            world.getBlockState(new BlockPos(x + -16, y + -3, z + -10)).getBlock() == Blocks.stone_brick_stairs &&
                            world.getBlockState(new BlockPos(x + -16, y + -3, z + -16)).getBlock() == Blocks.stone_brick_stairs &&
                            world.getBlockState(new BlockPos(x + 0, y + -4, z + -2)).getBlock() == Blocks.stone_brick_stairs &&
                            world.getBlockState(new BlockPos(x + -3, y + -4, z + -2)).getBlock() == Blocks.stone_brick_stairs &&
                            world.getBlockState(new BlockPos(x + -4, y + -4, z + -2)).getBlock() == Blocks.stone_brick_stairs) {
                        BlockPos pos1 = new BlockPos(x + -19, y + -3, z + -13);
                        blockTextMap.put(pos1, "PRECURSOR COLISEUM 1");
                        BlockPos pos2 = new BlockPos(x + -3, y + -3, z + 3);
                        blockTextMap.put(pos2, "PRECURSOR COLISEUM 2");
                    }
/*
/setblock 562 122 683 minecraft:hopper
/setblock 556 122 683 minecraft:hopper
/setblock 562 119 683 minecraft:stone_brick_stairs
/setblock 556 119 683 minecraft:stone_brick_stairs
/setblock 546 122 673 minecraft:hopper
/setblock 546 122 667 minecraft:hopper
/setblock 546 119 673 minecraft:stone_brick_stairs
/setblock 546 119 667 minecraft:stone_brick_stairs
/setblock 562 118 681 minecraft:stone_brick_stairs
/setblock 559 118 681 minecraft:stone_brick_stairs
/setblock 558 118 681 minecraft:stone_brick_stairs
/setblock 543 119 670 minecraft:chest
/setblock 559 119 686 minecraft:chest
*/

                    //Precursor Pitfall Corridor 595 155 606
                    if (world.getBlockState(pos).getBlock() == Blocks.sea_lantern &&
                            world.getBlockState(new BlockPos(x + -1, y + -1, z + 0)).getBlock() == Blocks.sea_lantern &&
                            world.getBlockState(new BlockPos(x + -2, y + 2, z + -21)).getBlock() == Blocks.cobblestone_wall &&
                            world.getBlockState(new BlockPos(x + -2, y + 3, z + -21)).getBlock() == Blocks.cobblestone_wall &&
                            world.getBlockState(new BlockPos(x + -1, y + 1, z + -21)).getBlock() == Blocks.stone_brick_stairs &&
                            world.getBlockState(new BlockPos(x + 0, y + 2, z + -21)).getBlock() == Blocks.cobblestone_wall &&
                            world.getBlockState(new BlockPos(x + 0, y + 3, z + -21)).getBlock() == Blocks.cobblestone_wall &&
                            world.getBlockState(new BlockPos(x + 7, y + -3, z + -10)).getBlock() == Blocks.dispenser &&
                            world.getBlockState(new BlockPos(x + 9, y + -3, z + -22)).getBlock() == Blocks.dispenser) {
                        BlockPos pos1 = new BlockPos(x + 8, y + -1, z + -5);
                        blockTextMap.put(pos1, "Precursor Pitfall Corridor 1");
                    }

/*
/setblock 595 155 606 minecraft:sea_lantern
/setblock 594 154 606 minecraft:sea_lantern
/setblock 593 157 585 minecraft:cobblestone_wall
/setblock 593 158 585 minecraft:cobblestone_wall
/setblock 594 156 585 minecraft:stone_brick_stairs
/setblock 595 157 585 minecraft:cobblestone_wall
/setblock 595 158 585 minecraft:cobblestone_wall
/setblock 602 152 596 minecraft:dispenser
/setblock 604 152 584 minecraft:dispenser
/setblock 603 154 601 minecraft:chest
*/

                    //Abandoned Lift 750 172 766
                    if (world.getBlockState(pos).getBlock() == Blocks.wooden_slab &&
                            world.getBlockState(new BlockPos(x + 0, y + 0, z + 1)).getBlock() == Blocks.double_wooden_slab &&
                            world.getBlockState(new BlockPos(x + 0, y + 0, z + 2)).getBlock() == Blocks.wooden_slab &&
                            world.getBlockState(new BlockPos(x + -1, y + -4, z + 2)).getBlock() == Blocks.wooden_slab &&
                            world.getBlockState(new BlockPos(x + -1, y + -4, z + 1)).getBlock() == Blocks.wooden_slab &&
                            world.getBlockState(new BlockPos(x + -1, y + -4, z + 0)).getBlock() == Blocks.wooden_slab &&
                            world.getBlockState(new BlockPos(x + -2, y + -4, z + 2)).getBlock() == Blocks.log &&
                            world.getBlockState(new BlockPos(x + -2, y + -4, z + 1)).getBlock() == Blocks.log &&
                            world.getBlockState(new BlockPos(x + -2, y + -4, z + 0)).getBlock() == Blocks.log &&
                            world.getBlockState(new BlockPos(x + 2, y + 6, z + -2)).getBlock() == Blocks.stone_brick_stairs &&
                            world.getBlockState(new BlockPos(x + 2, y + 6, z + -1)).getBlock() == Blocks.stone &&
                            world.getBlockState(new BlockPos(x + 2, y + 6, z + 0)).getBlock() == Blocks.stone_brick_stairs &&
                            world.getBlockState(new BlockPos(x + 2, y + 8, z + -2)).getBlock() == Blocks.stone_brick_stairs &&
                            world.getBlockState(new BlockPos(x + 2, y + 8, z + -1)).getBlock() == Blocks.stone &&
                            world.getBlockState(new BlockPos(x + 2, y + 8, z + 0)).getBlock() == Blocks.stone_brick_stairs) {
                        BlockPos pos1 = new BlockPos(x + -1, y + -30, z + -11);
                        blockTextMap.put(pos1, "Abandoned Lift 1");
                    }

/*
/setblock 750 172 766 minecraft:wooden_slab
/setblock 750 172 767 minecraft:double_wooden_slab
/setblock 750 172 768 minecraft:wooden_slab
/setblock 749 168 768 minecraft:wooden_slab
/setblock 749 168 767 minecraft:wooden_slab
/setblock 749 168 766 minecraft:wooden_slab
/setblock 748 168 768 minecraft:log
/setblock 748 168 767 minecraft:log
/setblock 748 168 766 minecraft:log
/setblock 752 178 764 minecraft:stone_brick_stairs
/setblock 752 178 765 minecraft:stone
/setblock 752 178 766 minecraft:stone_brick_stairs
/setblock 752 180 764 minecraft:stone_brick_stairs
/setblock 752 180 765 minecraft:stone
/setblock 752 180 766 minecraft:stone_brick_stairs
/setblock 749 142 755 minecraft:chest
*/


                    //Spider's Den 561 99 662
                    if (world.getBlockState(pos).getBlock() == Blocks.end_stone &&
                            world.getBlockState(new BlockPos(x + -1, y + 0, z + 0)).getBlock() == Blocks.end_stone &&
                            world.getBlockState(new BlockPos(x + -2, y + -1, z + -1)).getBlock() == Blocks.end_stone &&
                            world.getBlockState(new BlockPos(x + -3, y + -1, z + -1)).getBlock() == Blocks.end_stone &&
                            world.getBlockState(new BlockPos(x + -2, y + -2, z + 0)).getBlock() == Blocks.end_stone &&
                            world.getBlockState(new BlockPos(x + -1, y + -2, z + 0)).getBlock() == Blocks.end_stone &&
                            world.getBlockState(new BlockPos(x + 5, y + -2, z + -8)).getBlock() == Blocks.end_stone &&
                            world.getBlockState(new BlockPos(x + 5, y + -2, z + -9)).getBlock() == Blocks.end_stone &&
                            world.getBlockState(new BlockPos(x + 6, y + -2, z + -8)).getBlock() == Blocks.end_stone &&
                            world.getBlockState(new BlockPos(x + 6, y + 0, z + -8)).getBlock() == Blocks.end_stone &&
                            world.getBlockState(new BlockPos(x + 5, y + 0, z + -8)).getBlock() == Blocks.end_stone &&
                            world.getBlockState(new BlockPos(x + 5, y + 0, z + -9)).getBlock() == Blocks.end_stone) {
                        BlockPos pos1 = new BlockPos(x + 1, y + -3, z + -5);
                        blockTextMap.put(pos1, "Spider's Den 1");
                        BlockPos pos2 = new BlockPos(x + -20, y + -24, z + -14);
                        blockTextMap.put(pos2, "Spider's Den 2");
                    }

/*
/setblock 561 99 662 minecraft:end_stone
/setblock 560 99 662 minecraft:end_stone
/setblock 559 98 661 minecraft:end_stone
/setblock 558 98 661 minecraft:end_stone
/setblock 559 97 662 minecraft:end_stone
/setblock 560 97 662 minecraft:end_stone
/setblock 566 97 654 minecraft:end_stone
/setblock 566 97 653 minecraft:end_stone
/setblock 567 97 654 minecraft:end_stone
/setblock 567 99 654 minecraft:end_stone
/setblock 566 99 654 minecraft:end_stone
/setblock 566 99 653 minecraft:end_stone
/setblock 562 96 657 minecraft:chest
/setblock 541 75 648 minecraft:chest
*/

                    //Magma Spiral Cavern 456 50 769
                    if (world.getBlockState(pos).getBlock() == Blocks.stone_slab &&
                            world.getBlockState(new BlockPos(x + 0, y + 0, z + 2)).getBlock() == Blocks.stone_stairs &&
                            world.getBlockState(new BlockPos(x + -10, y + 0, z + 3)).getBlock() == Blocks.stone_slab &&
                            world.getBlockState(new BlockPos(x + 3, y + 0, z + -3)).getBlock() == Blocks.stone_slab &&
                            world.getBlockState(new BlockPos(x + 5, y + 0, z + 2)).getBlock() == Blocks.stone_slab &&
                            world.getBlockState(new BlockPos(x + 5, y + 0, z + 1)).getBlock() == Blocks.stone_slab &&
                            world.getBlockState(new BlockPos(x + 4, y + 0, z + 3)).getBlock() == Blocks.stone_slab &&
                            world.getBlockState(new BlockPos(x + 3, y + 0, z + 5)).getBlock() == Blocks.stone_slab &&
                            world.getBlockState(new BlockPos(x + 4, y + 1, z + 5)).getBlock() == Blocks.stone_slab &&
                            world.getBlockState(new BlockPos(x + -17, y + -1, z + 2)).getBlock() == Blocks.stone_stairs &&
                            world.getBlockState(new BlockPos(x + -18, y + 0, z + 3)).getBlock() == Blocks.stone_slab &&
                            world.getBlockState(new BlockPos(x + -19, y + 0, z + 2)).getBlock() == Blocks.stone_stairs) {
                        BlockPos pos1 = new BlockPos(x + 0, y + 1, z + 1);
                        blockTextMap.put(pos1, "Magma Spiral Cavern 1");
                        BlockPos pos2 = new BlockPos(x + -25, y + 29, z + 4);
                        blockTextMap.put(pos2, "Magma Spiral Cavern 2");
                    }

/*
/setblock 456 50 769 minecraft:stone_slab
/setblock 456 50 771 minecraft:stone_stairs
/setblock 446 50 772 minecraft:stone_slab
/setblock 459 50 766 minecraft:stone_slab
/setblock 461 50 771 minecraft:stone_slab
/setblock 461 50 770 minecraft:stone_slab
/setblock 460 50 772 minecraft:stone_slab
/setblock 459 50 774 minecraft:stone_slab
/setblock 460 51 774 minecraft:stone_slab
/setblock 439 49 771 minecraft:stone_stairs
/setblock 438 50 772 minecraft:stone_slab
/setblock 437 50 771 minecraft:stone_stairs
/setblock 456 51 770 minecraft:chest
/setblock 431 79 773 minecraft:chest
*/

                    //Caravan 688 141 341
                    if (world.getBlockState(pos).getBlock() == Blocks.birch_stairs &&
                            world.getBlockState(new BlockPos(x + 0, y + 0, z + 1)).getBlock() == Blocks.birch_stairs &&
                            world.getBlockState(new BlockPos(x + 1, y + 0, z + 1)).getBlock() == Blocks.birch_stairs &&
                            world.getBlockState(new BlockPos(x + 1, y + 0, z + 0)).getBlock() == Blocks.birch_stairs &&
                            world.getBlockState(new BlockPos(x + 7, y + 7, z + -3)).getBlock() == Blocks.cobblestone_wall &&
                            world.getBlockState(new BlockPos(x + 7, y + 8, z + -3)).getBlock() == Blocks.stone_slab &&
                            world.getBlockState(new BlockPos(x + 6, y + 2, z + -4)).getBlock() == Blocks.oak_stairs &&
                            world.getBlockState(new BlockPos(x + 6, y + 2, z + -3)).getBlock() == Blocks.wooden_slab &&
                            world.getBlockState(new BlockPos(x + 6, y + 2, z + -2)).getBlock() == Blocks.oak_stairs) {
                        BlockPos pos1 = new BlockPos(x + 6, y + 2, z + -5);
                        blockTextMap.put(pos1, "Caravan 1");
                    }

/*
/setblock 688 141 341 minecraft:birch_stairs
/setblock 688 141 342 minecraft:birch_stairs
/setblock 689 141 342 minecraft:birch_stairs
/setblock 689 141 341 minecraft:birch_stairs
/setblock 695 148 338 minecraft:cobblestone_wall
/setblock 695 149 338 minecraft:stone_slab
/setblock 694 143 337 minecraft:oak_stairs
/setblock 694 143 338 minecraft:wooden_slab
/setblock 694 143 339 minecraft:oak_stairs
/setblock 694 143 336 minecraft:chest
*/


                    //Treasure Deposits 668 142 393
                    if (world.getBlockState(pos).getBlock() == Blocks.hopper &&
                            world.getBlockState(new BlockPos(x + -8, y + 0, z + 0)).getBlock() == Blocks.hopper &&
                            world.getBlockState(new BlockPos(x + -5, y + -3, z + -2)).getBlock() == Blocks.stone_brick_stairs &&
                            world.getBlockState(new BlockPos(x + -5, y + -3, z + -3)).getBlock() == Blocks.stone_brick_stairs &&
                            world.getBlockState(new BlockPos(x + -3, y + -3, z + -3)).getBlock() == Blocks.stone_brick_stairs &&
                            world.getBlockState(new BlockPos(x + -3, y + -3, z + -2)).getBlock() == Blocks.stone_brick_stairs &&
                            world.getBlockState(new BlockPos(x + -4, y + -4, z + -3)).getBlock() == Blocks.double_stone_slab &&
                            world.getBlockState(new BlockPos(x + -4, y + -4, z + -2)).getBlock() == Blocks.double_stone_slab &&
                            world.getBlockState(new BlockPos(x + -7, y + -6, z + -2)).getBlock() == Blocks.stone_brick_stairs &&
                            world.getBlockState(new BlockPos(x + -6, y + -7, z + -2)).getBlock() == Blocks.stone_brick_stairs &&
                            world.getBlockState(new BlockPos(x + -5, y + -7, z + -2)).getBlock() == Blocks.stone_slab &&
                            world.getBlockState(new BlockPos(x + -4, y + -7, z + -2)).getBlock() == Blocks.stone_slab &&
                            world.getBlockState(new BlockPos(x + -3, y + -7, z + -2)).getBlock() == Blocks.stone_slab &&
                            world.getBlockState(new BlockPos(x + -2, y + -7, z + -2)).getBlock() == Blocks.stone_brick_stairs &&
                            world.getBlockState(new BlockPos(x + -1, y + -6, z + -2)).getBlock() == Blocks.stone_brick_stairs) {
                        BlockPos pos1 = new BlockPos(x + -7, y + -9, z + 13);
                        blockTextMap.put(pos1, "Treasure Deposits 1");
                        BlockPos pos2 = new BlockPos(x + -3, y + -9, z + 4);
                        blockTextMap.put(pos2, "Treasure Deposits 2");
                    }

/*
/setblock 668 142 393 minecraft:hopper
/setblock 660 142 393 minecraft:hopper
/setblock 663 139 391 minecraft:stone_brick_stairs
/setblock 663 139 390 minecraft:stone_brick_stairs
/setblock 665 139 390 minecraft:stone_brick_stairs
/setblock 665 139 391 minecraft:stone_brick_stairs
/setblock 664 138 390 minecraft:double_stone_slab
/setblock 664 138 391 minecraft:double_stone_slab
/setblock 661 136 391 minecraft:stone_brick_stairs
/setblock 662 135 391 minecraft:stone_brick_stairs
/setblock 663 135 391 minecraft:stone_slab
/setblock 664 135 391 minecraft:stone_slab
/setblock 665 135 391 minecraft:stone_slab
/setblock 666 135 391 minecraft:stone_brick_stairs
/setblock 667 136 391 minecraft:stone_brick_stairs
/setblock 661 133 406 minecraft:chest
/setblock 665 133 397 minecraft:chest
*/


                    //Sludge Cavern 520 76 352
                    if (world.getBlockState(pos).getBlock() == Blocks.glowstone &&
                            world.getBlockState(new BlockPos(x + -7, y + 2, z + -8)).getBlock() == Blocks.glowstone &&
                            world.getBlockState(new BlockPos(x + -3, y + 9, z + -4)).getBlock() == Blocks.glowstone &&
                            world.getBlockState(new BlockPos(x + 0, y + 7, z + -11)).getBlock() == Blocks.glowstone) {
                        BlockPos pos1 = new BlockPos(x + 1, y + -4, z + -8);
                        blockTextMap.put(pos1, "Sludge Cavern 1");
                        BlockPos pos2 = new BlockPos(x + -20, y + 14, z + -17);
                        blockTextMap.put(pos2, "Sludge Cavern 2");
                    }

/*
/setblock 520 76 352 minecraft:glowstone
/setblock 513 78 344 minecraft:glowstone
/setblock 517 85 348 minecraft:glowstone
/setblock 520 83 341 minecraft:glowstone
/setblock 521 72 344 minecraft:chest
/setblock 500 90 335 minecraft:chest
*/


                    //Jungle Lagoon Spiral 395 131 474
                    if (world.getBlockState(pos).getBlock() == Blocks.glowstone &&
                            world.getBlockState(new BlockPos(x + -17, y + -10, z + -4)).getBlock() == Blocks.glowstone &&
                            world.getBlockState(new BlockPos(x + 2, y + -32, z + 5)).getBlock() == Blocks.glowstone) {
                        BlockPos pos1 = new BlockPos(x + 0, y + 1, z + 0);
                        blockTextMap.put(pos1, "Jungle Lagoon Spiral 1");
                        BlockPos pos2 = new BlockPos(x + -17, y + -9, z + -4);
                        blockTextMap.put(pos2, "Jungle Lagoon Spiral 2");
                        BlockPos pos3 = new BlockPos(x + 2, y + -31, z + 5);
                        blockTextMap.put(pos3, "Jungle Lagoon Spiral 3");
                    }

/*
/setblock 395 131 474 minecraft:glowstone
/setblock 378 121 470 minecraft:glowstone
/setblock 397 99 479 minecraft:glowstone
/setblock 395 132 474 minecraft:chest
/setblock 378 122 470 minecraft:chest
/setblock 397 100 479 minecraft:chest
*/

                    //Aqueduct 296 74 501
                    if (world.getBlockState(pos).getBlock() == Blocks.stone_brick_stairs &&
                            world.getBlockState(new BlockPos(x + 0, y + 0, z + 1)).getBlock() == Blocks.stone_brick_stairs &&
                            world.getBlockState(new BlockPos(x + -1, y + 0, z + 1)).getBlock() == Blocks.stone_brick_stairs &&
                            world.getBlockState(new BlockPos(x + -1, y + 0, z + 0)).getBlock() == Blocks.stone_brick_stairs &&
                            world.getBlockState(new BlockPos(x + 0, y + 0, z + 8)).getBlock() == Blocks.stone_brick_stairs &&
                            world.getBlockState(new BlockPos(x + -1, y + 0, z + 8)).getBlock() == Blocks.stone_brick_stairs &&
                            world.getBlockState(new BlockPos(x + 0, y + 0, z + 7)).getBlock() == Blocks.stone_brick_stairs &&
                            world.getBlockState(new BlockPos(x + -1, y + 0, z + 7)).getBlock() == Blocks.stone_brick_stairs &&
                            world.getBlockState(new BlockPos(x + -9, y + 4, z + 8)).getBlock() == Blocks.cobblestone_wall &&
                            world.getBlockState(new BlockPos(x + -9, y + 5, z + 8)).getBlock() == Blocks.cobblestone_wall &&
                            world.getBlockState(new BlockPos(x + -9, y + 2, z + 2)).getBlock() == Blocks.cobblestone_wall &&
                            world.getBlockState(new BlockPos(x + -9, y + 3, z + 2)).getBlock() == Blocks.cobblestone_wall &&
                            world.getBlockState(new BlockPos(x + -9, y + 4, z + 2)).getBlock() == Blocks.cobblestone_wall &&
                            world.getBlockState(new BlockPos(x + -9, y + 6, z + 5)).getBlock() == Blocks.stone_slab &&
                            world.getBlockState(new BlockPos(x + -9, y + 6, z + 6)).getBlock() == Blocks.stone_slab &&
                            world.getBlockState(new BlockPos(x + -9, y + 6, z + 7)).getBlock() == Blocks.stone_slab &&
                            world.getBlockState(new BlockPos(x + -9, y + 6, z + 4)).getBlock() == Blocks.stone_slab &&
                            world.getBlockState(new BlockPos(x + -9, y + 6, z + 3)).getBlock() == Blocks.stone_slab) {
                        BlockPos pos1 = new BlockPos(x + 0, y + 0, z + 4);
                        blockTextMap.put(pos1, "Aqueduct 1");
                    }

/*
/setblock 296 74 501 minecraft:stone_brick_stairs
/setblock 296 74 502 minecraft:stone_brick_stairs
/setblock 295 74 502 minecraft:stone_brick_stairs
/setblock 295 74 501 minecraft:stone_brick_stairs
/setblock 296 74 509 minecraft:stone_brick_stairs
/setblock 295 74 509 minecraft:stone_brick_stairs
/setblock 296 74 508 minecraft:stone_brick_stairs
/setblock 295 74 508 minecraft:stone_brick_stairs
/setblock 287 78 509 minecraft:cobblestone_wall
/setblock 287 79 509 minecraft:cobblestone_wall
/setblock 287 76 503 minecraft:cobblestone_wall
/setblock 287 77 503 minecraft:cobblestone_wall
/setblock 287 78 503 minecraft:cobblestone_wall
/setblock 287 80 506 minecraft:stone_slab
/setblock 287 80 507 minecraft:stone_slab
/setblock 287 80 508 minecraft:stone_slab
/setblock 287 80 505 minecraft:stone_slab
/setblock 287 80 504 minecraft:stone_slab
/setblock 296 74 505 minecraft:chest
*/

                    //Magma Lavafalls 240 62 398
                    if (world.getBlockState(pos).getBlock() == Blocks.stone_stairs &&
                            world.getBlockState(new BlockPos(x + 0, y + 0, z + -1)).getBlock() == Blocks.stone_stairs &&
                            world.getBlockState(new BlockPos(x + 1, y + -1, z + 0)).getBlock() == Blocks.stone_slab &&
                            world.getBlockState(new BlockPos(x + 2, y + -1, z + -1)).getBlock() == Blocks.stone_slab &&
                            world.getBlockState(new BlockPos(x + 3, y + -1, z + -1)).getBlock() == Blocks.stone_slab &&
                            world.getBlockState(new BlockPos(x + 0, y + 0, z + -3)).getBlock() == Blocks.stone_stairs &&
                            world.getBlockState(new BlockPos(x + 1, y + 0, z + -4)).getBlock() == Blocks.stone_stairs &&
                            world.getBlockState(new BlockPos(x + 3, y + 0, z + -4)).getBlock() == Blocks.stone_slab &&
                            world.getBlockState(new BlockPos(x + 4, y + 0, z + -3)).getBlock() == Blocks.stone_slab) {
                        BlockPos pos1 = new BlockPos(x + 2, y + 0, z + -4);
                        blockTextMap.put(pos1, "Magma Lavafalls 1");
                        BlockPos pos2 = new BlockPos(x + 22, y + 3, z + -3);
                        blockTextMap.put(pos2, "Magma Lavafalls 2");
                    }

/*
/setblock 240 62 398 minecraft:stone_stairs
/setblock 240 62 397 minecraft:stone_stairs
/setblock 241 61 398 minecraft:stone_slab
/setblock 242 61 397 minecraft:stone_slab
/setblock 243 61 397 minecraft:stone_slab
/setblock 240 62 395 minecraft:stone_stairs
/setblock 241 62 394 minecraft:stone_stairs
/setblock 243 62 394 minecraft:stone_slab
/setblock 244 62 395 minecraft:stone_slab
/setblock 242 62 394 minecraft:chest
/setblock 262 65 395 minecraft:chest
*/


                    //Underground Spring 569 117 436
                    if (world.getBlockState(pos).getBlock() == Blocks.sea_lantern &&
                            world.getBlockState(new BlockPos(x + 5, y + 0, z + -2)).getBlock() == Blocks.sea_lantern &&
                            world.getBlockState(new BlockPos(x + 14, y + 0, z + -4)).getBlock() == Blocks.sea_lantern &&
                            world.getBlockState(new BlockPos(x + 9, y + 0, z + 3)).getBlock() == Blocks.sea_lantern) {
                        BlockPos pos1 = new BlockPos(x + 6, y + 0, z + 2);
                        blockTextMap.put(pos1, "Underground Spring 1");
                    }

/*
/setblock 569 117 436 minecraft:sea_lantern
/setblock 574 117 434 minecraft:sea_lantern
/setblock 583 117 432 minecraft:sea_lantern
/setblock 578 117 439 minecraft:sea_lantern
/setblock 575 117 438 minecraft:chest
*/

                    //Trapped Throne 799 97 392
                    if (world.getBlockState(pos).getBlock() == Blocks.anvil &&
                            world.getBlockState(new BlockPos(x + 0, y + 1, z + 0)).getBlock() == Blocks.cauldron &&
                            world.getBlockState(new BlockPos(x + 0, y + 0, z + -6)).getBlock() == Blocks.anvil &&
                            world.getBlockState(new BlockPos(x + 0, y + 1, z + -6)).getBlock() == Blocks.cauldron &&
                            world.getBlockState(new BlockPos(x + -9, y + 0, z + -4)).getBlock() == Blocks.planks &&
                            world.getBlockState(new BlockPos(x + -9, y + 0, z + -3)).getBlock() == Blocks.planks &&
                            world.getBlockState(new BlockPos(x + -9, y + 0, z + -2)).getBlock() == Blocks.planks &&
                            world.getBlockState(new BlockPos(x + -9, y + 0, z + 1)).getBlock() == Blocks.anvil &&
                            world.getBlockState(new BlockPos(x + -9, y + 1, z + 1)).getBlock() == Blocks.cauldron &&
                            world.getBlockState(new BlockPos(x + -9, y + 0, z + -7)).getBlock() == Blocks.anvil &&
                            world.getBlockState(new BlockPos(x + -9, y + 1, z + -7)).getBlock() == Blocks.cauldron) {
                        BlockPos pos1 = new BlockPos(x + -8, y + 2, z + -3);
                        blockTextMap.put(pos1, "Trapped Throne 1");
                    }

/*
/setblock 799 97 392 minecraft:anvil
/setblock 799 98 392 minecraft:cauldron
/setblock 799 97 386 minecraft:anvil
/setblock 799 98 386 minecraft:cauldron
/setblock 790 97 388 minecraft:planks
/setblock 790 97 389 minecraft:planks
/setblock 790 97 390 minecraft:planks
/setblock 790 97 393 minecraft:anvil
/setblock 790 98 393 minecraft:cauldron
/setblock 790 97 385 minecraft:anvil
/setblock 790 98 385 minecraft:cauldron
/setblock 791 99 389 minecraft:chest
*/
//Water Hall 574 122 418
                    if (world.getBlockState(pos).getBlock() == Blocks.sea_lantern &&
                            world.getBlockState(new BlockPos(x + 0, y + 0, z + 1)).getBlock() == Blocks.sea_lantern &&
                            world.getBlockState(new BlockPos(x + 0, y + 1, z + 1)).getBlock() == Blocks.carpet &&
                            world.getBlockState(new BlockPos(x + 0, y + 1, z + 0)).getBlock() == Blocks.carpet &&
                            world.getBlockState(new BlockPos(x + -6, y + 0, z + 1)).getBlock() == Blocks.sea_lantern &&
                            world.getBlockState(new BlockPos(x + -6, y + 0, z + 0)).getBlock() == Blocks.sea_lantern &&
                            world.getBlockState(new BlockPos(x + -6, y + 1, z + 0)).getBlock() == Blocks.carpet &&
                            world.getBlockState(new BlockPos(x + -6, y + 1, z + 1)).getBlock() == Blocks.carpet &&
                            world.getBlockState(new BlockPos(x + -2, y + 1, z + 6)).getBlock() == Blocks.iron_trapdoor &&
                            world.getBlockState(new BlockPos(x + -2, y + 2, z + 6)).getBlock() == Blocks.iron_trapdoor &&
                            world.getBlockState(new BlockPos(x + -4, y + 1, z + 6)).getBlock() == Blocks.iron_trapdoor &&
                            world.getBlockState(new BlockPos(x + -4, y + 2, z + 6)).getBlock() == Blocks.iron_trapdoor) {
                        BlockPos pos1 = new BlockPos(x + -3, y + 1, z + 6);
                        blockTextMap.put(pos1, "Water Hall 1");
                    }

/*
/setblock 574 122 418 minecraft:sea_lantern
/setblock 574 122 419 minecraft:sea_lantern
/setblock 574 123 419 minecraft:carpet
/setblock 574 123 418 minecraft:carpet
/setblock 568 122 419 minecraft:sea_lantern
/setblock 568 122 418 minecraft:sea_lantern
/setblock 568 123 418 minecraft:carpet
/setblock 568 123 419 minecraft:carpet
/setblock 572 123 424 minecraft:iron_trapdoor
/setblock 572 124 424 minecraft:iron_trapdoor
/setblock 570 123 424 minecraft:iron_trapdoor
/setblock 570 124 424 minecraft:iron_trapdoor
/setblock 571 123 424 minecraft:chest
*/
                    //Goblin Wide Pit 233 123 714
                    if (world.getBlockState(pos).getBlock() == Blocks.skull &&
                            world.getBlockState(new BlockPos(x + 4, y + -1, z + 3)).getBlock() == Blocks.skull &&
                            world.getBlockState(new BlockPos(x + 7, y + -2, z + 3)).getBlock() == Blocks.skull &&
                            world.getBlockState(new BlockPos(x + 3, y + -3, z + 3)).getBlock() == Blocks.wooden_slab &&
                            world.getBlockState(new BlockPos(x + 5, y + -3, z + 2)).getBlock() == Blocks.spruce_stairs &&
                            world.getBlockState(new BlockPos(x + 6, y + 2, z + 3)).getBlock() == Blocks.wool) {
                        BlockPos pos1 = new BlockPos(x + 3, y + -2, z + 1);
                        blockTextMap.put(pos1, "Goblin Wide Pit 1");
                    }

/*
/setblock 233 123 714 minecraft:skull
/setblock 237 122 717 minecraft:skull
/setblock 240 121 717 minecraft:skull
/setblock 236 120 717 minecraft:wooden_slab
/setblock 238 120 716 minecraft:spruce_stairs
/setblock 239 125 717 minecraft:wool
/setblock 236 121 715 minecraft:chest
*/

                    //Granite Walkway 802 166 313
                    if (world.getBlockState(pos).getBlock() == Blocks.netherrack &&
                            world.getBlockState(new BlockPos(x + -8, y + 0, z + 0)).getBlock() == Blocks.netherrack &&
                            world.getBlockState(new BlockPos(x + -8, y + 0, z + -6)).getBlock() == Blocks.netherrack &&
                            world.getBlockState(new BlockPos(x + 0, y + 0, z + -6)).getBlock() == Blocks.netherrack &&
                            world.getBlockState(new BlockPos(x + -11, y + -5, z + 0)).getBlock() == Blocks.log &&
                            world.getBlockState(new BlockPos(x + -11, y + -4, z + 0)).getBlock() == Blocks.log &&
                            world.getBlockState(new BlockPos(x + -11, y + -3, z + 0)).getBlock() == Blocks.log) {
                        BlockPos pos1 = new BlockPos(x + -2, y + -5, z + 4);
                        blockTextMap.put(pos1, "Granite Walkway 1");
                        BlockPos pos2 = new BlockPos(x + -4, y + -4, z + 6);
                        blockTextMap.put(pos2, "Granite Walkway 2");
                        BlockPos pos3 = new BlockPos(x + -4, y + 0, z + 8);
                        blockTextMap.put(pos3, "Granite Walkway 3");
                    }

/*
/setblock 802 166 313 minecraft:netherrack
/setblock 794 166 313 minecraft:netherrack
/setblock 794 166 307 minecraft:netherrack
/setblock 802 166 307 minecraft:netherrack
/setblock 791 161 313 minecraft:log
/setblock 791 162 313 minecraft:log
/setblock 791 163 313 minecraft:log
/setblock 800 161 317 minecraft:chest
/setblock 798 162 319 minecraft:chest
/setblock 798 166 321 minecraft:chest
*/

                    //Ruby Bridge 745 169 256
                    if (world.getBlockState(pos).getBlock() == Blocks.jungle_stairs &&
                            world.getBlockState(new BlockPos(x + 1, y + 0, z + 0)).getBlock() == Blocks.jungle_stairs &&
                            world.getBlockState(new BlockPos(x + 6, y + -1, z + -3)).getBlock() == Blocks.spruce_stairs &&
                            world.getBlockState(new BlockPos(x + 6, y + -2, z + -2)).getBlock() == Blocks.spruce_stairs &&
                            world.getBlockState(new BlockPos(x + 7, y + -3, z + 18)).getBlock() == Blocks.log &&
                            world.getBlockState(new BlockPos(x + 6, y + -3, z + 18)).getBlock() == Blocks.log &&
                            world.getBlockState(new BlockPos(x + 5, y + -3, z + 18)).getBlock() == Blocks.log) {
                        BlockPos pos1 = new BlockPos(x + 6, y + -2, z + 18);
                        blockTextMap.put(pos1, "Ruby Bridge 1");
                    }

/*
/setblock 745 169 256 minecraft:jungle_stairs
/setblock 746 169 256 minecraft:jungle_stairs
/setblock 751 168 253 minecraft:spruce_stairs
/setblock 751 167 254 minecraft:spruce_stairs
/setblock 752 166 274 minecraft:log
/setblock 751 166 274 minecraft:log
/setblock 750 166 274 minecraft:log
/setblock 751 167 274 minecraft:chest
*/

                    //Pavilion 566 124 376
                    if (world.getBlockState(pos).getBlock() == Blocks.sea_lantern &&
                            world.getBlockState(new BlockPos(x + 4, y + 2, z + 4)).getBlock() == Blocks.cobblestone_wall &&
                            world.getBlockState(new BlockPos(x + 3, y + 0, z + 2)).getBlock() == Blocks.stone_brick_stairs &&
                            world.getBlockState(new BlockPos(x + 2, y + 0, z + 3)).getBlock() == Blocks.stone_brick_stairs &&
                            world.getBlockState(new BlockPos(x + 1, y + 9, z + 2)).getBlock() == Blocks.leaves) {
                        BlockPos pos1 = new BlockPos(x + 0, y + 1, z + 0);
                        blockTextMap.put(pos1, "Pavilion 1");
                    }

/*
/setblock 566 124 376 minecraft:sea_lantern
/setblock 570 126 380 minecraft:cobblestone_wall
/setblock 569 124 378 minecraft:stone_brick_stairs
/setblock 568 124 379 minecraft:stone_brick_stairs
/setblock 567 133 378 minecraft:leaves
/setblock 566 125 376 minecraft:chest
*/
                }
            }
        }
        ChatComponentText chatMessage = new ChatComponentText("Blocks processed: " + processedPositions.size());
        player.addChatMessage(chatMessage);

        List<Map.Entry<String, Integer>> sortedEntries = new ArrayList<Map.Entry<String, Integer>>(blockCountMap.entrySet());

        // Sort by block counts (values) in descending order
        Collections.sort(sortedEntries, new Comparator<Map.Entry<String, Integer>>() {
            @Override
            public int compare(Map.Entry<String, Integer> entry1, Map.Entry<String, Integer> entry2) {
                return entry1.getValue().compareTo(entry2.getValue()); // Descending order
            }
        });

        for (Map.Entry<String, Integer> entry : sortedEntries) {
            ChatComponentText chatMessage2 = new ChatComponentText(entry.getKey() + ": " + entry.getValue());
            player.addChatMessage(chatMessage2);
        }
    }

    @SubscribeEvent
    public void onRenderWorldLast(RenderWorldLastEvent event) {
        EntityPlayer player = Minecraft.getMinecraft().thePlayer;
        double playerX = player.lastTickPosX + (player.posX - player.lastTickPosX) * event.partialTicks;
        double playerY = player.lastTickPosY + (player.posY - player.lastTickPosY) * event.partialTicks;
        double playerZ = player.lastTickPosZ + (player.posZ - player.lastTickPosZ) * event.partialTicks;

        if (!blockTextMap.isEmpty()) {
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

            for (BlockPos pos : blockTextMap.keySet()) {
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
