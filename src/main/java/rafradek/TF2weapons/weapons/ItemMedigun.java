package rafradek.TF2weapons.weapons;

import java.util.ArrayList;
import java.util.List;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;
import rafradek.TF2weapons.ClientProxy;
import rafradek.TF2weapons.ItemFromData;
import rafradek.TF2weapons.TF2Attribute;
import rafradek.TF2weapons.TF2EventBusListener;
import rafradek.TF2weapons.TF2weapons;
import rafradek.TF2weapons.WeaponData.PropertyType;
import rafradek.TF2weapons.building.EntityBuilding;
import rafradek.TF2weapons.message.TF2Message;
import rafradek.TF2weapons.message.TF2Message.PredictionMessage;

public class ItemMedigun extends ItemUsable {

	@Override
	public boolean use(ItemStack stack, EntityLivingBase living, World world,  PredictionMessage message) {
		//if(!world.isRemote||living != Minecraft.getMinecraft().thePlayer!(TF2weapons.medigunLock&&livingWeaponsCapability.get().healTarget>0)) return false;
        //System.out.println("View: "+var4+" "+startX+" "+startY+" "+startZ+" "+startX+endX+" "+endY+" "+endZ);
		if(world.isRemote&&living == Minecraft.getMinecraft().thePlayer){
			MovingObjectPosition trace=this.trace(stack, living, world);
	        if (world.getEntityByID(WeaponsCapability.get(living).healTarget)==null&&trace != null&&trace.entityHit != null&&trace.entityHit instanceof EntityLivingBase&&!(trace.entityHit instanceof EntityBuilding)){
	        	List<MovingObjectPosition> list=new ArrayList<MovingObjectPosition>();
	        	trace.hitInfo=new float[]{0,0};
	            list.add(trace);
	            message.target=list;
	            //System.out.println("healing: "+trace.entityHit.getEntityId());
		        //living.getCapability(TF2weapons.aaWEAPONS_CAP, null).healTarget=trace.entityHit.getEntityId();
	        	//TF2weapons.network.sendToServer(new TF2Message.CapabilityMessage(living));
		        
				//ClientProxy.playWeaponSound(living, ItemFromData.getSound(stack,PropertyType.HEAL_START_SOUND), false, 0, stack);
	        }
		}
		else if(!world.isRemote&&message!=null&&message.readData!=null){
			WeaponsCapability.get(living).healTarget=(int) message.readData.get(0)[0];
			TF2weapons.network.sendToAllAround(new TF2Message.CapabilityMessage(living), TF2weapons.pointFromEntity(living));
		}
		return true;
	}

