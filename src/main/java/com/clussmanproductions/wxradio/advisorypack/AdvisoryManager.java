package com.clussmanproductions.wxradio.advisorypack;

import com.clussmanproductions.wxradio.radio.NewStormEvent;
import com.clussmanproductions.wxradio.radio.RemovedStormEvent;

import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@EventBusSubscriber
public class AdvisoryManager {
	@SubscribeEvent
	public static void newStorm(NewStormEvent e)
	{
		
	}
	
	@SubscribeEvent
	public static void removedStorm(RemovedStormEvent e)
	{
		
	}
}
