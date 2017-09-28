package rafradek.TF2weapons.building;

import java.util.UUID;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IEntityOwnable;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.scoreboard.Team;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import rafradek.TF2weapons.ClientProxy;
import rafradek.TF2weapons.TF2weapons;
import rafradek.TF2weapons.weapons.ItemSapper;

public class EntityBuilding extends EntityCreature implements IEntityOwnable{
	
	public EntityLivingBase owner;
	public BuildingSound buildingSound;
	public ItemStack sapper;
	public EntityLivingBase sapperOwner;
	
	public EntityBuilding(World worldIn) {
		super(worldIn);
		this.applyTasks();
		//this.notifyDataManagerChange(LEVEL);
	}
	
	public EntityBuilding(World worldIn, EntityLivingBase owner) {
		this(worldIn);
		this.setOwner(owner);
		this.applyTasks();
	}
	public void applyTasks(){
		
	}
	@Override
	public boolean canBePushed()
    {
        return false;
    }
	public boolean isAIEnabled(){
		return true;
	}
	@Override
	public void applyEntityCollision(Entity entityIn)
    {
		if(entityIn.boundingBox.intersectsWith(this.getBoundingBox())){
			super.applyEntityCollision(entityIn);
		}
    }
	public void func_145781_i(int data){
		this.adjustSize();
		//System.out.println("Watcher update: "+data);
		if(this.worldObj.isRemote&&data==22){
			String sound=this.getSoundNameForState(this.getSoundState());
			if(sound!=null){
				//System.out.println("Playing Sound: "+sound);
				if(this.buildingSound!=null){
					this.buildingSound.stopPlaying();
				}
				this.buildingSound=new BuildingSound(this,new ResourceLocation(sound),this.getSoundState());
				ClientProxy.playBuildingSound(buildingSound);
			}
		}
	}
	@Override
	public boolean interact(EntityPlayer player)
    {
		if(!this.worldObj.isRemote&&player==this.getOwner()){
			this.grab();
			return true;
		}
		return false;
    }
	public void grab()
    {
		ItemStack stack=new ItemStack(TF2weapons.itemBuildingBox,1,(this instanceof EntitySentry?18:(this instanceof EntityDispenser?20:22))+this.getEntTeam());
		stack.setTagCompound(new NBTTagCompound());
		stack.getTagCompound().setTag("SavedEntity", new NBTTagCompound());
		this.writeEntityToNBT(stack.getTagCompound().getCompoundTag("SavedEntity"));
		this.entityDropItem(stack, 0);
		//System.out.println("Saved: "+stack.getTagCompound().getCompoundTag("SavedEntity"));
		this.setDead();
    }
	public void adjustSize(){
		
	}
	public String getSoundNameForState(int state){
		return state==50?TF2weapons.MOD_ID+":mob.sapper.idle":null;
	}
	@Override
	protected void applyEntityAttributes()
    {
        super.applyEntityAttributes();
        this.getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(16D);
        this.getEntityAttribute(SharedMonsterAttributes.knockbackResistance).setBaseValue(1D);
        this.getEntityAttribute(SharedMonsterAttributes.movementSpeed).setBaseValue(0D);
    }
	@Override
	public boolean canBreatheUnderwater()
    {
        return true;
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
		super.setFire(0);
	}
	@Override
	protected void entityInit()
	{
		super.entityInit();
		this.getDataWatcher().addObject(12, (byte)this.rand.nextInt(2));
		this.getDataWatcher().addObject(13, (byte)1);
		this.getDataWatcher().addObject(14, "");
		this.getDataWatcher().addObject(22, (byte)0);
		this.getDataWatcher().addObject(23, (short)0);
		this.getDataWatcher().addObject(24, (byte)0);
	}
	
	public int getSoundState(){
		return this.getDataWatcher().getWatchableObjectByte(22);
	}
	public void setSoundState(int state){
		this.getDataWatcher().updateObject(22, (byte)state);
	}
	public String getOwnerId() {
		// TODO Auto-generated method stub
		return this.getDataWatcher().getWatchableObjectString(14);
	}

	@Override
	public EntityLivingBase getOwner() {
		// TODO Auto-generated method stub
		if(this.owner!=null&&!(this.owner instanceof EntityPlayer && this.owner.isDead)){
			return this.owner;
		}
		else if(this.getOwnerId()!=null&&!this.getOwnerId().isEmpty()){
			return this.owner=this.worldObj.func_152378_a(UUID.fromString(this.getOwnerId()));
		}
		//System.out.println("owner: "+this.getOwnerId());
		return null;
	}

