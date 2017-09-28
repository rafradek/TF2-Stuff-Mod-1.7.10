package rafradek.TF2weapons.weapons;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.util.MathHelper;
import rafradek.TF2weapons.MapList;
import rafradek.TF2weapons.building.EntityBuilding;

public class ItemKnife extends ItemMeleeWeapon {

	public ItemKnife(){
		super();
		/*this.addPropertyOverride(new ResourceLocation("backstab"), new IItemPropertyGetter()
        {
            @SideOnly(Side.CLIENT)
            public float apply(ItemStack stack, @Nullable World worldIn, @Nullable EntityLivingBase entityIn)
            {
            	if(entityIn == Minecraft.getMinecraft().thePlayer && Minecraft.getMinecraft().objectMouseOver!=null&&Minecraft.getMinecraft().objectMouseOver.entityHit!=null&&
            			Minecraft.getMinecraft().objectMouseOver.entityHit.getDistanceSqToEntity(entityIn)<=getMaxRange()*getMaxRange()&&
        				isBackstab(entityIn, Minecraft.getMinecraft().objectMouseOver.entityHit)){
        			return 1;
        		}
            	return 0;
            }
        });*/
	}
	public boolean isBackstab(EntityLivingBase living, Entity target){
		if(target != null&& target instanceof EntityLivingBase&&!(target instanceof EntityBuilding)){
			float ourAngle=180+MathHelper.wrapAngleTo180_float(living.rotationYawHead);
			float angle2=(float) (Math.atan2(living.posX-target.posX, living.posZ-target.posZ) * 180.0D / Math.PI);
			//System.out.println(angle2);
			if(angle2>=0){
				angle2=180-angle2;
			}
			else{
				angle2=-180-angle2;
			}
			angle2+=180;
			float enemyAngle=180+MathHelper.wrapAngleTo180_float(target.getRotationYawHead());
			float difference=180 - Math.abs(Math.abs(ourAngle - enemyAngle) - 180); 
			float difference2=180 - Math.abs(Math.abs(angle2 - enemyAngle) - 180); 
			//System.out.println(angle2+" "+difference2+" "+difference);
			if(difference<90&&difference2<90){
				return true;
			}
		}
		return false;
	}
	@Override
	public float getWeaponDamage(ItemStack stack,EntityLivingBase living, Entity target){
		if(this.isBackstab(living, target)){
			return Math.min(50, ((EntityLivingBase)target).getMaxHealth()*2);
		}
		return super.getWeaponDamage(stack, living, target);
	}
	@Override
	public int setCritical(ItemStack stack,EntityLivingBase shooter, Entity target, int old){
		return super.setCritical(stack, shooter, target, this.isBackstab(shooter, target)?2:old);
	}
	public IIcon getIcon(ItemStack stack, int renderPass, EntityPlayer player, ItemStack usingItem, int useRemaining)
    {
		if(player!=null&&player.getHeldItem()==stack&&
				stack.getItem() instanceof ItemKnife&&Minecraft.getMinecraft().objectMouseOver.entityHit!=null&&
				this.isBackstab(player, Minecraft.getMinecraft().objectMouseOver.entityHit)&&Minecraft.getMinecraft().objectMouseOver.entityHit.getDistanceSqToEntity(player)<=getMaxRange()*getMaxRange()){
			return MapList.nameToIcon.get(stack.getTagCompound().getString("Type")+"/b");
		}
        return getIcon(stack, renderPass);
    }
	/*@SideOnly(Side.CLIENT)
    public ModelResourceLocation getModel(ItemStack stack, EntityPlayer player, int useRemaining)
    {
		if(Minecraft.getMinecraft().thePlayer!=null&&Minecraft.getMinecraft().thePlayer.getHeldItem()==stack&&
				stack.getItem() instanceof ItemKnife&&Minecraft.getMinecraft().objectMouseOver.entityHit!=null&&
				this.isBackstab(player, Minecraft.getMinecraft().objectMouseOver.entityHit)){
			return ClientProxy.nameToModel.get(stack.getTagCompound().getString("Type")+"/b");
		}
		return null;
    }*/
}
