package rafradek.TF2weapons.characters;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.common.base.Predicate;

import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.IMerchant;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIAttackOnCollide;
import net.minecraft.entity.ai.EntityAIHurtByTarget;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.ai.EntityAIWander;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.scoreboard.Team;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.village.MerchantRecipe;
import net.minecraft.village.MerchantRecipeList;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.EnumSkyBlock;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import rafradek.TF2weapons.ClientProxy;
import rafradek.TF2weapons.ItemFromData;
import rafradek.TF2weapons.TF2EventBusListener;
import rafradek.TF2weapons.TF2weapons;
import rafradek.TF2weapons.WeaponData.PropertyType;
import rafradek.TF2weapons.characters.ai.EntityAIFollowTrader;
import rafradek.TF2weapons.characters.ai.EntityAINearestChecked;
import rafradek.TF2weapons.characters.ai.EntityAISeek;
import rafradek.TF2weapons.characters.ai.EntityAIUseRangedWeapon;
import rafradek.TF2weapons.weapons.ItemAmmo;
import rafradek.TF2weapons.weapons.ItemUsable;
import rafradek.TF2weapons.weapons.ItemWeapon;
import rafradek.TF2weapons.weapons.WeaponsCapability;

public class EntityTF2Character extends EntityCreature implements IMob , IMerchant {
	
	public float[] lastRotation;
	public boolean jump;
	public boolean friendly;
	public boolean ranged;
	public EntityAIUseRangedWeapon attack;
	public EntityAINearestChecked findplayer =new EntityAINearestChecked(this, EntityLivingBase.class, true,false,this.getEntitySelector(), true);
	protected EntityAIAttackOnCollide attackMeele = new EntityAIAttackOnCollide(this, 1.1F, false);
	public EntityAIWander wander;
	public int ammoLeft;
	public boolean unlimitedAmmo;
	public boolean natural;
	private boolean noAmmo;
	public boolean alert;
	public static int nextEntTeam=-1;
	public EntityPlayer trader;
	public EntityPlayer lastTrader;
	public Map<EntityPlayer, Integer> tradeCount;
	public float rotation;
	public ItemStack[] loadout;
	//public int heldWeaponSlot;
	
	public int followTicks;
	public MerchantRecipeList tradeOffers;
	public double[] targetPrevPos=new double[9];
	public int traderFollowTicks;
	
