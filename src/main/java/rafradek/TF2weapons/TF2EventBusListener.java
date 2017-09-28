package rafradek.TF2weapons;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.client.event.ConfigChangedEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent.PlayerLoggedOutEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiMerchant;
import net.minecraft.client.gui.inventory.GuiContainerCreative;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.command.IEntitySelector;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraft.scoreboard.ScorePlayerTeam;
import net.minecraft.scoreboard.Team;
import net.minecraft.stats.Achievement;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.IIcon;
import net.minecraft.util.MathHelper;
import net.minecraft.village.MerchantRecipe;
import net.minecraftforge.client.event.FOVUpdateEvent;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.client.event.RenderHandEvent;
import net.minecraftforge.client.event.RenderLivingEvent;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.common.ISpecialArmor.ArmorProperties;
import net.minecraftforge.event.entity.EntityEvent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.living.LivingSetAttackTargetEvent;
import net.minecraftforge.event.entity.living.LivingSpawnEvent;
import net.minecraftforge.event.entity.player.EntityInteractEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.Action;
import net.minecraftforge.event.terraingen.OreGenEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.event.world.WorldEvent;
import rafradek.TF2weapons.building.EntityBuilding;
import rafradek.TF2weapons.building.EntityDispenser;
import rafradek.TF2weapons.building.EntitySentry;
import rafradek.TF2weapons.building.EntityTeleporter;
import rafradek.TF2weapons.characters.EntityDemoman;
import rafradek.TF2weapons.characters.EntityHeavy;
import rafradek.TF2weapons.characters.EntityMedic;
import rafradek.TF2weapons.characters.EntityPyro;
import rafradek.TF2weapons.characters.EntitySoldier;
import rafradek.TF2weapons.characters.EntityTF2Character;
import rafradek.TF2weapons.decoration.GuiWearables;
import rafradek.TF2weapons.decoration.InventoryWearables;
import rafradek.TF2weapons.message.TF2Message;
import rafradek.TF2weapons.weapons.ItemCloak;
import rafradek.TF2weapons.weapons.ItemDisguiseKit;
import rafradek.TF2weapons.weapons.ItemHorn;
import rafradek.TF2weapons.weapons.ItemMedigun;
import rafradek.TF2weapons.weapons.ItemMinigun;
import rafradek.TF2weapons.weapons.ItemSniperRifle;
import rafradek.TF2weapons.weapons.ItemSoldierBackpack;
import rafradek.TF2weapons.weapons.ItemUsable;
import rafradek.TF2weapons.weapons.ItemWeapon;
import rafradek.TF2weapons.weapons.WeaponsCapability;

public class TF2EventBusListener {
	public int tickleft;
	public static IIcon pelletIcon;
	boolean alreadypressed;
	private boolean alreadypressedalt;
	private boolean alreadypressedreload;
	// public ModelSkeleton skeletonModel=new ModelSkeleton();
	// private HashMap eligibleChunksForSpawning = new HashMap();
	public static final String[] STRANGE_TITLES = new String[] { "Strange", "Unremarkable", "Scarely lethal", "Mildly Menacing", "Somewhat threatening", "Uncharitable",
			"Notably dangerous", "Sufficiently lethal", "Truly feared", "Spectacularly lethal", "Gore-spatterer", "Wicked nasty", "Positively inhumane", "Totally ordinary",
			"Face-melting", "Rage-inducing", "Server-clearing", "Epic", "Legendary", "Australian", "Hale's own" };
	public static final int[] STRANGE_KILLS = new int[] { 0, 10, 25, 45, 70, 100, 135, 175, 225, 275, 350, 500, 750, 999, 1000, 1500, 2500, 5000, 7500, 7616, 8500 };
	public static HashMap<EntityLivingBase, EntityLivingBase> fakeEntities = new HashMap<EntityLivingBase, EntityLivingBase>();
	public static float tickTime = 0;

	/*
	 * @SubscribeEvent public void spawn(WorldEvent.PotentialSpawns event){ int
	 * time=(int) (event.world.getWorldInfo().getWorldTotalTime()/24000);
	 * if(MapList.scoutSpawn.containsKey(event.list)){
	 * MapList.scoutSpawn.get(event.list).itemWeight=time; } else{
	 * System.out.println("add"); SpawnListEntry entry=new
	 * SpawnListEntry(EntityScout.class, time, 1, 3); event.list.add(entry);
	 * MapList.scoutSpawn.put(event.list,entry); } }
	 */
	public static void disguise(EntityLivingBase entity, boolean active) {
		entity.getEntityData().setBoolean("IsDisguised", active);

		WeaponsCapability.get(entity).disguiseTicks = 0;
		if (!entity.worldObj.isRemote) {
			TF2weapons.network.sendToAllAround(new TF2Message.PropertyMessage("DisguiseType", entity.getEntityData().getString("DisguiseType"), entity),
					TF2weapons.pointFromEntity(entity));
			TF2weapons.network.sendToAllAround(new TF2Message.PropertyMessage("IsDisguised", active, entity), TF2weapons.pointFromEntity(entity));
		}
	}

	@SideOnly(Side.CLIENT)
	@SubscribeEvent
	public void registerIcons(TextureStitchEvent.Pre event) {
		// if(event.getMap().getGlTextureId()==1){
		// System.out.println("Registered icon:
		// "+event.getMap().getGlTextureId());
		ItemFromData.addedIcons = false;
		if (event.map.getTextureType() == 1) {
			pelletIcon = event.map.registerIcon("rafradek_tf2_weapons:pellet3");
		}
		/*
		 * pelletIcon=event.map.registerSprite(new
		 * ResourceLocation(TF2weapons.MOD_ID,"items/pellet3"));
		 * event.map.registerSprite(new
		 * ResourceLocation(TF2weapons.MOD_ID,"items/ammo_belt_empty"));
		 */
		// }
	}

	@SideOnly(Side.CLIENT)
	@SubscribeEvent
	public void clientTickEnd(TickEvent.ClientTickEvent event) {

		Minecraft minecraft = Minecraft.getMinecraft();

		if (event.phase == TickEvent.Phase.END) {
			/*
			 * Iterator<Entry<EntityLivingBase,EntityLivingBase>>
			 * iterator=fakeEntities.entrySet().iterator();
			 * while(iterator.hasNext()){
			 * Entry<EntityLivingBase,EntityLivingBase> entry=iterator.next();
			 * EntityLivingBase real=entry.getKey(); EntityLivingBase
			 * fake=entry.getValue(); fake.posX=real.posX; fake.posY=real.posY;
			 * fake.posZ=real.posZ; fake.prevPosX=real.prevPosX;
			 * fake.prevPosY=real.prevPosY; fake.prevPosZ=real.prevPosZ;
			 * fake.rotationPitch=real.rotationPitch;
			 * fake.rotationYaw=real.rotationYaw;
			 * fake.rotationYawHead=real.rotationYawHead;
			 * fake.motionX=real.motionX; fake.motionY=real.motionY;
			 * fake.motionZ=real.motionZ;
			 * //System.out.println("pos: "+fake.posX+" "+fake.posY+" "+fake.
			 * posZ); }
			 */
			Iterator<EntityLivingBase> soundIterator = ClientProxy.soundsToStart.keySet().iterator();
			while (soundIterator.hasNext()) {
				EntityLivingBase living = soundIterator.next();
				TF2weapons.proxy.playReloadSound(living, ClientProxy.soundsToStart.get(living));
				soundIterator.remove();
			}
			while (ClientProxy.weaponSoundsToStart.size() > 0) {
				minecraft.getSoundHandler().playSound(ClientProxy.weaponSoundsToStart.get(0));
				ClientProxy.weaponSoundsToStart.remove(0);
			}
			if (minecraft.currentScreen == null && minecraft.thePlayer.getHeldItem() != null) {
				if (minecraft.thePlayer.getHeldItem().getItem() instanceof ItemUsable) {

					boolean changed = false;
					ItemStack item = minecraft.thePlayer.getHeldItem();

					if (minecraft.gameSettings.keyBindAttack.getIsKeyPressed() && !this.alreadypressed) {
						changed = true;
						this.alreadypressed = true;
					}
					if (!minecraft.gameSettings.keyBindAttack.getIsKeyPressed() && this.alreadypressed) {
						changed = true;
						this.alreadypressed = false;
					}
					if (minecraft.gameSettings.keyBindUseItem.getIsKeyPressed() && !this.alreadypressedalt) {
						changed = true;
						this.alreadypressedalt = true;
					}
					if (!minecraft.gameSettings.keyBindUseItem.getIsKeyPressed() && this.alreadypressedalt) {
						changed = true;
						this.alreadypressedalt = false;
					}
					if (ClientProxy.reload.getIsKeyPressed() && !this.alreadypressedreload) {
						changed = true;
						this.alreadypressedreload = true;
					}
					if (!ClientProxy.reload.getIsKeyPressed() && this.alreadypressedreload) {
						changed = true;
						this.alreadypressedreload = false;
					}
					if (changed) {
						EntityLivingBase player = minecraft.thePlayer;
						WeaponsCapability cap = WeaponsCapability.get(minecraft.thePlayer);
						int oldState = cap.state & 3;
						int plus = cap.state & 8;
						int state = this.getActionType() + plus;
						cap.state = state;
						if (player.getHeldItem() != null && player.getHeldItem().getItem() instanceof ItemUsable && oldState != (this.getActionType() & 3)
								&& player.getHeldItem().getTagCompound().getByte("active") == 2) {
							if ((oldState & 2) < (state & 2)) {
								cap.stateDo(player, player.getHeldItem());
								((ItemUsable) player.getHeldItem().getItem()).startUse(player.getHeldItem(), player, player.worldObj, oldState, state & 3);
							} else if ((oldState & 2) > (state & 2)) {
								((ItemUsable) player.getHeldItem().getItem()).endUse(player.getHeldItem(), player, player.worldObj, oldState, state & 3);
							}
							if ((oldState & 1) < (state & 1)) {
								cap.stateDo(player, player.getHeldItem());
								((ItemUsable) player.getHeldItem().getItem()).startUse(player.getHeldItem(), player, player.worldObj, oldState, state & 3);
							} else if ((oldState & 1) > (state & 1)) {
								((ItemUsable) player.getHeldItem().getItem()).endUse(player.getHeldItem(), player, player.worldObj, oldState, state & 3);
							}
						}
						TF2weapons.network.sendToServer(new TF2Message.ActionMessage(this.getActionType()));
					}
				}
			}
			// ItemUsable.tick(true);
		}
	}

	@SideOnly(Side.CLIENT)
	public int getActionType() {
		Minecraft minecraft = Minecraft.getMinecraft();
		int value = 0;
		if (minecraft.gameSettings.keyBindAttack.getIsKeyPressed()) {
			value++;
		}
		if (minecraft.gameSettings.keyBindUseItem.getIsKeyPressed()) {
			value += 2;
		}
		if (ClientProxy.reload.getIsKeyPressed()) {
			value += 4;
		}
		return value;
	}

	@SideOnly(Side.CLIENT)
	@SubscribeEvent
	public void getFov(FOVUpdateEvent event) {
		if (event.entity.getHeldItem() != null && event.entity.getHeldItem().getItem() instanceof ItemUsable) {
			if (event.entity.getHeldItem().getItem() instanceof ItemSniperRifle && WeaponsCapability.get(event.entity).charging) {
				event.newfov = event.fov * 0.55f;
			} else if (event.entity.getEntityAttribute(SharedMonsterAttributes.movementSpeed).getModifier(ItemMinigun.slowdownUUID) != null) {
				event.newfov = event.fov * 1.4f;
			}
		}
	}

	@SideOnly(Side.CLIENT)
	@SubscribeEvent
	public void guiPostInit(GuiScreenEvent.InitGuiEvent.Post event) {
		if (Minecraft.getMinecraft().thePlayer != null) {
			if ((event.gui instanceof GuiInventory || event.gui instanceof GuiContainerCreative || event.gui instanceof GuiWearables)
					&& !InventoryWearables.get(Minecraft.getMinecraft().thePlayer).isEmpty()) {
				// GuiContainer gui = (GuiContainer) event.gui;
				event.buttonList.add(new GuiButton(97535627, event.gui.width / 2 - 10, event.gui.height / 2 - 140, 20, 20, "W"));
			}

			if (event.gui instanceof GuiMerchant) {
				// System.out.println(((MerchantRecipe)((GuiMerchant)event.gui).func_147035_g().getRecipes(Minecraft.getMinecraft().thePlayer).get(0)).getItemToBuy());
				// if
				// (((GuiMerchant)event.gui)..equals(I18n.format("entity.rafradek_tf2_weapons.hale.name")))
				// event.buttonList.add(new GuiButton(7578,
				// event.gui.width/2-50, event.gui.height/2-110, 100, 20,
				// "Change Team"));
			}
			WeaponsCapability.get(Minecraft.getMinecraft().thePlayer).state &= 8;
		}
	}

	@SideOnly(Side.CLIENT)
	@SubscribeEvent
	public void guiPostAction(GuiScreenEvent.ActionPerformedEvent.Post event) {

		if (event.gui instanceof GuiInventory || event.gui instanceof GuiContainerCreative) {
			if (event.button.id == 97535627) {
				// Minecraft.getMinecraft().displayGuiScreen(null);
				TF2weapons.network.sendToServer(new TF2Message.ShowGuiMessage(0));
			}
		}

		if (event.gui instanceof GuiWearables) {
			if (event.button.id == 97535627) {
				event.gui.mc.displayGuiScreen(new GuiInventory(event.gui.mc.thePlayer));
				// PacketHandler.INSTANCE.sendToServer(new
				// PacketOpenNormalInventory(event.gui.mc.thePlayer));
			}
		}
		if (event.gui instanceof GuiMerchant && event.button.id == 7578) {
			ClientProxy.displayScreenJoinTeam();
		}
	}

