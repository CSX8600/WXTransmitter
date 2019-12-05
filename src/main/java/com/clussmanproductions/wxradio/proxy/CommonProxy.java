package com.clussmanproductions.wxradio.proxy;

import java.io.File;

import com.clussmanproductions.wxradio.Config;
import com.clussmanproductions.wxradio.ModBlocks;
import com.clussmanproductions.wxradio.WXRadio;
import com.clussmanproductions.wxradio.blocks.BlockTransmitter;
import com.clussmanproductions.wxradio.gui.GuiProxy;
import com.clussmanproductions.wxradio.network.PacketHandler;
import com.clussmanproductions.wxradio.tileentity.TransmitterTileEntity;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.registry.GameRegistry;

@EventBusSubscriber
public class CommonProxy {
	public static Configuration config;
	@SubscribeEvent
	public static void registerBlocks(RegistryEvent.Register<Block> e)
	{
		e.getRegistry().register(new BlockTransmitter());
		
		GameRegistry.registerTileEntity(TransmitterTileEntity.class, WXRadio.MODID + "_transmitter");
	}
	
	@SubscribeEvent
	public static void registerItems(RegistryEvent.Register<Item> e)
	{
		e.getRegistry().register(new ItemBlock(ModBlocks.transmitter).setRegistryName(ModBlocks.transmitter.getRegistryName()));
	}
	
	public void preInit(FMLPreInitializationEvent e)
	{
		File directory = e.getModConfigurationDirectory();
		config = new Configuration(new File(directory.getPath(), "wxtransmitter.cfg"));
		Config.readConfig();
		
		PacketHandler.registerMessages("wxtransmitter");
	}
	
	public void init(FMLInitializationEvent e)
	{
		NetworkRegistry.INSTANCE.registerGuiHandler(WXRadio.instance, new GuiProxy());
	}
	
	public void postInit(FMLPostInitializationEvent e)
	{
		
	}
}
