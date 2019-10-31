package com.clussmanproductions.wxradio.proxy;

import com.clussmanproductions.wxradio.ModBlocks;
import com.clussmanproductions.wxradio.blocks.BlockTransmitter;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@EventBusSubscriber
public class CommonProxy {
	@SubscribeEvent
	public static void registerBlocks(RegistryEvent.Register<Block> e)
	{
		e.getRegistry().register(new BlockTransmitter());
	}
	
	@SubscribeEvent
	public static void registerItems(RegistryEvent.Register<Item> e)
	{
		e.getRegistry().register(new ItemBlock(ModBlocks.transmitter).setRegistryName(ModBlocks.transmitter.getRegistryName()));
	}
	
	public void preInit(FMLPreInitializationEvent e)
	{
		
	}
	
	public void init(FMLInitializationEvent e)
	{
		
	}
	
	public void postInit(FMLPostInitializationEvent e)
	{
		
	}
}