	/*
	 * @SideOnly(Side.CLIENT)
	 * 
	 * @SubscribeEvent public void applyRecoil(EntityViewRenderEvent.CameraSetup
	 * event){ if(event.entity.getExtendedProperties(WeaponsCapability.ID) !=
	 * null){ WeaponsCapability cap=event.entityWeaponsCapability.get();
	 * event.setPitch(event.getPitch()-cap.recoil); if(cap.recoil>0){
	 * cap.recoil=Math.max((cap.recoil*0.8f)-0.06f,0); } } }
	 */
	@SideOnly(Side.CLIENT)
	@SubscribeEvent
	public void renderOverlay(RenderGameOverlayEvent.Pre event) {
		EntityPlayer player = Minecraft.getMinecraft().thePlayer;
		WeaponsCapability cap = WeaponsCapability.get(player);
		GL11.glEnable(GL11.GL_BLEND);
		OpenGlHelper.glBlendFunc(770, 771, 1, 0);
		if (event.type == ElementType.HELMET && player != null && player.getHeldItem() != null && player.getHeldItem().getItem() instanceof ItemSniperRifle && cap.charging) {
			// System.out.println("drawing");
			GL11.glDisable(GL11.GL_DEPTH_TEST);
			GL11.glDepthMask(false);
			GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
			GL11.glDisable(GL11.GL_ALPHA_TEST);
			// Minecraft.getMinecraft().ingameGUI.drawTexturedModalRect(xCoord,
			// yCoord, textureSprite, widthIn, heightIn);
			Minecraft.getMinecraft().getTextureManager().bindTexture(ClientProxy.scopeTexture);
			double widthDrawStart = (double) (event.resolution.getScaledWidth() - event.resolution.getScaledHeight()) / 2;
			double widthDrawEnd = widthDrawStart + event.resolution.getScaledHeight();
			Tessellator tessellator = Tessellator.instance;

			tessellator.startDrawingQuads();
			tessellator.addVertexWithUV(widthDrawStart, event.resolution.getScaledHeight(), -90.0D, 0.0D, 1.0D);
			tessellator.addVertexWithUV(widthDrawEnd, event.resolution.getScaledHeight(), -90.0D, 1.0D, 1.0D);
			tessellator.addVertexWithUV(widthDrawEnd, 0.0D, -90.0D, 1.0D, 0.0D);
			tessellator.addVertexWithUV(widthDrawStart, 0.0D, -90.0D, 0.0D, 0.0D);
			tessellator.draw();
			Minecraft.getMinecraft().getTextureManager().bindTexture(ClientProxy.blackTexture);
			tessellator.startDrawingQuads();
			tessellator.addVertexWithUV(0, event.resolution.getScaledHeight(), -90.0D, 0d, 1d);
			tessellator.addVertexWithUV(widthDrawStart, event.resolution.getScaledHeight(), -90.0D, 1d, 1d);
			tessellator.addVertexWithUV(widthDrawStart, 0.0D, -90.0D, 1d, 0d);
			tessellator.addVertexWithUV(0, 0.0D, -90.0D, 0d, 0d);
			tessellator.draw();
			tessellator.startDrawingQuads();
			tessellator.addVertexWithUV(widthDrawEnd, event.resolution.getScaledHeight(), -90.0D, 0d, 1d);
			tessellator.addVertexWithUV(event.resolution.getScaledWidth(), event.resolution.getScaledHeight(), -90.0D, 1d, 1d);
			tessellator.addVertexWithUV(event.resolution.getScaledWidth(), 0.0D, -90.0D, 1d, 0d);
			tessellator.addVertexWithUV(widthDrawEnd, 0.0D, -90.0D, 0d, 0d);
			tessellator.draw();
			Minecraft.getMinecraft().getTextureManager().bindTexture(ClientProxy.chargeTexture);
			GL11.glColor4f(0.5F, 0.5F, 0.5F, 0.7F);
			tessellator.startDrawingQuads();
			tessellator.addVertexWithUV((double) event.resolution.getScaledWidth() / 2 + 50, (double) event.resolution.getScaledHeight() / 2 + 15, -90.0D, 0d, 0.25d);
			tessellator.addVertexWithUV((double) event.resolution.getScaledWidth() / 2 + 100, (double) event.resolution.getScaledHeight() / 2 + 15, -90.0D, 0.508d, 0.25d);
			tessellator.addVertexWithUV((double) event.resolution.getScaledWidth() / 2 + 100, (double) event.resolution.getScaledHeight() / 2, -90.0D, 0.508d, 0d);
			tessellator.addVertexWithUV((double) event.resolution.getScaledWidth() / 2 + 50, (double) event.resolution.getScaledHeight() / 2, -90.0D, 0d, 0d);
			tessellator.draw();
			if (cap.chargeTicks >= 20) {
				tessellator.startDrawingQuads();
				tessellator.addVertexWithUV((double) event.resolution.getScaledWidth() / 2 + 110, (double) event.resolution.getScaledHeight() / 2 + 18, -90.0D, 0d, 0.57d);
				tessellator.addVertexWithUV((double) event.resolution.getScaledWidth() / 2 + 121, (double) event.resolution.getScaledHeight() / 2 + 18, -90.0D, 0.125d, 0.57d);
				tessellator.addVertexWithUV((double) event.resolution.getScaledWidth() / 2 + 121, (double) event.resolution.getScaledHeight() / 2 - 3, -90.0D, 0.125d, 0.25d);
				tessellator.addVertexWithUV((double) event.resolution.getScaledWidth() / 2 + 110, (double) event.resolution.getScaledHeight() / 2 - 3, -90.0D, 0d, 0.25d);
				tessellator.draw();
			}
			double progress = cap.chargeTicks / ItemSniperRifle.getChargeTime(player.getHeldItem(), player);
			GL11.glColor4f(1F, 1F, 1F, 1F);
			tessellator.startDrawingQuads();
			tessellator.addVertexWithUV((double) event.resolution.getScaledWidth() / 2 + 50, (double) event.resolution.getScaledHeight() / 2 + 15, -90.0D, 0d, 0.25d);
			tessellator.addVertexWithUV((double) event.resolution.getScaledWidth() / 2 + 50 + progress * 50, (double) event.resolution.getScaledHeight() / 2 + 15, -90.0D,
					progress * 0.508d, 0.25d);
			tessellator.addVertexWithUV((double) event.resolution.getScaledWidth() / 2 + 50 + progress * 50, (double) event.resolution.getScaledHeight() / 2, -90.0D,
					progress * 0.508d, 0d);
			tessellator.addVertexWithUV((double) event.resolution.getScaledWidth() / 2 + 50, (double) event.resolution.getScaledHeight() / 2, -90.0D, 0d, 0d);
			tessellator.draw();
			if (progress == 1d) {
				tessellator.startDrawingQuads();
				tessellator.addVertexWithUV((double) event.resolution.getScaledWidth() / 2 + 110, (double) event.resolution.getScaledHeight() / 2 + 18, -90.0D, 0d, 0.57d);
				tessellator.addVertexWithUV((double) event.resolution.getScaledWidth() / 2 + 121, (double) event.resolution.getScaledHeight() / 2 + 18, -90.0D, 0.125d, 0.57d);
				tessellator.addVertexWithUV((double) event.resolution.getScaledWidth() / 2 + 121, (double) event.resolution.getScaledHeight() / 2 - 3, -90.0D, 0.125d, 0.25d);
				tessellator.addVertexWithUV((double) event.resolution.getScaledWidth() / 2 + 110, (double) event.resolution.getScaledHeight() / 2 - 3, -90.0D, 0d, 0.25d);
				tessellator.draw();
			}
			GL11.glDepthMask(true);
			GL11.glEnable(GL11.GL_DEPTH_TEST);
			GL11.glEnable(GL11.GL_ALPHA_TEST);
			GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		}
		if (event.type == ElementType.HOTBAR && player != null && player.getHeldItem() != null && player.getHeldItem().getItem() instanceof ItemMedigun) {
			Minecraft.getMinecraft().getTextureManager().bindTexture(ClientProxy.healingTexture);
			Tessellator tessellator = Tessellator.instance;
			GL11.glDisable(GL11.GL_DEPTH_TEST);
			GL11.glDepthMask(false);
			GL11.glDisable(GL11.GL_ALPHA_TEST);
			GL11.glColor4f(1.0F, 1.0F, 1.0F, 0.7F);

			tessellator.startDrawingQuads();
			tessellator.addVertexWithUV(event.resolution.getScaledWidth() - 140, event.resolution.getScaledHeight() - 18, 0.0D, 0.0D, 0.265625D);
			tessellator.addVertexWithUV(event.resolution.getScaledWidth() - 12, event.resolution.getScaledHeight() - 18, 0.0D, 1.0D, 0.265625D);
			tessellator.addVertexWithUV(event.resolution.getScaledWidth() - 12, event.resolution.getScaledHeight() - 52, 0.0D, 1.0D, 0.0D);
			tessellator.addVertexWithUV(event.resolution.getScaledWidth() - 140, event.resolution.getScaledHeight() - 52, 0.0D, 0.0D, 0.0D);
			tessellator.draw();

			Entity healTarget = player.worldObj.getEntityByID(cap.healTarget);
			if (healTarget != null && healTarget instanceof EntityLivingBase) {
				EntityLivingBase living = (EntityLivingBase) healTarget;
				GL11.glColor4f(1.0F, 1.0F, 1.0F, 0.7F);
				// Minecraft.getMinecraft().ingameGUI.drawTexturedModalRect(event.resolution.getScaledWidth()/2-64,
				// event.resolution.getScaledHeight()/2+35, 0, 0, 128, 40);
				tessellator.startDrawingQuads();
				tessellator.addVertexWithUV(event.resolution.getScaledWidth() / 2 - 64, event.resolution.getScaledHeight() / 2 + 72, 0.0D, 0.0D, 0.265625D);
				tessellator.addVertexWithUV(event.resolution.getScaledWidth() / 2 + 64, event.resolution.getScaledHeight() / 2 + 72, 0.0D, 1.0D, 0.265625D);
				tessellator.addVertexWithUV(event.resolution.getScaledWidth() / 2 + 64, event.resolution.getScaledHeight() / 2 + 38, 0.0D, 1.0D, 0.0D);
				tessellator.addVertexWithUV(event.resolution.getScaledWidth() / 2 - 64, event.resolution.getScaledHeight() / 2 + 38, 0.0D, 0.0D, 0.0D);
				tessellator.draw();
				float overheal = 1f + living.getAbsorptionAmount() / living.getMaxHealth();
				if (overheal > 1f) {
					GL11.glColor4f(1.0F, 1.0F, 1.0F, 0.4F);
					tessellator.startDrawingQuads();
					tessellator.addVertexWithUV(event.resolution.getScaledWidth() / 2 - 47 - 10 * overheal, event.resolution.getScaledHeight() / 2 + 55 + 10 * overheal, 0.0D, 0.0D,
							0.59375D);
					tessellator.addVertexWithUV(event.resolution.getScaledWidth() / 2 - 47 + 10 * overheal, event.resolution.getScaledHeight() / 2 + 55 + 10 * overheal, 0.0D,
							0.28125D, 0.59375D);
					tessellator.addVertexWithUV(event.resolution.getScaledWidth() / 2 - 47 + 10 * overheal, event.resolution.getScaledHeight() / 2 + 55 - 10 * overheal, 0.0D,
							0.28125D, 0.3125D);
					tessellator.addVertexWithUV(event.resolution.getScaledWidth() / 2 - 47 - 10 * overheal, event.resolution.getScaledHeight() / 2 + 55 - 10 * overheal, 0.0D, 0.0D,
							0.3125D);
					tessellator.draw();
				}
				GL11.glColor4f(0.12F, 0.12F, 0.12F, 1F);
				tessellator.startDrawingQuads();
				tessellator.addVertexWithUV(event.resolution.getScaledWidth() / 2 - 58.3, event.resolution.getScaledHeight() / 2 + 66.4, 0.0D, 0.0D, 0.59375D);
				tessellator.addVertexWithUV(event.resolution.getScaledWidth() / 2 - 35.7, event.resolution.getScaledHeight() / 2 + 66.4, 0.0D, 0.28125D, 0.59375D);
				tessellator.addVertexWithUV(event.resolution.getScaledWidth() / 2 - 35.7, event.resolution.getScaledHeight() / 2 + 43.6, 0.0D, 0.28125D, 0.3125D);
				tessellator.addVertexWithUV(event.resolution.getScaledWidth() / 2 - 58.3, event.resolution.getScaledHeight() / 2 + 43.6, 0.0D, 0.0D, 0.3125D);
				tessellator.draw();
				float health = living.getHealth() / living.getMaxHealth();
				if (health > 0.33f) {
					GL11.glColor4f(0.9F, 0.9F, 0.9F, 1F);
				} else {
					GL11.glColor4f(0.85F, 0.0F, 0.0F, 1F);
				}
				tessellator.startDrawingQuads();
				tessellator.addVertexWithUV(event.resolution.getScaledWidth() / 2 - 57, event.resolution.getScaledHeight() / 2 + 65, 0.0D, 0.0D, 0.59375D);
				tessellator.addVertexWithUV(event.resolution.getScaledWidth() / 2 - 37, event.resolution.getScaledHeight() / 2 + 65, 0.0D, 0.28125D, 0.59375D);
				tessellator.addVertexWithUV(event.resolution.getScaledWidth() / 2 - 37, event.resolution.getScaledHeight() / 2 + 65 - health * 20, 0.0D, 0.28125D,
						0.59375D - 0.28125D * health);
				tessellator.addVertexWithUV(event.resolution.getScaledWidth() / 2 - 57, event.resolution.getScaledHeight() / 2 + 65 - health * 20, 0.0D, 0.0D,
						0.59375D - 0.28125D * health);
				tessellator.draw();
				Minecraft.getMinecraft().ingameGUI.drawString(Minecraft.getMinecraft().fontRenderer, "Healing:", event.resolution.getScaledWidth() / 2 - 28,
						event.resolution.getScaledHeight() / 2 + 42, 16777215);
				Minecraft.getMinecraft().ingameGUI.drawString(Minecraft.getMinecraft().fontRenderer, I18n.format(living.getCommandSenderName()),
						event.resolution.getScaledWidth() / 2 - 28, event.resolution.getScaledHeight() / 2 + 54, 16777215);

			}

			float uber = player.getHeldItem().getTagCompound().getFloat("ubercharge");
			Minecraft.getMinecraft().ingameGUI.drawString(Minecraft.getMinecraft().fontRenderer, "UBERCHARGE: " + Math.round(uber * 100f) + "%",
					event.resolution.getScaledWidth() - 130, event.resolution.getScaledHeight() - 48, 16777215);
			GL11.glDisable(GL11.GL_TEXTURE_2D);
			GL11.glColor4f(1.0F, 1.0F, 1.0F, 0.33F);
			tessellator.startDrawingQuads();
			tessellator.addVertex(event.resolution.getScaledWidth() - 132, event.resolution.getScaledHeight() - 22, 0.0D);
			tessellator.addVertex(event.resolution.getScaledWidth() - 20, event.resolution.getScaledHeight() - 22, 0.0D);
			tessellator.addVertex(event.resolution.getScaledWidth() - 20, event.resolution.getScaledHeight() - 36, 0.0D);
			tessellator.addVertex(event.resolution.getScaledWidth() - 132, event.resolution.getScaledHeight() - 36, 0.0D);
			tessellator.draw();

			GL11.glColor4f(1.0F, 1.0F, 1.0F, 0.85F);
			tessellator.startDrawingQuads();
			tessellator.addVertex(event.resolution.getScaledWidth() - 132, event.resolution.getScaledHeight() - 22, 0.0D);
			tessellator.addVertex(event.resolution.getScaledWidth() - 132 + 112 * uber, event.resolution.getScaledHeight() - 22, 0.0D);
			tessellator.addVertex(event.resolution.getScaledWidth() - 132 + 112 * uber, event.resolution.getScaledHeight() - 36, 0.0D);
			tessellator.addVertex(event.resolution.getScaledWidth() - 132, event.resolution.getScaledHeight() - 36, 0.0D);
			tessellator.draw();
			GL11.glEnable(GL11.GL_TEXTURE_2D);
			GL11.glDepthMask(true);
			GL11.glEnable(GL11.GL_DEPTH_TEST);
			GL11.glEnable(GL11.GL_ALPHA_TEST);
			GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		}
		Entity mouseTarget = Minecraft.getMinecraft().objectMouseOver != null ? Minecraft.getMinecraft().objectMouseOver.entityHit : null;
		if (event.type == ElementType.HOTBAR && player != null && mouseTarget != null && mouseTarget instanceof EntityBuilding && TF2weapons.isOnSameTeam(player, mouseTarget)) {
			Minecraft.getMinecraft().getTextureManager().bindTexture(ClientProxy.buildingTexture);
			Tessellator tessellator = Tessellator.instance;
			GL11.glDisable(GL11.GL_DEPTH_TEST);
			GL11.glDepthMask(false);
			GL11.glDisable(GL11.GL_ALPHA_TEST);
			GL11.glColor4f(1.0F, 1.0F, 1.0F, 0.7F);
			if (mouseTarget instanceof EntitySentry) {
				EntitySentry sentry = (EntitySentry) mouseTarget;
				// GL11.glColor4f(1.0F, 1.0F, 1.0F, 0.7F);
				// Minecraft.getMinecraft().ingameGUI.drawTexturedModalRect(event.resolution.getScaledWidth()/2-64,
				// event.resolution.getScaledHeight()/2+35, 0, 0, 128, 40);
				tessellator.startDrawingQuads();
				tessellator.addVertexWithUV(event.resolution.getScaledWidth() / 2 - 72, event.resolution.getScaledHeight() / 2 + 84, 0.0D, 0.0D, 0.4375D);
				tessellator.addVertexWithUV(event.resolution.getScaledWidth() / 2 + 72, event.resolution.getScaledHeight() / 2 + 84, 0.0D, 0.5625D, 0.4375D);
				tessellator.addVertexWithUV(event.resolution.getScaledWidth() / 2 + 72, event.resolution.getScaledHeight() / 2 + 20, 0.0D, 0.5625D, 0.1875D);
				tessellator.addVertexWithUV(event.resolution.getScaledWidth() / 2 - 72, event.resolution.getScaledHeight() / 2 + 20, 0.0D, 0.0D, 0.1875D);
				tessellator.draw();
				double imagePos = sentry.getLevel() == 1 ? 0.375D : sentry.getLevel() == 2 ? 0.1875D : 0D;

				tessellator.startDrawingQuads();
				tessellator.addVertexWithUV(event.resolution.getScaledWidth() / 2 - 53, event.resolution.getScaledHeight() / 2 + 76, 0.0D, 0.75D, imagePos + 0.1875D);
				tessellator.addVertexWithUV(event.resolution.getScaledWidth() / 2 - 5, event.resolution.getScaledHeight() / 2 + 76, 0.0D, 0.9375D, imagePos + 0.1875D);
				tessellator.addVertexWithUV(event.resolution.getScaledWidth() / 2 - 5, event.resolution.getScaledHeight() / 2 + 28, 0.0D, 0.9375D, imagePos);
				tessellator.addVertexWithUV(event.resolution.getScaledWidth() / 2 - 53, event.resolution.getScaledHeight() / 2 + 28, 0.0D, 0.75D, imagePos);
				tessellator.draw();

				imagePos = sentry.getLevel() == 3 ? 0D : 0.0625D;
				tessellator.startDrawingQuads();
				tessellator.addVertexWithUV(event.resolution.getScaledWidth() / 2 - 5, event.resolution.getScaledHeight() / 2 + 77, 0.0D, 0.9375D, 0.0625D + imagePos);
				tessellator.addVertexWithUV(event.resolution.getScaledWidth() / 2 + 11, event.resolution.getScaledHeight() / 2 + 77, 0.0D, 1D, 0.0625D + imagePos);
				tessellator.addVertexWithUV(event.resolution.getScaledWidth() / 2 + 11, event.resolution.getScaledHeight() / 2 + 61, 0.0D, 1D, imagePos);
				tessellator.addVertexWithUV(event.resolution.getScaledWidth() / 2 - 5, event.resolution.getScaledHeight() / 2 + 61, 0.0D, 0.9375D, imagePos);
				tessellator.draw();

				tessellator.startDrawingQuads();
				tessellator.addVertexWithUV(event.resolution.getScaledWidth() / 2 - 5, event.resolution.getScaledHeight() / 2 + 41, 0.0D, 0.9375D, 0.25D);
				tessellator.addVertexWithUV(event.resolution.getScaledWidth() / 2 + 11, event.resolution.getScaledHeight() / 2 + 41, 0.0D, 1D, 0.25D);
				tessellator.addVertexWithUV(event.resolution.getScaledWidth() / 2 + 11, event.resolution.getScaledHeight() / 2 + 25, 0.0D, 1D, 0.1875D);
				tessellator.addVertexWithUV(event.resolution.getScaledWidth() / 2 - 5, event.resolution.getScaledHeight() / 2 + 25, 0.0D, 0.9375D, 0.1875D);
				tessellator.draw();

				tessellator.startDrawingQuads();
				tessellator.addVertexWithUV(event.resolution.getScaledWidth() / 2 - 5, event.resolution.getScaledHeight() / 2 + 59, 0.0D, 0.9375D, 0.1875D);
				tessellator.addVertexWithUV(event.resolution.getScaledWidth() / 2 + 11, event.resolution.getScaledHeight() / 2 + 59, 0.0D, 1D, 0.1875D);
				tessellator.addVertexWithUV(event.resolution.getScaledWidth() / 2 + 11, event.resolution.getScaledHeight() / 2 + 43, 0.0D, 1D, 0.125D);
				tessellator.addVertexWithUV(event.resolution.getScaledWidth() / 2 - 5, event.resolution.getScaledHeight() / 2 + 43, 0.0D, 0.9375D, 0.125D);
				tessellator.draw();

				imagePos = sentry.getLevel() == 1 ? 0.3125D : sentry.getLevel() == 2 ? 0.375D : 0.4375D;
				tessellator.startDrawingQuads();
				tessellator.addVertexWithUV(event.resolution.getScaledWidth() / 2 - 22, event.resolution.getScaledHeight() / 2 + 38, 0.0D, 0.9375D, 0.0625D + imagePos);
				tessellator.addVertexWithUV(event.resolution.getScaledWidth() / 2 - 6, event.resolution.getScaledHeight() / 2 + 38, 0.0D, 1D, 0.0625D + imagePos);
				tessellator.addVertexWithUV(event.resolution.getScaledWidth() / 2 - 6, event.resolution.getScaledHeight() / 2 + 22, 0.0D, 1D, imagePos);
				tessellator.addVertexWithUV(event.resolution.getScaledWidth() / 2 - 22, event.resolution.getScaledHeight() / 2 + 22, 0.0D, 0.9375D, imagePos);
				tessellator.draw();

				Minecraft.getMinecraft().ingameGUI.drawString(Minecraft.getMinecraft().fontRenderer, Integer.toString(sentry.getKills()),
						event.resolution.getScaledWidth() / 2 + 13, event.resolution.getScaledHeight() / 2 + 29, 16777215);
				float health = sentry.getHealth() / sentry.getMaxHealth();
				if (health > 0.33f) {
					GL11.glColor4f(0.9F, 0.9F, 0.9F, 1F);
				} else {
					GL11.glColor4f(0.85F, 0.0F, 0.0F, 1F);
				}
				GL11.glDisable(GL11.GL_TEXTURE_2D);
				for (int i = 0; i < health * 11; i++) {

					tessellator.startDrawingQuads();
					tessellator.addVertex(event.resolution.getScaledWidth() / 2 - 53, event.resolution.getScaledHeight() / 2 + 75 - i * 5, 0.0D);
					tessellator.addVertex(event.resolution.getScaledWidth() / 2 - 63, event.resolution.getScaledHeight() / 2 + 75 - i * 5, 0.0D);
					tessellator.addVertex(event.resolution.getScaledWidth() / 2 - 63, event.resolution.getScaledHeight() / 2 + 79 - i * 5, 0.0D);
					tessellator.addVertex(event.resolution.getScaledWidth() / 2 - 53, event.resolution.getScaledHeight() / 2 + 79 - i * 5, 0.0D);
					tessellator.draw();
				}

				GL11.glColor4f(1.0F, 1.0F, 1.0F, 0.33F);
				tessellator.startDrawingQuads();
				tessellator.addVertex(event.resolution.getScaledWidth() / 2 + 13, event.resolution.getScaledHeight() / 2 + 58, 0.0D);
				tessellator.addVertex(event.resolution.getScaledWidth() / 2 + 68, event.resolution.getScaledHeight() / 2 + 58, 0.0D);
				tessellator.addVertex(event.resolution.getScaledWidth() / 2 + 68, event.resolution.getScaledHeight() / 2 + 44, 0.0D);
				tessellator.addVertex(event.resolution.getScaledWidth() / 2 + 13, event.resolution.getScaledHeight() / 2 + 44, 0.0D);
				tessellator.draw();

				tessellator.startDrawingQuads();
				tessellator.addVertex(event.resolution.getScaledWidth() / 2 + 13, event.resolution.getScaledHeight() / 2 + 76, 0.0D);
				tessellator.addVertex(event.resolution.getScaledWidth() / 2 + 68, event.resolution.getScaledHeight() / 2 + 76, 0.0D);
				tessellator.addVertex(event.resolution.getScaledWidth() / 2 + 68, event.resolution.getScaledHeight() / 2 + 62, 0.0D);
				tessellator.addVertex(event.resolution.getScaledWidth() / 2 + 13, event.resolution.getScaledHeight() / 2 + 62, 0.0D);
				tessellator.draw();

				GL11.glColor4f(1.0F, 1.0F, 1.0F, 0.85F);
				tessellator.startDrawingQuads();
				tessellator.addVertex(event.resolution.getScaledWidth() / 2 + 13, event.resolution.getScaledHeight() / 2 + 58, 0.0D);
				tessellator.addVertex(event.resolution.getScaledWidth() / 2 + 13 + (double) sentry.getAmmo() / (double) sentry.getMaxAmmo() * 55D,
						event.resolution.getScaledHeight() / 2 + 58, 0.0D);
				tessellator.addVertex(event.resolution.getScaledWidth() / 2 + 13 + (double) sentry.getAmmo() / (double) sentry.getMaxAmmo() * 55D,
						event.resolution.getScaledHeight() / 2 + 44, 0.0D);
				tessellator.addVertex(event.resolution.getScaledWidth() / 2 + 13, event.resolution.getScaledHeight() / 2 + 44, 0.0D);
				tessellator.draw();

				double xOffset = sentry.getLevel() < 3 ? sentry.getProgress() * 0.275D : sentry.getRocketAmmo() * 2.75D;
				tessellator.startDrawingQuads();
				tessellator.addVertex(event.resolution.getScaledWidth() / 2 + 13, event.resolution.getScaledHeight() / 2 + 76, 0.0D);
				tessellator.addVertex(event.resolution.getScaledWidth() / 2 + 13 + xOffset, event.resolution.getScaledHeight() / 2 + 76, 0.0D);
				tessellator.addVertex(event.resolution.getScaledWidth() / 2 + 13 + xOffset, event.resolution.getScaledHeight() / 2 + 62, 0.0D);
				tessellator.addVertex(event.resolution.getScaledWidth() / 2 + 13, event.resolution.getScaledHeight() / 2 + 62, 0.0D);
				tessellator.draw();
			} else if (mouseTarget instanceof EntityDispenser) {
				EntityDispenser dispenser = (EntityDispenser) mouseTarget;
				// GL11.glColor4f(1.0F, 1.0F, 1.0F, 0.7F);
				// Minecraft.getMinecraft().ingameGUI.drawTexturedModalRect(event.resolution.getScaledWidth()/2-64,
				// event.resolution.getScaledHeight()/2+35, 0, 0, 128, 40);
				tessellator.startDrawingQuads();
				tessellator.addVertexWithUV(event.resolution.getScaledWidth() / 2 - 72, event.resolution.getScaledHeight() / 2 + 76, 0.0D, 0.0D, 0.1875D);
				tessellator.addVertexWithUV(event.resolution.getScaledWidth() / 2 + 72, event.resolution.getScaledHeight() / 2 + 76, 0.0D, 0.5625D, 0.1875D);
				tessellator.addVertexWithUV(event.resolution.getScaledWidth() / 2 + 72, event.resolution.getScaledHeight() / 2 + 28, 0.0D, 0.5625D, 0D);
				tessellator.addVertexWithUV(event.resolution.getScaledWidth() / 2 - 72, event.resolution.getScaledHeight() / 2 + 28, 0.0D, 0.0D, 0D);
				tessellator.draw();

				tessellator.startDrawingQuads();
				tessellator.addVertexWithUV(event.resolution.getScaledWidth() / 2 - 53, event.resolution.getScaledHeight() / 2 + 76, 0.0D, 0.75D, 0.75D);
				tessellator.addVertexWithUV(event.resolution.getScaledWidth() / 2 - 5, event.resolution.getScaledHeight() / 2 + 76, 0.0D, 0.9375D, 0.75D);
				tessellator.addVertexWithUV(event.resolution.getScaledWidth() / 2 - 5, event.resolution.getScaledHeight() / 2 + 28, 0.0D, 0.9375D, 0.5625D);
				tessellator.addVertexWithUV(event.resolution.getScaledWidth() / 2 - 53, event.resolution.getScaledHeight() / 2 + 28, 0.0D, 0.75D, 0.5625D);
				tessellator.draw();

				tessellator.startDrawingQuads();
				tessellator.addVertexWithUV(event.resolution.getScaledWidth() / 2 - 5, event.resolution.getScaledHeight() / 2 + 50, 0.0D, 0.9375D, 0.1875D);
				tessellator.addVertexWithUV(event.resolution.getScaledWidth() / 2 + 11, event.resolution.getScaledHeight() / 2 + 50, 0.0D, 1D, 0.1875D);
				tessellator.addVertexWithUV(event.resolution.getScaledWidth() / 2 + 11, event.resolution.getScaledHeight() / 2 + 34, 0.0D, 1D, 0.125D);
				tessellator.addVertexWithUV(event.resolution.getScaledWidth() / 2 - 5, event.resolution.getScaledHeight() / 2 + 34, 0.0D, 0.9375D, 0.125D);
				tessellator.draw();

				double imagePos = dispenser.getLevel() == 1 ? 0.3125D : dispenser.getLevel() == 2 ? 0.375D : 0.4375D;
				tessellator.startDrawingQuads();
				tessellator.addVertexWithUV(event.resolution.getScaledWidth() / 2 - 22, event.resolution.getScaledHeight() / 2 + 46, 0.0D, 0.9375D, 0.0625D + imagePos);
				tessellator.addVertexWithUV(event.resolution.getScaledWidth() / 2 - 6, event.resolution.getScaledHeight() / 2 + 46, 0.0D, 1D, 0.0625D + imagePos);
				tessellator.addVertexWithUV(event.resolution.getScaledWidth() / 2 - 6, event.resolution.getScaledHeight() / 2 + 30, 0.0D, 1D, imagePos);
				tessellator.addVertexWithUV(event.resolution.getScaledWidth() / 2 - 22, event.resolution.getScaledHeight() / 2 + 30, 0.0D, 0.9375D, imagePos);
				tessellator.draw();

				if (dispenser.getLevel() < 3) {
					tessellator.startDrawingQuads();
					tessellator.addVertexWithUV(event.resolution.getScaledWidth() / 2 - 5, event.resolution.getScaledHeight() / 2 + 70, 0.0D, 0.9375D, 0.125D);
					tessellator.addVertexWithUV(event.resolution.getScaledWidth() / 2 + 11, event.resolution.getScaledHeight() / 2 + 70, 0.0D, 1D, 0.125D);
					tessellator.addVertexWithUV(event.resolution.getScaledWidth() / 2 + 11, event.resolution.getScaledHeight() / 2 + 54, 0.0D, 1D, 0.0625);
					tessellator.addVertexWithUV(event.resolution.getScaledWidth() / 2 - 5, event.resolution.getScaledHeight() / 2 + 54, 0.0D, 0.9375D, 0.0625);
					tessellator.draw();
				}
				float health = dispenser.getHealth() / dispenser.getMaxHealth();
				if (health > 0.33f) {
					GL11.glColor4f(0.9F, 0.9F, 0.9F, 1F);
				} else {
					GL11.glColor4f(0.85F, 0.0F, 0.0F, 1F);
				}
				GL11.glDisable(GL11.GL_TEXTURE_2D);
				for (int i = 0; i < health * 8; i++) {

					tessellator.startDrawingQuads();
					tessellator.addVertex(event.resolution.getScaledWidth() / 2 - 53, event.resolution.getScaledHeight() / 2 + 67 - i * 5, 0.0D);
					tessellator.addVertex(event.resolution.getScaledWidth() / 2 - 63, event.resolution.getScaledHeight() / 2 + 67 - i * 5, 0.0D);
					tessellator.addVertex(event.resolution.getScaledWidth() / 2 - 63, event.resolution.getScaledHeight() / 2 + 71 - i * 5, 0.0D);
					tessellator.addVertex(event.resolution.getScaledWidth() / 2 - 53, event.resolution.getScaledHeight() / 2 + 71 - i * 5, 0.0D);
					tessellator.draw();
				}

				GL11.glColor4f(1.0F, 1.0F, 1.0F, 0.33F);
				tessellator.startDrawingQuads();
				tessellator.addVertex(event.resolution.getScaledWidth() / 2 + 13, event.resolution.getScaledHeight() / 2 + 49, 0.0D);
				tessellator.addVertex(event.resolution.getScaledWidth() / 2 + 68, event.resolution.getScaledHeight() / 2 + 49, 0.0D);
				tessellator.addVertex(event.resolution.getScaledWidth() / 2 + 68, event.resolution.getScaledHeight() / 2 + 35, 0.0D);
				tessellator.addVertex(event.resolution.getScaledWidth() / 2 + 13, event.resolution.getScaledHeight() / 2 + 35, 0.0D);
				tessellator.draw();

				if (dispenser.getLevel() < 3) {
					tessellator.startDrawingQuads();
					tessellator.addVertex(event.resolution.getScaledWidth() / 2 + 13, event.resolution.getScaledHeight() / 2 + 69, 0.0D);
					tessellator.addVertex(event.resolution.getScaledWidth() / 2 + 68, event.resolution.getScaledHeight() / 2 + 69, 0.0D);
					tessellator.addVertex(event.resolution.getScaledWidth() / 2 + 68, event.resolution.getScaledHeight() / 2 + 55, 0.0D);
					tessellator.addVertex(event.resolution.getScaledWidth() / 2 + 13, event.resolution.getScaledHeight() / 2 + 55, 0.0D);
					tessellator.draw();
				}

				GL11.glColor4f(1.0F, 1.0F, 1.0F, 0.85F);
				tessellator.startDrawingQuads();
				tessellator.addVertex(event.resolution.getScaledWidth() / 2 + 13, event.resolution.getScaledHeight() / 2 + 49, 0.0D);
				tessellator.addVertex(event.resolution.getScaledWidth() / 2 + 13 + dispenser.getMetal() * 0.1375D, event.resolution.getScaledHeight() / 2 + 49, 0.0D);
				tessellator.addVertex(event.resolution.getScaledWidth() / 2 + 13 + dispenser.getMetal() * 0.1375D, event.resolution.getScaledHeight() / 2 + 35, 0.0D);
				tessellator.addVertex(event.resolution.getScaledWidth() / 2 + 13, event.resolution.getScaledHeight() / 2 + 35, 0.0D);
				tessellator.draw();

				if (dispenser.getLevel() < 3) {
					tessellator.startDrawingQuads();
					tessellator.addVertex(event.resolution.getScaledWidth() / 2 + 13, event.resolution.getScaledHeight() / 2 + 69, 0.0D);
					tessellator.addVertex(event.resolution.getScaledWidth() / 2 + 13 + dispenser.getProgress() * 0.275D, event.resolution.getScaledHeight() / 2 + 69, 0.0D);
					tessellator.addVertex(event.resolution.getScaledWidth() / 2 + 13 + dispenser.getProgress() * 0.275D, event.resolution.getScaledHeight() / 2 + 55, 0.0D);
					tessellator.addVertex(event.resolution.getScaledWidth() / 2 + 13, event.resolution.getScaledHeight() / 2 + 55, 0.0D);
					tessellator.draw();
				}

			} else if (mouseTarget instanceof EntityTeleporter) {
				EntityTeleporter teleporter = (EntityTeleporter) mouseTarget;
				// GL11.glColor4f(1.0F, 1.0F, 1.0F, 0.7F);
				// Minecraft.getMinecraft().ingameGUI.drawTexturedModalRect(event.resolution.getScaledWidth()/2-64,
				// event.resolution.getScaledHeight()/2+35, 0, 0, 128, 40);
				tessellator.startDrawingQuads();
				tessellator.addVertexWithUV(event.resolution.getScaledWidth() / 2 - 72, event.resolution.getScaledHeight() / 2 + 76, 0.0D, 0.0D, 0.1875D);
				tessellator.addVertexWithUV(event.resolution.getScaledWidth() / 2 + 72, event.resolution.getScaledHeight() / 2 + 76, 0.0D, 0.5625D, 0.1875D);
				tessellator.addVertexWithUV(event.resolution.getScaledWidth() / 2 + 72, event.resolution.getScaledHeight() / 2 + 28, 0.0D, 0.5625D, 0D);
				tessellator.addVertexWithUV(event.resolution.getScaledWidth() / 2 - 72, event.resolution.getScaledHeight() / 2 + 28, 0.0D, 0.0D, 0D);
				tessellator.draw();

				double imagePos = teleporter.isExit() ? 0.1875D : 0;
				tessellator.startDrawingQuads();
				tessellator.addVertexWithUV(event.resolution.getScaledWidth() / 2 - 53, event.resolution.getScaledHeight() / 2 + 76, 0.0D, 0.5625D + imagePos, 0.9375D);
				tessellator.addVertexWithUV(event.resolution.getScaledWidth() / 2 - 5, event.resolution.getScaledHeight() / 2 + 76, 0.0D, 0.75D + imagePos, 0.9375D);
				tessellator.addVertexWithUV(event.resolution.getScaledWidth() / 2 - 5, event.resolution.getScaledHeight() / 2 + 28, 0.0D, 0.75D + imagePos, 0.75D);
				tessellator.addVertexWithUV(event.resolution.getScaledWidth() / 2 - 53, event.resolution.getScaledHeight() / 2 + 28, 0.0D, 0.5625D + imagePos, 0.75D);
				tessellator.draw();

				tessellator.startDrawingQuads();
				tessellator.addVertexWithUV(event.resolution.getScaledWidth() / 2 - 5, event.resolution.getScaledHeight() / 2 + 50, 0.0D, 0.9375D, 0.3125D);
				tessellator.addVertexWithUV(event.resolution.getScaledWidth() / 2 + 11, event.resolution.getScaledHeight() / 2 + 50, 0.0D, 1D, 0.3125D);
				tessellator.addVertexWithUV(event.resolution.getScaledWidth() / 2 + 11, event.resolution.getScaledHeight() / 2 + 34, 0.0D, 1D, 0.25D);
				tessellator.addVertexWithUV(event.resolution.getScaledWidth() / 2 - 5, event.resolution.getScaledHeight() / 2 + 34, 0.0D, 0.9375D, 0.25D);
				tessellator.draw();

				imagePos = teleporter.getLevel() == 1 ? 0.3125D : teleporter.getLevel() == 2 ? 0.375D : 0.4375D;
				tessellator.startDrawingQuads();
				tessellator.addVertexWithUV(event.resolution.getScaledWidth() / 2 - 22, event.resolution.getScaledHeight() / 2 + 46, 0.0D, 0.9375D, 0.0625D + imagePos);
				tessellator.addVertexWithUV(event.resolution.getScaledWidth() / 2 - 6, event.resolution.getScaledHeight() / 2 + 46, 0.0D, 1D, 0.0625D + imagePos);
				tessellator.addVertexWithUV(event.resolution.getScaledWidth() / 2 - 6, event.resolution.getScaledHeight() / 2 + 30, 0.0D, 1D, imagePos);
				tessellator.addVertexWithUV(event.resolution.getScaledWidth() / 2 - 22, event.resolution.getScaledHeight() / 2 + 30, 0.0D, 0.9375D, imagePos);
				tessellator.draw();

				if (teleporter.getLevel() < 3) {
					tessellator.startDrawingQuads();
					tessellator.addVertexWithUV(event.resolution.getScaledWidth() / 2 - 5, event.resolution.getScaledHeight() / 2 + 70, 0.0D, 0.9375D, 0.125D);
					tessellator.addVertexWithUV(event.resolution.getScaledWidth() / 2 + 11, event.resolution.getScaledHeight() / 2 + 70, 0.0D, 1D, 0.125D);
					tessellator.addVertexWithUV(event.resolution.getScaledWidth() / 2 + 11, event.resolution.getScaledHeight() / 2 + 54, 0.0D, 1D, 0.0625);
					tessellator.addVertexWithUV(event.resolution.getScaledWidth() / 2 - 5, event.resolution.getScaledHeight() / 2 + 54, 0.0D, 0.9375D, 0.0625);
					tessellator.draw();
				}
				if (teleporter.getTPprogress() <= 0) {
					Minecraft.getMinecraft().ingameGUI.drawString(Minecraft.getMinecraft().fontRenderer, teleporter.getTeleports() + " (ID: " + teleporter.getID() + ")",
							event.resolution.getScaledWidth() / 2 + 13, event.resolution.getScaledHeight() / 2 + 38, 16777215);
				}
				float health = teleporter.getHealth() / teleporter.getMaxHealth();
				if (health > 0.33f) {
					GL11.glColor4f(0.9F, 0.9F, 0.9F, 1F);
				} else {
					GL11.glColor4f(0.85F, 0.0F, 0.0F, 1F);
				}
				GL11.glDisable(GL11.GL_TEXTURE_2D);
				for (int i = 0; i < health * 8; i++) {

					tessellator.startDrawingQuads();
					tessellator.addVertex(event.resolution.getScaledWidth() / 2 - 53, event.resolution.getScaledHeight() / 2 + 67 - i * 5, 0.0D);
					tessellator.addVertex(event.resolution.getScaledWidth() / 2 - 63, event.resolution.getScaledHeight() / 2 + 67 - i * 5, 0.0D);
					tessellator.addVertex(event.resolution.getScaledWidth() / 2 - 63, event.resolution.getScaledHeight() / 2 + 71 - i * 5, 0.0D);
					tessellator.addVertex(event.resolution.getScaledWidth() / 2 - 53, event.resolution.getScaledHeight() / 2 + 71 - i * 5, 0.0D);
					tessellator.draw();
				}
				GL11.glColor4f(1.0F, 1.0F, 1.0F, 0.33F);
				if (teleporter.getTPprogress() > 0) {
					tessellator.startDrawingQuads();
					tessellator.addVertex(event.resolution.getScaledWidth() / 2 + 13, event.resolution.getScaledHeight() / 2 + 49, 0.0D);
					tessellator.addVertex(event.resolution.getScaledWidth() / 2 + 68, event.resolution.getScaledHeight() / 2 + 49, 0.0D);
					tessellator.addVertex(event.resolution.getScaledWidth() / 2 + 68, event.resolution.getScaledHeight() / 2 + 35, 0.0D);
					tessellator.addVertex(event.resolution.getScaledWidth() / 2 + 13, event.resolution.getScaledHeight() / 2 + 35, 0.0D);
					tessellator.draw();
				}
				if (teleporter.getLevel() < 3) {
					tessellator.startDrawingQuads();
					tessellator.addVertex(event.resolution.getScaledWidth() / 2 + 13, event.resolution.getScaledHeight() / 2 + 69, 0.0D);
					tessellator.addVertex(event.resolution.getScaledWidth() / 2 + 68, event.resolution.getScaledHeight() / 2 + 69, 0.0D);
					tessellator.addVertex(event.resolution.getScaledWidth() / 2 + 68, event.resolution.getScaledHeight() / 2 + 55, 0.0D);
					tessellator.addVertex(event.resolution.getScaledWidth() / 2 + 13, event.resolution.getScaledHeight() / 2 + 55, 0.0D);
					tessellator.draw();
				}
				GL11.glColor4f(1.0F, 1.0F, 1.0F, 0.85F);
				if (teleporter.getTPprogress() > 0) {
					double tpProgress = (1 - ((double) teleporter.getTPprogress() / (teleporter.getLevel() == 1 ? 200 : (teleporter.getLevel() == 2 ? 100 : 60)))) * 55;
					tessellator.startDrawingQuads();
					tessellator.addVertex(event.resolution.getScaledWidth() / 2 + 13, event.resolution.getScaledHeight() / 2 + 49, 0.0D);
					tessellator.addVertex(event.resolution.getScaledWidth() / 2 + 13 + tpProgress, event.resolution.getScaledHeight() / 2 + 49, 0.0D);
					tessellator.addVertex(event.resolution.getScaledWidth() / 2 + 13 + tpProgress, event.resolution.getScaledHeight() / 2 + 35, 0.0D);
					tessellator.addVertex(event.resolution.getScaledWidth() / 2 + 13, event.resolution.getScaledHeight() / 2 + 35, 0.0D);
					tessellator.draw();
				}
				if (teleporter.getLevel() < 3) {
					tessellator.startDrawingQuads();
					tessellator.addVertex(event.resolution.getScaledWidth() / 2 + 13, event.resolution.getScaledHeight() / 2 + 69, 0.0D);
					tessellator.addVertex(event.resolution.getScaledWidth() / 2 + 13 + teleporter.getProgress() * 0.275D, event.resolution.getScaledHeight() / 2 + 69, 0.0D);
					tessellator.addVertex(event.resolution.getScaledWidth() / 2 + 13 + teleporter.getProgress() * 0.275D, event.resolution.getScaledHeight() / 2 + 55, 0.0D);
					tessellator.addVertex(event.resolution.getScaledWidth() / 2 + 13, event.resolution.getScaledHeight() / 2 + 55, 0.0D);
					tessellator.draw();
				}
			}
			/*
			 * float
			 * uber=player.getHeldItem().getTagCompound().getFloat("ubercharge")
			 * ; Minecraft.getMinecraft().ingameGUI.drawString(Minecraft.
			 * getMinecraft().ingameGUI.getFontRenderer(), "UBERCHARGE: "
			 * +Math.round(uber*100f)+"%",
			 * event.resolution.getScaledWidth()-130,
			 * event.resolution.getScaledHeight()-48, 16777215);
			 * GL11.glDisable(GL11.GL_TEXTURE_2D); GL11.glColor4f(1.0F, 1.0F,
			 * 1.0F, 0.33F); tessellator.startDrawingQuads();
			 * tessellator.addVertexWithUV(event.resolution.getScaledWidth()-
			 * 132, event.resolution.getScaledHeight()-22, 0.0D);
			 * tessellator.addVertexWithUV(event.resolution.getScaledWidth()-20,
			 * event.resolution.getScaledHeight()-22, 0.0D);
			 * tessellator.addVertexWithUV(event.resolution.getScaledWidth()-20,
			 * event.resolution.getScaledHeight()-36, 0.0D);
			 * tessellator.addVertexWithUV(event.resolution.getScaledWidth()-
			 * 132, event.resolution.getScaledHeight()-36, 0.0D);
			 * tessellator.draw();
			 * 
			 * GL11.glColor4f(1.0F, 1.0F, 1.0F, 0.85F);
			 * tessellator.startDrawingQuads();
			 * tessellator.addVertexWithUV(event.resolution.getScaledWidth()-
			 * 132, event.resolution.getScaledHeight()-22, 0.0D);
			 * tessellator.addVertexWithUV(event.resolution.getScaledWidth()-132
			 * +112*uber, event.resolution.getScaledHeight()-22, 0.0D);
			 * tessellator.addVertexWithUV(event.resolution.getScaledWidth()-132
			 * +112*uber, event.resolution.getScaledHeight()-36, 0.0D);
			 * tessellator.addVertexWithUV(event.resolution.getScaledWidth()-
			 * 132, event.resolution.getScaledHeight()-36, 0.0D);
			 * tessellator.draw();
			 */
			GL11.glEnable(GL11.GL_TEXTURE_2D);
			GL11.glDepthMask(true);
			GL11.glEnable(GL11.GL_DEPTH_TEST);
			GL11.glEnable(GL11.GL_ALPHA_TEST);
			GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
			GL11.glDisable(GL11.GL_BLEND);
		}
	}

