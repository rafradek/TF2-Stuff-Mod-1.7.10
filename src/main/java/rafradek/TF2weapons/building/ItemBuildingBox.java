package rafradek.TF2weapons.building;

import java.util.List;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.util.MathHelper;
import net.minecraft.util.StatCollector;
import rafradek.TF2weapons.TF2weapons;
import rafradek.TF2weapons.characters.ItemMonsterPlacerPlus;

public class ItemBuildingBox extends ItemMonsterPlacerPlus {
	private static final String[] TEXTURE_NAMES = new String[]{"sentryred","sentryblu","dispenserred","dispenserblu","teleporterred","teleporterblu"};
	private static IIcon[] icons;
	public ItemBuildingBox(){
		this.setCreativeTab(TF2weapons.tabtf2);
		this.setUnlocalizedName("buildingbox");
		this.setHasSubtypes(true);
		this.setMaxStackSize(1);
	}
	public void registerIcons(IIconRegister par1IconRegister) {
	      icons=new IIcon[TEXTURE_NAMES.length];
	         for(int i=0;i<TEXTURE_NAMES.length;i++){
	        	 
	        	 icons[i]=par1IconRegister.registerIcon(TF2weapons.MOD_ID+":"+TEXTURE_NAMES[i]);
	         }
	   }
	@Override
	@SideOnly(Side.CLIENT)
    public IIcon getIconFromDamageForRenderPass(int p_77618_1_, int p_77618_2_)
    {
        return icons[MathHelper.clamp_int(p_77618_1_-18,0,5)];
    }
	@Override
	public void addInformation(ItemStack par1ItemStack, EntityPlayer par2EntityPlayer, List par2List, boolean par4)
    {
    	/*super.addInformation(par1ItemStack, par2EntityPlayer, par2List, par4);
    	if(par1ItemStack.getTagCompound()!=null)
    		par2List.add("tag: "+par1ItemStack.getTagCompound().toString());*/
    }
	@Override
	public void getSubItems(Item par1, CreativeTabs par2CreativeTabs, List par3List)
    {
    	for(int i=18;i<24;i++)
    		par3List.add(new ItemStack(par1, 1, i));
    }
	@Override
	@SuppressWarnings("deprecation")
	public String getItemStackDisplayName(ItemStack p_77653_1_)
    {
        //(I18n.format(this.getUnlocalizedName()+".name")).trim();
        int i=p_77653_1_.getItemDamage()/2;
        String s1="sentry";
        switch(i){
        case 10: s1="dispenser"; break;
        case 11: s1="teleporter"; break;
        }
        return StatCollector.translateToLocal(this.getUnlocalizedName()+"."+s1+".name");
    }
	@Override
	@SideOnly(Side.CLIENT)
    public int getColorFromItemStack(ItemStack p_82790_1_, int p_82790_2_)
    {
        return 16777215;
    }

}
