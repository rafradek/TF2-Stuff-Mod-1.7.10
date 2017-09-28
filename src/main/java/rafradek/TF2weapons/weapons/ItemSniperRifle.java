package rafradek.TF2weapons.weapons;

import java.util.UUID;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import rafradek.TF2weapons.TF2Attribute;
import rafradek.TF2weapons.characters.EntitySniper;
import rafradek.TF2weapons.message.TF2Message.PredictionMessage;

public class ItemSniperRifle extends ItemBulletWeapon {
	public static UUID slowdownUUID=UUID.fromString("12843092-A5D6-BBCD-3D4F-A3DD4D8C65A9");
	public static AttributeModifier slowdown = new AttributeModifier(slowdownUUID, "sniper slowdown", -0.73D, 2);
	public float oldSensitive;
	@Override
	public boolean canAltFire(World worldObj, EntityLivingBase player,
			ItemStack item) {
		return super.canAltFire(worldObj, player, item)&&WeaponsCapability.get(player).fire1Cool <= 0;
	}
	@Override
	public boolean use(ItemStack stack, EntityLivingBase living, World world,PredictionMessage message){
		if(living instanceof EntityPlayer||stack.getTagCompound().getBoolean("WaitProper")){
			super.use(stack, living, world, message);
			this.disableZoom(stack, living);
			stack.getTagCompound().setBoolean("WaitProper", false);
			return true;
		}
		else{
			stack.getTagCompound().setBoolean("WaitProper", true);
			this.altUse(stack, living, world);
			WeaponsCapability.get(living).fire1Cool=2500;
		}
		return false;
	}
	@Override
	public void altUse(ItemStack stack, EntityLivingBase living,
			World world) {
		WeaponsCapability cap=WeaponsCapability.get(living);
		if(!cap.charging){
			cap.charging=true;
			if(world.isRemote&& living==Minecraft.getMinecraft().thePlayer&&this.oldSensitive==0){
				this.oldSensitive=Minecraft.getMinecraft().gameSettings.mouseSensitivity;
				Minecraft.getMinecraft().gameSettings.mouseSensitivity*=0.4f;
			}
			if(living.getEntityAttribute(SharedMonsterAttributes.movementSpeed).getModifier(slowdownUUID)==null)
				living.getEntityAttribute(SharedMonsterAttributes.movementSpeed).applyModifier(slowdown);
		}
		else{
			this.disableZoom(stack, living);
		}
		
	}
	public void disableZoom(ItemStack stack,EntityLivingBase living){
		WeaponsCapability cap=WeaponsCapability.get(living);
		if(living.worldObj.isRemote&& living==Minecraft.getMinecraft().thePlayer&&this.oldSensitive!=0&&cap.charging){
			Minecraft.getMinecraft().gameSettings.mouseSensitivity=this.oldSensitive;
			this.oldSensitive=0;
		}
		cap.chargeTicks=0;
		cap.charging=false;
		living.getEntityAttribute(SharedMonsterAttributes.movementSpeed).removeModifier(slowdown);
	}
	@Override
	public boolean canHeadshot(EntityLivingBase living,ItemStack stack) {
		// TODO Auto-generated method stub
		return WeaponsCapability.get(living).chargeTicks>4;
	}
	@Override
	public boolean showTracer(ItemStack stack){
		return false;
	}
	@Override
	public float getWeaponDamage(ItemStack stack,EntityLivingBase living, Entity target){
		return super.getWeaponDamage(stack, living, target)*(living!=null?this.getZoomBonus(stack,living):1);
	}
	
	@Override
	public float getWeaponMaxDamage(ItemStack stack,EntityLivingBase living) {
		return super.getWeaponMaxDamage(stack, living);
	}
	
	@Override
	public float getWeaponMinDamage(ItemStack stack,EntityLivingBase living){
		return super.getWeaponMinDamage(stack, living);
	}
	public float getZoomBonus(ItemStack stack,EntityLivingBase living){
		return 1+Math.max(0,(WeaponsCapability.get(living).chargeTicks-20)/((getChargeTime(stack,living)-20)/2));
	}
	public static float getChargeTime(ItemStack stack,EntityLivingBase living){
		return 66 / TF2Attribute.getModifier("Charge", stack, 1,living);
	}
	@Override
	public short getAltFiringSpeed(ItemStack item, EntityLivingBase player) {
		return 400;
	}
	@Override
	public void onUpdate(ItemStack par1ItemStack, World par2World, Entity par3Entity, int par4, boolean par5)
	{
		super.onUpdate(par1ItemStack, par2World, par3Entity, par4, par5);
		WeaponsCapability cap=WeaponsCapability.get(par3Entity);
		
		if(cap.charging&&par5){
			if(cap.chargeTicks<getChargeTime(par1ItemStack, (EntityLivingBase) par3Entity)){
				cap.chargeTicks+=1;
				//System.out.println("Charging: "+cap.chargeTicks);
			}
		}
		
		if(par3Entity instanceof EntitySniper&&((EntitySniper) par3Entity).getAttackTarget()!=null&&par1ItemStack.getTagCompound().getBoolean("WaitProper")){
			if(((EntitySniper) par3Entity).getHealth()<8&&cap.fire1Cool>250){
				WeaponsCapability.get(par3Entity).fire1Cool=250;
			}
			/*else if(par1ItemStack.getTagCompound().getInteger("reload")<=100&&!((EntitySniper)par3Entity).attack.lookingAt(1)){
				par1ItemStack.getTagCompound().setInteger("reload", 100);
			}*/
			//par1ItemStack.getTagCompound().setBoolean("WaitProper", true);
		}
	}
	/*public double getDiff(EntityTF2Character mob){
		 if(mob.getAttackTarget()!=null){
			mob.attack.lookingAt(mob.getAttackTarget(),2)
			double mX=mob.getAttackTarget().posX-mob.getAttackTarget().lastTickPosX;
			double mY=mob.getAttackTarget().posY-mob.getAttackTarget().lastTickPosY;
			double mZ=mob.getAttackTarget().posZ-mob.getAttackTarget().lastTickPosZ;
			double totalMotion=Math.sqrt(mX*mX+mY*mY+mZ*mZ);
			System.out.println("Odskok: "+totalMotion);
			return totalMotion;
		}
		 return 0;
	}*/
	@Override
	public void holster(WeaponsCapability cap,ItemStack stack, EntityLivingBase living, World world) {
		super.holster(cap, stack, living, world);
		this.disableZoom(stack,living);
	}
}