	@SideOnly(Side.CLIENT)
	@SubscribeEvent
	public void renderPlayer(RenderPlayerEvent.Pre event) {
		if (event.entityPlayer != Minecraft.getMinecraft().thePlayer)
			renderBeam(event.entityPlayer, event.partialRenderTick);
		/*
		 * InventoryWearables
		 * inventory=event.entityPlayer.getCapability(TF2weapons.INVENTORY_CAP,
		 * null); for(int i=0;i<inventory.getInventoryStackLimit();i++){
		 * ItemStack stack=inventory.getStackInSlot(i); if(stack!=null){
		 * GL11.glPushMatrix();
		 * event.getRenderer().getMainModel().bipedHead.postRender(0.0625f);
		 * GL11.glTranslatef(0.0F, -0.25F, 0.0F); GlStateManager.rotate(180.0F,
		 * 0.0F, 1.0F, 0.0F); GlStateManager.scale(0.625F, -0.625F, -0.625F);
		 * 
		 * Minecraft.getMinecraft().getItemRenderer().renderItem(event.
		 * entityPlayer, stack, ItemCameraTransforms.TransformType.HEAD);
		 * GlStateManager.popMatrix(); } }
		 */
	}

	@SideOnly(Side.CLIENT)
	@SubscribeEvent
	public void renderHand(RenderHandEvent event) {

		if (Minecraft.getMinecraft().thePlayer.getEntityData().getBoolean("IsCloaked")) {
			/*
			 * GL11.glEnable(GL11.GL_BLEND); GlStateManager.clear(256);
			 * OpenGlHelper.glBlendFunc(770, 771, 1, 0);
			 * if(Minecraft.getMinecraft().thePlayer.getEntityData().getInteger(
			 * "VisTicks")>=20){ GL11.glColor4f(1.0F, 1.0F, 1.0F, 0.75f); }
			 * else{ GL11.glColor4f(1.0F, 1.0F, 1.0F,
			 * 0.6f*(1-(float)Minecraft.getMinecraft().thePlayer.getEntityData()
			 * .getInteger("VisTicks")/20)); } try { Method
			 * method=EntityRenderer.class.getDeclaredMethod("renderHand",
			 * float.class, int.class); method.setAccessible(true);
			 * method.invoke(Minecraft.getMinecraft().entityRenderer,
			 * event.partialTicks,event.renderPass); } catch (Exception e) { //
			 * TODO Auto-generated catch block e.printStackTrace(); }
			 */
			event.setCanceled(true);
		}
	}

