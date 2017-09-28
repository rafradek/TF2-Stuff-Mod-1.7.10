package rafradek.TF2weapons.crafting;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.item.ItemStack;

public class GuiButtonToggleItem extends GuiButton {

	public boolean selected;
	public ItemStack stackToDraw;
	public GuiButtonToggleItem(int buttonId, int x, int y, int widthIn, int heightIn) {
		super(buttonId, x, y, widthIn, heightIn, "");
		// TODO Auto-generated constructor stub
	}

	@Override
	public int getHoverState(boolean mouseOver)
    {
        int i = 1;

        if (!this.enabled)
        {
            i = 0;
        }
        else if (mouseOver||selected)
        {
            i = 2;
        }

        return i;
    }
	@Override
	public void drawButton(Minecraft mc, int mouseX, int mouseY)
    {
        if (this.visible&&this.stackToDraw!=null)
        {
        	super.drawButton(mc, mouseX, mouseY);
            this.zLevel = 100.0F;
            RenderItem renderItem=new RenderItem();
            renderItem.zLevel = 100.0F;
            RenderHelper.enableGUIStandardItemLighting();
            GL11.glEnable(GL11.GL_LIGHTING);
            GL11.glEnable(GL12.GL_RESCALE_NORMAL);
            renderItem.renderItemAndEffectIntoGUI(mc.fontRenderer,mc.renderEngine,this.stackToDraw, this.xPosition+1, this.yPosition+1);
            renderItem.renderItemOverlayIntoGUI(mc.fontRenderer,mc.renderEngine,this.stackToDraw, this.xPosition+1, this.yPosition+1);
            GL11.glDisable(GL11.GL_LIGHTING);
            RenderHelper.disableStandardItemLighting();
            renderItem.zLevel = 0.0F;
            this.zLevel = 0.0F;
        }
    }
}
