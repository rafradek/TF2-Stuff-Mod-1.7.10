package rafradek.TF2weapons.characters.ai;

import java.util.List;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import rafradek.TF2weapons.ItemFromData;
import rafradek.TF2weapons.MapList;
import rafradek.TF2weapons.TF2weapons;
import rafradek.TF2weapons.WeaponData.PropertyType;
import rafradek.TF2weapons.building.EntitySentry;
import rafradek.TF2weapons.projectiles.EntityProjectileBase;

public class EntityAISentryAttack extends EntityAIBase {

	public EntitySentry host;
	public EntityLivingBase target;
	private boolean lockTarget;
	public EntityAISentryAttack(EntitySentry sentry){
		this.host=sentry;
		this.setMutexBits(1);
	}
	@Override
	public void resetTask()
    {
		this.target=null;
		this.host.setSoundState(0);
    }
	@Override
	public boolean shouldExecute() {
		// TODO Auto-generated method stub
		return !this.host.isDisabled()&&(target=this.host.getAttackTarget())!=null&&this.host.getEntitySenses().canSee(this.host.getAttackTarget());
	}
	@Override
	public void updateTask()
    {
		//System.out.println("Executing: "+this.target+" "+this.host.attackDelay);
    	if((this.target != null && this.target.deathTime>0) || this.host.deathTime>0){
    		this.resetTask();
    		return;
    	}
    	if(this.target == null){
    		return;
    	}
    	EntityLivingBase owner=this.host.getOwner();
    	if(owner==null||owner.isDead){
    		owner=this.host;
    	}
    	double lookX=this.target.posX;
        double lookY=this.target.posY+this.target.height/2;
        double lookZ=this.target.posZ;
        if(this.lockTarget){
        	this.host.getLookHelper().setLookPosition(lookX,lookY,lookZ,30, 75);
        }
        else{
        	this.host.getLookHelper().setLookPosition(lookX,lookY,lookZ,5f+this.host.getLevel()*2.25f,50);

        }
        if(TF2weapons.lookingAt(this.host,12,lookX,lookY,lookZ)){
        	this.lockTarget=true;
        	this.host.setSoundState(this.host.getAmmo()>0?2:3);
        	
        	while(this.host.attackDelay<=0&&this.host.getAmmo()>0){
        		this.host.attackDelay+=this.host.getLevel()>1?2.5f:5f;
        		this.host.playSound(this.host.getLevel()==1?TF2weapons.MOD_ID+":mob.sentry.shoot.1":TF2weapons.MOD_ID+":mob.sentry.shoot.2", 0.6f, 1f);
        		List<MovingObjectPosition> list=TF2weapons.pierce(this.host.worldObj, this.host, this.host.posX, this.host.posY+this.host.getEyeHeight(), this.host.posZ, this.target.posX, this.target.posY+this.target.getEyeHeight(), this.target.posZ,false, 0.08f,false);
        		for(MovingObjectPosition bullet:list){
        			if(bullet.entityHit!=null){

	        			DamageSource src=TF2weapons.causeDirectDamage(host.sentryBullet, owner, 0).setProjectile();
	        			
	        			if(TF2weapons.dealDamage(bullet.entityHit, this.host.worldObj, owner, this.host.sentryBullet, TF2weapons.calculateCrits(bullet.entityHit, null, 0), 1.6f, src)){
		        			Vec3 dist=Vec3.createVectorHelper(bullet.entityHit.posX-this.host.posX,bullet.entityHit.posY-this.host.posY,bullet.entityHit.posZ-this.host.posZ).normalize();
		        			bullet.entityHit.addVelocity(dist.xCoord*0.35,dist.yCoord*0.35, dist.zCoord*0.35);
		        			if(bullet.entityHit instanceof EntityLivingBase){
		        				((EntityLivingBase)bullet.entityHit).setLastAttacker(host);
		        				((EntityLivingBase)bullet.entityHit).setRevengeTarget(host);
		        				if(!bullet.entityHit.isEntityAlive())
		        					this.host.setKills(this.host.getKills()+1);
		        			}
		        			
        			}
        			}
        			
        		}
        		this.host.setAmmo(this.host.getAmmo()-1);
        	}
        	
        	while(this.host.getLevel()==3&&this.host.getRocketAmmo()>0&&this.host.attackDelayRocket<=0){
        		this.host.attackDelayRocket+=60;
        		try {
        			//System.out.println(owner);
        			this.host.playSound(TF2weapons.MOD_ID+":mob.sentry.rocket", 0.6f, 1f);
					EntityProjectileBase proj=MapList.projectileClasses.get(ItemFromData.getData(this.host.sentryRocket).getString(PropertyType.PROJECTILE)).getConstructor(World.class,EntityLivingBase.class).newInstance(this.host.worldObj,this.host);
					proj.shootingEntity=owner;
					proj.usedWeapon=host.sentryRocket;
					proj.sentry=this.host;
					this.host.worldObj.spawnEntityInWorld(proj);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
        		this.host.setRocketAmmo(this.host.getRocketAmmo()-1);
        		/*MovingObjectPosition bullet=TF2weapons.pierce(this.host.worldObj, this.host, this.host.posX, this.host.posY+this.host.height/2, this.host.posZ, this.target.posX, this.target.posY+this.target.height/2, this.target.posZ,false, 0.08f);
        		if(bullet.entityHit!=null){
        			DamageSource src=TF2weapons.causeBulletDamage("Sentry Gun", this.host.getOwner(), 0);
        			TF2weapons.dealDamage(bullet.entityHit, this.host.worldObj, this.host.owner, null, 0, 1.6f, src);
        			Vec3 dist=Vec3.createVectorHelper(this.host.posX-bullet.entityHit.posX,this.host.posY-bullet.entityHit.posY,this.host.posZ-bullet.entityHit.posZ).normalize();
        			bullet.entityHit.addVelocity(dist.xCoord,dist.yCoord, dist.zCoord);
        		}*/
        		
        	}
        }
        else{
        	this.lockTarget=false;
        	if(this.host.getSoundState()>2){
        		this.host.setSoundState(1);
        	}
        }
    }
}
