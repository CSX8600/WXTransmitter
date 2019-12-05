package com.clussmanproductions.wxradio.advisory;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

public class SAMEBuilder {
	private String code;
	private int length;
	private String sender;
	private ArrayList<Region> regions = new ArrayList<Region>();
	private StartInfo start;
	
	public static class Region
	{
		private String subdiv;
		private String stateCode;
		private String countyCode;
		
		public Region(String subdiv, String stateCode, String countyCode)
		{
			this.subdiv = subdiv;
			this.stateCode = stateCode;
			this.countyCode = countyCode;
		}
	}
	
	public static class StartInfo
	{
		private int day;
		private int hour;
		private int minute;
		
		public static StartInfo fromDate(Date date)
		{
			Calendar cal = Calendar.getInstance();
			cal.setTime(date);
			int day = cal.get(Calendar.DAY_OF_YEAR);
			int hour = cal.get(Calendar.HOUR);
			int minute = cal.get(Calendar.MINUTE);
			
			return new StartInfo(day, hour, minute);
		}
		
		public StartInfo(int day, int hour, int minute)
		{
			
		}
	}
	
	public SAMEBuilder setCode(String code)
	{
		this.code = code;
		return this;
	}
	
	public SAMEBuilder setLength(int length)
	{
		this.length = length;
		return this;
	}
	
	public SAMEBuilder setSender(String sender)
	{
		this.sender = sender;
		return this;
	}
	
	public SAMEBuilder addRegion(String subdiv, String stateCode, String countyCode)
	{
		regions.add(new Region(subdiv, stateCode, countyCode));
		return this;
	}
	
	public SAMEBuilder setStart(Date start)
	{
		this.start = StartInfo.fromDate(start);
		return this;
	}

	public JsonObject toJsonObject()
	{
		JsonObject message = new JsonObject();
		message.addProperty("originator", "WXR");
		message.addProperty("code", code);
		
		JsonArray regionArray = new JsonArray();
		for(Region region : regions)
		{
			JsonObject regionObject = new JsonObject();
			regionObject.addProperty("subdiv", region.subdiv);
			regionObject.addProperty("stateCode", region.stateCode);
			regionObject.addProperty("countyCode", region.countyCode);
			regionArray.add(regionObject);
		}
		message.add("region", regionArray);
		message.addProperty("length", length);
		
		JsonObject startObject = new JsonObject();
		startObject.addProperty("day", start.day);
		startObject.addProperty("hour", start.hour);
		startObject.addProperty("minute", start.minute);
		
		message.add("start", startObject);
		message.addProperty("sender", sender);
		
		return message;
	}
}
