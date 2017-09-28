package rafradek.TF2weapons.decoration;

import net.minecraft.entity.Entity;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.world.World;
import net.minecraftforge.common.IExtendedEntityProperties;

public class InventoryWearables extends InventoryBasic implements IExtendedEntityProperties{

	public static final String ID="TF2Inventory";
	public InventoryWearables() {
		super("Wearables", false, 13);
		// TODO Auto-generated constructor stub
	}

	public static InventoryWearables get(Entity ent){
		return (InventoryWearables) ent.getExtendedProperties(ID);
	}
	public boolean isEmpty(){
		for(int i=0;i<4;i++){
			if(this.getStackInSlot(i)!=null){
				return false;
			}
		}
		return true;
	}

	@Override
	public void saveNBTData(NBTTagCompound compound) {
		NBTTagList list=new NBTTagList();
		for(int i=0;i<this.getSizeInventory();i++){
			ItemStack itemstack = this.getStackInSlot(i);

            if (itemstack != null)
            {
                NBTTagCompound nbttagcompound = new NBTTagCompound();
                nbttagcompound.setByte("Slot", (byte)i);
                itemstack.writeToNBT(nbttagcompound);
                list.appendTag(nbttagcompound);
            }
		}
		compound.setTag(ID, list);
	}

	@Override
	public void loadNBTData(NBTTagCompound compound) {
		NBTTagList nbt=compound.getTagList(ID, 10);
		for (int i = 0; i < nbt.tagCount(); ++i)
        {
            NBTTagCompound nbttagcompound = nbt.getCompoundTagAt(i);
            int j = nbttagcompound.getByte("Slot");
            this.setInventorySlotContents(j, ItemStack.loadItemStackFromNBT(nbttagcompound));
        }
	}

	@Override
	public void init(Entity entity, World world) {
		// TODO Auto-generated method stub
		
	}

}
