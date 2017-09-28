package rafradek.TF2weapons.weapons;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.EntityFX;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import rafradek.TF2weapons.TF2EventBusListener;
import rafradek.TF2weapons.TF2weapons;

public class EntityBulletTracer extends EntityFX {
	
	private int duration;
	private boolean nextDead;

	public EntityBulletTracer(World par1World, double startX, double startY, double startZ, double x, double y, double z, int duration,int crits,EntityLivingBase shooter) {
		super(par1World, startX, startY, startZ);
		this.particleScale=0.2f;
		this.duration=duration;
		this.motionX=(x-startX)/duration;
		this.motionY=(y-startY)/duration;
		this.motionZ=(z-startZ)/duration;
		this.particleMaxAge=200;
		this.noClip=true;
		this.setSize(0.025f, 0.025f);
		//this.setParticleIcon(Item.itemsList[2498+256].getIconFromDamage(0));
		this.setParticleIcon(TF2EventBusListener.pelletIcon);
		//this.setParticleTextureIndex(81);
		this.multipleParticleScaleBy(2);
		
		// TODO Auto-generated constructor stub
		if(crits!=2){
			this.setRBGColorF(0.97f, 0.76f,0.51f);
		}
		else{
			if(TF2weapons.getTeamForDisplay(shooter)==0){
				this.setRBGColorF(1f, 0.2f,0.2f);
			}
			else{
				this.setRBGColorF(0.2f, 0.2f,1f);
			}
		}
		//S/ystem.out.println("Crits: "+crits);
	}
	
	@Override
	public void onUpdate(){
		if(nextDead){
			this.setDead();
		}
		if(this.worldObj.rayTraceBlocks(Vec3.createVectorHelper(posX, posY, posZ), Vec3.createVectorHelper(posX+motionX, posY+motionY, posZ+motionZ)) != null){
			nextDead=true;
			//this.setVelocity(0, 0, 0);
		}
		super.onUpdate();
		this.motionX *= 1.025D;
        this.motionY *= 1.025D;
        this.motionZ *= 1.025D;
		if(duration > 0){
			duration--;
			if(duration==0)
				this.setDead();
		}
	}
	public void renderParticle(Tessellator worldRendererIn, float partialTicks, float rotationX, float rotationZ, float rotationYZ, float rotationXY, float rotationXZ)
    {
		
		
		float x = (float)(this.prevPosX + (this.posX - this.prevPosX) * partialTicks - interpPosX);
        float y = (float)(this.prevPosY + (this.posY - this.prevPosY) * partialTicks - interpPosY);
        float z = (float)(this.prevPosZ + (this.posZ - this.prevPosZ) * partialTicks - interpPosZ);
        Vec3 rightVec=Vec3.createVectorHelper(this.motionX,this.motionY,this.motionZ).crossProduct(Minecraft.getMinecraft().renderViewEntity.getLook(1)).normalize();
        //System.out.println(rightVec);
        float f4 = 0.1F * this.particleScale;
        
        int i = this.getBrightnessForRender(partialTicks);
        int j = i >> 16 & 65535;
        int k = i & 65535;
        
        float xNext = (float) (x+this.motionX*2);
        float yNext = (float) (y+this.motionY*2);
        float zNext = (float) (z+this.motionZ*2);
        
        float xMin = this.particleIcon.getMinU();
        float xMax = this.particleIcon.getMaxU();
        float yMin = this.particleIcon.getMinV();
        float yMax = this.particleIcon.getMaxV();
        worldRendererIn.setColorRGBA_F(this.particleRed, this.particleGreen, this.particleBlue, this.particleAlpha);
        worldRendererIn.addVertexWithUV(x - rightVec.xCoord * f4, y-rightVec.yCoord*f4, z-rightVec.zCoord*f4, xMax, yMax);
        worldRendererIn.addVertexWithUV(x + rightVec.xCoord * f4, y+rightVec.yCoord*f4, z+rightVec.zCoord*f4, xMax, yMin);
        worldRendererIn.addVertexWithUV(xNext + rightVec.xCoord * f4, yNext+rightVec.yCoord*f4, zNext+rightVec.zCoord*f4, xMin, yMin);
        worldRendererIn.addVertexWithUV(xNext - rightVec.xCoord * f4, yNext-rightVec.yCoord*f4, zNext-rightVec.zCoord*f4, xMin, yMax);
		
        /*worldRendererIn.pos((double)(x + rightVec.xCoord * f4), (double)(y+rightVec.yCoord*f4), (double)(z+rightVec.zCoord*f4), (double)xMax, (double)yMax).color(this.particleRed, this.particleGreen, this.particleBlue, this.particleAlpha).lightmap(j, k);;
        worldRendererIn.pos((double)(x - rightVec.xCoord * f4), (double)(y-rightVec.yCoord*f4), (double)(z-rightVec.zCoord*f4), (double)xMax, (double)yMin).color(this.particleRed, this.particleGreen, this.particleBlue, this.particleAlpha).lightmap(j, k);;
        worldRendererIn.pos((double)(xNext - rightVec.xCoord * f4), (double)(yNext-rightVec.yCoord*f4), (double)(zNext-rightVec.zCoord*f4), (double)xMin, (double)yMin).color(this.particleRed, this.particleGreen, this.particleBlue, this.particleAlpha).lightmap(j, k);;
        worldRendererIn.pos((double)(xNext + rightVec.xCoord * f4 ), (double)(yNext+rightVec.yCoord*f4), (double)(zNext+rightVec.zCoord*f4), (double)xMin, (double)yMin).color(this.particleRed, this.particleGreen, this.particleBlue, this.particleAlpha).lightmap(j, k);;*/
        //System.out.println("Rotation X: "+rotationX+" Rotation Z: "+rotationZ+" Rotation YZ: "+rotationYZ+" Rotation XY: "+rotationXY+" rotation XZ: "+rotationXZ);
		//super.renderParticle(worldRendererIn, entityIn, partialTicks, rotationX, rotationZ, rotationYZ, rotationXY, rotationXZ);
    }
	
	@Override
	public int getFXLayer()
    {
        return 2;
    }
	@Override
	@SideOnly(Side.CLIENT)
    public int getBrightnessForRender(float p_70070_1_)
    {
		return 15728880;
    }

    @Override
	public float getBrightness(float p_70013_1_)
    {
        return 1.0F;
    }
}
