package rafradek.TF2weapons.weapons;

import java.util.List;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import rafradek.TF2weapons.TF2weapons;
import rafradek.TF2weapons.decoration.InventoryWearables;

public class ItemFireAmmo extends ItemAmmo {
	
	int type;
	public ItemFireAmmo(int type, int uses){
		this.type=type;
		this.setMaxDamage(uses);
		this.setHasSubtypes(false);
	}
	@Override
	public int getTypeInt(ItemStack stack){
		return type;
	}
	@Override
	public void getSubItems(Item par1, CreativeTabs par2CreativeTabs, List par3List)
    {
		par3List.add(new ItemStack(this));
    }
	@Override
	public int getItemStackLimit(ItemStack stack){
		return 1;
	}
	@SideOnly(Side.CLIENT)
	public void registerIcons(IIconRegister par1IconRegister) {
		this.itemIcon=par1IconRegister.registerIcon(this.getIconString());
	}
	@Override
	@SideOnly(Side.CLIENT)
	   public IIcon getIconIndex(ItemStack stack) {
		return this.itemIcon;
	}
	@Override
	public void consumeAmmo(EntityLivingBase living,ItemStack stack,int amount){
		if(stack==STACK_FILL) return;
		if(amount>0){
			stack.damageItem(amount, living);
			
			if(stack.stackSize<=0&&living instanceof EntityPlayer){
				IInventory invAmmo=InventoryWearables.get(living);
				if(invAmmo.getStackInSlot(3)!=null){
					for(int i=4;i<invAmmo.getSizeInventory();i++){
						ItemStack stackInv=invAmmo.getStackInSlot(i);
						if(stack==stackInv){
							invAmmo.setInventorySlotContents(i, null);
							return;
						}
					}
				}
				for(int i=0;i<((EntityPlayer)living).inventory.getSizeInventory();i++){
					ItemStack stackInv=((EntityPlayer)living).inventory.getStackInSlot(i);
					if(stack==stackInv){
						((EntityPlayer)living).inventory.setInventorySlotContents(i, null);
						return;
					}
				}
				
			}
		}
	}
}
