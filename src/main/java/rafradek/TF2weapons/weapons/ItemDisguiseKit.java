package rafradek.TF2weapons.weapons;

import java.util.List;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import rafradek.TF2weapons.TF2weapons;

public class ItemDisguiseKit extends Item {
	
	public ItemDisguiseKit()
    {
        this.setCreativeTab(TF2weapons.tabtf2);
        this.setMaxStackSize(50);
        this.setMaxDamage(25);
    }
	public static void startDisguise(EntityLivingBase living, World world, String type){
		if(WeaponsCapability.get(living).disguiseTicks==0){
			//System.out.println("starting disguise");
			if(!world.isRemote){
				WeaponsCapability.get(living).disguiseTicks=1;
				living.getEntityData().setString("DisguiseType",type);
			}
		}
	}
	@Override
	public ItemStack onItemRightClick(ItemStack stack, World world,EntityPlayer living) {
		if(WeaponsCapability.get(living).disguiseTicks==0){
			if(!world.isRemote){
				startDisguise(living,world,"M:Zombie");
				if(!living.capabilities.isCreativeMode)
					stack.damageItem(1, living);
			}
		}
		return stack;
	}
	@Override
	public void addInformation(ItemStack par1ItemStack, EntityPlayer par2EntityPlayer, List par2List, boolean par4)
    {
    	super.addInformation(par1ItemStack, par2EntityPlayer, par2List, par4);
       
        //par2List.add("Charge: "+Float.toString(par1ItemStack.getTagCompound().getFloat("charge")));
    }
}
