package rafradek.TF2weapons.projectiles;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import cpw.mods.fml.common.registry.IThrowableEntity;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IProjectile;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.play.server.S27PacketExplosion;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.ReportedException;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import rafradek.TF2weapons.ClientProxy;
import rafradek.TF2weapons.TF2Attribute;
import rafradek.TF2weapons.TF2weapons;
import rafradek.TF2weapons.building.EntitySentry;
import rafradek.TF2weapons.weapons.ItemProjectileWeapon;
import rafradek.TF2weapons.weapons.TF2Explosion;

public abstract class EntityProjectileBase extends Entity implements IProjectile, IThrowableEntity
{
	public ArrayList<Entity> hitEntities=new ArrayList<Entity>();
    //private Block field_145790_g;
    /** Seems to be some sort of timer for animating an arrow. */
    /** The owner of this arrow. */
    public EntityLivingBase shootingEntity;
    public ItemStack usedWeapon;
    public double gravity=0.05d;
	public float distanceTravelled;
	public EntitySentry sentry;
   
	
    public EntityProjectileBase(World p_i1753_1_)
    {
        super(p_i1753_1_);
        this.setSize(0.5F, 0.5F);
    }
    
    public EntityProjectileBase(World world, EntityLivingBase shooter)
    {
        this(world);
        this.shootingEntity = shooter;
        this.usedWeapon=shooter.getHeldItem().copy();
        this.setLocationAndAngles(shooter.posX, shooter.posY+shooter.getEyeHeight(), shooter.posZ, shooter.rotationYawHead, shooter.rotationPitch+this.getPitchAddition());
        this.posX -= MathHelper.cos(this.rotationYaw / 180.0F * (float)Math.PI) * 0.16F;
        this.posY -= shooter instanceof EntitySentry?-0.1D:0.1D;
        this.posZ -= MathHelper.sin(this.rotationYaw / 180.0F * (float)Math.PI) * 0.16F;
        this.setPosition(this.posX, this.posY, this.posZ);
        this.motionX = -MathHelper.sin(this.rotationYaw/ 180.0F * (float)Math.PI) * MathHelper.cos(this.rotationPitch / 180.0F * (float)Math.PI);
        this.motionZ = MathHelper.cos(this.rotationYaw / 180.0F * (float)Math.PI) * MathHelper.cos(this.rotationPitch / 180.0F * (float)Math.PI);
        this.motionY = (-MathHelper.sin(this.rotationPitch / 180.0F * (float)Math.PI));
        this.setThrowableHeading(this.motionX, this.motionY, this.motionZ, ((ItemProjectileWeapon)this.usedWeapon.getItem()).getProjectileSpeed(usedWeapon, shooter), ((ItemProjectileWeapon)this.usedWeapon.getItem()).getWeaponSpread(usedWeapon, shooter));
    }
    @Override
	protected void entityInit()
    {
    	this.dataWatcher.addObject(16, Byte.valueOf((byte)0));
        this.dataWatcher.addObject(17, Byte.valueOf((byte)0));
        this.dataWatcher.addObject(18, Integer.valueOf(0));
        this.dataWatcher.addObject(19, Integer.valueOf(0));
        this.dataWatcher.addObject(20, Integer.valueOf(0));
    }
    public float getPitchAddition(){
    	return 0;
    }
    public boolean isImmuneToExplosions(){
		return true;
	}
    /**
     * Similar to setArrowHeading, it's point the throwable entity to a x, y, z direction.
     */
    @Override
	public void setThrowableHeading(double p_70186_1_, double p_70186_3_, double p_70186_5_, float p_70186_7_, float p_70186_8_)
    {
        float f2 = MathHelper.sqrt_double(p_70186_1_ * p_70186_1_ + p_70186_3_ * p_70186_3_ + p_70186_5_ * p_70186_5_);
        //System.out.println("motion: "+p_70186_1_+" "+p_70186_3_+" "+p_70186_5_+" "+f2);
        p_70186_1_ /= f2;
        p_70186_3_ /= f2;
        p_70186_5_ /= f2;
        p_70186_1_ += this.rand.nextGaussian() * (this.rand.nextBoolean() ? -1 : 1) * 0.007499999832361937D * p_70186_8_;
        p_70186_3_ += this.rand.nextGaussian() * (this.rand.nextBoolean() ? -1 : 1) * 0.007499999832361937D * p_70186_8_;
        p_70186_5_ += this.rand.nextGaussian() * (this.rand.nextBoolean() ? -1 : 1) * 0.007499999832361937D * p_70186_8_;
        p_70186_1_ *= p_70186_7_;
        p_70186_3_ *= p_70186_7_;
        p_70186_5_ *= p_70186_7_;
        this.motionX = p_70186_1_;
        this.motionY = p_70186_3_;
        this.motionZ = p_70186_5_;
        float f3 = MathHelper.sqrt_double(p_70186_1_ * p_70186_1_ + p_70186_5_ * p_70186_5_);
        this.prevRotationYaw = this.rotationYaw = (float)(Math.atan2(p_70186_1_, p_70186_5_) * 180.0D / Math.PI);
        this.prevRotationPitch = this.rotationPitch = (float)(Math.atan2(p_70186_3_, f3) * 180.0D / Math.PI);
    }

