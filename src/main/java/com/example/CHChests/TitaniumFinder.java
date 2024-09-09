package com.example.CHChests;

import com.example.CHChests.config.TestConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentText;
import net.minecraft.world.World;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import org.lwjgl.opengl.GL11;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class TitaniumFinder {
    private TestConfig config;
    private List<BlockPos> titaniumList;
    public List<BlockPos> getTitaniumList() {return titaniumList;}

    public TitaniumFinder(TestConfig c, List<BlockPos> t) {
        config = c;
        titaniumList = t;
    }

    @SubscribeEvent
    public void onKeyInput(InputEvent.KeyInputEvent event) throws IOException {
        if (KeyBindings.findTitanium.isPressed()) findTitanium();
    }

    private void findTitanium() {
        System.out.println("LOOKING FOR TITANIUM");
        EntityPlayer player = Minecraft.getMinecraft().thePlayer;
        World world = player.worldObj;
        titaniumList.clear();
        BlockPos playerPos = player.getPosition();
        int searchRadius = (int) config.TitaniumRadius;
        BlockPos startPos = playerPos.add(-searchRadius, -searchRadius, -searchRadius);
        BlockPos endPos = playerPos.add(searchRadius, searchRadius, searchRadius);

        for (int x = startPos.getX(); x <= endPos.getX(); x++) {
            for (int y = startPos.getY(); y <= endPos.getY(); y++) {
                for (int z = startPos.getZ(); z <= endPos.getZ(); z++) {

                    BlockPos pos = new BlockPos(x, y, z);
                    if (world.getBlockState(pos).getBlock() == Blocks.stone){
                        int metadata = world.getBlockState(pos).getBlock().getMetaFromState(world.getBlockState(pos));
                        if (metadata == 4) {
                            if (isInRange(pos, new BlockPos(108, 182, -29), new BlockPos(150,186, 112)) //walkway to king
                                    || isInRange(pos, new BlockPos(-21, 146, -110), new BlockPos(21, 150,-35)) //forge
                                    || isInRange(pos, new BlockPos(-18, 176, -62), new BlockPos(17, 176, -48))) //staircase
                            {continue;}

                            titaniumList.add(pos);
                            // It's a Smooth Diorite block
                        }
                    }

                }
            }
        }

        ChatComponentText chatMessage = new ChatComponentText("Titanium found: " + titaniumList.size());
        player.addChatMessage(chatMessage);

    }

    private boolean isInRange(BlockPos pos, BlockPos min, BlockPos max) {
        return pos.getX() >= min.getX() && pos.getX() <= max.getX()
                && pos.getY() >= min.getY() && pos.getY() <= max.getY()
                && pos.getZ() >= min.getZ() && pos.getZ() <= max.getZ();
    }


    @SubscribeEvent
    public void onRenderWorldLast(RenderWorldLastEvent event) {
        EntityPlayer player = Minecraft.getMinecraft().thePlayer;
        double playerX = player.lastTickPosX + (player.posX - player.lastTickPosX) * event.partialTicks;
        double playerY = player.lastTickPosY + (player.posY - player.lastTickPosY) * event.partialTicks;
        double playerZ = player.lastTickPosZ + (player.posZ - player.lastTickPosZ) * event.partialTicks;

        if (!titaniumList.isEmpty() && config.DwarvenMinesWaypoints) {
            GL11.glPushMatrix();
            GL11.glTranslated(-playerX, -playerY, -playerZ);
            GL11.glDisable(GL11.GL_TEXTURE_2D);
            GL11.glDisable(GL11.GL_DEPTH_TEST);
            GL11.glEnable(GL11.GL_BLEND);
            GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
            GL11.glLineWidth(2.0F);

            for (BlockPos pos : titaniumList) {
                AxisAlignedBB box = new AxisAlignedBB(pos.getX(), pos.getY(), pos.getZ(), pos.getX() + 1, pos.getY() + 1, pos.getZ() + 1);
                RenderGlobal.drawOutlinedBoundingBox(box, 255, 255, 255, 255);
            }

            GL11.glDisable(GL11.GL_BLEND);
            GL11.glEnable(GL11.GL_DEPTH_TEST);
            GL11.glEnable(GL11.GL_TEXTURE_2D);
            GL11.glPopMatrix();
        }
    }
}
