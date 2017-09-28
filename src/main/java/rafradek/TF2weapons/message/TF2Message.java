package rafradek.TF2weapons.message;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.MovingObjectPosition;
import rafradek.TF2weapons.TF2Attribute;
import rafradek.TF2weapons.WeaponData;
import rafradek.TF2weapons.WeaponData.PropertyType;
import rafradek.TF2weapons.weapons.WeaponsCapability;

public abstract class TF2Message implements IMessage{
	
	public static class ActionMessage extends TF2Message{
		int value;
		int entity;
		public ActionMessage(){
			
		}
		public ActionMessage(int value,EntityLivingBase entity){
			this.value=value;
			this.entity=entity.getEntityId();
		}
		public ActionMessage(int value){
			this.value=value;
		}
		@Override
		public void fromBytes(ByteBuf buf) {
			this.value=buf.readByte();
			if(buf.readableBytes()>0){
				this.entity=buf.readInt();
			}
		}

		@Override
		public void toBytes(ByteBuf buf) {
			buf.writeByte(this.value);
			if(this.entity!=0){
				buf.writeInt(this.entity);
			}
		}
	}
	public static class UseMessage extends TF2Message{
		int value;
		boolean reload;
		public UseMessage(){
		}
		public UseMessage(int value,boolean reload){
			this.value=value;
			this.reload=reload;
		}
		@Override
		public void fromBytes(ByteBuf buf) {
			this.value=buf.readShort();
			this.reload=buf.readBoolean();
		}

		@Override
		public void toBytes(ByteBuf buf) {
			buf.writeShort(value);
			buf.writeBoolean(reload);
		}
		
	}
	public static class PredictionMessage extends TF2Message{
		public double x;
		public double y;
		public double z;
		public float pitch;
		public float yaw;
		//public int slot;
		public List<MovingObjectPosition> target;
		public List<Object[]> readData;
		public int state;
		
		public PredictionMessage(){
		}
		public PredictionMessage(double x,double y, double z, float pitch, float yaw,int state){
			this.x=x;
			this.y=y;
			this.z=z;
			this.pitch=pitch;
			this.yaw=yaw;
			this.state=state;
		}
		public PredictionMessage(double x,double y, double z, float pitch, float yaw,int state,List<MovingObjectPosition> target2){
			this.x=x;
			this.y=y;
			this.z=z;
			this.pitch=pitch;
			this.yaw=yaw;
			this.target=target2;
			this.state=state;
		}
		
		@Override
		public void fromBytes(ByteBuf buf) {
			this.x=buf.readDouble();
			this.y=buf.readDouble();
			this.z=buf.readDouble();
			this.pitch=buf.readFloat();
			this.yaw=buf.readFloat();
			this.state=buf.readByte();
			if(buf.readableBytes()>0){
				this.readData=new ArrayList<Object[]>();
				while(buf.readableBytes()>0){
					Object[] obj=new Object[3];
					obj[0]=buf.readInt();
					//obj[1]=buf.readFloat();
					//obj[2]=buf.readFloat();
					//obj[3]=buf.readFloat();
					obj[1]=buf.readBoolean();
					obj[2]=buf.readFloat();
					this.readData.add(obj);
				}
			}
		}

