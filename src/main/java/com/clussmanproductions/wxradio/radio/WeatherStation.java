package com.clussmanproductions.wxradio.radio;

import java.util.HashMap;
import java.util.Map.Entry;
import java.util.UUID;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock.ReadLock;
import java.util.concurrent.locks.ReentrantReadWriteLock.WriteLock;

import com.clussmanproductions.wxradio.advisory.Advisory;
import com.clussmanproductions.wxradio.util.Region;
import com.clussmanproductions.wxradio.util.RegionalStorm;
import com.google.common.collect.ImmutableMap;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.Tuple;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

public class WeatherStation {	
	private String name;
	private BlockPos location;
	private WeatherStationManager manager;
	private ReentrantReadWriteLock advisoryLock = new ReentrantReadWriteLock(true);
	private ReentrantReadWriteLock stormLock = new ReentrantReadWriteLock(true);
	private HashMap<UUID, Advisory> advisories = new HashMap<UUID, Advisory>();
	private HashMap<Long, RegionalStorm> regionalStormsByID = new HashMap<Long, RegionalStorm>();
	private HashMap<Region, Tuple<BlockPos, BlockPos>> regionDefinitions = null;
	
	WeatherStation(String name, BlockPos location, WeatherStationManager manager)
	{
		this.name = name;
		this.location = location;
		this.manager = manager;
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
	
	public static WeatherStation fromNBT(NBTTagCompound tag, WeatherStationManager manager)
	{
		WeatherStation station = new WeatherStation();
		station.readNBT(tag);
		station.manager = manager;
		return station;
	}

	public BlockPos getLocationBlockPos()
	{
		return location;
	}
	
	public Vec3d getLocationVec3d()
	{
		return new Vec3d(location.getX(), location.getY(), location.getZ());
	}

	public String getName()
	{
		return name;
	}

	public void setName(String newName)
	{
		name = newName;
		manager.markDirty();
	}

	public Region getRegion(BlockPos pos)
	{
		if (regionDefinitions == null)
		{
			regionDefinitions = new HashMap<Region, Tuple<BlockPos, BlockPos>>();
			
			regionDefinitions.put(Region.Northwest, new Tuple<BlockPos, BlockPos>(location.north(1000).west(1000), location.north(333).west(333)));
			regionDefinitions.put(Region.North, new Tuple<BlockPos, BlockPos>(location.north(1000).west(333), location.north(333).east(333)));
			regionDefinitions.put(Region.Northeast, new Tuple<BlockPos, BlockPos>(location.north(1000).east(333), location.north(333).east(1000)));
			regionDefinitions.put(Region.West, new Tuple<BlockPos, BlockPos>(location.north(333).west(1000), location.south(333).west(333)));
			regionDefinitions.put(Region.Central, new Tuple<BlockPos, BlockPos>(location.north(333).west(333), location.south(333).east(333)));
			regionDefinitions.put(Region.East, new Tuple<BlockPos, BlockPos>(location.north(333).east(333), location.south(333).east(1000)));
			regionDefinitions.put(Region.Southwest, new Tuple<BlockPos, BlockPos>(location.south(333).west(1000), location.south(1000).west(333)));
			regionDefinitions.put(Region.South, new Tuple<BlockPos, BlockPos>(location.south(333).west(333), location.south(1000).east(333)));
			regionDefinitions.put(Region.Southeast, new Tuple<BlockPos, BlockPos>(location.south(333).east(333), location.south(1000).east(1000)));
		}
		
		int x = pos.getX();
		int z = pos.getZ();
		
		for(Entry<Region, Tuple<BlockPos, BlockPos>> entry : regionDefinitions.entrySet())
		{
			BlockPos upperLeft = entry.getValue().getFirst();
			BlockPos lowerRight = entry.getValue().getSecond();
			
			if (upperLeft.getX() <= x && upperLeft.getZ() <= z && lowerRight.getX() >= x && lowerRight.getZ() >= z)
			{
				return entry.getKey();
			}
		}
		
		return null;
	}
	
	public Region getSubRegion(BlockPos pos, Region region)
	{
		Tuple<BlockPos, BlockPos> regionPositions = regionDefinitions.get(region);
		
		int upperLeftX = regionPositions.getFirst().getX();
		int upperLeftZ = regionPositions.getFirst().getZ();
		int lowerRightX = regionPositions.getSecond().getX();
		int lowerRightZ = regionPositions.getSecond().getZ();

		int x = pos.getX();
		int z = pos.getZ();
		
		if (x - upperLeftX < 100 && z - upperLeftZ < 100)
		{
			return Region.Northwest;
		}
		else if (lowerRightX - x < 100 && z - upperLeftZ < 100)
		{
			return Region.Northeast;
		}
		else if (z - upperLeftZ < 100)
		{
			return Region.North;
		}
		else if (x - upperLeftX < 100 && lowerRightZ - z < 100)
		{
			return Region.Southwest;
		}
		else if (lowerRightX - x < 100 && lowerRightZ - z < 100)
		{
			return Region.Southeast;
		}
		else if (lowerRightZ - z < 100)
		{
			return Region.South;
		}
		else if (x - upperLeftX < 100)
		{
			return Region.West;
		}
		else if (lowerRightX - x < 100)
		{
			return Region.East;
		}
		else
		{
			return Region.Central;
		}
	}

	public void addAdvisory(Advisory advisory)
	{
		WriteLock lock = advisoryLock.writeLock();
		
		lock.lock();
		
		advisories.put(advisory.getIdentifier(), advisory);
		
		lock.unlock();
	}
}
