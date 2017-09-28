package rafradek.TF2weapons.weapons;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import rafradek.TF2weapons.WeaponData;

public class WeaponLoopSound extends WeaponSound {

	
	private boolean firing;
	private boolean crit;

	public WeaponLoopSound(ResourceLocation p_i45104_1_,EntityLivingBase entity,boolean firing, WeaponData conf,boolean crit, int type) {
		super(p_i45104_1_, entity, type, conf);
		this.repeat=true;
		this.firing=firing;
		this.crit=crit;
	}

	@Override
	public void update() {
		super.update();
		if(this.endsnextTick||this.donePlaying){
			return;
		}
		ItemStack stack=this.entity.getHeldItem();
		boolean playThis=(WeaponsCapability.get(entity).critTime>0&&crit)||(WeaponsCapability.get(entity).critTime<=0&&!crit);
		if(((ItemUsable)stack.getItem()).canFire(entity.worldObj, entity, stack)/*stack.getTagCompound().getShort("minigunticks")>=17*TF2Attribute.getModifier("Minigun Spinup", stack, 1,entity)*/){
			int action=WeaponsCapability.get(entity).state;
			if(((action&1)!=0&&firing&&playThis)||(!firing&&(action&3)==2)){
				//this.volume=1.0f;
			}
			else{
				this.setDone();
			}
		}
		else{
			this.setDone();
		}
	}
	
}
