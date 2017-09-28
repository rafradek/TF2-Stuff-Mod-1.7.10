package rafradek.TF2weapons.characters;

import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import rafradek.TF2weapons.ItemFromData;
import rafradek.TF2weapons.TF2weapons;
import rafradek.TF2weapons.weapons.WeaponsCapability;

public class EntityHeavy extends EntityTF2Character{
	
	public EntityHeavy(World par1World) {
		super(par1World);
		if(this.attack !=null){
			this.attack.setDodge(true);
			this.attack.dodgeSpeed=1.25f;
		}
		this.rotation=10;
		this.ammoLeft=133;
		this.experienceValue=15;
		//this.setItemStackToSlot(EntityEquipmentSlot.MAINHAND, ItemUsable.getNewStack("Minigun"));
		
	}
	@Override
	protected void applyEntityAttributes()
    {
        super.applyEntityAttributes();
        this.getEntityAttribute(SharedMonsterAttributes.followRange).setBaseValue(40.0D);
        this.getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(30.0D);
        this.getEntityAttribute(SharedMonsterAttributes.knockbackResistance).setBaseValue(0.75D);
        this.getEntityAttribute(SharedMonsterAttributes.movementSpeed).setBaseValue(0.27D);
        this.getEntityAttribute(SharedMonsterAttributes.attackDamage).setBaseValue(6.0D);
    }
	@Override
	public void onLivingUpdate()
    {
        super.onLivingUpdate();
        if(this.ammoLeft>0&&this.getAttackTarget()!=null&&this.getDistanceSqToEntity(this.getAttackTarget())<=350&&(WeaponsCapability.get(this).state&2)==0){
        	WeaponsCapability.get(this).state+=2;
    	}
    }
	@Override
	protected String getLivingSound()
    {
        return TF2weapons.MOD_ID+":mob.heavy.say";
    }
	
	public int[] ammoTypesDropped(){
		return new int[]{2,1};
	}
    /**
     * Returns the sound this mob makes when it is hurt.
     */
    @Override
	protected String getHurtSound()
    {
        return TF2weapons.MOD_ID+":mob.heavy.hurt";
    }

    /**
     * Returns the sound this mob makes on death.
     */
    @Override
	protected String getDeathSound()
    {
        return TF2weapons.MOD_ID+":mob.heavy.say";
    }

    /**
     * Get this Entity's EnumCreatureAttribute
     */
    @Override
	protected void dropFewItems(boolean p_70628_1_, int p_70628_2_)
    {
    	super.dropFewItems(p_70628_1_, p_70628_2_);
    }
	@Override
	public float getAttributeModifier(String attribute) {
		if(this.getAttackTarget() != null && this.getAttackTarget() instanceof EntityPlayer){
			if(attribute.equals("Minigun Spinup")){
				return this.getDiff()==1 ? 2f : (this.getDiff()==3 ? 1.2f : 1.55f);
			}
		}
		return super.getAttributeModifier(attribute);
	}
}
