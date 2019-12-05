package com.clussmanproductions.wxradio.radio;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import java.util.UUID;

import com.clussmanproductions.wxradio.WXRadio;
import com.clussmanproductions.wxradio.advisory.Advisory;
import com.clussmanproductions.wxradio.advisory.Broadcast;
import com.clussmanproductions.wxradio.util.RegionalStorm;
import com.google.common.collect.ImmutableMap;
import com.google.gson.JsonObject;

import CoroUtil.util.Vec3;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.event.FMLServerStoppingEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.ServerTickEvent;
import net.minecraftforge.fml.relauncher.Side;
import weather2.ServerTickHandler;
import weather2.util.WeatherUtilConfig;
import weather2.weathersystem.WeatherManagerServer;
import weather2.weathersystem.storm.StormObject;
import weather2.weathersystem.storm.WeatherObject;

@EventBusSubscriber
public class RadioEventHandler {
	private static int sleep = 0;
	private static int lastHeartbeat = 0;
	
	@SubscribeEvent
	public static void WorldTick(ServerTickEvent e)
	{
		lastHeartbeat++;
		if (lastHeartbeat >= 200)
		{
			JsonObject heartbeat = new JsonObject();
			heartbeat.addProperty("type", "heartbeat");
			WXRadio.radioThread.sendMessage(heartbeat.getAsString());
			lastHeartbeat = 0;
		}
		
		if (sleep < 20)
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
			
			WeatherStationManager manager = WeatherStationManager.get(DimensionManager.getWorld(dimensionID));
			
			for(WeatherStation station : manager.getReadOnlyWeatherStationsByLocation().values())
			{
				List<WeatherObject> weatherObjects = weatherManager.getStormsAround(new Vec3(station.getLocationBlockPos()), 1000);
				HashMap<Long, StormObject> currentStormObjects = new HashMap<Long, StormObject>();
				
				for(WeatherObject weatherObject : weatherObjects)
				{
					if (weatherObject instanceof StormObject)
					{
						currentStormObjects.put(weatherObject.ID, (StormObject)weatherObject);
					}
				}
				
				ImmutableMap<Long, RegionalStorm> regionalStormsByID = station.getReadOnlyCurrentRegionalStormsByID();
				Set<Long> knownObjects = new HashSet<Long>(regionalStormsByID.keySet());
				
				Set<Long> newObjects = new HashSet<Long>(currentStormObjects.keySet());
				newObjects.removeAll(knownObjects);
				
				Set<Long> deletedObjects = new HashSet<Long>(regionalStormsByID.keySet());
				deletedObjects.removeAll(currentStormObjects.keySet());
				
				for(long newStormID : newObjects)
				{
					StormObject currentStormObject = currentStormObjects.get(newStormID);
					RegionalStorm newRegionalStorm = new RegionalStorm(station, currentStormObject);
					station.addNewRegionalStorm(newRegionalStorm);
					
					MinecraftForge.EVENT_BUS.post(new NewStormEvent(newRegionalStorm, station));
				}
				
				for(long deletedStormID : deletedObjects)
				{
					RegionalStorm oldRegionalStorm = regionalStormsByID.get(deletedStormID);
					MinecraftForge.EVENT_BUS.post(new RemovedStormEvent(oldRegionalStorm));
					station.removeRegionalStorm(deletedStormID);
				}
				
				ImmutableMap<UUID, Advisory> advisoriesByUUID = station.getReadOnlyAdvisories();
				for(Entry<UUID, Advisory> advisoryEntry : advisoriesByUUID.entrySet())
				{
					Advisory advisory = advisoryEntry.getValue();
					advisory.update();
					
					if (advisory.isVoid())
					{
						station.removeAdvisory(advisoryEntry.getKey());
					}
				}
				
				for(Advisory advisory : advisoriesByUUID.values())
				{
					Broadcast nextBroadcast = advisory.getNextNewBroadcast();
					
					while(nextBroadcast != null)
					{
						String broadcastMessage = nextBroadcast.toJson();
						
						WXRadio.radioThread.sendMessage(broadcastMessage);
						
						advisory.markBroadcastAsSent(nextBroadcast);						
						nextBroadcast = advisory.getNextNewBroadcast();
					}
				}
			}
		}
	}
}
