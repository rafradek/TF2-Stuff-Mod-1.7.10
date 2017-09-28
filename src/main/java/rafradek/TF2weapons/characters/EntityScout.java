package rafradek.TF2weapons.characters;

import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import rafradek.TF2weapons.ItemFromData;
import rafradek.TF2weapons.TF2weapons;

public class EntityScout extends EntityTF2Character {
	public boolean doubleJumped;
	private int jumpDelay;
	public EntityScout(World par1World) {
		super(par1World);
		if(this.attack !=null){
			this.attack.setDodge(true);
			this.attack.jump=true;
			this.attack.jumprange=40;
			this.attack.dodgeSpeed=1.25f;
		}
		this.ammoLeft=24;
		this.experienceValue=15;
			
		//this.setItemStackToSlot(EntityEquipmentSlot.MAINHAND, ItemUsable.getNewStack("Minigun"));
		
	}
	/*protected void addWeapons()
    {
        this.setItemStackToSlot(EntityEquipmentSlot.MAINHAND, ItemFromData.getNewStack("scattergun"));
    }*/
	@Override
	protected void applyEntityAttributes()
    {
        super.applyEntityAttributes();
        this.getEntityAttribute(SharedMonsterAttributes.followRange).setBaseValue(40.0D);
        this.getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(15.0D);
        this.getEntityAttribute(SharedMonsterAttributes.knockbackResistance).setBaseValue(0.25D);
        this.getEntityAttribute(SharedMonsterAttributes.movementSpeed).setBaseValue(0.364D);
        this.getEntityAttribute(SharedMonsterAttributes.attackDamage).setBaseValue(3.0D);
    }
	
	@Override
	public void onLivingUpdate()
	{
        super.onLivingUpdate();
        if(jumpDelay>0&&--jumpDelay ==0){
        	this.jump();
        }
        if(this.onGround){
        	this.doubleJumped=false;
        }
        
    }
	@Override
	protected void jump()
    {
		super.jump();
		/*double speed=Math.sqrt(motionX*motionX+motionZ*motionZ);
		if(speed!=0){
			
			double speedMultiply=this.getEntityAttribute(SharedMonsterAttributes.movementSpeed).getAttributeValue()/speed;
			this.motionX*=speedMultiply;
			this.motionZ*=speedMultiply;
		}*/
		this.motionX = (-MathHelper.sin(this.rotationYaw / 180.0F * (float)Math.PI));
        this.motionZ = (MathHelper.cos(this.rotationYaw / 180.0F * (float)Math.PI));
        float f2 = (float) (MathHelper.sqrt_double(motionX * motionX + motionZ * motionZ)*this.getEntityAttribute(SharedMonsterAttributes.movementSpeed).getAttributeValue());
        this.motionX *= f2;
        this.motionZ *= f2;
        this.fallDistance = -3.0F;
		if(!this.doubleJumped&&this.jump){
			this.doubleJumped=true;
			this.jumpDelay=8;
		}
    }
	@Override
	protected String getLivingSound() {
	      return TF2weapons.MOD_ID+":mob.scout.say";
	   }

	   @Override
	protected String getHurtSound() {
	      return TF2weapons.MOD_ID+":mob.scout.hurt";
	   }

	   @Override
	protected String getDeathSound() {
	      return TF2weapons.MOD_ID+":mob.scout.death";
	   }
    
	public int[] ammoTypesDropped(){
		return new int[]{1,3};
	}
    /*public int getAttackStrength(Entity par1Entity)
    {
        ItemStack itemstack = this.getHeldItem();
        float f = (float)(this.getMaxHealth() - this.getHealth()) / (float)this.getMaxHealth();
        int i = 4 + MathHelper.floor_float(f * 4.0F);

        if (itemstack != null)
        {
            i += itemstack.getDamageVsEntity(this);
        }

        return i;
    }*/

    /**
     * Plays step sound at given x, y, z for the entity
     */
    @Override
	protected void dropFewItems(boolean p_70628_1_, int p_70628_2_)
    {
    	if(this.rand.nextBoolean()){
    		this.entityDropItem(new ItemStack(TF2weapons.itemAmmo,1,1), 0);
    	}
    	else{
    		this.entityDropItem(new ItemStack(TF2weapons.itemAmmo,1,1), 0);
    	}
    	if(this.rand.nextFloat()<0.60f+p_70628_2_*0.30f){
    		if(this.rand.nextBoolean()){
    			this.entityDropItem(ItemFromData.getNewStack("bonk"), 0);
    		}
    		else{
    			this.entityDropItem(ItemFromData.getNewStack("critcola"), 0);
    		}
    	}
    }

    @Override
	public float getMotionSensitivity(){
    	return 0;
    }
}