		@Override
		public void toBytes(ByteBuf buf) {
			buf.writeDouble(x);
			buf.writeDouble(y);
			buf.writeDouble(z);
			buf.writeFloat(pitch);
			buf.writeFloat(yaw);
			buf.writeByte(state);
			if(target!=null){
				for(MovingObjectPosition mop:target){
					buf.writeInt(mop.entityHit.getEntityId());
					//if(mop.hitVec!=null){
						//buf.writeFloat((float) mop.hitVec.xCoord);
						//buf.writeFloat((float) mop.hitVec.yCoord);
						//buf.writeFloat((float) mop.hitVec.zCoord);
					//}
					//buf.writeInt(mop.entityHit.getEntityId());
					buf.writeBoolean(((float[])mop.hitInfo)[0]==1);
					buf.writeFloat(((float[])mop.hitInfo)[1]);
				}
			}
		}
		
	}
	public static class PropertyMessage extends TF2Message{
		String name;
		int intValue;
		float floatValue;
		short shortValue;
		byte byteValue;
		String stringValue;
		int entityID;
		byte type;
		public PropertyMessage(){
		}
		public PropertyMessage(String name,Number value){
			this.name=name;
			if(value instanceof Integer){
				this.type=0;
				this.intValue=value.intValue();
			}
			else if(value instanceof Float){
				this.type=1;
				this.floatValue=value.floatValue();
			}
			else if(value instanceof Byte){
				this.type=2;
				this.byteValue=value.byteValue();
			}
		}
		public PropertyMessage(String name,Number value,Entity entity){
			this(name,value);
			this.entityID=entity.getEntityId();
		}
		public PropertyMessage(String name,Boolean value,Entity entity){
			this(name,value?(byte)1:(byte)0,entity);
			this.type=4;
		}
		public PropertyMessage(String name,String value,Entity entity){
			this.type=3;
			this.name=name;
			this.stringValue=value;
			this.entityID=entity.getEntityId();
		}
		@Override
		public void fromBytes(ByteBuf buf) {
			entityID=buf.readInt();
			type=buf.readByte();
			if(type==0){
				intValue=buf.readInt();
			}
			else if(type==1){
				floatValue=buf.readFloat();
			}
			else if(type==2||type==4){
				byteValue=buf.readByte();
			}
			else if(type==3){
				int stringLength=buf.readByte();
				stringValue=buf.toString(buf.readerIndex(),stringLength, StandardCharsets.UTF_8);
				buf.readerIndex(buf.readerIndex()+stringLength);
			}
			//value=buf.readInt();
			name=buf.toString(buf.readerIndex(),buf.readableBytes(), StandardCharsets.UTF_8);
		}

		@Override
		public void toBytes(ByteBuf buf) {
			buf.writeInt(entityID);
			buf.writeByte(type);
			if(type==0){
				buf.writeInt(intValue);
			}
			else if(type==1){
				buf.writeFloat(floatValue);
			}
			else if(type==2||type==4){
				buf.writeByte(byteValue);
			}
			else if(type==3){
				byte[] stringValueArray=stringValue.getBytes(StandardCharsets.UTF_8);
				buf.writeByte(stringValueArray.length);
				buf.writeBytes(stringValueArray);
			}
			byte[] stringNameArray=name.getBytes(StandardCharsets.UTF_8);
			buf.writeBytes(stringNameArray);
		}
		
	}
	public static class CapabilityMessage extends TF2Message{
		int healTarget;
		int entityID;
		int critTime;
		public CapabilityMessage(){
		}
		public CapabilityMessage(Entity entity){
			WeaponsCapability cap=WeaponsCapability.get(entity);
			this.entityID=entity.getEntityId();
			this.healTarget=cap.healTarget;
			this.critTime=cap.critTime;
			//new Exception().printStackTrace();
		}
		@Override
		public void fromBytes(ByteBuf buf) {
			entityID=buf.readInt();
			healTarget=buf.readInt();
			critTime=buf.readByte();
			/*try {
				tag=CompressedStreamTools.read(new ByteBufInputStream(buf), new NBTSizeTracker(2097152L));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}*/
		}

		@Override
		public void toBytes(ByteBuf buf) {
			buf.writeInt(entityID);
			buf.writeInt(healTarget);
			buf.writeByte(critTime);
			/*try {
				CompressedStreamTools.write(tag, new ByteBufOutputStream(buf));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}*/
		}
		
	}
	public static class BulletMessage extends TF2Message{
		//public int shooter;
		public ArrayList<MovingObjectPosition> target;
		public ArrayList<Object[]> readData;
		public int slot;
		public BulletMessage(){
			
		}
		public BulletMessage(int slot,ArrayList<MovingObjectPosition> target){
			//this.shooter=shooter.getEntityId();
			this.slot=slot;
			this.target=target;
		}
		@Override
		public void fromBytes(ByteBuf buf) {
			slot=buf.readByte();
			this.readData=new ArrayList<Object[]>();
			while(buf.readableBytes()>0){
				Object[] obj=new Object[3];
				obj[0]=buf.readInt();
				//obj[1]=buf.readFloat();
				//obj[2]=buf.readFloat();
				//obj[3]=buf.readFloat();
				obj[1]=buf.readBoolean();
				obj[2]=buf.readFloat();
				this.readData.add(obj);
			}
		}

