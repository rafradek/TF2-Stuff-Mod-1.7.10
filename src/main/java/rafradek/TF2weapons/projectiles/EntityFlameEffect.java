package rafradek.TF2weapons.projectiles;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.particle.EntityFX;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public class EntityFlameEffect extends EntityFX {

	
	protected EntityFlameEffect(World worldIn, double p_i1209_2_, double p_i1209_4_, double p_i1209_6_, double motionX, double motionY, double motionZ) {
		super(worldIn, p_i1209_2_, p_i1209_4_, p_i1209_6_);
		this.motionX=motionX;
		this.motionY=motionY;
		this.motionZ=motionZ;
		//this.noClip=false;
		this.particleRed = this.particleGreen = this.particleBlue = 1.0F;
		this.particleMaxAge=5;
		this.setParticleTextureIndex(48);
	}
	@Override
	public void renderParticle(Tessellator p_180434_1_, float p_180434_3_, float p_180434_4_, float p_180434_5_, float p_180434_6_, float p_180434_7_, float p_180434_8_)
    {
        float f6 = (this.particleAge + p_180434_3_) / this.particleMaxAge;
        this.particleScale = 1.0F + f6 * f6 * 5.5F;
        super.renderParticle(p_180434_1_, p_180434_3_, p_180434_4_, p_180434_5_, p_180434_6_, p_180434_7_, p_180434_8_);
    }
	
	@Override
	public void onUpdate(){
		super.onUpdate();
		this.particleAlpha*=0.9f;
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
	public static EntityFlameEffect createNewEffect(World world, EntityLivingBase living,float step){
		double posX=living.posX - MathHelper.cos(living.rotationYawHead / 180.0F * (float)Math.PI) * 0.16F;
		double posY=living.posY + living.getEyeHeight()-0.1;
		double posZ=living.posZ - MathHelper.sin(living.rotationYawHead / 180.0F * (float)Math.PI) * 0.16F;
        double motionX = -MathHelper.sin(living.rotationYawHead / 180.0F * (float)Math.PI) * MathHelper.cos(living.rotationPitch / 180.0F * (float)Math.PI);
        double motionZ = MathHelper.cos(living.rotationYawHead / 180.0F * (float)Math.PI) * MathHelper.cos(living.rotationPitch / 180.0F * (float)Math.PI);
        double motionY = (-MathHelper.sin(living.rotationPitch / 180.0F * (float)Math.PI));
        float f2 = MathHelper.sqrt_double(motionX * motionX + motionY * motionY + motionZ * motionZ);
        motionX = (motionX / f2 + world.rand.nextGaussian() * (world.rand.nextBoolean() ? -1 : 1) * 0.045D)*1.257;
        motionY = (motionY / f2 + world.rand.nextGaussian() * (world.rand.nextBoolean() ? -1 : 1) * 0.045D)*1.257;
        motionZ = (motionZ / f2 + world.rand.nextGaussian() * (world.rand.nextBoolean() ? -1 : 1) * 0.045D)*1.257;
        
		return new EntityFlameEffect(world,posX+motionX*step,posY+motionY*step,posZ+motionZ*step,motionX,motionY,motionZ);
		
	}
}
