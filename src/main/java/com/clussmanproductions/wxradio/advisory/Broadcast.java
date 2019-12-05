package com.clussmanproductions.wxradio.advisory;

import com.google.gson.JsonObject;

public abstract class Broadcast {
	private boolean hasBroadcast = false;
	
	public abstract String getDisplayText();
	public abstract String getVoiceText();
	public abstract boolean isForImmediateBroadcast();
	public SAMEBuilder getSAMEBuilder() { return null; };
	
	public boolean getHasBroadcast()
	{
		return hasBroadcast;
	}
	
	public void setHasBroadcast(boolean hasBroadcast)
	{
		this.hasBroadcast = hasBroadcast;
	}
	
	public String toJson()
	{
		JsonObject broadcastObject = new JsonObject();
		broadcastObject.addProperty("type", "broadcast");
		broadcastObject.addProperty("displayText", getDisplayText());
		broadcastObject.addProperty("voiceText", getVoiceText());
		broadcastObject.addProperty("isForImmediateBroadcast", isForImmediateBroadcast());
		
		SAMEBuilder builder = getSAMEBuilder();
		JsonObject sameHeader = new JsonObject();
		if (builder != null)
		{
			sameHeader = builder.toJsonObject();
		}
		
		broadcastObject.add("sameHeader", sameHeader);
		
		return broadcastObject.getAsString();
	}
	
	
}
