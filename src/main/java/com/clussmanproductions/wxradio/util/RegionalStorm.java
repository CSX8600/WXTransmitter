package com.clussmanproductions.wxradio.util;

import weather2.weathersystem.storm.StormObject;

public class RegionalStorm {
	private StormObject stormObject;
	
	public RegionalStorm(StormObject stormObject)
	{
		this.stormObject = stormObject;
	}
	
	public long getID()
	{
		return stormObject.ID;
	}
}
