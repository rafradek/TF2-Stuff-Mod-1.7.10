package rafradek.TF2weapons.message;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import rafradek.TF2weapons.ClientProxy;
import rafradek.TF2weapons.TF2weapons;
import rafradek.TF2weapons.weapons.ItemWeapon;

public class TF2UseHandler implements IMessageHandler<TF2Message.UseMessage, IMessage> {
	
	@Override
	public IMessage onMessage(TF2Message.UseMessage message, MessageContext ctx) {
		EntityPlayer player=TF2weapons.proxy.getPlayerForSide(ctx);
		ItemStack stack=player.getHeldItem();
		if(stack!=null&&stack.getItem() instanceof ItemWeapon){
			//System.out.println("setting "+message.value);
			stack.setItemDamage(message.value);
			if(message.reload&&message.value!=0){
				/*if(stack.getItemDamage()==0&&TF2ActionHandler.playerAction.get().get(player)!=null&&(TF2ActionHandler.playerAction.get().get(player)&8)==0){
					TF2ActionHandler.playerAction.get().put(player, arg1)
				}*/
				ClientProxy.soundsToStart.put(player,stack);//TF2weapons.proxy.playReloadSound(player,stack);
			}
		}
		return null;
	}
}
