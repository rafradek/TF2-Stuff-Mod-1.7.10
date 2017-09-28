package rafradek.TF2weapons.building;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.util.ResourceLocation;
import rafradek.TF2weapons.TF2weapons;

public class RenderSentry extends RenderLiving {

	private static final ResourceLocation SENTRY_RED = new ResourceLocation(TF2weapons.MOD_ID,"textures/entity/tf2/red/Sentry.png");
	private static final ResourceLocation SENTRY_BLU = new ResourceLocation(TF2weapons.MOD_ID,"textures/entity/tf2/blu/Sentry.png");
	public ModelBase level1;
	public ModelBase level2;
	public ModelBase level3;
	public RenderSentry() {
		super(new ModelSentry(),0.8f);
		level1=this.mainModel;
		level2=new ModelSentry2();
		level3=new ModelSentry3();
	}
	
	@Override
	protected ResourceLocation getEntityTexture(Entity par1EntityLiving)
    {
		//System.out.println("class: "+clazz);

		return ((EntitySentry) par1EntityLiving).getEntTeam()==0?SENTRY_RED:SENTRY_BLU;
    }
	@Override
	public void doRender(EntityLiving living, double p_76986_2_, double p_76986_4_, double p_76986_6_, float p_76986_8_, float p_76986_9_)
	{
		if(((EntitySentry) living).getLevel()==1){
			this.mainModel=this.level1;
		}
		else if(((EntitySentry) living).getLevel()==2){
			this.mainModel=this.level2;
		}
		else{
			this.mainModel=this.level3;
		}
		super.doRender(living, p_76986_2_, p_76986_4_, p_76986_6_, p_76986_8_, p_76986_9_);
		
    }
}