	@SideOnly(Side.CLIENT)
	public static void renderBeam(EntityLivingBase ent, float partialTicks) {
		if (ent.getExtendedProperties(WeaponsCapability.ID) == null)
			return;
		// System.out.println("Drawing");
		Entity healTarget = ent.worldObj.getEntityByID(WeaponsCapability.get(ent).healTarget);
		if (healTarget != null) {
			Entity camera = Minecraft.getMinecraft().renderViewEntity;
			double cameraX = camera.prevPosX + (camera.posX - camera.prevPosX) * partialTicks;
			double cameraY = camera.prevPosY + (camera.posY - camera.prevPosY) * partialTicks;
			double cameraZ = camera.prevPosZ + (camera.posZ - camera.prevPosZ) * partialTicks;
			// System.out.println("rendering");
			double xPos1 = ent.prevPosX + (ent.posX - ent.prevPosX) * partialTicks;
			double yPos1 = ent.prevPosY + (ent.posY - ent.prevPosY) * partialTicks;
			double zPos1 = ent.prevPosZ + (ent.posZ - ent.prevPosZ) * partialTicks;
			double xPos2 = healTarget.prevPosX + (healTarget.posX - healTarget.prevPosX) * partialTicks;
			double yPos2 = healTarget.prevPosY + (healTarget.posY - healTarget.prevPosY) * partialTicks;
			double zPos2 = healTarget.prevPosZ + (healTarget.posZ - healTarget.prevPosZ) * partialTicks;
			double xDist = xPos2 - xPos1;
			double yDist = (yPos2 + (healTarget.boundingBox.maxY - healTarget.boundingBox.minY) / 2 + 0.1) - (yPos1 + ent.getEyeHeight() - 0.1);
			double zDist = zPos2 - zPos1;
			float f = MathHelper.sqrt_double(xDist * xDist + zDist * zDist);
			float fullDist = MathHelper.sqrt_double(xDist * xDist + yDist * yDist + zDist * zDist);
			Tessellator tessellator = Tessellator.instance;
			GL11.glPushMatrix();
			GL11.glTranslatef((float) (xPos1 - cameraX), (float) ((yPos1 + (!(ent == Minecraft.getMinecraft().thePlayer) ? ent.getEyeHeight() : 0) - 0.1) - cameraY),
					(float) (zPos1 - cameraZ));
			GL11.glRotatef((float) (Math.atan2(xDist, zDist) * 180.0D / Math.PI), 0.0F, 1.0F, 0.0F);
			GL11.glRotatef((float) (Math.atan2(yDist, f) * 180.0D / Math.PI) * -1, 1.0F, 0.0F, 0.0F);
			GL11.glDisable(GL11.GL_TEXTURE_2D);
			GL11.glDisable(GL11.GL_LIGHTING);
			GL11.glEnable(GL11.GL_BLEND);
			OpenGlHelper.glBlendFunc(770, 771, 1, 0);
			if (TF2weapons.getTeamForDisplay(ent) == 0) {
				GL11.glColor4f(1.0F, 0.0F, 0.0F, 0.23F);
			} else {
				GL11.glColor4f(0.0F, 0.0F, 1.0F, 0.23F);
			}
			tessellator.startDrawingQuads();
			tessellator.addVertex(-0.04, -0.04, 0);
			tessellator.addVertex(0.04, 0.04, 0);
			tessellator.addVertex(0.04, 0.04, fullDist);
			tessellator.addVertex(-0.04, -0.04, fullDist);
			tessellator.draw();
			tessellator.startDrawingQuads();
			tessellator.addVertex(-0.04, -0.04, fullDist);
			tessellator.addVertex(0.04, 0.04, fullDist);
			tessellator.addVertex(0.04, 0.04, 0);
			tessellator.addVertex(-0.04, -0.04, 0);
			tessellator.draw();
			tessellator.startDrawingQuads();
			tessellator.addVertex(0.04, -0.04, 0);
			tessellator.addVertex(-0.04, 0.04, 0);
			tessellator.addVertex(-0.04, 0.04, fullDist);
			tessellator.addVertex(0.04, -0.04, fullDist);
			tessellator.draw();
			tessellator.startDrawingQuads();
			tessellator.addVertex(0.04, -0.04, fullDist);
			tessellator.addVertex(-0.04, 0.04, fullDist);
			tessellator.addVertex(-0.04, 0.04, 0);
			tessellator.addVertex(0.04, -0.04, 0);
			tessellator.draw();
			GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
			GL11.glDisable(GL11.GL_BLEND);
			GL11.glEnable(GL11.GL_TEXTURE_2D);
			GL11.glEnable(GL11.GL_LIGHTING);
			GL11.glPopMatrix();
		}
	}

