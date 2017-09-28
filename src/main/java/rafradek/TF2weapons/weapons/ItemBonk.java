package rafradek.TF2weapons.weapons;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumAction;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.world.World;
import rafradek.TF2weapons.ItemFromData;
import rafradek.TF2weapons.MapList;
import rafradek.TF2weapons.WeaponData.PropertyType;

public class ItemBonk extends ItemFromData {
	public ItemBonk()
    {
        super();
        this.setMaxStackSize(64);
    }
	@Override
	public int getMaxItemUseDuration(ItemStack stack)
    {
        return 40;
    }
    @Override
	public EnumAction getItemUseAction(ItemStack stack)
    {
        return EnumAction.drink;
    }
    @SideOnly(Side.CLIENT)
    @Override
	public boolean showDurabilityBar(ItemStack stack)
    {
    	Integer value=WeaponsCapability.get(Minecraft.getMinecraft().thePlayer).effectsCool.get(getData(stack).getName());
    	return value!=null&&value>0;
    }
    @SideOnly(Side.CLIENT)
    @Override
	public double getDurabilityForDisplay(ItemStack stack)
    {
    	Integer value=WeaponsCapability.get(Minecraft.getMinecraft().thePlayer).effectsCool.get(getData(stack).getName());
        return (double)(value!=null?value:0) / (double)ItemFromData.getData(stack).getInt(PropertyType.COOLDOWN);
    }
    @Override
	public ItemStack onItemRightClick(ItemStack itemStackIn, World worldIn, EntityPlayer playerIn)
    {
    	Integer value=WeaponsCapability.get(playerIn).effectsCool.get(getData(itemStackIn).getName());
    	if(value==null||value<=0){
    		playerIn.setItemInUse(itemStackIn, this.getMaxItemUseDuration(itemStackIn));
    	}
        return itemStackIn;
    }
    public ItemStack onEaten(ItemStack stack, World worldIn, EntityPlayer entityLiving)
    {
    	if(!entityLiving.capabilities.isCreativeMode)
    		stack.stackSize--;
    	WeaponsCapability.get(entityLiving).effectsCool.put(getData(stack).getName(), ItemFromData.getData(stack).getInt(PropertyType.COOLDOWN));
    	entityLiving.addPotionEffect(new PotionEffect(MapList.potionNames.get(getData(stack).getString(PropertyType.EFFECT_TYPE)), ItemFromData.getData(stack).getInt(PropertyType.DURATION)));
    	return stack;
    }
}
