package com.clussmanproductions.wxradio.advisory;

import java.util.UUID;

public abstract class Advisory {
	private UUID identifier;
	private boolean hasUpdate;
	
	public Advisory()
	{
		identifier = UUID.randomUUID();
	}
	
	public abstract String getUnlocalizedName();
	public abstract String getSpeechText();
	public abstract String getDisplayText();
	public abstract boolean isForImmediateBroadcast();
	public abstract boolean isVoid();
	public abstract void update();
	
	public boolean getHasUpdate()
	{
		return hasUpdate;
	}
	
	public void setHasUpdate(boolean hasUpdate)
	{
		this.hasUpdate = hasUpdate;
	}
	
	public UUID getIdentifier()
	{
		return identifier;
	}
}
