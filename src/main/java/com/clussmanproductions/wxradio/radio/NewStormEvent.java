package com.clussmanproductions.wxradio.radio;

import com.clussmanproductions.wxradio.util.RegionalStorm;

import net.minecraftforge.fml.common.eventhandler.Event;

public class NewStormEvent extends Event {
	private RegionalStorm regionalStorm;
	private WeatherStation station;
	public NewStormEvent(RegionalStorm regionalStorm, WeatherStation station)
	{
		super();
		this.regionalStorm = regionalStorm;
		this.station = station;
	}
	
	public RegionalStorm getRegionalStorm()
	{
		return regionalStorm;
	}
	
	public WeatherStation getWeatherStation()
	{
		return station;
	}
}
