package rafradek.TF2weapons.weapons;

import java.util.List;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import rafradek.TF2weapons.ClientProxy;
import rafradek.TF2weapons.IWeaponItem;
import rafradek.TF2weapons.ItemFromData;
import rafradek.TF2weapons.TF2Attribute;
import rafradek.TF2weapons.TF2weapons;
import rafradek.TF2weapons.WeaponData.PropertyType;
import rafradek.TF2weapons.characters.EntityTF2Character;
import rafradek.TF2weapons.message.TF2Message;
import rafradek.TF2weapons.message.TF2Message.PredictionMessage;

public abstract class ItemWeapon extends ItemUsable implements IWeaponItem
{
    /*public float damage;
    public float scatter;
    public int pellets;
    public float maxDamage;
    public int damageFalloff;
    public float minDamage;
    public int reload;
    public int clipSize;
    public boolean hasClip;
    public boolean clipType;
	public boolean randomCrits;
	public float criticalDamage;
	public int firstReload;
	public int knockback;*/
	public static boolean shouldSwing=false;
	public static int critical;
    public ItemWeapon()
    {
    	super();
        
    }
    
    @Override
	public void onUpdate(ItemStack par1ItemStack, World par2World, Entity par3Entity, int par4, boolean par5)
	{
    	super.onUpdate(par1ItemStack, par2World, par3Entity, par4, par5);
    	if(par5){
    		WeaponsCapability cap=WeaponsCapability.get(par3Entity);
			if(!par2World.isRemote&&cap.critTimeCool<=0){
				cap.critTimeCool=20;
				if(this.rapidFireCrits(par1ItemStack) && this.hasRandomCrits(par1ItemStack,par3Entity) && ((EntityLivingBase) par3Entity).getRNG().nextFloat()<=this.critChance(par1ItemStack, par3Entity)){
        			cap.critTime=40;
        			TF2weapons.network.sendToAllAround(new TF2Message.CapabilityMessage(par3Entity), TF2weapons.pointFromEntity(par3Entity));
        			//System.out.println("Apply crits rapid");
        		}
			}
			if(cap.critTime>0){
				cap.critTime-=1;
			}
			cap.critTimeCool-=1;
			/*if(par3Entity instanceof EntityTF2Character&&((EntityTF2Character) par3Entity).getAttackTarget()!=null){
				System.out.println(this.getWeaponSpreadBase(par1ItemStack, (EntityLivingBase) par3Entity));
				if(par1ItemStack.getTagCompound().getInteger("reload")<=100&&!((EntityTF2Character)par3Entity).attack.lookingAt(this.getWeaponSpreadBase(par1ItemStack, (EntityLivingBase) par3Entity)*100+1)){
					par1ItemStack.getTagCompound().setInteger("reload", 100);
				}
				//par1ItemStack.getTagCompound().setBoolean("WaitProper", true);
			}*/
    	}
	}
    
