package rafradek.TF2weapons;

import java.util.HashMap;
import java.util.Map;

import net.minecraft.item.Item;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.IIcon;
import net.minecraft.util.ResourceLocation;
import rafradek.TF2weapons.decoration.ItemWearable;
import rafradek.TF2weapons.projectiles.EntityFlame;
import rafradek.TF2weapons.projectiles.EntityGrenade;
import rafradek.TF2weapons.projectiles.EntityProjectileBase;
import rafradek.TF2weapons.projectiles.EntityRocket;
import rafradek.TF2weapons.projectiles.EntitySyringe;
import rafradek.TF2weapons.weapons.ItemBonk;
import rafradek.TF2weapons.weapons.ItemBulletWeapon;
import rafradek.TF2weapons.weapons.ItemCloak;
import rafradek.TF2weapons.weapons.ItemFlameThrower;
import rafradek.TF2weapons.weapons.ItemKnife;
import rafradek.TF2weapons.weapons.ItemMedigun;
import rafradek.TF2weapons.weapons.ItemMeleeWeapon;
import rafradek.TF2weapons.weapons.ItemMinigun;
import rafradek.TF2weapons.weapons.ItemProjectileWeapon;
import rafradek.TF2weapons.weapons.ItemSapper;
import rafradek.TF2weapons.weapons.ItemSniperRifle;
import rafradek.TF2weapons.weapons.ItemSoldierBackpack;
import rafradek.TF2weapons.weapons.ItemStickyLauncher;
import rafradek.TF2weapons.weapons.ItemUsable;
import rafradek.TF2weapons.weapons.ItemWrench;

public class MapList {
	
	public static Map<Integer, String> integerToMob;
	public static Map<String, Item> weaponClasses;
	public static Map<String, WeaponData.PropertyType> propertyTypes;
	public static Map<String, Class<? extends EntityProjectileBase>> projectileClasses;
	public static Map<String, WeaponData> nameToData;
	public static Map<String, IIcon> nameToIcon;
	public static Map<String, TF2Attribute> nameToAttribute;
	public static Map<String, ItemUsable> specialWeapons;
	public static Map<String, NBTTagCompound> buildInAttributes;
	public static Map<String, Integer> potionNames;
	//public static Map<MinigunLoopSound, EntityLivingBase > fireCritSounds;
	//public static Map<List<SpawnListEntry>, SpawnListEntry> scoutSpawn;
	
	public static void initMaps(){
		weaponClasses = new HashMap<String, Item>();
		projectileClasses = new HashMap<String, Class<? extends EntityProjectileBase>>();
		nameToData = new HashMap<String, WeaponData>();
		propertyTypes = new HashMap<String, WeaponData.PropertyType>();
		nameToAttribute = new HashMap<String, TF2Attribute>();
		buildInAttributes = new HashMap<String, NBTTagCompound>();
		specialWeapons = new HashMap<String, ItemUsable>();
		integerToMob = new HashMap<Integer,String>();
		potionNames=new HashMap<>();
		nameToIcon=new HashMap<>();
		//scoutSpawn=new HashMap<List<SpawnListEntry>, SpawnListEntry>();
		weaponClasses.put("sniperrifle", new ItemSniperRifle());
		weaponClasses.put("bullet", new ItemBulletWeapon());
		weaponClasses.put("minigun", new ItemMinigun());
		weaponClasses.put("projectile", new ItemProjectileWeapon());
		weaponClasses.put("stickybomb", new ItemStickyLauncher());
		weaponClasses.put("flamethrower", new ItemFlameThrower());
		weaponClasses.put("knife", new ItemKnife());
		weaponClasses.put("medigun", new ItemMedigun());
		weaponClasses.put("cloak", new ItemCloak());
		weaponClasses.put("wrench", new ItemWrench());
		weaponClasses.put("bonk", new ItemBonk());
		weaponClasses.put("cosmetic", new ItemWearable());
		weaponClasses.put("melee", new ItemMeleeWeapon());
		weaponClasses.put("sapper", new ItemSapper());
		weaponClasses.put("backpack", new ItemSoldierBackpack());
		weaponClasses.put("crate", new ItemCrate());
		/*weaponDatas.put("sniperrifle", );
		weaponDatas.put("bullet", new ItemBulletWeapon());
		weaponDatas.put("minigun", new ItemMinigun());
		weaponDatas.put("projectile", new ItemProjectileWeapon());
		weaponDatas.put("stickybomb", new ItemStickyLauncher());
		weaponDatas.put("flamethrower", new ItemFlameThrower());
		weaponDatas.put("knife", new ItemKnife());
		weaponDatas.put("medigun", new ItemMedigun());
		weaponDatas.put("cloak", new ItemCloak());
		weaponDatas.put("wrench", new ItemWrench());
		weaponDatas.put("bonk", new ItemBonk());
		weaponDatas.put("cosmetic", new ItemWearable());*/
		
		projectileClasses.put("rocket", EntityRocket.class);
		projectileClasses.put("fire", EntityFlame.class);
		projectileClasses.put("grenade", EntityGrenade.class);
		projectileClasses.put("syringe", EntitySyringe.class);
		
		
	}
}
