package com.example.CHChests;

import jdk.nashorn.internal.ir.Block;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.util.AxisAlignedBB;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class KeyInputHandler {
    private Map<BlockPos, String> blockTextMap = new HashMap<BlockPos, String>();

    @SubscribeEvent
    public void onKeyInput(InputEvent.KeyInputEvent event) {
        if (KeyBindings.clearChests.isPressed()) blockTextMap.clear();
        if(!KeyBindings.findChests.isPressed()) return;

        System.out.println("LOOKING FOR CHESTS");
        EntityPlayer player = Minecraft.getMinecraft().thePlayer;
        World world = player.worldObj;
        //jungle bottom
        BlockPos startPos = new BlockPos(202, 31, 202);
        //precursor top
        BlockPos endPos = new BlockPos(823, 188, 823);

        blockTextMap.clear();

        for (int x = startPos.getX(); x <= endPos.getX(); x++) {
            for (int y = startPos.getY(); y <= endPos.getY(); y++) {
                for (int z = startPos.getZ(); z <= endPos.getZ(); z++) {
                    BlockPos pos = new BlockPos(x, y, z);


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

                    //Corleone Hideout
//                           if (world.getBlockState(pos).getBlock() == Blocks.stonebrick) {
//                               System.out.println("stonebrick found at: " + pos);
//                           }


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