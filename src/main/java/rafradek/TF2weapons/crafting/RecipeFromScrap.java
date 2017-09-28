package rafradek.TF2weapons.crafting;

import java.util.ArrayList;

import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.world.World;
import rafradek.TF2weapons.TF2weapons;

public class RecipeFromScrap implements IRecipe {

	@Override
	public boolean matches(InventoryCrafting inv, World worldIn) {
		ArrayList<ItemStack> stacks=new ArrayList<>();
		
		for(int x=0;x<inv.getSizeInventory();x++){
			ItemStack stack=inv.getStackInSlot(x);
			if(stack!=null){
				if(stacks.size()<2&&stack.getItem() instanceof ItemTF2 && stack.getItemDamage()==3){
					stacks.add(stack);
				}
				else{
					return false;
				}
			}
		}
		//System.out.println("matches "+(australium&&stack2!=null));
		return stacks.size()==2;
	}

	@Override
	public ItemStack getCraftingResult(InventoryCrafting inv) {
		// TODO Auto-generated method stub
		return new ItemStack(TF2weapons.itemTF2,1,6);
	}

	@Override
	public int getRecipeSize() {
		// TODO Auto-generated method stub
		return 2;
	}

	@Override
	public ItemStack getRecipeOutput() {
		// TODO Auto-generated method stu
		return new ItemStack(TF2weapons.itemTF2,1,6);
	}

}