    /**
     * Sets the position and rotation. Only difference from the other one is no bounding on the rotation. Args: posX,
     * posY, posZ, yaw, pitch
     */
    /*@SideOnly(Side.CLIENT)
    public void setPositionAndRotation2(double p_70056_1_, double p_70056_3_, double p_70056_5_, float p_70056_7_, float p_70056_8_, int p_70056_9_)
    {
        this.setPosition(p_70056_1_, p_70056_3_, p_70056_5_);
        this.setRotation(p_70056_7_, p_70056_8_);
    }*/

    /**
     * Sets the velocity to the args. Args: x, y, z
     */
    //@SideOnly(Side.CLIENT)
    @Override
	public void setVelocity(double p_70016_1_, double p_70016_3_, double p_70016_5_)
    {
        this.motionX = p_70016_1_;
        this.motionY = p_70016_3_;
        this.motionZ = p_70016_5_;

        if (this.prevRotationPitch == 0.0F && this.prevRotationYaw == 0.0F)
        {
            float f = MathHelper.sqrt_double(p_70016_1_ * p_70016_1_ + p_70016_5_ * p_70016_5_);
            this.prevRotationYaw = this.rotationYaw = (float)(Math.atan2(p_70016_1_, p_70016_5_) * 180.0D / Math.PI);
            this.prevRotationPitch = this.rotationPitch = (float)(Math.atan2(p_70016_3_, f) * 180.0D / Math.PI);
            this.prevRotationPitch = this.rotationPitch;
            this.prevRotationYaw = this.rotationYaw;
            this.setLocationAndAngles(this.posX, this.posY, this.posZ, this.rotationYaw, this.rotationPitch);
        }
    }
    
