package rafradek.TF2weapons.weapons;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.common.IExtendedEntityProperties;
import rafradek.TF2weapons.ClientProxy;
import rafradek.TF2weapons.TF2EventBusListener;
import rafradek.TF2weapons.TF2weapons;
import rafradek.TF2weapons.characters.EntityTF2Character;
import rafradek.TF2weapons.message.TF2Message;

public class WeaponsCapability implements IExtendedEntityProperties{
	public static final String ID="TF2weapons";
	public EntityLivingBase owner;
	public int state;
	public int minigunTicks;
	public int fire1Cool;
	public int fire2Cool;
	public int reloadCool;
	public int lastFire;
	public int critTime;
	public int healTarget=-1;
	public HashMap<String,Integer> effectsCool=new HashMap<String, Integer>();
	public int chargeTicks;
	public boolean charging;
	public int critTimeCool;
	public ArrayList<TF2Message.PredictionMessage> predictionList=new ArrayList<TF2Message.PredictionMessage>();
	public float recoil;
	public int invisTicks;
	public int disguiseTicks;
	public int buffDuration;
	public boolean pressedStart;
	public int active;
	
	public WeaponsCapability(EntityLivingBase entity) {
		this.owner=entity;
	}
	public static WeaponsCapability get(Entity ent){
		return (WeaponsCapability)ent.getExtendedProperties(ID);
	}
	public void tick(){
		
		Iterator<Entry<String, Integer>> iterator=effectsCool.entrySet().iterator();
		while(iterator.hasNext()){
			Entry<String,Integer> entry=iterator.next();
			entry.setValue(entry.getValue()-1);
			if(entry.getValue()<=0){
				iterator.remove();
			}
		}

		ItemStack stack = owner.getHeldItem();
		/*if(itemProperties.get(client).get(player)==null){
			itemProperties.get(client).put(player, new NBTTagCompound());
		}*/
		//player.getEntityData().setTag("TF2", tag);
		
		this.lastFire-=50;
		if (stack != null&&stack.getItem() instanceof ItemUsable)
        {
			ItemUsable item=(ItemUsable) stack.getItem();
			
			//if(!client)
				//System.out.println(client+" rel " +item.getTagCompound().getShort("reload")+" "+state+" "+item.getDisplayName());
			if (this.reloadCool > 0)
            {
                this.reloadCool-=50;
            }
			if (this.fire1Cool > 0)
            {
				this.fire1Cool-=50;
            }
			if (this.fire2Cool > 0)
            {
				this.fire2Cool-=50;
            }
			
			if(owner.worldObj.isRemote){
				//((EntityPlayerMP)player).isChangingQuantityOnly=state>0;
				stack.animationsToGo=0;
			}
			if(owner instanceof EntityTF2Character){
				EntityTF2Character shooter=((EntityTF2Character)owner);
				if(shooter.getAttackTarget() != null){
					shooter.targetPrevPos[1]=shooter.targetPrevPos[0];
					shooter.targetPrevPos[3]=shooter.targetPrevPos[2];
					shooter.targetPrevPos[5]=shooter.targetPrevPos[4];
					shooter.targetPrevPos[0]=shooter.getAttackTarget().posX;
					shooter.targetPrevPos[2]=shooter.getAttackTarget().posY;
					shooter.targetPrevPos[4]=shooter.getAttackTarget().posZ;
				}
			}
			this.stateDo(owner, stack);
			if((!owner.worldObj.isRemote||owner!=Minecraft.getMinecraft().thePlayer)&&stack.getItem() instanceof ItemWeapon && ((ItemWeapon)stack.getItem()).hasClip(stack)&&
					(!(owner instanceof EntityPlayer)||ItemAmmo.searchForAmmo(owner, stack)!=null||owner.worldObj.isRemote)){
				if(((state&4)!=0||stack.getItemDamage()==stack.getMaxDamage())&&(state&8)==0&&stack.getItemDamage()!=0&& this.reloadCool<=0
						&&(this.fire1Cool<=0||((ItemWeapon)stack.getItem()).IsReloadingFullClip(stack))&&owner.getActivePotionEffect(TF2weapons.stun)==null){
					state+=8;
					this.reloadCool=((ItemWeapon)stack.getItem()).getWeaponFirstReloadTime(stack,owner);
					
					if(!owner.worldObj.isRemote&&owner instanceof EntityPlayerMP)
	                	TF2weapons.network.sendTo(new TF2Message.UseMessage(stack.getItemDamage(), true), (EntityPlayerMP) owner);
					
					if(owner.worldObj.isRemote&&((ItemWeapon)stack.getItem()).IsReloadingFullClip(stack)){
						TF2weapons.proxy.playReloadSound(owner,stack);
	                }
					
				}
				else if(this.fire1Cool<=0||((ItemWeapon)stack.getItem()).IsReloadingFullClip(stack)){
	                while ((state&8)!=0&&this.reloadCool <= 0 && stack.getItemDamage()!=0)
	                {
	                	//System.out.println("On client: "+owner.worldObj.isRemote);
	                	int maxAmmoUse=ItemAmmo.getAmmoAmount(owner, stack);
	                	int consumeAmount=0;
	                	
	                    if(((ItemWeapon)stack.getItem()).IsReloadingFullClip(stack)){
	                    	consumeAmount+=Math.min(stack.getItemDamage(),maxAmmoUse);
	                    	stack.setItemDamage(Math.max(0, stack.getItemDamage()-consumeAmount));
	                    	maxAmmoUse-=consumeAmount;
	                    	
	                    } else {
	                    	consumeAmount=1;
	                    	stack.setItemDamage(stack.getItemDamage() - 1); 
	                    	TF2weapons.proxy.playReloadSound(owner,stack);
	                    }
	                    ItemAmmo.consumeAmmoGlobal(owner, stack, consumeAmount);
	                    if(!owner.worldObj.isRemote&&owner instanceof EntityPlayerMP)
	                    	TF2weapons.network.sendTo(new TF2Message.UseMessage(stack.getItemDamage(), true), (EntityPlayerMP) owner);
	                    
	                    this.reloadCool+=((ItemWeapon)stack.getItem()).getWeaponReloadTime(stack,owner);
	                    
	                    if(stack.getItemDamage()==0){
	                    	state-=8;
	                    	this.reloadCool=0;
	                    }
	                }
				}
	        }
			else{
				state&=7;
			}
        }
		else {
			owner.getEntityAttribute(SharedMonsterAttributes.movementSpeed).removeModifier(ItemMinigun.slowdown);
			owner.getEntityAttribute(SharedMonsterAttributes.movementSpeed).removeModifier(ItemSniperRifle.slowdown);
		}
	}

