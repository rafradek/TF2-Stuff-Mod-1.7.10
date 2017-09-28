package rafradek.TF2weapons.crafting;

import java.util.List;

import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.ShapedRecipes;
import net.minecraft.item.crafting.ShapelessRecipes;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.oredict.ShapedOreRecipe;
import net.minecraftforge.oredict.ShapelessOreRecipe;
import rafradek.TF2weapons.ItemFromData;
import rafradek.TF2weapons.TF2weapons;
import rafradek.TF2weapons.message.TF2Message;

public class GuiTF2Crafting extends GuiContainer
{
    private static final ResourceLocation CRAFTING_TABLE_GUI_TEXTURES = new ResourceLocation(TF2weapons.MOD_ID,"textures/gui/container/cabinet.png");

    public GuiButtonToggleItem[] buttonsItem;
    public ItemStack[] itemsToRender;
    public int firstIndex;
    public float scroll;
    public int tabid;
    public ItemStack craftingTabStack=new ItemStack(TF2weapons.itemAmmo,1,1);

    //public TileEntityCabinet cabinet;
	private boolean isScrolling;

	private boolean wasClicking;
    
    public GuiTF2Crafting(InventoryPlayer playerInv, World worldIn, int x, int y, int z)
    {
        super(new ContainerTF2Workbench(Minecraft.getMinecraft().thePlayer,playerInv, worldIn, x,y,z));
        //this.cabinet=cabinet;
        this.xSize=176;
        this.ySize=180;
        this.itemsToRender=new ItemStack[9];
        this.buttonsItem=new GuiButtonToggleItem[12];
    }
    @Override
	public void initGui()
    {
        super.initGui();
        for(int x=0;x<3;x++){
        	for(int y=0;y<4;y++){
        		this.buttonList.add(buttonsItem[x+y*3]=new GuiButtonToggleItem(x+y*3, this.guiLeft+7+x*18, this.guiTop+14+y*18, 18, 18));
        	}
        }
        setButtons();
    }
    public void setButtons(){
    	for(int i=0;i<12;i++){
    		//System.out.println("Buttons: "+buttonsItem[i]+" "+firstIndex);
    		if(i+firstIndex<TF2CraftingManager.INSTANCE.getRecipeList().size()){
    			buttonsItem[i].stackToDraw=TF2CraftingManager.INSTANCE.getRecipeList().get(i+firstIndex).getRecipeOutput();
    			buttonsItem[i].selected=i+firstIndex==((ContainerTF2Workbench)this.inventorySlots).currentRecipe;
    		}
    		else{
    			buttonsItem[i].stackToDraw=null;
    			buttonsItem[i].selected=false;
    		}
    	}
    }
    @Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks){
    	boolean flag = Mouse.isButtonDown(0);
        int i = this.guiLeft;
        int j = this.guiTop;
        int k = i + 61;
        int l = j + 14;
        int i1 = k + 14;
        int j1 = l + 72;

        if (!this.wasClicking && flag && mouseX >= k && mouseY >= l && mouseX < i1 && mouseY < j1)
        {
            this.isScrolling = true;
        }

        if (!flag)
        {
            this.isScrolling = false;
        }

        this.wasClicking = flag;

        if (this.isScrolling)
        {
        	int size=TF2CraftingManager.INSTANCE.getRecipeList().size();
            this.scroll = (mouseY - l - 7.5F) / (j1 - l - 15.0F);
            this.scroll = MathHelper.clamp_float(this.scroll, 0.0F, 1.0F);
            this.firstIndex=Math.round(this.scroll*(size-12)/3)*3;
            this.setButtons();
        }
        
        super.drawScreen(mouseX, mouseY, partialTicks);
    }
    
    public void drawHoveringText(List<String> textLines, int x, int y)
    {
        drawHoveringText(textLines, x, y, fontRendererObj);   
    }
    @Override
	@SuppressWarnings("unchecked")
	protected void actionPerformed(GuiButton button)
    {
    	if(button.id<12){
    		int currentRecipe=button.id+this.firstIndex;
    		((ContainerTF2Workbench)this.inventorySlots).currentRecipe=currentRecipe;
    		TF2weapons.network.sendToServer(new TF2Message.GuiConfigMessage(this.inventorySlots.windowId, (byte) 0, currentRecipe));
    		setButtons();
    		this.inventorySlots.onCraftMatrixChanged(null);
    		itemsToRender=new ItemStack[9];
            if(currentRecipe>=0&&currentRecipe<TF2CraftingManager.INSTANCE.getRecipeList().size()){
            	IRecipe recipe=TF2CraftingManager.INSTANCE.getRecipeList().get(currentRecipe);
            	
            	if(recipe instanceof ShapelessOreRecipe){
            		List<Object> input=((ShapelessOreRecipe)recipe).getInput();
            		for(int i=0;i<input.size();i++){
            			if(input.get(i) instanceof ItemStack){
            				itemsToRender[i]=(ItemStack) input.get(i);
            			}
            			else if(input.get(i) != null){
            				itemsToRender[i]=((List<ItemStack>) input.get(i)).get(0);
            			}
            		}
            	}
            	else if(recipe instanceof ShapelessRecipes){
            		List<ItemStack> input=((ShapelessRecipes)recipe).recipeItems;
            		for(int i=0;i<input.size();i++){
            			itemsToRender[i]=input.get(i);
            		}
            	}
            	else if(recipe instanceof ShapedOreRecipe){
            		Object[] input=((ShapedOreRecipe)recipe).getInput();
            		for(int i=0;i<input.length;i++){
            			if(input[i] instanceof ItemStack){
            				itemsToRender[i]=(ItemStack) input[i];
            			}
            			else if(input[i] != null){
            				itemsToRender[i]=((List<ItemStack>) input[i]).get(0);
            			}
            		}
            	}
            	else if(recipe instanceof ShapedRecipes){
            		ItemStack[] input=((ShapedRecipes)recipe).recipeItems;
            		for(int i=0;i<input.length;i++){
            			itemsToRender[i]=input[i];
            		}
            	}
            	else if(recipe instanceof AustraliumRecipe){
            		itemsToRender[0]=new ItemStack(TF2weapons.itemTF2,1,2);
            		itemsToRender[1]=new ItemStack(TF2weapons.itemTF2,1,2);
            		itemsToRender[2]=new ItemStack(TF2weapons.itemTF2,1,2);
            		itemsToRender[3]=new ItemStack(TF2weapons.itemTF2,1,2);
            		itemsToRender[4]=new ItemStack(TF2weapons.itemTF2,1,2);
            		itemsToRender[6]=new ItemStack(TF2weapons.itemTF2,1,2);
            		itemsToRender[7]=new ItemStack(TF2weapons.itemTF2,1,2);
            		itemsToRender[8]=new ItemStack(TF2weapons.itemTF2,1,2);
            		itemsToRender[5]=new ItemStack(TF2weapons.itemTF2,1,9);
            	}
            	else if(recipe instanceof RecipeToScrap){
            		itemsToRender[0]=new ItemStack(TF2weapons.itemTF2,1,9);
            		itemsToRender[1]=new ItemStack(TF2weapons.itemTF2,1,9);
            	}
            	else if(recipe instanceof OpenCrateRecipe){
            		itemsToRender[0]=new ItemStack(TF2weapons.itemTF2,1,7);
            		itemsToRender[1]=ItemFromData.getNewStack("crate1");
            	}
            }
    	}
    }
    /**
     * Draw the foreground layer for the GuiContainer (everything in front of the items)
     */
    @Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY)
    {
        this.fontRendererObj.drawString(I18n.format("container.crafting", new Object[0]), 8, 5, 4210752);
        this.fontRendererObj.drawString(I18n.format("container.inventory", new Object[0]), 8, this.ySize - 96 + 3, 4210752);
        for(int i=0;i<12;i++){
        	if(this.buttonsItem[i].stackToDraw !=null && this.buttonsItem[i].func_146115_a()){
        		((GuiTF2Crafting)mc.currentScreen).drawHoveringText(this.buttonsItem[i].stackToDraw.getTooltip(mc.thePlayer, false), mouseX-this.guiLeft, mouseY-this.guiTop);
        	}
        }
        /*for(int i=0;i<4;i++){
        	this.fontRendererObj.drawString(I18n.format(TF2CraftingManager.INSTANCE.getRecipeList().get(i).getRecipeOutput().getDisplayName(), new Object[0]), 10, 17+18*i, 16777215);
        }*/
    }

    /**
     * Draws the background layer of this container (behind the items).
     */
    @Override
	protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY)
    {
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.mc.getTextureManager().bindTexture(CRAFTING_TABLE_GUI_TEXTURES);
        int x = (this.width - this.xSize) / 2;
        int y = (this.height - this.ySize) / 2;
        this.drawTexturedModalRect(x, y, 0, 0, this.xSize, this.ySize);
       // this.drawTab(0);
        //this.drawTab(1);
        
        x = this.guiLeft + 62;
        y = this.guiTop + 15;
        int k = y + 72;
        
        this.drawTexturedModalRect(x, y + (int)((k - y - 17) * this.scroll), 232, 0, 12, 15);
		GL11.glEnable(GL11.GL_LIGHTING);
		GL11.glEnable(GL12.GL_RESCALE_NORMAL);
        RenderHelper.enableGUIStandardItemLighting();
        itemRender.zLevel=0;
        for(x=0;x<3;x++){
        	for(y=0;y<3;y++){
	        	ItemStack stack=this.itemsToRender[x+y*3];
	        	System.out.println("StackRendr "+stack);
	        	if(stack!=null){
	        		itemRender.renderItemAndEffectIntoGUI(this.fontRendererObj,this.mc.getTextureManager(),stack, this.guiLeft+86+18*x, this.guiTop+23+18*y);
	        	}
        	}
        }
        RenderHelper.disableStandardItemLighting();
        GL11.glDisable(GL11.GL_LIGHTING);
        this.mc.getTextureManager().bindTexture(CRAFTING_TABLE_GUI_TEXTURES);
        GL11.glEnable(GL11.GL_BLEND);
        OpenGlHelper.glBlendFunc(770, 771, 1, 0);
        GL11.glDisable(GL12.GL_RESCALE_NORMAL);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 0.5F);
        this.zLevel=120;
        this.drawTexturedModalRect(85+this.guiLeft, 22+this.guiTop, 85, 22, 54,54);
        this.zLevel=0;
        GL11.glDisable(GL11.GL_BLEND);
        /*int currentRecipe=((ContainerTF2Workbench)this.inventorySlots).currentRecipe;
        if(currentRecipe>=0&&currentRecipe<TF2CraftingManager.INSTANCE.getRecipeList().size()){
        	IRecipe recipe=TF2CraftingManager.INSTANCE.getRecipeList().get(currentRecipe);
        	
        	if(recipe instanceof ShapelessOreRecipe){
        		List<Object> input=;
        		for(int i=0;i<((ShapelessOreRecipe)recipe).getInput().size();i++){
        			this.itemRender.renderItemIntoGUI(((ShapelessOreRecipe)recipe).getInput().get(i), , y);
        		}
        	}
        	
        }*/
    }
    /*protected void renderItemModelIntoGUI(ItemStack stack, int x, int y, IBakedModel bakedmodel)
    {
        GL11.glPushMatrix();
        this.mc.getTextureManager().bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
        this.mc.getTextureManager().getTexture(TextureMap.LOCATION_BLOCKS_TEXTURE).setBlurMipmap(false, false);
        GlStateManager.enableRescaleNormal();
        GlStateManager.enableAlpha();
        GlStateManager.alphaFunc(516, 0.1F);
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
        this.setupGuiTransform(x, y, bakedmodel.isGui3d());
        bakedmodel = net.minecraftforge.client.ForgeHooksClient.handleCameraTransforms(bakedmodel, ItemCameraTransforms.TransformType.GUI, false);
        GlStateManager.color(0.4F, 0.4F, 0.4F);
        this.itemRender.renderItem(stack, bakedmodel);
        GlStateManager.disableAlpha();
        GlStateManager.disableRescaleNormal();
        GL11.glDisable(GL11.GL_LIGHTING);
        GlStateManager.popMatrix();
        this.mc.getTextureManager().bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
        this.mc.getTextureManager().getTexture(TextureMap.LOCATION_BLOCKS_TEXTURE).restoreLastBlurMipmap();
    }
    private void setupGuiTransform(int xPosition, int yPosition, boolean isGui3d)
    {
        GL11.glTranslatef((float)xPosition, (float)yPosition, 100.0F + this.zLevel);
        GL11.glTranslatef(8.0F, 8.0F, 0.0F);
        GlStateManager.scale(1.0F, -1.0F, 1.0F);
        GlStateManager.scale(16.0F, 16.0F, 16.0F);

        if (isGui3d)
        {
            GL11.glEnable(GL11.GL_LIGHTING);
        }
        else
        {
            GL11.glDisable(GL11.GL_LIGHTING);
        }
    }*/
}