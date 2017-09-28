package rafradek.TF2weapons.characters;

import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;
import rafradek.TF2weapons.ItemFromData;
import rafradek.TF2weapons.TF2weapons;
import rafradek.TF2weapons.characters.ai.EntityAIAirblast;

public class EntityPyro extends EntityTF2Character{
	
	public EntityPyro(World par1World) {
		super(par1World);
		this.tasks.addTask(3, new EntityAIAirblast(this));
		if(this.attack !=null){
			this.attack.setDodge(true);
			this.attack.dodgeSpeed=1.2f;
			this.attack.setRange(6.2865F);
			this.attack.projSpeed=1.2570f;
		}
		this.rotation=16;
		this.ammoLeft=133;
		this.experienceValue=15;
		//((PathNavigateGround)this.getNavigator()).set(true);
		//this.setItemStackToSlot(EntityEquipmentSlot.MAINHAND, ItemUsable.getNewStack("Minigun"));
		
	}
	@Override
	public boolean attackEntityFrom(DamageSource source, float amount)
    {
        if(source==DamageSource.onFire){
        	return false;
        }
        return super.attackEntityFrom(source, amount);
    }
	@Override
	public void setFire(int time){
		super.setFire(1);
	}
	/*protected void addWeapons()
    {
        this.setItemStackToSlot(EntityEquipmentSlot.MAINHAND, ItemFromData.getNewStack("flamethrower"));
    }*/
	@Override
	protected void applyEntityAttributes()
    {
        super.applyEntityAttributes();
        this.getEntityAttribute(SharedMonsterAttributes.followRange).setBaseValue(35.0D);
        this.getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(18.5D);
        this.getEntityAttribute(SharedMonsterAttributes.knockbackResistance).setBaseValue(0.5D);
        this.getEntityAttribute(SharedMonsterAttributes.movementSpeed).setBaseValue(0.31D);
        this.getEntityAttribute(SharedMonsterAttributes.attackDamage).setBaseValue(6.0D);
    }
	/*public void onLivingUpdate()
    {
        super.onLivingUpdate();
        if(this.ammoLeft>0&&this.getAttackTarget()!=null&&this.getDistanceSqToEntity(this.getAttackTarget())<=400&&(!TF2ActionHandler.playerAction.get(this.worldObj.isRemote).containsKey(this)||(TF2ActionHandler.playerAction.get(this.worldObj.isRemote).get(this)&3)==0)){
    		TF2ActionHandler.playerAction.get(this.worldObj.isRemote).put(this, TF2ActionHandler.playerAction.get(this.worldObj.isRemote).containsKey(this)?TF2ActionHandler.playerAction.get(this.worldObj.isRemote).get(this)+2:2);
    	}
    }*/
	@Override
	protected String getLivingSound()
    {
        return TF2weapons.MOD_ID+":mob.pyro.say";
    }
	

    /**
     * Returns the sound this mob makes when it is hurt.
     */
    @Override
	protected String getHurtSound()
    {
        return TF2weapons.MOD_ID+":mob.pyro.hurt";
    }

    /**
     * Returns the sound this mob makes on death.
     */
    @Override
	protected String getDeathSound()
    {
        return TF2weapons.MOD_ID+":mob.pyro.say";
    }
    public int[] ammoTypesDropped(){
		return new int[]{10,1};
	}
    /**
     * Get this Entity's EnumCreatureAttribute
     */
    @Override
	protected void dropFewItems(boolean p_70628_1_, int p_70628_2_)
    {
    	super.dropFewItems(p_70628_1_, p_70628_2_);
    }
	/*@Override
	public float getAttributeModifier(String attribute) {
		if(attribute.equals("Minigun Spinup")){
			return super.getAttributeModifier(attribute)*1.5f;
		}
		return super.getAttributeModifier(attribute);
	}*/
}
