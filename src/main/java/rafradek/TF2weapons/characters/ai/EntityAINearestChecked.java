package rafradek.TF2weapons.characters.ai;

import java.util.Collections;
import java.util.List;

import com.google.common.base.Predicate;

import net.minecraft.command.IEntitySelector;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IEntityOwnable;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget.Sorter;
import net.minecraft.entity.ai.EntityAITarget;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.scoreboard.Team;
import rafradek.TF2weapons.TF2weapons;
import rafradek.TF2weapons.building.EntityBuilding;
import rafradek.TF2weapons.characters.EntityMedic;
import rafradek.TF2weapons.characters.EntityTF2Character;
import rafradek.TF2weapons.weapons.WeaponsCapability;



public class EntityAINearestChecked extends EntityAITarget {
	public int targetChoosen=0;
	private Class<? extends EntityLivingBase> targetClass;
	private Sorter theNearestAttackableTargetSorter;
	private Predicate<EntityLivingBase> targetEntitySelector;
	public EntityLivingBase targetEntity;
	private boolean targetLock;
	private int targetUnseenTicks;
	public EntityAINearestChecked(EntityCreature p_i1665_1_, Class<? extends EntityLivingBase> p_i1665_2_, boolean p_i1665_4_, boolean p_i1665_5_, final Predicate<EntityLivingBase> p_i1665_6_, boolean targetLock)
    {
        super(p_i1665_1_, p_i1665_4_, p_i1665_5_);
        this.targetClass = p_i1665_2_;
        this.theNearestAttackableTargetSorter = new EntityAINearestAttackableTarget.Sorter(p_i1665_1_);
        this.setMutexBits(1);
        this.targetLock=targetLock;
        this.targetEntitySelector = new Predicate<EntityLivingBase>()
        {
            @Override
			public boolean apply(EntityLivingBase target)
            {
                if (p_i1665_6_ != null && !p_i1665_6_.apply(target))
                {
                    return false;
                }
                else
                {
                	//System.out.println("found "+target.getClass().getName()+" "+EntityAINearestChecked.this.taskOwner.getClass().getName());
                    if (target instanceof EntityLivingBase)
                    {
                        double d0 = EntityAINearestChecked.this.getTargetDistance();

                        if (target.isSneaking())
                        {
                            d0 *= 0.800000011920929D;
                        }

                        if (target instanceof EntityPlayer && target.isInvisible())
                        {
                            float f = ((EntityPlayer)target).getArmorVisibility();

                            if (f < 0.1F)
                            {
                                f = 0.1F;
                            }

                            d0 *= 0.7F * f;
                        }
                        if(target.getExtendedProperties(WeaponsCapability.ID)!=null&&(WeaponsCapability.get(target).invisTicks>=20||(target.getEntityData().getBoolean("IsDisguised")&&(WeaponsCapability.get(target).invisTicks==0||taskOwner instanceof EntityBuilding)))){
                        	d0=taskOwner instanceof EntityBuilding?0:1;
                        }
                        
                        if (target.getDistanceToEntity(EntityAINearestChecked.this.taskOwner) > d0||(!(taskOwner instanceof EntityBuilding)&&!TF2weapons.lookingAt(EntityAINearestChecked.this.taskOwner, 105, target.posX, target.posY+target.getEyeHeight(), target.posZ)))
                        {
                            return false;
                        }
                        
                        if(target instanceof EntityPlayer && ((EntityPlayer)target).capabilities.isCreativeMode){
                        	return false;
                        }
                        
                    }

                    return EntityAINearestChecked.this.isSuitableTarget(target, false);
                }
            }
        };
    }
	@Override
	@SuppressWarnings("unchecked")
	public boolean shouldExecute()
    {
		double d0 = this.getTargetDistance()/2;
		if(!this.targetLock||((this.taskOwner.getAttackTarget()==null)||this.taskOwner.getAttackTarget().getDistanceSqToEntity(taskOwner)>d0*d0)){
			this.targetChoosen++;
		}
        if(((this.taskOwner.getAttackTarget()==null)&&this.targetChoosen>1)||this.targetChoosen>5){
        	//System.out.println("executing "+this.taskOwner.getClass().getName());
        	this.targetChoosen=0;
            double d1 = this.getTargetDistance();
           // System.out.println("choose "+this.taskOwner.getClass().getName());
            List<? extends EntityLivingBase> list = this.taskOwner.worldObj.selectEntitiesWithinAABB(this.targetClass, this.taskOwner.boundingBox.expand(d1, d0, d1), new IEntitySelector(){

				@Override
				public boolean isEntityApplicable(Entity p_82704_1_) {
					// TODO Auto-generated method stub
					return targetEntitySelector.apply((EntityLivingBase) p_82704_1_);
				}
            	
            });
            Collections.sort(list, this.theNearestAttackableTargetSorter);

            if (list.isEmpty())
            {
            	//System.out.println("emptylist "+this.taskOwner.getClass().getName());
                return false;
            }
            else
            {
                this.targetEntity = list.get(0);
                return true;
            }
        }
        return false;
    }
	@Override
	public void startExecuting()
    {
        this.taskOwner.setAttackTarget(this.targetEntity);
        if(this.taskOwner instanceof EntityTF2Character && this.taskOwner.getAttackTarget()!=null){
        	EntityTF2Character shooter=((EntityTF2Character)this.taskOwner);
        	shooter.targetPrevPos[0]=shooter.getAttackTarget().posX;
        	shooter.targetPrevPos[2]=shooter.getAttackTarget().posY;
        	shooter.targetPrevPos[4]=shooter.getAttackTarget().posZ;
        }
        super.startExecuting();
    }

