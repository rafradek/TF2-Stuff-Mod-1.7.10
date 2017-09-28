package rafradek.TF2weapons.projectiles;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;
import rafradek.TF2weapons.TF2Attribute;
import rafradek.TF2weapons.TF2weapons;

public class EntityFlame extends EntityProjectileBase {
	
	public EntityFlame(World world){
		super(world);
	}
	
	public EntityFlame(World p_i1756_1_, EntityLivingBase p_i1756_2_) {
		super(p_i1756_1_, p_i1756_2_);
		// TODO Auto-generated constructor stub
	}
	
	@Override
	@SideOnly(Side.CLIENT)
    public boolean isInRangeToRender3d(double x, double y, double z)
    {
		return false;
    }
	
	@Override
	public void onHitGround(int x, int y, int z, MovingObjectPosition mop) {
		int xOff=EnumFacing.getFront(mop.sideHit).getFrontOffsetX();
		int yOff=EnumFacing.getFront(mop.sideHit).getFrontOffsetY();
		int zOff=EnumFacing.getFront(mop.sideHit).getFrontOffsetZ();
		if(!this.worldObj.isRemote&&TF2weapons.destTerrain&&this.worldObj.getBlock(x, y, z).getMaterial().getCanBurn()&&this.worldObj.getBlock(x+xOff, y+yOff, z+zOff).getMaterial()!=Material.fire
				&&this.worldObj.getBlock(x+xOff, y+yOff, z+zOff).isReplaceable(worldObj, x+xOff, y+yOff, z+zOff)){
			this.worldObj.setBlock(x+xOff, y+yOff, z+zOff, Blocks.fire);
		}
		this.setDead();
	}

	@Override
	public void onHitMob(Entity entityHit, MovingObjectPosition mop) {
		if(!this.worldObj.isRemote && !this.hitEntities.contains(entityHit)){
			this.hitEntities.add(entityHit);
			int critical=TF2weapons.calculateCrits(entityHit, shootingEntity, this.getCritical());
			//float distance= (float) Vec3.createVectorHelper(this.shootingEntity.posX, this.shootingEntity.posY, this.shootingEntity.posZ).distanceTo(Vec3.createVectorHelper(mop.hitVec.xCoord, mop.hitVec.yCoord, mop.hitVec.zCoord))+5.028f;
			float dmg=TF2weapons.calculateDamage(entityHit,worldObj, this.shootingEntity, usedWeapon, critical, 1+this.ticksExisted/this.getMaxTime());
			//System.out.println("damage: "+dmg);
			//dmg*=ItemUsable.getData(this.usedWeapon).get("Min damage").getDouble()+1-(this.ticksExisted/this.getMaxTime())*ItemUsable.getData(this.usedWeapon).get("Min damage").getDouble();
			
			
			
			if(TF2weapons.dealDamage(entityHit, this.worldObj, this.shootingEntity, this.usedWeapon, critical, dmg, TF2weapons.causeBulletDamage(this.usedWeapon,this.shootingEntity,critical, this).setFireDamage())
					&&entityHit.ticksExisted-entityHit.getEntityData().getInteger("LastHitBurn")>18||entityHit.getEntityData().getInteger("LastHitBurn")>entityHit.ticksExisted){
				entityHit.getEntityData().setInteger("LastHitBurn", entityHit.ticksExisted);
				entityHit.setFire((int) (6*TF2Attribute.getModifier("Burn Time", this.usedWeapon, 1, shootingEntity)));
			}
			
		}
	}
	@Override
	public void onUpdate(){
		if(this.isInsideOfMaterial(Material.water)){
			this.setDead();
			return;
		}
		super.onUpdate();
	}
	@Override
	public void spawnParticles(double x, double y, double z) {
		// TODO Auto-generated method stub

	}
	
	/*public double getMaxDistance(){
		return 6.2865;
	}*/
	
	@Override
	public int getMaxTime(){
		return Math.round(5*(1+TF2Attribute.getModifier("Flame Range", this.usedWeapon, 0.5f, this.shootingEntity)));
	}
	
	@Override
	protected float getSpeed()
    {
        return 1.2570f*(1+TF2Attribute.getModifier("Flame Range", this.usedWeapon, 0.4f, this.shootingEntity));
    }
    
    @Override
	protected double getGravity()
    {
        return 0;
    }
    @Override
	public float getCollisionSize(){
    	return 0.2f+this.ticksExisted*0.18f;
    }
}
