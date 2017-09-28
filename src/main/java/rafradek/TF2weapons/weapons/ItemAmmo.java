package rafradek.TF2weapons.weapons;

import java.util.List;

import cpw.mods.fml.common.network.internal.FMLNetworkHandler;
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
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import rafradek.TF2weapons.ItemFromData;
import rafradek.TF2weapons.TF2Attribute;
import rafradek.TF2weapons.TF2weapons;
import rafradek.TF2weapons.WeaponData.PropertyType;
import rafradek.TF2weapons.building.EntityDispenser;
import rafradek.TF2weapons.characters.EntityTF2Character;
import rafradek.TF2weapons.decoration.InventoryWearables;

public class ItemAmmo extends Item {
	
	public static final String[] AMMO_TYPES=new String[]{"none","shotgun","minigun","pistol","revolver","smg","sniper","rocket","grenade","syringe","fire","sticky","medigun"};
	public static final int[] AMMO_MAX_STACK=new int[]{64,64,64,64,64,64,16,24,24,64,1,32,1};
	public static final int[] AMMO_DROP=new int[]{0,2,6,6,2,10,1,2,2,10,1,2};
	public static ItemStack STACK_FILL;
	private static IIcon[] icons;
	
	public ItemAmmo(){
		this.setHasSubtypes(true);
	}
	public String getType(ItemStack stack){
		return AMMO_TYPES[this.getTypeInt(stack)];
	}
	public int getTypeInt(ItemStack stack){
		return stack.getItemDamage();
	}
	public boolean isValidForWeapon(ItemStack ammo,ItemStack weapon){
		return getTypeInt(ammo)==ItemFromData.getData(weapon).getInt(PropertyType.AMMO_TYPE);
	}
	@Override
	@SideOnly(Side.CLIENT)
    public CreativeTabs getCreativeTab()
    {
        return TF2weapons.tabtf2;
    }
	
	@Override
	public String getUnlocalizedName(ItemStack stack)
    {
		return "item.tf2ammo."+getType(stack);
    }
	@Override
	public void getSubItems(Item par1, CreativeTabs par2CreativeTabs, List par3List)
    {
		//System.out.println(this.getCreativeTab());
		for(int i=1;i<AMMO_TYPES.length;i++){
			if(i!=10&&i!=12){
				par3List.add(new ItemStack(this,1,i));
			}
		}
    }
	@Override
	public int getItemStackLimit(ItemStack stack){
		return AMMO_MAX_STACK[stack.getItemDamage()];
	}
	public void consumeAmmo(EntityLivingBase living,ItemStack stack,int amount){
		if(stack==STACK_FILL) return;
		//if(EntityDispenser.isNearDispenser(living.worldObj, living)) return;
		if(amount>0){
			stack.stackSize-=amount;
			
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
	public static void consumeAmmoGlobal(EntityLivingBase living,ItemStack stack,int amount){
		if(EntityDispenser.isNearDispenser(living.worldObj, living)) return;
		if(!(living instanceof EntityPlayer)) return;
		if(amount>0){
			amount=((ItemWeapon)stack.getItem()).getActualAmmoUse(stack, living, amount);
			//int type=ItemFromData.getData(stack).getInt(PropertyType.AMMO_TYPE);
			
			//stack.stackSize-=amount;
			ItemStack stackAmmo;
			while(amount>0&&(stackAmmo=searchForAmmo(living,stack))!=null){
				int inStack=stackAmmo.stackSize;
				((ItemAmmo)stackAmmo.getItem()).consumeAmmo(living,stackAmmo,amount);
				amount-=inStack;
			}
		}
	}
	public static ItemStack searchForAmmo(EntityLivingBase owner, ItemStack stack){
		if(EntityDispenser.isNearDispenser(owner.worldObj, owner)) return STACK_FILL;
		
		if(!(owner instanceof EntityPlayer)) return STACK_FILL;
		
		int type=ItemFromData.getData(stack).getInt(PropertyType.AMMO_TYPE);
		
		if(type == 0) return STACK_FILL;
		
		if(InventoryWearables.get(owner).getStackInSlot(3)!=null){
			for(int i=4;i<InventoryWearables.get(owner).getSizeInventory();i++){
				ItemStack stackCap=InventoryWearables.get(owner).getStackInSlot(i);
				if(stackCap !=null && stackCap.getItem() instanceof ItemAmmo&&((ItemAmmo)stackCap.getItem()).getTypeInt(stackCap)==type){
					return stackCap;
				}
			}
		}
		
		for(int i=0;i<((EntityPlayer)owner).inventory.mainInventory.length;i++){
			ItemStack stackInv=((EntityPlayer)owner).inventory.mainInventory[i];
			if(stackInv !=null && stackInv.getItem() instanceof ItemAmmo&&((ItemAmmo)stackInv.getItem()).getTypeInt(stackInv)==type){
				return stackInv;
			}
		}
		return null;
	}
	public static int getAmmoAmount(EntityLivingBase owner, ItemStack stack){
		if(EntityDispenser.isNearDispenser(owner.worldObj, owner)) return 900;
		
		if(!(owner instanceof EntityPlayer)) return ((EntityTF2Character)owner).ammoLeft;
		
		int type=ItemFromData.getData(stack).getInt(PropertyType.AMMO_TYPE);
		
		if(type==0) return 900;
		
		int ammoCount=0;
		
		if(InventoryWearables.get(owner).getStackInSlot(3)!=null){
			for(int i=4;i<InventoryWearables.get(owner).getSizeInventory();i++){
				ItemStack stackCap=InventoryWearables.get(owner).getStackInSlot(i);
				if(stackCap !=null && stackCap.getItem() instanceof ItemAmmo&&((ItemAmmo)stackCap.getItem()).getTypeInt(stackCap)==type){
					ammoCount+=stackCap.stackSize;
				}
			}
		}
		
		for(int i=0;i<((EntityPlayer)owner).inventory.mainInventory.length;i++){
			ItemStack stackInv=((EntityPlayer)owner).inventory.mainInventory[i];
			if(stackInv !=null && stackInv.getItem() instanceof ItemAmmo&&((ItemAmmo)stackInv.getItem()).getTypeInt(stackInv)==type){
				ammoCount+=stackInv.stackSize;
			}
		}
		return (int) (ammoCount/TF2Attribute.getModifier("Ammo Eff", stack, 1, owner));
	}
	@Override
	public ItemStack onItemRightClick(ItemStack stack, World world,EntityPlayer living) {
		/*if(!world.isRemote){
			FMLNetworkHandler.openGui(living, TF2weapons.instance, 0, world, 0, 0, 0);
		}*/
		return stack;
	}
	public void registerIcons(IIconRegister par1IconRegister) {
		  icons=new IIcon[AMMO_TYPES.length];
		     for(int i=1;i<AMMO_TYPES.length;i++){
		    	 
		    	 icons[i]=par1IconRegister.registerIcon(TF2weapons.MOD_ID+":ammo_"+AMMO_TYPES[i]);
		         }
		   }
	@Override
	@SideOnly(Side.CLIENT)
	   public IIcon getIconFromDamage(int damage) {
			return this.isDamageable()?super.getIconFromDamage(damage):icons[damage];
	   }
}
