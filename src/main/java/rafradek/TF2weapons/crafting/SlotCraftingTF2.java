package rafradek.TF2weapons.crafting;

import cpw.mods.fml.common.FMLCommonHandler;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.inventory.SlotCrafting;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerDestroyItemEvent;
import rafradek.TF2weapons.ItemFromData;
import rafradek.TF2weapons.TF2weapons;

public class SlotCraftingTF2 extends SlotCrafting {

	private InventoryCrafting craftMatrix;
	private EntityPlayer thePlayer;
	public SlotCraftingTF2(EntityPlayer player, InventoryCrafting craftingInventory, IInventory inventoryIn,
			int slotIndex, int xPosition, int yPosition) {
		super(player, craftingInventory, inventoryIn, slotIndex, xPosition, yPosition);
		thePlayer=player;
		craftMatrix=craftingInventory;
	}
	@Override
	public void onPickupFromSlot(EntityPlayer playerIn, ItemStack stack)
    {
		if(stack.getItem()==TF2weapons.itemTF2&&stack.getItemDamage()==9){
			stack=ItemFromData.getRandomWeapon(playerIn.getRNG(),ItemFromData.VISIBLE_WEAPON);
			playerIn.inventory.setItemStack(stack);
	    }
		else if(stack.getItem()==TF2weapons.itemTF2&&stack.getItemDamage()==10){
			stack=ItemFromData.getRandomWeaponOfClass("cosmetic",playerIn.getRNG());
			playerIn.inventory.setItemStack(stack);
	    }
		FMLCommonHandler.instance().firePlayerCraftingEvent(playerIn, stack, craftMatrix);
        this.onCrafting(stack);

        for (int i = 0; i < this.craftMatrix.getSizeInventory(); ++i)
        {
            ItemStack itemstack1 = this.craftMatrix.getStackInSlot(i);

            if (itemstack1 != null)
            {
                this.craftMatrix.decrStackSize(i, 1);

                if (itemstack1.getItem().hasContainerItem(itemstack1))
                {
                    ItemStack itemstack2 = itemstack1.getItem().getContainerItem(itemstack1);

                    if (itemstack2 != null && itemstack2.isItemStackDamageable() && itemstack2.getItemDamage() > itemstack2.getMaxDamage())
                    {
                        MinecraftForge.EVENT_BUS.post(new PlayerDestroyItemEvent(thePlayer, itemstack2));
                        continue;
                    }

                    if (!itemstack1.getItem().doesContainerItemLeaveCraftingGrid(itemstack1) || !this.thePlayer.inventory.addItemStackToInventory(itemstack2))
                    {
                        if (this.craftMatrix.getStackInSlot(i) == null)
                        {
                            this.craftMatrix.setInventorySlotContents(i, itemstack2);
                        }
                        else
                        {
                            this.thePlayer.dropPlayerItemWithRandomChoice(itemstack2, false);
                        }
                    }
                }
            }
        }
    }
}
