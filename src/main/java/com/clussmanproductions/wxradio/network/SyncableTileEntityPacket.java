package com.clussmanproductions.wxradio.network;

import com.clussmanproductions.wxradio.tileentity.SyncableTileEntity;

import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class SyncableTileEntityPacket implements IMessage {

	public NBTTagCompound tag;
	@Override
	public void fromBytes(ByteBuf buf) {
		tag = ByteBufUtils.readTag(buf);
		
	}

	@Override
	public void toBytes(ByteBuf buf) {
		ByteBufUtils.writeTag(buf, tag);
	}

	public static class Handler implements IMessageHandler<SyncableTileEntityPacket, IMessage>
	{
		@Override
		public IMessage onMessage(SyncableTileEntityPacket message, MessageContext ctx) {
			FMLCommonHandler.instance().getWorldThread(ctx.netHandler).addScheduledTask(() -> handle(message, ctx));
			return null;
		}
		
		private void handle(SyncableTileEntityPacket message, MessageContext ctx)
		{
			NBTTagCompound tag = message.tag;
			BlockPos tileEntityPos = BlockPos.fromLong(tag.getLong("pos"));
			World world = ctx.getServerHandler().player.world;
			TileEntity te = world.getTileEntity(tileEntityPos);
			
			if (te instanceof SyncableTileEntity)
			{
				SyncableTileEntity ste = (SyncableTileEntity)te;
				ste.handleClientToServerTag(tag);
			}
		}
	}
}
