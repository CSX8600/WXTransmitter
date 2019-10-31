package com.clussmanproductions.wxradio.radio;

import com.clussmanproductions.wxradio.WXRadio;

class RadioThread extends Thread {
	private WeatherStation radio;
	RadioThread(WeatherStation radio)
	{
		this.radio = radio;
	}
	
	@Override
	public void run() {
		WXRadio.logger.debug("Thread has started");
		
		
	}
	
	@Override
	protected void finalize() throws Throwable {
		WXRadio.logger.debug("Thread has stopped");
	}
}
