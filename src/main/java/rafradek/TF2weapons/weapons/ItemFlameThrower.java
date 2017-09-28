package rafradek.TF2weapons.weapons;

import java.util.List;

import cpw.mods.fml.common.registry.IThrowableEntity;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EntityTracker;
import net.minecraft.entity.IProjectile;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.server.S12PacketEntityVelocity;
import net.minecraft.network.play.server.S18PacketEntityTeleport;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import rafradek.TF2weapons.ClientProxy;
import rafradek.TF2weapons.ItemFromData;
import rafradek.TF2weapons.TF2weapons;
import rafradek.TF2weapons.WeaponData.PropertyType;
import rafradek.TF2weapons.building.EntityBuilding;
import rafradek.TF2weapons.projectiles.EntityFlame;
import rafradek.TF2weapons.projectiles.EntityProjectileBase;
import rafradek.TF2weapons.projectiles.EntityStickybomb;
import rafradek.TF2weapons.projectiles.EntitySyringe;

public class ItemFlameThrower extends ItemProjectileWeapon {
	
	@Override
	public boolean canAltFire(World worldObj, EntityLivingBase player,
			ItemStack item) {
		return super.canAltFire(worldObj, player, item);
	}
	@Override
	public boolean canFire(World world, EntityLivingBase living,
			ItemStack stack) {
		return super.canFire(world, living, stack);
	}
	@Override
	public short getAltFiringSpeed(ItemStack item, EntityLivingBase player) {
		return 750;
	}
	@Override
	public boolean startUse(ItemStack stack, EntityLivingBase living, World world, int action, int newState) {
		if(world.isRemote&&(newState&1)-(action&1)==1&&this.canFire(world, living, stack)){
			String playSound=ItemFromData.getSound(stack, PropertyType.FIRE_START_SOUND);
			ClientProxy.playWeaponSound(living, playSound,false,2,stack);
		}
		return false;
	}
	@Override
	public boolean endUse(ItemStack stack, EntityLivingBase living, World world, int action, int newState) {
		if((action&1)==1){
			if(world.isRemote){
				//System.out.println("called"+ClientProxy.fireSounds.get(living));
				if(ClientProxy.fireSounds.get(living)!=null){
					//System.out.println("called2"+ClientProxy.fireSounds.get(living).type);
					ClientProxy.fireSounds.get(living).setDone();
					//Minecraft.getMinecraft().getSoundHandler().stopSound(ClientProxy.fireSounds.get(living));
				}
			}
			living.playSound(ItemFromData.getSound(stack, PropertyType.FIRE_STOP_SOUND), 0.5f, 1f);
		}
		return false;
	}
	
	@Override
	public boolean fireTick(ItemStack stack, EntityLivingBase living, World world) {
		if(world.isRemote&&WeaponsCapability.get(living).fire1Cool<=50&&this.canFire(world, living, stack)){
			if(WeaponsCapability.get(living).startedPress()){
				String playSound=ItemFromData.getSound(stack, PropertyType.FIRE_START_SOUND);
				ClientProxy.playWeaponSound(living, playSound,false,2,stack);
			}
			if(living.isInsideOfMaterial(Material.water)){
				world.spawnParticle("bubble", living.posX, living.posY+living.getEyeHeight()-0.1, living.posZ, living.motionX, 0.2D+living.motionY, living.motionZ);
			}
			else{
				ClientProxy.spawnFlameParticle(world, living,0f);
				ClientProxy.spawnFlameParticle(world, living,0.5f);
			}
			//System.out.println("to: "+ClientProxy.fireSounds.containsKey(living));
			/*if(ClientProxy.fireSounds.containsKey(living)){
				System.out.println("to2: "+Minecraft.getMinecraft().getSoundHandler().isSoundPlaying(ClientProxy.fireSounds.get(living))+" "+ClientProxy.fireSounds.get(living).type);
			}*/
			if(WeaponsCapability.get(living).critTime<=0&&(!ClientProxy.fireSounds.containsKey(living)||!Minecraft.getMinecraft().getSoundHandler().isSoundPlaying(ClientProxy.fireSounds.get(living))||(ClientProxy.fireSounds.get(living).type!=0&&ClientProxy.fireSounds.get(living).type!=2))){
				//new ResourceLocation(ItemFromData.getData(stack).getString(PropertyType.FIRE_LOOP_SOUND));
				
				ClientProxy.playWeaponSound(living, ItemFromData.getSound(stack,PropertyType.FIRE_LOOP_SOUND),true,0,stack);
			}
			else if(WeaponsCapability.get(living).critTime>0&&(!ClientProxy.fireSounds.containsKey(living)||!Minecraft.getMinecraft().getSoundHandler().isSoundPlaying(ClientProxy.fireSounds.get(living))||(ClientProxy.fireSounds.get(living).type!=1))){
				String playSoundCrit=ItemFromData.getData(stack).getString(PropertyType.FIRE_LOOP_SOUND)+".crit";
				
				ClientProxy.playWeaponSound(living, playSoundCrit,true,1,stack);
			}
		}
		//System.out.println("nie");
		return false;
	}
	
