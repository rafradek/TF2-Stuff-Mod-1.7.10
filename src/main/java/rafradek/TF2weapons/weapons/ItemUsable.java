package rafradek.TF2weapons.weapons;

import java.util.HashMap;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import rafradek.TF2weapons.ItemFromData;
import rafradek.TF2weapons.TF2Attribute;
import rafradek.TF2weapons.WeaponData.PropertyType;
import rafradek.TF2weapons.message.TF2Message.PredictionMessage;

public abstract class ItemUsable extends ItemFromData
{
    //public ConfigCategory data;
    //public String render;
    public static int sps;
    //public static int tickleft;
    //public static boolean addedIcons;
    //public static ThreadLocalMap<EntityLivingBase, NBTTagCompound> itemProperties=new ThreadLocalMap<EntityLivingBase, NBTTagCompound>();
    public static HashMap<EntityLivingBase,float[]> lastDamage= new HashMap<EntityLivingBase,float[]>();
    
    /*public ActionResult<ItemStack> onItemRightClick(ItemStack itemStackIn, World worldIn, EntityPlayer playerIn)
    {
        return new ActionResult<ItemStack>(this.canAltFire(worldIn, playerIn, itemStackIn)&&this.getAltFiringSpeed(itemStackIn, playerIn)!=Short.MAX_VALUE?EnumActionResult.SUCCESS:EnumActionResult.PASS, itemStackIn);
    }*/
    public abstract boolean use(ItemStack stack, EntityLivingBase living, World world, PredictionMessage message);
    
    public boolean startUse(ItemStack stack, EntityLivingBase living, World world, int oldState, int newState) {
    	WeaponsCapability.get(living).pressedStart=true;
		return false;
	}
    
    public int getAmmoType(ItemStack stack){
    	return getData(stack).getInt(PropertyType.AMMO_TYPE);
    }
    
    public int getActualAmmoUse(ItemStack stack,EntityLivingBase living, int amount){
    	if(this.getAmmoType(stack)==0) return 0;
    	
    	stack.getTagCompound().setFloat("UsedAmmo", stack.getTagCompound().getFloat("UsedAmmo")+amount*TF2Attribute.getModifier("Ammo Eff", stack, 1, living));
		amount=0;
		while(stack.getTagCompound().getFloat("UsedAmmo")>=1){
			stack.getTagCompound().setFloat("UsedAmmo",stack.getTagCompound().getFloat("UsedAmmo")-1);
			amount++;
		}
		return amount;
    }
    
    public boolean endUse(ItemStack stack, EntityLivingBase living, World world, int oldState, int newState) {
		return false;
	}
    
    @Override
	public void onUpdate(ItemStack par1ItemStack, World par2World, Entity par3Entity, int par4, boolean par5)
   	{
    	if(!par1ItemStack.hasTagCompound()&&par3Entity instanceof EntityPlayer){
    		((EntityPlayer)par3Entity).inventory.setInventorySlotContents(par4, null);
    		par1ItemStack.stackSize=0;
    		return;
    	}
    	/*if(itemProperties.get(par2World.isRemote).get(par3Entity)==null){
			itemProperties.get(par2World.isRemote).put((EntityLivingBase) par3Entity, new NBTTagCompound());
		}*/
    	WeaponsCapability cap=WeaponsCapability.get(par3Entity);
    	
    	if(par1ItemStack.getTagCompound().getByte("active")==0&&par5){
			par1ItemStack.getTagCompound().setByte("active", (byte) 1);
			//itemProperties.get(par2World.isRemote).get(par3Entity).setShort("reloadd", (short) 800);
			this.draw(cap,par1ItemStack,(EntityLivingBase) par3Entity,par2World);
		}
		else if(par1ItemStack.getTagCompound().getByte("active")>0 && !par5){
			par1ItemStack.getTagCompound().setByte("active", (byte) 0);
			this.holster(cap,par1ItemStack,(EntityLivingBase) par3Entity,par2World);
			
			if(par1ItemStack.getTagCompound().getByte("active")==2&&(cap.state&3)>0)
				this.endUse(par1ItemStack,(EntityLivingBase) par3Entity,par2World,cap.state,0);
			
		}
    	if(par1ItemStack.getTagCompound().getByte("active")==1 && cap.fire1Cool==0){
    		par1ItemStack.getTagCompound().setByte("active", (byte) 2);
    		
    		/*if(par3Entity instanceof EntityPlayerMP){
    			TF2weapons.network.sendTo(new TF2Message.ActionMessage(22,(EntityLivingBase) par3Entity), (EntityPlayerMP)par3Entity);
    		}*/
    		
    		if((cap.state&3)>0){
    			cap.state=cap.state&7;
				if((cap.state&3)>0){
					this.startUse(par1ItemStack,(EntityLivingBase) par3Entity,par2World, 0, cap.state&3);
				}
			}
    	}
   		/*if(par1ItemStack.hasTagCompound()&&par1ItemStack.getTagCompound().getShort("lastfire")>0){
   			par1ItemStack.getTagCompound().setShort("lastfire", (short) (par1ItemStack.getTagCompound().getShort("lastfire") - 50));
   		}*/
   		/*if(!par5&&!(par3Entity instanceof EntityPlayer&&((EntityPlayer)par3Entity).capabilities.isCreativeMode)){
   			par1ItemStack.getTagCompound().setShort("reload", (short) (800));
   		}*/
   	}

