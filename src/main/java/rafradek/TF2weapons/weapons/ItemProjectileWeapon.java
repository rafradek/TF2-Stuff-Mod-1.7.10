package rafradek.TF2weapons.weapons;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import rafradek.TF2weapons.ItemFromData;
import rafradek.TF2weapons.MapList;
import rafradek.TF2weapons.TF2Attribute;
import rafradek.TF2weapons.WeaponData.PropertyType;
import rafradek.TF2weapons.projectiles.EntityProjectileBase;

public class ItemProjectileWeapon extends ItemWeapon {

	@Override
	public void shoot(ItemStack stack, EntityLivingBase living, World world,
			int thisCritical) {
		if(!world.isRemote){
			//System.out.println("Tick: "+living.ticksExisted);
			EntityProjectileBase proj;
			/*double oldX=living.posX;
			double oldY=living.posY;
			double oldZ=living.posZ;
			float oldPitch=living.rotationPitch;
			float oldYaw=living.rotationYawHead;
			if(this.usePrediction()&&living instanceof EntityPlayer){
				PredictionMessage message=TF2ProjectileHandler.nextShotPos.get(living);
				living.posX=message.x;
				living.posY=message.y;
				living.posZ=message.z;
				living.rotationYawHead=message.yaw;
				living.rotationPitch=message.pitch;
			}*/
			try {
				proj = MapList.projectileClasses.get(ItemFromData.getData(stack).getString(PropertyType.PROJECTILE)).getConstructor(World.class,EntityLivingBase.class).newInstance(world,living);
				//proj.setIsCritical(thisCritical);
				world.spawnEntityInWorld(proj);
				proj.setCritical(thisCritical);
			} catch (Exception exception){
				exception.printStackTrace();
			}
			}
			//living.posX=oldX;
			//living.posY=oldY;
			//living.posZ=oldZ;
			//living.rotationPitch=oldPitch;
			//living.rotationYawHead=oldYaw;
	}
	public float getProjectileSpeed(ItemStack stack,EntityLivingBase living){
		return TF2Attribute.getModifier("Proj Speed", stack, ItemFromData.getData(stack).getFloat(PropertyType.PROJECTILE_SPEED),living);
	}
	@Override
    public boolean canFire(World world, EntityLivingBase living, ItemStack stack)
    {
    	return /*(((!(living instanceof EntityPlayer) || ) || TF2ProjectileHandler.nextShotPos.containsKey(living))||world.isRemote*/super.canFire(world, living, stack);
    }
}
