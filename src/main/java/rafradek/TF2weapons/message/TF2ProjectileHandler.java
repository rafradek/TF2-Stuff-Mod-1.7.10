package rafradek.TF2weapons.message;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import net.minecraft.entity.player.EntityPlayer;
import rafradek.TF2weapons.message.TF2Message.PredictionMessage;
import rafradek.TF2weapons.weapons.WeaponsCapability;

public class TF2ProjectileHandler implements IMessageHandler<TF2Message.PredictionMessage, IMessage> {

	//public static HashMap<Entity, ArrayList<PredictionMessage>> nextShotPos= new HashMap<Entity, ArrayList<PredictionMessage>>();
	
	@Override
	public IMessage onMessage(PredictionMessage message, MessageContext ctx) {
		EntityPlayer shooter=ctx.getServerHandler().playerEntity;
		//ItemStack stack=shooter.getHeldItem();
		WeaponsCapability.get(shooter).predictionList.add(message);
		return null;
	}

}
