package rafradek.TF2weapons.weapons;

import java.util.List;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import rafradek.TF2weapons.ItemFromData;
import rafradek.TF2weapons.TF2weapons;
import rafradek.TF2weapons.WeaponData.PropertyType;
import rafradek.TF2weapons.building.EntityBuilding;
import rafradek.TF2weapons.building.EntitySentry;

public class ItemWrench extends ItemMeleeWeapon {
	
	 @Override
	public void addInformation(ItemStack par1ItemStack, EntityPlayer par2EntityPlayer, List par2List, boolean par4)
	    {
	    	super.addInformation(par1ItemStack, par2EntityPlayer, par2List, par4);
	    	par2List.add("Metal: "+Integer.toString(200-par1ItemStack.getItemDamage())+"/200");
	    }
	@Override
	public boolean onHit(ItemStack stack,EntityLivingBase attacker, Entity target){
		//attacker.swingArm(EnumHand.MAIN_HAND);
		
		if(target instanceof EntityBuilding && TF2weapons.isOnSameTeam(target,attacker)){
			EntityBuilding building=(EntityBuilding)target;
			
			if(building.isSapped()){
				building.removeSapper();
			}
			else{
				boolean useIgnot=false;
				int metalLeft=TF2weapons.getMetal(attacker);
				ItemStack ingot=new ItemStack(Items.iron_ingot);
				if(metalLeft==0&&attacker instanceof EntityPlayer &&((EntityPlayer)attacker).inventory.hasItemStack(ingot)){
					metalLeft=50;
					TF2weapons.setMetal(attacker, 50);
					useIgnot=true;
				}
				int metalUse=0;
				
				if(building.getHealth()<building.getMaxHealth()){
					metalUse=(int)Math.min( (Math.min((building.getMaxHealth()-building.getHealth())*3.333333f, 33)+1),metalLeft);
					building.heal(metalUse*0.3f);
					metalLeft-=metalUse;
				}
				
				if(building instanceof EntitySentry){
					metalUse=Math.min( Math.min(((EntitySentry) building).getMaxAmmo()-((EntitySentry)building).getAmmo(), 40),metalLeft);
					((EntitySentry)building).setAmmo(Math.min(((EntitySentry) building).getMaxAmmo(),((EntitySentry)building).getAmmo()+metalUse));
					metalLeft-=metalUse;
					if(building.getLevel()==3){
						metalUse=Math.min( Math.min(20-((EntitySentry)building).getRocketAmmo(), 8),metalLeft/2);
						((EntitySentry)building).setRocketAmmo(Math.min(20,((EntitySentry)building).getRocketAmmo()+metalUse));
						metalLeft-=metalUse*2;
					}
				}
				if(building.getLevel()<3){
					metalUse=Math.min( Math.min(200-building.getProgress(), 25),metalLeft);
					building.setProgress(Math.min(building.getProgress()+metalUse,200));
					metalLeft-=metalUse;
					if(building.getProgress()>=200){
						building.upgrade();
					}
				}
				
				building.playSound(ItemFromData.getSound(stack,metalLeft!=TF2weapons.getMetal(attacker)?PropertyType.BUILD_HIT_SUCCESS_SOUND:PropertyType.BUILD_HIT_FAIL_SOUND), 0.55f, 1f);
				//System.out.println("metal: "+TF2weapons.getMetal(attacker)+" used: "+metalLeft);
				if(useIgnot && metalLeft !=50){
					((EntityPlayer)attacker).inventory.consumeInventoryItem(Items.iron_ingot);
				}
				
				if(!(attacker instanceof EntityPlayer && ((EntityPlayer)attacker).capabilities.isCreativeMode)){
					TF2weapons.setMetal(attacker, metalLeft);
				}
			}
			
			return false;
		}
		return true;
	}
	/*public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged)
    {
        return super.shouldCauseReequipAnimation(oldStack, newStack, slotChanged);
    }*/
	@Override
	public int getMaxDamage(ItemStack stack)
    {
		return 200;
    }
}
