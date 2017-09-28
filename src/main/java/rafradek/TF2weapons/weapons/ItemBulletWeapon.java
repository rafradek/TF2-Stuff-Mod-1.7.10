package rafradek.TF2weapons.weapons;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import rafradek.TF2weapons.ClientProxy;
import rafradek.TF2weapons.TF2Attribute;
import rafradek.TF2weapons.TF2weapons;
import rafradek.TF2weapons.WeaponData.PropertyType;
import rafradek.TF2weapons.message.TF2Message.PredictionMessage;

public class ItemBulletWeapon extends ItemWeapon {
	public static double lastStartX=90;
	public static double lastStartY=90;
	public static double lastStartZ=90;
	public static double lastEndX=990;
	public static double lastEndY=900;
	public static double lastEndZ=900;
	public static HashMap<Entity, float[]> lastShot= new HashMap<Entity, float[]>();
	public static ArrayList<MovingObjectPosition> lastShotClient= new ArrayList<MovingObjectPosition>();
	public static boolean processShotServer;
	
	public void handleShoot(EntityLivingBase living, ItemStack stack,World world, HashMap<Entity, float[]> map,int critical){
		DamageSource var22=TF2weapons.causeDirectDamage(stack, living, critical);
		
		if(!(this instanceof ItemMeleeWeapon))
			var22.setProjectile();
		
    	Iterator<Entity> iterator=map.keySet().iterator();
        while(iterator.hasNext()){
        	Entity entity=iterator.next();
        	if(!((ItemWeapon)stack.getItem()).onHit(stack, living, entity)) continue;
        	float distance = (float) Vec3.createVectorHelper(living.posX, living.posY, living.posZ).distanceTo(Vec3.createVectorHelper(entity.posX, entity.posY, entity.posZ));
        	if(map.get(entity) != null && map.get(entity)[1] != 0&&TF2weapons.dealDamage(entity, world, living, stack, critical, map.get(entity)[1],var22)){
        		//System.out.println("Damage: "+map.get(entity)[1]);
            	distance=((ItemBulletWeapon) stack.getItem()).getMaxRange()/distance;
            	double distX=(living.posX-entity.posX)*distance;
            	double distY=(living.posY-entity.posY)*distance;
            	double distZ=(living.posZ-entity.posZ)*distance;
            	if(entity != null && stack != null && map.get(entity) != null){
            		double knockbackAmount=((ItemBulletWeapon) stack.getItem()).getWeaponKnockback(stack,living)*map.get(entity)[1]* 0.01625D / ((ItemBulletWeapon) stack.getItem()).getMaxRange();
	            	if (knockbackAmount > 0)
	                {
	                    if (distance > 0.0F)
	                    {
	                        entity.addVelocity(-(distX * knockbackAmount),-(distY * knockbackAmount), -(distZ * knockbackAmount));
	                        entity.isAirBorne=-(distY * knockbackAmount) > 0.01D;
	                    }
	                }
            	}
        	}
        }
        map.clear();

	}
	
