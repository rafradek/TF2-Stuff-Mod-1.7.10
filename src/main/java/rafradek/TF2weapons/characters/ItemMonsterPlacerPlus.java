package rafradek.TF2weapons.characters;

import java.util.List;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.BlockLiquid;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.Facing;
import net.minecraft.util.IIcon;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;
import rafradek.TF2weapons.TF2weapons;
import rafradek.TF2weapons.building.EntityBuilding;
import rafradek.TF2weapons.building.EntityDispenser;
import rafradek.TF2weapons.building.EntitySentry;
import rafradek.TF2weapons.building.EntityTeleporter;

public class ItemMonsterPlacerPlus extends Item {
	
	

	private IIcon theIcon;

	public ItemMonsterPlacerPlus()
    {
        this.setHasSubtypes(true);
        this.setCreativeTab(TF2weapons.tabtf2);
    }

    /**
     * Callback for item usage. If the item does something special on right clicking, he will have one of those. Return
     * True if something happen and false if it don't. This is for ITEMS, not BLOCKS
     */
	@Override
	@SideOnly(Side.CLIENT)
    public int getColorFromItemStack(ItemStack stack, int p_82790_2_)
    {
		return stack.getItemDamage()/2==13?0xFFFFFF:(stack.getItemDamage()%2==0?16711680:255);
    }
	
	@Override
	@SideOnly(Side.CLIENT)
    public boolean requiresMultipleRenderPasses()
    {
        return true;
    }

    /**
     * Gets an icon index based on an item's damage value and the given render pass
     */
    @Override
	@SideOnly(Side.CLIENT)
    public IIcon getIconFromDamageForRenderPass(int p_77618_1_, int p_77618_2_)
    {
        return p_77618_2_ > 0 ? this.theIcon : super.getIconFromDamageForRenderPass(p_77618_1_, p_77618_2_);
    }
    @Override
	@SideOnly(Side.CLIENT)
    public void registerIcons(IIconRegister p_94581_1_)
    {
        super.registerIcons(p_94581_1_);
        this.theIcon = p_94581_1_.registerIcon(this.getIconString() + "_overlay");
    }
    /**
     * Callback for item usage. If the item does something special on right clicking, he will have one of those. Return
     * True if something happen and false if it don't. This is for ITEMS, not BLOCKS
     */
    @Override
	public boolean onItemUse(ItemStack p_77648_1_, EntityPlayer p_77648_2_, World p_77648_3_, int p_77648_4_, int p_77648_5_, int p_77648_6_, int p_77648_7_, float p_77648_8_, float p_77648_9_, float p_77648_10_)
    {
        if (p_77648_3_.isRemote)
        {
            return true;
        }
        else
        {
            Block block = p_77648_3_.getBlock(p_77648_4_, p_77648_5_, p_77648_6_);
            p_77648_4_ += Facing.offsetsXForSide[p_77648_7_];
            p_77648_5_ += Facing.offsetsYForSide[p_77648_7_];
            p_77648_6_ += Facing.offsetsZForSide[p_77648_7_];
            double d0 = 0.0D;

            if (p_77648_7_ == 1 && block.getRenderType() == 11)
            {
                d0 = 0.5D;
            }


            EntityLivingBase entity = spawnCreature(p_77648_3_, p_77648_1_.getItemDamage(), p_77648_4_ + 0.5D, p_77648_5_ + d0, p_77648_6_ + 0.5D,
            		 p_77648_1_.getTagCompound()!=null&& p_77648_1_.getTagCompound().hasKey("SavedEntity")? p_77648_1_.getTagCompound().getCompoundTag("SavedEntity"):null);

            if (entity != null)
            {
                if (entity instanceof EntityLivingBase && p_77648_1_.hasDisplayName())
                {
                	((EntityLiving)entity).setCustomNameTag(p_77648_1_.getDisplayName());
                }

      

                if (!p_77648_2_.capabilities.isCreativeMode)
                {
                    --p_77648_1_.stackSize;
                }
                if(entity instanceof EntityBuilding){
    		     	((EntityBuilding)entity).setOwner(p_77648_2_);
    		     	entity.rotationYaw=p_77648_2_.rotationYawHead;
    		     	entity.renderYawOffset=p_77648_2_.rotationYawHead;
    		     	entity.rotationYawHead=p_77648_2_.rotationYawHead;
    		     	if(entity instanceof EntityTeleporter){
    		     		((EntityTeleporter) entity).setExit(p_77648_1_.getItemDamage()>23);
    		     	}
    		     }
            }

            return true;
        }
    }

    /**
     * Applies the data in the EntityTag tag of the given ItemStack to the given Entity.
     */