		@Override
		public void toBytes(ByteBuf buf) {
			//buf.writeInt(shooter);
			buf.writeByte(this.slot);
			for(MovingObjectPosition mop:target){
				buf.writeInt(mop.entityHit.getEntityId());
				//if(mop.hitVec!=null){
					//buf.writeFloat((float) mop.hitVec.xCoord);
					//buf.writeFloat((float) mop.hitVec.yCoord);
					//buf.writeFloat((float) mop.hitVec.zCoord);
				//}
				//buf.writeInt(mop.entityHit.getEntityId());
				buf.writeBoolean(((float[])mop.hitInfo)[0]==1);
				buf.writeFloat(((float[])mop.hitInfo)[1]);
			}
			
		}
		
	}
	public static class GuiConfigMessage extends TF2Message{
		//public int shooter;
		int entityid;
		boolean isTile;
		byte id;
		boolean exit;
		boolean grab;
		int value;
		public GuiConfigMessage(){
			
		}
		public GuiConfigMessage(int entityid,byte id, boolean exit,boolean grab){
			//this.shooter=shooter.getEntityId();
			this.entityid=entityid;
			this.id=id;
			this.exit=exit;
			this.grab=grab;
		}
		public GuiConfigMessage(int windowID,byte id, int value){
			//this.shooter=shooter.getEntityId();
			this.isTile=true;
			this.id=id;
			this.value=value;
			this.entityid=windowID;
		}
		@Override
		public void fromBytes(ByteBuf buf) {
			id=buf.readByte();
			isTile=buf.readBoolean();
			if(this.isTile){
				entityid=buf.readInt();
				value=buf.readInt();
			}
			else{
				entityid=buf.readInt();
				exit=buf.readByte()!=0;
				grab=buf.readByte()!=0;
			}
		}

		@Override
		public void toBytes(ByteBuf buf) {
			buf.writeByte(id);
			buf.writeBoolean(isTile);
			if(this.isTile){
				buf.writeInt(entityid);
				buf.writeInt(value);
			}
			else{
				buf.writeInt(entityid);
				buf.writeByte(exit?1:0);
				buf.writeByte(grab?1:0);
			}
		}
		
	}
	public static class ShowGuiMessage extends TF2Message{
		//public int shooter;

		public int id;
		public ShowGuiMessage(){
			
		}
		public ShowGuiMessage(int id){
			//this.shooter=shooter.getEntityId();
			this.id=id;
		}
		@Override
		public void fromBytes(ByteBuf buf) {
			id=buf.readByte();
		}

		@Override
		public void toBytes(ByteBuf buf) {
			buf.writeByte(id);
			
		}
		
	}
	public static class WeaponDataMessage extends TF2Message{

