package com.example.CHChests;

import com.google.common.base.Predicate;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.passive.*;
import net.minecraftforge.client.event.RenderLivingEvent;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.opengl.GL11;

import javax.annotation.Nullable;
import java.awt.*;

public class MobTracker {

    private static final Minecraft mc = Minecraft.getMinecraft();
    private boolean trackerEnabled = false;

    private static Color getColorForMob(EntityLivingBase entity) {
        if (entity instanceof EntityMooshroom) return null; // Ignore mushroom cows
        if (entity instanceof EntityCow) return Color.RED;
        if (entity instanceof EntityPig) return Color.PINK;
        if (entity instanceof EntitySheep && !isInExcludedArea(entity)) return Color.WHITE;
        if (entity instanceof EntityRabbit && !isInExcludedArea(entity)) return Color.GREEN;
        if (entity instanceof EntityChicken) return Color.YELLOW;
        if (entity instanceof EntityHorse) return Color.BLUE;
        return null;
    }

    private static boolean isInExcludedArea(EntityLivingBase entity) {
        double x = entity.posX;
        double y = entity.posY;
        double z = entity.posZ;

        // First exclusion area for sheep
        if (entity instanceof EntitySheep) {
            if (x >= 341 && x <= 388 && y >= 77 && y <= 86 && z >= -415 && z <= -334) {
                return true;
            }
        }

        // Second exclusion area for sheep and rabbits
        if ((entity instanceof EntitySheep || entity instanceof EntityRabbit)) {
            if (x >= 89 && x <= 206 && y >= 61 && y <= 96 && z >= -584 && z <= -404) {
                return true;
            }
        }

        // Third exclusion area for cows
        if (entity instanceof EntityCow) {
            if (x >= 168 && x <= 186 && y >= 27 && y <= 34 && z >= -503 && z <= -483) {
                return true;
            }
        }

        return false;
    }

    @SubscribeEvent
    public void onRenderLiving(RenderLivingEvent.Pre<EntityLivingBase> event) {
        if (trackerEnabled) {
            EntityLivingBase entity = event.entity;
            Color color = getColorForMob(entity);

            if (color != null) {
                drawOutline(event, color);
            }
        }
    }

    @SubscribeEvent
    public void onRenderWorldLast(RenderWorldLastEvent event) {
        if (trackerEnabled) {
            for (EntityLivingBase entity : mc.theWorld.getEntities(EntityLivingBase.class, new Predicate<EntityLivingBase>() {
                @Override
                public boolean apply(@Nullable EntityLivingBase e) {
                    return getColorForMob(e) != null;
                }
            })) {
                Color color = getColorForMob(entity);
                if (color != null) {
                    drawBeam(entity, color, event.partialTicks);
                }
            }
        }
    }

    private void drawOutline(RenderLivingEvent.Pre<EntityLivingBase> event, Color color) {
        GlStateManager.pushMatrix();
        GlStateManager.disableTexture2D();
        GlStateManager.disableLighting();
        GlStateManager.disableDepth();
        GlStateManager.enableBlend();
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
        GlStateManager.disableAlpha();
        GlStateManager.color(color.getRed() / 255.0F, color.getGreen() / 255.0F, color.getBlue() / 255.0F, 1.0F);

        // Draw the outline at the entity's position
        double x = event.entity.posX - Minecraft.getMinecraft().getRenderManager().viewerPosX;
        double y = event.entity.posY - Minecraft.getMinecraft().getRenderManager().viewerPosY;
        double z = event.entity.posZ - Minecraft.getMinecraft().getRenderManager().viewerPosZ;

        // Apply transformations to position the outline correctly
        GlStateManager.translate(x, 1 + y + event.entity.height / 2.0, z); // This centers the hologram
        GlStateManager.rotate(180, 1.0F, 0.0F, 0.0F); // Rotate the model 180 degrees around the X-axis if it's upside down

        // Render the entity model
        float limbSwing = event.entity.limbSwing;
        float limbSwingAmount = event.entity.limbSwingAmount;
        float ageInTicks = event.entity.ticksExisted;
        float headYaw = event.entity.rotationYawHead;
        float headPitch = event.entity.rotationPitch;
        float scale = 0.0625F;

        event.renderer.getMainModel().render(
                event.entity,
                limbSwing,
                limbSwingAmount,
                ageInTicks,
                headYaw,
                headPitch,
                scale
        );

        GlStateManager.enableAlpha();
        GlStateManager.enableDepth();
        GlStateManager.enableLighting();
        GlStateManager.enableTexture2D();
        GlStateManager.popMatrix();
    }


    private void drawBeam(EntityLivingBase entity, Color color, float partialTicks) {
        // Interpolating entity's position
        double entityX = entity.lastTickPosX + (entity.posX - entity.lastTickPosX) * partialTicks;
        double entityY = entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * partialTicks;
        double entityZ = entity.lastTickPosZ + (entity.posZ - entity.lastTickPosZ) * partialTicks;

        // Interpolating player's position
        double playerX = Minecraft.getMinecraft().thePlayer.lastTickPosX + (Minecraft.getMinecraft().thePlayer.posX - Minecraft.getMinecraft().thePlayer.lastTickPosX) * partialTicks;
        double playerY = Minecraft.getMinecraft().thePlayer.lastTickPosY + (Minecraft.getMinecraft().thePlayer.posY - Minecraft.getMinecraft().thePlayer.lastTickPosY) * partialTicks;
        double playerZ = Minecraft.getMinecraft().thePlayer.lastTickPosZ + (Minecraft.getMinecraft().thePlayer.posZ - Minecraft.getMinecraft().thePlayer.lastTickPosZ) * partialTicks;

        // Calculating the final coordinates for the start and end points of the beam
        double startX = entityX - Minecraft.getMinecraft().getRenderManager().viewerPosX;
        double startY = entityY + entity.height / 2.0 - Minecraft.getMinecraft().getRenderManager().viewerPosY;
        double startZ = entityZ - Minecraft.getMinecraft().getRenderManager().viewerPosZ;

        double endX = playerX - Minecraft.getMinecraft().getRenderManager().viewerPosX;
        double endY = playerY + Minecraft.getMinecraft().thePlayer.getEyeHeight() - Minecraft.getMinecraft().getRenderManager().viewerPosY;
        double endZ = playerZ - Minecraft.getMinecraft().getRenderManager().viewerPosZ;

        GlStateManager.pushMatrix();
        GlStateManager.disableCull();
        GlStateManager.disableTexture2D();
        GlStateManager.disableLighting();
        GlStateManager.disableDepth();
        GlStateManager.enableBlend();
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);

        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer worldRenderer = tessellator.getWorldRenderer();

        worldRenderer.begin(GL11.GL_LINES, DefaultVertexFormats.POSITION_COLOR);
        worldRenderer.pos(startX, startY, startZ).color(color.getRed(), color.getGreen(), color.getBlue(), 255).endVertex();
        worldRenderer.pos(endX, endY, endZ).color(color.getRed(), color.getGreen(), color.getBlue(), 255).endVertex();
        tessellator.draw();

        GlStateManager.enableCull();
        GlStateManager.enableDepth();
        GlStateManager.enableLighting();
        GlStateManager.enableTexture2D();
        GlStateManager.popMatrix();
    }

    public void onKeyPress() {
        if (KeyBindings.toggleTracker.isPressed()) {
            trackerEnabled = !trackerEnabled;
        }
    }
}