    @Override
    public boolean use(ItemStack stack, EntityLivingBase living, World world, PredictionMessage message)
    {
    	//boolean mainHand=living instanceof EntityPlayer&&living.getEntityData().getCompoundTag("TF2").getBoolean("mainhand");
        if (stack.getItemDamage() != stack.getMaxDamage())
            if (this.hasClip(stack))
            {
                stack.damageItem(1, living);
                if(!world.isRemote && living instanceof EntityPlayerMP)
                	TF2weapons.network.sendTo(new TF2Message.UseMessage(stack.getItemDamage(), false), (EntityPlayerMP) living);
            }
        /*if(living instanceof EntityPlayer && hand==EnumHand.MAIN_HAND){
        	((EntityPlayer)living).resetCooldown();
        }
        else if(world.isRemote&&Minecraft.getMinecraft().thePlayer==living){
        	Minecraft.getMinecraft().getItemRenderer().resetEquippedProgress(EnumHand.OFF_HAND);
        }*/
        int thisCritical=0;
        
        if((!this.rapidFireCrits(stack)&&this.hasRandomCrits(stack,living) && living.getRNG().nextFloat()<=this.critChance(stack, living))||WeaponsCapability.get(living).critTime>0)
            thisCritical=2;
        if(thisCritical==0&&living.getActivePotionEffect(TF2weapons.crit)!=null||living.getActivePotionEffect(TF2weapons.buffbanner)!=null)
        	thisCritical=1;
        
        critical=thisCritical;
        
        if(/*living instanceof EntityTF2Ch\aracter&&*/this.getAmmoType(stack)!=0/*&&((EntityTF2Character)living).getAmmo()>=0*/){
        	//
        	
        	if(living instanceof EntityTF2Character&&((EntityTF2Character)living).getAmmo()>=0){
        		((EntityTF2Character)living).useAmmo(1);
        	}
        	if(living instanceof EntityPlayer&&!((EntityPlayer)living).capabilities.isCreativeMode&&!this.hasClip(stack)){
        		ItemStack stackAmmo=ItemAmmo.searchForAmmo(living, stack);
        		if(stackAmmo!=null){
        			((ItemAmmo)stackAmmo.getItem()).consumeAmmo(living, stackAmmo, ((ItemWeapon)stack.getItem()).getActualAmmoUse(stack, living, 1));
        		}
        	}
    	}
        
        if(ItemFromData.getData(stack).hasProperty(PropertyType.FIRE_SOUND)){
        	String soundToPlay=ItemFromData.getData(stack).getString(PropertyType.FIRE_SOUND)+(thisCritical==2?".crit":"");
        	living.playSound(soundToPlay, 0.45f, 1f);
        	if(world.isRemote){
        		ClientProxy.removeReloadSound(living);
        	}
        }
        
        for (int x = 0; x < this.getWeaponPelletCount(stack,living); x++)
        {
        	//System.out.println("shoot");
        	/*EntityBullet bullet;
        	if(target==null){
        		bullet = new EntityBullet(world, living, this.scatter);
        	}
        	else{
        		bullet = new EntityBullet(world, living, target, this.scatter);
        	}
            bullet.stack = stack;
            bullet.setDamage(this.damage*damagemult);
            bullet.damageM = this.maxDamage;
            bullet.damageMM = this.damageFalloff;
            bullet.minDamage = this.minDamage;
            if(thisCritical)
            	bullet.critical = true;
            	bullet.setDamage(this.damage*3);
            }
            world.spawnEntityInWorld(bullet);*/
        	this.shoot(stack,living,world,thisCritical);
        }
        return true;
    }
    public abstract void shoot(ItemStack stack, EntityLivingBase living, World world, int thisCritical);
    @Override
    public boolean canFire(World world, EntityLivingBase living, ItemStack stack)
    {
    	/*boolean flag=true;
    	if(living instanceof EntityTF2Character&&((EntityTF2Character)living).getAmmo()<=0){
    		flag=false;
    	}*/
        return super.canFire(world, living, stack)&&(((this.hasClip(stack)||ItemAmmo.searchForAmmo(living, stack)!=null)&&(!this.hasClip(stack)||stack.getItemDamage()<stack.getMaxDamage()))
        				|| (living instanceof EntityPlayer && ((EntityPlayer)living).capabilities.isCreativeMode));
    }
    @Override
	public void addInformation(ItemStack par1ItemStack, EntityPlayer par2EntityPlayer, List par2List, boolean par4)
    {
    	super.addInformation(par1ItemStack, par2EntityPlayer, par2List, par4);
        /*if (par1ItemStack.hasTagCompound())
        {
            par2List.add("Firing: "+Integer.toString(par1ItemStack.getTagCompound().getShort("reload")));
            par2List.add("Reload: "+Integer.toString(par1ItemStack.getTagCompound().getShort("reloadd")));
            par2List.add("Crit: "+Integer.toString(par1ItemStack.getTagCompound().getShort("crittime")));
        }*/

    	if(this.hasClip(par1ItemStack)){
    		par2List.add("Clip: "+(this.getWeaponClipSize(par1ItemStack, par2EntityPlayer)-par1ItemStack.getItemDamage())+"/"+this.getWeaponClipSize(par1ItemStack, par2EntityPlayer));
    	}
    }
    
    public float critChance(ItemStack stack, Entity entity){
    	float chance=0.025f;
    	if(ItemUsable.lastDamage.containsKey(entity)){
			for(int i=0;i<20;i++){
				chance+=ItemUsable.lastDamage.get(entity)[i]/800;
			}
    	}
    	return Math.min(chance,0.125f);
    }
	@Override
	public boolean fireTick(ItemStack stack, EntityLivingBase living, World world) {
		// TODO Auto-generated method stub
		return false;
	}
	@Override
	@SideOnly(Side.CLIENT)
    public boolean isFull3D()
    {
        return true;
    }
	@Override
	public boolean altFireTick(ItemStack stack, EntityLivingBase living, World world) {
		// TODO Auto-generated method stub
		return false;
	}
	
	@Override
	public int getMaxDamage(ItemStack stack)
    {
		return stack.hasTagCompound()?this.getWeaponClipSize(stack,null):0;
    }
	
	@Override
	public float getWeaponDamage(ItemStack stack,EntityLivingBase living, Entity target){
		return TF2Attribute.getModifier("Damage", stack, ItemFromData.getData(stack).getFloat(PropertyType.DAMAGE),living);
	}
	
	public float getWeaponMaxDamage(ItemStack stack,EntityLivingBase living) {
		return ItemFromData.getData(stack).getFloat(PropertyType.MAX_DAMAGE);
	}
	
