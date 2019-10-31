package com.clussmanproductions.wxradio;

import com.clussmanproductions.wxradio.blocks.BlockTransmitter;

import net.minecraftforge.fml.common.registry.GameRegistry.ObjectHolder;

@ObjectHolder(WXRadio.MODID)
public class ModBlocks {
	@ObjectHolder("transmitter")
	public static BlockTransmitter transmitter;
}
