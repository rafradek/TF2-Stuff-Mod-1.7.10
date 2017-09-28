package rafradek.TF2weapons.characters.ai;

import java.util.List;

import net.minecraft.entity.Entity;
import net.minecraft.entity.IProjectile;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.Vec3;
import net.minecraft.world.EnumDifficulty;
import rafradek.TF2weapons.TF2weapons;
import rafradek.TF2weapons.characters.EntityTF2Character;
import rafradek.TF2weapons.weapons.ItemFlameThrower;
import rafradek.TF2weapons.weapons.WeaponsCapability;

public class EntityAIAirblast extends EntityAIBase {

	public int delay;
	public EntityTF2Character host;
	public EntityAIAirblast(EntityTF2Character entity){
		host=entity;
		this.setMutexBits(0);
	}
	@Override
	public boolean shouldExecute() {
		// TODO Auto-generated method stub
		//System.out.println("executing "+TF2ActionHandler.playerAction.server.get(host));
		//System.out.println("should execute: "+(host.worldObj.difficultySetting.getDifficultyId()>=2));
		return this.host.getHeldItem() != null && host.worldObj.difficultySetting.getDifficultyId()>=2;
		
	}
	@Override
	@SuppressWarnings("unchecked")
	public void updateTask() {
		//System.out.println("executing "+TF2ActionHandler.playerAction.server.get(host));
		boolean easier=host.worldObj.difficultySetting==EnumDifficulty.NORMAL;
		delay--;
		if(delay>0||this.host.getRNG().nextFloat()>(easier?0.22f:0.28f)) {
			WeaponsCapability.get(host).state&=5;//System.out.println("reset:");
			return;
		}
		Vec3 eyeVec=Vec3.createVectorHelper(host.posX, host.posY + host.getEyeHeight(), host.posZ);
		List<Entity> list=host.worldObj.getEntitiesWithinAABB(Entity.class, AxisAlignedBB.getBoundingBox(eyeVec.xCoord-5, eyeVec.yCoord-5, eyeVec.zCoord-5,
				eyeVec.xCoord+5, eyeVec.yCoord+5, eyeVec.zCoord+5));
		boolean airblast=false;
		for (Entity entity : list) {
			//System.out.println(entity+" "+ItemFlameThrower.isPushable(host,entity));
			if(ItemFlameThrower.isPushable(host,entity)&&(entity instanceof EntityThrowable || entity instanceof IProjectile)){
				//System.out.println(entity);
				//System.out.println("dystans: "+(entity.getDistanceSq(host.posX, host.posY + (double)host.getEyeHeight(), host.posZ)<25));
				//System.out.println(TF2weapons.getTeam(entity)+" "+TF2weapons.getTeam(host));
				airblast= entity.getDistanceSq(host.posX, host.posY + host.getEyeHeight(), host.posZ)<(easier?16:25) && TF2weapons.lookingAt(host, (easier?30:45), entity.posX, entity.posY+entity.height/2, entity.posZ);
				if(airblast){
					break;
				}
			}
		}
		
		if(airblast){
			((ItemFlameThrower) this.host.getHeldItem().getItem()).altUse(this.host.getHeldItem(), host, this.host.worldObj);
			this.delay=easier?30:18;
		}
		else{
			WeaponsCapability.get(host).state&=5;
		}
		//System.out.println("end executing "+TF2ActionHandler.playerAction.server.get(host));
	}
}
