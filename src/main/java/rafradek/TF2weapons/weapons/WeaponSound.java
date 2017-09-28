package rafradek.TF2weapons.weapons;

import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.MovingSound;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.ResourceLocation;
import rafradek.TF2weapons.ClientProxy;
import rafradek.TF2weapons.ItemFromData;
import rafradek.TF2weapons.WeaponData;

public class WeaponSound extends MovingSound {
	public EntityLivingBase entity;
	public int type;
	public WeaponData conf;
	public boolean endsnextTick;
	public WeaponSound playOnEnd;
	public WeaponSound(ResourceLocation p_i45104_1_,EntityLivingBase entity,int type,WeaponData conf) {
		super(p_i45104_1_);
		this.type=type;
		this.entity=entity;
		this.conf=conf;
		this.volume=0.55f;
	}
	@Override
	public void update() {
		// TODO Auto-generated method stub
		if(this.endsnextTick){
			this.setDone();
		}
		this.xPosF=(float) entity.posX;
		this.yPosF=(float) entity.posY;
		this.zPosF=(float) entity.posZ;
		if(/*!(entity instanceof EntityPlayer && ((EntityPlayer)entity).inventory.currentItem != slot)*/ItemFromData.getData(entity.getHeldItem())!=conf || this.entity.isDead){
			this.setDone();
		}
	}
	public void setDone(){
		ClientProxy.fireSounds.remove(entity);
		if(this.playOnEnd!=null){
			Minecraft.getMinecraft().getSoundHandler().playSound(playOnEnd);
			ClientProxy.fireSounds.put(entity, playOnEnd);
		}
		this.donePlaying=true;
	}
}
