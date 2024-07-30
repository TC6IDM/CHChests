package com.example.CHChests;

import com.example.CHChests.KeyInputHandler;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class BlockEventHandler {
    private final KeyInputHandler keyInputHandler;

    public BlockEventHandler(KeyInputHandler keyInputHandler) {
        this.keyInputHandler = keyInputHandler;
    }
    @SubscribeEvent
    public void onBlockBreak(BlockEvent.BreakEvent event) {
        BlockPos clickedPos = event.pos;
        List<BlockPos> titaniumList = keyInputHandler.getTitaniumList();
        titaniumList.remove(clickedPos);
    }
}