	public void heal(ItemStack stack, EntityLivingBase living, World world, EntityLivingBase target){
		if(living instanceof EntityPlayer && !((EntityPlayer)living).capabilities.isCreativeMode){
			ItemStack stackAmmo=ItemAmmo.searchForAmmo(living, stack);
			if(stackAmmo!=null){
				((ItemAmmo)stackAmmo.getItem()).consumeAmmo(living, stackAmmo, this.getActualAmmoUse(stack, living, 1));
			}
		}
		
		int lastHitTime=target.ticksExisted-target.getEntityData().getInteger("lasthit");
    	float heal=this.getHealAmount(stack, living);
    	if(lastHitTime>200){
    		heal*=1+Math.min(2,(lastHitTime-200f)/50f);
    	}
    	float overheal=heal+target.getMaxHealth()*0.001666f;
    	float ubercharge=0.00125f;
    	if(target.getHealth()<target.getMaxHealth()){
    		overheal=(target.getHealth()+heal)-target.getMaxHealth()+0.04f;
    		target.heal(heal);
    	}
    	if(target.getHealth()>=target.getMaxHealth()&&target.getAbsorptionAmount()<target.getMaxHealth()*this.getMaxOverheal(stack, living)){
    		target.setAbsorptionAmount(Math.min(target.getAbsorptionAmount()+overheal,target.getMaxHealth()*this.getMaxOverheal(stack, living)));
    		target.getEntityData().setFloat("Overheal",target.getAbsorptionAmount()/*Math.max(target.getEntityData().getFloat("overhealamount")+overheal,target.getMaxHealth()*this.getMaxOverheal(stack, living))*/);
    		TF2weapons.network.sendToDimension(new TF2Message.PropertyMessage("Overheal", target.getAbsorptionAmount(),target),target.dimension);
    	}
    	if(target.getHealth()>=target.getMaxHealth()&&target.getAbsorptionAmount()>=target.getMaxHealth()*(this.getMaxOverheal(stack, living)-0.075)){
    		ubercharge/=2;
    	}
    	if(!stack.getTagCompound().getBoolean("Activated")&&stack.getTagCompound().getFloat("ubercharge")<1){
    		stack.getTagCompound().setFloat("ubercharge", Math.min(1,stack.getTagCompound().getFloat("ubercharge")+ubercharge));
    		if(stack.getTagCompound().getFloat("ubercharge")==1){
    			living.playSound(ItemFromData.getSound(stack,PropertyType.CHARGED_SOUND), 0.75f, 1);
    		}
    	}
	}
	public MovingObjectPosition trace(ItemStack stack, EntityLivingBase living, World world){
		double startX=0;
		double startY=0;
		double startZ=0;
		
		double endX=0;
		double endY=0;
		double endZ=0;
		
		//double[] rand=TF2weapons.radiusRandom3D(this.getWeaponSpread(stack,living), world.rand);
		
		startX=living.posX;// - (double)(MathHelper.cos(living.rotationYaw / 180.0F * (float)Math.PI) * 0.16F);
		startY=living.posY + living.getEyeHeight();
		startZ=living.posZ;// - (double)(MathHelper.sin(living.rotationYaw / 180.0F * (float)Math.PI) * 0.16F);
		
		//double[] rand=TF2weapons.radiusRandom2D(this.getWeaponSpread(stack), world.rand);
		
		//float spreadPitch = (float) (living.rotationPitch / 180 + rand[1]);
		//float spreadYaw = (float) (living.rotationYaw / 180 + rand[0]*(90/Math.max(90-Math.abs(spreadPitch*180),0.0001f)));
		//System.out.println("Rot: "+living.rotationYawHead+" "+living.rotationPitch);
		float spreadPitch = living.rotationPitch / 180;
		float spreadYaw = living.rotationYawHead / 180;
	
		endX=-MathHelper.sin(spreadYaw * (float)Math.PI) * MathHelper.cos(spreadPitch * (float)Math.PI);
		endY=(-MathHelper.sin(spreadPitch * (float)Math.PI));
		endZ=MathHelper.cos(spreadYaw * (float)Math.PI) * MathHelper.cos(spreadPitch * (float)Math.PI);
		
		float var9 = MathHelper.sqrt_double(endX * endX + endY * endY + endZ * endZ);
		//float[] ratioX= this.calculateRatioX(living.rotationYaw, living.rotationPitch);
		//float[] ratioY= this.calculateRatioY(living.rotationYaw, living.rotationPitch);
		//float wrapAngledYaw=MathHelper.wrapAngleTo180_float(living.rotationYaw);
		//float fixedYaw=Math.max(Math.abs(wrapAngledYaw),90)-Math.min(Math.abs(wrapAngledYaw),90);
		
		endX = (endX / var9) * 8 /*+ (rand[0]*ratioX[0])((fixedYaw/90)+(1-fixedYaw/90)*(-living.rotationPitch/90))*this.positive(wrapAngledYaw)*40*/;
		endY = (endY / var9) * 8 /*+ (rand[1]*ratioY[1])(0.5-Math.abs(spreadPitch))*80*40*/;
		endZ = (endZ / var9) * 8 /*+ ((ratioX[2]>ratioY[2]?rand[0]:rand[1])*(ratioX[2]+ratioY[2]))(rand[0]*ratioX[2] + rand[1]*ratioY[2])((1-fixedYaw/90)+(fixedYaw/90)*(-living.rotationPitch/90))*this.positive(wrapAngledYaw)*40*/;
		//System.out.println(startX+" "+startY+" "+startZ+" "+endX+" "+endY+" "+endZ);
		List<MovingObjectPosition> list=TF2weapons.pierce(world, living, startX, startY, startZ, startX+endX, startY+endY, startZ+endZ,false, 0.2f,false);
        return !list.isEmpty()?list.get(0):null;
		
	}
	@Override
	public void onUpdate(ItemStack par1ItemStack, World par2World, Entity par3Entity, int par4, boolean par5)
	{
    	super.onUpdate(par1ItemStack, par2World, par3Entity, par4, par5);
    	if(par5&&!this.canFire(par2World, (EntityLivingBase) par3Entity, par1ItemStack)){
    		WeaponsCapability.get(par3Entity).healTarget=-1;
    		if(!par2World.isRemote)
    			TF2weapons.network.sendToAllAround(new TF2Message.CapabilityMessage(par3Entity), TF2weapons.pointFromEntity(par3Entity));
    	}
    	if(par5&&!par2World.isRemote){
    		if(par1ItemStack.getTagCompound().getBoolean("Activated")){
    			par1ItemStack.getTagCompound().setFloat("ubercharge", Math.max(0,par1ItemStack.getTagCompound().getFloat("ubercharge")-0.00625f));
    			if(par1ItemStack.getTagCompound().getFloat("ubercharge")==0){
    				
    				par1ItemStack.getTagCompound().setBoolean("Activated", false);
    				par3Entity.playSound(ItemFromData.getSound(par1ItemStack, PropertyType.UBER_STOP_SOUND), 0.75f, 1);
    				par3Entity.getEntityData().setBoolean("Ubercharge",false);
    				TF2weapons.network.sendToAllAround(new TF2Message.PropertyMessage("Ubercharge", (byte)0,par3Entity),TF2weapons.pointFromEntity(par3Entity));
    			}
        	}
    		Entity healTarget=par2World.getEntityByID(WeaponsCapability.get(par3Entity).healTarget);
    		//System.out.println("healing: "+ItemUsable.itemProperties.server.get(par3Entity).getInteger("HealTarget"));
    		if(healTarget != null&&par3Entity.getDistanceSqToEntity(healTarget)>72){
    			WeaponsCapability.get(par3Entity).healTarget=-1;
    			TF2weapons.network.sendToAllAround(new TF2Message.CapabilityMessage(par3Entity), TF2weapons.pointFromEntity(par3Entity));
    			//TF2weapons.network.sendToDimension(new TF2Message.PropertyMessage("HealTarget", -1,par3Entity),par3Entity.dimension);
    		}
    		else if(healTarget != null&& healTarget instanceof EntityLivingBase){
    			this.heal(par1ItemStack, (EntityLivingBase)par3Entity, par2World, (EntityLivingBase)healTarget);
    			if(par1ItemStack.getTagCompound().getBoolean("Activated")&&!healTarget.getEntityData().getBoolean("Ubercharge")){
    				healTarget.getEntityData().setBoolean("Ubercharge",true);
    				TF2weapons.network.sendToAllAround(new TF2Message.PropertyMessage("Ubercharge", (byte)1,healTarget),TF2weapons.pointFromEntity(par3Entity));
    			}
    		}
    	}
    	
	}
	