	public static float interpolateRotation(float par1, float par2, float par3) {
		float f;

		for (f = par2 - par1; f < -180.0F; f += 360.0F) {
			;
		}

		while (f >= 180.0F) {
			f -= 360.0F;
		}

		return par1 + par3 * f;
	}

	@SideOnly(Side.CLIENT)
	@SubscribeEvent
	public void renderTick(TickEvent.RenderTickEvent event) {

		tickTime = event.renderTickTime;
	}

	@SideOnly(Side.CLIENT)
	@SubscribeEvent
	public void renderWorld(RenderWorldLastEvent event) {
		if (Minecraft.getMinecraft().thePlayer != null)
			renderBeam(Minecraft.getMinecraft().thePlayer, event.partialTicks);
	}

	@SideOnly(Side.CLIENT)
	@SubscribeEvent
	public void renderLivingEntity(RenderLivingEvent.Pre event) {

		if (!(event.entity instanceof EntityPlayer || event.entity instanceof EntityTF2Character))
			return;
		int visTick = WeaponsCapability.get(event.entity).invisTicks;

		if (visTick > 0) {
			if (visTick >= 20) {
				event.setCanceled(true);
				// System.out.println("VisTicks
				// "+event.entity.getEntityData().getInteger("VisTicks"));
			} else {
				// System.out.println("VisTicksRender
				// "+event.entity.getEntityData().getInteger("VisTicks"));
				GL11.glEnable(GL11.GL_BLEND);
				OpenGlHelper.glBlendFunc(770, 771, 1, 0);
				Team team = event.entity.getTeam();
				if (team == event.entity.worldObj.getScoreboard().getTeam("RED")) {
					GL11.glColor4f(1.0F, 0.17F, 0.17F, 0.7f * (1 - (float) visTick / 20));

				} else if (team == event.entity.worldObj.getScoreboard().getTeam("BLU")) {
					GL11.glColor4f(0.17F, 0.17F, 1.0F, 0.7f * (1 - (float) visTick / 20));
				} else {
					GL11.glColor4f(1.0F, 1.0F, 1.0F, 0.7f * (1 - (float) visTick / 20));
				}
			}
		}
		/*
		 * else if(event.entity instanceof EntityPlayer &&
		 * event.entity.getHeldItem() != null &&
		 * event.entity.getHeldItem().getItem() instanceof ItemUsable&&
		 * event.getRenderer().getMainModel() instanceof ModelBiped){
		 * 
		 * ((ModelBiped)event.renderer.getMainModel()).aimedBow=((
		 * WeaponsCapability.get(event.entity).state&3)>0)||
		 * WeaponsCapability.get(event.entity).charging; }
		 */
		if (event.entity.getEntityData().getBoolean("Ubercharge")) {
			// GL11.glDisable(GL11.GL_LIGHTING);
			if (TF2weapons.getTeamForDisplay(event.entity) == 0) {
				GL11.glColor4f(1.0F, 0.33F, 0.33F, 1F);

			} else {
				GL11.glColor4f(0.33F, 0.33F, 1.0F, 1F);
			}
		}

		if (event.renderer != ClientProxy.disguiseRender && event.entity.getEntityData().getBoolean("IsDisguised")
				&& event.entity.getEntityData().getString("DisguiseType").startsWith("M:")) {
			/*
			 * EntityLivingBase entToRender=fakeEntities.get(event.entity);
			 * entToRender.prevRenderYawOffset=event.entity.prevRenderYawOffset;
			 * entToRender.renderYawOffset=event.entity.renderYawOffset;
			 * entToRender.limbSwing=event.entity.limbSwing;
			 * entToRender.limbSwingAmount=event.entity.limbSwingAmount;
			 * entToRender.prevLimbSwingAmount=event.entity.prevLimbSwingAmount;
			 */
			// Entity camera=Minecraft.getMinecraft().renderViewEntity;
			float partialTicks = tickTime;/*
											 * 0;
											 * if(camera.posX-camera.prevPosX!=0
											 * ){ partialTicks=(float)
											 * ((camera.posX-event.x)/(camera.
											 * posX-camera.prevPosX)); }
											 * /"lel: "+event.x+" "+camera.
											 * posX+" "+camera.prevPosX+" "+);
											 */
			/*
			 * ModelBase model=ClientProxy.entityRenderers.get("Creeper");
			 * GL11.glPushMatrix(); GlStateManager.disableCull();
			 * model.swingProgress =
			 * event.entity.getSwingProgress(partialTicks); model.isRiding =
			 * event.entity.isRiding(); model.isChild = event.entity.isChild();
			 * 
			 * try { float f =
			 * interpolateRotation(event.entity.prevRenderYawOffset,
			 * event.entity.renderYawOffset, partialTicks); float f1 =
			 * interpolateRotation(event.entity.prevRotationYawHead,
			 * event.entity.rotationYawHead, partialTicks); float f2 = f1 - f;
			 * 
			 * if (event.entity.isRiding() && event.entity.ridingEntity
			 * instanceof EntityLivingBase) { EntityLivingBase entitylivingbase
			 * = (EntityLivingBase)event.entity.ridingEntity; f =
			 * interpolateRotation(entitylivingbase.prevRenderYawOffset,
			 * entitylivingbase.renderYawOffset, partialTicks); f2 = f1 - f;
			 * float f3 = MathHelper.wrapAngleTo180_float(f2);
			 * 
			 * if (f3 < -85.0F) { f3 = -85.0F; }
			 * 
			 * if (f3 >= 85.0F) { f3 = 85.0F; }
			 * 
			 * f = f1 - f3;
			 * 
			 * if (f3 * f3 > 2500.0F) { f += f3 * 0.2F; } }
			 * 
			 * float f7 = event.entity.prevRotationPitch +
			 * (event.entity.rotationPitch - event.entity.prevRotationPitch) *
			 * partialTicks; GL11.glTranslatef((float)event.x, (float)event.y,
			 * (float)event.z); float ticks=
			 * this.handleRotationFloat(event.entity, partialTicks);
			 * this.rotateCorpse(entity, f8, f, partialTicks);
			 * GlStateManager.enableRescaleNormal(); GlStateManager.scale(-1.0F,
			 * -1.0F, 1.0F); this.preRenderCallback(entity, partialTicks); float
			 * f4 = 0.0625F; GL11.glTranslatef(0.0F, -1.5078125F, 0.0F); float
			 * f5 = entity.prevLimbSwingAmount + (entity.limbSwingAmount -
			 * entity.prevLimbSwingAmount) * partialTicks; float f6 =
			 * entity.limbSwing - entity.limbSwingAmount * (1.0F -
			 * partialTicks); GlStateManager.enableAlpha();
			 * this.mainModel.setLivingAnimations(entity, f6, f5, partialTicks);
			 * this.mainModel.setRotationAngles(f6, f5, f8, f2, f7, 0.0625F,
			 * entity);
			 * 
			 * if (this.renderOutlines) { boolean flag1 =
			 * this.setScoreTeamColor(entity); this.renderModel(entity, f6, f5,
			 * f8, f2, f7, 0.0625F);
			 * 
			 * if (flag1) { this.unsetScoreTeamColor(); } } else { boolean flag
			 * = event.renderer.setDoRenderBrightness(event.entity,
			 * partialTicks); M.renderModel(event.entity, f6, f5, f8, f2, f7,
			 * 0.0625F);
			 * 
			 * if (flag) { event.renderer.unsetBrightness(); }
			 * 
			 * GlStateManager.depthMask(true);
			 * //event.renderer.renderLayers(event.entity, f6, f5, partialTicks,
			 * f8, f2, f7, 0.0625F); //}
			 * 
			 * GlStateManager.disableRescaleNormal(); } catch (Exception
			 * exception) { //logger.error((String)"Couldn\'t render entity",
			 * (Throwable)exception); }
			 * 
			 * GlStateManager.setActiveTexture(OpenGlHelper.lightmapTexUnit);
			 * GL11.glEnable(GL11.GL_TEXTURE_2D);
			 * GlStateManager.setActiveTexture(OpenGlHelper.defaultTexUnit);
			 * GlStateManager.enableCull(); GlStateManager.popMatrix();
			 * 
			 * /*if (!event.renderer.renderOutlines) { super.doRender(entity, x,
			 * y, z, entityYaw, partialTicks); }
			 */
			String mobType = event.entity.getEntityData().getString("DisguiseType").substring(2);
			ClientProxy.disguiseRender.setRenderManager(RenderManager.instance);
			ClientProxy.disguiseRender.setRenderOptions(ClientProxy.entityModel.get(mobType), ClientProxy.textureDisguise.get(mobType));
			ClientProxy.disguiseRender.doRender(event.entity, event.x, event.y, event.z, event.entity.rotationYaw, partialTicks);
			event.setCanceled(true);
		}

	}

