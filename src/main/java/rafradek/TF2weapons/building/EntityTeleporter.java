package rafradek.TF2weapons.building;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import rafradek.TF2weapons.ClientProxy;
import rafradek.TF2weapons.TF2weapons;

public class EntityTeleporter extends EntityBuilding {
	//public static ArrayList<BlockPosDimension> teleportersData=new ArrayList<BlockPosDimension>();
	public static final int TP_PER_PLAYER=64;
	
	public static int tpCount=0;
	public static HashMap<UUID, TeleporterData[]> teleporters=new HashMap<UUID, TeleporterData[]>();
	
	public int tpID=-1;
	public int ticksToTeleport;
	
	public EntityTeleporter linkedTp;
	
	public float spin;
	public float spinRender;
	
	public EntityTeleporter(World worldIn) {
		super(worldIn);
		this.setSize(1f, 0.2f);
	}
	public EntityTeleporter(World worldIn,EntityLivingBase living) {
		super(worldIn,living);
		this.setSize(1f, 0.2f);
	}
	@Override
	public AxisAlignedBB getCollisionBox(Entity entityIn)
    {
		if(!this.worldObj.isRemote&&!this.isExit()&&this.getTPprogress()<=0&& entityIn!=null&&entityIn instanceof EntityLivingBase&&!(entityIn instanceof EntityBuilding)
				&&((TF2weapons.dispenserHeal && getTeam()==null && ((EntityLivingBase) entityIn).getTeam() == null)||TF2weapons.isOnSameTeam(EntityTeleporter.this,entityIn))
				&&entityIn.boundingBox.intersectsWith(this.boundingBox.expand(0, 0.5, 0).offset(0, 0.5D, 0))){
			
			if(ticksToTeleport<=0){
				if(ticksToTeleport<0){
					ticksToTeleport=10;
				}
				else{
					TeleporterData exit=this.getTeleportExit();
					if(exit !=null){
						if(exit.dimension!=this.dimension)
							entityIn.travelToDimension(exit.dimension);
						((EntityLivingBase) entityIn).setPositionAndUpdate(exit.x+0.5, exit.y+0.23, exit.z+0.5);
						this.setTeleports(this.getTeleports()+1);
						this.setTPprogress(this.getLevel()==1?200:(this.getLevel()==2?100:60));
						this.worldObj.playSoundAtEntity(this, "rafradek_tf2_weapons:mob.teleporter.send", 0.75f, 1f);
						entityIn.worldObj.playSoundAtEntity(entityIn, "rafradek_tf2_weapons:mob.teleporter.receive", 0.75f, 1f);
					}
				}
			}
		}
		return super.getCollisionBox(entityIn);
    }
	@Override
	public void onLivingUpdate(){
		super.onLivingUpdate();
		if(!this.worldObj.isRemote){
			if(this.tpID==-1){
				tpCount++;
				this.tpID=tpCount;
			}
			/*if(teleporters.get(id)==null){
				teleporters.put(id, new TeleporterData[TP_PER_PLAYER]);
				System.out.println("putting data");
			}*/
			if(!this.isExit()){
				ticksToTeleport--;
				if(this.getTPprogress()>0){
					this.setTPprogress(this.getTPprogress()-1);
				}
				if(this.getSoundState()==1&&(this.getTPprogress()>0||this.getTeleportExit()==null)){
					this.setSoundState(0);
					if(this.linkedTp!=null){
						this.linkedTp.setSoundState(0);
					}
					
				}
				if(this.getSoundState()==0&&this.getTPprogress()<=0&&this.getTeleportExit()!=null){
					this.setSoundState(1);
					if(this.linkedTp!=null){
						this.linkedTp.setSoundState(1);
					}
				}
				/*if(ticksToTeleport<=0){
					List<EntityLivingBase> targetList=this.worldObj.getEntitiesWithinAABB(EntityLivingBase.class, this.boundingBox.expand(0, 0.5, 0).offset(0, 0.5D, 0), new Predicate<EntityLivingBase>(){
		
						@Override
						public boolean apply(EntityLivingBase input) {
							
							return !(input instanceof EntityBuilding)&&EntityTeleporter.this!=input&&((TF2weapons.dispenserHeal&&input instanceof EntityPlayer && getTeam()==null && input.getTeam() == null)||TF2weapons.isOnSameTeam(EntityTeleporter.this,input));
						}
						
					});
					
					if(!targetList.isEmpty()){
						if(ticksToTeleport<0){
							ticksToTeleport=10;
						}
						else{
							BlockPosDimension exit=this.getTeleportExit();
							if(exit !=null){
								if(exit.dimension!=this.dimension)
									targetList.get(0).travelToDimension(exit.dimension);
								targetList.get(0).setPositionAndUpdate(exit.getX()+0.5, exit.getY()+0.23, exit.getZ()+0.5);
							}
						}
					}
				}*/
			}
			else if(teleporters.get(UUID.fromString(this.getOwnerId()))[this.getID()]==null||teleporters.get(UUID.fromString(this.getOwnerId()))[this.getID()].id!=this.tpID){
				this.setExit(false);
				this.updateTeleportersData(true);
			}
			else if(teleporters.get(UUID.fromString(this.getOwnerId()))[this.getID()].id==this.tpID&&!(this.isTeleporterData(teleporters.get(UUID.fromString(this.getOwnerId()))[this.getID()]))){
				this.updateTeleportersData(false);
			}
		}
		else{
			if(this.getSoundState()==1){
				this.spin+=(float)Math.PI*(this.getLevel()==1?0.25f:(this.getLevel()==2?0.325f:0.4f));
			}
			else{
				this.spin=0;
			}
		}
	}
	public TeleporterData getTeleportExit(){
		UUID uuid=UUID.fromString(this.getOwnerId());
		if(this.getOwnerId()!=null&&teleporters.get(uuid)!=null){
			final TeleporterData data=teleporters.get(uuid)[this.getID()];
			List<Entity> list=worldObj.getLoadedEntityList();/*{

				@Override
				public boolean apply(EntityTeleporter input) {
					// TODO Auto-generated method stub
					return data != null && data.id==input.tpID;
				}
				
			});*/
			for(Entity ent:list){
				if(data != null && ent instanceof EntityTeleporter && data.id==((EntityTeleporter)ent).tpID){
					this.linkedTp=(EntityTeleporter) ent;
				}
			}
				
				
				//System.out.println("linkedtpset");
			return data;
		}
		return null;
	}
	@Override
	public String getSoundNameForState(int state){
		switch(state){
		case 0:return null;
		case 1:return "rafradek_tf2_weapons:mob.teleporter.spin."+this.getLevel();
		default:return null;
		}
	}
	@Override
	public boolean interact(EntityPlayer player)
    {
		if(this.worldObj.isRemote &&player==this.getOwner()){
			ClientProxy.showGuiTeleporter(this);
		}
		return false;
	}
	@Override
	protected void entityInit()
	{
		super.entityInit();
		this.getDataWatcher().addObject(16, (short)0);
		this.getDataWatcher().addObject(17, (byte)0);
		this.getDataWatcher().addObject(18, (byte)0);
		this.getDataWatcher().addObject(19, (short)0);
	}
	public boolean isExit(){
		return this.getDataWatcher().getWatchableObjectByte(18)==1;
	}
	public void setExit(boolean exit){
		//if(this.getOwner()!=null&&exit&&teleporters.get(UUID.fromString(this.getOwnerId()))[this.getID()]!=null) return;
		this.getDataWatcher().updateObject(18, exit?(byte)1:(byte)0);
		if(!this.getOwnerId().isEmpty()){
			this.updateTeleportersData(false);
		}
	}
	public int getID(){
		return this.getDataWatcher().getWatchableObjectByte(17);
	}
	public void setID(int id){
		if((id>=TP_PER_PLAYER||id<0)/*||teleporters.get(UUID.fromString(this.getOwnerId()))[id]!=null*/) return;
		if(!this.getOwnerId().isEmpty()){
			this.updateTeleportersData(true);
		}
		this.getDataWatcher().updateObject(17, (byte)id);
		if(!this.getOwnerId().isEmpty()){
			this.updateTeleportersData(false);
		}
	}
	public int getTPprogress(){
		return this.getDataWatcher().getWatchableObjectShort(16);
	}
	public void setTPprogress(int progress){
		/*if(progress<=0&&this.getDataWatcher().getWatchableObjectShort(16)>0&&this.getTeleportExit()!=null){
			this.setSoundState(0);
			this.worldObj.playSoundAtEntity(this, "rafradek_tf2_weapons:mob.teleporter.ready", 0.75f, 1f);
		}*/
		/*if(progress>0&&this.getDataWatcher().getWatchableObjectShort(16)<=0)
			this.setSoundState(1);*/
		
		this.getDataWatcher().updateObject(16, (short)progress);
	}
	public void setTeleports(int amount) {
		// TODO Auto-generated method stub
		this.getDataWatcher().updateObject(19, (short)amount);
	}
	public int getTeleports() {
		// TODO Auto-generated method stub
		return this.getDataWatcher().getWatchableObjectShort(19);
	}
	/*public void setOwner(EntityLivingBase owner) {
		super.setOwner(owner);
		if(owner instanceof EntityPlayer){
			this.dataManager.set(key, value);14, owner.getUniqueID().toString());
		}
	}*/
	@Override
	public void upgrade(){
		super.upgrade();
	}
	@Override
	protected String getHurtSound()
    {
        return null;
    }
	@Override
	public void setDead(){
		//Chunk chunk=this.worldObj.getChunkFromBlockCoords(this.getPosition());
		//if(chunk.isLoaded()){
		if(!this.worldObj.isRemote)
			this.updateTeleportersData(true);
		//}
		//System.out.println("teleporter removed: "+chunk.isLoaded()+" "+chunk.isEmpty()+" "+chunk.isPopulated());
		super.setDead();
	}
	public void setOwner(EntityLivingBase owner) {
		// TODO Auto-generated method stub
		super.setOwner(owner);
		UUID id=owner.getUniqueID();
		if(!teleporters.containsKey(id)){
			teleporters.put(id, new TeleporterData[TP_PER_PLAYER]);
		}
	}
	@Override
	public void func_145781_i(int data){
		super.func_145781_i(data);
		if(!this.worldObj.isRemote){
			if(data==14){
				UUID id=UUID.fromString(this.dataWatcher.getWatchableObjectString(14));
				if(!teleporters.containsKey(id)){
					teleporters.put(id, new TeleporterData[TP_PER_PLAYER]);
				}
			}
		}
	}
	public void updateTeleportersData(boolean forceremove){
		if(this.worldObj.isRemote) return;
		UUID id=UUID.fromString(this.getOwnerId());
		if(teleporters.get(id)==null){
			teleporters.put(id, new TeleporterData[TP_PER_PLAYER]);
		}
		if(!forceremove&&this.isExit()/*&&teleporters.get(id)[this.getID()]==null*/){
			teleporters.get(id)[this.getID()]=new TeleporterData(MathHelper.floor_double(this.posX),MathHelper.floor_double(this.posY),MathHelper.floor_double(this.posZ),this.tpID,this.dimension);
		}else if(this.isExit()||(!this.isExit()&& this.isTeleporterData(teleporters.get(id)[this.getID()]))){
			teleporters.get(id)[this.getID()]=null;
		}
	}
	@Override
	protected String getDeathSound()
    {
        return "rafradek_tf2_weapons:mob.teleporter.death";
    }
    @Override
	public float getCollHeight(){
		return 0.2f;
	}
	@Override
	public float getCollWidth(){
		return 1f;
	}
	@Override
	public void writeEntityToNBT(NBTTagCompound par1NBTTagCompound)
    {
        super.writeEntityToNBT(par1NBTTagCompound);
        
        par1NBTTagCompound.setByte("TeleExitID", (byte) this.getID());
        par1NBTTagCompound.setBoolean("TeleExit", this.isExit());
        par1NBTTagCompound.setInteger("TeleID", this.tpID);
        par1NBTTagCompound.setShort("Teleports", (short) this.getTeleports());
    }
	@Override
	public void readEntityFromNBT(NBTTagCompound par1NBTTagCompound)
    {
        super.readEntityFromNBT(par1NBTTagCompound);
        
        this.tpID=par1NBTTagCompound.getInteger("TeleID");
        this.setTeleports(par1NBTTagCompound.getShort("Teleports"));
        this.setID(par1NBTTagCompound.getByte("TeleExitID"));
        this.setExit(par1NBTTagCompound.getBoolean("TeleExit"));
    }
	public boolean isTeleporterData(TeleporterData data){
		return data !=null && data.x==MathHelper.floor_double(posX) && data.y==MathHelper.floor_double(posY) && data.z==MathHelper.floor_double(posZ);
	}
	public static class TeleporterData{

		public final int x;
		public final int y;
		public final int z;
		public final int dimension;
		public final int id;
		public TeleporterData(int x, int y, int z,int id,int dimension) {
			this.x=x;
			this.y=y;
			this.z=z;
			this.id=id;
			this.dimension=dimension;
		}
		
	}
	
}
