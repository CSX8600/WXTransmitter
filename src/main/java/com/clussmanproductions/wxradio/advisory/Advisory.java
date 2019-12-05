package com.clussmanproductions.wxradio.advisory;

import java.util.LinkedList;
import java.util.Queue;
import java.util.Stack;
import java.util.UUID;

import com.clussmanproductions.wxradio.radio.WeatherStation;

public abstract class Advisory {
	private UUID identifier;
	private boolean hasUpdate;
	private Queue<Broadcast> newBroadcasts = new LinkedList<Broadcast>();
	private Stack<Broadcast> sentBroadcasts = new Stack<Broadcast>();
	protected WeatherStation station;
	
	public Advisory(WeatherStation station)
	{
		this.station = station;
		identifier = UUID.randomUUID();
	}
	
	public abstract String getUnlocalizedName();
	public abstract boolean isVoid();
	public abstract void update();
	
	public Broadcast getNextNewBroadcast()
	{
		return newBroadcasts.poll();
	}
	
	public void markBroadcastAsSent(Broadcast broadcast)
	{
		newBroadcasts.remove(broadcast);
		sentBroadcasts.add(broadcast);
	}
	
	protected void addBroadcast(Broadcast broadcast)
	{
		newBroadcasts.add(broadcast);
	}
	
	public UUID getIdentifier()
	{
		return identifier;
	}
}
