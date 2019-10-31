package com.clussmanproductions.wxradio.radio;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock.ReadLock;
import java.util.concurrent.locks.ReentrantReadWriteLock.WriteLock;

import com.clussmanproductions.wxradio.advisory.Advisory;
import com.clussmanproductions.wxradio.util.RegionalStorm;
import com.google.common.collect.ImmutableMap;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;

public class WeatherStation {	
	private String name;
	private BlockPos location;
	private ReentrantReadWriteLock advisoryLock = new ReentrantReadWriteLock(true);
	private ReentrantReadWriteLock stormLock = new ReentrantReadWriteLock(true);
	private HashMap<UUID, Advisory> advisories = new HashMap<UUID, Advisory>();
	private HashMap<Long, RegionalStorm> regionalStormsByID = new HashMap<Long, RegionalStorm>();
	
	WeatherStation(String name)
	{
		this.name = name;
	}
	
	private WeatherStation() {}
	
	
	public ImmutableMap<Long, RegionalStorm> getReadOnlyCurrentRegionalStormsByID()
	{
		ReadLock lock = stormLock.readLock();
		lock.lock();
		ImmutableMap<Long, RegionalStorm> returnValue = ImmutableMap.copyOf(regionalStormsByID);
		lock.unlock();
		
		return returnValue;
	}
	
	public void addNewRegionalStorm(RegionalStorm regionalStorm)
	{
		WriteLock lock = stormLock.writeLock();
		lock.lock();
		
		regionalStormsByID.put(regionalStorm.getID(), regionalStorm);
		
		lock.unlock();
	}
	
	public void removeRegionalStorm(long id)
	{
		WriteLock lock = stormLock.writeLock();
		lock.lock();
		
		regionalStormsByID.remove(id);
		
		lock.unlock();
	}
	
	public ImmutableMap<UUID, Advisory> getReadOnlyAdvisories()
	{
		ReadLock lock = advisoryLock.readLock();
		lock.lock();
		
		ImmutableMap<UUID, Advisory> returnValue = ImmutableMap.copyOf(advisories);
		
		lock.unlock();
		return returnValue;
	}
	
	public void removeAdvisory(UUID id)
	{
		WriteLock lock = advisoryLock.writeLock();
		lock.lock();
		
		advisories.remove(id);
		
		lock.unlock();
	}

	public void readNBT(NBTTagCompound compound)
	{
		name = compound.getString("name");
		location = BlockPos.fromLong(compound.getLong("location"));
	}
	
	public NBTTagCompound writeNBT()
	{
		NBTTagCompound tag = new NBTTagCompound();
		tag.setString("name", name);
		tag.setLong("location", location.toLong());
		
		return tag;
	}
	
	public static WeatherStation fromNBT(NBTTagCompound tag)
	{
		WeatherStation station = new WeatherStation();
		station.readNBT(tag);
		return station;
	}
}
