package rafradek.TF2weapons;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ResourceLocation;

public class PotionTF2Item extends Potion {

	public ResourceLocation texture;
	public PotionTF2Item(int id,boolean isBadEffectIn, int liquidColorIn, ResourceLocation texture) {
		super(id,isBadEffectIn, liquidColorIn);
		this.texture=texture;
	}
	
	@Override
	@SideOnly(Side.CLIENT)
    public void renderInventoryEffect(int x, int y, PotionEffect effect, Minecraft mc) { 
		mc.getTextureManager().bindTexture(texture);
		//mc.ingameGUI.drawTexturedModalRect(x+6,y+7,0,0,16,16);
		Tessellator tessellator = Tessellator.instance;
		
        tessellator.startDrawingQuads();
        
        tessellator.addVertexWithUV(x+7, y+23, 0.0D,  0.0D, 1D);
        tessellator.addVertexWithUV(x+23, y+23, 0.0D,  1.0D, 1D);
        tessellator.addVertexWithUV(x+23, y+7, 0.0D,  1.0D, 0.0D);
        tessellator.addVertexWithUV(x+7, y+7, 0.0D,  0.0D, 0.0D);
        tessellator.draw();
	}
	/*@SideOnly(Side.CLIENT)
	public void renderHUDEffect(int x, int y, PotionEffect effect, net.minecraft.client.Minecraft mc, float alpha) {
		mc.getTextureManager().bindTexture(texture);
		//mc.ingameGUI.drawTexturedModalRect(x+3,y+3,0,0,16,16);
		Tessellator tessellator = Tessellator.instance;
        
        tessellator.startDrawingQuads();
        
        tessellator.addVertexWithUV(x+4, y+20, 0.0D,  0.0D, 1D);
        tessellator.addVertexWithUV(x+20, y+20, 0.0D,  1.0D, 1D);
        tessellator.addVertexWithUV(x+20, y+4, 0.0D,  1.0D, 0.0D);
        tessellator.addVertexWithUV(x+4, y+4, 0.0D,  0.0D, 0.0D);
        tessellator.draw();
	}*/
}