	@SideOnly(Side.CLIENT)
	@SubscribeEvent
	public void renderLivingPostEntity(RenderLivingEvent.Post event) {
		if (!(event.entity instanceof EntityPlayer || event.entity instanceof EntityTF2Character))
			return;
		if (event.entity.getEntityData().getBoolean("Ubercharge")) {
			GL11.glColor4f(1.0F, 1F, 1.0F, 1F);
			// GL11.glEnable(GL11.GL_LIGHTING);
		}
		if (WeaponsCapability.get(event.entity).invisTicks > 0) {
			GL11.glDisable(GL11.GL_BLEND);
			GL11.glColor4f(1.0F, 1F, 1.0F, 1F);
		}
	}

	@SubscribeEvent
	public void untargetable(LivingSetAttackTargetEvent event) {
		if (event.target != null && (WeaponsCapability.get(event.target) != null && WeaponsCapability.get(event.target).invisTicks >= 20)) {
			event.entityLiving.setRevengeTarget(null);
			if (event.entityLiving instanceof EntityLiving) {
				((EntityLiving) event.entity).setAttackTarget(null);
			}
		}
		if (event.target != null && (WeaponsCapability.get(event.target) != null && WeaponsCapability.get(event.target).invisTicks == 0
				&& event.target.getEntityData().getBoolean("IsDisguised") && event.entityLiving.getAITarget() != event.target)) {
			if (event.entityLiving instanceof EntityLiving) {
				((EntityLiving) event.entity).setAttackTarget(null);
			}
		}
	}

	@SubscribeEvent
	public void onConfigChanged(ConfigChangedEvent.OnConfigChangedEvent eventArgs) {
		// TF2weapons.syncConfig();
		if (eventArgs.modID.equals(TF2weapons.MOD_ID))
			TF2weapons.syncConfig();
	}

	@SubscribeEvent
	public void serverTickEnd(TickEvent.ServerTickEvent event) {

		if (event.phase == TickEvent.Phase.START) {

			if (tickleft <= 0) {
				tickleft = 20;
				Object[] entArray = ItemUsable.lastDamage.keySet().toArray();
				for (int x = 0; x < entArray.length; x++) {
					Entity entity = (Entity) entArray[x];
					float[] dmg = ItemUsable.lastDamage.get(entArray[x]);
					for (int i = 19; i >= 0; i--) {
						if (i > 0) {
							dmg[i] = dmg[i - 1];
						} else {
							dmg[0] = 0;
						}
					}
				}
			} else
				tickleft--;
		}
	}

	/*
	 * @SubscribeEvent public void spawnCharacters(TickEvent.WorldTickEvent
	 * event){ if(!event.world.isRemote && event.phase==TickEvent.Phase.END){
	 * 
	 * //if(time!=0&&event.world.rand.nextInt(2500/time)!=0) return;
	 * this.eligibleChunksForSpawning.clear(); int i; int k;
	 * 
	 * for (i = 0; i < event.world.playerEntities.size(); ++i) { EntityPlayer
	 * entityplayer = (EntityPlayer)event.world.playerEntities.get(i); int j =
	 * MathHelper.floor_double(entityplayer.posX / 16.0D); k =
	 * MathHelper.floor_double(entityplayer.posZ / 16.0D); byte b0 = 8;
	 * 
	 * for (int l = -b0; l <= b0; ++l) { for (int i1 = -b0; i1 <= b0; ++i1) {
	 * boolean flag3 = l == -b0 || l == b0 || i1 == -b0 || i1 == b0;
	 * ChunkCoordIntPair chunkcoordintpair = new ChunkCoordIntPair(l + j, i1 +
	 * k);
	 * 
	 * if (!flag3) { this.eligibleChunksForSpawning.put(chunkcoordintpair,
	 * Boolean.valueOf(false)); } else if
	 * (!this.eligibleChunksForSpawning.containsKey(chunkcoordintpair)) {
	 * this.eligibleChunksForSpawning.put(chunkcoordintpair,
	 * Boolean.valueOf(true)); } } } }
	 * 
	 * i = 0; ChunkCoordinates chunkcoordinates = event.world.getSpawnPoint();
	 * Iterator iterator = this.eligibleChunksForSpawning.keySet().iterator();
	 * ArrayList<ChunkCoordIntPair> tmp = new
	 * ArrayList(eligibleChunksForSpawning.keySet()); Collections.shuffle(tmp);
	 * iterator = tmp.iterator(); label110:
	 * 
	 * while (iterator.hasNext()) { ChunkCoordIntPair chunkcoordintpair1 =
	 * (ChunkCoordIntPair)iterator.next();
	 * 
	 * if (!((Boolean)this.eligibleChunksForSpawning.get(chunkcoordintpair1)).
	 * booleanValue()) { Chunk chunk =
	 * event.world.getChunkFromChunkCoords(chunkcoordintpair1.chunkXPos,
	 * chunkcoordintpair1.chunkZPos); int x = chunkcoordintpair1.chunkXPos * 16
	 * + event.world.rand.nextInt(16); int z = chunkcoordintpair1.chunkZPos * 16
	 * + event.world.rand.nextInt(16); int y = event.world.rand.nextInt(chunk ==
	 * null ? event.world.getActualHeight() : chunk.getTopFilledSegment() + 16 -
	 * 1); System.out.println("tick2"); if (!event.world.getBlock(x, y,
	 * z).isNormalCube() && event.world.getBlock(x, y, z).getMaterial() ==
	 * Material.air) { int i2 = 0; int j2 = 0; int
	 * team=event.world.rand.nextInt(2); System.out.println("tick3"); while (j2
	 * < 1) { int k2 = x; int l2 = y; int i3 = z; byte b1 = 6; IEntityLivingData
	 * ientitylivingdata = null; int j3 = 0; System.out.println("tick4"); while
	 * (true) { System.out.println("tick5"); if (j3 < 4) { label103: { k2 +=
	 * event.world.rand.nextInt(b1) - event.world.rand.nextInt(b1); l2 +=
	 * event.world.rand.nextInt(1) - event.world.rand.nextInt(1); i3 +=
	 * event.world.rand.nextInt(b1) - event.world.rand.nextInt(b1);
	 * 
	 * if (canCreatureTypeSpawnAtLocation(EnumCreatureType.monster, event.world,
	 * k2, l2, i3)) {System.out.println("tick6"); float f = (float)k2 + 0.5F;
	 * float f1 = (float)l2; float f2 = (float)i3 + 0.5F;
	 * 
	 * if (event.world.getClosestPlayer((double)f, (double)f1, (double)f2,
	 * 24.0D) == null) { System.out.println("tick7"); float f3 = f -
	 * (float)chunkcoordinates.posX; float f4 = f1 -
	 * (float)chunkcoordinates.posY; float f5 = f2 -
	 * (float)chunkcoordinates.posZ; float f6 = f3 * f3 + f4 * f4 + f5 * f5;
	 * 
	 * if (f6 >= 576.0F) { EntityTF2Character entityliving=null;
	 * 
	 * System.out.println("tick8"); try { switch (event.world.rand.nextInt(2)){
	 * case 1:entityliving = new EntityScout(event.world); default:entityliving
	 * = new EntityHeavy(event.world); } entityliving.setEntTeam(team); } catch
	 * (Exception exception) { exception.printStackTrace(); }
	 * 
	 * entityliving.setLocationAndAngles((double)f, (double)f1, (double)f2,
	 * event.world.rand.nextFloat() * 360.0F, 0.0F);
	 * 
	 * Result canSpawn = ForgeEventFactory.canEntitySpawn(entityliving,
	 * event.world, f, f1, f2); if (canSpawn == Result.ALLOW || (canSpawn ==
	 * Result.DEFAULT && entityliving.getCanSpawnHere())) {
	 * System.out.println("tick9"); ++i2;
	 * event.world.spawnEntityInWorld(entityliving); if
	 * (!ForgeEventFactory.doSpecialSpawn(entityliving, event.world, f, f1, f2))
	 * { ientitylivingdata = entityliving.onSpawnWithEgg(ientitylivingdata); }
	 * 
	 * if (j2 >= ForgeEventFactory.getMaxSpawnPackSize(entityliving)) { continue
	 * label110; } }
	 * 
	 * i += i2; } } }
	 * 
	 * ++j3; continue; } } j2++; break; } } } } } } } public static boolean
	 * canCreatureTypeSpawnAtLocation(EnumCreatureType p_77190_0_, World
	 * p_77190_1_, int p_77190_2_, int p_77190_3_, int p_77190_4_) { if
	 * (!World.doesBlockHaveSolidTopSurface(p_77190_1_, p_77190_2_, p_77190_3_ -
	 * 1, p_77190_4_)) { return false; } else { Block block =
	 * p_77190_1_.getBlock(p_77190_2_, p_77190_3_ - 1, p_77190_4_); boolean
	 * spawnBlock = block.canCreatureSpawn(p_77190_0_, p_77190_1_, p_77190_2_,
	 * p_77190_3_ - 1, p_77190_4_); return spawnBlock && block != Blocks.bedrock
	 * && !p_77190_1_.getBlock(p_77190_2_, p_77190_3_,
	 * p_77190_4_).isNormalCube() && !p_77190_1_.getBlock(p_77190_2_,
	 * p_77190_3_, p_77190_4_).getMaterial().isLiquid() &&
	 * !p_77190_1_.getBlock(p_77190_2_, p_77190_3_ + 1,
	 * p_77190_4_).isNormalCube(); } }
	 */
	@SubscribeEvent
	public void stopHurt(LivingAttackEvent event) {
		if (event.source.getEntity() != null && (event.source.damageType.equals("mob") || event.source.damageType.equals("player"))) {
			EntityLivingBase damageSource = (EntityLivingBase) event.source.getEntity();
			if (damageSource.getActivePotionEffect(TF2weapons.stun) != null || damageSource.getActivePotionEffect(TF2weapons.bonk) != null) {
				event.setCanceled(true);
			}
			if (WeaponsCapability.get(damageSource) != null && WeaponsCapability.get(damageSource).invisTicks > 0) {
				event.setCanceled(true);
			}
			if (WeaponsCapability.get(damageSource) != null && damageSource.getEntityData().getBoolean("IsDisguised")) {
				disguise(damageSource, false);
			}
		}

		if (!event.isCanceled() && event.ammount > 0) {
			/*
			 * if(event.entity.getEntityData().getByte("IsCloaked")!=0){
			 * event.entity.getEntityData().setInteger("VisTicks",
			 * Math.min(10,event.entity.getEntityData().getInteger("VisTicks")))
			 * ; event.entity.setInvisible(false);
			 * //System.out.println("notInvisible"); }
			 */
			event.entityLiving.getEntityData().setInteger("lasthit", event.entityLiving.ticksExisted);
		}

	}

	@SubscribeEvent
	public void clonePlayer(PlayerEvent.Clone event) {
		if (event.wasDeath) {
			InventoryWearables oldInv = InventoryWearables.get(event.original);
			InventoryWearables newInv = InventoryWearables.get(event.entityPlayer);
			for (int i = 0; i < 3; i++) {
				newInv.setInventorySlotContents(i, oldInv.getStackInSlot(i));
			}
		}
	}

