package rafradek.TF2weapons.upgrade;

import java.util.List;

import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import rafradek.TF2weapons.TF2Attribute;
import rafradek.TF2weapons.TF2weapons;

public class GuiUpgradeStation extends GuiContainer
{
    public GuiUpgradeStation(Container inventorySlotsIn) {
		super(inventorySlotsIn);
		// TODO Auto-generated constructor stub
	}

	private static final ResourceLocation UPGRADES_GUI_TEXTURES = new ResourceLocation(TF2weapons.MOD_ID,"textures/gui/container/upgrades.png");

    //public ItemStack[] itemsToRender;
	public GuiButton[] buttons=new GuiButton[12];
    public int firstIndex;
    public float scroll;
    public int tabid;
    public ItemStack craftingTabStack=new ItemStack(TF2weapons.itemAmmo,1,1);
    public ItemStack chestTabStack=new ItemStack(Blocks.chest);

    public TileEntityUpgrades station;
	private boolean isScrolling;

	private boolean wasClicking;
    
    public GuiUpgradeStation(InventoryPlayer playerInv,TileEntityUpgrades station, World worldIn, int x, int y, int z)
    {
        super(new ContainerUpgrades(Minecraft.getMinecraft().thePlayer,playerInv,station, worldIn, x, y, z));
        this.station=station;
        this.xSize=230;
        this.ySize=216;
        //this.itemsToRender=new ItemStack[9];
    }
    @Override
	public void initGui()
    {
        super.initGui();
        for(int x=0;x<2;x++){
        	for(int y=0;y<3;y++){
        		this.buttonList.add(buttons[x*2+y*4]=new GuiButton(x*2+y*4, this.guiLeft+81+x*101, this.guiTop+47+y*30, 12, 12, "+"));
        		this.buttonList.add(buttons[x*2+y*4+1]=new GuiButton(x*2+y*4+1, this.guiLeft+94+x*101, this.guiTop+47+y*30, 12, 12, "-"));
        	}
        }
        setButtons();
    }
    public void setButtons(){
    	/*for(int i=0;i<12;i++){
    		//System.out.println("Buttons: "+buttonsItem[i]+" "+firstIndex);
    		if(i+firstIndex<TF2CraftingManager.INSTANCE.getRecipeList().size()){
    			buttonsItem[i].stackToDraw=TF2CraftingManager.INSTANCE.getRecipeList().get(i+firstIndex).getRecipeOutput();
    			buttonsItem[i].selected=i+firstIndex==((ContainerTF2Workbench)this.inventorySlots).currentRecipe;
    		}
    		else{
    			buttonsItem[i].stackToDraw=null;
    			buttonsItem[i].selected=false;
    		}
    	}*/
    }
    @Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks){
    	boolean flag = Mouse.isButtonDown(0);
        int i = this.guiLeft;
        int j = this.guiTop;
        int k = i + 209;
        int l = j + 30;
        int i1 = k + 14;
        int j1 = l + 96;

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
        	int size=TileEntityUpgrades.UPGRADES_COUNT;
            this.scroll = (mouseY - l - 7.5F) / (j1 - l - 15.0F);
            this.scroll = MathHelper.clamp_float(this.scroll, 0.0F, 1.0F);
            this.firstIndex=Math.round(this.scroll*(size-6)/2)*2;
            this.setButtons();
        }
        
        super.drawScreen(mouseX, mouseY, partialTicks);
    }
    public void drawHoveringText(List<String> textLines, int x, int y)
    {
        drawHoveringText(textLines, x, y, fontRendererObj);   
    }
    @Override
	protected void actionPerformed(GuiButton button)
    {
    	if(button.id<12){
    		this.mc.playerController.sendEnchantPacket(this.inventorySlots.windowId, button.id+this.firstIndex*2);
    	}
    }
    /**
     * Draw the foreground layer for the GuiContainer (everything in front of the items)
     */
    @Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY)
    {
    	int expPoints=TF2weapons.getExperiencePoints(mc.thePlayer);
        for(int i=0;i<6;i++){
        	GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
            this.mc.getTextureManager().bindTexture(UPGRADES_GUI_TEXTURES);
            
        	TF2Attribute attr=this.station.attributeList[i+firstIndex];
        	ItemStack stack=((Slot)this.inventorySlots.inventorySlots.get(0)).getStack();
        	
        	int xOffset=101*(i%2);
        	int yOffset=(i/2)*30;
        	int currLevel=attr.calculateCurrLevel(stack);
        	for(int j=0;j<this.station.attributes.get(attr);j++){
        		
        		
        		//System.out.println("render: "+currLevel+" "+this.inventorySlots.inventorySlots.get(0).getStack());
        		this.drawTexturedModalRect(9+xOffset+j*9, 50+yOffset, currLevel>j?240:248, 24,8,8);
        	}
        	this.fontRendererObj.drawString(String.valueOf(attr.getUpgradeCost(stack)), 56+xOffset, 50+yOffset, 16777215);
        	this.fontRendererObj.drawSplitString(attr.getTranslatedString(1+attr.perLevel,false), 9+xOffset, 32+yOffset, 98, 16777215);
        	if(!attr.canApply(stack)||currLevel>=this.station.attributes.get(attr)||attr.getUpgradeCost(stack)>expPoints){
        		//System.out.println("DrawingRect");
        		this.buttons[i*2].enabled=false;
        		this.buttons[i*2+1].enabled=false;
        		this.drawGradientRect(8+xOffset, 31+yOffset, 107+xOffset, 59+yOffset, 0x77000000, 0x77000000);
        	}
        	else{
        		this.buttons[i*2].enabled=true;
        		this.buttons[i*2+1].enabled=true;
        	}
        }
        this.fontRendererObj.drawString(I18n.format("container.upgrades", new Object[0]), 8, 5, 4210752);
        this.fontRendererObj.drawString(I18n.format("container.currency", new Object[]{String.valueOf(expPoints)}), 128, 10, 4210752);
        this.fontRendererObj.drawString(I18n.format("container.inventory", new Object[0]), 36, this.ySize - 96 + 3, 4210752);
        /*for(int i=0;i<12;i++){
        	if(this.buttonsItem[i].stackToDraw !=null && this.buttonsItem[i].isMouseOver()){
        		((GuiTF2Crafting)mc.currentScreen).drawHoveringText(this.buttonsItem[i].stackToDraw.getTooltip(mc.thePlayer, false), mouseX-this.guiLeft, mouseY-this.guiTop);
        	}
        }*/
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
        this.mc.getTextureManager().bindTexture(UPGRADES_GUI_TEXTURES);
        int x = (this.width - this.xSize) / 2;
        int y = (this.height - this.ySize) / 2;
        this.drawTexturedModalRect(x, y, 0, 0, this.xSize, this.ySize);
       // this.drawTab(0);
        //this.drawTab(1);
        
        x = this.guiLeft + 210;
        y = this.guiTop + 31;
        int k = y + 96;
        
        this.drawTexturedModalRect(x, y + (int)((k - y - 17) * this.scroll), 232, 0, 12, 15);
        
		/*GL11.glEnable(GL11.GL_LIGHTING);
        GlStateManager.enableRescaleNormal();
        RenderHelper.enableGUIStandardItemLighting();
        this.itemRender.zLevel=0;
        for(x=0;x<3;x++){
        	for(y=0;y<3;y++){
	        	ItemStack stack=this.itemsToRender[x+y*3];
	        	if(stack!=null){
	        		this.itemRender.renderItemIntoGUI(stack, this.guiLeft+86+18*x, this.guiTop+23+18*y);
	        	}
        	}
        }
        RenderHelper.disableStandardItemLighting();
        GL11.glDisable(GL11.GL_LIGHTING);
        this.mc.getTextureManager().bindTexture(UPGRADES_GUI_TEXTURES);
        GlStateManager.enableBlend();
        GlStateManager.color(1.0F, 1.0F, 1.0F, 0.5F);*/
        /*this.zLevel=120;
        this.drawTexturedModalRect(85+this.guiLeft, 22+this.guiTop, 85, 22, 54,54);
        this.zLevel=0;*/
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