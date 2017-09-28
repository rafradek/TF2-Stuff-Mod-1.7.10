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
import rafradek.TF2weapons.message.TF2Message.CapabilityMessage;
import rafradek.TF2weapons.weapons.WeaponsCapability;

public class TF2CapabilityHandler implements IMessageHandler<TF2Message.CapabilityMessage, IMessage> {

	@Override
	public IMessage onMessage(final CapabilityMessage message, MessageContext ctx) {
		if(ctx.side.isClient()){
			Entity ent=Minecraft.getMinecraft().theWorld.getEntityByID(message.entityID);
			if(ent != null && ent.getExtendedProperties(WeaponsCapability.ID) != null){
				WeaponsCapability cap=WeaponsCapability.get(ent);
				if(cap.healTarget!=message.healTarget&&message.healTarget>0){
					String sound=ItemFromData.getSound(((EntityLivingBase)ent).getHeldItem(),PropertyType.HEAL_START_SOUND);
					ClientProxy.playWeaponSound((EntityLivingBase) ent, sound, false, 0, ((EntityLivingBase)ent).getHeldItem());
				}
				
				cap.healTarget=message.healTarget;
				cap.critTime=message.critTime;
			}
			/*if(ent !=null){
				ent.getEntityData().setTag("TF2", message.tag);
			}*/
		}
		else{
			WeaponsCapability.get(ctx.getServerHandler().playerEntity).healTarget=message.healTarget;
			message.entityID=ctx.getServerHandler().playerEntity.getEntityId();
			TF2weapons.network.sendToDimension(message, ctx.getServerHandler().playerEntity.dimension);
		}
		return null;
	}

}