    public void explode(double x, double y, double z,Entity direct,float damageMult){
		if(worldObj.isRemote||this.shootingEntity==null) return;
		this.setDead();
		float distance= (float) Vec3.createVectorHelper(this.shootingEntity.posX, this.shootingEntity.posY, this.shootingEntity.posZ).distanceTo(Vec3.createVectorHelper(x, y, z));
		TF2Explosion explosion = new TF2Explosion(this.worldObj, this, x,y,z, this.getExplosionSize()*TF2Attribute.getModifier("Explosion Radius", this.usedWeapon, 1, this.shootingEntity),direct);
		//System.out.println("ticks: "+this.ticksExisted);
        explosion.isFlaming = false;
        explosion.isSmoking = true;
        explosion.doExplosionA();
        explosion.doExplosionB(true);
        Iterator<Entity> affectedIterator=explosion.affectedEntities.keySet().iterator();
        while(affectedIterator.hasNext()){
        	Entity ent=affectedIterator.next();
        	int critical=TF2weapons.calculateCrits(ent, shootingEntity, this.getCritical());
        	float dmg=TF2weapons.calculateDamage(ent,worldObj, this.shootingEntity, usedWeapon, critical, distance)*damageMult;
        	TF2weapons.dealDamage(ent, this.worldObj, this.shootingEntity, this.usedWeapon, critical, explosion.affectedEntities.get(ent)*dmg, TF2weapons.causeBulletDamage(this.usedWeapon,this.shootingEntity,critical, this).setExplosion());
        	if(this.sentry !=null&&ent instanceof EntityLivingBase){
        		((EntityLivingBase)ent).setLastAttacker(this.sentry);
        		((EntityLivingBase)ent).setRevengeTarget(this.sentry);
        		if(!ent.isEntityAlive())
        			this.sentry.setKills(this.sentry.getKills()+1);
        	}
        }
        Iterator<EntityPlayer> iterator = this.worldObj.playerEntities.iterator();

        while (iterator.hasNext())
        {
            EntityPlayer entityplayer = iterator.next();

            if (entityplayer.getDistanceSq(x, y, z) < 4096.0D)
            {
                ((EntityPlayerMP)entityplayer).playerNetServerHandler.sendPacket(new S27PacketExplosion(x, y, z,this.getExplosionSize(), explosion.affectedBlockPositions, explosion.func_77277_b().get(entityplayer)));
            }
        }
	}
    @Override
	public void travelToDimension(int dimensionId)
    {
    	
    }
    public float getExplosionSize(){
    	return 2.74f;
    }
    /**
     * Called to update the entity's position/logic.
     */
    @Override
	public void onUpdate()
    {
    	if (this.ticksExisted>this.getMaxTime()){
        	this.setDead();
        	return;
        }
    	
        super.onUpdate();

        if (this.prevRotationPitch == 0.0F && this.prevRotationYaw == 0.0F)
        {
            float f = MathHelper.sqrt_double(this.motionX * this.motionX + this.motionZ * this.motionZ);
            this.prevRotationYaw = this.rotationYaw = (float)(Math.atan2(this.motionX, this.motionZ) * 180.0D / Math.PI);
            this.prevRotationPitch = this.rotationPitch = (float)(Math.atan2(this.motionY, f) * 180.0D / Math.PI);
        }

        /*BlockPos blockpos = new BlockPos(this.field_145791_d, this.field_145792_e, this.field_145789_f);
        IBlockState iblockstate = this.worldObj.getBlockState(blockpos);
        Block block = iblockstate.getBlock();

        if (block.getMaterial() != Material.air)
        {
            block.setBlockBoundsBasedOnState(this.worldObj, blockpos);
            AxisAlignedBB axisalignedbb = block.getCollisionBoundingBox(this.worldObj, blockpos, iblockstate);

            if (axisalignedbb != null && axisalignedbb.isVecInside(Vec3.createVectorHelper(this.posX, this.posY, this.posZ)))
            {
                this.inGround = true;
            }
        }*/
        
        
        Vec3 Vec31 = Vec3.createVectorHelper(this.posX, this.posY, this.posZ);
        Vec3 Vec3 = net.minecraft.util.Vec3.createVectorHelper(this.posX + this.motionX, this.posY + this.motionY, this.posZ + this.motionZ);
        MovingObjectPosition MovingObjectPosition = this.worldObj.func_147447_a(Vec31, Vec3, false, true, false);
        Vec31 = net.minecraft.util.Vec3.createVectorHelper(this.posX, this.posY, this.posZ);
        Vec3 = net.minecraft.util.Vec3.createVectorHelper(this.posX + this.motionX, this.posY + this.motionY, this.posZ + this.motionZ);

        if (MovingObjectPosition != null)
        {
            Vec3 = net.minecraft.util.Vec3.createVectorHelper(MovingObjectPosition.hitVec.xCoord, MovingObjectPosition.hitVec.yCoord, MovingObjectPosition.hitVec.zCoord);
        }

        Entity entity = null;
        Vec3 result=null;
        List<?> list = this.worldObj.getEntitiesWithinAABBExcludingEntity(this, this.boundingBox.addCoord(this.motionX, this.motionY, this.motionZ).expand(1.0D, 1.0D, 1.0D));
        double d0 = 0.0D;
        int i;
        float f1;

        for (i = 0; i < list.size(); ++i)
        {
            Entity entity1 = (Entity)list.get(i);

            if (entity1.canBeCollidedWith() && entity1.isEntityAlive() && entity1 != this.sentry&&/*TF2weapons.canHit(shootingEntity, entity1)*/ entity1 != this.shootingEntity)
            {
                f1 = this.getCollisionSize();
                AxisAlignedBB axisalignedbb1 = entity1.boundingBox.expand(f1, f1, f1);
                MovingObjectPosition MovingObjectPosition1 = axisalignedbb1.calculateIntercept(Vec31, Vec3);

                if (MovingObjectPosition1 != null)
                {
                    double d1 = Vec31.distanceTo(MovingObjectPosition1.hitVec);

                    if (d1 < d0 || d0 == 0.0D)
                    {
                        entity = entity1;
                        d0 = d1;
                        result=MovingObjectPosition1.hitVec;
                    }
                }
            }
        }

        if (entity != null)
        {
            MovingObjectPosition = new MovingObjectPosition(entity,result);
        }

        if (MovingObjectPosition != null && MovingObjectPosition.entityHit != null && MovingObjectPosition.entityHit instanceof EntityPlayer)
        {
            EntityPlayer entityplayer = (EntityPlayer)MovingObjectPosition.entityHit;

            if (entityplayer.capabilities.disableDamage || this.shootingEntity instanceof EntityPlayer && !((EntityPlayer)this.shootingEntity).canAttackPlayer(entityplayer))
            {
                MovingObjectPosition = null;
            }
        }

        float f2;
        if (MovingObjectPosition != null)
        {
            if (MovingObjectPosition.entityHit != null)
            {
            	this.onHitMob(MovingObjectPosition.entityHit, MovingObjectPosition);
         
            }
            else if(!this.useCollisionBox())
            {
            	int attr=this.worldObj.isRemote?0:(int) TF2Attribute.getModifier("Coll Remove", this.usedWeapon, 0, this.shootingEntity);
            	if(attr==0){
	            	this.onHitGround(MovingObjectPosition.blockX, MovingObjectPosition.blockY, MovingObjectPosition.blockZ, MovingObjectPosition);
            	}
            	else if(attr==2){
            		this.explode(MovingObjectPosition.hitVec.xCoord, MovingObjectPosition.hitVec.yCoord, MovingObjectPosition.hitVec.zCoord, null, 1f);
            	}
            	else{
            		this.setDead();
            	}
            }
        }

        /*if (this.getIsCritical())
        {
            for (i = 0; i < 4; ++i)
            {
                this.worldObj.spawnParticle("crit", this.posX + this.motionX * (double)i / 4.0D, this.posY + this.motionY * (double)i / 4.0D, this.posZ + this.motionZ * (double)i / 4.0D, -this.motionX, -this.motionY + 0.2D, -this.motionZ);
            }
        }*/
        /*if(!this.worldObj.isRemote&&this.isSticked()&&(this.stickedBlock==null||this.worldObj.isAirBlock(this.stickedBlock))){
        	this.setSticked(false);
        	this.stickedBlock=null;
        }*/
        if(this.isSticked()){
        	this.setPosition((double)this.dataWatcher.getWatchableObjectInt(18)/32,
                	(double)this.dataWatcher.getWatchableObjectInt(19)/32,
                	(double)this.dataWatcher.getWatchableObjectInt(20)/32);
        }
        if(this.moveable()){
	        if(!this.useCollisionBox()){
	        	this.posX += this.motionX;
	            this.posY += this.motionY;
	            this.posZ += this.motionZ;
	        }
	        else {
	        	//this.setPosition(this.posX, this.posY, this.posZ);
	        	this.moveEntity(this.motionX, this.motionY, this.motionZ);
	        }
	        float f3 = (float) (1-this.getGravity()/5);
        	this.motionX *= f3;
            this.motionY *= f3;
            this.motionZ *= f3;
            this.motionY -= this.getGravity();
	        f2 = MathHelper.sqrt_double(this.motionX * this.motionX + this.motionZ * this.motionZ);
	        if(f2>0.1||Math.abs(this.motionY)>this.getGravity()*3){
	            this.rotationYaw = (float)(Math.atan2(this.motionX, this.motionZ) * 180.0D / Math.PI);
	
	            for (this.rotationPitch = (float)(Math.atan2(this.motionY, f2) * 180.0D / Math.PI); this.rotationPitch - this.prevRotationPitch < -180.0F; this.prevRotationPitch -= 360.0F)
	            {
	                ;
	            }
	
	            while (this.rotationPitch - this.prevRotationPitch >= 180.0F)
	            {
	                this.prevRotationPitch += 360.0F;
	            }
	
	            while (this.rotationYaw - this.prevRotationYaw < -180.0F)
	            {
	                this.prevRotationYaw -= 360.0F;
	            }
	
	            while (this.rotationYaw - this.prevRotationYaw >= 180.0F)
	            {
	                this.prevRotationYaw += 360.0F;
	            }
	
	            this.rotationPitch = this.prevRotationPitch + (this.rotationPitch - this.prevRotationPitch) * 0.2F;
	            this.rotationYaw = this.prevRotationYaw + (this.rotationYaw - this.prevRotationYaw) * 0.2F;
	        }
        }
        if(this.worldObj.isRemote){
			for (int j = 0; j < 4; ++j)
            {
				double pX=this.posX - this.motionX * j / 4.0D - this.motionX;
				double pY=this.posY + this.height/2 - this.motionY * j / 4.0D- this.motionY;
				double pZ=this.posZ - this.motionZ * j / 4.0D- this.motionZ;
				if(this.getCritical()==2){
					ClientProxy.spawnCritParticle(this.worldObj, pX, pY, pZ,TF2weapons.getTeamForDisplay(this.shootingEntity));
				}
				this.spawnParticles(pX, pY, pZ);
				//EntityFX ent=Minecraft.getMinecraft().effectRenderer.spawnEffectParticle(EnumParticleTypes.EXPLOSION_NORMAL.getParticleID(), pX, pY, pZ, 0, 0, 0);
                
            }
			//ClientProxy.spawnParticle(this.worldObj,new EntityRocketEffect(worldObj, this.posX, this.posY, this.posZ));
		}
        
        if (this.isWet())
        {
            this.extinguish();
        }

        
        
        if(!this.useCollisionBox()){
	        this.setPosition(this.posX, this.posY, this.posZ);
	        this.func_145775_I();
        }
        /*if(!this.worldObj.isRemote&&MinecraftServer.getServer().isSinglePlayer()&&Minecraft.getMinecraft().thePlayer!=null){
        	Entity fentity=Minecraft.getMinecraft().theWorld.getEntityByID(this.getEntityId());
        	if(fentity!=null){
            	fentity.setVelocity(this.motionX,this.motionY,this.motionZ);
            	fentity.setPosition(this.posX, this.posY, this.posZ);
            	fentity.prevPosX=this.prevPosX;
            	fentity.prevPosY=this.prevPosY;
            	fentity.prevPosZ=this.prevPosZ;
        	}
        }*/
    }
    