    /**
     * A method used to see if an entity is a suitable target through a number of checks. Args : entity,
     * canTargetInvinciblePlayer
     */
	@Override
	public boolean continueExecuting()
    {
        EntityLivingBase entitylivingbase = this.taskOwner.getAttackTarget();

        if (entitylivingbase == null)
        {
            return false;
        }
        else if (!entitylivingbase.isEntityAlive())
        {
            return false;
        }
        else
        {
            Team team = this.taskOwner.getTeam();
            Team team1 = entitylivingbase.getTeam();

            if((team != null && team1 == team)&&!(this.taskOwner instanceof EntityMedic))
            {
                return false;
            }
            else
            {
                double d0 = this.getTargetDistance();

                if (this.taskOwner.getDistanceSqToEntity(entitylivingbase) > d0 * d0)
                {
                    return false;
                }
                else
                {
                    if (this.shouldCheckSight)
                    {
                        if (this.taskOwner.getEntitySenses().canSee(entitylivingbase))
                        {
                            this.targetUnseenTicks = 0;
                        }
                        else if (++this.targetUnseenTicks > 60)
                        {
                            return false;
                        }
                    }

                    return !(entitylivingbase instanceof EntityPlayer) || !((EntityPlayer)entitylivingbase).capabilities.disableDamage;
                }
            }
        }
    }
    @Override
	protected boolean isSuitableTarget(EntityLivingBase target, boolean includeInvincibles)
    {
    	if (target == null)
        {
            return false;
        }
        else if (target == this.taskOwner)
        {
            return false;
        }
        else if (!target.isEntityAlive())
        {
            return false;
        }
        else if (!this.taskOwner.canAttackClass(target.getClass()))
        {
            return false;
        }
        else
        {
            Team team = this.taskOwner.getTeam();
            Team team1 = target.getTeam();

            if ((team != null && team1 == team)&&!(this.taskOwner instanceof EntityMedic))
            {
                return false;
            }
            else
            {
                if (this.taskOwner instanceof IEntityOwnable && ((IEntityOwnable)this.taskOwner).func_152113_b()!=null)
                {
                    if (target instanceof IEntityOwnable && ((IEntityOwnable)this.taskOwner).func_152113_b().equals(((IEntityOwnable)target).func_152113_b()))
                    {
                        return false;
                    }

                    if (target == ((IEntityOwnable)this.taskOwner).getOwner())
                    {
                        return false;
                    }
                }
                else if (target instanceof EntityPlayer && !includeInvincibles && (((EntityPlayer)target).capabilities.disableDamage||((EntityPlayer)target).capabilities.isCreativeMode))
                {
                    return false;
                }

                return !this.shouldCheckSight || this.taskOwner.getEntitySenses().canSee(target);
            }
        }
    }
}
