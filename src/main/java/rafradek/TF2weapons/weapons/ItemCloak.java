package rafradek.TF2weapons.weapons;

import java.util.List;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import rafradek.TF2weapons.ItemFromData;
import rafradek.TF2weapons.TF2EventBusListener;
import rafradek.TF2weapons.TF2weapons;
import rafradek.TF2weapons.WeaponData.PropertyType;
import rafradek.TF2weapons.characters.EntitySpy;
import rafradek.TF2weapons.message.TF2Message;

public class ItemCloak extends ItemFromData {

	@Override
	public void onUpdate(ItemStack par1ItemStack, World par2World, Entity par3Entity, int par4, boolean par5)
   	{
		super.onUpdate(par1ItemStack, par2World, par3Entity, par4, par5);
		if(par1ItemStack.getTagCompound().getBoolean("Active")&&par3Entity.getEntityData().getBoolean("IsCloaked")){
			//System.out.println("uncharge");
			par1ItemStack.setItemDamage(Math.min(600,par1ItemStack.getItemDamage()+3));
			if(par1ItemStack.getTagCompound().getBoolean("Strange")){
				par1ItemStack.getTagCompound().setInteger("CloakTicks",par1ItemStack.getTagCompound().getInteger("CloakTicks")+1);
				if(par1ItemStack.getTagCompound().getInteger("CloakTicks")%20==0)
					TF2EventBusListener.onStrangeUpdate(par1ItemStack,(EntityLivingBase) par3Entity);
			}
			if(par1ItemStack.getItemDamage()>=600){
				par1ItemStack.setItemDamage(600);
				this.setCloak(false, par1ItemStack, (EntityLivingBase) par3Entity, par2World);
			}
		}
		else if(par1ItemStack.getTagCompound().getBoolean("Active")&&!par3Entity.getEntityData().getBoolean("IsCloaked")){
			par1ItemStack.getTagCompound().setBoolean("Active",false);
		}
		else{
			par1ItemStack.setItemDamage(Math.max(par1ItemStack.getItemDamage()-1,0));
			
		}
   	}
	@Override
	public int getMaxDamage(ItemStack stack)
    {
		return 600;
    }
	public boolean canAltFire(World worldObj, EntityLivingBase player,
			ItemStack item) {
		// TODO Auto-generated method stub
		return false;
	}
	@Override
	public ItemStack onItemRightClick(ItemStack stack, World world,EntityPlayer living) {
		if(living.isInvisible()||stack.getItemDamage()<528){
			this.setCloak(!living.getEntityData().getBoolean("IsCloaked"), stack, living, world);
		}
		return stack;
	}
	
	@Override
	public boolean onDroppedByPlayer(ItemStack item, EntityPlayer player)
    {
		if(item.getTagCompound().getBoolean("Active")){
			this.setCloak(false, item, player, player.worldObj);
			
		}
		return super.onDroppedByPlayer(item, player);
    }
	public static ItemStack searchForWatches(EntityLivingBase living){
		if(living instanceof EntitySpy) return ((EntitySpy)living).loadout[3];
		if(living instanceof EntityPlayer){
			EntityPlayer player=(EntityPlayer) living;
			for(ItemStack stack:player.inventory.mainInventory){
				if(stack != null && stack.getItem() instanceof ItemCloak && stack.getTagCompound().getBoolean("Active")){
					//System.out.println("Found hand");
					return stack;
				}
			}
			
		}
		return null;
	}
	public static int searchForWatchesSlot(EntityLivingBase living){
		if(living instanceof EntitySpy) return 3;
		if(living instanceof EntityPlayer){
			EntityPlayer player=(EntityPlayer) living;
			for(int i=0; i<player.inventory.mainInventory.length;i++){
				ItemStack stack=player.inventory.mainInventory[i];
				if(stack != null && stack.getItem() instanceof ItemCloak && stack.getTagCompound().getBoolean("Active")){
					return i;
				}
			}
			
		}
		return -1;
	}
	public void setCloak(boolean active,ItemStack stack, EntityLivingBase living,
			World world){
		//System.out.println("set active: "+active);
		if(!active || !(living instanceof EntityPlayer) || searchForWatches(living)==null){
			if(!active){
				living.setInvisible(false);
				WeaponsCapability.get(living).invisTicks=20;
			}
			//System.out.println("ok: "+active);
			stack.getTagCompound().setBoolean("Active", active);
			living.getEntityData().setBoolean("IsCloaked",active);
			
			//setInvisiblity(living);
			if(active){
				living.playSound(ItemFromData.getSound(stack, PropertyType.CLOAK_SOUND), 1f, 1);
			}
			else{
				living.playSound(ItemFromData.getSound(stack, PropertyType.DECLOAK_SOUND), 1f, 1);
			}
			if(!world.isRemote){
				TF2weapons.network.sendToAllAround(new TF2Message.PropertyMessage("IsCloaked", (byte)(active?1:0),living),TF2weapons.pointFromEntity(living));
			}
		}
	}
	public static void setInvisiblity(EntityLivingBase living){
		boolean cloaked=WeaponsCapability.get(living).invisTicks>=20;
		boolean disguised=living.getEntityData().getBoolean("IsDisguised");
		living.setInvisible(cloaked||(disguised&&WeaponsCapability.get(living).invisTicks==0&&!living.getEntityData().getBoolean("IsCloaked")));
	}
	@Override
	public void addInformation(ItemStack par1ItemStack, EntityPlayer par2EntityPlayer, List par2List, boolean par4)
    {
    	super.addInformation(par1ItemStack, par2EntityPlayer, par2List, par4);
       
        par2List.add("Charge: "+(100-par1ItemStack.getItemDamage()/6)+"%");
    }
}