    //@SideOnly(Side.CLIENT)
    @Override
	public void setPositionAndRotation2(double p_180426_1_, double p_180426_3_, double p_180426_5_, float p_180426_7_, float p_180426_8_, int p_180426_9_)
    {
    	if(this.moveable()){
    		super.setPositionAndRotation2(p_180426_1_, p_180426_3_, p_180426_5_, p_180426_7_, p_180426_8_, p_180426_9_);
    	}
    }
    
    
    
   /* public void setPosition(double x, double y, double z)
    {
	    this.posX = x;
	    this.posY = y;
	    this.posZ = z;
	    float f = this.width / 2f;
	    float f1 = this.height /2f;
	    this.setEntityBoundingBox(AxisAlignedBB.getBoundingBox(x - (double)f, y-(double)f1, z - (double)f, x + (double)f, y + (double)f1, z + (double)f));
    }*/
    
    @Override
	public void moveEntity(double x, double y, double z)
    {
    	if (this.noClip)
        {
            this.boundingBox.offset(x, y, z);
            this.posX = (this.boundingBox.minX + this.boundingBox.maxX) / 2.0D;
            this.posY = this.boundingBox.minY + this.yOffset - this.ySize;
            this.posZ = (this.boundingBox.minZ + this.boundingBox.maxZ) / 2.0D;
        }
        else
        {
            this.worldObj.theProfiler.startSection("move");
            this.ySize *= 0.4F;
            double d3 = this.posX;
            double d4 = this.posY;
            double d5 = this.posZ;

            if (this.isInWeb)
            {
                this.isInWeb = false;
                x *= 0.25D;
                y *= 0.05000000074505806D;
                z *= 0.25D;
                this.motionX = 0.0D;
                this.motionY = 0.0D;
                this.motionZ = 0.0D;
            }

            double d6 = x;
            double d7 = y;
            double d8 = z;
            AxisAlignedBB axisalignedbb = this.boundingBox.copy();
            
            
            List list = this.worldObj.getCollidingBoundingBoxes(this, this.boundingBox.addCoord(x, y, z));

            for (int i = 0; i < list.size(); ++i)
            {
                y = ((AxisAlignedBB)list.get(i)).calculateYOffset(this.boundingBox, y);
            }

            double limit=y/d7;
            if(!this.isSticky()){
            	this.boundingBox.offset(0.0D, y, 0.0D);
            }

            if (!this.field_70135_K && d7 != y)
            {
                z = 0.0D;
                y = 0.0D;
                x = 0.0D;
            }

            boolean flag1 = this.onGround || d7 != y && d7 < 0.0D;
            int j;

            for (j = 0; j < list.size(); ++j)
            {
                x = ((AxisAlignedBB)list.get(j)).calculateXOffset(this.boundingBox, x);
            }

            if(this.isSticky()){
	            if(x/d6<limit){
	            	limit=x/d6;
	            }
            }
            else {
            	this.boundingBox.offset(x, 0.0D, 0.0D);
            }

            if (!this.field_70135_K && d6 != x)
            {
                z = 0.0D;
                y = 0.0D;
                x = 0.0D;
            }

            for (j = 0; j < list.size(); ++j)
            {
                z = ((AxisAlignedBB)list.get(j)).calculateZOffset(this.boundingBox, z);
            }

            if(this.isSticky()){
        		if(z/d8<limit){
	            	limit=z/d8;
	            }
        		this.boundingBox.offset(d6*limit,d7*limit,d8*limit);
            }
            else{
            	this.boundingBox.offset(0.0D, 0.0D, z);
            }
            

            if (!this.field_70135_K && d8 != z)
            {
                z = 0.0D;
                y = 0.0D;
                x = 0.0D;
            }

            double d10;
            double d11;
            int k;
            double d12;

            if (this.stepHeight > 0.0F && flag1 && (d6 != x || d8 != z))
            {
                d12 = x;
                d10 = y;
                d11 = z;
                x = d6;
                y = this.stepHeight;
                z = d8;
                AxisAlignedBB axisalignedbb1 = this.boundingBox.copy();
                this.boundingBox.setBB(axisalignedbb);
                list = this.worldObj.getCollidingBoundingBoxes(this, this.boundingBox.addCoord(d6, y, d8));

                for (k = 0; k < list.size(); ++k)
                {
                    y = ((AxisAlignedBB)list.get(k)).calculateYOffset(this.boundingBox, y);
                }

                this.boundingBox.offset(0.0D, y, 0.0D);

                if (!this.field_70135_K && d7 != y)
                {
                    z = 0.0D;
                    y = 0.0D;
                    x = 0.0D;
                }

                for (k = 0; k < list.size(); ++k)
                {
                    x = ((AxisAlignedBB)list.get(k)).calculateXOffset(this.boundingBox, x);
                }

                this.boundingBox.offset(x, 0.0D, 0.0D);

                if (!this.field_70135_K && d6 != x)
                {
                    z = 0.0D;
                    y = 0.0D;
                    x = 0.0D;
                }

                for (k = 0; k < list.size(); ++k)
                {
                    z = ((AxisAlignedBB)list.get(k)).calculateZOffset(this.boundingBox, z);
                }

                this.boundingBox.offset(0.0D, 0.0D, z);

                if (!this.field_70135_K && d8 != z)
                {
                    z = 0.0D;
                    y = 0.0D;
                    x = 0.0D;
                }

                if (!this.field_70135_K && d7 != y)
                {
                    z = 0.0D;
                    y = 0.0D;
                    x = 0.0D;
                }
                else
                {
                    y = (-this.stepHeight);

                    for (k = 0; k < list.size(); ++k)
                    {
                        y = ((AxisAlignedBB)list.get(k)).calculateYOffset(this.boundingBox, y);
                    }

                    this.boundingBox.offset(0.0D, y, 0.0D);
                }

                if (d12 * d12 + d11 * d11 >= x * x + z * z)
                {
                    x = d12;
                    y = d10;
                    z = d11;
                    this.boundingBox.setBB(axisalignedbb1);
                }
            }

            this.worldObj.theProfiler.endSection();
            this.worldObj.theProfiler.startSection("rest");
            this.posX = (this.boundingBox.minX + this.boundingBox.maxX) / 2.0D;
            this.posY = this.boundingBox.minY + this.yOffset - this.ySize;
            this.posZ = (this.boundingBox.minZ + this.boundingBox.maxZ) / 2.0D;
            this.isCollidedHorizontally = d6 != x || d8 != z;
            this.isCollidedVertically = d7 != y;
            this.onGround = d7 != y && d7 < 0.0D;
            this.isCollided = this.isCollidedHorizontally || this.isCollidedVertically;
            this.updateFallState(y, this.onGround);
            
            if(this.isSticky() && !this.worldObj.isRemote && this.isCollided){
            	this.setSticked(true);
            }
            /*j4 = MathHelper.floor_double(this.posX);
            int l4 = MathHelper.floor_double(this.posY - 0.20000000298023224D);
            int i5 = MathHelper.floor_double(this.posZ);
            
            BlockPos blockpos = new BlockPos(j4, l4, i5);
            IBlockState iblockstate = this.worldObj.getBlockState(blockpos);

            if (iblockstate.getMaterial() == Material.AIR)
            {
                BlockPos blockpos1 = blockpos.down();
                IBlockState iblockstate1 = this.worldObj.getBlockState(blockpos1);
                Block block1 = iblockstate1.getBlock();

                if (block1 instanceof BlockFence || block1 instanceof BlockWall || block1 instanceof BlockFenceGate)
                {
                    iblockstate = iblockstate1;
                    blockpos = blockpos1;
                }
            }
            this.stickedBlock=blockpos;
            this.updateFallState(y, this.onGround, iblockstate, blockpos);*/
            /*double limit=y/d7;
            if(!this.isSticky()){
            	this.setEntityBoundingBox(this.boundingBox.offset(0.0D, y, 0.0D));
            }
            if(this.isSticky()){
	            if(x/d6<limit){
	            	limit=x/d6;
	            }
            }
            if(this.isSticky()){
        		if(z/d8<limit){
	            	limit=z/d8;
	            }
            	this.setEntityBoundingBox(this.boundingBox.offset(d6*limit,d7*limit,d8*limit));
            }*/

            if (d6 != x)
            {
            	this.onHitBlockX();
            }

            if (d8 != z)
            {
            	this.onHitBlockZ();
            }
            if (d7 != y)
            {
            	this.onHitBlockY();
            }

            try
            {
                this.func_145775_I();
            }
            catch (Throwable throwable)
            {
                CrashReport crashreport = CrashReport.makeCrashReport(throwable, "Checking entity block collision");
                CrashReportCategory crashreportcategory = crashreport.makeCategory("Entity being checked for collision");
                this.addEntityCrashInfo(crashreportcategory);
                throw new ReportedException(crashreport);
            }

            this.worldObj.theProfiler.endSection();
        }
    }
    /*public void resetPositionToBB()
    {
        AxisAlignedBB axisalignedbb = this.boundingBox;
        this.posX = (axisalignedbb.minX + axisalignedbb.maxX) / 2.0D;
        this.posY = (axisalignedbb.minY + axisalignedbb.maxY) / 2.0D;
        this.posZ = (axisalignedbb.minZ + axisalignedbb.maxZ) / 2.0D;
    }*/
    /**
     * (abstract) Protected helper method to write subclass entity data to NBT.
     */
    @Override
	public void writeEntityToNBT(NBTTagCompound p_70014_1_)
    {

    }

