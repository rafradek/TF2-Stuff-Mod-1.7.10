package rafradek.TF2weapons.building;

import com.google.common.base.Predicate;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityLookHelper;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import rafradek.TF2weapons.TF2weapons;
import rafradek.TF2weapons.characters.EntityTF2Character;
import rafradek.TF2weapons.characters.ai.EntityAINearestChecked;
import rafradek.TF2weapons.characters.ai.EntityAISentryAttack;
import rafradek.TF2weapons.characters.ai.EntityAISentryIdle;
import rafradek.TF2weapons.characters.ai.EntityAISentryOwnerHurt;
import rafradek.TF2weapons.weapons.ItemUsable;

public class EntitySentry extends EntityBuilding {

	public ItemStack sentryBullet=ItemUsable.getNewStack("sentrybullet");
	public ItemStack sentryBullet2=ItemUsable.getNewStack("sentrybullet2");
	public ItemStack sentryRocket=ItemUsable.getNewStack("sentryrocket");
	public float rotationDefault=0;
	public int attackDelay;
	public int attackDelayRocket;
	public EntityLookHelper sentryLookHelper;
	
	public EntitySentry(World worldIn) {
		super(worldIn);
		this.setSize(0.8f, 0.8f);
	}
	public EntitySentry(World worldIn, EntityLivingBase owner) {
		super(worldIn,owner);
		this.setSize(0.8f, 0.8f);
	}
	@Override
	public void adjustSize(){
		if(this.getLevel()==1){
			this.setSize(0.8f, 0.8f);
		}
		else if(this.getLevel()==2){
			this.setSize(1f, 1f);
		}
		else if(this.getLevel()==3){
			this.setSize(1.2f, 1.4f);
		}
	}
	@Override
	public String getSoundNameForState(int state){
		switch(state){
		case 0:return "rafradek_tf2_weapons:mob.sentry.scan."+this.getLevel();
		case 2:return "rafradek_tf2_weapons:mob.sentry.shoot."+this.getLevel();
		case 3:return "rafradek_tf2_weapons:mob.sentry.empty";
		default:return null;
		}
	}
	@Override
	public void setAttackTarget(EntityLivingBase target){
		if(TF2weapons.isOnSameTeam(this.getOwner(),target)){
			return;
		}
		if(target!=this.getAttackTarget()&&target != null){
			this.worldObj.playSoundAtEntity(this, "rafradek_tf2_weapons:mob.sentry.spot", 0.55f, 1f);
		}
		super.setAttackTarget(target);
	}
	@Override
	public void applyTasks(){
		
		this.targetTasks.addTask(1, new EntityAISentryOwnerHurt(this,true));
		this.targetTasks.addTask(2, new EntityAINearestChecked(this, EntityLivingBase.class, true,false,new Predicate<EntityLivingBase>(){
			@Override
			public boolean apply(EntityLivingBase target)
	        {
				return ((((TF2weapons.sentryAttacksPlayers||getOwnerId().isEmpty())&&target instanceof EntityPlayer) || target.getTeam()!=null || (TF2weapons.sentryAttacksMobs&&target instanceof IMob&& !getOwnerId().isEmpty()) ||(target instanceof EntityLiving &&((EntityLiving)target).getAttackTarget()==getOwner()))&&!TF2weapons.isOnSameTeam(EntitySentry.this, target))&&(!(target instanceof EntityTF2Character) ||!((EntityTF2Character)target).natural);
				
	        }
		}, false));
		this.tasks.addTask(1, new EntityAISentryAttack(this));
		this.tasks.addTask(2, new EntityAISentryIdle(this));
	}
	
	@Override
	public void onLivingUpdate()
	{
		if(this.rotationDefault==0){
			this.rotationDefault=this.rotationYawHead;
		}
		if(this.attackDelay>0){
			this.attackDelay--;
		}
		if(this.attackDelayRocket>0){
			this.attackDelayRocket--;
		}
		if(this.getAttackTarget()!=null&&!this.getAttackTarget().isEntityAlive()){
			this.setAttackTarget(null);
		}
        super.onLivingUpdate();
	}
	@Override
	public ItemStack getHeldItem(){
		return sentryRocket;
	}
	@Override
	protected void applyEntityAttributes()
    {
        super.applyEntityAttributes();
        this.getEntityAttribute(SharedMonsterAttributes.followRange).setBaseValue(20.0D);
        //this.getEntityAttribute(SharedMonsterAttributes.attackDamage).setBaseValu(1.6D);
    }
	@Override
	protected void entityInit()
	{
		super.entityInit();
		this.getDataWatcher().addObject(16, (short)150);
		this.getDataWatcher().addObject(17, (short)20);
		this.getDataWatcher().addObject(18, (short)0);
	}

	@Override
	public boolean canEntityBeSeen(Entity entityIn)
    {
		return this.worldObj.func_147447_a(Vec3.createVectorHelper(this.posX, this.posY + this.getEyeHeight(), this.posZ), Vec3.createVectorHelper(entityIn.posX, entityIn.posY + entityIn.getEyeHeight(), entityIn.posZ),false,true,false) == null;
    }
	public int getMaxAmmo(){
		return this.getLevel()==1?150:200;
	}
	
	public short getAmmo(){
		return this.getDataWatcher().getWatchableObjectShort(16);
	}
	
	public short getKills(){
		return this.getDataWatcher().getWatchableObjectShort(18);
	}
	
	public short getRocketAmmo(){
		return this.getDataWatcher().getWatchableObjectShort(17);
	}
	
	public void setAmmo(int ammo){
		this.getDataWatcher().updateObject(16, (short) ammo);
	}
	
	public void setRocketAmmo(int ammo){
		this.getDataWatcher().updateObject(17, (short) ammo);
	}
	public void setKills(int kills){
		this.getDataWatcher().updateObject(18, (short) kills);
	}
	@Override
	public void writeEntityToNBT(NBTTagCompound par1NBTTagCompound)
    {
        super.writeEntityToNBT(par1NBTTagCompound);
        
        par1NBTTagCompound.setShort("Ammo", this.getAmmo());
        par1NBTTagCompound.setShort("RocketAmmo", this.getRocketAmmo());
        par1NBTTagCompound.setShort("Kills", this.getKills());
    }
	@Override
	public void readEntityFromNBT(NBTTagCompound par1NBTTagCompound)
    {
        super.readEntityFromNBT(par1NBTTagCompound);
        
        this.setAmmo(par1NBTTagCompound.getShort("Ammo"));
        this.setRocketAmmo(par1NBTTagCompound.getShort("RocketAmmo"));
        this.setKills(par1NBTTagCompound.getShort("Kills"));
    }
	@Override
	public float getCollHeight(){
		return 1.2f;
	}
	@Override
	public float getCollWidth(){
		return 1.12f;
	}
	@Override
	public float getEyeHeight(){
		return this.height/2+0.2f;
	}
	@Override
	protected String getHurtSound()
    {
        return this.isSapped()?null:"rafradek_tf2_weapons:mob.sentry.hurt";
    }

    @Override
	protected String getDeathSound()
    {
        return "rafradek_tf2_weapons:mob.sentry.death";
    }
    
    @Override
	public boolean canUseWrench(){
		return super.canUseWrench()||this.getAmmo()<this.getMaxAmmo()||this.getRocketAmmo()<20;
	}
    @Override
	public void upgrade(){
    	super.upgrade();
    	this.setAmmo(200);
    }
}
