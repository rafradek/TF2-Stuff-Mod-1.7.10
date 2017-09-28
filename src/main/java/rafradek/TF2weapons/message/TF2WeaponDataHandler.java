package rafradek.TF2weapons.message;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import rafradek.TF2weapons.TF2weapons;
import rafradek.TF2weapons.message.TF2Message.WeaponDataMessage;

public class TF2WeaponDataHandler implements IMessageHandler<TF2Message.WeaponDataMessage, IMessage> {

	public static int size;
	@Override
	public IMessage onMessage(final WeaponDataMessage message, MessageContext ctx) {
		
		TF2weapons.loadWeapon(message.weapon.getName(), message.weapon);
		//ClientProxy.RegisterWeaponData(message.weapon);
		return null;
	}

}