	public EntityTF2Character(World p_i1738_1_) {
		super(p_i1738_1_);
		this.tasks.addTask(0, new EntityAISwimming(this));
		this.tasks.addTask(6, new EntityAIFollowTrader(this));
		this.tasks.addTask(6, wander=new EntityAIWander(this, 1.0D));
		this.tasks.addTask(7, new EntityAIWatchClosest(this, EntityTF2Character.class, 8.0F));
		this.tasks.addTask(7, new EntityAISeek(this));
		this.targetTasks.addTask(1, new EntityAIHurtByTarget(this, true));
		this.targetTasks.addTask(2, findplayer);
		//this.lookHelper=new 
		//this.motionSensitivity=4;
		this.rotation=17;
		this.lastRotation=new float[20];
		this.loadout=new ItemStack[5];
		//this.inventoryHandsDropChances[0]=0;
		if(p_i1738_1_ != null){
		//this.getHeldItem().getTagCompound().setTag("Attributes", (NBTTagCompound) ((ItemUsable)this.getHeldItem().getItem()).buildInAttributes.copy());
			
			this.attack =new EntityAIUseRangedWeapon(this, 1.0F, 20.0F);
			this.setCombatTask(true);
		}
		this.tradeCount=new HashMap<>();
		
		/*for (int i = 0; i < this.e.length; ++i)
        {
            this.equipmentDropChances[i] = 0.0F;
        }*/
		// TODO Auto-generated constructor stub
	}
	/*public EntityLookHelper getLookHelper()
    {
        return this.lookHelper;
    }*/
	public boolean isAIEnabled()
    {
        return true;
    }
	@Override
	public ItemStack getPickedResult(MovingObjectPosition target)
    {
		return new ItemStack(TF2weapons.itemPlacer,1,1);
    }
	protected void addWeapons()
    {
		String className=this.getClass().getSimpleName().substring(6).toLowerCase();
		//System.out.println("Class name: "+className);
		this.loadout[0]=ItemFromData.getRandomWeaponOfSlotMob(className, 0, this.rand, false);
		this.loadout[1]=ItemFromData.getRandomWeaponOfSlotMob(className, 1, this.rand, false);
    }
    /*public ItemStack getItemStackFromSlot(EntityEquipmentSlot slotIn){
    	if(slotIn==EntityEquipmentSlot.MAINHAND){
    		//System.out.println("Held item: "+this.loadout[this.heldWeaponSlot]);
    		return this.loadout[this.heldWeaponSlot];
    	}
    	else{
    		return super.getItemStackFromSlot(slotIn);
    	}
    }*/
    /*public void setItemStackToSlot(EntityEquipmentSlot slotIn, @Nullable ItemStack stack)
    {
    	if(slotIn==EntityEquipmentSlot.MAINHAND){
    		this.loadout[this.heldWeaponSlot]=stack;
    	}
    	else{
    		super.setItemStackToSlot(slotIn, stack);
    	}
    }*/
	@Override
	protected void entityInit()
	{
		super.entityInit();
		this.getDataWatcher().addObject(12, Byte.valueOf((byte)this.rand.nextInt(2)));
		this.getDataWatcher().addObject(13, Byte.valueOf((byte)0));
	}
	public int getEntTeam(){
		return this.getDataWatcher().getWatchableObjectByte(12);
	}
	public int getDiff(){
		return this.getDataWatcher().getWatchableObjectByte(13);
	}
	public void setEntTeam(int team){
		this.getDataWatcher().updateObject(12,(byte) team);
	}
	public void setDiff(int diff){
		this.getDataWatcher().updateObject(13,(byte) diff);
	}
	public int getAmmo() {
		// TODO Auto-generated method stub
		return ammoLeft;
	}
	@Override
	@SuppressWarnings("unchecked")
	public void setAttackTarget(EntityLivingBase target){
		
		if(!this.friendly && TF2weapons.isOnSameTeam(this,target)){
			return;
		}
		super.setAttackTarget(target);
		if(this.isTrading()){
			this.setCustomer(null);
		}
		if(!this.alert){
			for(EntityTF2Character ent:(List<EntityTF2Character>)this.worldObj.getEntitiesWithinAABB(EntityTF2Character.class, AxisAlignedBB.getBoundingBox(this.posX-15, this.posY-6, this.posZ-15, this.posX+15, this.posY+6, this.posZ+15))){
				if(TF2weapons.isOnSameTeam(this,ent)&&!TF2weapons.isOnSameTeam(this, target)&&(ent.getAttackTarget()==null||ent.getAttackTarget().isDead)){
					ent.alert=true;
					ent.setAttackTarget(target);
					ent.alert=false;
				}
			}
		}
	}
	public void useAmmo(int amount) {
		if(!this.unlimitedAmmo)
			this.ammoLeft -= amount;
		
	}
	public float getAttributeModifier(String attribute) {
		if(this.getAttackTarget() != null && this.getAttackTarget() instanceof EntityPlayer){
			if(attribute.equals("Knockback"))
				return this.getDiff()==1 ? 0.4f : (this.getDiff()==3 ? 0.75f : 0.55f);
			else if(attribute.equals("Fire Rate"))
				return this.getDiff()==1 ? 1.9f : (this.getDiff()==3 ? 1.2f : 1.55f);
			else if(attribute.equals("Spread")){
				float base=this.getDiff()==1 ? 1.9f : (this.getDiff()==3 ? 1.2f : 1.55f);
				return base;
			}
		}
		return 1f;
	}
	@Override
	public void onLivingUpdate()
    {
		
		super.onLivingUpdate();
		this.updateArmSwingProgress();
		if(this.getAttackTarget()!=null&&!this.getAttackTarget().isEntityAlive()){
			this.setAttackTarget(null);
		}
		if(!this.friendly&&this.getAttackTarget() instanceof EntityTF2Character&&TF2weapons.isOnSameTeam(this, this.getAttackTarget())){
			this.setAttackTarget(null);
		}
		if(this.jump&&this.onGround){
			this.jump();
		}
		if((WeaponsCapability.get(this).state&4)==0){
			WeaponsCapability.get(this).state+=4;
    	}
		if(!this.noAmmo&&this.getAttackTarget()!=null&&Math.abs(this.rotationYaw-this.rotationYawHead)>60/*TF2ActionHandler.playerAction.get().get(this)!=null&&(TF2ActionHandler.playerAction.get().get(this)&3)>0*/){
			if(this.rotationYawHead-this.rotationYaw>60){
				this.rotationYaw=this.rotationYawHead+60;
			}
			else{
				this.rotationYaw=this.rotationYawHead-60;
			}
		}
		if(!this.worldObj.isRemote){
			this.setDiff(this.worldObj.difficultySetting.getDifficultyId());
			if(this.isTrading()&&(this.trader.getDistanceSqToEntity(trader)>100||!this.isEntityAlive())){
				this.setCustomer(null);
			}
			if(this.ammoLeft<=0&&!this.noAmmo){
				this.setCombatTask(false);
				this.noAmmo=true;
			}
		}
		if(this.getHeldItem()!=null&&this.getHeldItem().getItem() instanceof ItemUsable){
			this.getHeldItem().getItem().onUpdate(getHeldItem(), worldObj, this, 0, true);
		}
		
		for(int i=19;i>0;i--){
			this.lastRotation[i]=this.lastRotation[i-1];
		}
		this.lastRotation[0]=(float) Math.sqrt((this.rotationYawHead-this.prevRotationYawHead)*(this.rotationYawHead-this.prevRotationYawHead)+
				(this.rotationPitch-this.prevRotationPitch)*(this.rotationPitch-this.prevRotationPitch));
    }
	@Override
	@SuppressWarnings("unchecked")
	public IEntityLivingData onSpawnWithEgg(IEntityLivingData p_110161_1_)
    {
		super.onSpawnWithEgg(p_110161_1_);
		if(p_110161_1_==null){
			p_110161_1_=new TF2CharacterAdditionalData();
			((TF2CharacterAdditionalData)p_110161_1_).natural=true;
			if(nextEntTeam>=0){
				((TF2CharacterAdditionalData)p_110161_1_).team=nextEntTeam;
				nextEntTeam=-1;
			}
			List<EntityTF2Character> list = this.worldObj.getEntitiesWithinAABB(EntityTF2Character.class, this.boundingBox.expand(40, 4.0D, 40));
			if(list.isEmpty()){
				((TF2CharacterAdditionalData)p_110161_1_).team=this.rand.nextInt(2);
			}
			else{
				((TF2CharacterAdditionalData)p_110161_1_).team=list.get(0).getEntTeam();
			}
		}
		if(p_110161_1_ instanceof TF2CharacterAdditionalData){
			this.natural=(((TF2CharacterAdditionalData)p_110161_1_).natural);
			this.setEntTeam(((TF2CharacterAdditionalData)p_110161_1_).team);
		}
		if(this.natural){
			
		}
		this.addWeapons();
		this.switchSlot(this.getDefaultSlot());
		return p_110161_1_;
    }
	public int getDefaultSlot() {
		// TODO Auto-generated method stub
		return 0;
	}
	public void setCombatTask(boolean ranged)
    {
		this.ranged=ranged; 
        this.tasks.removeTask(this.attack);
        this.tasks.removeTask(this.attackMeele);
        WeaponsCapability.get(this).state=0;
        //System.out.println(TF2ActionHandler.playerAction.get(this.worldObj.isRemote).size());

        if (ranged)
        {
            this.tasks.addTask(4, this.attack);
        }
        else
        {
            this.tasks.addTask(4, this.attackMeele);
        }
    }
	public Predicate<EntityLivingBase> getEntitySelector(){
		return new Predicate<EntityLivingBase>(){
			@Override
			public boolean apply(EntityLivingBase p_82704_1_)
	        {
				return ((p_82704_1_.getTeam()!=null)&&!TF2weapons.isOnSameTeam(EntityTF2Character.this, p_82704_1_))&&(!(p_82704_1_ instanceof EntityTF2Character) ||(!((EntityTF2Character)p_82704_1_).natural||!natural));
				
	        }
		};
	}
	@Override
	public void readEntityFromNBT(NBTTagCompound par1NBTTagCompound)
    {
        super.readEntityFromNBT(par1NBTTagCompound);

        this.ammoLeft=par1NBTTagCompound.getShort("Ammo");
        this.unlimitedAmmo=par1NBTTagCompound.getBoolean("UnlimitedAmmo");
        this.setEntTeam(par1NBTTagCompound.getByte("Team"));
        this.natural=par1NBTTagCompound.getBoolean("Natural");
        this.getEntityData().setBoolean("IsCloaked", par1NBTTagCompound.getBoolean("Cloaked"));
        this.getEntityData().setBoolean("IsDisguised", par1NBTTagCompound.getBoolean("Disguised"));
        this.getEntityData().setString("DisguiseType", par1NBTTagCompound.getString("DisguiseType"));
        if(this.getEntityData().getBoolean("IsDisguised")){
        	TF2EventBusListener.disguise(this, true);
        }
        NBTTagList list=(NBTTagList) par1NBTTagCompound.getTag("Loadout");
		
        for (int i = 0; i < list.tagCount(); ++i)
        {
            NBTTagCompound nbttagcompound = list.getCompoundTagAt(i);
            int j = nbttagcompound.getByte("Slot");
            this.loadout[j]= ItemStack.loadItemStackFromNBT(nbttagcompound);
        }
        if(par1NBTTagCompound.hasKey("Offers")){
        	this.tradeOffers=new MerchantRecipeList();
        	this.tradeOffers.readRecipiesFromTags(par1NBTTagCompound.getCompoundTag("Offers"));
        }
        	
    }
	@Override
	public boolean interact(EntityPlayer player)
    {
        if (!(player.getHeldItem() !=null && player.getHeldItem().getItem() instanceof ItemMonsterPlacerPlus)&& ( this.getAttackTarget() == null || this.friendly) &&this.isEntityAlive() && !this.isTrading() && !this.isChild() && !player.isSneaking())
        {
        	if (this.worldObj.isRemote && player.getTeam() == null && ((WeaponsCapability.get(this).state&1)==0 || this.friendly) && !player.capabilities.isCreativeMode){
        		ClientProxy.displayScreenConfirm("Choose a team to interact", "Visit the Mann Co. Store located in a village");
        	}
            if (!this.worldObj.isRemote && (TF2weapons.isOnSameTeam(this, player)||player.capabilities.isCreativeMode) &&(this.tradeOffers == null || !this.tradeOffers.isEmpty()))
            {
                this.setCustomer(player);
                player.displayGUIMerchant(this, this.getCommandSenderName());
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
	public void writeEntityToNBT(NBTTagCompound par1NBTTagCompound)
    {
        super.writeEntityToNBT(par1NBTTagCompound);
        par1NBTTagCompound.setBoolean("Cloaked", this.getEntityData().getBoolean("IsCloaked"));
        par1NBTTagCompound.setBoolean("Disguised", this.getEntityData().getBoolean("IsDisguised"));
        par1NBTTagCompound.setString("DisguiseType", this.getEntityData().getString("DisguiseType"));
        par1NBTTagCompound.setShort("Ammo", (short) this.ammoLeft);
        par1NBTTagCompound.setBoolean("UnlimitedAmmo", this.unlimitedAmmo);
        par1NBTTagCompound.setByte("Team", (byte) this.getEntTeam());
        par1NBTTagCompound.setBoolean("Natural", this.natural);
        NBTTagList list=new NBTTagList();
       
        for(int i=0;i<this.loadout.length;i++){
			ItemStack itemstack = this.loadout[i];

            if (itemstack != null)
            {
                NBTTagCompound nbttagcompound = new NBTTagCompound();
                nbttagcompound.setByte("Slot", (byte)i);
                itemstack.writeToNBT(nbttagcompound);
                list.appendTag(nbttagcompound);
            }
		}
        par1NBTTagCompound.setTag("Loadout", list);
        if(this.tradeOffers!=null)
        	par1NBTTagCompound.setTag("Offers",this.tradeOffers.getRecipiesAsTags());
    }
    @Override
	public boolean getCanSpawnHere()
    {
    	boolean validLight=this.isValidLightLevel();
    	Chunk chunk = this.worldObj.getChunkFromBlockCoords(MathHelper.floor_double(this.posX),MathHelper.floor_double(this.posZ));
    	boolean spawnDay=this.rand.nextInt(48)==0&&chunk.getRandomWithSeed(987234911L).nextInt(10)==0;

    	if(!spawnDay&&!validLight) return false;
    	int time=(int) Math.min((this.worldObj.getWorldInfo().getWorldTime()/24000),16);
    	
        if (this.rand.nextInt(16) < time)
        {
            return this.worldObj.difficultySetting != EnumDifficulty.PEACEFUL&&super.getCanSpawnHere();
        }
        return false;
    }
    /**
     * Called to update the entity's position/logic.
     */
    @Override
	public void onUpdate()
    {
        super.onUpdate();

        if (!this.worldObj.isRemote && this.worldObj.difficultySetting == EnumDifficulty.PEACEFUL)
        {
            this.setDead();
        }
    }
    @Override
	public Team getTeam(){
    	return this.getEntTeam()==0?this.worldObj.getScoreboard().getTeam("RED"):this.worldObj.getScoreboard().getTeam("BLU");
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
    /**
     * Called when the entity is attacked.
     */
    @Override
	public boolean attackEntityFrom(DamageSource p_70097_1_, float p_70097_2_)
    {
        if (this.isEntityInvulnerable())
        {
            return false;
        }
        else if (super.attackEntityFrom(p_70097_1_, p_70097_2_))
        {
            Entity entity = p_70097_1_.getEntity();

            if (this.riddenByEntity != entity && this.ridingEntity != entity)
            {
                if (entity != this)
                {
                    this.entityToAttack = entity;
                }

                return true;
            }
            else
            {
                return true;
            }
        }
        else
        {
            return false;
        }
    }
    
    /**
     * Returns the sound this mob makes when it is hurt.
     */
    @Override
	protected String getHurtSound()
    {
        return "game.hostile.hurt";
    }

    /**
     * Returns the sound this mob makes on death.
     */
    @Override
	protected String getDeathSound()
    {
        return "game.hostile.die";
    }

    @Override
	protected String func_146067_o(int p_146067_1_)
    {
        return p_146067_1_ > 4 ? "game.hostile.hurt.fall.big" : "game.hostile.hurt.fall.small";
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

    protected boolean isValidLightLevel()
    {
    	int i = MathHelper.floor_double(this.posX);
        int j = MathHelper.floor_double(this.boundingBox.minY);
        int k = MathHelper.floor_double(this.posZ);

        if (this.worldObj.getSavedLightValue(EnumSkyBlock.Sky, i, j, k) > this.rand.nextInt(32))
        {
            return false;
        }
        else
        {
            int i1 = this.worldObj.getBlockLightValue(i,j,k);

            if (this.worldObj.isThundering())
            {
                int j1 = this.worldObj.skylightSubtracted;
                this.worldObj.skylightSubtracted = 10;
                i1 = this.worldObj.getBlockLightValue(i,j,k);
                this.worldObj.skylightSubtracted = j1;
            }

            return i1 <= 4+this.rand.nextInt(4);
        }
    }

    /**
     * Checks if the entity's current position is a valid location to spawn this entity.
     */

    @Override
	protected void applyEntityAttributes()
    {
        super.applyEntityAttributes();
        this.getAttributeMap().registerAttribute(SharedMonsterAttributes.attackDamage);
    }

    protected boolean canDropLoot()
    {
        return true;
    }
    @Override
	public int getTalkInterval()
    {
        return 220;
    }
    @Override
	protected float getSoundPitch()
    {
        return this.isChild() ? (this.rand.nextFloat() - this.rand.nextFloat()) * 0.08F + 1.5F : (this.rand.nextFloat() - this.rand.nextFloat()) * 0.08F + 1.0F;
    }
    public float getMotionSensitivity(){
    	return this.getDiff()==1 ? 0.18f : (this.getDiff()==3 ? 0.07f : 0.11f);
    }
	public void onShot() {

	}
	@Override
	protected boolean canDespawn()
    {
        return this.natural;
    }
	/*@Nullable
    protected ResourceLocation getLootTable()
    {
        return TF2weapons.lootTF2Character;
    }*/
	@Override
	public void setCustomer(EntityPlayer player) {
		this.trader=player;
	}
	@Override
	public EntityPlayer getCustomer() {
		// TODO Auto-generated method stub
		return this.trader;
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
		int weaponCount=1+this.rand.nextInt(2);
		for(int i=0;i<weaponCount;i++){
			
			boolean buyItem=this.rand.nextBoolean();
			int slot=getValidSlots()[this.rand.nextInt(getValidSlots().length)];
			String className=this.getClass().getSimpleName().substring(6).toLowerCase();
			ItemStack item=ItemFromData.getRandomWeaponOfSlotMob(className,slot,this.getRNG(), false);
			ItemStack metal=new ItemStack(TF2weapons.itemTF2,Math.max(1,ItemFromData.getData(item).getInt(PropertyType.COST)/9),3);
			this.tradeOffers.add(new MerchantRecipe(buyItem?item:metal,null,buyItem?metal:item));
		}
		/*int hatCount=this.rand.nextInt(3);
		
		for(int i=0;i<hatCount;i++){
			
			boolean buyItem=this.rand.nextBoolean();
			ItemStack item=ItemFromData.getRandomWeaponOfClass("cosmetic", this.rand);
			int cost = Math.max(1,ItemFromData.getData(item).getInt(PropertyType.COST));
			ItemStack metal=new ItemStack(TF2weapons.itemTF2,cost/18,5);
			ItemStack metal2=new ItemStack(TF2weapons.itemTF2,this.rand.nextInt(3),4);
			if(metal2.stackSize==0)
				metal2=null;
			
			this.tradeOffers.add(new MerchantRecipe(buyItem?item:metal,buyItem?null:metal2,buyItem?metal:item));
		}*/
	}
	@Override
	public void setRecipes(MerchantRecipeList recipeList) {
		this.tradeOffers=recipeList;
		
	}
	public int[] getValidSlots(){
		return new int[]{0,1};
	}
	@Override
	public void useRecipe(MerchantRecipe recipe) {
		recipe.incrementToolUses();
		if(recipe.getItemToBuy().getItem() instanceof ItemWeapon){
			this.setCurrentItemOrArmor(0,recipe.getItemToBuy());
		}
		
        this.livingSoundTime = -this.getTalkInterval();
        this.playSound(TF2weapons.MOD_ID+":mob.villager.yes", this.getSoundVolume(), this.getSoundPitch());
        int i = 3 + this.rand.nextInt(4);

        this.lastTrader=this.trader;
        this.tradeCount.put(this.trader, this.tradeCount.containsKey(trader)?this.tradeCount.get(this.trader)+1:1);
        this.traderFollowTicks=Math.min(4800,this.tradeCount.get(this.trader)*250+350);
		
	}
	public void switchSlot(int slot){
    	this.setCurrentItemOrArmor(0,this.loadout[slot]);
    }

	public int[] ammoTypesDropped(){
		return new int[]{0,0};
	}
	
	@Override
	protected void dropEquipment(boolean wasRecentlyHit, int lootingModifier)
    {
		if(this.getAITarget() instanceof EntityPlayer&&this.getAITarget().getTeam()!=null){
			for(ItemStack stack:loadout){
				
				if( stack != null && stack.getItem() instanceof ItemFromData && this.rand.nextFloat() <= ItemFromData.getData(stack).getFloat(PropertyType.DROP_CHANCE)*(1+lootingModifier*0.4f)){
					//System.out.println(ItemFromData.getData(stack).getFloat(PropertyType.DROP_CHANCE)*(1+lootingModifier*0.4f));
					stack.stackSize=1;
					this.entityDropItem(stack, 0);
				}
			}
			if(this.rand.nextFloat()<=0.6+lootingModifier*0.2){
				this.entityDropItem(new ItemStack(TF2weapons.itemTF2,1,3), 0);
			}
			
		}
		int type=this.ammoTypesDropped()[this.rand.nextInt(2)];
		if(type != 0){
			if(type != 10 && type !=12){
				this.entityDropItem(new ItemStack(TF2weapons.itemAmmo,Math.round(ItemAmmo.AMMO_DROP[type]*(this.rand.nextFloat()+1)*(1+lootingModifier*0.5f)),type),0);
			}
			else if(type == 10){
				this.entityDropItem(new ItemStack(TF2weapons.itemAmmoFire,1,(int) ((0.3+this.rand.nextFloat()*0.4)*TF2weapons.itemAmmoFire.getMaxDamage())),0);
			}
			else if(type == 12){
				this.entityDropItem(new ItemStack(TF2weapons.itemAmmoMedigun,1,(int) ((0.3+this.rand.nextFloat()*0.4)*TF2weapons.itemAmmoMedigun.getMaxDamage())),0);
			}
		}
		super.dropEquipment(wasRecentlyHit, lootingModifier);
    }
	@Override
	public void func_110297_a_(ItemStack p_110297_1_) {
		// TODO Auto-generated method stub
		
	}
	
	/*@Override
	public void writeSpawnData(ByteBuf buffer) {
		PacketBuffer packet=new PacketBuffer(buffer);
		for(int i=0;i<this.loadout.length;i++){
			packet.writeByte(i);
			packet.writeItemStackToBuffer(this.loadout[i]);
		}
	}
	@Override
	public void readSpawnData(ByteBuf additionalData) {
		PacketBuffer packet=new PacketBuffer(additionalData);
		while(packet.readableBytes()>0){
			try {
				this.loadout[packet.readByte()]=packet.readItemStackFromBuffer();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}*/
}
