package rafradek.TF2weapons;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.Random;

import com.google.common.base.Predicate;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagFloat;
import net.minecraft.util.IIcon;
import rafradek.TF2weapons.WeaponData.PropertyType;
import rafradek.TF2weapons.weapons.ItemBulletWeapon;

public class ItemFromData extends Item {

	public static final Predicate<WeaponData> VISIBLE_WEAPON=new Predicate<WeaponData>(){

		@Override
		public boolean apply(WeaponData input) {
			// TODO Auto-generated method stub
			return !input.getBoolean(PropertyType.HIDDEN)&&!input.getBoolean(PropertyType.ROLL_HIDDEN)&&!input.getString(PropertyType.CLASS).equals("cosmetic")&&!input.getString(PropertyType.CLASS).equals("crate");
		}
		
	};
	public static boolean addedIcons;
	public ItemFromData()
    {
    	this.setCreativeTab(TF2weapons.tabtf2);
        this.setUnlocalizedName("tf2usable");
        this.setMaxStackSize(1);
        this.setNoRepair();
        // TODO Auto-generated constructor stub
    }
	@Override
	@SideOnly(Side.CLIENT)
    public CreativeTabs getCreativeTab()
    {
        return TF2weapons.tabtf2;
    }
	public static WeaponData getData(ItemStack stack){
		if(stack != null&& stack.hasTagCompound()){
			return MapList.nameToData.get(stack.getTagCompound().getString("Type"));
		}
		return null;
	}
	@Override
	public void getSubItems(Item par1, CreativeTabs par2CreativeTabs, List par3List)
    {
		//System.out.println(this.getCreativeTab());
		Iterator<Entry<String,WeaponData>> iterator=MapList.nameToData.entrySet().iterator();
		while(iterator.hasNext()){
			Entry<String, WeaponData> entry=iterator.next();
			//System.out.println("Hidden: "+entry.getValue().hasProperty(PropertyType.HIDDEN));
			if(entry.getValue().hasProperty(PropertyType.HIDDEN)&&entry.getValue().getBoolean(PropertyType.HIDDEN)){
				continue;
			}
			Item item=MapList.weaponClasses.get(entry.getValue().getString(PropertyType.CLASS));
			if(item==this){
				par3List.add(ItemFromData.getNewStack(entry.getKey()));
			}
		}
    }
	public static ItemStack getNewStack(String type){
		//System.out.println("Dano: "+type+" "+MapList.weaponClasses.get(MapList.nameToCC.get(type).get("Class").getString())+" "+Thread.currentThread().getName());
		ItemStack stack=new ItemStack(MapList.weaponClasses.get(MapList.nameToData.get(type).getString(PropertyType.CLASS)));
		stack.setTagCompound((NBTTagCompound) MapList.buildInAttributes.get(type).copy());
		//System.out.println(stack.toString());
		return stack;
	}
	public static ItemStack getRandomWeapon(Random random,Predicate<WeaponData> predicate){
		
		ArrayList<String> weapons=new ArrayList<>();
		for(Entry<String,WeaponData> entry: MapList.nameToData.entrySet()){
			if(predicate.apply(entry.getValue())){
				weapons.add(entry.getKey());
			}
		}
		if(weapons.isEmpty()){
			return null;
		}
		return getNewStack(weapons.get(random.nextInt(weapons.size())));
	}
	public static ItemStack getRandomWeaponOfType(String type, float chanceOfParent,Random random){
		//WeaponData parent=MapList.nameToData.get(type);
		if(chanceOfParent>=0&&random.nextFloat()<=chanceOfParent){
			return getNewStack(type);
		}
		else{
			ArrayList<String> weapons=new ArrayList<>();
			if(chanceOfParent<0){
				weapons.add(type);
			}
			for(Entry<String,WeaponData> entry: MapList.nameToData.entrySet()){
				if(!entry.getValue().getBoolean(PropertyType.HIDDEN)&&!entry.getValue().getBoolean(PropertyType.ROLL_HIDDEN)&&entry.getValue().getString(PropertyType.BASED_ON).equals(type)){
					weapons.add(entry.getKey());
				}
			}
			if(weapons.size()>0)
				return getNewStack(weapons.get(random.nextInt(weapons.size())));
			else
				return getNewStack(type);
		}
		
	}
	public static ItemStack getRandomWeaponOfClass(String clazz, Random random){
		ArrayList<String> weapons=new ArrayList<>();
		for(Entry<String,WeaponData> entry: MapList.nameToData.entrySet()){
			if(!entry.getValue().getBoolean(PropertyType.HIDDEN)&&!entry.getValue().getBoolean(PropertyType.ROLL_HIDDEN)&&entry.getValue().getString(PropertyType.CLASS).equals(clazz)){
				weapons.add(entry.getKey());
			}
		}
		return getNewStack(weapons.get(random.nextInt(weapons.size())));
	}
	public static ItemStack getRandomWeaponOfSlotMob(final String mob,final int slot, Random random, final boolean showHidden){
		/*ArrayList<String> weapons=new ArrayList<>();
		for(Entry<String,WeaponData> entry: MapList.nameToData.entrySet()){
			if(!entry.getValue().getBoolean(PropertyType.ROLL_HIDDEN)&&entry.getValue().getString(PropertyType.TYPE).equals(type)){
				String[] mobTypes=entry.getValue().getString(PropertyType.MOB_TYPE).contains(s)
				for(String mobType:mobTypes){
					if(mob.equalsIgnoreCase(mobType.trim())){
						weapons.add(entry.getKey());
						break;
					}
				}
			}
		}*/
		return getRandomWeapon(random, new Predicate<WeaponData>(){

			@Override
			public boolean apply(WeaponData input) {
				// TODO Auto-generated method stub
				return !(input.getBoolean(PropertyType.ROLL_HIDDEN)&&!showHidden)&&input.getInt(PropertyType.SLOT)==slot&&input.getString(PropertyType.MOB_TYPE).contains(mob);
			}
			
		});
	}
	public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged)
    {
        return getData(oldStack)!=getData(newStack)||(slotChanged);
    }

	@Override
	public String getItemStackDisplayName(ItemStack stack)
    {
		if(ItemFromData.getData(stack) == null){
			return "Weapon";
		}
		String name=ItemFromData.getData(stack).getString(PropertyType.NAME);
		if(stack.getTagCompound().getBoolean("Strange")){
			name=TF2EventBusListener.STRANGE_TITLES[stack.getTagCompound().getInteger("StrangeLevel")]+" "+name;
		}
		if(stack.getTagCompound().getBoolean("Australium")){
			name="Australium "+name;
		}
		return name;
    }

	public static String getSound(ItemStack stack, PropertyType name){
		return getData(stack).getString(name);
	}
	public void registerIcons(IIconRegister par1IconRegister) {
	      //this.itemIcon = par1IconRegister.registerIcon(this.getIconString());
	      
	         for(Entry<String,WeaponData> entry:MapList.nameToData.entrySet()){
	        	if(MapList.weaponClasses.get(entry.getValue().getString(PropertyType.CLASS)) == this){
		            MapList.nameToIcon.put(entry.getKey(), par1IconRegister.registerIcon(entry.getValue().getString(PropertyType.RENDER)));
		            if(entry.getValue().hasProperty(PropertyType.RENDER_BACKSTAB)){
		            	MapList.nameToIcon.put(entry.getKey()+"/b",par1IconRegister.registerIcon(entry.getValue().getString(PropertyType.RENDER_BACKSTAB)));
		            }
	        	}
	         }
	   }
	@SideOnly(Side.CLIENT)
	   public IIcon getIconIndex(ItemStack stack) {
	      if(stack.stackTagCompound != null) {
	         IIcon icon = (IIcon)MapList.nameToIcon.get(stack.stackTagCompound.getString("Type"));
	         if(icon != null) {
	            return icon;
	         }
	      }

	      return (IIcon)MapList.nameToIcon.get("minigun");
	   }
	@SideOnly(Side.CLIENT)
	   public IIcon getIcon(ItemStack stack,int pass) {
	      if(stack.stackTagCompound != null) {
	         IIcon icon = (IIcon)MapList.nameToIcon.get(stack.stackTagCompound.getString("Type"));
	         if(icon != null) {
	            return icon;
	         }
	      }

	      return (IIcon)MapList.nameToIcon.get("minigun");
	   }
	@Override
	@SideOnly(Side.CLIENT)
    public int getColorFromItemStack(ItemStack stack, int p_82790_2_)
    {
		return stack.hasTagCompound() && stack.getTagCompound().getBoolean("Australium") ? 0xFFD400:0xFFFFFF;
    }
	@Override
	public void addInformation(ItemStack par1ItemStack, EntityPlayer par2EntityPlayer, List par2List, boolean par4)
    {	
		/*if (!par1ItemStack.hasTagCompound()) {
			par1ItemStack.getTagCompound()=new NBTTagCompound();
			par1ItemStack.getTagCompound().setTag("Attributes", (NBTTagCompound) ((ItemUsable)par1ItemStack.getItem()).buildInAttributes.copy());
		}*/
        if (par1ItemStack.hasTagCompound())
        {
        	NBTTagCompound attributeList=par1ItemStack.getTagCompound().getCompoundTag("Attributes");
    		Iterator<String> iterator=attributeList.func_150296_c().iterator();
    		while(iterator.hasNext()){
    			String name=iterator.next();
    			NBTBase tag=attributeList.getTag(name);
    			if(tag instanceof NBTTagFloat){
    				NBTTagFloat tagFloat=(NBTTagFloat) tag;
    				TF2Attribute attribute=TF2Attribute.attributes[Integer.parseInt(name)];
    				par2List.add(attribute.getTranslatedString(tagFloat.func_150288_h(),true));
    			}
            }
        }
    }

}
