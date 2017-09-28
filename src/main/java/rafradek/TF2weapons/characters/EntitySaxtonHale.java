package rafradek.TF2weapons.characters;

import java.util.List;

import com.google.common.base.Predicate;

import net.minecraft.command.IEntitySelector;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IMerchant;
import net.minecraft.entity.INpc;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIAttackOnCollide;
import net.minecraft.entity.ai.EntityAIHurtByTarget;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.boss.IBossDisplayData;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.Potion;
import net.minecraft.scoreboard.Team;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;
import net.minecraft.village.MerchantRecipe;
import net.minecraft.village.MerchantRecipeList;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeHooks;
import rafradek.TF2weapons.ClientProxy;
import rafradek.TF2weapons.ItemFromData;
import rafradek.TF2weapons.TF2DamageSource;
import rafradek.TF2weapons.TF2weapons;
import rafradek.TF2weapons.WeaponData.PropertyType;
import rafradek.TF2weapons.characters.ai.EntityAINearestChecked;
import rafradek.TF2weapons.characters.ai.EntityAISeek;
import rafradek.TF2weapons.weapons.ItemKnife;

public class EntitySaxtonHale extends EntityCreature implements INpc, IMerchant, IBossDisplayData {

	public EntityPlayer trader;
	public MerchantRecipeList tradeOffers;
	public float rage;
	public boolean hostile;
	public boolean superJump;
	public int jumpCooldown;
	public boolean endangered;
	
	//private final BossInfoServer bossInfo = (BossInfoServer)(new BossInfoServer(this.getDisplayName(), BossInfo.Color.PURPLE, BossInfo.Overlay.PROGRESS));
	public EntitySaxtonHale(World worldIn) {
		super(worldIn);
		this.tasks.addTask(0, new EntityAISwimming(this));
		this.tasks.addTask(4, new EntityAIAttackOnCollide(this, 1.1F, false));
		this.tasks.addTask(7, new EntityAIWatchClosest(this, EntityTF2Character.class, 8.0F));
		this.tasks.addTask(7, new EntityAISeek(this));
		this.targetTasks.addTask(1, new EntityAIHurtByTarget(this, false));
		this.experienceValue=1500;
	}
	public boolean isAIEnabled()
    {
        return true;
    }
	@Override
	public void setCustomer(EntityPlayer player) {
		this.trader=player;
	}

	@Override
	public EntityPlayer getCustomer() {
		// TODO Auto-generated method stub
		return trader;
	}

	@Override
	public MerchantRecipeList getRecipes(EntityPlayer player) {
		// TODO Auto-generated method stub
		if(this.tradeOffers==null){
			makeOffers();
		}
		return tradeOffers;
	}
	public void makeOffers(){
		this.tradeOffers=new MerchantRecipeList();
		this.tradeOffers.add(new MerchantRecipe(new ItemStack(TF2weapons.itemTF2,5,2),null,new ItemStack(TF2weapons.itemTF2,1,7)));
		int weaponCount=9+this.rand.nextInt(2);
		for(int i=0;i<weaponCount;i++){
			ItemStack item=ItemFromData.getRandomWeapon(this.rand, ItemFromData.VISIBLE_WEAPON);
			int cost=ItemFromData.getData(item).getInt(PropertyType.COST);
			ItemStack ingot=new ItemStack(TF2weapons.itemTF2,cost/9,2);
			ItemStack nugget=new ItemStack(TF2weapons.itemTF2,cost%9,6);
			this.tradeOffers.add(new MerchantRecipe(ingot.stackSize>0?ingot:nugget,nugget.stackSize>0&&ingot.stackSize>0?nugget:null,item));
		}
		/*int hatCount=4+this.rand.nextInt(6);
		
		for(int i=0;i<hatCount;i++){
			
			ItemStack item=ItemFromData.getRandomWeaponOfClass("cosmetic", this.rand);
			int cost=ItemFromData.getData(item).getInt(PropertyType.COST);
			ItemStack ingot=new ItemStack(TF2weapons.itemTF2,cost/9,2);
			ItemStack nugget=new ItemStack(TF2weapons.itemTF2,cost%9,6);
			this.tradeOffers.add(new MerchantRecipe(ingot.stackSize>0?ingot:nugget,nugget.stackSize>0?nugget:null,item));
		}*/
	}

