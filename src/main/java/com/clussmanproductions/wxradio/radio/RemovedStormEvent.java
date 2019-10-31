package com.clussmanproductions.wxradio.radio;

import com.clussmanproductions.wxradio.util.RegionalStorm;

import net.minecraftforge.fml.common.eventhandler.Event;

public class RemovedStormEvent extends Event {
	private RegionalStorm regionalStorm;
	public RemovedStormEvent(RegionalStorm regionalStorm)
	{
		this.regionalStorm = regionalStorm;
	}
	
	public RegionalStorm getRegionalStorm()
	{
		return regionalStorm;
	}
}
