package rafradek.TF2weapons.decoration;

import javax.annotation.Nullable;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import rafradek.TF2weapons.weapons.ItemAmmo;
import rafradek.TF2weapons.weapons.ItemAmmoBelt;

public class ContainerWearables extends Container
{
    /** The crafting matrix inventory. */
    /** Determines if inventory manipulation should be handled. */
    public boolean isLocalWorld;
    private final EntityPlayer thePlayer;
    public IInventory wearables;

    public ContainerWearables(final InventoryPlayer playerInventory, final InventoryWearables wearables, boolean localWorld, EntityPlayer player)
    {
        this.isLocalWorld = localWorld;
        this.thePlayer = player;
        this.wearables=wearables;
        
        for (int i = 0; i < 3; ++i)
        {
            for (int j = 0; j < 3; ++j)
            {
                this.addSlotToContainer(new Slot(wearables, 4+j + i * 3, 98 + j * 18, 18 + i * 18){
                	
                    @Override
					public boolean isItemValid(@Nullable ItemStack stack)
                    {
                        if (stack==null || wearables.getStackInSlot(3)==null)
                        {
                            return false;
                        }
                        else
                        {
                            return stack.getItem() instanceof ItemAmmo;
                        }
                    }
                });
            }
        }

        for (int k = 0; k < 4; ++k)
        {
            final int i=k;
            this.addSlotToContainer(new Slot(playerInventory, 36 + (3 - k), 8, 8 + k * 18)
            {
                /**
                 * Returns the maximum stack size for a given slot (usually the same as getInventoryStackLimit(), but 1
                 * in the case of armor slots)
                 */
                @Override
				public int getSlotStackLimit()
                {
                    return 1;
                }
                /**
                 * Check if the stack is a valid item for this slot. Always true beside for the armor slots.
                 */
                @Override
				public boolean isItemValid(@Nullable ItemStack stack)
                {
                    if (stack == null)
                    {
                        return false;
                    }
                    else
                    {
                        return stack.getItem().isValidArmor(stack, i, thePlayer);
                    }
                }
                @SideOnly(Side.CLIENT)
                public IIcon getBackgroundIconIndex()
                {
                    return ItemArmor.func_94602_b(i);
                }
            });
        }
        /*for(int k=0; k<3; k++){
        	this.addSlotToContainer(new Slot(wearables, k, 77, 8+k*18){
	        	@Override
				public int getSlotStackLimit()
	            {
	                return 1;
	            }
	        	@Override
				public void onSlotChanged()
	            {
	        		super.onSlotChanged();
	        		if(!thePlayer.worldObj.isRemote){
	        			//System.out.println("changed");
	        			TF2weapons.network.sendToAllAround(new TF2Message.WearableChangeMessage(thePlayer, this.getSlotIndex(), this.getStack()), new TargetPoint(thePlayer.dimension, thePlayer.posX, thePlayer.posY, thePlayer.posZ, 256));
	        		}
	            }
	            @Override
				public boolean isItemValid(@Nullable ItemStack stack)
	            {
	                if (stack == null)
	                {
	                    return false;
	                }
	                else
	                {
	                    return stack.getItem() instanceof ItemWearable;
	                }
	            }
	            @Nullable
	            @SideOnly(Side.CLIENT)
	            public String getSlotTexture()
	            {
	                return ItemArmor.EMPTY_SLOT_NAMES[EntityEquipmentSlot.HEAD.getIndex()];
	            }
	        });
        }
        for(int k=0; k<3; k++){
        	this.addSlotToContainer(new Slot(wearables, k, 77, 8+k*18){
	        	@Override
				public int getSlotStackLimit()
	            {
	                return 1;
	            }
	        	@Override
				public void onSlotChanged()
	            {
	        		super.onSlotChanged();
	        		if(!thePlayer.worldObj.isRemote){
	        			//System.out.println("changed");
	        			TF2weapons.network.sendToAllAround(new TF2Message.WearableChangeMessage(thePlayer, this.getSlotIndex(), this.getStack()), new TargetPoint(thePlayer.dimension, thePlayer.posX, thePlayer.posY, thePlayer.posZ, 256));
	        		}
	            }

	            @Override
				public boolean isItemValid(@Nullable ItemStack stack)
	            {
	                if (stack == null)
	                {
	                    return false;
	                }
	                else
	                {
	                    return stack.getItem() instanceof ItemWearable;
	                }
	            }
	            @Nullable
	            @SideOnly(Side.CLIENT)
	            public String getSlotTexture()
	            {
	                return ItemArmor.EMPTY_SLOT_NAMES[EntityEquipmentSlot.HEAD.getIndex()];
	            }
	        });
        }*/
        this.addSlotToContainer(new Slot(wearables, 3, 154, 28){
        	@Override
			public int getSlotStackLimit()
            {
                return 1;
            }
            /**
             * Check if the stack is a valid item for this slot. Always true beside for the armor slots.
             */
            @Override
			public boolean isItemValid(@Nullable ItemStack stack)
            {
                if (stack == null)
                {
                    return false;
                }
                else
                {
                    return stack.getItem() instanceof ItemAmmoBelt;
                }
            }
            @SideOnly(Side.CLIENT)
            public IIcon getBackgroundIconIndex()
            {
                return ItemAmmoBelt.ammoBeltEmptyTex;
            }
        });
        for (int l = 0; l < 3; ++l)
        {
            for (int j1 = 0; j1 < 9; ++j1)
            {
                this.addSlotToContainer(new Slot(playerInventory, j1 + (l + 1) * 9, 8 + j1 * 18, 84 + l * 18));
            }
        }

        for (int i1 = 0; i1 < 9; ++i1)
        {
            this.addSlotToContainer(new Slot(playerInventory, i1, 8 + i1 * 18, 142));
        }
        //System.out.println("Slot count: "+this.inventorySlots.size());
    }

