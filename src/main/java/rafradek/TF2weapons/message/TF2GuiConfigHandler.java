package rafradek.TF2weapons.message;

import java.util.HashMap;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import net.minecraft.entity.Entity;
import net.minecraft.inventory.Container;
import rafradek.TF2weapons.building.EntityTeleporter;
import rafradek.TF2weapons.crafting.ContainerTF2Workbench;
import rafradek.TF2weapons.message.TF2Message.GuiConfigMessage;

public class TF2GuiConfigHandler implements IMessageHandler<TF2Message.GuiConfigMessage, IMessage> {

	public static HashMap<Entity, float[]> shotInfo= new HashMap<Entity, float[]>();
	
	@Override
	public IMessage onMessage(final GuiConfigMessage message, final MessageContext ctx) {
		if(message.isTile){
			Container container=ctx.getServerHandler().playerEntity.openContainer;
			if(container !=null && container.windowId==message.entityid&& container instanceof ContainerTF2Workbench){
				((ContainerTF2Workbench)container).currentRecipe=message.value;
				container.onCraftMatrixChanged(null);
			}
			/*TileEntity tile=ctx.getServerHandler().playerEntity.worldObj.getTileEntity(message.pos);
			if(tile !=null&&tile instanceof TileEntityCabinet){
				((TileEntityCabinet)tile).selectedRecipe=message.value;
			}*/
		}
		else{
			Entity ent=ctx.getServerHandler().playerEntity.worldObj.getEntityByID(message.entityid);
			if(ent!=null&&ent instanceof EntityTeleporter&&((EntityTeleporter)ent).getOwner()==ctx.getServerHandler().playerEntity){
			//if(message.option==0)
				((EntityTeleporter)ent).setID(message.id);
			//else if(message.option==1)
				((EntityTeleporter)ent).setExit(message.exit);
			//else if(message.option==2)
				if(message.grab)
					((EntityTeleporter)ent).grab();
			}
		}
		
		return null;
	}

}