	public void setOwner(EntityLivingBase owner) {
		// TODO Auto-generated method stub
		this.owner=owner;
		if(owner instanceof EntityPlayer){
			this.getDataWatcher().updateObject(14, owner.getUniqueID().toString());
		}
	}
	@Override
	public void onUpdate(){
		this.motionX=0;
		this.motionZ=0;
		if(this.motionY>0){
			this.motionY=0;
		}
		if(!this.worldObj.isRemote&&this.isSapped()){
			TF2weapons.dealDamage(this, this.worldObj, this.sapperOwner, this.sapper, 0, this.sapper==null?0.14f:((ItemSapper)this.sapper.getItem()).getWeaponDamage(sapper,this.sapperOwner,this), TF2weapons.causeDirectDamage(this.sapper, this.sapperOwner, 0));
		}
		super.onUpdate();
	}
	public void setSapped(EntityLivingBase owner,ItemStack sapper){
		this.sapperOwner=owner;
		this.sapper=sapper;
		this.getDataWatcher().updateObject(24, (byte) 2);
		this.setSoundState(50);
	}
	public boolean isSapped(){
		return this.getDataWatcher().getWatchableObjectByte(24)>0;
	}
	public boolean isDisabled(){
		return this.isSapped() || this.getActivePotionEffect(TF2weapons.stun)!=null;
	}
	public void removeSapper(){
		this.getDataWatcher().updateObject(24, (byte)(this.getDataWatcher().getWatchableObjectByte(24)-1));
		if(!isSapped()){
			this.setSoundState(0);
			this.playSound(TF2weapons.MOD_ID+":mob.sapper.death", 0.7f, 1f);
			this.dropItem(Items.iron_ingot,1);
		}
	}
	@Override
	public AxisAlignedBB getCollisionBox(Entity entityIn)
    {
        return this.isEntityAlive()?entityIn.boundingBox:null;
    }
    public AxisAlignedBB getBoundingBox()
    {
    	if(!this.isEntityAlive())
    		return null;
    	/*else if(this.height>this.getCollHeight()){
	    	AxisAlignedBB colBox=this.boundingBox;
	    	colBox=colBox.expand((this.getCollWidth()-this.width)/2, (this.getCollHeight()-this.height)/2, (this.getCollWidth()-this.width)/2);
	    	colBox=colBox.offset(0, this.boundingBox.minY-colBox.minY, 0);
	    	return colBox;
    	}*/
        return this.boundingBox;
    }
	@Override
	public Team getTeam(){
    	return this.getOwner()!=null?this.getOwner().getTeam():(this.getEntTeam()==0?this.worldObj.getScoreboard().getTeam("RED"):this.worldObj.getScoreboard().getTeam("BLU"));
    }

	public int getProgress(){
		return this.getDataWatcher().getWatchableObjectShort(23);
	}
	
	public void setProgress(int progress){
		this.getDataWatcher().updateObject(23, (short) progress);
	}
	
	public int getLevel(){
		return this.getDataWatcher().getWatchableObjectByte(13);
	}
	
	public void setLevel(int level){
		this.getDataWatcher().updateObject(13, (byte) level);
	}
	
	public void upgrade(){
		this.setLevel(this.getLevel()+1);
		this.setProgress(0);
		this.getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(this.getEntityAttribute(SharedMonsterAttributes.maxHealth).getBaseValue()*1.2);
		this.setHealth(this.getMaxHealth());
		this.adjustSize();
	}
	
	public byte getEntTeam(){
		return this.getDataWatcher().getWatchableObjectByte(12);
	}
	
	public void setEntTeam(int team){
		this.getDataWatcher().updateObject(12,(byte) team);
	}
	@Override
	public boolean writeToNBTOptional(NBTTagCompound tagCompund){
		return this.getOwner() instanceof EntityPlayer?super.writeToNBTOptional(tagCompund):false;
	}
	@Override
	public void writeEntityToNBT(NBTTagCompound par1NBTTagCompound)
    {
        super.writeEntityToNBT(par1NBTTagCompound);
        
        par1NBTTagCompound.setByte("Team", this.getEntTeam());
        par1NBTTagCompound.setByte("Level", (byte) this.getLevel());
        par1NBTTagCompound.setShort("Progress", (byte) this.getProgress());
        par1NBTTagCompound.setString("Owner", this.getOwnerId()!=null?this.getOwnerId():"");
    }
	@Override
	public void readEntityFromNBT(NBTTagCompound par1NBTTagCompound)
    {
        super.readEntityFromNBT(par1NBTTagCompound);
        
        this.setEntTeam(par1NBTTagCompound.getByte("Team"));
        this.setLevel(par1NBTTagCompound.getByte("Level"));
        this.setProgress(par1NBTTagCompound.getByte("Progress"));
        if(par1NBTTagCompound.getByte("Sapper")!=0){
        	this.setSapped(this, null);
        }
        String ownerID=par1NBTTagCompound.getString("Owner");
        if(!ownerID.isEmpty()){
        	this.dataWatcher.updateObject(14, ownerID);
        	this.getOwner();
        }
    }
	public float getCollHeight(){
		return 1f;
	}
	public float getCollWidth(){
		return 0.95f;
	}
	public boolean canUseWrench(){
		return this.getMaxHealth()>this.getHealth()||this.getLevel()<3;
	}
	@Override
	protected void dropFewItems(boolean p_70628_1_, int p_70628_2_)
    {
    	for(int i=0;i<(this.getOwner() instanceof EntityPlayer && !(this instanceof EntityDispenser)?4:3);i++){
    		this.dropItem(Items.iron_ingot, 1);
    	}
    }
	@Override
	protected boolean canDespawn()
    {
        return this.getOwnerId() ==null;
    }

	@Override
	public String func_152113_b() {
		// TODO Auto-generated method stub
		return this.getDataWatcher().getWatchableObjectString(14);
	}
}
