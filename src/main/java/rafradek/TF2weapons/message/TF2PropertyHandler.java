package rafradek.TF2weapons.message;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import rafradek.TF2weapons.ClientProxy;
import rafradek.TF2weapons.ItemFromData;
import rafradek.TF2weapons.TF2weapons;
import rafradek.TF2weapons.WeaponData.PropertyType;
import rafradek.TF2weapons.message.TF2Message.PropertyMessage;
import rafradek.TF2weapons.weapons.ItemMedigun;
import rafradek.TF2weapons.weapons.WeaponsCapability;

public class TF2PropertyHandler implements IMessageHandler<TF2Message.PropertyMessage, IMessage> {

	@Override
	public IMessage onMessage(final PropertyMessage message, MessageContext ctx) {
		
		if(ctx.side.isClient()){
					if(Minecraft.getMinecraft().theWorld == null) return null;
					Entity ent=Minecraft.getMinecraft().theWorld.getEntityByID(message.entityID);
					if(ent !=null&&ent.getExtendedProperties(WeaponsCapability.ID) != null){
						if(ent instanceof EntityLivingBase&&((EntityLivingBase)ent).getHeldItem() != null&&((EntityLivingBase)ent).getHeldItem().getItem() instanceof ItemMedigun){
							if(message.name.equals("HealTarget")&&ent.getEntityData().getInteger("HealTarget")!=message.intValue&&message.intValue>0){
								String sound=ItemFromData.getSound(((EntityLivingBase)ent).getHeldItem(),PropertyType.HEAL_START_SOUND);
								ClientProxy.playWeaponSound((EntityLivingBase) ent, sound, false, 0, ((EntityLivingBase)ent).getHeldItem());
							}
						}
						
						if(message.type==0){
							ent.getEntityData().setInteger(message.name, message.intValue);
						}
						else if(message.type==1){
							ent.getEntityData().setFloat(message.name, message.floatValue);
						}
						else if(message.type==2){
							ent.getEntityData().setByte(message.name, message.byteValue);
						}
						else if(message.type==3){
							ent.getEntityData().setString(message.name, message.stringValue);
						}
						else if(message.type==4){
							ent.getEntityData().setBoolean(message.name, message.byteValue!=0);
						}
					}
		}
		else{
			if(message.type==0){
				ctx.getServerHandler().playerEntity.getEntityData().setInteger(message.name, message.intValue);
			}
			else if(message.type==1){
				ctx.getServerHandler().playerEntity.getEntityData().setFloat(message.name, message.floatValue);
			}
			else if(message.type==2){
				ctx.getServerHandler().playerEntity.getEntityData().setByte(message.name, message.byteValue);
			}
			else if(message.type==3){
				ctx.getServerHandler().playerEntity.getEntityData().setString(message.name, message.stringValue);
			}
			else if(message.type==4){
				ctx.getServerHandler().playerEntity.getEntityData().setBoolean(message.name, message.byteValue!=0);
			}
			//System.out.println("send: "+message.name+" "+message.intValue+" "+message.floatValue);
			message.entityID=ctx.getServerHandler().playerEntity.getEntityId();
			TF2weapons.network.sendToDimension(message, ctx.getServerHandler().playerEntity.dimension);
		}
		return null;
	}

}
