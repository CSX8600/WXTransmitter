package com.clussmanproductions.wxradio.radio;

import java.io.BufferedWriter;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock.ReadLock;
import java.util.concurrent.locks.ReentrantReadWriteLock.WriteLock;

import com.clussmanproductions.wxradio.Config;
import com.clussmanproductions.wxradio.WXRadio;

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
			
			WriteLock lock = clientLock.writeLock();
			lock.lock();
			
			clients.add(socket);
			
			lock.unlock();
		}
	}
	
	public void sendMessage(String message)
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
			
			try
			{
				socket.getOutputStream().write(message.getBytes());
			}
			catch(Exception ex)
			{
				WXRadio.logger.warn("WARNING: Failed to write broadcast to a client", ex);
			}
		}
		
		lock.unlock();
		
		if (closedSockets.size() > 0)
		{
			WriteLock writeLock = clientLock.writeLock();
			
			for(Socket socket : closedSockets)
			{
				clients.remove(socket);
			}
			
			writeLock.unlock();
		}
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
	
	@Override
	protected void finalize() throws Throwable {
		if (server != null && !server.isClosed())
		{
			try
			{
				server.close();
			}
			catch(Exception ex) {}
		}
		
		WXRadio.logger.debug("Thread has stopped");
	}
}
