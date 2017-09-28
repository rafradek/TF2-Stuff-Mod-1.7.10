package rafradek.TF2weapons.characters.ai;

import java.util.List;

import net.minecraft.command.IEntitySelector;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.util.MathHelper;
import rafradek.TF2weapons.TF2weapons;
import rafradek.TF2weapons.building.EntityBuilding;
import rafradek.TF2weapons.characters.EntityEngineer;
import rafradek.TF2weapons.message.TF2Message;
import rafradek.TF2weapons.weapons.ItemUsable;
import rafradek.TF2weapons.weapons.ItemWeapon;
import rafradek.TF2weapons.weapons.WeaponsCapability;

public class EntityAIRepair extends EntityAIBase {

	/** The entity the AI instance has been applied to */
    protected final EntityEngineer entityHost;

    /**
     * The entity (as a RangedAttackMob) the AI instance has been applied to.
     */
    protected EntityBuilding attackTarget;
    
    protected float entityMoveSpeed;
    protected int field_75318_f;

    /**
     * The maximum time the AI has to wait before peforming another ranged attack.
     */
    private float attackRange;
    protected float attackRangeSquared;
    
    protected boolean pressed;
    private boolean altpreesed;
    protected boolean dodging;
	protected boolean dodge;
	public boolean jump;
	public float dodgeSpeed=1f;
	public int jumprange;
	public int searchTimer;
	private boolean firstTick;

	public float gravity;

	public boolean explosive;


    public EntityAIRepair(EntityEngineer par1IRangedAttackMob, float par2, float par5)
    {
        this.field_75318_f = 0;
            this.entityHost = par1IRangedAttackMob;
            this.entityMoveSpeed = par2;
            this.attackRange = par5;
            this.attackRangeSquared = par5 * par5;
            
            this.setMutexBits(3);
    }
    public void setRange(float range){
    	this.attackRange = range;
        this.attackRangeSquared = range * range;
    }
    /**
     * Returns whether the EntityAIBase should begin execution.
     */
    public boolean isValidTarget(EntityBuilding building){
    	return building != null && building.isEntityAlive() && (building.getMaxHealth()>building.getHealth()||((this.entityHost.getAttackTarget()==null||this.entityHost.getAttackTarget().isDead)&&building.canUseWrench()));
    }
    @Override
	@SuppressWarnings("unchecked")
	public boolean shouldExecute()
    {
    	this.searchTimer--;
    	if(this.entityHost.metal<=0){
        	return false;
        	
        }
        EntityBuilding building = this.entityHost.sentry;
        if(this.isValidTarget(building)||this.isValidTarget(building=this.entityHost.dispenser)){
        	this.attackTarget = building;
        	this.entityHost.switchSlot(2);
            return true;
        }
        else if(this.searchTimer<=0){
        	this.searchTimer=4;
        	for(EntityBuilding build:(List<EntityBuilding>)this.entityHost.worldObj.selectEntitiesWithinAABB(EntityBuilding.class, this.entityHost.boundingBox.expand(10, 3, 10), new IEntitySelector(){

				@Override
				public boolean isEntityApplicable(Entity p_82704_1_) {
					return TF2weapons.isOnSameTeam(p_82704_1_,entityHost)&&isValidTarget((EntityBuilding) p_82704_1_);
				}

				
        		
        	})){
        		this.attackTarget = build;
                this.entityHost.switchSlot(2);
                return true;
        	}
        }
        
        return this.attackTarget!=null;
    }

    /**
     * Returns whether an in-progress EntityAIBase should continue executing
     */
    @Override
	public boolean continueExecuting()
    {
        return this.shouldExecute() || !this.entityHost.getNavigator().noPath();
    }

    /**
     * Resets the task
     */
    @Override
	public void resetTask()
    {
    	if(WeaponsCapability.get(this.entityHost).state!=0){
	    	pressed=false;
	    	if(this.entityHost.getHeldItem().getItem() instanceof ItemWeapon)
	    		((ItemWeapon)this.entityHost.getHeldItem().getItem()).endUse(this.entityHost.getHeldItem(), this.entityHost, this.entityHost.worldObj, WeaponsCapability.get(this.entityHost).state, 0);
	    	WeaponsCapability.get(entityHost).state=0;
	    	TF2weapons.network.sendToDimension(new TF2Message.ActionMessage(0, entityHost), entityHost.dimension);
    	}
    	this.entityHost.getNavigator().clearPathEntity();
        this.attackTarget = null;
        this.field_75318_f = 0;
        this.entityHost.switchSlot(0);
    }

