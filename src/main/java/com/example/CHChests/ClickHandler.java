package com.example.CHChests;

import net.minecraft.block.BlockChest;
import net.minecraft.util.BlockPos;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.List;
import java.util.Map;

public class ClickHandler {
    private final KeyInputHandler keyInputHandler;

    public ClickHandler(KeyInputHandler keyInputHandler) {
        this.keyInputHandler = keyInputHandler;
    }

    @SubscribeEvent
    public void onPlayerInteract(PlayerInteractEvent event) {

        //remove chest when right clicked
        if (event.action == PlayerInteractEvent.Action.RIGHT_CLICK_BLOCK) {
            BlockPos clickedPos = event.pos;
            Map<BlockPos, String> blockTextMap = keyInputHandler.getBlockTextMap();
            blockTextMap.remove(clickedPos);
        }

        //remove titanium when left clicked
        if (event.action == PlayerInteractEvent.Action.LEFT_CLICK_BLOCK) {
            BlockPos clickedPos = event.pos;
            List<BlockPos> titaniumList = keyInputHandler.getTitaniumList();
            titaniumList.remove(clickedPos);
        }
    }
}
