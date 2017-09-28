package rafradek.TF2weapons.message;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import rafradek.TF2weapons.decoration.InventoryWearables;
import rafradek.TF2weapons.message.TF2Message.WearableChangeMessage;

public class TF2WearableChangeHandler implements IMessageHandler<TF2Message.WearableChangeMessage, IMessage> {

	@Override
	public IMessage onMessage(final WearableChangeMessage message, MessageContext ctx) {
		Entity entity=Minecraft.getMinecraft().theWorld.getEntityByID(message.entityID);
		if(entity!=null){
			InventoryWearables.get(entity).setInventorySlotContents(message.slot, message.stack);
		}
		return null;
	}

}
