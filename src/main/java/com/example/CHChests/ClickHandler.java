package com.example.CHChests;

import net.minecraft.util.BlockPos;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.List;
import java.util.Map;

public class ClickHandler {
    private final CHChestsFinder chchestsfinder;
    private final TitaniumFinder titaniumFinder;

    public ClickHandler(CHChestsFinder chchestsfinder, TitaniumFinder titaniumFinder) {
        this.chchestsfinder = chchestsfinder;
        this.titaniumFinder = titaniumFinder;
    }

    @SubscribeEvent
    public void onPlayerInteract(PlayerInteractEvent event) {

        //remove chest when right clicked
        if (event.action == PlayerInteractEvent.Action.RIGHT_CLICK_BLOCK) {
            BlockPos clickedPos = event.pos;
            Map<BlockPos, String> blockTextMap = chchestsfinder.getBlockTextMap();
            blockTextMap.remove(clickedPos);
        }

        //remove titanium when left clicked
        if (event.action == PlayerInteractEvent.Action.LEFT_CLICK_BLOCK) {
            BlockPos clickedPos = event.pos;
            List<BlockPos> titaniumList = titaniumFinder.getTitaniumList();
            titaniumList.remove(clickedPos);
        }
    }
}