    public void draw(WeaponsCapability weaponsCapability,ItemStack stack, EntityLivingBase living, World world) {
    	weaponsCapability.fire1Cool=750;
		weaponsCapability.fire2Cool=750;
	}
    public void holster(WeaponsCapability cap,ItemStack stack, EntityLivingBase living, World world) {
    	cap.chargeTicks=0;
    	cap.charging=false;

	}
	@Override
	public boolean onDroppedByPlayer(ItemStack item, EntityPlayer player)
    {
    	item.getTagCompound().removeTag("active");
    	WeaponsCapability.get(player).state=0;
    	this.holster(WeaponsCapability.get(player),item,player,player.worldObj);
        return true;
    }
	
	public boolean canFire(World world, EntityLivingBase living, ItemStack stack){
		return stack.getTagCompound().getByte("active")==2 && WeaponsCapability.get(living).invisTicks==0;
	}
	public abstract boolean fireTick(ItemStack stack, EntityLivingBase living, World world);
	public abstract boolean altFireTick(ItemStack stack, EntityLivingBase living, World world);
	
	
	
	/*public void registerIcons(IIconRegister par1IconRegister)
    {
		this.itemIcon = par1IconRegister.registerIcon(this.getIconString());
		if(addedIcons) return;
		Iterator<String> iterator=MapList.nameToCC.keySet().iterator();
		addedIcons=true;
		while(iterator.hasNext()){
			String name=iterator.next();
			//System.out.println(MapList.nameToCC.get(name).get("Render").getString()+" "+name);
			MapList.nameToIcon.put(name, par1IconRegister.registerIcon(MapList.nameToCC.get(name).get("Render").getString()));
		}
        //this.itemIcon = par1IconRegister.registerIcon(getData(stack).get("Render").getString());
    }*/
	/*@SideOnly(Side.CLIENT)
    public boolean requiresMultipleRenderPasses()
    {
        return true;
    }
	public int getRenderPasses(int metadata)
    {
        return 1;
    }*/
	
	
	
	public int getFiringSpeed(ItemStack stack,EntityLivingBase living){
		return (int) ( TF2Attribute.getModifier("Fire Rate", stack, ItemFromData.getData(stack).getInt(PropertyType.FIRE_SPEED),living));
	}
	
	@Override
	public boolean onBlockStartBreak(ItemStack itemstack, int x, int y, int z, EntityPlayer player)
    {
        return true;
    }
	public boolean canAltFire(World worldObj, EntityLivingBase player,
			ItemStack item) {
		// TODO Auto-generated method stub
		return WeaponsCapability.get(player).invisTicks==0;
	}
	public void altUse(ItemStack stack, EntityLivingBase living,
			World world) {
		
	}
	public short getAltFiringSpeed(ItemStack item, EntityLivingBase player) {
		return Short.MAX_VALUE;
	}
}