    /**
     * (abstract) Protected helper method to read subclass entity data from NBT.
     */
    @Override
	public void readEntityFromNBT(NBTTagCompound p_70037_1_)
    {
    	
    }

    /**
     * Called by a player entity when they collide with an entity
     */
    /**
     * returns if this entity triggers Block.onEntityWalking on the blocks they walk on. used for spiders and wolves to
     * prevent them from trampling crops
     */
    public abstract void onHitGround(int x, int y, int z, MovingObjectPosition mop);
    
    public abstract void onHitMob(Entity entityHit, MovingObjectPosition mop);
    
    @Override
	protected boolean canTriggerWalking()
    {
        return false;
    }

    //@SideOnly(Side.CLIENT)
    @Override
	public float getShadowSize()
    {
        return 0.0F;
    }

    public boolean moveable(){
    	return !this.isSticked();
    }
    /**
     * Sets the amount of knockback the arrow applies when it hits a mob.
     */
    public void setKnockbackStrength(int p_70240_1_)
    {
    }

    /**
     * If returns false, the item will not inflict any damage against entities.
     */
    @Override
	public boolean canAttackWithItem()
    {
        return false;
    }

    /**
     * Whether the arrow has a stream of critical hit particles flying behind it.
     */
    public void setCritical(int critical)
    {
        this.dataWatcher.updateObject(16, (byte) critical);
    }
    