	public boolean startedPress(){
		if(this.pressedStart){
			this.pressedStart=false;
			return true;
		}
		return false;
		
	}
	/*public static void tick(boolean client){
		
		Map<EntityLivingBase,Integer> map=TF2ActionHandler.playerAction.get(client);
	
		if(map.isEmpty()) return;
		Iterator<EntityLivingBase> iterator=map.keySet().iterator();
		while(iterator.hasNext())
		{	
			EntityLivingBase player = iterator.next();
	
			if(player.isDead||player.deathTime>0){
	
				iterator.remove();
				continue;
			}
			int state=0;
			if(map.get(player)!=null){
				state=map.get(player);
			}
		
			ItemStack stack = player.getHeldItem();
	
			WeaponsCapability cap=playerWeaponsCapability.get();
	
			cap.lastFire-=50;
			if (stack != null&&stack.getItem() instanceof ItemUsable)
	        {
				ItemUsable item=(ItemUsable) stack.getItem();
				
				//if(!client)
					//System.out.println(client+" rel " +item.getTagCompound().getShort("reload")+" "+state+" "+item.getDisplayName());
				if (cap.reloadCool > 0)
	            {
	                cap.reloadCool-=50;
	            }
				if (cap.fire1Cool > 0)
	            {
					cap.fire1Cool-=50;
	            }
				if (cap.fire2Cool > 0)
	            {
					cap.fire2Cool-=50;
	            }
				
				if(client){
					//((EntityPlayerMP)player).isChangingQuantityOnly=state>0;
					stack.animationsToGo=0;
				}
				if(player instanceof EntityTF2Character){
					EntityTF2Character shooter=((EntityTF2Character)player);
					if(shooter.getAttackTarget() != null){
						shooter.targetPrevPos[1]=shooter.targetPrevPos[0];
						shooter.targetPrevPos[3]=shooter.targetPrevPos[2];
						shooter.targetPrevPos[5]=shooter.targetPrevPos[4];
						shooter.targetPrevPos[0]=shooter.getAttackTarget().posX;
						shooter.targetPrevPos[2]=shooter.getAttackTarget().posY;
						shooter.targetPrevPos[4]=shooter.getAttackTarget().posZ;
					}
				}
				
				state=stateDo(player, stack, state,cap, client);
				if((!client||player!=Minecraft.getMinecraft().thePlayer)&&stack.getItem() instanceof ItemRangedWeapon && ((ItemRangedWeapon)stack.getItem()).hasClip(stack)){
					if(((state&4)!=0||stack.getItemDamage()==stack.getMaxDamage())&&(state&8)==0&&stack.getItemDamage()!=0&& cap.reloadCool<=0
							&&(cap.fire1Cool<=0||((ItemRangedWeapon)stack.getItem()).IsReloadingFullClip(stack))){
						state+=8;
						
						cap.reloadCool=((ItemRangedWeapon)stack.getItem()).getWeaponFirstReloadTime(stack,player);
						
						if(!client&&player instanceof EntityPlayerMP)
		                	TF2weapons.network.sendTo(new TF2Message.UseMessage(stack.getItemDamage(), true,EnumHand.MAIN_HAND), (EntityPlayerMP) player);
						
						if(client&&((ItemRangedWeapon)stack.getItem()).IsReloadingFullClip(stack)){
							TF2weapons.proxy.playReloadSound(player,stack);
		                }
						
					}
					else if(cap.fire1Cool<=0||((ItemRangedWeapon)stack.getItem()).IsReloadingFullClip(stack)){
		                while ((state&8)!=0&&cap.reloadCool <= 0 && stack.getItemDamage()!=0)
		                {
		                    if(((ItemRangedWeapon)stack.getItem()).IsReloadingFullClip(stack)){
		                    	stack.setItemDamage(0);
		                    	if(item.isDoubleWielding(player)){
		                    		player.getHeldItemOffhand().setItemDamage(0);
		                    	}
		                    } else {
		                    	stack.setItemDamage(stack.getItemDamage() - 1); 
		                    	TF2weapons.proxy.playReloadSound(player,stack);
		                    }
		                    if(!client&&player instanceof EntityPlayerMP)
		                    	TF2weapons.network.sendTo(new TF2Message.UseMessage(stack.getItemDamage(), true,EnumHand.MAIN_HAND), (EntityPlayerMP) player);
		                    
		                    cap.reloadCool+=((ItemRangedWeapon)stack.getItem()).getWeaponReloadTime(stack,player);
		                    
		                    if(stack.getItemDamage()==0){
		                    	state-=8;
		                    	cap.reloadCool=0;
		                    }
		                }
					}
		        }
	        }
			else {
				player.getEntityAttribute(SharedMonsterAttributes.movementSpeed).removeModifier(ItemMinigun.slowdown);
				player.getEntityAttribute(SharedMonsterAttributes.movementSpeed).removeModifier(ItemSniperRifle.slowdown);
			}
			map.put(player, state);
		}
	}*/
	public boolean shouldShoot(EntityLivingBase player,int state){
		return player.getActivePotionEffect(TF2weapons.stun)==null&&(!(!player.worldObj.isRemote && player instanceof EntityPlayer && (this.predictionList.isEmpty()||(this.predictionList.get(0).state&state)!=state))&&
				!((player.worldObj.isRemote||!(player instanceof EntityPlayer))&&(this.state&state)!=state));
	}
	public void stateDo(EntityLivingBase player,ItemStack stack){
		ItemUsable item=(ItemUsable) stack.getItem();
		//System.out.println(stack.getTagCompound().getByte("active"));
		if((this.state&1)!=0 && stack.getTagCompound().getByte("active")==2){
			//System.out.println("firin");
	        item.fireTick(stack, player, player.worldObj);
	        
	        /*if (!item.hasTagCompound())
	        {
	            item.getTagCompound() = new NBTTagCompound();
	            item.getTagCompound().setTag("Attributes", (NBTTagCompound) ((ItemUsable)item.getItem()).buildInAttributes.copy());
	        }*/
	        
		}
		while(this.fire1Cool <= 0&&shouldShoot(player,1)){
	    	
	    	//System.out.println("PLAJERRRR: "+player);
				TF2Message.PredictionMessage message=null;
				if(!player.worldObj.isRemote && player instanceof EntityPlayer){
					message=this.predictionList.remove(0);
				}
				if(!item.canFire(player.worldObj, player, stack))
					break;
	    		this.fire1Cool+=item.getFiringSpeed(stack,player);
	    		
	    		if(player instanceof EntityTF2Character){
	    			((EntityTF2Character)player).onShot();
	    		}
	    		if(player.getEntityData().getBoolean("IsDisguised")&& !(item instanceof ItemSapper)){
	    			TF2EventBusListener.disguise(player, false);
	    		}
	    		
	    		double oldX=player.posX, oldY=player.posY, oldZ=player.posZ;
				float oldPitch=player.rotationPitch, oldYaw=player.rotationYawHead;
				
				if(!player.worldObj.isRemote && player instanceof EntityPlayer){
	    			
	    			this.preparePlayerPrediction(player, message);
	    		}
				if(player.worldObj.isRemote && player == ClientProxy.getLocalPlayer()){
					message=new TF2Message.PredictionMessage(player.posX, player.posY-1.62, player.posZ, player.rotationPitch, player.rotationYawHead, 1);
				}
				
	    		item.use(stack, player, player.worldObj, message);
	    		
	    		if(player.worldObj.isRemote && player == ClientProxy.getLocalPlayer()){
	    			//System.out.println("Shoot Res: "+message.target);
		    		TF2weapons.network.sendToServer(message);
	    		}
	    		player.posX=oldX;
    			player.posY=oldY;
    			player.posZ=oldZ;
    			player.rotationYawHead=oldYaw;
    			player.rotationPitch=oldPitch;
    			
    			/*if(!player.worldObj.isRemote && player instanceof EntityPlayer){
    				this.predictionList.remove(0);
    				System.out.println(this.predictionList.size());
	    		}*/
    			
	    		this.lastFire=1250;
	    		if (stack.getItem() instanceof ItemWeapon)
	            {
	    			this.reloadCool=0;
	            	if((this.state&8)!=0){
	            		this.state-=8;
	            	}
	            }
    	}
		if((this.state&2)!=0 && stack.getTagCompound().getByte("active")==2){
			((ItemUsable)stack.getItem()).altFireTick(stack, player, player.worldObj);
			
		}
		while(this.fire2Cool <= 0&&shouldShoot(player,2)){
	    	
	    	//System.out.println("PLAJERRRR: "+player);
				TF2Message.PredictionMessage message=null;
				if(!player.worldObj.isRemote && player instanceof EntityPlayer){
					message=this.predictionList.remove(0);
				}
				
				if(item.getAltFiringSpeed(stack, player)==Short.MAX_VALUE||!item.canAltFire(player.worldObj, player, stack))
					break;
				this.fire2Cool+=item.getAltFiringSpeed(stack,player);
				
				if(player.getEntityData().getBoolean("IsDisguised")){
	    			TF2EventBusListener.disguise(player, false);
	    		}
				double oldX=player.posX, oldY=player.posY, oldZ=player.posZ;
				float oldPitch=player.rotationPitch, oldYaw=player.rotationYawHead;
				
	    		if(!player.worldObj.isRemote && player instanceof EntityPlayer){
	    			this.preparePlayerPrediction(player, message);
	    		}
	    		
	    		((ItemUsable)stack.getItem()).altUse(stack, player, player.worldObj);
	    		
	    		if(player.worldObj.isRemote && player == ClientProxy.getLocalPlayer()){
		    		TF2weapons.network.sendToServer(new TF2Message.PredictionMessage(player.posX, player.posY-1.62, player.posZ, player.rotationPitch, player.rotationYawHead, 2));
	    		}
	    		
	    		/*if(!player.worldObj.isRemote && player instanceof EntityPlayer){
    				this.predictionList.remove(0);
    				System.out.println(this.predictionList.size());
	    		}*/
	    		player.posX=oldX;
    			player.posY=oldY;
    			player.posZ=oldZ;
    			player.rotationYawHead=oldYaw;
    			player.rotationPitch=oldPitch;
    			
	    		if(!player.worldObj.isRemote)
	    			ItemUsable.sps++;
	    		if (stack.getItem() instanceof ItemWeapon)
	            {
	    			this.reloadCool=0;
	            	if((this.state&8)!=0){
	            		this.state-=8;
	            	}
	            }
	    	}
		
	}
	public void preparePlayerPrediction(EntityLivingBase player,TF2Message.PredictionMessage message){
		player.posX=message.x;
		player.posY=message.y;
		player.posZ=message.z;
		player.rotationYawHead=message.yaw;
		player.rotationPitch=message.pitch;
	}



	@Override
	public void saveNBTData(NBTTagCompound compound) {
		NBTTagCompound tag=new NBTTagCompound();
		tag.setByte("VisTicks", (byte) this.invisTicks);
		tag.setByte("DisguiseTicks", (byte) this.disguiseTicks);
		tag.setInteger("HealTarget", this.healTarget);
		compound.setTag(ID, tag);
	}

	@Override
	public void loadNBTData(NBTTagCompound compound) {
		NBTTagCompound nbt=compound.getCompoundTag(ID);
		this.invisTicks=nbt.getByte("VisTicks");
		this.disguiseTicks=nbt.getByte("DisguiseTicks");
		this.healTarget=nbt.getByte("HealTarget");
	}

	@Override
	public void init(Entity entity, World world) {
		this.owner=(EntityLivingBase)entity;
	}
}
