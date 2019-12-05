package com.clussmanproductions.wxradio;

import com.clussmanproductions.wxradio.proxy.CommonProxy;

import net.minecraftforge.common.config.Configuration;

public class Config {
	private static final String CATEGORY_GENERAL = "general";
	
	public static int port = 25566;
	
	public static void readConfig()
	{
		Configuration cfg = CommonProxy.config;
		try
		{
			cfg.load();
			
		}
		catch(Exception ex)
		{
			WXRadio.logger.error("Problem loading config file", ex);
		}
	}
	
	private static void initGeneralConfig(Configuration cfg)
	{
		cfg.addCustomCategoryComment(CATEGORY_GENERAL, "General configuration");
		port = cfg.getInt("port", CATEGORY_GENERAL, port, Integer.MIN_VALUE, Integer.MAX_VALUE, "What port should the WX Transmitter broadcast on?"); 
	}
}
