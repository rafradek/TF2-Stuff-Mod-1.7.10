package rafradek.TF2weapons;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.lwjgl.input.Keyboard;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;

import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import cpw.mods.fml.relauncher.Side;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiYesNo;
import net.minecraft.client.gui.GuiYesNoCallback;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.particle.EntityFX;
import net.minecraft.client.renderer.entity.RenderBiped;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.RendererLivingEntity;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.entity.monster.EntitySkeleton;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.entity.passive.EntityCow;
import net.minecraft.entity.passive.EntityPig;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import rafradek.TF2weapons.WeaponData.PropertyType;
import rafradek.TF2weapons.building.BuildingSound;
import rafradek.TF2weapons.building.EntityDispenser;
import rafradek.TF2weapons.building.EntitySentry;
import rafradek.TF2weapons.building.EntityTeleporter;
import rafradek.TF2weapons.building.GuiTeleporter;
import rafradek.TF2weapons.building.RenderDispenser;
import rafradek.TF2weapons.building.RenderSentry;
import rafradek.TF2weapons.building.RenderTeleporter;
import rafradek.TF2weapons.characters.EntitySaxtonHale;
import rafradek.TF2weapons.characters.EntityStatue;
import rafradek.TF2weapons.characters.EntityTF2Character;
import rafradek.TF2weapons.characters.GuiConfirm;
import rafradek.TF2weapons.characters.RenderStatue;
import rafradek.TF2weapons.characters.RenderTF2Character;
import rafradek.TF2weapons.message.TF2Message;
import rafradek.TF2weapons.projectiles.EntityCritEffect;
import rafradek.TF2weapons.projectiles.EntityFlameEffect;
import rafradek.TF2weapons.projectiles.EntityGrenade;
import rafradek.TF2weapons.projectiles.EntityRocket;
import rafradek.TF2weapons.projectiles.EntityRocketEffect;
import rafradek.TF2weapons.projectiles.EntityStickybomb;
import rafradek.TF2weapons.projectiles.EntitySyringe;
import rafradek.TF2weapons.projectiles.RenderGrenade;
import rafradek.TF2weapons.projectiles.RenderRocket;
import rafradek.TF2weapons.projectiles.RenderStickybomb;
import rafradek.TF2weapons.projectiles.RenderSyringe;
import rafradek.TF2weapons.upgrade.TileEntityUpgradeStationRenderer;
import rafradek.TF2weapons.upgrade.TileEntityUpgrades;
import rafradek.TF2weapons.weapons.EntityBulletTracer;
import rafradek.TF2weapons.weapons.ItemUsable;
import rafradek.TF2weapons.weapons.ReloadSound;
import rafradek.TF2weapons.weapons.WeaponLoopSound;
import rafradek.TF2weapons.weapons.WeaponSound;

public class ClientProxy extends CommonProxy
{
	public static HashMap<String, ModelBase> entityModel=new HashMap<String, ModelBase>();
	public static HashMap<String, ResourceLocation> textureDisguise=new HashMap<String, ResourceLocation>();
	public static RenderCustomModel disguiseRender;
	public static TextureMap particleMap;
	public static KeyBinding reload= new KeyBinding("key.reload", Keyboard.KEY_R,"lol");
	public static ResourceLocation scopeTexture=new ResourceLocation(TF2weapons.MOD_ID,"textures/misc/scope.png");
	//public static Map<MinigunLoopSound, EntityLivingBase > spinSounds;
	public static BiMap<EntityLivingBase, WeaponSound > fireSounds;
	public static Map<EntityLivingBase, ReloadSound> reloadSounds;
	public static ConcurrentMap<EntityLivingBase, ItemStack> soundsToStart;
	public static ResourceLocation blackTexture=new ResourceLocation(TF2weapons.MOD_ID,"textures/misc/black.png");
	public static ResourceLocation healingTexture=new ResourceLocation(TF2weapons.MOD_ID,"textures/gui/healing.png");
	public static ResourceLocation buildingTexture=new ResourceLocation(TF2weapons.MOD_ID,"textures/gui/buildings.png");
	public static ResourceLocation chargeTexture=new ResourceLocation(TF2weapons.MOD_ID,"textures/misc/charge.png");
	public static List<WeaponSound> weaponSoundsToStart;
	
