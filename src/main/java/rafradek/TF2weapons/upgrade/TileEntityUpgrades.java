package rafradek.TF2weapons.upgrade;

import java.util.HashMap;
import java.util.List;
import java.util.Set;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import rafradek.TF2weapons.TF2Attribute;

public class TileEntityUpgrades extends TileEntity{
	
	public static final int UPGRADES_COUNT=8;
	public HashMap<TF2Attribute, Integer> attributes=new HashMap<>();
	public TF2Attribute[] attributeList=new TF2Attribute[UPGRADES_COUNT];
	
	public TileEntityUpgrades(){
		super();
	}
	public TileEntityUpgrades(World world){
		this.worldObj=world;
		if(!worldObj.isRemote){
			this.generateUpgrades();
		}
	}
    /**
     * Removes up to a specified number of items from an inventory slot and returns them in a new stack.
     */
	public void generateUpgrades(){
		//System.out.println("Max Size: "+MapList.nameToAttribute.size());
		List<TF2Attribute> passAttributes=TF2Attribute.getAllPassibleAttributesForUpgradeStation();
		for(int i=0;i<UPGRADES_COUNT;i++){
			while(true){
				TF2Attribute attr=passAttributes.get(this.worldObj.rand.nextInt(passAttributes.size()));
				if(!this.attributes.containsKey(attr)){
					attributeList[i]=attr;
					this.attributes.put(attr,Math.max(1,attr.numLevels-(i<2?0:1)));
					break;
				}
			}
		}
		//this.worldObj.markAndNotifyBlock(pos, this.worldObj.getChunkFromBlockCoords(getPos()), this.worldObj.getBlockState(getPos()), this.worldObj.getBlockState(pos), 2);
		this.markDirty();
	}
	
    @Override
	public void readFromNBT(NBTTagCompound compound)
    {
        super.readFromNBT(compound);
        if(compound.hasKey("Attributes")){
        	NBTTagCompound attrs=compound.getCompoundTag("Attributes");
        	for(String key:(Set<String>)attrs.func_150296_c()){
        		this.attributes.put(TF2Attribute.attributes[Integer.parseInt(key)], attrs.getInteger(key));
        	}
        	for(int i=0;i<compound.getIntArray("AttributesList").length;i++){
        		this.attributeList[i]=TF2Attribute.attributes[compound.getIntArray("AttributesList")[i]];
        	}
        	//System.out.println("Reading");
        }
    }

    @Override
	public void writeToNBT(NBTTagCompound compound)
    {
        super.writeToNBT(compound);
        NBTTagCompound attrs=new NBTTagCompound();
        int[] arr=new int[this.attributeList.length];
        compound.setTag("Attributes", attrs);
        for(int i=0;i<this.attributeList.length;i++){
        	TF2Attribute attr=this.attributeList[i];
        	if(attr!=null){
        		
	        	arr[i]=attr.id;
	        	attrs.setInteger(String.valueOf(attr.id),this.attributes.get(attr));
        	}
        }
        compound.setIntArray("AttributesList", arr);
    }
    
    @Override
	public Packet getDescriptionPacket()
    {
    	//System.out.println("Sending packet");
    	NBTTagCompound nbttagcompound = new NBTTagCompound();
        this.writeToNBT(nbttagcompound);
        return new S35PacketUpdateTileEntity(this.xCoord,this.yCoord,this.zCoord, 29, nbttagcompound);
    }

    
    @Override
	public void onDataPacket(net.minecraft.network.NetworkManager net, S35PacketUpdateTileEntity pkt)
    {
    	//System.out.println("Received: "+pkt.getNbtCompound());
    	this.readFromNBT(pkt.func_148857_g());
    }
}