	public static boolean isPushable(EntityLivingBase living, Entity target){
		return !(target instanceof EntitySyringe)&&!(target instanceof EntityBuilding)&&!(target instanceof EntityFlame) && !(target instanceof EntityArrow && target.onGround) && !(target instanceof IThrowableEntity&& ((IThrowableEntity)target).getThrower()==living) && !TF2weapons.isOnSameTeam(living, target);
	}
	
	@Override
	public void altUse(ItemStack stack, EntityLivingBase living,
			World world) {
		WeaponsCapability.get(living).fire1Cool=750;
		if(world.isRemote){
			if(ClientProxy.fireSounds.get(living)!=null){
				ClientProxy.fireSounds.get(living).setDone();
				//Minecraft.getMinecraft().getSoundHandler().stopSound(ClientProxy.fireSounds.get(living));
			}
			return;
		}
		//String airblastSound=getData(stack).get("Airblast Sound").getString();
    	living.playSound(ItemFromData.getSound(stack, PropertyType.AIRBLAST_SOUND), 0.5f, 1f);
    	//System.out.println("Sound "+ItemFromData.getSound(stack, PropertyType.AIRBLAST_SOUND));
    	
		Vec3 lookVec=living.getLookVec();
		Vec3 eyeVec=Vec3.createVectorHelper(living.posX, living.posY + living.getEyeHeight(), living.posZ);
		//eyeVec.(lookVec);
		List<Entity> list=world.getEntitiesWithinAABB(Entity.class, AxisAlignedBB.getBoundingBox(eyeVec.xCoord-5, eyeVec.yCoord-5, eyeVec.zCoord-5,
				eyeVec.xCoord+5, eyeVec.yCoord+5, eyeVec.zCoord+5));
		//System.out.println("aiming: "+lookVec+" "+eyeVec+" "+centerVec);
		for (Entity entity : list) {
		
			if(!isPushable(living,entity) || entity.getDistanceSq(living.posX, living.posY + living.getEyeHeight(), living.posZ)>25 || !TF2weapons.lookingAt(living, 45, entity.posX, entity.posY+entity.height/2, entity.posZ)){
				continue;
			}
			
			if(entity instanceof IThrowableEntity && !(entity instanceof EntityStickybomb)){
				((IThrowableEntity)entity).setThrower(living);
			}
			else if(entity instanceof EntityArrow){
				((EntityArrow)entity).shootingEntity=living;
				((EntityArrow)entity).setDamage(((EntityArrow)entity).getDamage()*1.35);
			}
			if(entity instanceof IProjectile){
				IProjectile proj=(IProjectile)entity;
				float speed=(float) Math.sqrt(entity.motionX*entity.motionX+entity.motionY*entity.motionY+entity.motionZ*entity.motionZ)*1.15f;
				List<MovingObjectPosition> rayTraces=TF2weapons.pierce(world, living, eyeVec.xCoord, eyeVec.yCoord, eyeVec.zCoord, eyeVec.xCoord+lookVec.xCoord*256, eyeVec.yCoord+lookVec.yCoord*256, eyeVec.zCoord+lookVec.zCoord*256, false, 0.08f,false);
				if(!rayTraces.isEmpty() && rayTraces.get(0).hitVec != null){
					//System.out.println("hit: "+mop.hitVec);
					proj.setThrowableHeading(rayTraces.get(0).hitVec.xCoord-entity.posX, rayTraces.get(0).hitVec.yCoord-entity.posY,rayTraces.get(0).hitVec.zCoord-entity.posZ, speed, 0);
				}
				else
				{
					proj.setThrowableHeading(eyeVec.xCoord+lookVec.xCoord*256-entity.posX, eyeVec.yCoord+lookVec.yCoord*256-entity.posY, eyeVec.zCoord+lookVec.zCoord*256-entity.posZ, speed, 0);
				}
			}
			else{
				double mult=entity instanceof EntityLivingBase?1.8:1;
				entity.motionX=lookVec.xCoord*0.6*mult;
				entity.motionY=(lookVec.yCoord*0.2+0.36)*mult;
				entity.motionZ=lookVec.zCoord*0.6*mult;
			}
			if(entity instanceof EntityProjectileBase){
				((EntityProjectileBase)entity).setCritical(Math.max(((EntityProjectileBase)entity).getCritical(),1));
			}
			if(!(entity instanceof EntityLivingBase)){
				//String throwObjectSound=getData(stack).get("Airblast Rocket Sound").getString();
		    	entity.playSound(ItemFromData.getSound(stack, PropertyType.AIRBLAST_ROCKET_SOUND), 0.5f, 1f);
		    	
			}
			EntityTracker tracker=((WorldServer)world).getEntityTracker();
			tracker.func_151247_a(entity, new S12PacketEntityVelocity(entity));
			tracker.func_151247_a(entity, new S18PacketEntityTeleport(entity));
		}
	}
}
