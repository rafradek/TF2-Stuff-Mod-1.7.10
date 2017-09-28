package rafradek.TF2weapons.characters;

import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import rafradek.TF2weapons.ItemFromData;
import rafradek.TF2weapons.TF2weapons;

public class EntityDemoman extends EntityTF2Character{
	
	public ItemStack stickyBombLauncher=ItemFromData.getNewStack("stickybomblauncher");
	
	public EntityDemoman(World par1World) {
		super(par1World);
		if(this.attack !=null){
			this.attack.setRange(20F);
			this.attack.projSpeed=1.16205f;
			this.attack.gravity=0.0381f;
		}
		this.rotation=10;
		this.ammoLeft=12;
		this.experienceValue=15;
		
		//this.setItemStackToSlot(EntityEquipmentSlot.MAINHAND, ItemUsable.getNewStack("Minigun"));
		
	}
	public int[] ammoTypesDropped(){
		return new int[]{8,11};
	}
	/*protected ResourceLocation getLootTable()
    {
        return TF2weapons.lootDemoman;
    }*/
	/*protected void addWeapons()
    {
        this.setItemStackToSlot(EntityEquipmentSlot.MAINHAND, ItemFromData.getNewStack("grenadelauncher"));
    }*/
	@Override
	protected void applyEntityAttributes()
    {
        super.applyEntityAttributes();
        this.getEntityAttribute(SharedMonsterAttributes.followRange).setBaseValue(35.0D);
        this.getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(18.5D);
        this.getEntityAttribute(SharedMonsterAttributes.knockbackResistance).setBaseValue(0.5D);
        this.getEntityAttribute(SharedMonsterAttributes.movementSpeed).setBaseValue(0.302D);
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
        return TF2weapons.MOD_ID+":mob.demoman.say";
    }
	

    /**
     * Returns the sound this mob makes when it is hurt.
     */
    @Override
	protected String getHurtSound()
    {
        return TF2weapons.MOD_ID+":mob.demoman.hurt";
    }

    /**
     * Returns the sound this mob makes on death.
     */
    @Override
	protected String getDeathSound()
    {
        return TF2weapons.MOD_ID+":mob.demoman.say";
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
