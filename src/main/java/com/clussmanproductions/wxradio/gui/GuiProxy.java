package com.clussmanproductions.wxradio.gui;

import com.clussmanproductions.wxradio.tileentity.TransmitterTileEntity;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.IGuiHandler;

public class GuiProxy implements IGuiHandler {

	@Override
	public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
		return null;
	}

	@Override
	public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
		switch(ID)
		{
			case GUI_IDs.TRANSMITTER:
				BlockPos blockPos = new BlockPos(x, y, z);
				TileEntity te = world.getTileEntity(blockPos);
				if (te instanceof TransmitterTileEntity)
				{
					TransmitterTileEntity transmitter = (TransmitterTileEntity)te;
					return new TransmitterGui(transmitter);
				}
				break;
		}
		
		return null;
	}

	public static class GUI_IDs
	{
		public static final int TRANSMITTER = 1;
	}
}
