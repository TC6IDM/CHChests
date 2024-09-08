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
import net.minecraft.util.*;
import net.minecraft.world.World;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import org.lwjgl.opengl.GL11;

import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class StructureBuilder {
    private TestConfig config;

    public StructureBuilder(TestConfig c) {
        config = c;
    }

    private static class BlockInfo {
        BlockPos pos;
        String blockName;

        BlockInfo(BlockPos pos, String blockName) {
            this.pos = pos;
            this.blockName = blockName;
        }
    }

    private List<BlockInfo> structureBlocks = new ArrayList<BlockInfo>();
    private List<BlockPos> structureChests = new ArrayList<BlockPos>();


    @SubscribeEvent
    public void onKeyInput(InputEvent.KeyInputEvent event) throws IOException {
        if (KeyBindings.addBlock.isPressed()) addBlock();
        if (KeyBindings.done.isPressed()) done();
    }

    private boolean isInRange(BlockPos pos, BlockPos min, BlockPos max) {
        return pos.getX() >= min.getX() && pos.getX() <= max.getX()
                && pos.getY() >= min.getY() && pos.getY() <= max.getY()
                && pos.getZ() >= min.getZ() && pos.getZ() <= max.getZ();
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
        if (block.equals("Blocks.chest")) {
            structureChests.add(pos);
        } else {
            structureBlocks.add(new BlockInfo(pos, block));
        }
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
        FileWriter writer = new FileWriter("C:\\Users\\Owner\\Desktop\\OneConfigTest\\Struct.txt");
        writer.write(fullText.toString());
        writer.write("\n\n");
        writer.write(debugText.toString());
        writer.close();
        structureChests.clear();
        structureBlocks.clear();

    }

    private String getBlockRegistryName(Block block) {
        ResourceLocation registryName = Block.blockRegistry.getNameForObject(block);
        return registryName != null ? registryName.toString().replace("minecraft:", "Blocks.") : "Unknown";
    }
}