	@Override
	public boolean attackEntityFrom(DamageSource source, float amount)
    {
        if (this.isEntityInvulnerable())
        {
            return false;
        }
        else if (super.attackEntityFrom(source, amount))
        {
        	if(source == DamageSource.drown || source == DamageSource.lava){
        		this.superJump=true;
        		this.jump();
        	}
            this.rage+=amount/100f;
            if(source instanceof TF2DamageSource && ((TF2DamageSource)source).getCritical()==2 && ((TF2DamageSource)source).getWeapon() != null && ((TF2DamageSource)source).getWeapon().getItem() instanceof ItemKnife){
            	this.playSound(TF2weapons.MOD_ID+":mob.saxton.stab", 1F, 1f);
            }
 
            return true;
        }
        else
        {
            return false;
        }
    }
	public void setHostile(){
		this.targetTasks.addTask(2, new EntityAINearestChecked(this, EntityLivingBase.class, true, false, new Predicate<EntityLivingBase>(){

			@Override
			public boolean apply(EntityLivingBase input) {
				// TODO Auto-generated method stub
				return input instanceof EntityPlayer || input instanceof EntityTF2Character;
			}
			
		}, true));
		this.hostile=true;
	}
	@Override
	public void setRecipes(MerchantRecipeList recipeList) {
		this.tradeOffers=recipeList;
	}

	@Override
	public void useRecipe(MerchantRecipe recipe) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onLivingUpdate(){
		super.onLivingUpdate();
		if(!this.worldObj.isRemote){
			this.jumpCooldown--;
			
			if(this.getAttackTarget()==null){
				this.heal(0.35f);
			}
			
			//this.bossInfo.setPercent(this.getHealth()/this.getMaxHealth());
			//System.out.println("Has path: "+this.getNavigator().noPath());
			List<AxisAlignedBB> boxes=this.worldObj.getCollidingBoundingBoxes(this, boundingBox.expand(1, 0, 1));
			boolean obscuredView=false;
			Vec3 lookVec=this.getLookVec();
			for(AxisAlignedBB box:boxes){
				if(box.calculateIntercept(Vec3.createVectorHelper(this.posX, this.posY+this.getEyeHeight(), this.posZ), Vec3.createVectorHelper(this.posX, this.posY+this.getEyeHeight(), this.posZ).addVector(lookVec.xCoord,lookVec.yCoord,lookVec.zCoord))!=null){
					obscuredView=true;
					break;
				}
			}
			
			if(this.getAttackTarget()!=null&&this.getAttackTarget().isEntityAlive()&&obscuredView){
				this.superJump=true;
				this.jump();
			}
			if(this.rage>1){
				List<EntityLivingBase> list=this.worldObj.selectEntitiesWithinAABB(EntityLivingBase.class, this.boundingBox.expand(12, 12, 12), new IEntitySelector(){

					@Override
					public boolean isEntityApplicable(Entity input) {
						// TODO Auto-generated method stub
						return !(input instanceof EntitySaxtonHale) && !(input instanceof EntityPlayer && ((EntityPlayer)input).capabilities.isCreativeMode) && input.getDistanceSqToEntity(EntitySaxtonHale.this)<144;
					}
					
				}

					
				);
				if(!list.isEmpty()){
					this.rage=0;
					this.playSound(TF2weapons.MOD_ID+":mob.saxton.rage", 1F, 1F);
					for(EntityLivingBase living:list){
						TF2weapons.stun(living, 160, false);
					}
					this.superJump=true;
					this.jump();
				}
			}
		}
	}
	@Override
	public void setAttackTarget(EntityLivingBase living){
		super.setAttackTarget(living);
		if(!endangered){
			this.endangered=true;
			this.playSound(TF2weapons.MOD_ID+":mob.saxton.start", 0.9F, 1F);
		}
	}
	@Override
	protected String getSwimSound()
    {
        return "game.hostile.swim";
    }

    @Override
	protected String getSplashSound()
    {
        return "game.hostile.swim.splash";
    }
    @Override
	protected String getHurtSound()
    {
        return "game.hostile.hurt";
    }
    @Override
	protected String getDeathSound()
    {
        return TF2weapons.MOD_ID+":mob.saxton.death";
    }
    
	public boolean isNonBoss(){
		return !hostile;
	}
	@Override
	public Team getTeam(){
		return this.hostile?this.worldObj.getScoreboard().getTeam("TF2Bosses"):null;
	}
	@Override
	public void fall(float distance)
    {
		super.fall(Math.min(distance, 2.9f));
    }
	protected float getJumpUpwardsMotion()
    {
		if(superJump&&jumpCooldown<=0){
			return 2.7F;
		}
		return 0.7F;
    }

