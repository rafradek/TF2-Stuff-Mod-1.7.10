package rafradek.TF2weapons.projectiles;

import net.minecraft.client.renderer.entity.Render;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;

public class RenderProj<T extends EntityProjectileBase> extends Render {

	// if you want a model, be sure to add it here:
	
	public RenderProj() {
		super();
	// we could have initialized it above, but here is fine as well:
	}

	@SuppressWarnings("unchecked")
	@Override
	protected ResourceLocation getEntityTexture(Entity entity) {
		
		return this.getEntityTexture((T)entity);
	}

	protected ResourceLocation getEntityTexture(T entity){
		return null;
	}
	@SuppressWarnings("unchecked")
	@Override
	public void doRender(Entity entity, double x, double y, double z, float yaw, float partialTick) {
		this.doRender((T)entity, x, y, z, yaw, partialTick);
	}

	public void doRender(T entity, double x, double y, double z, float yaw, float partialTick) {
		
	}
}
