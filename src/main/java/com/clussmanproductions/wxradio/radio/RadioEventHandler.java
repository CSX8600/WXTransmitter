package com.clussmanproductions.wxradio.radio;

import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import java.util.UUID;

import com.clussmanproductions.wxradio.advisory.Advisory;
import com.clussmanproductions.wxradio.util.RegionalStorm;
import com.google.common.collect.ImmutableMap;

import net.minecraft.world.DimensionType;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.gameevent.TickEvent.ServerTickEvent;
import net.minecraftforge.fml.relauncher.Side;
import weather2.ServerTickHandler;
import weather2.weathersystem.WeatherManagerServer;
import weather2.weathersystem.storm.StormObject;
import weather2.weathersystem.storm.WeatherObject;

@EventBusSubscriber(value = Side.SERVER)
public class RadioEventHandler {
	private static int sleep = 0;
	public static void WorldTick(ServerTickEvent e)
	{
		if (sleep < 5)
		{
			sleep++;
			return;
		}
		
		sleep = 0;
		
		WeatherManagerServer weatherManager = ServerTickHandler.getWeatherSystemForDim(DimensionManager.getDimensions(DimensionType.OVERWORLD)[0]);
		if (weatherManager == null)
		{
			return;
		}
		
		//WeatherStationManager weatherStationManager = e.
		
		List<WeatherObject> weatherObjects = weatherManager.getStormObjects();
		HashMap<Long, StormObject> currentStormObjects = new HashMap<Long, StormObject>();
		
		for(WeatherObject weatherObject : weatherObjects)
		{
			if (weatherObject instanceof StormObject)
			{
				currentStormObjects.put(weatherObject.ID, (StormObject)weatherObject);
			}
		}
		
		WeatherStation radioInstance = WeatherStation.getInstance();
		
		ImmutableMap<Long, RegionalStorm> regionalStormsByID = radioInstance.getReadOnlyCurrentRegionalStormsByID();
		Set<Long> knownObjects = regionalStormsByID.keySet();
		
		Set<Long> newObjects = currentStormObjects.keySet();
		newObjects.removeAll(knownObjects);
		
		Set<Long> deletedObjects = regionalStormsByID.keySet();
		deletedObjects.removeAll(currentStormObjects.keySet());
		
		for(long newStormID : newObjects)
		{
			StormObject currentStormObject = currentStormObjects.get(newStormID);
			RegionalStorm newRegionalStorm = new RegionalStorm(currentStormObject);
			radioInstance.addNewRegionalStorm(newRegionalStorm);
			
			MinecraftForge.EVENT_BUS.post(new NewStormEvent(newRegionalStorm));
		}
		
		for(long deletedStormID : deletedObjects)
		{
			RegionalStorm oldRegionalStorm = regionalStormsByID.get(deletedStormID);
			MinecraftForge.EVENT_BUS.post(new RemovedStormEvent(oldRegionalStorm));
			radioInstance.removeRegionalStorm(deletedStormID);
		}
		
		ImmutableMap<UUID, Advisory> advisoriesByUUID = radioInstance.getReadOnlyAdvisories();
		for(Entry<UUID, Advisory> advisoryEntry : advisoriesByUUID.entrySet())
		{
			Advisory advisory = advisoryEntry.getValue();
			advisory.update();
			
			if (advisory.isVoid())
			{
				radioInstance.removeAdvisory(advisoryEntry.getKey());
			}
			
			if (advisory.isForImmediateBroadcast())
			{
				// Broadcast NOW
			}
		}
		
		for(Advisory advisory : advisoriesByUUID.values())
		{
			if (advisory.getHasUpdate())
			{
				// Send update
				advisory.setHasUpdate(false);
			}
		}
	}
}
