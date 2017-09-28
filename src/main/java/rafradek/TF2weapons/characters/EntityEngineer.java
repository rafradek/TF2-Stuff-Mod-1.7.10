package rafradek.TF2weapons.characters;

import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import rafradek.TF2weapons.ItemFromData;
import rafradek.TF2weapons.TF2weapons;
import rafradek.TF2weapons.building.EntityDispenser;
import rafradek.TF2weapons.building.EntitySentry;
import rafradek.TF2weapons.characters.ai.EntityAIRepair;
import rafradek.TF2weapons.characters.ai.EntityAISetup;

public class EntityEngineer extends EntityTF2Character {
	
	public EntitySentry sentry;
	public EntityDispenser dispenser;
	public int metal=500;
	public EntityEngineer(World p_i1738_1_) {
		super(p_i1738_1_);
		this.ammoLeft=24;
		this.experienceValue=15;
		this.rotation=15;
		this.tasks.addTask(3, new EntityAIRepair(this,1,2f));
		this.tasks.addTask(5, new EntityAISetup(this));
		this.tasks.removeTask(wander);
		if(this.attack !=null){
			attack.setRange(20);
		}
	}
	@Override
	protected void addWeapons()
    {
        super.addWeapons();
        this.loadout[2]=ItemFromData.getRandomWeaponOfSlotMob("engineer", 2, this.rand, false);
    }
	@Override
	protected void applyEntityAttributes()
    {
        super.applyEntityAttributes();
        this.getEntityAttribute(SharedMonsterAttributes.followRange).setBaseValue(10.0D);
        this.getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(15.0D);
        this.getEntityAttribute(SharedMonsterAttributes.knockbackResistance).setBaseValue(0.25D);
        this.getEntityAttribute(SharedMonsterAttributes.movementSpeed).setBaseValue(0.33D);
        this.getEntityAttribute(SharedMonsterAttributes.attackDamage).setBaseValue(6.0D);
    }
	@Override
	public int[] getValidSlots(){
		return new int[]{0,1,2};
	}

    /**
     * Returns the sound this mob makes when it is hurt.
     */
	@Override
	protected String getLivingSound()
    {
        return TF2weapons.MOD_ID+":mob.engineer.say";
    }
	

    /**
     * Returns the sound this mob makes when it is hurt.
     */
    @Override
	protected String getHurtSound()
    {
        return TF2weapons.MOD_ID+":mob.engineer.hurt";
    }

    /**
     * Returns the sound this mob makes on death.
     */
    @Override
	protected String getDeathSound()
    {
        return TF2weapons.MOD_ID+":mob.engineer.death";
    }

    /**
     * Get this Entity's EnumCreatureAttribute
     */
    public int[] ammoTypesDropped(){
		return new int[]{1,3};
	}
    @Override
	protected void dropFewItems(boolean p_70628_1_, int p_70628_2_)
    {
    	super.dropFewItems(p_70628_1_, p_70628_2_);
    	this.entityDropItem(new ItemStack(TF2weapons.itemBuildingBox,1,18+this.rand.nextInt(3)*2+this.getEntTeam()), 0);
    }
    public int getMaxSpawnedInChunk()
    {
		return 2;
    }
    /*public void writeEntityToNBT(NBTTagCompound par1NBTTagCompound)
    {
        super.writeEntityToNBT(par1NBTTagCompound);
        if(this.sentry!=null&&this.sentry.isEntityAlive()){
        	NBTTagCompound sentryTag=new NBTTagCompound();
        	this.sentry.writeToNBTAtomically(sentryTag);
        	par1NBTTagCompound.setTag("Sentry", sentryTag);
        }
        if(this.dispenser!=null&&this.dispenser.isEntityAlive()){
        	NBTTagCompound dispenserTag=new NBTTagCompound();
        	this.dispenser.writeToNBTAtomically(dispenserTag);
        	par1NBTTagCompound.setTag("Dispenser", dispenserTag);
        }
        
    }
    public void readEntityFromNBT(NBTTagCompound par1NBTTagCompound)
    {
        super.readEntityFromNBT(par1NBTTagCompound);
        if(par1NBTTagCompound.hasKey("Sentry")){
        	//System.out.println(par1NBTTagCompound.getCompoundTag("Sentry"));
        	this.sentry=(EntitySentry) AnvilChunkLoader.readWorldEntityPos(par1NBTTagCompound.getCompoundTag("Sentry"), this.worldObj,this.posX,this.posY,this.posZ,true);
        	//this.worldObj.spawnEntityInWorld(sentry);
        }
        if(par1NBTTagCompound.hasKey("Dispenser")){
        	this.dispenser=(EntityDispenser) AnvilChunkLoader.readWorldEntityPos(par1NBTTagCompound.getCompoundTag("Dispenser"), this.worldObj,this.posX,this.posY,this.posZ,true);
        	//dispenser.readFromNBT(par1NBTTagCompound.getCompoundTag("Dispenser"));
        	//this.worldObj.spawnEntityInWorld(dispenser);
        }
    }*/
}