    @Override
	public void registerRenderInformation()
    {
    	ClientRegistry.bindTileEntitySpecialRenderer(TileEntityUpgrades.class, new TileEntityUpgradeStationRenderer());
    	/*List<Item> items=GameRegistry.findRegistry(Item.class).getValues();
    	Iterator<Item> itemIterator=items.iterator();
    	while(itemIterator.hasNext()){
    		Item item=itemIterator.next();
    		if(!(item instanceof ItemFromData||item instanceof ItemTool || item instanceof ItemSword || item instanceof ItemBow)){
    			itemIterator.remove();
    		}
    	}
    	Minecraft.getMinecraft().getItemColors().registerItemColorHandler(new IItemColor(){

			@Override
			public int getColorFromItemstack(ItemStack stack, int tintIndex) {
				if(stack.hasTagCompound()&&stack.getTagCompound().getBoolean("Australium"))
					return 0xFFD400;
				else{
					return 0xFFFFFF;
				}
			}
    		
    	}, items.toArray(new Item[items.size()]));*/
    	
    	/*for(RenderPlayer render:RenderManager.instance.getEntityClassRenderObject(EntityPlayer.class);.getSkinMap().values()){
    	    render.addLayer(new LayerWearables(render,render.getMainModel()));
    	    render.addLayer(new LayerBipedArmor(render){
    	    	
    	    	protected void initArmor()
    	        {
    	            this.modelLeggings = new ModelBiped(0.5F);
    	            this.modelArmor = new ModelBiped(1.25F);
    	        }
    	    	
    	        public ItemStack getItemStackFromSlot(EntityLivingBase living, EntityEquipmentSlot slotIn)
    	        {
    	        	
    	        	if(slotIn==EntityEquipmentSlot.CHEST){
    	        	
    	        		return living.getCapability(TF2weapons.INVENTORY_CAP, null).getStackInSlot(3);
    	        	}
    	        	
    	        	return null;
    	        }
    	        
    	    });
    	}*/
	    
    	/*Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(TF2weapons.itemPlacer, new ItemMeshDefinition()
        {
            public ModelResourceLocation getModelLocation(ItemStack stack)
            {
                return new ModelResourceLocation("spawn_egg", "inventory");
            }
        });*/
    	/*Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(TF2weapons.itemDisguiseKit, new ItemMeshDefinition()
        {
            public ModelResourceLocation getModelLocation(ItemStack stack)
            {
                return new ModelResourceLocation(TF2weapons.MOD_ID+":disguiseKit", "inventory");
            }
        });*/
    	/*Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(TF2weapons.itemBuildingBox, new ItemMeshDefinition()
        {
            public ModelResourceLocation getModelLocation(ItemStack stack)
            {
                return new ModelResourceLocation("spawn_egg", "inventory");
            }
        });*/
    	//Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(p_178086_1_, p_178086_2_, p_178086_3_);
    	reloadSounds=new HashMap<EntityLivingBase,ReloadSound>();
    	soundsToStart=new ConcurrentHashMap<EntityLivingBase,ItemStack>();
    	weaponSoundsToStart=new ArrayList<WeaponSound>();
		fireSounds=HashBiMap.create();
    	ClientRegistry.registerKeyBinding(ClientProxy.reload);
    	disguiseRender=new RenderCustomModel(new ModelBiped(), 0);
    	entityModel.put("Creeper", getMainModel(((RendererLivingEntity) RenderManager.instance.entityRenderMap.get(EntityCreeper.class))));
    	textureDisguise.put("Creeper", new ResourceLocation("textures/entity/creeper/creeper.png"));
    	entityModel.put("Zombie", getMainModel(((RendererLivingEntity) RenderManager.instance.entityRenderMap.get(EntityZombie.class))));
    	textureDisguise.put("Zombie", new ResourceLocation("textures/entity/zombie/zombie.png"));
    	entityModel.put("Skeleton", getMainModel(((RendererLivingEntity) RenderManager.instance.entityRenderMap.get(EntitySkeleton.class))));
    	textureDisguise.put("Skeleton", new ResourceLocation("textures/entity/skeleton/skeleton.png"));
    	entityModel.put("Cow", getMainModel(((RendererLivingEntity) RenderManager.instance.entityRenderMap.get(EntityCow.class))));
    	textureDisguise.put("Cow", new ResourceLocation("textures/entity/cow/cow.png"));
    	entityModel.put("Pig", getMainModel(((RendererLivingEntity) RenderManager.instance.entityRenderMap.get(EntityPig.class))));
    	textureDisguise.put("Pig", new ResourceLocation("textures/entity/pig/pig.png"));
    	//RenderingRegistry.registerEntityRenderingHandler(EntityScout.class, new RenderTF2Character());	
    	//Minecraft.getMinecraft().renderEngine.loadTextureMap(new ResourceLocation("textures/tfatlas/particles.png"), particleMap=new TF2TextureMap("textures/particle"));
    }
    public static ModelBase getMainModel(RendererLivingEntity renderer){
    	try {
    		Class<?> clazz=renderer.getClass();
    		while(clazz!=RendererLivingEntity.class){
    			clazz=clazz.getSuperclass();
    		}
			Field fields[]=clazz.getDeclaredFields();
			for(Field field:fields){
				if(field.getName().equals("mainModel")||field.getName().equals("field_77045_g")){
					field.setAccessible(true);
					return (ModelBase) field.get(renderer);
				}
    		}
		
		} catch (SecurityException | IllegalArgumentException | IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	return null;
    }
    @Override
	public EntityPlayer getPlayerForSide(MessageContext ctx) {
		return ctx.side==Side.SERVER?ctx.getServerHandler().playerEntity:Minecraft.getMinecraft().thePlayer;
	}
    @Override
	public void preInit() {
		// TODO Auto-generated method stub
    	/*for(int i=1;i<ItemAmmo.AMMO_TYPES.length;i++){
    		if(i!=10 && i !=12){
    		ModelLoader.setCustomModelResourceLocation(TF2weapons.itemAmmo, i, new ModelResourceLocation(TF2weapons.MOD_ID+":ammo_"+ItemAmmo.AMMO_TYPES[i], "inventory"));
    		}
    	}
    	
    	ModelLoader.registerItemVariants(TF2weapons.itemTF2, new ModelResourceLocation(TF2weapons.MOD_ID+":copper_ingot", "inventory"),new ModelResourceLocation(TF2weapons.MOD_ID+":lead_ingot", "inventory"));
    	ModelLoader.setCustomModelResourceLocation(TF2weapons.itemAmmoFire, 0, new ModelResourceLocation(TF2weapons.MOD_ID+":ammo_fire", "inventory"));
    	ModelLoader.setCustomModelResourceLocation(TF2weapons.itemChocolate, 0, new ModelResourceLocation(TF2weapons.MOD_ID+":chocolate", "inventory"));
    	ModelLoader.setCustomModelResourceLocation(TF2weapons.itemHorn, 0, new ModelResourceLocation(TF2weapons.MOD_ID+":horn", "inventory"));
    	ModelLoader.setCustomModelResourceLocation(TF2weapons.itemAmmoMedigun, 0, new ModelResourceLocation(TF2weapons.MOD_ID+":ammo_medigun", "inventory"));
    	ModelLoader.setCustomModelResourceLocation(TF2weapons.itemTF2, 0, new ModelResourceLocation(TF2weapons.MOD_ID+":copper_ingot", "inventory"));
    	ModelLoader.setCustomModelResourceLocation(TF2weapons.itemTF2, 1, new ModelResourceLocation(TF2weapons.MOD_ID+":lead_ingot", "inventory"));
    	ModelLoader.setCustomModelResourceLocation(TF2weapons.itemTF2, 2, new ModelResourceLocation(TF2weapons.MOD_ID+":australium_ingot", "inventory"));
    	ModelLoader.setCustomModelResourceLocation(TF2weapons.itemTF2, 3, new ModelResourceLocation(TF2weapons.MOD_ID+":scrap_metal", "inventory"));
    	ModelLoader.setCustomModelResourceLocation(TF2weapons.itemTF2, 4, new ModelResourceLocation(TF2weapons.MOD_ID+":reclaimed_metal", "inventory"));
    	ModelLoader.setCustomModelResourceLocation(TF2weapons.itemTF2, 5, new ModelResourceLocation(TF2weapons.MOD_ID+":refined_metal", "inventory"));
    	ModelLoader.setCustomModelResourceLocation(TF2weapons.itemTF2, 6, new ModelResourceLocation(TF2weapons.MOD_ID+":australium_nugget", "inventory"));
    	ModelLoader.setCustomModelResourceLocation(TF2weapons.itemTF2, 7, new ModelResourceLocation(TF2weapons.MOD_ID+":key", "inventory"));
    	ModelLoader.setCustomModelResourceLocation(TF2weapons.itemTF2, 8, new ModelResourceLocation(TF2weapons.MOD_ID+":crate", "inventory"));
    	ModelLoader.setCustomModelResourceLocation(TF2weapons.itemTF2, 9, new ModelResourceLocation(TF2weapons.MOD_ID+":random_weapon", "inventory"));
    	ModelLoader.setCustomModelResourceLocation(TF2weapons.itemTF2, 10, new ModelResourceLocation(TF2weapons.MOD_ID+":random_hat", "inventory"));*/
    	RenderManager manager=RenderManager.instance;
    	RenderingRegistry.registerEntityRenderingHandler(EntityTF2Character.class,new RenderTF2Character());
    	/*RenderingRegistry.registerEntityRenderingHandler(EntityProjectileBase.class, new IRenderFactory<Entity>(){
			@Override
			public Render<Entity> createRenderFor(RenderManager manager) {
				// TODO Auto-generated method stub
				return new RenderEntity(manager);
			}
    	});*/
    	RenderingRegistry.registerEntityRenderingHandler(EntityRocket.class, new RenderRocket());
    	/*RenderingRegistry.registerEntityRenderingHandler(EntityFlame.class, new IRenderFactory<EntityFlame>(){
			@Override
			public Render<EntityFlame> createRenderFor(RenderManager manager) {
				// TODO Auto-generated method stub
				return (Render<EntityFlame>) new RenderEntity();
			}
    	});*/
    	RenderingRegistry.registerEntityRenderingHandler(EntityGrenade.class, new RenderGrenade());
    	
    	RenderingRegistry.registerEntityRenderingHandler(EntityStickybomb.class, new RenderStickybomb());
    	
    	RenderingRegistry.registerEntityRenderingHandler(EntitySyringe.class, new RenderSyringe());
    	
    	RenderingRegistry.registerEntityRenderingHandler(EntitySentry.class, new RenderSentry());
    	
    	RenderingRegistry.registerEntityRenderingHandler(EntityDispenser.class, new RenderDispenser());

    	RenderingRegistry.registerEntityRenderingHandler(EntityTeleporter.class, new RenderTeleporter());
    	
    	RenderingRegistry.registerEntityRenderingHandler(EntityStatue.class, new RenderStatue());
    	
    	RenderingRegistry.registerEntityRenderingHandler(EntitySaxtonHale.class, new RenderBiped(new ModelBiped(), 0.5F, 1.0F){
    		
				private final ResourceLocation TEXTURE=new ResourceLocation(TF2weapons.MOD_ID,"textures/entity/tf2/SaxtonHale.png");
				protected ResourceLocation getEntityTexture(Entity entity)
				{
					return TEXTURE;
			    }
				
			});
	}
    
    public static void playBuildingSound(BuildingSound sound){
    	Minecraft.getMinecraft().getSoundHandler().playSound(sound);
    }
    @Override
	public void registerTicks()
    {
    }
    public static void spawnFlameParticle(World world, EntityLivingBase ent, float step){
    	EntityFX entity=EntityFlameEffect.createNewEffect(world, ent, step);
		spawnParticle(world,entity);
	}
	public static void spawnBulletParticle(World world, EntityLivingBase living, double startX, double startY, double startZ, double endX, double endY, double endZ, int j,int crits){
		EntityFX entity=new EntityBulletTracer(world, startX, startY, startZ, endX, endY, endZ, j,crits,living);
		spawnParticle(world,entity);
	}
	public static void spawnCritParticle(World world, double pX, double pY, double pZ, int teamForDisplay) {
		EntityFX entity=new EntityCritEffect(world, pX, pY, pZ, teamForDisplay);
		spawnParticle(world,entity);
	}
	public static void spawnParticle(World world, EntityFX entity){
		if(Minecraft.getMinecraft() != null && Minecraft.getMinecraft().renderViewEntity != null && Minecraft.getMinecraft().effectRenderer != null){
			int i = Minecraft.getMinecraft().gameSettings.particleSetting;
	
	        if (i == 1 && world.rand.nextInt(3) == 0)
	            i = 2;
	        if (i > 1){
	        	entity.setDead();
	            return;
	        }
	        Minecraft.getMinecraft().effectRenderer.addEffect(entity);
		}
	}
	@Override
	public void playReloadSound(EntityLivingBase player,ItemStack stack){
		if(!Thread.currentThread().getName().equals("Client thread")||!(stack.getItem() instanceof ItemUsable)) return;
		//ResourceLocation soundName=new ResourceLocation(ItemUsable.getData(stack).get("Reload Sound").getString());
		ReloadSound sound=new ReloadSound(new ResourceLocation(ItemFromData.getSound(stack, PropertyType.RELOAD_SOUND)), player);
		if(ClientProxy.reloadSounds.get(player)!=null){
			ClientProxy.reloadSounds.get(player).done=true;
		}
		ClientProxy.reloadSounds.put(player, sound);
		Minecraft.getMinecraft().getSoundHandler().playSound(sound);
	}
	
	public static WeaponSound playWeaponSound(EntityLivingBase living,String path, boolean loop, int type,ItemStack stack){
		//System.out.println(sound.type);
		WeaponSound sound;
		if(loop)
			sound=new WeaponLoopSound(new ResourceLocation(path), living, type<2,ItemFromData.getData(stack),type==1,type);
		else
			sound=new WeaponSound(new ResourceLocation(path), living, type,ItemFromData.getData(stack));
		if(fireSounds.get(living)!=null){
			//Minecraft.getMinecraft().getSoundHandler().stopSound(fireSounds.get(living));
			fireSounds.get(living).setDone();
		}
		/*if(Thread.currentThread().getName().equals("Client thread")){
			Minecraft.getMinecraft().getSoundHandler().playSound(sound);
		}
		else{*/
			weaponSoundsToStart.add(sound);
		//}
		fireSounds.put(living, sound);
		return sound;
	}
	public static void removeReloadSound(EntityLivingBase entity) {
		if(reloadSounds.get(entity)!=null)
		reloadSounds.remove(entity).done=true;
	}
	public static void spawnRocketParticle(World world, EntityRocket rocket) {
		spawnParticle(world,new EntityRocketEffect(world, rocket));
	}
	public static class RenderCustomModel extends RendererLivingEntity{

		private ResourceLocation texture;
		public RenderCustomModel(ModelBase modelBaseIn, float shadowSizeIn) {
			super(modelBaseIn, shadowSizeIn);
			// TODO Auto-generated constructor stub
		}

		@Override
		protected ResourceLocation getEntityTexture(Entity entity) {
			// TODO Auto-generated method stub
			return texture;
		}
		public void setRenderOptions(ModelBase model, ResourceLocation texture){
			this.mainModel=model;
			this.texture=texture;
		}
	}
	public static EntityPlayer getLocalPlayer(){
		return Minecraft.getMinecraft().thePlayer;
	}
	public static void showGuiTeleporter(EntityTeleporter entityTeleporter) {
		Minecraft.getMinecraft().displayGuiScreen(new GuiTeleporter(entityTeleporter));
	}
	public static void displayScreenConfirm(String str1, String str2){
		Minecraft.getMinecraft().displayGuiScreen(new GuiConfirm(str1, str2));
	}
	public static void displayScreenJoinTeam(){
		final GuiScreen prevScreen=Minecraft.getMinecraft().currentScreen;
		Minecraft.getMinecraft().displayGuiScreen(new GuiYesNo(new GuiYesNoCallback(){

			@Override
			public void confirmClicked(boolean result, int id) {
				if(result)
					TF2weapons.network.sendToServer(new TF2Message.ActionMessage(16));
				else
					TF2weapons.network.sendToServer(new TF2Message.ActionMessage(17));
				Minecraft.getMinecraft().displayGuiScreen(prevScreen);
			}
			
		}, "Choose your team", "Before using the store, you need to join a team", "RED", "BLU", 0));
	}
}
