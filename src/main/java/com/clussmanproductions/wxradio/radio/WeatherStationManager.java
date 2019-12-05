package com.clussmanproductions.wxradio.radio;

import java.util.HashMap;
import java.util.Map.Entry;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock.ReadLock;
import java.util.concurrent.locks.ReentrantReadWriteLock.WriteLock;

import com.google.common.collect.ImmutableMap;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.storage.WorldSavedData;

public class WeatherStationManager extends WorldSavedData {
	ReentrantReadWriteLock weatherStationLock = new ReentrantReadWriteLock(true);
	private static String worldSavedDataName = "weather_station_manager";
	
	public WeatherStationManager() {
		super(worldSavedDataName);
	}

	private HashMap<BlockPos, WeatherStation> weatherStations = new HashMap<BlockPos, WeatherStation>();
	
	public WeatherStation getWeatherStation(BlockPos location)
	{
		ReadLock readLock = weatherStationLock.readLock();
		readLock.lock();
		
		WeatherStation weatherStation = weatherStations.get(location);
		
		readLock.unlock();
		
		return weatherStation;
	}

	public WeatherStation createWeatherStation(BlockPos pos)
	{
		WriteLock lock = weatherStationLock.writeLock();
		lock.lock();
		
		int stationIteration = 1;
		
		boolean nameValid = false;
		while(!nameValid)
		{
			nameValid = true;
			for(WeatherStation station : weatherStations.values())
			{
				if (station.getName().equalsIgnoreCase("[New Station " + stationIteration + "]"))
				{
					nameValid = false;
					stationIteration++;
					break;
				}
			}
		}
		
		String newStationName = "[New Station " + stationIteration + "]";
		
		WeatherStation newStation = new WeatherStation(newStationName, pos, this);
		weatherStations.put(pos, newStation);
		
		lock.unlock();
		markDirty();
		
		return newStation;
	}
	
	public ImmutableMap<BlockPos, WeatherStation> getReadOnlyWeatherStationsByLocation()
	{
		ReadLock readLock = weatherStationLock.readLock();
		readLock.lock();
		
		ImmutableMap<BlockPos, WeatherStation> returnValue = ImmutableMap.copyOf(weatherStations);
		
		readLock.unlock();
		
		return returnValue;
	}
	
	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		for(String key : nbt.getKeySet())
		{
			NBTTagCompound tagCompound = (NBTTagCompound)nbt.getTag(key);
			WeatherStation station = WeatherStation.fromNBT(tagCompound, this);
			
			weatherStations.put(station.getLocationBlockPos(), station);
		}
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound compound) {
		for(WeatherStation entry : weatherStations.values())
		{
			compound.setTag(entry.getName(), entry.writeNBT());
		}
		
		return compound;
	}

	public static WeatherStationManager get(World world)
	{
		WeatherStationManager manager = (WeatherStationManager)world.loadData(WeatherStationManager.class, worldSavedDataName);
		
		if (manager == null)
		{
			manager = new WeatherStationManager();
			world.setData(worldSavedDataName, manager);
		}
		
		return manager;
	}
}
