package rafradek.TF2weapons.projectiles;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;
import rafradek.TF2weapons.weapons.ItemStickyLauncher;

public class EntityStickybomb extends EntityProjectileBase {

	public EntityStickybomb(World p_i1756_1_) {
		super(p_i1756_1_);
		this.setSize(0.3f, 0.3f);
	}
	
	public EntityStickybomb(World p_i1756_1_, EntityLivingBase p_i1756_2_) {
		super(p_i1756_1_, p_i1756_2_);
		this.setSize(0.3f, 0.3f);
	}
	
	@Override
	public float getPitchAddition(){
    	return 3;
    }
	
	@Override
	public void onHitGround(int x, int y, int z, MovingObjectPosition mop) {
		
	}
	@Override
	public void onHitMob(Entity entityHit, MovingObjectPosition mop) {
		
	}
	public double maxMotion(){
		return Math.max(this.motionX, Math.max(this.motionY, this.motionZ));
	}
	@Override
	public void spawnParticles(double x, double y, double z){

	}
	@Override
	public void onUpdate()
    {
		super.onUpdate();
		if(!this.shootingEntity.isEntityAlive()){
			this.setDead();
		}
    }
	@Override
	public void setDead(){
		super.setDead();
		if(!this.worldObj.isRemote){
			ItemStickyLauncher.activeBombs.get(this.shootingEntity).remove(this);
		}
	}
	@Override
	protected float getSpeed()
    {
        return 0.7667625f;
    }
    
    @Override
	protected double getGravity()
    {
        return 0.0381f;
    }
    @Override
	public boolean isSticky(){
    	return true;
    }
    @Override
	public boolean useCollisionBox(){
		return true;
	}
    @Override
	public int getMaxTime(){
		return 72000;
	}
    @Override
	public void onHitBlockX(){
    	this.motionX=0;
    	this.motionY=0;
    	this.motionZ=0;
	}
	public void onHitBlockY(Block block){
		this.motionX=0;
    	this.motionY=0;
    	this.motionZ=0;
	}
	@Override
	public void onHitBlockZ(){
		this.motionX=0;
    	this.motionY=0;
    	this.motionZ=0;
	}
	
}