	@Override
	public boolean use(ItemStack stack, EntityLivingBase living, World world, PredictionMessage message)
    {
		if(world.isRemote&&living==ClientProxy.getLocalPlayer()){
			lastShotClient.clear();
		}
		super.use(stack, living, world, message);
		if(world.isRemote&&living==ClientProxy.getLocalPlayer()){
			WeaponsCapability.get(ClientProxy.getLocalPlayer()).recoil+=getData(stack).getFloat(PropertyType.RECOIL);
			message.target=lastShotClient;
			return true;
		}
		else if(!world.isRemote){
			if(living instanceof EntityPlayer){
				//System.out.println("Shoot: "+message.readData);
				if(message.readData==null){
					return false;
				}
				int totalCrit=0;
				if((!this.rapidFireCrits(stack)&&this.hasRandomCrits(stack,living) && living.getRNG().nextFloat()<this.critChance(stack, living))||WeaponsCapability.get(living).critTime>0){
		            totalCrit=2;
		        }
				HashMap<Entity, float[]> shotInfo= new HashMap<Entity, float[]>();
				for(Object[] obj:message.readData){
					Entity target=world.getEntityByID((Integer) obj[0]);
					if(target==null) continue;
					
					if(!shotInfo.containsKey(target)||shotInfo.get(target)==null){
			    		shotInfo.put(target, new float[3]);
			    	}
					int critical=totalCrit;
			    	//System.out.println(var4.hitInfo);
			    	if((Boolean)obj[1]){
			    		critical=2;
			    	}
			    	critical=this.setCritical(stack, living, target, critical);
			    	if(critical>0){
			    		totalCrit=critical;
			    	}
			    	//ItemRangedWeapon.critical=critical;
			    	float[] values=shotInfo.get(target);
			    	//System.out.println(obj[2]+" "+critical);
			    	values[0]++;
			    	values[1]+=TF2weapons.calculateDamage(target,world, living, stack, critical, (Float) obj[2]);
				}
				//livingWeaponsCapability.get().predictionList.add(message);
				handleShoot(living, stack, world, shotInfo,totalCrit);
			}
			else {
				handleShoot(living, stack, world, lastShot,critical);
				lastShot.clear();
			}
		}
		return true;
		//if(world.isRemote) return false;
		/*if(((!world.isRemote && (processShotServer||!(living instanceof EntityPlayer)))||(world.isRemote&&living instanceof EntityPlayer)) && super.use(stack, living, world, hand)){
			//System.out.println(world.isRemote+" "+stack.getTagCompound().getShort("reload")+" "+TF2ActionHandler.playerAction.get(world.isRemote).get(living));

            if(!world.isRemote && living != null&& !processShotServer)
            {
            	handleShoot(living, stack, world, lastShot,critical);
            }
            else if(world.isRemote&&living==Minecraft.getMinecraft().thePlayer){
            	
            	//TF2weapons.network.sendToServer(new BulletMessage(Minecraft.getMinecraft().thePlayer.inventory.currentItem,lastShotClient, hand));
            	//lastShotClient.clear();
            }
            
            return ;
		}*/
    }
	public boolean showTracer(ItemStack stack){
		return true;
	}
	@Override
	public void shoot(ItemStack stack, EntityLivingBase living, World world, int critical) {
		if(!world.isRemote && living instanceof EntityPlayer) return;
		double startX=0;
		double startY=0;
		double startZ=0;
		
		double endX=0;
		double endY=0;
		double endZ=0;
		
		double[] rand=TF2weapons.radiusRandom3D(this.getWeaponSpread(stack,living), world.rand);
		
		//if(target==null){
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
			
			endX = (endX / var9 + rand[0]) * getMaxRange() /*+ (rand[0]*ratioX[0])((fixedYaw/90)+(1-fixedYaw/90)*(-living.rotationPitch/90))*this.positive(wrapAngledYaw)*40*/;
			endY = (endY / var9 + rand[1]) * getMaxRange() /*+ (rand[1]*ratioY[1])(0.5-Math.abs(spreadPitch))*80*40*/;
			endZ = (endZ / var9 + rand[2]) * getMaxRange() /*+ ((ratioX[2]>ratioY[2]?rand[0]:rand[1])*(ratioX[2]+ratioY[2]))(rand[0]*ratioX[2] + rand[1]*ratioY[2])((1-fixedYaw/90)+(fixedYaw/90)*(-living.rotationPitch/90))*this.positive(wrapAngledYaw)*40*/;
			double distanceMax=getMaxRange()/Math.sqrt(endX*endX+endY*endY+endZ*endZ);
			//System.out.println(ratioX[0]+" "+ratioX[1]+" "+ratioX[2]+" "+ratioY[0]+" "+ratioY[1]+" "+ratioY[2]);
			
			endX *= distanceMax;
			endY *= distanceMax;
			endZ *= distanceMax;
			endX += startX;
			endY += startY;
			endZ += startZ;
			
			
			
		/*} else {
			startY = living.posY + (double)living.getEyeHeight() - 0.10000000149011612D;
			endX = target.posX - living.posX;
	        double var8 = target.posY + (double)target.getEyeHeight() - 0.699999988079071D - startY;
	        endZ = target.posZ - living.posZ;
	        double var12 = (double)MathHelper.sqrt_double(endX * endX + endZ * endZ);

	        if (var12 >= 1.0E-7D)
	        {
	            float var14 = (float)(Math.atan2(endZ, endX) * 180.0D / Math.PI) - 90.0F;
	            float var15 = (float)(-(Math.atan2(var8, var12) * 180.0D / Math.PI));
	            double var16 = endX / var12;
	            double var18 = endZ / var12;
	            startX=living.posX + var16;
	            startZ=living.posZ + var18;
	            float var20 = (float)var12 * 0.2F;
	            
	            endY=var8 + (double)var20;
	            
	            float var9 = MathHelper.sqrt_double(endX * endX + endY * endY + endZ * endZ);
	            endX = (endX / (double)var9 + rand[0]) * getMaxRange();
				endY = (endY / (double)var9 + rand[1]) * getMaxRange();
				endZ = (endZ / (double)var9 + rand[2]) * getMaxRange();
				
				double distance=getMaxRange()/Math.sqrt(Math.pow(endX, 2)+Math.pow(endY, 2)+Math.pow(endZ,2));
				
				endX *= distance;
				endY *= distance;
				endZ *= distance;
				endX += startX;
				endY += startY;
				endZ += startZ;
	        }
		}*/
		if(world.isRemote){
			if(this.showTracer(stack)){
			float mult=1;
			ClientProxy.spawnBulletParticle(world,living,startX-MathHelper.cos(living.rotationYaw / 180.0F * (float)Math.PI) * 0.16F * mult, startY, 
					startZ- MathHelper.sin(living.rotationYaw / 180.0F * (float)Math.PI) * 0.16F * mult, endX, endY, endZ, 20,critical);
			}
			if(living !=Minecraft.getMinecraft().thePlayer) return;
		}
		//System.out.println(startX+" "+startY+" "+startZ+" "+endX+" "+endY+" "+endZ);
        List<MovingObjectPosition> list=TF2weapons.pierce(world, living, startX, startY, startZ, endX, endY, endZ,this.canHeadshot(living,stack), this.getBulletSize(stack,living),this.canPenetrate(stack,living));
        for(MovingObjectPosition var4:list){	
            if (var4.entityHit != null)
            {
            	float distance = 0;
                if (living != null)
                {
                    distance = (float) Vec3.createVectorHelper(living.posX, living.posY, living.posZ).distanceTo(Vec3.createVectorHelper(var4.entityHit.posX, var4.entityHit.posY, var4.entityHit.posZ));
                    distance-=living.width/2 + var4.entityHit.width/2;
                    
                    if(distance<0)
                    	distance=0;
                }
                if(!world.isRemote){
	            	if(!lastShot.containsKey(var4.entityHit)||lastShot.get(var4.entityHit)==null){
	            		lastShot.put(var4.entityHit, new float[3]);
	            	}
	            	//System.out.println(var4.hitInfo);
	            	if(var4.hitInfo!=null){
	            		critical=2;
	            		ItemWeapon.critical=2;
	            	}
	            	critical=this.setCritical(stack, living, var4.entityHit, critical);
	            	ItemWeapon.critical=critical;
	            	float[] values=lastShot.get(var4.entityHit);
	            	values[0]++;
	            	values[1]+=TF2weapons.calculateDamage(var4.entityHit,world, living, stack, critical, distance);
	            	//values[2]=distance;
                }
                else{
                	//System.out.println(var4.hitInfo);
    	            var4.hitInfo=new float[]{var4.hitInfo!=null?1:0,distance};
    	            lastShotClient.add(var4);
                }
            }
            
            
        }
	}
	public boolean canPenetrate(ItemStack stack,EntityLivingBase shooter) {
		// TODO Auto-generated method stub
		return TF2Attribute.getModifier("Penetration", stack, 0, shooter)>0;
	}

