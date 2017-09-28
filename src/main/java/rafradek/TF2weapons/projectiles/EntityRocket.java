package rafradek.TF2weapons.projectiles;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;
import rafradek.TF2weapons.ClientProxy;

public class EntityRocket extends EntityProjectileBase {

	public EntityRocket(World p_i1756_1_) {
		super(p_i1756_1_);
		if(p_i1756_1_.isRemote){
			ClientProxy.spawnRocketParticle(this.worldObj, this);
		}
	}
	
	public EntityRocket(World p_i1756_1_, EntityLivingBase p_i1756_2_) {
		super(p_i1756_1_, p_i1756_2_);
		
	}

	@Override
	public void onHitGround(int x, int y, int z, MovingObjectPosition mop) {
		this.explode(mop.hitVec.xCoord+EnumFacing.getFront(mop.sideHit).getFrontOffsetX()*0.05, mop.hitVec.yCoord+EnumFacing.getFront(mop.sideHit).getFrontOffsetY()*0.05, mop.hitVec.zCoord+EnumFacing.getFront(mop.sideHit).getFrontOffsetZ()*0.05,null,1f);
	}

	@Override
	public void onHitMob(Entity entityHit, MovingObjectPosition mop) {
		this.explode(mop.hitVec.xCoord, mop.hitVec.yCoord, mop.hitVec.zCoord,mop.entityHit,1f);
	}
	public double maxMotion(){
		return Math.max(this.motionX, Math.max(this.motionY, this.motionZ));
	}
	@Override
	public void onUpdate()
    {
		super.onUpdate();
		
    }
	@Override
	public void spawnParticles(double x, double y, double z){
		if (this.isInWater())
        {
        	this.worldObj.spawnParticle("bubble", x, y, z, this.motionX, this.motionY, this.motionZ);
        }
        else{
        	this.worldObj.spawnParticle("smoke", x, y, z, 0, 0, 0);
        }
	}
	@Override
	protected float getSpeed()
    {
        return 1.04f;
    }
    
    @Override
	protected double getGravity()
    {
        return 0;
    }

}
