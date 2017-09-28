package rafradek.TF2weapons.upgrade;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import rafradek.TF2weapons.TF2weapons;

public class TileEntityUpgradeStationRenderer extends TileEntitySpecialRenderer {

	public ModelUpgradeStation model=new ModelUpgradeStation();
	public static final ResourceLocation TEXTURE=new ResourceLocation(TF2weapons.MOD_ID,"textures/blocks/upgrade_station.png");
	@Override
	public void renderTileEntityAt(TileEntity p_147500_1_, double p_147500_2_, double p_147500_4_, double p_147500_6_,
			float p_147500_8_) {
		int i = 0;

        if (p_147500_1_.hasWorldObj())
        {
            i = p_147500_1_.getBlockMetadata()-8;
        }

        this.bindTexture(TEXTURE);
        GL11.glPushMatrix();
        GL11.glEnable(GL12.GL_RESCALE_NORMAL);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        GL11.glTranslatef((float)p_147500_2_, (float)p_147500_4_ + 1.0F, (float)p_147500_6_ + 1.0F);
        GL11.glScalef(1.0F, -1.0F, -1.0F);
        GL11.glTranslatef(0.5F, -0.5F, 0.5F);
        short short1 = 0;

        if (i == 2)
        {
            short1 = 180;
        }

        if (i == 3)
        {
            short1 = 0;
        }

        if (i == 4)
        {
            short1 = 90;
        }

        if (i == 5)
        {
            short1 = -90;
        }
        GL11.glRotatef((float)short1, 0.0F, 1.0F, 0.0F);
        this.model.renderAll();
        GL11.glDisable(GL12.GL_RESCALE_NORMAL);
        GL11.glPopMatrix();
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		
	}

}
