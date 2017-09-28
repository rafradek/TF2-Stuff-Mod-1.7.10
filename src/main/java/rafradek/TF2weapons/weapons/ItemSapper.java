package rafradek.TF2weapons.weapons;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import rafradek.TF2weapons.TF2weapons;
import rafradek.TF2weapons.building.EntityBuilding;

public class ItemSapper extends ItemBulletWeapon {

	public ItemSapper()
    {
        super();
        this.setMaxStackSize(64);
    }
	
	@Override
	public boolean onHit(ItemStack stack,EntityLivingBase attacker, Entity target){
		//System.out.println("Can hit: " + TF2weapons.canHit(attacker, target));
		if(target instanceof EntityBuilding&&!((EntityBuilding)target).isSapped()&&TF2weapons.canHit(attacker, target)){
			((EntityBuilding)target).setSapped(attacker,stack);
			((EntityBuilding)target).playSound(TF2weapons.MOD_ID+":mob.sapper.plant", 0.8f, 1);
			if(((EntityBuilding)target).getOwner()!=null){
				((EntityBuilding)target).getOwner().setRevengeTarget(attacker);
			}
			stack.stackSize--;
			if(stack.stackSize<=0&&attacker instanceof EntityPlayer){
				((EntityPlayer)attacker).inventory.setInventorySlotContents(((EntityPlayer)attacker).inventory.currentItem, null);
				
			}
		}
		return false;
	}
	
	@Override
	public float getMaxRange(){
		return 2.4f;
	}
	
	public float getBulletSize(){
		return 0.35f;
	}
	
	@Override
	public boolean showTracer(ItemStack stack){
		return false;
	}
}
