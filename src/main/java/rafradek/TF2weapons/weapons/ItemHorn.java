package rafradek.TF2weapons.weapons;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumAction;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import rafradek.TF2weapons.ItemFromData;
import rafradek.TF2weapons.TF2weapons;
import rafradek.TF2weapons.WeaponData.PropertyType;

public class ItemHorn extends Item {
	
	public ItemHorn(){
		this.setMaxStackSize(1);
		this.setCreativeTab(TF2weapons.tabtf2);
	}
	@Override
	public int getMaxItemUseDuration(ItemStack stack)
    {
        return 72000;
    }
	@Override
	public EnumAction getItemUseAction(ItemStack stack)
    {
        return EnumAction.bow;
    }
	public void onPlayerStoppedUsing(ItemStack stack, World worldIn, EntityPlayer entityLiving, int timeLeft)
    {
		ItemStack backpack=getBackpack(entityLiving);
		if(backpack != null && this.getMaxItemUseDuration(stack)-timeLeft>=ItemFromData.getData(backpack).getInt(PropertyType.FIRE_SPEED) && backpack.getTagCompound().getFloat("Rage")>=1){
			backpack.getTagCompound().setBoolean("Active", true);
			
			
		}
    }
	@Override
	public ItemStack onItemRightClick(ItemStack itemStackIn, World worldIn, EntityPlayer playerIn)
    {
		ItemStack backpack=getBackpack(playerIn);
		if(backpack !=null && (backpack.getTagCompound().getFloat("Rage")>=1 || playerIn.capabilities.isCreativeMode)){
            playerIn.setItemInUse(itemStackIn, this.getMaxItemUseDuration(itemStackIn));
            if(TF2weapons.getTeamForDisplay(playerIn)==1){
            	playerIn.playSound(ItemFromData.getSound(backpack, PropertyType.HORN_BLU_SOUND), 0.8f, 1f);
            }
            else{
            	playerIn.playSound(ItemFromData.getSound(backpack, PropertyType.HORN_RED_SOUND), 0.8f, 1f);
            }
            return itemStackIn;
        }
		return itemStackIn;
    }
	@SideOnly(Side.CLIENT)
	@Override
	public boolean showDurabilityBar(ItemStack stack)
    {
		if(getBackpack(Minecraft.getMinecraft().thePlayer) == null){
			return false;
		}
    	return getBackpack(Minecraft.getMinecraft().thePlayer).getTagCompound().getFloat("Rage")!=1;
    }
	@SideOnly(Side.CLIENT)
    @Override
	public double getDurabilityForDisplay(ItemStack stack)
    {
    	if(getBackpack(Minecraft.getMinecraft().thePlayer) == null){
			return 0;
		}
    	return 1-getBackpack(Minecraft.getMinecraft().thePlayer).getTagCompound().getFloat("Rage");
    }
	public static ItemStack getBackpack(EntityLivingBase living){
		return living.getEquipmentInSlot(3)!=null && living.getEquipmentInSlot(3).getItem() instanceof ItemSoldierBackpack ?living.getEquipmentInSlot(3):null;
	}
}
