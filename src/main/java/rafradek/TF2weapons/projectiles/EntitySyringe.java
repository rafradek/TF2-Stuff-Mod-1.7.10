package rafradek.TF2weapons.projectiles;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import rafradek.TF2weapons.TF2weapons;

public class EntitySyringe extends EntityProjectileBase {

	public boolean sticked;
	public int stickBlockX;
	public int stickBlockY;
	public int stickBlockZ;
	public EntitySyringe(World world){
		super(world);
		this.setSize(0.3F, 0.3F);
	}
	
	public EntitySyringe(World world, EntityLivingBase living) {
		super(world, living);
		this.setSize(0.3F, 0.3F);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void onHitGround(int x, int y, int z, MovingObjectPosition mop) {
		this.setPosition(mop.hitVec.xCoord+EnumFacing.getFront(mop.sideHit).getFrontOffsetX()*0.1, mop.hitVec.yCoord-EnumFacing.getFront(mop.sideHit).getFrontOffsetY()*0.15f, mop.hitVec.zCoord+EnumFacing.getFront(mop.sideHit).getFrontOffsetZ()*0.1);
		this.sticked=true;
		this.stickBlockX=x;
		this.stickBlockY=y;
		this.stickBlockZ=z;
	}

	@Override
	public void onHitMob(Entity entityHit, MovingObjectPosition mop) {
		if(!this.worldObj.isRemote){
			this.setDead();
			float distance= (float) Vec3.createVectorHelper(this.shootingEntity.posX, this.shootingEntity.posY, this.shootingEntity.posZ).distanceTo(Vec3.createVectorHelper(mop.hitVec.xCoord, mop.hitVec.yCoord, mop.hitVec.zCoord));
			int critical=TF2weapons.calculateCrits(entityHit, shootingEntity, this.getCritical());
			float dmg=TF2weapons.calculateDamage(entityHit,worldObj, this.shootingEntity, usedWeapon, critical, distance);
			TF2weapons.dealDamage(entityHit, this.worldObj, this.shootingEntity, this.usedWeapon, critical, dmg, TF2weapons.causeBulletDamage(this.usedWeapon,this.shootingEntity,critical, this));
		}
	}

	@Override
	public void onUpdate(){
		if(this.ticksExisted>this.getMaxTime()||(this.sticked&&this.worldObj.isAirBlock(this.stickBlockX,this.stickBlockY,this.stickBlockZ))){
			this.setDead();
			return;
		}
		else if(!this.sticked){
			super.onUpdate();
		}
	}
	@Override
	public void spawnParticles(double x, double y, double z) {
		// TODO Auto-generated method stub

	}
	@Override
	public boolean moveable(){
		return !this.sticked;
	}
}
