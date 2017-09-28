package rafradek.TF2weapons.crafting;

import java.util.List;
import java.util.Map.Entry;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import rafradek.TF2weapons.MapList;
import rafradek.TF2weapons.TF2weapons;
import rafradek.TF2weapons.WeaponData;
import rafradek.TF2weapons.WeaponData.PropertyType;

public class ItemTF2 extends Item {

	public static final String[] NAMES=new String[]{"ingotCopper","ingotLead","ingotAustralium","scrapMetal","reclaimedMetal","refinedMetal","nuggetAustralium","key","crate","randomWeapon","randomHat"};
	public static final String[] TEXTURE_NAMES=new String[]{"copper_ingot","lead_ingot","australium_ingot","scrap_metal","reclaimed_metal","refined_metal","australium_nugget","key","crate","random_weapon","random_hat"};
	public static IIcon[] icons;
	public ItemTF2(){
		this.setHasSubtypes(true);
		this.setCreativeTab(TF2weapons.tabtf2);
		this.setUnlocalizedName("tf2item");
	}
	public void registerIcons(IIconRegister par1IconRegister) {
	      icons=new IIcon[TEXTURE_NAMES.length];
	         for(int i=0;i<TEXTURE_NAMES.length;i++){
	        	 
	        	 icons[i]=par1IconRegister.registerIcon(TF2weapons.MOD_ID+":"+TEXTURE_NAMES[i]);
	         }
	   }
	@SideOnly(Side.CLIENT)
	   public IIcon getIconFromDamage(int damage) {
			return icons[damage%12];
	   }
	@Override
	public String getUnlocalizedName(ItemStack stack)
    {
		return "item."+NAMES[stack.getItemDamage()];
    }
	@Override
	public int getItemStackLimit(ItemStack stack){
		return (stack.getItemDamage()==9||stack.getItemDamage()==10)?1:64;
	}
	@Override
	public void getSubItems(Item par1, CreativeTabs par2CreativeTabs, List par3List)
    {
		//System.out.println(this.getCreativeTab());
		for(int i=0;i<8;i++){
			par3List.add(new ItemStack(this,1,i));
		}
    }
	
}
