package rafradek.TF2weapons.upgrade;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import org.lwjgl.opengl.GL11;

import cpw.mods.fml.relauncher.SideOnly;

/**
 * Upgrade Station by Unknown
 */
public class ModelUpgradeStation extends ModelBase {
    public ModelRenderer Cube1;
    public ModelRenderer Cube2;
    public ModelRenderer Cube3;
    public ModelRenderer Cube4;
    public ModelRenderer Cube5;
    public ModelRenderer Cube6;

    public ModelUpgradeStation() {
        this.textureWidth = 128;
        this.textureHeight = 128;

        this.Cube1 = new ModelRenderer(this, 0, 0);
        this.Cube1.setRotationPoint(-24.0F, -8.0F, 6.0F);
        this.Cube1.addBox(0.0F, 0.0F, 0.0F, 48, 32, 2);
        this.Cube2 = new ModelRenderer(this, 0, 34);
        this.Cube2.setRotationPoint(-24.0F, -8.0F, -4.0F);
        this.Cube2.addBox(0.0F, 0.0F, 0.0F, 48, 8, 10);
        this.Cube3 = new ModelRenderer(this, 0, 71);
        this.Cube3.setRotationPoint(-20.0F, 0.0F, 5.0F);
        this.Cube3.addBox(0.0F, 0.0F, 0.0F, 40, 15, 1);
        this.Cube4 = new ModelRenderer(this, 0, 52);
        this.Cube4.setRotationPoint(-24.0F, 15.0F, -4.0F);
        this.Cube4.addBox(0.0F, 0.0F, 0.0F, 48, 9, 10);
        this.Cube5 = new ModelRenderer(this, 104, 0);
        this.Cube5.setRotationPoint(-24.0F, 0.0F, -2.0F);
        this.Cube5.addBox(0.0F, 0.0F, 0.0F, 4, 15, 8);
        this.Cube6 = new ModelRenderer(this, 104, 0);
        this.Cube6.setRotationPoint(20.0F, 0.0F, -2.0F);
        this.Cube6.addBox(0.0F, 0.0F, 0.0F, 4, 15, 8);
    }

    public void renderAll() {
        this.Cube1.render(0.0625F);
        this.Cube2.render(0.0625F);
        this.Cube3.render(0.0625F);
        this.Cube4.render(0.0625F);
        this.Cube5.render(0.0625F);
        this.Cube6.render(0.0625F);
    }

    public void setRotationAngles(ModelRenderer modelRenderer, float x, float y, float z) {
        modelRenderer.rotateAngleX = x;
        modelRenderer.rotateAngleY = y;
        modelRenderer.rotateAngleZ = z;
    }
}
