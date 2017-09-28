package rafradek.TF2weapons.weapons;

import cpw.mods.fml.common.network.internal.FMLNetworkHandler;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import rafradek.TF2weapons.TF2weapons;

public class ItemAmmoBelt extends Item {

	public static IIcon ammoBeltEmptyTex;

	@Override
	public ItemStack onItemRightClick(ItemStack stack, World world,EntityPlayer living) {
		if(!world.isRemote){
			FMLNetworkHandler.openGui(living, TF2weapons.instance, 0, world, 0, 0, 0);
		}
		return stack;
	}
	
	@Override
	public String getArmorTexture(ItemStack stack, Entity entity, int slot, String type)
    {
		return TF2weapons.MOD_ID+":textures/models/tf2/ammo_belt.png";
    }
}
