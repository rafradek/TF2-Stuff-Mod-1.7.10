package rafradek.TF2weapons.characters;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import rafradek.TF2weapons.ItemFromData;
import rafradek.TF2weapons.TF2weapons;
import rafradek.TF2weapons.building.EntityBuilding;
import rafradek.TF2weapons.characters.ai.EntityAIAmbush;
import rafradek.TF2weapons.weapons.ItemCloak;
import rafradek.TF2weapons.weapons.WeaponsCapability;

public class EntitySpy extends EntityTF2Character {

	public int weaponCounter;
	public int cloakCounter;
	public EntitySpy(World p_i1738_1_) {
		super(p_i1738_1_);
		this.ammoLeft=16;
		this.experienceValue=15;
		this.rotation=20;
		this.tasks.addTask(3, new EntityAIAmbush(this));
		if(this.attack !=null){
			attack.setRange(30f);
			this.tasks.addTask(4, this.attack);
		}
		WeaponsCapability.get(this).invisTicks=20;
		this.getEntityData().setBoolean("IsCloaked", true);
		this.getEntityData().setBoolean("IsDisguised", true);
		this.getEntityData().setString("DisguiseType", "T:Engineer");
	}
	@Override
	public void onUpdate(){
		super.onUpdate();
		//cloak.getItem().onUpdate(cloak, worldObj, this, 0, true);
		//ItemDisguiseKit.startDisguise(this, this.worldObj, "M:Cow");
	}
	@Override
	public int getTalkInterval()
    {
        return 80;
    }
	@Override
	public void onLivingUpdate(){
		super.onLivingUpdate();
		if(!this.worldObj.isRemote){
			this.cloakCounter--;
			EntityLivingBase target=this.getAttackTarget();
			if(target!=null&&this.loadout[3].getTagCompound().getBoolean("Active")){
				boolean useKnife=false;
				if((this.getAITarget()!=null&&this.ticksExisted-this.func_142015_aE()<45)||(useKnife=(this.getDistanceSqToEntity(target)<13&&!TF2weapons.lookingAt(target, 110, this.posX, this.posY, this.posZ)))){
					
		        	((ItemCloak)this.loadout[3].getItem()).setCloak(!this.getEntityData().getBoolean("IsCloaked"), this.loadout[3], this, this.worldObj);
		        	if(useKnife){
		        		this.weaponCounter=8;
		        		this.setCombatTask(false);
		        		this.cloakCounter=36;
		        	}
		        	else{
		        		this.cloakCounter=20+(int) ((16-this.getDistanceToEntity(target))*10);
		        	}
		        }
				/*float x = -MathHelper.sin(target.rotationYaw / 180.0F * (float)Math.PI);
			    float z = MathHelper.cos(target.rotationYaw / 180.0F * (float)Math.PI);
			    this.setPosition(target.posX-x,target.posY,target.posZ-z);*/
			}
			
			if(this.cloakCounter<=0&&!this.loadout[3].getTagCompound().getBoolean("Active")){
				((ItemCloak)this.loadout[3].getItem()).setCloak(true, this.loadout[3], this, this.worldObj);
			}
			this.weaponCounter--;
			
			if(this.weaponCounter<=0&&this.getAttackTarget()!=null&&this.getDistanceSqToEntity(this.getAttackTarget())<4){
				this.setCombatTask(false);
				this.weaponCounter=8;
			}
			else if(this.weaponCounter<=0&&this.getHeldItem()==this.loadout[2]){
				this.setCombatTask(true);
				this.weaponCounter=3;
			}
			if(this.getAttackTarget()!=null && this.getAttackTarget() instanceof EntityBuilding &&
					((EntityBuilding)this.getAttackTarget()).isSapped()&&((EntityBuilding)this.getAttackTarget()).getOwner()!=null){
				this.setAttackTarget(((EntityBuilding)this.getAttackTarget()).getOwner());
			}
		}
	}
	@Override
	public int[] getValidSlots(){
		return new int[]{0,1,2,3};
	}
	@Override
	protected void addWeapons()
    {
        this.loadout[0]=ItemFromData.getRandomWeaponOfSlotMob("spy", 0, this.rand, false);
        this.loadout[1]=ItemFromData.getRandomWeaponOfSlotMob("spy", 1, this.rand, true);
        this.loadout[2]=ItemFromData.getRandomWeaponOfSlotMob("spy", 2, this.rand, false);
        this.loadout[3]=ItemFromData.getRandomWeaponOfSlotMob("spy", 3, this.rand, false);
        this.loadout[1].stackSize=64;
    }
	@Override
	protected String getLivingSound()
    {
        return TF2weapons.MOD_ID+":mob.spy.say";
    }
	

    /**
     * Returns the sound this mob makes when it is hurt.
     */
    @Override
	protected String getHurtSound()
    {
        return TF2weapons.MOD_ID+":mob.spy.hurt";
    }

    /**
     * Returns the sound this mob makes on death.
     */
    @Override
	protected String getDeathSound()
    {
        return TF2weapons.MOD_ID+":mob.spy.death";
    }
    /**
     * Plays step sound at given x, y, z for the entity
     */
    @Override
	public void setCombatTask(boolean ranged){
    	this.ranged=ranged;
    	if(ranged){
    		
    		this.switchSlot(0);
			this.attack.setRange(30);
    	}
    	else{
    		if(this.getAttackTarget() instanceof EntityBuilding){
    			this.switchSlot(1);
    			this.attack.setRange(1.9f);
    		}
    		else{
    			this.switchSlot(2);
    			this.attack.setRange(2.2f);
    		}
			
    	}
    }
    
    /**
     * Get this Entity's EnumCreatureAttribute
     */
    public int[] ammoTypesDropped(){
		return new int[]{4,0};
	}
    @Override
	protected void dropFewItems(boolean p_70628_1_, int p_70628_2_)
    {
    	super.dropFewItems(p_70628_1_, p_70628_2_);
    	/*if(this.rand.nextFloat()<0.085f+p_70628_2_*0.05f){
    		this.entityDropItem(ItemFromData.getNewStack("revolver"), 0);
    	}
    	if(this.rand.nextFloat()<0.06f+p_70628_2_*0.025f){
    		this.entityDropItem(ItemFromData.getNewStack("butterflyknife"), 0);
    	}
    	if(this.rand.nextFloat()<0.05f+p_70628_2_*0.025f){
    		this.entityDropItem(ItemFromData.getNewStack("cloak"), 0);
    	}*/
    	if(this.rand.nextFloat()<0.05f+p_70628_2_*0.025f){
    		this.entityDropItem(new ItemStack(TF2weapons.itemDisguiseKit), 0);
    	}
    }
	@Override
	protected void applyEntityAttributes()
    {
        super.applyEntityAttributes();
        this.getEntityAttribute(SharedMonsterAttributes.followRange).setBaseValue(50.0D);
        this.getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(15.0D);
        this.getEntityAttribute(SharedMonsterAttributes.knockbackResistance).setBaseValue(0.25D);
        this.getEntityAttribute(SharedMonsterAttributes.movementSpeed).setBaseValue(0.32D);
        this.getEntityAttribute(SharedMonsterAttributes.attackDamage).setBaseValue(4.0D);
    }
}
