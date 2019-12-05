package com.clussmanproductions.wxradio.advisorypack;

import com.clussmanproductions.wxradio.advisory.Advisory;
import com.clussmanproductions.wxradio.advisory.Broadcast;
import com.clussmanproductions.wxradio.radio.WeatherStation;
import com.clussmanproductions.wxradio.util.Region;
import com.clussmanproductions.wxradio.util.RegionalStorm;
import com.clussmanproductions.wxradio.util.RegionalStorm.Stages;

public class SevereThunderstormWatch extends Advisory {

	private static int severeThunderstormWarningCount;
	
	RegionalStorm thunderstorm;
	boolean stormInWatchRegion;
	int number;
	boolean isCanceled;
	int canceledTick = 0;
	boolean isVoid;
	Region watchRegion;
	public SevereThunderstormWatch(WeatherStation station, RegionalStorm thunderstorm) {
		super(station);
		
		this.thunderstorm = thunderstorm;
		this.thunderstorm.registerIsGrowingChangeCallback(getIdentifier(), (growing) -> onIsGrowingChange(growing));
		this.thunderstorm.registerStageChangeCallback(getIdentifier(), (stage) -> onStageChange(stage));
		watchRegion = thunderstorm.getFutureRegion();
		severeThunderstormWarningCount++;
		number = severeThunderstormWarningCount;
		
		Broadcast initialBroadcast = new Broadcast() {
			
			@Override
			public boolean isForImmediateBroadcast() {
				return true;
			}
			
			@Override
			public String getVoiceText() {
				return String.format("The National Weather Service in %s has issued Severe Thunderstorm Watch %s for %s %s.  Remember, "
						+ "a severe thunderstorm watch means that conditions are favorable for the development of severe weather, including "
						+ "large hail and damaging winds, in and close to the watch area.  While severe weather may not be imminent, persons "
						+ "should remain alert for rapidly changing weather conditions and listen for later statements and possible warnings.  "
						+ "Stay tuned to weather radio, commercial radio, and television outlets, or in ternet sources of the latest severe "
						+ "weather information.", station.getName(), number, watchRegion.toString(), station.getName());
			}
			
			@Override
			public String getDisplayText() {
				// TODO Auto-generated method stub
				return null;
			}
		};
	}
	@Override
	public String getUnlocalizedName() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isVoid() {
		return isVoid;
	}

	@Override
	public void update() {
		if (isCanceled)
		{
			canceledTick++;
			
			if (canceledTick > 300) // 5 minutes
			{
				isVoid = true;
				return;
			}
			
			return;
		}
		
		if (!stormInWatchRegion)
		{
			stormInWatchRegion = thunderstorm.getCurrentRegion() == watchRegion;
		}
		
		/*
		 * Possibilities:
		 * 1. Storm is in this watch region
		 * 2. Storm has exited this watch region
		 * 3. Storm is still approaching watch region
		 * 4. Storm is no longer approaching watch region 
		 * 5. Storm is in this watch region and is about to approach another region
		 */
		
		Region stormRegion = thunderstorm.getCurrentRegion();
		Region stormFutureRegion = thunderstorm.getFutureRegion();
		
		// Possibility 2
		if (stormInWatchRegion && stormRegion != watchRegion)
		{
			setCanceled();
			return;
		}
		
		// Possibility 4
		if (!stormInWatchRegion && stormFutureRegion != watchRegion)
		{
			setCanceled();
			return;
		}
		
		// Possibility 5
		if (stormInWatchRegion && stormFutureRegion != watchRegion)
		{
			SevereThunderstormWatch severeThunderstormWatch = new SevereThunderstormWatch(station, thunderstorm);
			station.addAdvisory(severeThunderstormWatch);
		}
		
		// Possibilities 1 & 3 are valid reasons to keep this watch going, so do nothing
	}

	private void onIsGrowingChange(boolean isGrowing)
	{
		if (!isGrowing)
		{
			setCanceled();
		}
	}
	
	private void onStageChange(Stages stage)
	{
		if (stage.getValue() < Stages.Thunder.getValue())
		{
			setCanceled();
		}
		else
		{
			// Spin up new severe t-storm warning
		}
	}
	
	private void setCanceled()
	{
		isCanceled = true;
		Broadcast canceledBroadcast = new Broadcast() {
			
			@Override
			public boolean isForImmediateBroadcast() {
				return true;
			}
			
			@Override
			public String getVoiceText() {
				return String.format("Severe Thunderstorm Watch %s for %s %s has been cancelled.", number, watchRegion.toString(), station.getName());
			}
			
			@Override
			public String getDisplayText() {
				return null;
			}
		};
		
		addBroadcast(canceledBroadcast);
	}
}
