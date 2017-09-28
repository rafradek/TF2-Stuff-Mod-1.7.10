package rafradek.TF2weapons.message;

import java.util.ArrayList;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import cpw.mods.fml.relauncher.Side;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import rafradek.TF2weapons.TF2weapons;
import rafradek.TF2weapons.characters.EntityStatue;
import rafradek.TF2weapons.decoration.InventoryWearables;
import rafradek.TF2weapons.weapons.ItemUsable;
import rafradek.TF2weapons.weapons.WeaponsCapability;

public class TF2ActionHandler implements IMessageHandler<TF2Message.ActionMessage, IMessage> {
	
	/*public static Map<EntityLivingBase,Integer> playerAction=new HashMap<EntityLivingBase,Integer>();
	public static Map<EntityLivingBase,Integer> playerActionClient=new HashMap<EntityLivingBase,Integer>();*/
	//public static ThreadLocalMap<EntityLivingBase,Integer> playerAction=new ThreadLocalMap<EntityLivingBase,Integer>();
	//public static ThreadLocalMap<EntityLivingBase,Integer> previousPlayerAction=new ThreadLocalMap<EntityLivingBase,Integer>();
	@Override
	public IMessage onMessage(final TF2Message.ActionMessage message, final MessageContext ctx) {
		if(ctx.side==Side.SERVER){
			final EntityPlayerMP player=ctx.getServerHandler().playerEntity;
			if(message.value<=15){
				handleMessage(message, player,false);
				message.entity=player.getEntityId();
				TF2weapons.network.sendToDimension(message, player.dimension);
			}
			else if(message.value==99){
				Entity wearer=ctx.getServerHandler().playerEntity.worldObj.getEntityByID(message.entity);
				//System.out.println("ID: "+message.entity+" "+wearer);
				if(wearer==null||!(wearer instanceof EntityPlayer)){
					wearer=player;
				}
				TF2weapons.network.sendTo(new TF2Message.WearableChangeMessage((EntityPlayer) wearer, 0, InventoryWearables.get(wearer).getStackInSlot(0)), player);
				TF2weapons.network.sendTo(new TF2Message.WearableChangeMessage((EntityPlayer) wearer, 1, InventoryWearables.get(wearer).getStackInSlot(1)), player);
				TF2weapons.network.sendTo(new TF2Message.WearableChangeMessage((EntityPlayer) wearer, 2, InventoryWearables.get(wearer).getStackInSlot(2)), player);
				TF2weapons.network.sendTo(new TF2Message.WearableChangeMessage((EntityPlayer) wearer, 3, InventoryWearables.get(wearer).getStackInSlot(3)), player);
			}
			else if(message.value==16){
				player.worldObj.getScoreboard().func_151392_a(player.getDisplayName(), "RED");
			}
			else if(message.value==17){
				player.worldObj.getScoreboard().func_151392_a(player.getDisplayName(), "BLU");
			}
		}
		else{
			final EntityLivingBase player=(EntityLivingBase) Minecraft.getMinecraft().theWorld.getEntityByID(message.entity);
			if(message.value<=15){
				handleMessage(message, player,true);
			}
			else if(message.value==19){
				if(player != null){
					player.setDead();
					player.worldObj.spawnEntityInWorld(new EntityStatue(player.worldObj,player));
				}
			}
			else if(message.value==22){
				if(player !=null && player.getHeldItem() != null && player.getHeldItem().hasTagCompound()){
					player.getHeldItem().getTagCompound().setByte("active",(byte) 2);
				}
			}
		}
		return null;
	}
	/*public static class TF2ActionHandlerReturn implements IMessageHandler<TF2Message.ActionMessage, IMessage> {

		@Override
		public IMessage onMessage(TF2Message.ActionMessage message, MessageContext ctx) {
			EntityLivingBase player=(EntityLivingBase) Minecraft.getMinecraft().theWorld.getEntityByID(message.entity);
			handleMessage(message, player);
			return null;
		}

	}*/
	public static void handleMessage(TF2Message.ActionMessage message,EntityLivingBase player,boolean client){
		if(player!=null){
			/*int oldValue=playerAction.get().containsKey(player)?playerAction.get().get(player):0;
			if(player.getHeldItem() != null && player.getHeldItem().getItem() instanceof ItemUsable){
				if((oldValue&1)==0&&(message.value&1)!=0){
					((ItemUsable)player.getHeldItem().getItem()).startUse(player.getHeldItem(), player, player.worldObj);
				}
				if((oldValue&1)==0&&(message.value&1)!=0){
					((ItemUsable)player.getHeldItem().getItem()).endUse(player.getHeldItem(), player, player.worldObj);
				}
			}*/
			/*if(previousPlayerAction.get(player.worldObj.isRemote).containsKey(player)){
				previousPlayerAction.get(player.worldObj.isRemote).put(player, 0);
			}
			int oldState=previousPlayerAction.get(player.worldObj.isRemote).get(player);
			
			previousPlayerAction.get(player.worldObj.isRemote).put(player, playerAction.get(true).get(player));*/
			
			WeaponsCapability cap= WeaponsCapability.get(player);
			ItemStack stack=player.getHeldItem();
			int oldState=cap.state&3;
			
			cap.state=message.value+(cap.state&8);
			
			if(stack != null&&stack.getItem() instanceof ItemUsable&&oldState!=(message.value&3)&&stack.getTagCompound().getByte("active")==2){
				if((oldState&2)<(message.value&2)){
					((ItemUsable)stack.getItem()).startUse(stack, player, player.worldObj, oldState, message.value&3);
					cap.stateDo(player, stack);
				}
				else if((oldState&2)>(message.value&2)){
					((ItemUsable)stack.getItem()).endUse(stack, player, player.worldObj, oldState, message.value&3);
				}
				if((oldState&1)<(message.value&1)){
					((ItemUsable)stack.getItem()).startUse(stack, player, player.worldObj, oldState, message.value&3);
					cap.stateDo(player, stack);
				}
				else if((oldState&1)>(message.value&1)){
					((ItemUsable)stack.getItem()).endUse(stack, player, player.worldObj, oldState, message.value&3);
				}
			}
			//System.out.println("change "+playerAction.get(player.worldObj.isRemote).get(player));
			//System.out.println("dostal: "+message.value);
		}
	}
	public static ArrayList<EnumAction> value=new ArrayList<EnumAction>();
	public static enum EnumAction{
		
		IDLE,
		FIRE,
		ALTFIRE,
		RELOAD;

		private EnumAction(){
			value.add(this);
		}
		
		/*public boolean leftClick(){
			return this==FIRE||this==DOUBLE;
		}
		public boolean rightClick(){
			return this==ALTFIRE||this==DOUBLE;
		}*/
	}
}