	public void jump()
    {
		if(superJump&&jumpCooldown<=0){
			this.playSound(TF2weapons.MOD_ID+":mob.saxton.jump", 0.85F, 1F);
			
		}
		
		if(this.onGround||this.jumpCooldown<=0){
			if(superJump&&jumpCooldown<=0){
				this.motionY = 2.7F;
			}
			else{
				this.motionY = 0.7D;
			}
	        
	
	        if (this.isPotionActive(Potion.jump))
	        {
	            this.motionY += (double)((float)(this.getActivePotionEffect(Potion.jump).getAmplifier() + 1) * 0.1F);
	        }
	
	        if (this.isSprinting())
	        {
	            float f = this.rotationYaw * 0.017453292F;
	            this.motionX -= (double)(MathHelper.sin(f) * 0.2F);
	            this.motionZ += (double)(MathHelper.cos(f) * 0.2F);
	        }
	        
	        this.isAirBorne = true;
	        ForgeHooks.onLivingJump(this);
		}
		if(superJump)
			this.superJump=false;

		this.jumpCooldown=20;
    }
	@Override
	protected void applyEntityAttributes()
    {
        super.applyEntityAttributes();
        this.getAttributeMap().registerAttribute(SharedMonsterAttributes.attackDamage);
        this.getEntityAttribute(SharedMonsterAttributes.followRange).setBaseValue(50.0D);
        this.getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(1000.0D);
        this.getEntityAttribute(SharedMonsterAttributes.knockbackResistance).setBaseValue(0.8D);
        this.getEntityAttribute(SharedMonsterAttributes.movementSpeed).setBaseValue(0.364D);
        this.getEntityAttribute(SharedMonsterAttributes.attackDamage).setBaseValue(20D);
    }
	@Override
	public boolean attackEntityAsMob(Entity p_70652_1_)
    {
        float f = (float)this.getEntityAttribute(SharedMonsterAttributes.attackDamage).getAttributeValue();
        int i = 0;

        if (p_70652_1_ instanceof EntityLivingBase)
        {
            f += EnchantmentHelper.getEnchantmentModifierLiving(this, (EntityLivingBase)p_70652_1_);
            i += EnchantmentHelper.getKnockbackModifier(this, (EntityLivingBase)p_70652_1_);
        }

        boolean flag = p_70652_1_.attackEntityFrom(DamageSource.causeMobDamage(this), f);

        if (flag)
        {
            if (i > 0)
            {
                p_70652_1_.addVelocity(-MathHelper.sin(this.rotationYaw * (float)Math.PI / 180.0F) * i * 0.5F, 0.1D, MathHelper.cos(this.rotationYaw * (float)Math.PI / 180.0F) * i * 0.5F);
                this.motionX *= 0.6D;
                this.motionZ *= 0.6D;
            }

            int j = EnchantmentHelper.getFireAspectModifier(this);

            if (j > 0)
            {
                p_70652_1_.setFire(j * 4);
            }

            if (p_70652_1_ instanceof EntityLivingBase)
            {
                EnchantmentHelper.func_151384_a((EntityLivingBase)p_70652_1_, this);
            }

            EnchantmentHelper.func_151385_b(this, p_70652_1_);
        }

        return flag;
    }
	@Override
	public void readEntityFromNBT(NBTTagCompound par1NBTTagCompound)
    {
		super.readEntityFromNBT(par1NBTTagCompound);
		if(par1NBTTagCompound.getBoolean("Hostile")){
			this.setHostile();
		}
		if(par1NBTTagCompound.hasKey("Offers")){
        	this.tradeOffers=new MerchantRecipeList();
        	this.tradeOffers.readRecipiesFromTags(par1NBTTagCompound.getCompoundTag("Offers"));
        }
		this.endangered=par1NBTTagCompound.getBoolean("Endangered");
    }
	@Override
	public void writeEntityToNBT(NBTTagCompound par1NBTTagCompound)
    {
		if(this.tradeOffers!=null)
        	par1NBTTagCompound.setTag("Offers",this.tradeOffers.getRecipiesAsTags());
		par1NBTTagCompound.setBoolean("Hostile", hostile);
		par1NBTTagCompound.setBoolean("Endangered",this.endangered);
    }
	@Override
	protected boolean canDespawn()
    {
        return false;
    }
	@Override
	public boolean interact(EntityPlayer player)
    {
        if (!(player.getHeldItem() !=null && player.getHeldItem().getItem() instanceof ItemMonsterPlacerPlus)&&this.getAttackTarget() == null &&this.isEntityAlive() && !this.isTrading() && !this.isChild() && !player.isSneaking())
        {
        	if (this.worldObj.isRemote && player.getTeam()==null && !player.capabilities.isCreativeMode){
        		ClientProxy.displayScreenJoinTeam();
        	}
        	else if (!this.worldObj.isRemote && (player.getTeam()!=null||player.capabilities.isCreativeMode) &&(this.tradeOffers == null || !this.tradeOffers.isEmpty()))
            {
                this.setCustomer(player);
                player.displayGUIMerchant(this,this.getCommandSenderName());
            }

            return true;
        }
        else
        {
            return super.interact(player);
        }
    }

	public boolean isTrading() {
		// TODO Auto-generated method stub
		return this.trader!=null;
	}

	@Override
	public void func_110297_a_(ItemStack p_110297_1_) {
		// TODO Auto-generated method stub
		
	}
}
