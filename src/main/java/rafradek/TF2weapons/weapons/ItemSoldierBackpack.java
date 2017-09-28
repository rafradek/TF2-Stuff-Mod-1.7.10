package rafradek.TF2weapons.weapons;

import java.util.List;

import net.minecraft.command.IEntitySelector;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;
import net.minecraftforge.common.ISpecialArmor;
import rafradek.TF2weapons.ItemFromData;
import rafradek.TF2weapons.MapList;
import rafradek.TF2weapons.TF2Attribute;
import rafradek.TF2weapons.TF2weapons;
import rafradek.TF2weapons.WeaponData.PropertyType;
import rafradek.TF2weapons.characters.EntityTF2Character;

public class ItemSoldierBackpack extends ItemFromData implements ISpecialArmor {
	public ItemSoldierBackpack(){
		/*this.addPropertyOverride(new ResourceLocation("bodyModel"), new IItemPropertyGetter()
        {
            @SideOnly(Side.CLIENT)
            public float apply(ItemStack stack, @Nullable World worldIn, @Nullable EntityLivingBase entityIn)
            {
            	if(ItemWearable.usedModel==1){
        			return 1;
        		}
            	return 0;
            }
        });
		this.addPropertyOverride(new ResourceLocation("headModel"), new IItemPropertyGetter()
        {
            @SideOnly(Side.CLIENT)
            public float apply(ItemStack stack, @Nullable World worldIn, @Nullable EntityLivingBase entityIn)
            {
            	if(ItemWearable.usedModel==2){
        			return 1;
        		}
            	return 0;
            }
        });*/
	}
	/*public Multimap<String, AttributeModifier> getAttributeModifiers(EntityEquipmentSlot slot, ItemStack stack)
    {
    	Multimap<String, AttributeModifier> multimap = super.getAttributeModifiers(slot, stack);
 
        if (slot == EntityEquipmentSlot.CHEST)
        {
            multimap.put(SharedMonsterAttributes.ARMOR.getAttributeUnlocalizedName(), new AttributeModifier(UUID.fromString("D8499B04-0E66-4726-AB29-64469D734E0D"), "Armor modifier", (double)getData(stack).getFloat(PropertyType.ARMOR), 0));
            multimap.put(SharedMonsterAttributes.ARMOR_TOUGHNESS.getAttributeUnlocalizedName(), new AttributeModifier(UUID.fromString("D8499B04-0E66-4726-AB29-64469D734AB7"), "Armor toughness modifier", (double)(double)getData(stack).getFloat(PropertyType.ARMOR_TOUGHNESS), 0));
        }

        return multimap;
    }*/
	@Override
	public boolean isValidArmor(ItemStack stack, int armorType, Entity entity)
    {
        return armorType==1;
    }
	public int getBuff(ItemStack stack){
		return MapList.potionNames.get(getData(stack).getString(PropertyType.EFFECT_TYPE));
	}
	@Override
	public boolean showDurabilityBar(ItemStack stack)
    {
    	return stack.getTagCompound().getFloat("Rage")!=1;
    }
    @Override
	public double getDurabilityForDisplay(ItemStack stack)
    {
    	return 1-stack.getTagCompound().getFloat("Rage");
    }
    public void addRage(ItemStack stack, float damage, EntityLivingBase target){
    	if(target instanceof EntityTF2Character){
    		damage *=0.5f;
    	}
    	else if(!(target instanceof EntityPlayer)){
    		damage *=0.35f;
    	}
    	stack.getTagCompound().setFloat("Rage", Math.min(1, stack.getTagCompound().getFloat("Rage")+damage/getData(stack).getFloat(PropertyType.DAMAGE)));
    }
    @Override
	@SuppressWarnings("unchecked")
	public void onArmorTick(World world, final EntityPlayer player, ItemStack itemStack){
    	if(!world.isRemote){
    		if(itemStack.getTagCompound().getBoolean("Active")){
    			itemStack.getTagCompound().setFloat("Rage", Math.max(0,itemStack.getTagCompound().getFloat("Rage")-1/(TF2Attribute.getModifier("Buff Duration", itemStack, getData(itemStack).getInt(PropertyType.DURATION),player)-20)));
    			if(itemStack.getTagCompound().getFloat("Rage")<=0){
    				itemStack.getTagCompound().setBoolean("Active", false);
    			}
    			for(EntityLivingBase living : (List<EntityLivingBase>) world.getEntitiesWithinAABBExcludingEntity(null, player.boundingBox.expand(10, 10, 10), new IEntitySelector(){

					@Override
					public boolean isEntityApplicable(Entity p_82704_1_) {
						// TODO Auto-generated method stub
						return p_82704_1_ instanceof EntityLivingBase && TF2weapons.isOnSameTeam(player,p_82704_1_);
					}
    				
    			})){
    				living.addPotionEffect(new PotionEffect(this.getBuff(itemStack),20));
    			}
    			
    		}
    		if(player.capabilities.isCreativeMode){
    			itemStack.getTagCompound().setFloat("Rage", 1);
            }
    	}
    }
	@Override
	public ArmorProperties getProperties(EntityLivingBase player, ItemStack armor, DamageSource source, double damage,
			int slot) {
		return new ArmorProperties(0, (getData(armor).getFloat(PropertyType.ARMOR)+getData(armor).getFloat(PropertyType.ARMOR_TOUGHNESS))/25, Integer.MAX_VALUE);
	}
	@Override
	public int getArmorDisplay(EntityPlayer player, ItemStack armor, int slot) {
		// TODO Auto-generated method stub
		return (int) (getData(armor).getFloat(PropertyType.ARMOR)+getData(armor).getFloat(PropertyType.ARMOR_TOUGHNESS));
	}
	@Override
	public void damageArmor(EntityLivingBase entity, ItemStack stack, DamageSource source, int damage, int slot) {
		
	}
    
    
}
