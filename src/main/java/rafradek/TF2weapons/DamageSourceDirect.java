package rafradek.TF2weapons;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.EntityDamageSource;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.StatCollector;

public class DamageSourceDirect extends EntityDamageSource implements TF2DamageSource
{
    public ItemStack weapon;
    public int critical;
    public DamageSourceDirect(ItemStack weapon, Entity shooter, int critical)
    {
        super("bullet", shooter);
        this.weapon = weapon;
        this.critical=critical;
    }
    
    @Override
	public boolean isDifficultyScaled()
    {
        return false;
    }
    /* (non-Javadoc)
	 * @see rafradek.TF2weapons.TF2DamageSource#getWeapon()
	 */
    @Override
	public ItemStack getWeapon(){
    	return weapon;
    }
    /* (non-Javadoc)
	 * @see rafradek.TF2weapons.TF2DamageSource#getCritical()
	 */
    @Override
	public int getCritical(){
    	return critical;
    }
   /* public DamageSource bypassesArmor()
    {
        this.setDamageBypassesArmor();
        return this;
    }*/
    /**
     * Returns the message to be displayed on player death.
     */
    public IChatComponent getDeathMessage(EntityLivingBase p_151519_1_)
    {
        //ItemStack itemstack = this.damageSourceEntity instanceof EntityLivingBase ? ((EntityLivingBase)this.damageSourceEntity).getHeldItem() : null;
        String s = "death.attack." + this.damageType;
        String s1 = s+ ".item";
        return weapon != null && StatCollector.canTranslate(s1) ? new ChatComponentTranslation(s1, new Object[] {p_151519_1_.func_145748_c_(), this.getEntity().func_145748_c_(), weapon.getDisplayName()}): new ChatComponentTranslation(s, new Object[] {p_151519_1_.func_145748_c_(), this.getEntity().func_145748_c_()});
    }
    /*public String getDeathMessage(EntityPlayer par1EntityPlayer)
    {
        return StatCollector.translateToLocalFormatted("death." + this.damageType, new Object[] {par1EntityPlayer.getDisplayName(), this.shooter.getCommandSenderName(), StatCollector.translateToLocal(this.weapon)});
    }*/
}
