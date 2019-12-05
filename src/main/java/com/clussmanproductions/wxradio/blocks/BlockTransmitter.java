package com.clussmanproductions.wxradio.blocks;

import com.clussmanproductions.wxradio.WXRadio;
import com.clussmanproductions.wxradio.gui.GuiProxy;
import com.clussmanproductions.wxradio.tileentity.TransmitterTileEntity;

import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
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
	
	@Override
	public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn,
			EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
		if (!worldIn.isRemote)
		{
			return true;
		}
		
		TileEntity te = worldIn.getTileEntity(pos);
		
		if (!(te instanceof TransmitterTileEntity))
		{
			return false;
		}
		
		playerIn.openGui(WXRadio.instance, GuiProxy.GUI_IDs.TRANSMITTER, worldIn, pos.getX(), pos.getY(), pos.getZ());
		return true;
	}
}
