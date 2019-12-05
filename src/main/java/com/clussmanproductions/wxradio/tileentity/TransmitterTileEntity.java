package com.clussmanproductions.wxradio.tileentity;

import com.clussmanproductions.wxradio.radio.WeatherStation;
import com.clussmanproductions.wxradio.radio.WeatherStationManager;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;

public class TransmitterTileEntity extends SyncableTileEntity {
	// These members are for client-only
	// Server sided operations will be using the WeatherStation values
	private String name;
	@Override
	public void readFromNBT(NBTTagCompound compound) {
		
		super.readFromNBT(compound);
	}
	
	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound compound) {
		
		return super.writeToNBT(compound);
	}
	
	@Override
	public NBTTagCompound getUpdateTag() {
		WeatherStation station = getWeatherStation();
		
		NBTTagCompound tag = super.getUpdateTag();
		tag.setString("name", station.getName());
		
		return tag;
	}
	
	@Override
	public void handleUpdateTag(NBTTagCompound tag) {
		super.handleUpdateTag(tag);
		name = tag.getString("name");
	}
	
	@Override
	public SPacketUpdateTileEntity getUpdatePacket() {
		return new SPacketUpdateTileEntity(getPos(), 0, getUpdateTag());
	}
	
	@Override
	public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity pkt) {
		super.onDataPacket(net, pkt);
		handleUpdateTag(pkt.getNbtCompound());
	}
	
	@Override
	public void onLoad() {
		if (world.isRemote)
		{
			return;
		}
		
		WeatherStationManager manager = WeatherStationManager.get(world);
		manager.createWeatherStation(getPos());
	}
	
	private WeatherStation getWeatherStation()
	{
		WeatherStationManager manager = WeatherStationManager.get(world);
		return manager.getWeatherStation(getPos());
	}
	
	public String getName()
	{
		if (world.isRemote)
		{
			return name;
		}
		else
		{
			return getWeatherStation().getName();
		}
	}

	public void setName(String newName)
	{
		if (world.isRemote)
		{
			name = newName;
		}
		else
		{
			getWeatherStation().setName(newName);
		}
	}
	
	@Override
	public NBTTagCompound getClientToServerTag() {
		NBTTagCompound compound = new NBTTagCompound();
		compound.setString("name", name);
		return compound;
	}

	@Override
	public void handleClientToServerTag(NBTTagCompound compound) {
		WeatherStation station = getWeatherStation();
		String name = compound.getString("name");
		station.setName(name);
	}
}
