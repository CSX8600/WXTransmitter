package com.clussmanproductions.wxradio.radio;

import com.clussmanproductions.wxradio.util.RegionalStorm;

import net.minecraftforge.fml.common.eventhandler.Event;

public class NewStormEvent extends Event {
	private RegionalStorm regionalStorm;
	public NewStormEvent(RegionalStorm regionalStorm)
	{
		super();
		this.regionalStorm = regionalStorm;
	}
	
	public RegionalStorm getRegionalStorm()
	{
		return regionalStorm;
	}
}