		WeaponData weapon;
		public WeaponDataMessage(){
			
		}
		public WeaponDataMessage(WeaponData weapon){
			this.weapon=weapon;
		}
		@Override
		public void fromBytes(ByteBuf buf) {
			int stringLength=buf.readByte();
			weapon= new WeaponData(buf.toString(buf.readerIndex(),stringLength, StandardCharsets.UTF_8));
			buf.readerIndex(buf.readerIndex()+stringLength);
			int propertyCount=buf.readByte();
			for(int i=0; i<propertyCount; i++){
				int propId=buf.readByte();
				PropertyType prop=WeaponData.propertyTypes[propId];
				prop.deserialize(buf, weapon);
				
				//System.out.println("Property: "+prop.name+" "+weapon.properties.get(prop).stringValue);
				/*stringLength=buf.readByte();
				String propName=buf.toString(buf.readerIndex(),stringLength, StandardCharsets.UTF_8);
				buf.readerIndex(buf.readerIndex()+stringLength);
				Property.Type type=Property.Type.values()[buf.readByte()];
				int listLength=buf.readByte();

				if(listLength>0){
					String[] values=new String[listLength];
					for(int i=0;i<listLength;i++){
						stringLength=buf.readByte();
						values[i]=buf.toString(buf.readerIndex(),stringLength, StandardCharsets.UTF_8);
						buf.readerIndex(buf.readerIndex()+stringLength);
					}
					weapon.put(propName, new Property(propName,values,type));
				}
				else{
					stringLength=buf.readByte();
					weapon.put(propName, new Property(propName,buf.toString(buf.readerIndex(),stringLength, StandardCharsets.UTF_8),type));
					buf.readerIndex(buf.readerIndex()+stringLength);
				}*/
			}
			while(buf.readableBytes()!=0){
				if(weapon.getString(PropertyType.CLASS).equals("crate")){
					stringLength=buf.readByte();
					String entry=buf.toString(buf.readerIndex(),stringLength, StandardCharsets.UTF_8);
					buf.readerIndex(buf.readerIndex()+stringLength);
					weapon.crateContent.put(entry,buf.readInt());
					
				}
				else{
					weapon.attributes.put(TF2Attribute.attributes[buf.readByte()], buf.readFloat());
				}
			}
			
		}

		@Override
		public void toBytes(ByteBuf buf) {
			byte[] stringNameArray=weapon.getName().getBytes(StandardCharsets.UTF_8);
			buf.writeByte(stringNameArray.length);
			buf.writeBytes(stringNameArray);
			buf.writeByte(weapon.properties.size());
			for(PropertyType type:weapon.properties.keySet()){
				type.serialize(buf, weapon);
				/*byte[] stringKeyArray=stringKe.getKey().getBytes(StandardCharsets.UTF_8);
				buf.writeByte(stringKeyArray.length);
				buf.writeBytes(stringKeyArray);
				buf.writeByte(entry.getValue().getType().ordinal());
				buf.writeByte(entry.getValue().isList()?entry.getValue().getStringList().length:0);
				if(entry.getValue().isList()){
					for(String string:entry.getValue().getStringList()){
						byte[] stringValueArray=string.getBytes(StandardCharsets.UTF_8);
						buf.writeByte(stringValueArray.length);
						buf.writeBytes(stringValueArray);
					}
				}
				else{
					byte[] stringValueArray=entry.getValue().getString().getBytes(StandardCharsets.UTF_8);
					buf.writeByte(stringValueArray.length);
					buf.writeBytes(stringValueArray);
				}*/
			}
			for(Entry<TF2Attribute, Float> attr:weapon.attributes.entrySet()){
				buf.writeByte(attr.getKey().id);
				buf.writeFloat(attr.getValue());
			}
			for(Entry<String, Integer> entry:weapon.crateContent.entrySet()){
				byte[] stringContentArray=entry.getKey().getBytes(StandardCharsets.UTF_8);
				buf.writeByte(stringContentArray.length);
				buf.writeBytes(stringContentArray);
				buf.writeFloat(entry.getValue());
			}
		}
		
	}
	public static class WearableChangeMessage extends TF2Message{
		//public int shooter;

		public int slot;
		public int entityID;
		public ItemStack stack;
		public WearableChangeMessage(){
			
		}
		public WearableChangeMessage(EntityPlayer player, int slot, ItemStack stack){
			//this.shooter=shooter.getEntityId();
			this.slot=slot;
			this.entityID=player.getEntityId();
			this.stack=stack;
		}
		@Override
		public void fromBytes(ByteBuf buf) {
			slot=buf.readByte();
			entityID=buf.readInt();
			try {
				stack=new PacketBuffer(buf).readItemStackFromBuffer();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		@Override
		public void toBytes(ByteBuf buf) {
			buf.writeByte(slot);
			buf.writeInt(entityID);
			try {
				new PacketBuffer(buf).writeItemStackToBuffer(stack);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
		
	}
}
