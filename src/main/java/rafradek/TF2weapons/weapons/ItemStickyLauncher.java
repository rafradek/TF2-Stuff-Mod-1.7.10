package rafradek.TF2weapons.weapons;

import java.util.ArrayList;
import java.util.HashMap;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import rafradek.TF2weapons.ClientProxy;
import rafradek.TF2weapons.ItemFromData;
import rafradek.TF2weapons.WeaponData.PropertyType;
import rafradek.TF2weapons.message.TF2Message.PredictionMessage;
import rafradek.TF2weapons.projectiles.EntityStickybomb;

public class ItemStickyLauncher extends ItemProjectileWeapon {
	
	public static HashMap<EntityLivingBase, ArrayList<EntityStickybomb>> activeBombs=new HashMap<EntityLivingBase, ArrayList<EntityStickybomb>>();
	
	@Override
	public boolean use(ItemStack stack, EntityLivingBase living, World world,PredictionMessage message){
		WeaponsCapability cap=WeaponsCapability.get(living);
		if(cap.charging){
			//System.out.println("Firing");
			super.use(stack, living, world, message);
		}
		else{
			//System.out.println("Start charging");
			cap.charging=true;
			cap.chargeTicks=0;
			if(world.isRemote){
				ClientProxy.playWeaponSound(living, ItemFromData.getSound(stack,PropertyType.CHARGE_SOUND), false, 0, stack);
			}
		}
		return true;
	}
	public boolean usePrediction(){
		return false;
	}
	@Override
    public boolean canFire(World world, EntityLivingBase living, ItemStack stack)
    {
		return !WeaponsCapability.get(living).charging&&super.canFire(world, living, stack);
    }
	@Override
	public float getProjectileSpeed(ItemStack stack,EntityLivingBase living){
		return super.getProjectileSpeed(stack, living)*(1+WeaponsCapability.get(living).chargeTicks*0.02f);
	}
	@Override
	public void shoot(ItemStack stack, EntityLivingBase living, World world,
			int thisCritical) {
		if(!world.isRemote){
			EntityStickybomb bomb = new EntityStickybomb(world,living);
			bomb.setCritical(thisCritical);
			world.spawnEntityInWorld(bomb);
			ArrayList<EntityStickybomb> list=activeBombs.get(living);
			if(list == null){
				activeBombs.put(living, list=new ArrayList<EntityStickybomb>());
			}
			list.add(bomb);
			if(list.size()>8){
				EntityStickybomb firstBomb=list.get(0);
				firstBomb.explode(firstBomb.posX, firstBomb.posY, firstBomb.posZ, null, 1);
			}
		}
	}
	@Override
	public boolean endUse(ItemStack stack, EntityLivingBase living, World world,int action, int newState) {
		/*if(!world.isRemote&&(!ClientProxy.fireSounds.containsKey(living)||ClientProxy.fireSounds.get(living).type!=3)){
			worl
		}*/
		WeaponsCapability cap=WeaponsCapability.get(living);
		if((newState&1)==0&&cap.charging){
			//System.out.println("stop charging "+newState);
			cap.charging=false;
			cap.fire1Cool=this.getFiringSpeed(stack,living);
			
			if(world.isRemote&&ClientProxy.fireSounds.get(living)!=null){
					ClientProxy.fireSounds.get(living).setDone();
					//Minecraft.getMinecraft().getSoundHandler().stopSound(ClientProxy.fireSounds.get(living));
			}
			super.use(stack, living, world, null);
			cap.lastFire=1250;
			if(world.isRemote)
			sps++;
			cap.reloadCool=0;
	    	if((cap.state&8)!=0){
	    		cap.state-=8;
	    	}
		}
		return false;
	}
	@Override
	public boolean fireTick(ItemStack stack, EntityLivingBase living, World world) {
		WeaponsCapability cap=WeaponsCapability.get(living);
		if(cap.charging){
			//System.out.println("charging "+tag.getShort("chargeticks"));
			if(cap.chargeTicks<80){
				cap.chargeTicks+=1;
			}
			else{
				this.endUse(stack, living, world, 1, 0);
			}
		}
		return false;
	}
	@Override
	public boolean altFireTick(ItemStack stack, EntityLivingBase living, World world) {
		if(!world.isRemote){
			ArrayList<EntityStickybomb> list=activeBombs.get(living);
			if(list == null || list.isEmpty()){
				return false;
			}
			boolean exploded=false;
			
			for(int i=0;i<list.size();i++){
				EntityStickybomb bomb=list.get(i);
				if(bomb.ticksExisted>16){
					bomb.explode(bomb.posX, bomb.posY+bomb.height/2, bomb.posZ, null, 1);
					i--;
					exploded=true;
				}
			}
			if(exploded){
				living.playSound(ItemFromData.getSound(stack,PropertyType.DETONATE_SOUND), 0.55f, 1);
			}
		}
		return false;
	}
	@Override
	public void holster(WeaponsCapability cap,ItemStack stack, EntityLivingBase living, World world) {
		super.holster(cap, stack, living, world);
		cap.chargeTicks=0;
		cap.charging=false;
	}
}