    /**
     * Updates the task
     */
    public double lookingAtMax(){
    	if(this.attackTarget==null) return 0;
    	double d0 = this.attackTarget.posX - this.entityHost.posX;
        double d1 = (this.attackTarget.posY + this.attackTarget.getEyeHeight()) - (this.entityHost.posY + this.entityHost.getEyeHeight());
        double d2 = this.attackTarget.posZ - this.entityHost.posZ;
        double d3 = MathHelper.sqrt_double(d0 * d0 + d2 * d2);
        float f = (float)(Math.atan2(d2, d0) * 180.0D / Math.PI) - 90.0F;
        float f1 = (float)(-(Math.atan2(d1, d3) * 180.0D / Math.PI));
        float compareyaw=Math.abs( 180 - Math.abs(Math.abs(f - MathHelper.wrapAngleTo180_float(this.entityHost.rotationYawHead)) - 180)); 
        float comparepitch=Math.abs( 180 - Math.abs(Math.abs(f1 - this.entityHost.rotationPitch) - 180)); 
        //System.out.println("Angled: "+compareyaw+" "+comparepitch);
        return Math.max(compareyaw, comparepitch);
    }
    @Override
	public void updateTask()
    {
    	if(!this.isValidTarget(attackTarget)){
    		this.attackTarget=null;
    	}
    	if(this.attackTarget == null){
    		this.resetTask();
    		return;
    	}
        double d0 = this.entityHost.getDistanceSq(this.attackTarget.posX, this.attackTarget.boundingBox.minY, this.attackTarget.posZ);
        
        double lookX=this.attackTarget.posX;
        double lookY=this.attackTarget.posY+this.attackTarget.getEyeHeight();
        double lookZ=this.attackTarget.posZ;
        //System.out.println("raytracing:"+this.entityHost.worldObj.rayTraceBlocks(Vec3.createVectorHelper(this.entityHost.posX,this.entityHost.posY+this.entityHost.getEyeHeight(),this.entityHost.posZ), Vec3.createVectorHelper(lookX,this.attackTarget.posY,lookZ)));
        boolean comeCloser=this.entityHost.getEntitySenses().canSee(this.attackTarget);
        //boolean flag = comeCloser&&TF2weapons.lookingAt(this.entityHost,15,lookX,lookY,lookZ);
        this.entityHost.setJumping(true);
        if (comeCloser){
            ++this.field_75318_f;
        }
        else
            this.field_75318_f = 0;

        if (d0 <= this.attackRangeSquared && this.field_75318_f >= 6){
        	if(!this.dodging){
        		this.entityHost.getNavigator().clearPathEntity();
        		this.dodging=true;
        	}
        }
        else {
        	this.dodging=false;
        	/*if(this.entityHost.onGround&&this.entityHost instanceof EntitySoldier&&this.entityHost.getHeldItem().getItemDamage()<this.entityHost.getHeldItem().getMaxDamage()-1){
        		((EntitySoldier)this.entityHost).rocketJump=true;
        	}*/
            this.entityHost.getNavigator().tryMoveToEntityLiving(this.attackTarget, this.entityMoveSpeed);
        }
        //if(!(this.entityHost instanceof EntitySoldier&&((EntitySoldier)this.entityHost).rocketJump))
        this.entityHost.getLookHelper().setLookPosition(lookX,lookY,lookZ, this.entityHost.rotation, 90.0F);
        //this.entityHost.getLookHelper().setLookPositionWithEntity(this.attackTarget, 1.0F, 90.0F);
        if(d0 <= this.attackRangeSquared/* && this.entityHost.getAmmo() > 0*/){
        	if(!pressed){
        		pressed=true;
        		((ItemUsable)this.entityHost.getHeldItem().getItem()).startUse(this.entityHost.getHeldItem(), this.entityHost, this.entityHost.worldObj, WeaponsCapability.get(this.entityHost).state, 1);
        		WeaponsCapability.get(this.entityHost).state=1;
        		TF2weapons.network.sendToDimension(new TF2Message.ActionMessage(1, entityHost), entityHost.dimension);
        		//System.out.println("co�do");
        	}
        	
    	}
        else{
        	if(pressed){
        		((ItemUsable)this.entityHost.getHeldItem().getItem()).endUse(this.entityHost.getHeldItem(), this.entityHost, this.entityHost.worldObj, WeaponsCapability.get(this.entityHost).state, 0);
        		WeaponsCapability.get(this.entityHost).state=0;
		    	TF2weapons.network.sendToDimension(new TF2Message.ActionMessage(0, entityHost), entityHost.dimension);
		    	//System.out.println("co�z");
        	}
        	pressed=false;
        }
        //if(){
       // }
    }

}
