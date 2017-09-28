package rafradek.TF2weapons.decoration;

import cpw.mods.fml.common.network.internal.FMLNetworkHandler;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import rafradek.TF2weapons.ItemFromData;
import rafradek.TF2weapons.TF2weapons;
import rafradek.TF2weapons.WeaponData.PropertyType;

public class ItemWearable extends ItemFromData {

	public static int usedModel;
    @Override
	public boolean isValidArmor(ItemStack stack, int slot, Entity player){
		return slot==(isHat(stack)?0:1);
    }
    @Override
	public ItemStack onItemRightClick(ItemStack stack, World world,EntityPlayer living) {
		if(!world.isRemote){
			FMLNetworkHandler.openGui(living, TF2weapons.instance, 0, world, 0, 0, 0);
		}
		return stack;
	}
    
    public boolean isHat(ItemStack stack){
    	return getData(stack).getBoolean(PropertyType.HAT);
    }
    
    @Override
	public String getArmorTexture(ItemStack stack, Entity entity,int slot, String type)
    {
		return getData(stack).getString(PropertyType.ARMOR_IMAGE);
    }
}
