package com.clussmanproductions.wxradio.radio;

import java.util.HashMap;
import java.util.Map.Entry;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock.ReadLock;
import java.util.concurrent.locks.ReentrantReadWriteLock.WriteLock;

import com.google.common.collect.ImmutableMap;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.storage.WorldSavedData;

public class WeatherStationManager extends WorldSavedData {
	ReentrantReadWriteLock weatherStationLock = new ReentrantReadWriteLock(true);
	
	public WeatherStationManager() {
		super("weather_station_manager");
	}

	private HashMap<String, WeatherStation> weatherStations = new HashMap<String, WeatherStation>();
	
	public WeatherStation getWeatherStation(String name)
	{
		ReadLock readLock = weatherStationLock.readLock();
		readLock.lock();
		
		WeatherStation weatherStation = weatherStations.get(name);
		
		readLock.unlock();
		
		if (weatherStation == null)
		{
			WriteLock writeLock = weatherStationLock.writeLock();
			writeLock.lock();
			
			weatherStation = new WeatherStation(name);
			weatherStations.put(name, weatherStation);
			
			writeLock.unlock();
		}
		
		return weatherStation;
	}

	public ImmutableMap<String, WeatherStation> getReadOnlyWeatherStationsByName()
	{
		ReadLock readLock = weatherStationLock.readLock();
		readLock.lock();
		
		ImmutableMap<String, WeatherStation> returnValue = ImmutableMap.copyOf(weatherStations);
		
		readLock.unlock();
		
		return returnValue;
	}
	
	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		for(String key : nbt.getKeySet())
		{
			weatherStations.put(key, WeatherStation.fromNBT((NBTTagCompound)nbt.getTag(key)));
		}
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound compound) {
		for(Entry<String, WeatherStation> entry : weatherStations.entrySet())
		{
			compound.setTag(entry.getKey(), entry.getValue().writeNBT());
		}
		
		return compound;
	}
}
