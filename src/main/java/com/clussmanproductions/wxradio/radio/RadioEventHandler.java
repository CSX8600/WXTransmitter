package com.clussmanproductions.wxradio.radio;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.clussmanproductions.wxradio.WXRadio;
import com.google.common.collect.ImmutableMap;

import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;
import net.minecraftforge.fml.common.gameevent.TickEvent.ServerTickEvent;
import weather2.ServerTickHandler;
import weather2.util.WeatherUtilConfig;
import weather2.weathersystem.WeatherManagerServer;
import weather2.weathersystem.storm.StormObject;
import weather2.weathersystem.storm.WeatherObject;

@EventBusSubscriber
public class RadioEventHandler {
	private static int sleep = 0;
	private static int lastHeartbeat = 0;
	private static HashMap<Long, StormObject> knownStorms = new HashMap<Long, StormObject>();
	
	@SubscribeEvent
	public static void WorldTick(ServerTickEvent e)
	{
		if (e.phase == Phase.START)
		{
			return;
		}
		
		lastHeartbeat++;
		if (lastHeartbeat >= 200)
		{
			WXRadio.radioThread.heartbeat();
			lastHeartbeat = 0;
		}
		
		if (sleep < 100)
		{
			sleep++;
			return;
		}
		
		sleep = 0;
		
		List<Integer> dimensions = WeatherUtilConfig.listDimensionsStorms;
		
		for(int dimensionID : dimensions)
		{
			WeatherManagerServer weatherManager = ServerTickHandler.getWeatherSystemForDim(dimensionID);
			if (weatherManager == null)
			{
				return;
			}
			
			List<WeatherObject> weatherObjects = weatherManager.getStormObjects();
			HashMap<Long, StormObject> currentStormObjects = new HashMap<Long, StormObject>();
			
			for(WeatherObject weatherObject : weatherObjects)
			{
				if (weatherObject instanceof StormObject && (((StormObject)weatherObject).levelCurIntensityStage > 0 || (((StormObject)weatherObject).levelCurStagesIntensity == 0 && ((StormObject)weatherObject).attrib_precipitation)))
				{
					currentStormObjects.put(weatherObject.ID, (StormObject)weatherObject);
				}
			}
			
			Set<Long> knownObjects = new HashSet<Long>(knownStorms.keySet());
			
			Set<Long> newObjects = new HashSet<Long>(currentStormObjects.keySet());
			newObjects.removeAll(knownObjects);
			
			Set<Long> deletedObjects = new HashSet<Long>(knownStorms.keySet());
			deletedObjects.removeAll(currentStormObjects.keySet());
			
			WXRadio.radioThread.notifyUpdateStorms(knownStorms.values());
			
			for(long newStormID : newObjects)
			{
				StormObject currentStormObject = currentStormObjects.get(newStormID);
				knownStorms.put(newStormID, currentStormObject);
				WXRadio.radioThread.notifyNewStorm(currentStormObject);
			}
			
			for(long deletedStormID : deletedObjects)
			{
				WXRadio.radioThread.notifyDeleteStorm(deletedStormID);
				knownStorms.remove(deletedStormID);
			}
		}
	}

	public static ImmutableMap<Long, StormObject> getKnownStorms()
	{
		return ImmutableMap.copyOf(knownStorms);
	}
}
