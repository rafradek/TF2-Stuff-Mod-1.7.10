package rafradek.TF2weapons.building;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import rafradek.TF2weapons.TF2weapons;

public class RenderDispenser extends RenderLiving {

	private static final ResourceLocation DISPENSER_RED = new ResourceLocation(TF2weapons.MOD_ID,"textures/entity/tf2/red/Dispenser.png");
	private static final ResourceLocation DISPENSER_BLU = new ResourceLocation(TF2weapons.MOD_ID,"textures/entity/tf2/blu/Dispenser.png");
	public ModelBase level1;
	public ModelBase level2;
	public ModelBase level3;
	public RenderDispenser() {
		super(new ModelDispenser(),0.8f);
		level1=this.mainModel;
		level2=new ModelDispenser2();
		level3=new ModelDispenser3();
	}
	
	@Override
	protected ResourceLocation getEntityTexture(Entity par1EntityLiving)
    {
		return ((EntityDispenser) par1EntityLiving).getEntTeam()==0?DISPENSER_RED:DISPENSER_BLU;
    }
	@Override
	public void doRender(EntityLiving ent, double p_76986_2_, double p_76986_4_, double p_76986_6_, float p_76986_8_, float p_76986_9_)
	{
		EntityDispenser living=(EntityDispenser)ent;
		if(living.getLevel()==1){
			this.mainModel=this.level1;
		}
		else if(living.getLevel()==2){
			this.mainModel=this.level2;
		}
		else{
			this.mainModel=this.level3;
		}
		super.doRender(living, p_76986_2_, p_76986_4_, p_76986_6_, p_76986_8_, p_76986_9_);
		Tessellator tessellator = Tessellator.instance;
        if(living.dispenserTarget!=null){
	        for(EntityLivingBase target:living.dispenserTarget){
	    		double xPos2=target.prevPosX+(target.posX-target.prevPosX)*p_76986_9_;
	    		double yPos2=target.prevPosY+(target.posY-target.prevPosY)*p_76986_9_;
	    		double zPos2=target.prevPosZ+(target.posZ-target.prevPosZ)*p_76986_9_;
	    		double xDist=xPos2-(living.prevPosX+(living.posX-living.prevPosX)*p_76986_9_);
	    		double yDist=(yPos2+target.height/2)-(living.prevPosY+(living.posY-living.prevPosY)*p_76986_9_+living.height/2);
	    		double zDist=zPos2-(living.prevPosZ+(living.posZ-living.prevPosZ)*p_76986_9_);
	    		float f = MathHelper.sqrt_double(xDist * xDist + zDist * zDist);
	    		float fullDist = MathHelper.sqrt_double(xDist * xDist + yDist * yDist + zDist * zDist);
	            GL11.glPushMatrix();
	            GL11.glTranslatef((float)p_76986_2_, (float)p_76986_4_+living.height/2-(target == Minecraft.getMinecraft().thePlayer?living.getEyeHeight():0), (float)p_76986_6_);
	            GL11.glRotatef((float)(Math.atan2(xDist, zDist) * 180.0D / Math.PI), 0.0F, 1.0F, 0.0F);
	            GL11.glRotatef((float)(Math.atan2(yDist, f) * 180.0D / Math.PI)*-1, 1.0F, 0.0F, 0.0F);
	    		GL11.glDisable(GL11.GL_TEXTURE_2D);
	            GL11.glDisable(GL11.GL_LIGHTING);
	            GL11.glEnable(GL11.GL_BLEND);
	            OpenGlHelper.glBlendFunc(770, 771, 1, 0);
	            if(TF2weapons.getTeamForDisplay(living)==0){
	            	GL11.glColor4f(1.0F, 0.0F, 0.0F, 0.23F);
	            }
	            else{
	            	GL11.glColor4f(0.0F, 0.0F, 1.0F, 0.23F);
	            }
	    		tessellator.startDrawingQuads();
	            tessellator.addVertex(-0.04, -0.04,0);
	            tessellator.addVertex(0.04,0.04,0);
	            tessellator.addVertex(0.04,0.04, fullDist);
	            tessellator.addVertex(-0.04,-0.04, fullDist);
	            tessellator.draw();
	            tessellator.startDrawingQuads();
	            tessellator.addVertex(-0.04,-0.04, fullDist);
	            tessellator.addVertex(0.04,0.04, fullDist);
	            tessellator.addVertex(0.04, 0.04,0);
	            tessellator.addVertex(-0.04, -0.04,0);
	            tessellator.draw();
	            tessellator.startDrawingQuads();
	            tessellator.addVertex(0.04, -0.04,0);
	            tessellator.addVertex(-0.04, 0.04,0);
	            tessellator.addVertex(-0.04,0.04, fullDist);
	            tessellator.addVertex(0.04,-0.04, fullDist);
	            tessellator.draw();
	            tessellator.startDrawingQuads();
	            tessellator.addVertex(0.04,-0.04, fullDist);
	            tessellator.addVertex(-0.04,0.04, fullDist);
	            tessellator.addVertex(-0.04, 0.04,0);
	            tessellator.addVertex(0.04, -0.04,0);
	            tessellator.draw();
	            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
	            GL11.glDisable(GL11.GL_BLEND);
	            GL11.glEnable(GL11.GL_LIGHTING);
	            GL11.glEnable(GL11.GL_TEXTURE_2D);
	            GL11.glPopMatrix();
	        }
        }
    }
}