    @Override
	public ItemStack onItemRightClick(ItemStack p_77659_1_, World p_77659_2_, EntityPlayer p_77659_3_)
    {
        if (p_77659_2_.isRemote)
        {
            return p_77659_1_;
        }
        else
        {
            MovingObjectPosition movingobjectposition = this.getMovingObjectPositionFromPlayer(p_77659_2_, p_77659_3_, true);

            if (movingobjectposition == null)
            {
                return p_77659_1_;
            }
            else
            {
                if (movingobjectposition.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK)
                {
                    int i = movingobjectposition.blockX;
                    int j = movingobjectposition.blockY;
                    int k = movingobjectposition.blockZ;

                    if (!p_77659_2_.canMineBlock(p_77659_3_, i, j, k))
                    {
                        return p_77659_1_;
                    }

                    if (!p_77659_3_.canPlayerEdit(i, j, k, movingobjectposition.sideHit, p_77659_1_))
                    {
                        return p_77659_1_;
                    }

                    if (p_77659_2_.getBlock(i, j, k) instanceof BlockLiquid)
                    {
                    	EntityLivingBase entity = spawnCreature(p_77659_2_, p_77659_1_.getItemDamage(), i, j, k,
                        		p_77659_1_.getTagCompound()!=null&&p_77659_1_.getTagCompound().hasKey("SavedEntity")?p_77659_1_.getTagCompound().getCompoundTag("SavedEntity"):null);

                        if (entity == null)
                        {
                            return p_77659_1_;
                        }
                        else
                        {
                            if (entity instanceof EntityLivingBase && p_77659_1_.hasDisplayName())
                            {
                                ((EntityLiving) entity).setCustomNameTag(p_77659_1_.getDisplayName());
                            }


                            if (!p_77659_3_.capabilities.isCreativeMode)
                            {
                                --p_77659_1_.stackSize;
                            }
                            if(entity instanceof EntityBuilding){
                		     	((EntityBuilding)entity).setOwner(p_77659_3_);
                		     	entity.rotationYaw=p_77659_3_.rotationYawHead;
                		     	entity.renderYawOffset=p_77659_3_.rotationYawHead;
                		     	entity.rotationYawHead=p_77659_3_.rotationYawHead;
                		     	/*if(entity instanceof EntityTeleporter){
                		     		((EntityTeleporter) entity).setExit(p_77659_1_.getItemDamage()>23);
                		     	}*/
                		     }
                            return p_77659_1_;
                        }
                    }
                    
                }
                else
                {
                    return p_77659_1_;
                }
            }
        }
		return p_77659_1_;
    }


    /**
     * Spawns the creature specified by the egg's type in the location specified by the last three parameters.
     * Parameters: world, entityID, x, y, z.
     */
    public static EntityLiving spawnCreature(World par0World, int par1, double par2, double par4, double par6, NBTTagCompound nbtdata)
    {
            EntityLiving entity = null;

            for (int j = 0; j < 1; ++j)
            {
            	if(par1/2==0){
                	entity = new EntityHeavy(par0World);
            	}else if(par1/2==1){
                	entity = new EntityScout(par0World);
            	}else if(par1/2==2){
                	entity = new EntitySniper(par0World);
            	}else if(par1/2==3){
                	entity = new EntitySoldier(par0World);
            	}else if(par1/2==4){
                	entity = new EntityPyro(par0World);
            	}else if(par1/2==5){
                	entity = new EntityDemoman(par0World);
            	}else if(par1/2==6){
                	entity = new EntityMedic(par0World);
            	}else if(par1/2==7){
                	entity = new EntitySpy(par0World);
            	}else if(par1/2==8){
                	entity = new EntityEngineer(par0World);
            	}else if(par1/2==9){
                	entity = new EntitySentry(par0World);
            	}else if(par1/2==10){
                	entity = new EntityDispenser(par0World);
            	}else if(par1/2==11){
                	entity = new EntityTeleporter(par0World);
            	}else if(par1/2==12){
                	entity = new EntityTeleporter(par0World);
            	}else if(par1/2==13){
                	entity = new EntitySaxtonHale(par0World);
            	}
                if (entity != null)
                {
                    EntityLiving entityliving = entity;
                    if(nbtdata!=null){
                    	entityliving.readFromNBT(nbtdata);
                    	//System.out.println("read");
                    }
                    entity.setLocationAndAngles(par2, par4, par6, MathHelper.wrapAngleTo180_float(par0World.rand.nextFloat() * 360.0F), 0.0F);
                    entityliving.rotationYawHead = entityliving.rotationYaw;
                    entityliving.renderYawOffset = entityliving.rotationYaw;
                    TF2CharacterAdditionalData data=new TF2CharacterAdditionalData();
                    data.team=par1%2;
                    entityliving.onSpawnWithEgg(data);
                    entityliving.playLivingSound();
                    if(entity instanceof EntityBuilding){
                    	((EntityBuilding)entity).setEntTeam(par1%2);
                    }
                    if(entity instanceof EntitySaxtonHale && par1%2==1){
                    	((EntitySaxtonHale)entity).setHostile();
                    }
                    if(!par0World.getCollidingBoundingBoxes(entity, entity.boundingBox).isEmpty()){
                    	return null;
                    }
                    par0World.spawnEntityInWorld(entity);
                    
                }
                
            }

           return entity;
    }

    @Override
	@SideOnly(Side.CLIENT)

    /**
     * returns a list of items with the same ID, but different meta (eg: dye returns 16 items)
     */
    public void getSubItems(Item par1, CreativeTabs par2CreativeTabs, List par3List)
    {
    	for(int i=0;i<18;i++)
    		par3List.add(new ItemStack(par1, 1, i));
    	par3List.add(new ItemStack(par1, 1, 26));
    	par3List.add(new ItemStack(par1, 1, 27));
    }

    @Override
	public String getItemStackDisplayName(ItemStack p_77653_1_)
    {
        String s = ("" + StatCollector.translateToLocal(this.getUnlocalizedName() + ".name")).trim();
        int i=p_77653_1_.getItemDamage()/2;
        String s1="Heavy";
        switch(i){
        case 1: s1="Scout"; break;
        case 2: s1="Sniper"; break;
        case 3: s1="Soldier"; break;
        case 4: s1="Pyro"; break;
        case 5: s1="Demoman"; break;
        case 6: s1="Medic"; break;
        case 7: s1="Spy"; break;
        case 8: s1="Engineer"; break;
        case 13: s1="Saxton Hale"; break;
        }
        if(p_77653_1_.getItemDamage()==27){
        	s1= s1.concat(" (Hostile)");
        }
        return s.concat(" "+s1);
    }
}
