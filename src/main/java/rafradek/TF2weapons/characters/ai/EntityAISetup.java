package rafradek.TF2weapons.characters.ai;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.RandomPositionGenerator;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;
import rafradek.TF2weapons.TF2weapons;
import rafradek.TF2weapons.building.EntityBuilding;
import rafradek.TF2weapons.building.EntityDispenser;
import rafradek.TF2weapons.building.EntitySentry;
import rafradek.TF2weapons.characters.EntityEngineer;
import rafradek.TF2weapons.weapons.ItemWrench;

public class EntityAISetup extends EntityAIBase {

	public EntityEngineer engineer;
	public int buildType;
	public boolean found;
	public Vec3 target;
	public EntityAISetup(EntityEngineer engineer){
		this.engineer=engineer;
		this.setMutexBits(3);
	}
	@Override
	public boolean shouldExecute() {

		if(this.engineer.isInWater()||this.engineer.getHeldItem()==null||this.engineer.getHeldItem().getItem() instanceof ItemWrench)
			return false;
		
		buildType=(this.engineer.metal>=130&&(this.engineer.sentry==null||this.engineer.sentry.isDead))?1:(this.engineer.metal>=100&&(this.engineer.dispenser==null||this.engineer.dispenser.isDead))?2:0;
		//System.out.println("Promote: "+buildType);
		if(buildType>0){
			this.engineer.setCurrentItemOrArmor(0,new ItemStack(TF2weapons.itemBuildingBox,1,16+buildType*2+this.engineer.getEntTeam()));
			this.engineer.getHeldItem().setTagCompound(new NBTTagCompound());
		}
		return buildType>0;
	}
	@Override
	public void resetTask()
    {
    	this.engineer.getNavigator().clearPathEntity();
    	this.engineer.switchSlot(0);
    }
	@Override
	public void updateTask()
    {
		if(this.buildType==1){
			if(this.target!=null&&this.engineer.getDistanceSq(this.target.xCoord,this.target.yCoord, this.target.zCoord)<2){
				this.engineer.sentry=(EntitySentry) this.spawn();
				return;
			}
			if(this.engineer.getNavigator().noPath()){
				
				Vec3 Vec3 = RandomPositionGenerator.findRandomTarget(this.engineer, 3, 2);
				if(Vec3!=null){
					AxisAlignedBB box=AxisAlignedBB.getBoundingBox(Vec3.xCoord-0.5, Vec3.yCoord, Vec3.zCoord-0.5, Vec3.xCoord+0.5, Vec3.yCoord+1, Vec3.zCoord+0.5);
					List<AxisAlignedBB> list=this.engineer.worldObj.getCollidingBoundingBoxes(this.engineer, box);
					
					if (list.isEmpty()&&!this.engineer.worldObj.isMaterialInBB(box,Material.water))
		            {
		                this.engineer.getNavigator().tryMoveToXYZ(Vec3.xCoord, Vec3.yCoord, Vec3.zCoord, 1);
		                this.target=Vec3;
		            }
				}
				/*for(AxisAlignedBB entry:list){
					System.out.println(entry);
				}*/
				
	            
				
			}
		}
		else if(this.buildType==2&&this.engineer.getNavigator().noPath()){
			if(this.target!=null&&this.engineer.getDistanceSq(this.target.xCoord,this.target.yCoord, this.target.zCoord)<2){
				this.engineer.dispenser= (EntityDispenser) this.spawn();
				return;
			}
			if(this.engineer.getNavigator().noPath()){
				
				Vec3 Vec3 = RandomPositionGenerator.findRandomTarget((this.engineer.sentry!=null&&!this.engineer.sentry.isDead)?this.engineer.sentry:(EntityCreature) this.engineer, 2, 1);
				if(Vec3!=null){
					AxisAlignedBB box=AxisAlignedBB.getBoundingBox(Vec3.xCoord-0.5, Vec3.yCoord, Vec3.zCoord-0.5, Vec3.xCoord+0.5, Vec3.yCoord+1, Vec3.zCoord+0.5);
					List<AxisAlignedBB> list=this.engineer.worldObj.getCollidingBoundingBoxes(this.engineer, box);
					/*for(AxisAlignedBB entry:list){
						System.out.println(entry);
					}*/
					if (list.isEmpty()&&!this.engineer.worldObj.isMaterialInBB(box,Material.water))
		            {
		                this.engineer.getNavigator().tryMoveToXYZ(Vec3.xCoord, Vec3.yCoord, Vec3.zCoord, 1);
		                this.target=Vec3;
		            }
				}
			}
		}
    }
	public EntityBuilding spawn(){
		EntityBuilding building;
		if(buildType==1){
			building=new EntitySentry(this.engineer.worldObj,this.engineer);
		}
		else {
			building=new EntityDispenser(this.engineer.worldObj,this.engineer);
		}
		Block blockTarget=this.engineer.worldObj.getBlock(MathHelper.floor_double(target.xCoord), MathHelper.floor_double(target.yCoord), MathHelper.floor_double(target.zCoord));
		if(!blockTarget.isCollidable()){
			building.setPosition(target.xCoord,target.yCoord+1.3,target.zCoord);
		}
		else{
			building.setPosition(target.xCoord,target.yCoord+0.3,target.zCoord);
		}
		building.setEntTeam(this.engineer.getEntTeam());
		this.engineer.worldObj.spawnEntityInWorld(building);
		this.target=null;
		this.buildType=0;
		this.resetTask();
		return building;
	}
}
