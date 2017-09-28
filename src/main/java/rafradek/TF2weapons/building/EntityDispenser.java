package rafradek.TF2weapons.building;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.command.IEntitySelector;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.play.server.S2FPacketSetSlot;
import net.minecraft.world.World;
import rafradek.TF2weapons.TF2weapons;
import rafradek.TF2weapons.characters.EntityTF2Character;
import rafradek.TF2weapons.weapons.ItemCloak;
import rafradek.TF2weapons.weapons.ItemWrench;

public class EntityDispenser extends EntityBuilding {

	public int reloadTimer;
	public int giveAmmoTimer;
	public List<EntityLivingBase> dispenserTarget;
	
	public EntityDispenser(World worldIn) {
		super(worldIn);
		this.setSize(1f, 1.1f);
		this.dispenserTarget=new ArrayList<>();
	}
	public EntityDispenser(World worldIn,EntityLivingBase living) {
		super(worldIn,living);
		this.setSize(1f, 1.1f);
		this.dispenserTarget=new ArrayList<>();
	}
	@Override
	@SuppressWarnings("unchecked")
	public void onLivingUpdate(){
		super.onLivingUpdate();
		if(this.isDisabled()){
			this.dispenserTarget.clear();
			return;
		}
		
		List<EntityLivingBase> targetList=this.worldObj.getEntitiesWithinAABBExcludingEntity(null, this.boundingBox.expand(2, 1.5d, 2), new IEntitySelector(){

			@Override
			public boolean isEntityApplicable(Entity input) {
				
				return input instanceof EntityLivingBase && (!(input instanceof EntityBuilding)&&EntityDispenser.this!=input&&((TF2weapons.dispenserHeal&&input instanceof EntityPlayer && getTeam()==null && ((EntityLivingBase) input).getTeam() == null)||TF2weapons.isOnSameTeam(EntityDispenser.this,input)));
			}
			
		});
		if(!this.worldObj.isRemote){
			this.reloadTimer--;
			if(this.reloadTimer<=0&&this.getMetal()<400){
				int metalAmount=TF2weapons.fastMetalProduction?30:21;
				this.setMetal(Math.min(400,this.getMetal()+metalAmount+this.getLevel()*(metalAmount/3)));
				//System.out.println("MetalGenerated "+this.getMetal());
				this.playSound(TF2weapons.MOD_ID+":mob.dispenser.generatemetal", 0.75f, 1f);
				this.reloadTimer=TF2weapons.fastMetalProduction?100:200;
			}
			this.giveAmmoTimer--;
			
			for(EntityLivingBase living:targetList){
				int level=this.getLevel();
				living.heal(0.025f+0.025f*level);
				if(this.giveAmmoTimer==0){
					if(living instanceof EntityTF2Character || (living.getHeldItem() !=null &&living.getHeldItem().getItem() instanceof ItemWrench)){
						int metalUse=Math.min(30+this.getLevel()*10,Math.min(200-TF2weapons.getMetal(living),this.getMetal()));
						this.setMetal(this.getMetal()-metalUse);
						TF2weapons.setMetal(living, TF2weapons.getMetal(living)+metalUse);
						
						if(living instanceof EntityPlayerMP){
							((EntityPlayerMP)living).updateHeldItem();
						}
					}
					if(living.getHeldItem()!=null && living.getHeldItem().getItem().isRepairable()&&living.getHeldItem().getItemDamage()!=0){
						
						float repairMult=3f;
						NBTTagList list=living.getHeldItem().getEnchantmentTagList();
						if(list!=null){
							for(int i=0;i<list.tagCount();i++){
								repairMult-=list.getCompoundTagAt(i).getShort("lvl")*0.2f;
							}
							if(repairMult<=1f){
								repairMult=1f;
							}
						}
						int metalUse=Math.min(15+this.getLevel()*10,Math.min((int)(living.getHeldItem().getItemDamage()/repairMult)+1,this.getMetal()));
						this.setMetal(this.getMetal()-metalUse);
						living.getHeldItem().setItemDamage(living.getHeldItem().getItemDamage()-(int)(metalUse*repairMult));
						
						if(living instanceof EntityPlayerMP){
							((EntityPlayerMP)living).updateHeldItem();
						}
					}
				}
				int cloakSlot=ItemCloak.searchForWatchesSlot(living);
				ItemStack cloak=living instanceof EntityPlayer && cloakSlot>=0?((EntityPlayer)living).inventory.mainInventory[cloakSlot]:null;
				if(cloak !=null || (living.getHeldItem() !=null && living.getHeldItem().getItem() instanceof ItemCloak)){
					if(cloak == null)
						cloak=living.getHeldItem();
					
					cloak.setItemDamage(Math.max(cloak.getItemDamage()-(2+this.getLevel()),0));
					
					if(living instanceof EntityPlayerMP){
						((EntityPlayerMP)living).playerNetServerHandler.sendPacket(new S2FPacketSetSlot(-1, cloakSlot, cloak));
					}
				}
				if(this.dispenserTarget!=null&&!this.dispenserTarget.contains(living)){
					this.playSound(TF2weapons.MOD_ID+":mob.dispenser.heal", 0.75f, 1f);
				}
			}
			if(this.giveAmmoTimer<=0){
				this.giveAmmoTimer=20;
			}
		}
		this.dispenserTarget=targetList;
	}
	@Override
	public String getSoundNameForState(int state){
		switch(state){
		case 0:return TF2weapons.MOD_ID+":mob.dispenser.idle";
		default:return super.getSoundNameForState(state);
		}
	}
	@SuppressWarnings("unchecked")
	public static boolean isNearDispenser(World world, final EntityLivingBase living){
		List<EntityDispenser> targetList=world.getEntitiesWithinAABB(EntityDispenser.class,living.boundingBox.expand(2.5D, 2D, 2.5D));
		for(EntityDispenser input:targetList){
			if(!input.isDisabled()&&input.dispenserTarget != null &&input.dispenserTarget.contains(living)){
				return true;
			}
		}
		return false;
	}
	@Override
	protected void entityInit()
	{
		super.entityInit();
		this.getDataWatcher().addObject(16, (short)0);
	}
	public int getMetal(){
		return this.getDataWatcher().getWatchableObjectShort(16);
	}
	public void setMetal(int amount){
		this.getDataWatcher().updateObject(16, (short)amount);
	}
	@Override
	public void upgrade(){
		super.upgrade();
		this.setMetal(this.getMetal()+25);
	}
	@Override
	protected String getHurtSound()
    {
        return null;
    }

    @Override
	protected String getDeathSound()
    {
        return TF2weapons.MOD_ID+":mob.dispenser.death";
    }
    @Override
	public void writeEntityToNBT(NBTTagCompound par1NBTTagCompound)
    {
        super.writeEntityToNBT(par1NBTTagCompound);
        
        par1NBTTagCompound.setShort("Metal", (short) this.getMetal());
    }
	@Override
	public void readEntityFromNBT(NBTTagCompound par1NBTTagCompound)
    {
        super.readEntityFromNBT(par1NBTTagCompound);
        
        this.setMetal(par1NBTTagCompound.getShort("Metal"));
    }
}