	@SubscribeEvent
	public void uber(LivingHurtEvent event) {
		/*
		 * if(event.entity.getEntityData().getByte("IsCloaked")!=0){
		 * event.entity.getEntityData().setInteger("VisTicks",
		 * Math.min(10,event.entity.getEntityData().getInteger("VisTicks")));
		 * event.entity.setInvisible(false);
		 * //System.out.println("notInvisible"); }
		 */

		if (event.entityLiving.getActivePotionEffect(TF2weapons.crit) != null) {
			event.ammount *= 1.1f;
		}
		if (event.entityLiving.getActivePotionEffect(TF2weapons.backup) != null) {
			if ((event.source.damageType.equals("mob") || event.source.damageType.equals("player"))
					&& (event.source.getEntity() != null && event.source.getEntity() instanceof EntityLivingBase && ((EntityLivingBase) event.source.getEntity()).getAttributeMap()
							.getAttributeInstance(SharedMonsterAttributes.attackDamage).getModifier(UUID.fromString("CB3F55D3-645C-4F38-A497-9C13A33DB5CF")) != null)) {
				event.ammount = (float) Math.min(event.ammount, 1 + ((EntityLivingBase) event.source.getEntity()).getAttributeMap()
						.getAttributeInstance(SharedMonsterAttributes.attackDamage).getModifier(UUID.fromString("CB3F55D3-645C-4F38-A497-9C13A33DB5CF"))
						.getAmount())/*
										 * ((EntityLivingBase)event.source.
										 * getEntity()).getHeldItem().
										 * getAttributeModifiers(
										 * EntityEquipmentSlot.MAINHAND).get(
										 * SharedMonsterAttributes.attackDamage.
										 * getAttributeUnlocalizedName()).
										 * toArray(new
										 * AttributeModifier[2])[0].))
										 */;
			}
			event.ammount *= 0.65f;
		}
		if (event.entityLiving instanceof EntityTF2Character && event.source.getEntity() != null && event.source.getEntity() == event.entity) {
			event.ammount *= 0.35f;
		}

		if ((event.entityLiving.getActivePotionEffect(TF2weapons.bonk) != null || event.entityLiving.getEntityData().getBoolean("Ubercharge"))
				&& !event.source.canHarmInCreative()) {
			event.setCanceled(true);
		}
		if ((event.source.getEntity() != null && event.source.getEntity() instanceof EntityLivingBase)) {
			if (((EntityLivingBase) event.source.getEntity()).getActivePotionEffect(TF2weapons.bonk) != null) {
				event.setCanceled(true);
			}
			if (!(event.source instanceof TF2DamageSource) && TF2weapons.calculateCrits(event.entityLiving, (EntityLivingBase) event.source.getEntity(), 0) == 1) {
				event.ammount *= 1.35f;
			}
			ItemStack backpack = ItemHorn.getBackpack((EntityLivingBase) event.source.getEntity());
			if (backpack != null && !backpack.getTagCompound().getBoolean("Active")) {
				((ItemSoldierBackpack) backpack.getItem()).addRage(backpack, event.ammount, event.entityLiving);
			}
			if (((EntityLivingBase) event.source.getEntity()).getActivePotionEffect(TF2weapons.conch) != null) {
				ItemStack[] equipment = new ItemStack[] { event.entityLiving.getEquipmentInSlot(1), event.entityLiving.getEquipmentInSlot(2),
						event.entityLiving.getEquipmentInSlot(3), event.entityLiving.getEquipmentInSlot(4) };
				((EntityLivingBase) event.source.getEntity()).heal(0.35f * ArmorProperties.ApplyArmor(event.entityLiving, equipment, event.source, event.ammount));
			}
		}
		if (!event.entityLiving.worldObj.isRemote && event.entityLiving.getEntityData().getFloat("Overheal") > 0) {
			event.entityLiving.getEntityData().setFloat("Overheal", event.entityLiving.getAbsorptionAmount());
			if (event.entityLiving.getEntityData().getFloat("Overheal") <= 0) {
				event.entityLiving.getEntityData().setFloat("Overheal", -1f);
			}
			// TF2weapons.network.sendToDimension(new
			// TF2Message.PropertyMessage("overheal",
			// event.entityLiving.getAbsorptionAmount(),event.entityLiving),event.entityLiving.dimension);
		}
	}

	@SubscribeEvent
	public void stopBreak(BlockEvent.BreakEvent event) {
		if (event.getPlayer().getHeldItem() != null && event.getPlayer().getHeldItem().getItem() instanceof ItemUsable) {
			event.setCanceled(true);
		}
		if (event.getPlayer().getActivePotionEffect(TF2weapons.bonk) != null) {
			event.setCanceled(true);
		}
		if (WeaponsCapability.get(event.getPlayer()).invisTicks > 0) {
			event.setCanceled(true);
		}
		if (event.getPlayer().getActivePotionEffect(TF2weapons.stun) != null) {
			event.setCanceled(true);
		}
		if (event.getPlayer().getEntityData().getBoolean("IsDisguised")) {
			disguise(event.getPlayer(), false);
		}
	}

	/*
	 * @SubscribeEvent public void stopInteract(PlayerInteractEvent event){
	 * if(!((event.==PlayerInteractEvent.Action.RIGHT_CLICK_AIR||event.action==
	 * PlayerInteractEvent.Action.RIGHT_CLICK_BLOCK)&&event.entityPlayer.
	 * getHeldItem()!=null&&(event.entityPlayer.getHeldItem().getItem()
	 * instanceof ItemCloak || event.entityPlayer.getHeldItem().getItem()
	 * instanceof ItemDisguiseKit))){
	 * if(event.entityPlayer.getEntityData().getByte("Disguised")!=0){
	 * disguise(event.entityPlayer,false); }
	 * if((event.entityPlayer.getHeldItem() !=
	 * null&&event.entityPlayer.getHeldItem().getItem() instanceof ItemUsable &&
	 * event.action==PlayerInteractEvent.Action.LEFT_CLICK_BLOCK)||event.
	 * entityPlayer.getEntityData().getInteger("VisTicks")!=0){
	 * event.setCanceled(true); } } }
	 */
	@SubscribeEvent
	public void startTracking(PlayerEvent.StartTracking event) {
		if (event.target instanceof EntityPlayer && !event.target.worldObj.isRemote) {
			// System.out.println("Tracking");
			InventoryWearables inv = InventoryWearables.get(event.target);
			TF2weapons.network.sendTo(new TF2Message.WearableChangeMessage((EntityPlayer) event.target, 0, inv.getStackInSlot(0)), (EntityPlayerMP) event.entityPlayer);
			TF2weapons.network.sendTo(new TF2Message.WearableChangeMessage((EntityPlayer) event.target, 1, inv.getStackInSlot(1)), (EntityPlayerMP) event.entityPlayer);
			TF2weapons.network.sendTo(new TF2Message.WearableChangeMessage((EntityPlayer) event.target, 2, inv.getStackInSlot(2)), (EntityPlayerMP) event.entityPlayer);
			TF2weapons.network.sendTo(new TF2Message.WearableChangeMessage((EntityPlayer) event.target, 3, inv.getStackInSlot(3)), (EntityPlayerMP) event.entityPlayer);
		}
		/*
		 * if(event.entity instanceof EntityPlayer){
		 * if(event.entity.worldObj.isRemote){
		 * TF2weapons.network.sendToServer(new TF2Message.ActionMessage(99,
		 * (EntityLivingBase) event.entity)); } }
		 */

		/*
		 * if(event.entity.getDataManager().get(ENTITY_DISGUISE_TICKS)==40){
		 * disguise((EntityLivingBase) event.entity, true); }
		 */
	}

	@SubscribeEvent
	public void onSpawn(EntityJoinWorldEvent event) {
		if (event.entity instanceof EntityPlayer) {
			if (event.entity.worldObj.isRemote) {
				TF2weapons.network.sendToServer(new TF2Message.ActionMessage(99, (EntityLivingBase) event.entity));
			}
		}
	}

	@SubscribeEvent
	public void entityConstructing(EntityEvent.EntityConstructing event) {

		if (event.entity instanceof EntityLivingBase) {

			if (event.entity instanceof EntityTF2Character || event.entity instanceof EntityPlayer) {
				event.entity.registerExtendedProperties(WeaponsCapability.ID, new WeaponsCapability((EntityLivingBase) event.entity));
			}
			if (event.entity instanceof EntityPlayer) {
				event.entity.registerExtendedProperties(InventoryWearables.ID, new InventoryWearables());
			}
		}

	}

	/*
	 * @SubscribeEvent public void ChunkLoad(ChunkEvent.Load event){
	 * if(!event.world.isRemote){ List<EntityTeleporter>
	 * teleporter=event.world.getEntitiesWithinAABB(EntityTeleporter.class, new
	 * AxisAligned())); } }
	 */
	@SubscribeEvent
	public void cleanPlayer(PlayerLoggedOutEvent event) {
		event.player.getEntityData().setBoolean("Cloaked", event.player.getEntityData().getBoolean("IsCloaked"));
		event.player.getEntityData().setInteger("VisTicks", WeaponsCapability.get(event.player).invisTicks);
		event.player.getEntityData().setBoolean("Disguised", event.player.getEntityData().getBoolean("IsDisguised"));
		event.player.getEntityData().setString("DisguiseType", event.player.getEntityData().getString("DisguiseType"));
		ItemUsable.lastDamage.remove(event.player);
	}

	@SubscribeEvent
	public void loadPlayer(PlayerLoggedInEvent event) {
		// System.out.println("LoggedIn");
		if (TF2weapons.server.isDedicatedServer() || Minecraft.getMinecraft().getIntegratedServer().getPublic()) {
			for (WeaponData weapon : MapList.nameToData.values()) {
				TF2weapons.network.sendTo(new TF2Message.WeaponDataMessage(weapon), (EntityPlayerMP) event.player);
			}
		}
		/*
		 * TF2weapons.network.sendToAllAround(new
		 * TF2Message.WearableChangeMessage(event.player, 0,
		 * event.player.getCapability(TF2weapons.INVENTORY_CAP,
		 * null).getStackInSlot(0)), TF2weapons.pointFromEntity(event.player));
		 * TF2weapons.network.sendToAllAround(new
		 * TF2Message.WearableChangeMessage(event.player, 1,
		 * event.player.getCapability(TF2weapons.INVENTORY_CAP,
		 * null).getStackInSlot(1)), TF2weapons.pointFromEntity(event.player));
		 * TF2weapons.network.sendToAllAround(new
		 * TF2Message.WearableChangeMessage(event.player, 2,
		 * event.player.getCapability(TF2weapons.INVENTORY_CAP,
		 * null).getStackInSlot(2)), TF2weapons.pointFromEntity(event.player));
		 */
	}

	/*
	 * @SubscribeEvent public void onConnect(ServerConnectionFromClientEvent
	 * event){ new NetHandlerPlayServer(MinecraftServer.getServer(),
	 * event.manager, ((NetHandlerPlayServer)event.handler).playerEntity); }
	 */
	/*
	 * public static class PacketReceiveHack extends
	 * SimpleChannelInboundHandler<Packet>{
	 * 
	 * @Override protected void channelRead0(ChannelHandlerContext ctx, Packet
	 * msg) throws Exception { System.out.println(msg); }
	 * 
	 * }
	 */
	/*
	 * public static class MovePacketHack extends NetHandlerPlayServer{
	 * 
	 * public MovePacketHack(MinecraftServer server, NetworkManager
	 * networkManagerIn, EntityPlayerMP playerIn) { super(server,
	 * networkManagerIn, playerIn); // TODO Auto-generated constructor stub }
	 * public void processPlayer(C03PacketPlayer packetIn) {
	 * System.out.println("send"); super.processPlayer(packetIn); } }
	 */
	/*
	 * @SubscribeEvent public void stopUsing(PlayerInteractEvent event){
	 * if(event.entityPlayer.getActivePotionEffect(TF2weapons.stun)!=null){
	 * event.setCanceled(true); }
	 * if(event.entityWeaponsCapability.get().invisTicks>0){
	 * event.setCanceled(true); } }
	 */
	@SubscribeEvent
	public void stopUsing(PlayerInteractEvent event) {
		if (event.entityPlayer.getActivePotionEffect(TF2weapons.stun) != null) {
			event.setCanceled(true);
		}
		if (event.action == Action.RIGHT_CLICK_BLOCK) {
			if (WeaponsCapability.get(event.entity).invisTicks > 0 && !(event.entityPlayer.getHeldItem().getItem() instanceof ItemDisguiseKit)
					&& !(event.entityPlayer.getHeldItem().getItem() instanceof ItemCloak && event.entityPlayer.getHeldItem().getTagCompound().getBoolean("Active"))) {
				event.setCanceled(true);
			}
			if (event.entity.getEntityData().getBoolean("IsDisguised")
					&& !(event.entityPlayer.getHeldItem().getItem() instanceof ItemFood || event.entityPlayer.getHeldItem().getItem() instanceof ItemCloak)) {
				disguise(event.entityPlayer, false);
			}
		}
		if (event.entityLiving.getActivePotionEffect(TF2weapons.bonk) != null) {
			event.setCanceled(true);
		}
	}

	@SubscribeEvent
	public void stopUsing(EntityInteractEvent event) {

		if (event.entityPlayer.getActivePotionEffect(TF2weapons.stun) != null) {
			event.setCanceled(true);
		}
		if (WeaponsCapability.get(event.entity).invisTicks > 0) {
			event.setCanceled(true);
		}
		if (event.entity.getEntityData().getBoolean("IsDisguised") && !(event.entityPlayer.getHeldItem().getItem() instanceof ItemFood)) {
			disguise(event.entityPlayer, false);
		}
		if (event.entityLiving.getActivePotionEffect(TF2weapons.bonk) != null) {
			event.setCanceled(true);
		}
	}

