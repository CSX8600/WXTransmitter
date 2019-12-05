package com.clussmanproductions.wxradio.util;

import java.util.HashMap;
import java.util.UUID;
import java.util.function.Consumer;

import com.clussmanproductions.wxradio.radio.WeatherStation;

import CoroUtil.util.Vec3;
import weather2.weathersystem.storm.StormObject;

public class RegionalStorm {
	private StormObject stormObject;
	private boolean isGrowing;
	private Stages stage;
	private WeatherStation station;
	
	private HashMap<UUID, Consumer<Boolean>> isGrowingChangeCallbacks = new HashMap<UUID, Consumer<Boolean>>();
	private HashMap<UUID, Consumer<Stages>> stageChangeCallbacks = new HashMap<UUID, Consumer<Stages>>();
	
	public RegionalStorm(WeatherStation station, StormObject stormObject)
	{
		this.station = station;
		this.stormObject = stormObject;
		isGrowing = stormObject.isGrowing;
		stage = Stages.getByValue(stormObject.levelCurIntensityStage);
	}
	
	public long getID()
	{
		return stormObject.ID;
	}
	
	public void update()
	{
		if (stormObject == null)
		{
			return;
		}
		
		if (stormObject.isGrowing != isGrowing)
		{
			for(Consumer<Boolean> consumer : isGrowingChangeCallbacks.values())
			{
				consumer.accept(stormObject.isGrowing);
			}
			
			isGrowing = stormObject.isGrowing;
		}
		
		if (stormObject.levelCurIntensityStage != stage.getValue())
		{
			Stages newStage = Stages.getByValue(stormObject.levelCurIntensityStage);
			
			for(Consumer<Stages> consumer : stageChangeCallbacks.values())
			{
				consumer.accept(newStage);
			}
			
			stage = newStage;
		}
	}
	
	public void registerIsGrowingChangeCallback(UUID identifier, Consumer<Boolean> callback)
	{
		isGrowingChangeCallbacks.put(identifier, callback);
	}
	
	public void registerStageChangeCallback(UUID identifier, Consumer<Stages> callback)
	{
		stageChangeCallbacks.put(identifier, callback);
	}
	
	public enum Stages
	{
		Normal(0),
		Thunder(1),
		HighWind(2),
		Hail(3),
		Forming(4),
		Stage1(5),
		Stage2(6),
		Stage3(7),
		Stage4(8),
		Stage5(9);
		private int value;
		Stages(int value)
		{
			this.value = value;
		}
		
		public int getValue()
		{
			return value;
		}
		
		public static Stages getByValue(int value)
		{
			for(Stages stage : Stages.values())
			{
				if (stage.value == value)
				{
					return stage;
				}
			}
			
			return null;
		}
	}

	public Region getCurrentRegion()
	{
		return station.getRegion(stormObject.pos.toBlockPos());
	}
	
	public Region getFutureRegion()
	{
		Vec3 motion = stormObject.motion;
		float avgSpeed = 0.1F;
		float warningTicks = 3_600F; // 3 minutes
		
		double newXMotion = motion.xCoord * (double)(avgSpeed * warningTicks);
		double newYMotion = (double)(avgSpeed * warningTicks);
		double newZMotion = (double)(avgSpeed * warningTicks);
		
		Vec3 futurePosition = stormObject.pos.addVector(newXMotion, newYMotion, newZMotion);
		
		return station.getRegion(futurePosition.toBlockPos());
	}
}
