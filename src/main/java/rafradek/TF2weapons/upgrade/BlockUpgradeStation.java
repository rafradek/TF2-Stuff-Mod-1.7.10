package rafradek.TF2weapons.upgrade;

import java.util.List;
import java.util.Random;

import cpw.mods.fml.common.network.internal.FMLNetworkHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import rafradek.TF2weapons.TF2weapons;

public class BlockUpgradeStation extends BlockContainer {

	
	public BlockUpgradeStation() {
		super(Material.iron);
		this.setStepSound(soundTypeMetal);
        this.setCreativeTab(TF2weapons.tabtf2);
	}

	public String getItemIconName(){
		return TF2weapons.MOD_ID+":upgrade_station";
	}
	@Override
	public TileEntity createNewTileEntity(World worldIn, int meta) {
		if((meta&8)==8){
			TileEntityUpgrades upgrades=new TileEntityUpgrades(worldIn);
			//upgrades.generateUpgrades();
			return upgrades;
		}
		return null;
	}

	@Override
	public boolean onBlockActivated(World worldIn, int x, int y, int z, EntityPlayer playerIn, int side, float hitX, float hitY, float hitZ)
    {
        if (!worldIn.isRemote)
        {
        	if(worldIn.getTileEntity(x, y, z) != null)
        		FMLNetworkHandler.openGui(playerIn, TF2weapons.instance, 2, worldIn, x, y, z);
        	else{
        		for(int x1=-1;x1<2;x1++){
        			for(int y1=-1;y1<2;y1++){
        				for(int z1=-1;z1<2;z1++){
                			if(worldIn.getBlock(x+x1, y+y1, z+z1) instanceof BlockUpgradeStation && worldIn.getBlockMetadata(x+x1, y+y1, z+z1)>=8){
                				FMLNetworkHandler.openGui(playerIn, TF2weapons.instance, 2, worldIn, x+x1, y+y1, z+z1);
                				return true;
                			}
                		}
            		}
        		}
        	}
        }
        return true;
    }

	
	/*public IBlockState onBlockPlaced(World worldIn, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer)
    {
        return this.getDefaultState().withProperty(FACING, placer.getHorizontalFacing().getOpposite());
    }*/
	@Override
	public void onBlockPlacedBy(World p_149689_1_, int p_149689_2_, int p_149689_3_, int p_149689_4_, EntityLivingBase p_149689_5_, ItemStack p_149689_6_)
    {
        int l = MathHelper.floor_double(p_149689_5_.rotationYaw * 4.0F / 360.0F + 0.5D) & 3;

        if (l == 0)
        {
            p_149689_1_.setBlockMetadataWithNotify(p_149689_2_, p_149689_3_, p_149689_4_, 2+(p_149689_6_.getItemDamage()&8), 2);
        }

        if (l == 1)
        {
            p_149689_1_.setBlockMetadataWithNotify(p_149689_2_, p_149689_3_, p_149689_4_, 5+(p_149689_6_.getItemDamage()&8), 2);
        }

        if (l == 2)
        {
            p_149689_1_.setBlockMetadataWithNotify(p_149689_2_, p_149689_3_, p_149689_4_, 3+(p_149689_6_.getItemDamage()&8), 2);
        }

        if (l == 3)
        {
            p_149689_1_.setBlockMetadataWithNotify(p_149689_2_, p_149689_3_, p_149689_4_, 4+(p_149689_6_.getItemDamage()&8), 2);
        }
    }
	@SideOnly(Side.CLIENT)
    public void registerBlockIcons(IIconRegister p_149651_1_)
    {
        
    }
    /**
     * Returns the blockstate with the given mirror of the passed blockstate. If inapplicable, returns the passed
     * blockstate.
     */
	 @SideOnly(Side.CLIENT)
    public IIcon getIcon(int p_149691_1_, int p_149691_2_)
    {
        return Blocks.planks.getBlockTextureFromSide(p_149691_1_);
    }
	 
    @Override
	@SideOnly(Side.CLIENT)
    public void getSubBlocks(Item itemIn, CreativeTabs tab, List list)
    {
        list.add(new ItemStack(itemIn, 1, 8));
    }

    @Override
	public Item getItemDropped(int state, Random rand, int fortune)
    {
        return null;
    }
    public int getRenderType()
    {
        return -1;
    }

    @Override
	public boolean isOpaqueCube()
    {
        return false;
    }
    
    @Override
	public boolean renderAsNormalBlock()
    {
        return false;
    }

}
