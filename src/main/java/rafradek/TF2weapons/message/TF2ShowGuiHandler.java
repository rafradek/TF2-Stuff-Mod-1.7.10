package rafradek.TF2weapons.message;

import cpw.mods.fml.common.network.internal.FMLNetworkHandler;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import rafradek.TF2weapons.TF2weapons;
import rafradek.TF2weapons.message.TF2Message.ShowGuiMessage;

public class TF2ShowGuiHandler implements IMessageHandler<TF2Message.ShowGuiMessage, IMessage> {

	@Override
	public IMessage onMessage(final ShowGuiMessage message, MessageContext ctx) {
		if(message.id!=99){
			FMLNetworkHandler.openGui(ctx.getServerHandler().playerEntity, TF2weapons.instance, message.id, ctx.getServerHandler().playerEntity.worldObj, 0, 0, 0);
		}
		else{
			
		}
		return null;
	}

}
