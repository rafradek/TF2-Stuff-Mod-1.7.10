package rafradek.TF2weapons.weapons;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import rafradek.TF2weapons.TF2Attribute;
import rafradek.TF2weapons.message.TF2Message.PredictionMessage;

public class ItemMeleeWeapon extends ItemBulletWeapon {

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
	
	@Override
	public boolean use(ItemStack stack, EntityLivingBase living, World world, PredictionMessage message)
    {
		ItemWeapon.shouldSwing=true;
		living.swingItem();
		ItemWeapon.shouldSwing=false;
		return super.use(stack, living, world,message);
	}
	
	@Override
	public float critChance(ItemStack stack, Entity entity){
    	float chance=0.15f;
    	if(ItemUsable.lastDamage.containsKey(entity)){
			for(int i=0;i<20;i++){
				chance+=ItemUsable.lastDamage.get(entity)[i]/177;
			}
    	}
    	return Math.min(chance,0.6f);
    }
}
