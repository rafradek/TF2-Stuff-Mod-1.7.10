package rafradek.TF2weapons.characters;

import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.world.World;
import rafradek.TF2weapons.ItemFromData;
import rafradek.TF2weapons.TF2weapons;

public class EntitySoldier extends EntityTF2Character{
	
	public boolean rocketJump;
	public boolean rocketJumper;
	public boolean airborne;

	public EntitySoldier(World par1World) {
		super(par1World);
		//this.rotation=90;
		this.rocketJumper=this.rand.nextBoolean();
		if(this.attack !=null){
			attack.setRange(35);
			attack.fireAtFeet=2;
			attack.projSpeed=1.04f;
			attack.explosive=true;
			attack.setDodge(this.rocketJumper);
		}
		this.ammoLeft=20;
		this.experienceValue=15;
		//this.setItemStackToSlot(EntityEquipmentSlot.MAINHAND, ItemUsable.getNewStack("Minigun"));
		
	}
	/*protected void addWeapons()
    {
        this.setItemStackToSlot(EntityEquipmentSlot.MAINHAND, ItemFromData.getNewStack("rocketlauncher"));
    }*/
	@Override
	protected void applyEntityAttributes()
    {
        super.applyEntityAttributes();
        this.getEntityAttribute(SharedMonsterAttributes.followRange).setBaseValue(50.0D);
        this.getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(20.0D);
        this.getEntityAttribute(SharedMonsterAttributes.knockbackResistance).setBaseValue(0.75D);
        this.getEntityAttribute(SharedMonsterAttributes.movementSpeed).setBaseValue(0.28D);
        this.getEntityAttribute(SharedMonsterAttributes.attackDamage).setBaseValue(6.0D);
    }
	@Override
	public void onLivingUpdate()
    {
		
		if(!this.worldObj.isRemote&&this.rocketJumper&&this.getAttackTarget()!=null&&this.getHealth()>7f&&!this.airborne&&this.onGround&&
				this.getHeldItem().getItemDamage()==0){
			this.rocketJump=true;
		}
		/*if(this.rocketJump&&this.getEntityData().getCompoundTag("TF2").getShort("reload")<=50){
			TF2ActionHandler.playerAction.get(this.worldObj.isRemote).put(this, 1);
			this.jump=true;
			this.rotationYaw-=
			this.rotationPitch=8;
			this.getLookHelper().setLookPosition(this.posX,this.posY-1,this.posZ, 180, 90.0F);
		}*/
		/*if(this.rocketJump&&!this.onGround){
			this.rocketJump=false;
			TF2ActionHandler.playerAction.get(this.worldObj.isRemote).put(this, 0);
		}*/
		if(this.airborne){
			this.jump=false;
		}
        super.onLivingUpdate();
        /*if(this.ammoLeft>0&&this.getAttackTarget()!=null&&this.getDistanceSqToEntity(this.getAttackTarget())<=400&&(!TF2ActionHandler.playerAction.get(this.worldObj.isRemote).containsKey(this)||(TF2ActionHandler.playerAction.get(this.worldObj.isRemote).get(this)&3)==0)){
    		TF2ActionHandler.playerAction.get(this.worldObj.isRemote).put(this, TF2ActionHandler.playerAction.get(this.worldObj.isRemote).containsKey(this)?TF2ActionHandler.playerAction.get(this.worldObj.isRemote).get(this)+2:2);
    	}*/
    }
	@Override
	public void fall(float distance)
    {
		super.fall(distance*0.35f);
		this.airborne=false;
    }
	@Override
	public void onShot() {
		if(this.rocketJump){
			this.jump=true;
			this.rotationYawHead=this.rotationYawHead+180;
			this.rotationPitch=50;
			this.rocketJump=false;
			this.airborne=true;
			//this.getLookHelper().setLookPosition(this.posX,this.posY-1,this.posZ, 180, 90.0F);
		}
	}
	@Override
	protected String getLivingSound()
    {
        return TF2weapons.MOD_ID+":mob.soldier.say";
    }
	

    /**
     * Returns the sound this mob makes when it is hurt.
     */
    @Override
	protected String getHurtSound()
    {
        return TF2weapons.MOD_ID+":mob.soldier.hurt";
    }

    /**
     * Returns the sound this mob makes on death.
     */
    @Override
	protected String getDeathSound()
    {
        return TF2weapons.MOD_ID+":mob.soldier.death";
    }
    public int[] ammoTypesDropped(){
		return new int[]{7,1};
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
