package com.clussmanproductions.wxradio.proxy;

import java.io.File;

import com.clussmanproductions.wxradio.Config;

import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

@EventBusSubscriber
public class CommonProxy {
	public static Configuration config;
	
	public void preInit(FMLPreInitializationEvent e)
	{
		File directory = e.getModConfigurationDirectory();
		config = new Configuration(new File(directory.getPath(), "wxtransmitter.cfg"));
		Config.readConfig();
	}
	
	public void init(FMLInitializationEvent e)
	{
		
	}
	
	public void postInit(FMLPostInitializationEvent e)
	{
		
	}
}
