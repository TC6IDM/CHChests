package com.example.CHChests;

import net.minecraft.block.BlockChest;
import net.minecraft.util.BlockPos;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.Map;

public class ChestClickHandler {
    private final KeyInputHandler keyInputHandler;

    public ChestClickHandler(KeyInputHandler keyInputHandler) {
        this.keyInputHandler = keyInputHandler;
    }

    @SubscribeEvent
    public void onPlayerInteract(PlayerInteractEvent event) {
        // Check if the event is a right-click block event
        if (event.action == PlayerInteractEvent.Action.RIGHT_CLICK_BLOCK) {
            // Get the position of the clicked block
            BlockPos clickedPos = event.pos;

            // Check if the clicked block is a chest
            if (event.world.getBlockState(clickedPos).getBlock() instanceof BlockChest) {
                // Remove the block from the blockTextMap
                Map<BlockPos, String> blockTextMap = keyInputHandler.getBlockTextMap();
                blockTextMap.remove(clickedPos);
            }
        }
    }
}