	/*
	 * @SubscribeEvent public void stopUsing(PlayerInteractEvent event){
	 * if(event.entityPlayer.getActivePotionEffect(TF2weapons.stun)!=null){
	 * event.setCanceled(true); }
	 * if(event.entityWeaponsCapability.get().invisTicks>0){
	 * event.setCanceled(true); } }/*
	 * 
	 * @SubscribeEvent public void stopJump(LivingEvent.LivingJumpEvent event){
	 * 
	 * if(event.entityLiving.getActivePotionEffect(TF2weapons.stun)!=null||(
	 * event.entityLiving.getHeldItem()!=null&&event.entityLiving.getHeldItem().
	 * getItem() instanceof
	 * ItemMinigun&&event.entityLivingWeaponsCapability.get().chargeTicks>0)){
	 * event.entityLiving.isAirBorne=false; event.entityLiving.motionY-=0.5f;
	 * if(event.entityLiving.isSprinting()){ float f =
	 * event.entityLiving.rotationYaw * 0.017453292F; event.entityLiving.motionX
	 * += (double)(MathHelper.sin(f) * 0.2F); event.entityLiving.motionZ -=
	 * (double)(MathHelper.cos(f) * 0.2F); } }
	 * 
	 * } public static void disguise(EntityLivingBase entity, boolean active){
	 * entity.getDataManager().set(ENTITY_DISGUISED, active);
	 * entityWeaponsCapability.get().disguiseTicks=0;
	 * //System.out.println("disguised: "+active);
	 * /*if(!entity.worldObj.isRemote){ TF2weapons.network.sendToDimension(new
	 * TF2Message.PropertyMessage("Disguised",
	 * (byte)(active?1:0),entity),entity.dimension); }
	 */
	// ItemCloak.setInvisiblity(entity);
	/*
	 * if(entity.worldObj instanceof WorldServer && active){ if(active){
	 * EntityCreeper creeper=new EntityCreeper(entity.worldObj);
	 * creeper.tasks.taskEntries.clear();
	 * creeper.targetTasks.taskEntries.clear(); fakeEntities.put(entity,
	 * creeper); creeper.setPositionAndRotation(entity.posX, entity.posY,
	 * entity.posZ, entity.rotationYaw, entity.rotationPitch);
	 * entity.worldObj.spawnEntityInWorld(creeper);
	 * //((WorldServer)event.entity.worldObj).getEntityTracker().untrackEntity(
	 * event.entity); } else{ EntityLivingBase ent=fakeEntities.remove(entity);
	 * if(ent!=null){ ent.setDead(); } } } if(entity.worldObj.isRemote){
	 * if(active){ EntityCreeper creeper=new EntityCreeper(entity.worldObj);
	 * creeper.tasks.taskEntries.clear();
	 * creeper.targetTasks.taskEntries.clear(); fakeEntities.put(entity,
	 * creeper); creeper.setPositionAndRotation(entity.posX, entity.posY,
	 * entity.posZ, entity.rotationYaw, entity.rotationPitch);
	 * //((WorldServer)event.entity.worldObj).getEntityTracker().untrackEntity(
	 * event.entity); System.out.println("Disguise"); } else{ EntityLivingBase
	 * ent=fakeEntities.remove(entity); if(ent!=null){ ent.setDead(); } } }
	 */
	@SuppressWarnings("unchecked")
	@SubscribeEvent
	public void livingUpdate(final LivingEvent.LivingUpdateEvent event) {
		if (event.entity.isEntityAlive() && (event.entity.getExtendedProperties(WeaponsCapability.ID) != null)) {

			WeaponsCapability cap = WeaponsCapability.get(event.entity);
			cap.tick();

			if (event.entity.getEntityData().getBoolean("ExpJump")) {
				if (event.entity.onGround) {
					event.entity.getEntityData().setBoolean("ExpJump", false);
				}
				event.entity.motionX = event.entity.motionX * 1.04;
				event.entity.motionZ = event.entity.motionZ * 1.04;
			}
			/*
			 * if(event.entity instanceof EntityPlayer){
			 * System.out.println("Invisible: "+event.entity.isInvisible()); }
			 */
			if (!event.entity.worldObj.isRemote && cap.disguiseTicks > 0) {
				// System.out.println("disguise progress:
				// "+event.entity.getEntityData().getByte("DisguiseTicks"));
				if (++cap.disguiseTicks >= 40) {
					disguise(event.entityLiving, true);

				}
			}
			/*
			 * if(event.entity.worldObj.isRemote &&
			 * event.entity.getEntityData().getByte("Disguised")==1&&(
			 * fakeEntities.get(event.entityLiving)==null||fakeEntities.get(
			 * event.entityLiving).isDead)){ disguise(event.entityLiving,true);
			 * }
			 */
			if (event.entity.getEntityData().getBoolean("IsCloaked")) {
				// System.out.println("cloak");
				boolean visible = event.entityLiving.hurtTime == 10;
				if (!visible) {
					List<Entity> closeEntities = event.entity.worldObj.getEntitiesWithinAABBExcludingEntity(event.entity, event.entity.boundingBox.expand(1, 2, 1),
							new IEntitySelector() {

								@Override
								public boolean isEntityApplicable(Entity input) {
									// TODO Auto-generated method stub
									return input instanceof EntityLivingBase && !TF2weapons.isOnSameTeam(event.entity, input);
								}

							});
					for (Entity ent : closeEntities) {
						if (ent.getDistanceSqToEntity(event.entity) < 1) {
							visible = true;
						}
						break;
					}
				}
				if (visible) {
					// System.out.println("reveal");
					cap.invisTicks = Math.min(10, cap.invisTicks);
					event.entity.setInvisible(false);
				}
				if (WeaponsCapability.get(event.entity).invisTicks < 20) {

					cap.invisTicks = Math.min(20, cap.invisTicks + 2);

				} else {
					if (!event.entity.isInvisible()) {
						// System.out.println("full");
						event.entity.setInvisible(true);
					}

				}
				boolean active = event.entity.worldObj.isRemote || ItemCloak.searchForWatches(event.entityLiving) != null;
				if (!active) {
					event.entity.getEntityData().setBoolean("IsCloaked", false);
					event.entity.setInvisible(false);
					// System.out.println("decloak");
				}
			} else {
				if (WeaponsCapability.get(event.entity).invisTicks > 0) {
					cap.invisTicks--;
					if (WeaponsCapability.get(event.entity).invisTicks == 0) {
						event.entity.setInvisible(false);
					}
				}
			}
		}

		if (event.entityLiving.getAITarget() != null && event.entityLiving.getAITarget().getExtendedProperties(WeaponsCapability.ID) != null
				&& WeaponsCapability.get(event.entityLiving.getAITarget()).invisTicks >= 20) {
			event.entityLiving.setRevengeTarget(null);
		}

		if (event.entityLiving instanceof EntityLiving && ((EntityLiving) event.entity).getAttackTarget() != null
				&& ((EntityLiving) event.entity).getAttackTarget().getExtendedProperties(WeaponsCapability.ID) != null
				&& WeaponsCapability.get(((EntityLiving) event.entity).getAttackTarget()).invisTicks >= 20) {
			((EntityLiving) event.entity).setAttackTarget(null);
		}

		if (event.entity.getEntityData().getFloat("Overheal") == -1) {
			event.entityLiving.getEntityData().setFloat("Overheal", 0f);
			event.entityLiving.setAbsorptionAmount(0);
		}
		if (event.entity.getEntityData().getFloat("Overheal") > 0) {
			if (event.entityLiving.worldObj.isRemote) {
				event.entityLiving.setAbsorptionAmount(event.entityLiving.getEntityData().getFloat("Overheal"));
			}
			event.entityLiving.setAbsorptionAmount(event.entityLiving.getAbsorptionAmount() - event.entityLiving.getMaxHealth() * 0.001666f);
			if (!event.entityLiving.worldObj.isRemote) {

				if (event.entityLiving.getAbsorptionAmount() <= 0) {
					event.entityLiving.getEntityData().setFloat("Overheal", -1f);
				} else {
					event.entityLiving.getEntityData().setFloat("Overheal", event.entityLiving.getAbsorptionAmount());
				}
				TF2weapons.network.sendToAllAround(new TF2Message.PropertyMessage("Overheal", event.entityLiving.getEntityData().getFloat("Overheal"), event.entityLiving),
						TF2weapons.pointFromEntity(event.entityLiving));
			}
		}

		if (event.entity.getEntityData().getBoolean("Ubercharge") && !(event.entityLiving.getHeldItem() != null && event.entityLiving.getHeldItem().getItem() instanceof ItemMedigun
				&& event.entityLiving.getHeldItem().getTagCompound().getBoolean("Activated"))) {
			List<EntityLivingBase> list = event.entityLiving.worldObj.selectEntitiesWithinAABB(EntityLivingBase.class, AxisAlignedBB.getBoundingBox(event.entityLiving.posX - 8,
					event.entityLiving.posY - 8, event.entityLiving.posZ - 8, event.entityLiving.posX + 8, event.entityLiving.posY + 8, event.entityLiving.posZ + 8),
					new IEntitySelector() {

						@Override
						public boolean isEntityApplicable(Entity input) {
							// TODO Auto-generated method stub
							return input.worldObj.getEntityByID(WeaponsCapability.get(input) != null ? WeaponsCapability.get(input).healTarget : -1) == event.entityLiving
									&& ((EntityLivingBase) input).getHeldItem().getTagCompound().getBoolean("Activated");
						}

					});
			boolean isOK = !list.isEmpty();
			if (!isOK) {
				event.entity.getEntityData().setBoolean("Ubercharge",false);
			}
		}
	}

	@SubscribeEvent
	public void loadWorld(WorldEvent.Load event) {
		if (!event.world.isRemote && event.world.getScoreboard().getTeam("RED") == null) {
			ScorePlayerTeam teamRed = event.world.getScoreboard().createTeam("RED");
			ScorePlayerTeam teamBlu = event.world.getScoreboard().createTeam("BLU");

			teamRed.setSeeFriendlyInvisiblesEnabled(true);
			teamRed.setAllowFriendlyFire(false);
			teamBlu.setSeeFriendlyInvisiblesEnabled(true);
			teamBlu.setAllowFriendlyFire(false);

			event.world.getScoreboard().func_96513_c(teamRed);
			event.world.getScoreboard().func_96513_c(teamBlu);

		}
		if (!event.world.isRemote && event.world.getScoreboard().getTeam("TF2Bosses") == null) {
			ScorePlayerTeam teamBosses = event.world.getScoreboard().createTeam("TF2Bosses");
			teamBosses.setSeeFriendlyInvisiblesEnabled(true);
			teamBosses.setAllowFriendlyFire(false);
			event.world.getScoreboard().func_96513_c(teamBosses);
		}
	}

	@SubscribeEvent
	public void medicSpawn(LivingSpawnEvent.SpecialSpawn event) {
		float chance = 0;
		if (event.entity instanceof EntityHeavy) {
			chance = 0.16f;
		} else if (event.entity instanceof EntitySoldier) {
			chance = 0.08f;
		} else if (event.entity instanceof EntityDemoman) {
			chance = 0.07f;
		} else if (event.entity instanceof EntityPyro) {
			chance = 0.06f;
		} else {
			return;
		}
		if (event.world.rand.nextFloat() < event.world.difficultySetting.getDifficultyId() * chance) {
			EntityMedic medic = new EntityMedic(event.world);
			medic.setLocationAndAngles(event.entity.posX + event.world.rand.nextDouble() * 0.5 - 0.25, event.entity.posY,
					event.entity.posZ + event.world.rand.nextDouble() * 0.5 - 0.25, event.world.rand.nextFloat() * 360.0F, 0.0F);
			medic.natural = true;
			// medic.setEntTeam(event.world.rand.nextInt(2));
			medic.onSpawnWithEgg(null);
			EntityTF2Character.nextEntTeam = medic.getEntTeam();

			event.world.spawnEntityInWorld(medic);
		}
	}

	@SubscribeEvent
	public void livingDeath(LivingDeathEvent event) {
		if (!event.entity.worldObj.isRemote && event.source != null && event.source.getEntity() != null && event.source.getEntity() instanceof EntityLivingBase) {
			ItemStack stack = null;
			EntityLivingBase living = (EntityLivingBase) event.source.getEntity();
			if (event.source instanceof TF2DamageSource) {
				stack = ((TF2DamageSource) event.source).getWeapon();
			} else {
				stack = living.getHeldItem();
			}
			if (stack != null && stack.hasTagCompound() && stack.getTagCompound().getBoolean("Strange") && stack.getItem() instanceof ItemWeapon) {
				if (!(event.entityLiving instanceof EntityPlayer)) {
					stack.getTagCompound().setInteger("Kills", stack.getTagCompound().getInteger("Kills") + 1);
				} else {
					stack.getTagCompound().setInteger("PlayerKills", stack.getTagCompound().getInteger("PlayerKills") + 1);
				}
				onStrangeUpdate(stack, living);
				if (stack.getTagCompound().getBoolean("Australium")) {
					TF2weapons.network.sendToAllAround(new TF2Message.ActionMessage(19, event.entityLiving), TF2weapons.pointFromEntity(event.entity));
					event.entity.playSound("weapon.turntogold", 0.8f, 2f);
					event.entityLiving.deathTime = 20;
					event.entityLiving.onEntityUpdate();
				}
			}
			if (stack != null && stack.getItem() instanceof ItemWeapon) {
				float toHeal = TF2Attribute.getModifier("Health Kill", stack, 0, null);
				if (toHeal != 0) {
					living.heal(toHeal);
				}
			}
		}
		if (event.entityLiving instanceof EntityPlayer && !event.entity.worldObj.isRemote) {
			InventoryWearables inv = InventoryWearables.get(event.entityLiving);
			for (int i = 3; i < 13; i++) {
				if (inv.getStackInSlot(i) != null)
					event.entityLiving.entityDropItem(inv.getStackInSlot(i), 0.5f);
			}
		}
	}

	@SubscribeEvent
	public void addTooltip(ItemTooltipEvent event) {
		if (event.itemStack.hasTagCompound() && event.itemStack.getTagCompound().getBoolean("Australium") && !(event.itemStack.getItem() instanceof ItemFromData)
				&& !event.itemStack.hasDisplayName()) {
			event.toolTip.set(0, "Australium " + event.toolTip.get(0));
		}
		if (event.itemStack.hasTagCompound() && event.itemStack.getTagCompound().getBoolean("Strange")) {
			if (!(event.itemStack.getItem() instanceof ItemFromData) && !event.itemStack.hasDisplayName())
				event.toolTip.set(0, STRANGE_TITLES[event.itemStack.getTagCompound().getInteger("StrangeLevel")] + " " + event.toolTip.get(0));

			event.toolTip.add("");
			if (event.itemStack.getItem() instanceof ItemMedigun) {
				event.toolTip.add("Ubercharges: " + event.itemStack.getTagCompound().getInteger("Ubercharges"));
			} else if (event.itemStack.getItem() instanceof ItemCloak) {
				event.toolTip.add("Seconds cloaked: " + event.itemStack.getTagCompound().getInteger("CloakTicks") / 20);
			} else {
				event.toolTip.add("Mob kills: " + event.itemStack.getTagCompound().getInteger("Kills"));
				event.toolTip.add("Player kills: " + event.itemStack.getTagCompound().getShort("PlayerKills"));
			}
		}
	}

	public static void onStrangeUpdate(ItemStack stack, EntityLivingBase player) {
		int points = 0;
		if (stack.getItem() instanceof ItemMedigun) {
			points = stack.getTagCompound().getInteger("Ubercharges");
		} else if (stack.getItem() instanceof ItemCloak) {
			points = stack.getTagCompound().getInteger("CloakTicks") / 400;
		} else {
			points = stack.getTagCompound().getInteger("Kills");
			points += stack.getTagCompound().getInteger("PlayerKills") * 5;
		}
		int calculatedLevel = 0;

		if (points >= STRANGE_KILLS[STRANGE_KILLS.length - 1])
			calculatedLevel = STRANGE_KILLS.length - 1;
		else {
			for (int i = 1; i < STRANGE_KILLS.length; i++) {
				if (points < STRANGE_KILLS[i]) {
					calculatedLevel = i - 1;
					break;
				}
			}
		}

		if (calculatedLevel > stack.getTagCompound().getInteger("StrangeLevel")) {
			stack.getTagCompound().setInteger("StrangeLevel", calculatedLevel);
			final int level = calculatedLevel;
			if (player instanceof EntityPlayerMP) {
				((EntityPlayerMP) player).addStat(new Achievement(Integer.toString(player.getRNG().nextInt()), "strangeUp", 0, 0, stack, null) {
					@Override
					public IChatComponent func_150951_e() {
						return super.func_150951_e().appendText(STRANGE_TITLES[level]);
					}
				}, 665);
			}
		}
	}
}
