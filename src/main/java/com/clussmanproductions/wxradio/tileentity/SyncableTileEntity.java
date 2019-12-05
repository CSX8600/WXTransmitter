package com.clussmanproductions.wxradio.tileentity;

import com.clussmanproductions.wxradio.network.PacketHandler;
import com.clussmanproductions.wxradio.network.SyncableTileEntityPacket;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;

public abstract class SyncableTileEntity extends TileEntity {
	public abstract NBTTagCompound getClientToServerTag();
	public abstract void handleClientToServerTag(NBTTagCompound compound);
	public void syncClientToServer()
	{
		NBTTagCompound tag = getClientToServerTag();
		tag.setLong("pos", getPos().toLong());
		
		SyncableTileEntityPacket packet = new SyncableTileEntityPacket();
		packet.tag = tag;
		PacketHandler.INSTANCE.sendToServer(packet);
	}
}