    public void onCraftMatrixChanged(IInventory p_75130_1_)
    {
       
    }

    /**
     * Called when the container is closed.
     */
    public void onContainerClosed(EntityPlayer p_75134_1_)
    {
        super.onContainerClosed(p_75134_1_);
    }

    public boolean canInteractWith(EntityPlayer p_75145_1_)
    {
        return true;
    }

    /**
     * Called when a player shift-clicks on a slot. You must override this or you will crash when someone does that.
     */
    public ItemStack transferStackInSlot(EntityPlayer p_82846_1_, int p_82846_2_)
    {
        ItemStack itemstack = null;
        Slot slot = (Slot)this.inventorySlots.get(p_82846_2_);

        if (slot != null && slot.getHasStack())
        {
            ItemStack itemstack1 = slot.getStack();
            itemstack = itemstack1.copy();

            if (p_82846_2_ == 0)
            {
                if (!this.mergeItemStack(itemstack1, 9, 45, true))
                {
                    return null;
                }

                slot.onSlotChange(itemstack1, itemstack);
            }
            else if (p_82846_2_ >= 1 && p_82846_2_ < 5)
            {
                if (!this.mergeItemStack(itemstack1, 9, 45, false))
                {
                    return null;
                }
            }
            else if (p_82846_2_ >= 5 && p_82846_2_ < 9)
            {
                if (!this.mergeItemStack(itemstack1, 9, 45, false))
                {
                    return null;
                }
            }
            else if (itemstack.getItem() instanceof ItemArmor && !((Slot)this.inventorySlots.get(5 + ((ItemArmor)itemstack.getItem()).armorType)).getHasStack())
            {
                int j = 5 + ((ItemArmor)itemstack.getItem()).armorType;

                if (!this.mergeItemStack(itemstack1, j, j + 1, false))
                {
                    return null;
                }
            }
            else if (p_82846_2_ >= 9 && p_82846_2_ < 36)
            {
                if (!this.mergeItemStack(itemstack1, 36, 45, false))
                {
                    return null;
                }
            }
            else if (p_82846_2_ >= 36 && p_82846_2_ < 45)
            {
                if (!this.mergeItemStack(itemstack1, 9, 36, false))
                {
                    return null;
                }
            }
            else if (!this.mergeItemStack(itemstack1, 9, 45, false))
            {
                return null;
            }

            if (itemstack1.stackSize == 0)
            {
                slot.putStack((ItemStack)null);
            }
            else
            {
                slot.onSlotChanged();
            }

            if (itemstack1.stackSize == itemstack.stackSize)
            {
                return null;
            }

            slot.onPickupFromSlot(p_82846_1_, itemstack1);
        }

        return itemstack;
    }
}