	/*public boolean checkHeadshot(World world, Entity living,
			ItemStack stack, Vec3 hitVec) {
		double ymax=living.boundingBox.maxY;
		AxisAlignedBB head=AxisAlignedBB.getBoundingBox(living.posX-0.21, ymax-0.21, living.posZ-0.21,living.posX+0.21, ymax+0.21, living.posZ+0.21);
		System.out.println("Trafienie: "+Math.abs(ymax-hitVec.yCoord));
		
		return Math.abs(ymax-hitVec.yCoord)<0.205;
	}*/
	public boolean canHeadshot(EntityLivingBase living,ItemStack stack) {
		// TODO Auto-generated method stub
		return false;
	}
	public int positive(float value){
		if(value>0)
			return 1;
		return -1;
	}
	public float[] calculateRatioX(float yaw,float pitch){
		float[] result=new float[3];
		float angledYaw=Math.abs(MathHelper.wrapAngleTo180_float(yaw));
		float distanceYaw=Math.max(angledYaw,90)-Math.min(angledYaw,90);
		result[0]=(distanceYaw/90)+(1-distanceYaw/90)*(-pitch/90);
		result[2]=(1-distanceYaw/90);//+(1-distanceYaw/90)*(-pitch/90);
		result[1]=0;
		return result;
	}
	public float[] calculateRatioY(float yaw,float pitch){
		float[] result=new float[3];
		float angledYaw=Math.abs(MathHelper.wrapAngleTo180_float(yaw));
		float distanceYaw=Math.max(angledYaw,90)-Math.min(angledYaw,90);
		result[0]=0;
		result[2]=(distanceYaw/90)*(-pitch/90);
		result[1]=1-Math.abs(pitch)/90;
		return result;
	}
	
	public float getMaxRange(){
		return 256;
	}
	public float getBulletSize(ItemStack stack,EntityLivingBase living){
		return 0.04f;
	}
	public int setCritical(ItemStack stack,EntityLivingBase shooter, Entity target, int old){
		return TF2weapons.calculateCrits(target, shooter, old);
	}
}
