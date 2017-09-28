package rafradek.TF2weapons.characters;

import java.nio.charset.StandardCharsets;

import cpw.mods.fml.common.registry.IEntityAdditionalSpawnData;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

public class EntityStatue extends Entity implements IEntityAdditionalSpawnData{

	public EntityLivingBase entity;
	private float renderYawOffset;
	private float rotationYawHead;
	private String entityClass;
	private float limbSwingAmount;
	private float limbSwing;
	public boolean isFalling;
	public EntityStatue(World worldIn) {
		super(worldIn);
		// TODO Auto-generated constructor stub
	}
	
	public EntityStatue(World worldIn, EntityLivingBase toCopy) {
		super(worldIn);
		this.setPosition(toCopy.posX, toCopy.posY, toCopy.posZ);
		this.width=toCopy.width;
		this.height=toCopy.height;
		this.entity=toCopy;
		this.entity.deathTime=0;
		this.entity.hurtTime=0;
		/*this.renderYawOffset=toCopy.renderYawOffset;
		this.rotationYawHead=toCopy.rotationYawHead;
		this.rotationYaw=toCopy.rotationYaw;
		this.rotationPitch=toCopy.rotationPitch;
		this.limbSwingAmount=toCopy.limbSwingAmount;
		this.limbSwing=toCopy.limbSwing;
		this.entityClass=toCopy.getClass().getName();
		this.data=toCopy.getDataManager();*/
	}
	
	@Override
	protected void entityInit() {
		// TODO Auto-generated method stub

	}

	@Override
	public void onUpdate(){
		this.motionX*=0.98f;
		this.motionY*=0.98f;
		this.motionZ*=0.98f;
		this.motionY-=0.08f;
		this.moveEntity(this.motionX, this.motionY, this.motionZ);
	}
	
	@Override
	protected void readEntityFromNBT(NBTTagCompound compound) {
		// TODO Auto-generated method stub

	}

	@Override
	protected void writeEntityToNBT(NBTTagCompound compound) {
		// TODO Auto-generated method stub

	}

	@Override
	public void writeSpawnData(ByteBuf buffer) {
		buffer.writeByte(this.entityClass.length());
		buffer.writeBytes(this.entityClass.getBytes(StandardCharsets.UTF_8));
		buffer.writeFloat(renderYawOffset);
		buffer.writeFloat(rotationYawHead);
		buffer.writeFloat(rotationYaw);
		buffer.writeFloat(rotationPitch);
		buffer.writeFloat(limbSwingAmount);
		buffer.writeFloat(limbSwing);

	}

	@Override
	public void readSpawnData(ByteBuf additionalData) {
		String entityClass=additionalData.toString(1, additionalData.readByte(),StandardCharsets.UTF_8);
	}

}
