package rafradek.TF2weapons.characters;

import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import rafradek.TF2weapons.ItemFromData;
import rafradek.TF2weapons.TF2weapons;
import rafradek.TF2weapons.weapons.ItemSniperRifle;

public class EntitySniper extends EntityTF2Character{

	public EntitySniper(World par1World) {
		super(par1World);
		this.ammoLeft=18;
		this.experienceValue=15;
		this.rotation=3;
		if(this.attack !=null){
			attack.setRange(50);
		}
		//this.setItemStackToSlot(EntityEquipmentSlot.MAINHAND, ItemUsable.getNewStack("Minigun"));
		
	}
	/*protected void addWeapons()
    {
		this.loadout[0]=ItemFromData.getNewStack("sniperrifle");
		this.loadout[1]=ItemFromData.getNewStack("sniperrifle");
    }*/
	@Override
	protected void applyEntityAttributes()
    {
        super.applyEntityAttributes();
        this.getEntityAttribute(SharedMonsterAttributes.followRange).setBaseValue(60.0D);
        this.getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(15.0D);
        this.getEntityAttribute(SharedMonsterAttributes.knockbackResistance).setBaseValue(0.25D);
        this.getEntityAttribute(SharedMonsterAttributes.movementSpeed).setBaseValue(0.31D);
        this.getEntityAttribute(SharedMonsterAttributes.attackDamage).setBaseValue(6.0D);
    }
	@Override
	public void onLivingUpdate()
    {
		if(!this.worldObj.isRemote&&(this.getAttackTarget()==null||!this.getAttackTarget().isEntityAlive())&&this.getHeldItem().getTagCompound().getBoolean("Zoomed")){
			((ItemSniperRifle) this.getHeldItem().getItem()).disableZoom(this.getHeldItem(), this);
		}
		if(this.getHeldItem()!=null&&!this.getHeldItem().getTagCompound().getBoolean("Zoomed")){
			this.ignoreFrustumCheck=false;
			this.rotation=15;
		}
		else{
			this.ignoreFrustumCheck=true;
			this.rotation=3;
		}
		//System.out.println("state: "+this.getHeldItem().getTagCompound().getBoolean("Zoomed")+" "+this.worldObj.isRemote);
		//System.out.println(this.getAttackTarget()!=null&&!this.getAttackTarget().isEntityAlive());
		/*if(this.getAttackTarget()!=null&&this.getEntitySenses().canSee(this.getAttackTarget())&&){
			((ItemRangedWeapon)this.getHeldItem().getItem()).altUse(getHeldItem(EnumHand.MAIN_HAND), this, worldObj);
		}
		else if(this.getHeldItem().getTagCompound().getBoolean("Zoomed")){
			((ItemRangedWeapon)this.getHeldItem().getItem()).altUse(getHeldItem(EnumHand.MAIN_HAND), this, worldObj);
		}*/
        super.onLivingUpdate();

    }
	@Override
	protected String getLivingSound()
    {
        return TF2weapons.MOD_ID+":mob.sniper.say";
    }
	

    /**
     * Returns the sound this mob makes when it is hurt.
     */
    @Override
	protected String getHurtSound()
    {
        return TF2weapons.MOD_ID+":mob.sniper.hurt";
    }

    /**
     * Returns the sound this mob makes on death.
     */
    @Override
	protected String getDeathSound()
    {
        return TF2weapons.MOD_ID+":mob.sniper.death";
    }

    /**
     * Get this Entity's EnumCreatureAttribute
     */
    public int[] ammoTypesDropped(){
		return new int[]{6,5};
	}
    @Override
	protected void dropFewItems(boolean p_70628_1_, int p_70628_2_)
    {
    	super.dropFewItems(p_70628_1_, p_70628_2_);
    }
    @Override
	public float getAttributeModifier(String attribute) {
    	if(this.getAttackTarget() != null && this.getAttackTarget() instanceof EntityPlayer){
			if(attribute.equals("Spread")){
				return super.getAttributeModifier(attribute);
			}
    	}
		return super.getAttributeModifier(attribute);
	}
    
    @Override
	public float getMotionSensitivity(){
    	return this.getDiff()==1 ? 0.75f : (this.getDiff()==3 ? 0.45f : 0.55f);
    }
    public int getMaxSpawnedInChunk()
    {
		return 2;
    }
}
