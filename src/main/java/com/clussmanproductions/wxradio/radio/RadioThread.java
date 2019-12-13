package com.clussmanproductions.wxradio.radio;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock.ReadLock;
import java.util.concurrent.locks.ReentrantReadWriteLock.WriteLock;

import com.clussmanproductions.wxradio.Config;
import com.clussmanproductions.wxradio.WXRadio;
import com.google.common.collect.ImmutableMap;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import weather2.weathersystem.storm.StormObject;

public class RadioThread extends Thread {	
	ServerSocket server;
	ReentrantReadWriteLock clientLock = new ReentrantReadWriteLock(true);
	ArrayList<Socket> clients = new ArrayList<Socket>();
	@Override
	public void run() {
		WXRadio.logger.debug("Thread has started");
		
		try
		{
			server = new ServerSocket(Config.port);
		}
		catch(Exception ex)
		{
			WXRadio.logger.error("An error occurred while starting the WX Transmitter broadcast server", ex);
		}
		
		while(true)
		{
			Socket socket;
			try
			{
				socket = server.accept();
			}
			catch(Exception ex)
			{
				if (!server.isClosed())
				{
					try
					{
						server.close();
					}
					catch(Exception innerEx) {}
				}
				
				return;
			}
			
			ImmutableMap<Long, StormObject> storms = RadioEventHandler.getKnownStorms();
			JsonObject response = new JsonObject();
			response.addProperty("event", "initial");
			JsonArray stormObjectArray = new JsonArray();
			for(StormObject storm : storms.values())
			{
				JsonObject stormJson = new JsonObject();
				stormJson.addProperty("id", storm.ID);
				stormJson.addProperty("strength", storm.levelCurIntensityStage);
				stormJson.addProperty("posx", storm.pos.xCoord);
				stormJson.addProperty("posz", storm.pos.zCoord);
				stormObjectArray.add(stormJson);
			}
			response.add("storms", stormObjectArray);
			byte[] responseBytes = response.toString().getBytes();
			int len = responseBytes.length;
			String lengthString = String.format("%8s", Integer.toString(len)).replace(' ', '0');
			
			try
			{
				socket.getOutputStream().write(lengthString.getBytes());
				socket.getOutputStream().write(responseBytes);
			}
			catch(Exception ex) {}
			
			WriteLock lock = clientLock.writeLock();
			lock.lock();
			
			clients.add(socket);
			
			lock.unlock();
		}
	}
	
	private void sendMessage(String message)
	{
		ReadLock lock = clientLock.readLock();
		lock.lock();
		
		ArrayList<Socket> closedSockets = new ArrayList<Socket>();
		for(Socket socket : clients)
		{
			if (socket.isClosed())
			{
				closedSockets.add(socket);
				continue;
			}
			
			int len = message.length();
			String messageLength = String.format("%8s", Integer.toString(len)).replace(' ', '0');
			
			try
			{
				socket.getOutputStream().write(messageLength.getBytes());
				socket.getOutputStream().write(message.getBytes());
			}
			catch(Exception ex)
			{
				WXRadio.logger.warn("WARNING: Failed to write broadcast to a client, kicking", ex);
				closedSockets.add(socket);
			}
		}
		
		lock.unlock();
		
		if (closedSockets.size() > 0)
		{
			WriteLock writeLock = clientLock.writeLock();
			writeLock.lock();
			
			for(Socket socket : closedSockets)
			{
				try
				{
					socket.close();
				}
				catch(Exception ex) {}
				clients.remove(socket);
			}
			
			writeLock.unlock();
		}
	}
	
	public void notifyNewStorm(StormObject storm)
	{
		JsonObject jObject = new JsonObject();
		jObject.addProperty("event", "new");
		jObject.addProperty("id", storm.ID);
		jObject.addProperty("strength", storm.levelCurIntensityStage);
		jObject.addProperty("posx", storm.pos.xCoord);
		jObject.addProperty("posz", storm.pos.zCoord);
		
		sendMessage(jObject.toString());
	}
	
	public void notifyDeleteStorm(long id)
	{
		JsonObject jObject = new JsonObject();
		jObject.addProperty("event", "delete");
		jObject.addProperty("id", id);
		
		sendMessage(jObject.toString());
	}
	
	public void notifyUpdateStorms(Collection<StormObject> storms)
	{
		if (storms.isEmpty())
		{
			return;
		}
		
		JsonObject mainMessage = new JsonObject();
		mainMessage.addProperty("event", "update");
		JsonArray jsonStorms = new JsonArray();
		for(StormObject storm : storms)
		{
			JsonObject jObject = new JsonObject();
			jObject.addProperty("id", storm.ID);
			jObject.addProperty("strength", storm.levelCurIntensityStage);
			jObject.addProperty("posx", storm.pos.xCoord);
			jObject.addProperty("posz", storm.pos.zCoord);
			jsonStorms.add(jObject);
		}
		
		mainMessage.add("storms", jsonStorms);
		
		sendMessage(mainMessage.toString());
	}
	
	public void heartbeat()
	{
		JsonObject jObject = new JsonObject();
		jObject.addProperty("event", "heartbeat");
		
		sendMessage(jObject.toString());
	}
	
	public void stopThread()
	{
		if (server != null && !server.isClosed())
		{
			try
			{
				server.close();
			}
			catch(Exception ex)
			{
				WXRadio.logger.error("Tried to shutdown WX Transmitter server, but an error occurred", ex);
			}
		}
	}
}