	public float getWeaponMinDamage(ItemStack stack,EntityLivingBase living){
		return ItemFromData.getData(stack).getFloat(PropertyType.MIN_DAMAGE);
	}
	
	public float getWeaponSpread(ItemStack stack,EntityLivingBase living){
		float base=this.getWeaponSpreadBase(stack, living);
		if(living instanceof EntityTF2Character&&((EntityTF2Character)living).getAttackTarget()!=null){
			float totalRotation=0;
			for(int i=0;i<20;i++){
				totalRotation+=((EntityTF2Character)living).lastRotation[i];
			}
			/*double speed=Math.sqrt((target.posX-shooter.targetPrevPos[1])*(target.posX-shooter.targetPrevPos[1])+(target.posY-shooter.targetPrevPos[3])
					*(target.posY-shooter.targetPrevPos[3])+(target.posZ-shooter.targetPrevPos[5])*(target.posZ-shooter.targetPrevPos[5]));*/
			base+=/*(speed+0.045)*/((EntityTF2Character)living).getMotionSensitivity()*totalRotation*0.01f;
			//System.out.println(target.motionX+" "+target.motionY+" "+target.motionZ+" "+(speed+0.045)*((EntityTF2Character)living).getMotionSensitivity());
			/*shooter.targetPrevPosX=target.posX;
			shooter.targetPrevPosY=target.posY;
			shooter.targetPrevPosZ=target.posZ;*/
		}
		return Math.abs(base);
	}
	public float getWeaponSpreadBase(ItemStack stack,EntityLivingBase living){
		return living!=null&&ItemFromData.getData(stack).getBoolean(PropertyType.SPREAD_RECOVERY)&&WeaponsCapability.get(living).lastFire<=0?0:TF2Attribute.getModifier("Spread", stack, ItemFromData.getData(stack).getFloat(PropertyType.SPREAD),living)/TF2Attribute.getModifier("Accuracy",stack,1,living);
	}
	public int getWeaponPelletCount(ItemStack stack,EntityLivingBase living){
		return (int) (TF2Attribute.getModifier("Pellet Count", stack, ItemFromData.getData(stack).getInt(PropertyType.PELLETS),living));
	}
	
	public float getWeaponDamageFalloff(ItemStack stack){
		return ItemFromData.getData(stack).getFloat(PropertyType.DAMAGE_FALOFF);
	}
	
	public int getWeaponReloadTime(ItemStack stack,EntityLivingBase living){
		return (int) (TF2Attribute.getModifier("Reload Time", stack, ItemFromData.getData(stack).getInt(PropertyType.RELOAD_TIME),living));
	}
	
	public int getWeaponFirstReloadTime(ItemStack stack,EntityLivingBase living){
		return (int) (TF2Attribute.getModifier("Reload Time", stack, ItemFromData.getData(stack).getInt(PropertyType.RELOAD_TIME_FIRST),living));
	}
	
	public boolean hasClip(ItemStack stack){
		//System.out.println("Clip:"+stack.getTagCompound());
		return ItemFromData.getData(stack).getBoolean(PropertyType.RELOADS_CLIP);
	}
	
	public int getWeaponClipSize(ItemStack stack,EntityLivingBase living){
		//System.out.println("With tag: "+stack.getTagCompound());
		return (int) (TF2Attribute.getModifier("Clip Size", stack, ItemFromData.getData(stack).getInt(PropertyType.CLIP_SIZE),living));
	}
	
	public boolean IsReloadingFullClip(ItemStack stack){
		return ItemFromData.getData(stack).getBoolean(PropertyType.RELOADS_FULL_CLIP);
	}
	
	public boolean hasRandomCrits(ItemStack stack,Entity par3Entity){
		return par3Entity instanceof EntityPlayer&&ItemFromData.getData(stack).getBoolean(PropertyType.RANDOM_CRITS);
	}

	public double getWeaponKnockback(ItemStack stack,EntityLivingBase living){
		return  TF2Attribute.getModifier("Knockback", stack, ItemFromData.getData(stack).getInt(PropertyType.KNOCKBACK),living);
	}
	public boolean rapidFireCrits(ItemStack stack){
		return ItemFromData.getData(stack).getBoolean(PropertyType.RAPIDFIRE_CRITS);
	}
	@Override
	public boolean onLeftClickEntity(ItemStack stack, EntityPlayer player, Entity entity)
    {
        return true;
    }
	@Override
	public boolean onEntitySwing(EntityLivingBase entityLiving, ItemStack stack)
    {
        return !shouldSwing;
    }
	@Override
	public void draw(WeaponsCapability cap,ItemStack stack, EntityLivingBase living, World world) {
		cap.reloadCool=0;
		cap.critTime=0;
		cap.state=cap.state&7;
		super.draw(cap,stack, living, world);
	}
	public boolean onHit(ItemStack stack,EntityLivingBase attacker, Entity target){
		return true;
	}
}