	@Override
	public void holster(WeaponsCapability cap,ItemStack stack, EntityLivingBase living, World world) {
		cap.healTarget=-1;
		living.getEntityData().setBoolean("Ubercharge",false);
		super.draw(cap,stack, living, world);
	}

	@Override
	public boolean canFire(World world, EntityLivingBase living, ItemStack stack) {
		// TODO Auto-generated method stub
		return !(living instanceof EntityPlayer) || (((EntityPlayer)living).capabilities.isCreativeMode||ItemAmmo.searchForAmmo(living, stack)!=null);
	}

	@Override
	public boolean fireTick(ItemStack stack, EntityLivingBase living, World world) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean altFireTick(ItemStack stack, EntityLivingBase living, World world) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean onLeftClickEntity(ItemStack stack, EntityPlayer player, Entity entity)
    {
        return true;
    }
	@Override
	public boolean onEntitySwing(EntityLivingBase entityLiving, ItemStack stack)
    {
        return true;
    }
	public float getHealAmount(ItemStack stack,EntityLivingBase living){
		return TF2Attribute.getModifier("Heal", stack, ItemFromData.getData(stack).getFloat(PropertyType.HEAL),living);
	}
	public float getMaxOverheal(ItemStack stack,EntityLivingBase living){
		return TF2Attribute.getModifier("Overheal", stack, ItemFromData.getData(stack).getFloat(PropertyType.MAX_OVERHEAL),living);
	}
	@Override
	@SideOnly(Side.CLIENT)
    public boolean isFull3D()
    {
        return true;
    }
	@Override
	public void addInformation(ItemStack par1ItemStack, EntityPlayer par2EntityPlayer, List par2List, boolean par4)
    {
    	super.addInformation(par1ItemStack, par2EntityPlayer, par2List, par4);
       
        par2List.add("Charge: "+Float.toString(par1ItemStack.getTagCompound().getFloat("ubercharge")));
    }
	@Override
	public boolean startUse(ItemStack stack, EntityLivingBase living, World world, int oldState, int newState) {
		if(world.isRemote&&((newState&1)-(oldState&1))==1){
			MovingObjectPosition trace=this.trace(stack, living, world);
			if(trace == null || trace.entityHit == null || !(trace.entityHit instanceof EntityLivingBase)){
				ClientProxy.playWeaponSound(living, ItemFromData.getSound(stack,PropertyType.NO_TARGET_SOUND), false, 1, stack);
			}
			//System.out.println("Stop heal");
			if(WeaponsCapability.get(living).healTarget!=-1){
				WeaponsCapability.get(living).healTarget=-1;
				TF2weapons.network.sendToServer(new TF2Message.CapabilityMessage(living));
			}
			
		}
		if(!world.isRemote&&((newState&2)-(oldState&2))==2&&stack.getTagCompound().getFloat("ubercharge")==1f){
			stack.getTagCompound().setBoolean("Activated", true);
			living.playSound(ItemFromData.getSound(stack,PropertyType.UBER_START_SOUND), 0.75f, 1);
			living.getEntityData().setBoolean("Ubercharge",true);
			if(stack.getTagCompound().getBoolean("Strange")){
				stack.getTagCompound().setInteger("Ubercharges",stack.getTagCompound().getInteger("Ubercharges")+1);
				TF2EventBusListener.onStrangeUpdate(stack, living);
			}
			TF2weapons.network.sendToDimension(new TF2Message.PropertyMessage("Ubercharge", (byte)1,living),living.dimension);
		}
		return false;
	}
	@Override
	public boolean endUse(ItemStack stack, EntityLivingBase living, World world, int oldState, int newState) {
		if(world.isRemote&&!TF2weapons.medigunLock&&(oldState&1-newState&1)==1){
			WeaponsCapability.get(living).healTarget=-1;
			TF2weapons.network.sendToServer(new TF2Message.CapabilityMessage(living));
		}
		return false;
	}
}
