package rafradek.TF2weapons.characters.ai;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.RandomPositionGenerator;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.EnumDifficulty;
import rafradek.TF2weapons.TF2weapons;
import rafradek.TF2weapons.characters.EntityHeavy;
import rafradek.TF2weapons.characters.EntityTF2Character;
import rafradek.TF2weapons.message.TF2Message;
import rafradek.TF2weapons.weapons.ItemWeapon;
import rafradek.TF2weapons.weapons.WeaponsCapability;

public class EntityAIUseRangedWeapon extends EntityAIBase {

	/** The entity the AI instance has been applied to */
    protected final EntityTF2Character entityHost;

    /**
     * The entity (as a RangedAttackMob) the AI instance has been applied to.
     */
    protected EntityLivingBase attackTarget;
    public boolean reloading;

    /**
     * A decrementing tick that spawns a ranged attack once this value reaches 0. It is then set back to the
     * maxRangedAttackTime.
     */
    protected int rangedAttackTime;
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
	public float projSpeed;
	public int fireAtFeet;
	
	private boolean firstTick;

	public float gravity;

	public boolean explosive;

	public boolean dodgeHeadFor;


    public EntityAIUseRangedWeapon(EntityTF2Character par1IRangedAttackMob, float par2, float par5)
    {
        this.rangedAttackTime = -1;
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
    @Override
	public boolean shouldExecute()
    {
        EntityLivingBase EntityLivingBase = this.entityHost.getAttackTarget();

        if (EntityLivingBase == null)
        {
            return false;
        }
        else
        {
            this.attackTarget = EntityLivingBase;
            return this.entityHost.getHeldItem()!=null;
        }
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
	    	((ItemWeapon)this.entityHost.getHeldItem().getItem()).endUse(this.entityHost.getHeldItem(), this.entityHost, this.entityHost.worldObj, WeaponsCapability.get(this.entityHost).state, 0);
	    	WeaponsCapability.get(this.entityHost).state=0;
	    	TF2weapons.network.sendToDimension(new TF2Message.ActionMessage(0, entityHost), entityHost.dimension);
    	}
    	if(this.jump){
			this.entityHost.jump=false;
    	}
    	this.entityHost.getNavigator().clearPathEntity();
        this.attackTarget = null;
        this.field_75318_f = 0;
        this.rangedAttackTime = -1;
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
    	if((this.attackTarget != null && this.attackTarget.deathTime>0) || this.entityHost.deathTime>0){
    		this.resetTask();
    		return;
    	}
    	if(this.attackTarget == null){
    		return;
    	}
    	
    	ItemStack item=this.entityHost.getHeldItem();
    	ItemWeapon weapon=((ItemWeapon)item.getItem());
    	
        double d0 = this.entityHost.getDistanceSq(this.attackTarget.posX, this.attackTarget.boundingBox.minY, this.attackTarget.posZ);
        
        double lookX=this.attackTarget.posX;
        double lookY=this.attackTarget.posY+this.attackTarget.getEyeHeight();
        double lookZ=this.attackTarget.posZ;
        boolean shouldFireProj=true;
        if(this.projSpeed>0){
        	
        	float dist=this.entityHost.getDistanceToEntity(this.attackTarget);
        	float ticksToReach=dist/this.projSpeed;
        	
        	lookX+=(this.attackTarget.posX-this.entityHost.targetPrevPos[1])*ticksToReach*0.5;
            lookZ+=(this.attackTarget.posZ-this.entityHost.targetPrevPos[5])*ticksToReach*0.5;
            lookY=this.attackTarget.posY+this.attackTarget.height/2;
            if(this.entityHost.worldObj.func_147447_a(Vec3.createVectorHelper(this.entityHost.posX,this.entityHost.posY+this.entityHost.getEyeHeight(),this.entityHost.posZ), Vec3.createVectorHelper(lookX,lookY,lookZ),false,true,false)!=null){
            	lookY=this.attackTarget.posY+this.attackTarget.getEyeHeight();
            }
            double yFall=this.attackTarget.motionY<0?this.attackTarget.motionY:0;
            for(int i=1; !this.attackTarget.isInWater()&&i<=(ticksToReach);i++){
            	lookY+=gravity*i;
            	if(!this.attackTarget.onGround&&this.attackTarget.motionY<0){
            		yFall+=0.08*i;
            	}
            }
            
            MovingObjectPosition mop=this.entityHost.worldObj.rayTraceBlocks(Vec3.createVectorHelper(this.attackTarget.posX,this.attackTarget.posY,this.attackTarget.posZ), Vec3.createVectorHelper(this.attackTarget.posX,this.attackTarget.posY-0.3f-yFall,this.attackTarget.posZ));
            if(mop != null && mop.typeOfHit==MovingObjectPosition.MovingObjectType.BLOCK){
        		yFall=this.attackTarget.posY-mop.hitVec.yCoord;
        	}
            shouldFireProj=mop!=null||this.attackTarget.motionY<=0.1f;
            //System.out.println("perform"+yFall);
            //System.out.println("perform"+yFall);
            //System.out.println("look: "+this.attackTarget.posX+" "+this.entityHost.targetPrevPos[1]);
        
        //System.out.println("raytracing:"+this.entityHost.worldObj.rayTraceBlocks(Vec3.createVectorHelper(this.entityHost.posX,this.entityHost.posY+this.entityHost.getEyeHeight(),this.entityHost.posZ), Vec3.createVectorHelper(lookX,this.attackTarget.posY,lookZ)));
	        if(this.fireAtFeet>0 && this.entityHost.worldObj.func_147447_a(Vec3.createVectorHelper(this.entityHost.posX,this.entityHost.posY+this.entityHost.getEyeHeight(),this.entityHost.posZ), Vec3.createVectorHelper(lookX,this.attackTarget.posY,lookZ),false,true,false)==null){
	        	/*if(this.fireAtFeet==2&&this.attackTarget.motionY<=0){
		        	MovingObjectPosition mop=this.entityHost.worldObj.rayTraceBlocks(this.attackTarget.getPositionVector(), this.attackTarget.getPositionVector().addVector(0, yFall*1.2, 0));
		        	if(mop != null && mop.typeOfHit==MovingObjectPosition.Type.BLOCK){
		        		lookY=mop.hitVec.yCoord;
		        	}
		        	else{
		        		lookY=this.attackTarget.posY-yFall;
		        	}
	        	}
	        	else{*/
	        		lookY=this.attackTarget.posY-yFall;
	        	//}
	        }
	        
        }
        
        boolean stay=this.entityHost.getEntitySenses().canSee(this.attackTarget)||(this.projSpeed>0&&this.attackTarget.motionY>0);
        boolean fire = stay&&shouldFireProj&&TF2weapons.lookingAt(this.entityHost,(this.explosive&&d0<16?30:0)+weapon.getWeaponSpreadBase(item, this.entityHost)*100+1,lookX,lookY,lookZ);
        if(!this.reloading&&(this.entityHost.worldObj.difficultySetting!=EnumDifficulty.HARD||!fire)&&item.getItemDamage()==item.getMaxDamage()&&weapon.hasClip(item)){
        	this.reloading=true;
        }
        else if(this.reloading&&item.getItemDamage()==0){
        	this.reloading=false;
        }
        this.entityHost.setJumping(true);
        if (stay){
            ++this.field_75318_f;
            if(d0 <= (double)this.attackRangeSquared/4){
            	this.field_75318_f=20;
            }
        }
        else
            this.field_75318_f = 0;

        if (d0 <= this.attackRangeSquared && this.field_75318_f >= 20){
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
        this.entityHost.getLookHelper().setLookPosition(lookX,lookY,lookZ, this.entityHost.rotation, 90.0F);
        //this.entityHost.getLookHelper().onUpdateLook();
        //if(!(this.entityHost instanceof EntitySoldier&&((EntitySoldier)this.entityHost).rocketJump))
        
        //this.entityHost.getLookHelper().setLookPositionWithEntity(this.attackTarget, 1.0F, 90.0F);
        if((!reloading/*||(this.entityHost.worldObj.difficultySetting==EnumDifficulty.HARD&&d0 <= (double)this.attackRangeSquared/4)*/)&&fire && d0 <= this.attackRangeSquared && (this.entityHost.getAmmo() > 0 || weapon.getAmmoType(item)==0)){
        	this.reloading=false;
        	if(!pressed){
        		pressed=true;
        		weapon.startUse(item, this.entityHost, this.entityHost.worldObj, WeaponsCapability.get(this.entityHost).state, 1);
        		WeaponsCapability.get(this.entityHost).state=1;
        		TF2weapons.network.sendToDimension(new TF2Message.ActionMessage(1, entityHost), entityHost.dimension);
        		//System.out.println("coœdo");
        	}
        	
    	}
        else{
        	if(pressed){
        		if(this.jump){
        			this.entityHost.jump=false;
            	}
        		int valuedef=this.entityHost instanceof EntityHeavy?2:0;
        		weapon.endUse(item, this.entityHost, this.entityHost.worldObj, WeaponsCapability.get(this.entityHost).state, valuedef);
        		WeaponsCapability.get(this.entityHost).state=valuedef;
		    	TF2weapons.network.sendToDimension(new TF2Message.ActionMessage(valuedef, entityHost), entityHost.dimension);
		    	//System.out.println("coœz");
        	}
        	pressed=false;
        }
        //if(){
        	if(this.jump&&d0<this.jumprange){
        		this.entityHost.jump=true;
        	}
        	else if(this.jump){
        		this.entityHost.jump=false;
        	}
        	if(this.dodge&&this.entityHost.getNavigator().noPath()){
	        	Vec3 Vec3 = RandomPositionGenerator.findRandomTarget(this.entityHost, 4, 2);
	
	            if (Vec3 != null)
	            {
	            	/*double offsetX=this.dodgeHeadFor?this.attackTarget.posX-this.entityHost.posX:0;
	            	double offsetY=this.dodgeHeadFor?this.attackTarget.posY-this.entityHost.posY:0;
	            	double offsetZ=this.dodgeHeadFor?this.attackTarget.posZ-this.entityHost.posZ:0;*/
	                this.entityHost.getNavigator().tryMoveToXYZ(this.attackTarget.posX+(Vec3.xCoord-this.entityHost.posX), this.attackTarget.posY+(Vec3.yCoord-this.entityHost.posY), this.attackTarget.posZ+(Vec3.zCoord-this.entityHost.posZ), this.entityMoveSpeed*this.dodgeSpeed);
	            }
        	}
       // }
    }

	public void setDodge(boolean i) {
		this.dodge=i;
	}

}