    public void setSticked(boolean stick)
    {
        this.dataWatcher.updateObject(17, (byte)(stick?1:0));
        this.dataWatcher.updateObject(18,MathHelper.floor_double(this.posX * 32.0D));
        this.dataWatcher.updateObject(19,MathHelper.floor_double(this.posY * 32.0D));
        this.dataWatcher.updateObject(20,MathHelper.floor_double(this.posZ * 32.0D));
        /*if(!this.worldObj.isRemote){
        	EntityTracker tracker=((WorldServer)this.worldObj).getEntityTracker();
			tracker.sendToAllTrackingEntity(this, new S12PacketEntityVelocity(this));
			tracker.sendToAllTrackingEntity(this, new S18PacketEntityTeleport(this));
        }*/
    }
    /**
     * Whether the arrow has a stream of critical hit particles flying behind it.
     */
    public int getCritical()
    {
    	return this.dataWatcher.getWatchableObjectByte(16);
    }
    
    public boolean isSticked()
    {
        return this.dataWatcher.getWatchableObjectByte(17) == (byte) 1;
    }
    
    protected float getSpeed()
    {
        return 3;
    }
    
    protected double getGravity()
    {
    	return 0.0381f;
    }

	@Override
	public Entity getThrower() {
		return this.shootingEntity;
	}

	@Override
	public void setThrower(Entity entity) {
		this.shootingEntity=(EntityLivingBase) entity;
		
	}
	public boolean isSticky(){
		return false;
	}
	public void onHitBlockX(){
		this.motionX=0;
	}
	public void onHitBlockY(){
		this.motionY=0;
	}
	public void onHitBlockZ(){
		this.motionZ=0;
	}
	public abstract void spawnParticles(double x, double y, double z);
	
	public int getMaxTime(){
		return 1000;
	}
	
	@Override
	public boolean writeToNBTOptional(NBTTagCompound tagCompund){
		return false;
	}
	
	public boolean useCollisionBox(){
		return false;
	}
	
	public float getCollisionSize(){
    	return 0.3f;
	}
	@Override
	@SideOnly(Side.CLIENT)
    public boolean isInRangeToRenderDist(double distance)
    {
        double d0 = this.boundingBox.getAverageEdgeLength();

        if (Double.isNaN(d0))
        {
            d0 = 1.0D;
        }

        d0 = d0 * 512.0D * this.renderDistanceWeight;
        return distance < d0 * d0;
    }
}