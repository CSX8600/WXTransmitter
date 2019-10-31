package com.clussmanproductions.wxradio.blocks;

import com.clussmanproductions.wxradio.WXRadio;
import com.clussmanproductions.wxradio.tileentity.TransmitterTileEntity;

import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class BlockTransmitter extends Block implements ITileEntityProvider {
	public BlockTransmitter()
	{
		super(Material.IRON);
		setRegistryName("transmitter");
		setUnlocalizedName(WXRadio.MODID + ".transmitter");
		setHardness(2f);
		setCreativeTab(CreativeTabs.MISC);
	}

	@Override
	public TileEntity createNewTileEntity(World worldIn, int meta) {
		return new TransmitterTileEntity();
	}
}
