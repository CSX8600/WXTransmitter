package com.clussmanproductions.wxradio;

import org.apache.logging.log4j.Logger;

import com.clussmanproductions.wxradio.proxy.CommonProxy;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.event.FMLServerStoppingEvent;
import com.clussmanproductions.wxradio.radio.RadioThread;

@Mod(modid = WXRadio.MODID, name = WXRadio.NAME, version = WXRadio.VERSION, dependencies = "required:weather2@[1.12.1-2.6.12,)")
public class WXRadio
{
    public static final String MODID = "wxradio";
    public static final String NAME = "Weather Radio Mod";
    public static final String VERSION = "0.0.1";

    public static Logger logger;
	public static RadioThread radioThread;

    @SidedProxy(clientSide = "com.clussmanproductions.wxradio.proxy.ClientProxy", serverSide = "com.clussmanproductions.wxradio.proxy.ServerProxy")
    public static CommonProxy proxy;
    
    @Mod.Instance
    public static WXRadio instance;
    
    @EventHandler
    public void preInit(FMLPreInitializationEvent e)
    {
        logger = e.getModLog();
        proxy.preInit(e);
    }

    @EventHandler
    public void init(FMLInitializationEvent e)
    {
        proxy.init(e);
    }
    
    @EventHandler
    public void postInit(FMLPostInitializationEvent e)
    {
    	proxy.postInit(e);
    }

	@EventHandler
	public static void serverStarting(FMLServerStartingEvent e)
	{
		radioThread = new RadioThread();
		radioThread.start();
	}
	
	@EventHandler
	public static void serverStopping(FMLServerStoppingEvent e)
	{
		if (radioThread != null)
		{
			try
			{
				radioThread.stopThread();
			}
			catch(Exception ex)
			{
				WXRadio.logger.error("An error occurred while trying to stop WX Transmitter server during the server shutdown event", ex);
			}
		}
	}
}
