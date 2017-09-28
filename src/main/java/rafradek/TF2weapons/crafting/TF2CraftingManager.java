package rafradek.TF2weapons.crafting;

import java.util.List;
import java.util.Map;

import javax.annotation.Nullable;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.ShapedRecipes;
import net.minecraft.item.crafting.ShapelessRecipes;
import net.minecraft.world.World;
import net.minecraftforge.oredict.ShapedOreRecipe;
import net.minecraftforge.oredict.ShapelessOreRecipe;
import rafradek.TF2weapons.ItemFromData;
import rafradek.TF2weapons.TF2weapons;

public class TF2CraftingManager {
	public static final TF2CraftingManager INSTANCE= new TF2CraftingManager();
    private final List<IRecipe> recipes = Lists.<IRecipe>newArrayList();

	public TF2CraftingManager(){
		ItemStack bonk=ItemFromData.getNewStack("bonk");
		bonk.stackSize=2;
		ItemStack cola=ItemFromData.getNewStack("critcola");
		cola.stackSize=2;
		addRecipe(new ShapelessOreRecipe(new ItemStack(TF2weapons.itemAmmo,8,1),new Object[]{"ingotCopper","ingotLead",Items.gunpowder}));
		addRecipe(new ShapelessOreRecipe(new ItemStack(TF2weapons.itemAmmo,20,2),new Object[]{"ingotCopper","ingotLead",Items.gunpowder}));
		addRecipe(new ShapelessOreRecipe(new ItemStack(TF2weapons.itemAmmo,20,3),new Object[]{"ingotCopper","ingotLead",Items.gunpowder}));
		addRecipe(new ShapelessOreRecipe(new ItemStack(TF2weapons.itemAmmo,8,4),new Object[]{"ingotCopper","ingotLead",Items.gunpowder}));
		addRecipe(new ShapelessOreRecipe(new ItemStack(TF2weapons.itemAmmo,32,5),new Object[]{"ingotCopper","ingotLead",Items.gunpowder}));
		addRecipe(new ShapelessOreRecipe(new ItemStack(TF2weapons.itemAmmo,3,6),new Object[]{"ingotCopper","ingotLead",Items.gunpowder}));
		addRecipe(new ShapelessOreRecipe(new ItemStack(TF2weapons.itemAmmo,8,7),new Object[]{"ingotIron","ingotIron",Blocks.tnt}));
		addRecipe(new ShapelessOreRecipe(new ItemStack(TF2weapons.itemAmmo,8,8),new Object[]{"ingotIron","ingotIron",Blocks.tnt}));
		addRecipe(new ShapelessOreRecipe(new ItemStack(TF2weapons.itemAmmo,8,11),new Object[]{"ingotIron","ingotIron",Blocks.tnt}));
		addShapelessRecipe(new ItemStack(TF2weapons.itemAmmoMedigun,1),new Object[]{Items.speckled_melon,Items.ghast_tear,new ItemStack(Items.dye,1,15)});
		addRecipe(new ShapelessOreRecipe(new ItemStack(TF2weapons.itemAmmoFire,1),new Object[]{"ingotIron",Items.magma_cream,"ingotIron"}));
		addRecipe(new ShapelessOreRecipe(new ItemStack(TF2weapons.itemAmmo,25,9),new Object[]{"ingotIron",Items.paper}));
		addRecipe(new AustraliumRecipe());
		addRecipe(new ShapedOreRecipe(ItemFromData.getNewStack("cloak"),new Object[]{"AAA","LGL","AAA",'A',"ingotAustralium",'I',"ingotIron",'G', "blockGlass",'L',Items.leather}));
		addRecipe(new ShapedOreRecipe(TF2weapons.itemDisguiseKit,new Object[]{"I I","PAG","I I",'A',"ingotAustralium",'I',"ingotIron",'G', "blockGlass",'P',Items.paper}));
		addRecipe(new ShapedOreRecipe(ItemFromData.getNewStack("sapper"),new Object[]{" R ","IRI"," R ",'I',"ingotIron",'R', "dustRedstone"}));
		addRecipe(new ShapedOreRecipe(new ItemStack(TF2weapons.itemBuildingBox,1,18),new Object[]{"RDR","GIG","III",'D',new ItemStack(Blocks.dispenser),'I',"ingotIron",'G', Items.gunpowder,'R',"dustRedstone"}));
		addRecipe(new ShapedOreRecipe(new ItemStack(TF2weapons.itemHorn),new Object[]{"CLC","C C"," C ",'C',"ingotCopper",'L',Items.leather}));
		addRecipe(new ShapedOreRecipe(new ItemStack(TF2weapons.itemBuildingBox,1,20),new Object[]{"MDR","SIm","rIG",'D',new ItemStack(Blocks.dispenser),'I',"ingotIron",'M',new ItemStack(TF2weapons.itemAmmo,1,12),'G', new ItemStack(TF2weapons.itemAmmo,1,8),'R',"dustRedstone",'r', new ItemStack(TF2weapons.itemAmmo,1,7),'S', new ItemStack(TF2weapons.itemAmmo,1,1),'m', new ItemStack(TF2weapons.itemAmmo,1,2)}));
		addRecipe(new ShapedOreRecipe(new ItemStack(TF2weapons.itemBuildingBox,1,22),new Object[]{"IAI","RAR","IAI",'I',"ingotIron",'A',new ItemStack(TF2weapons.itemTF2,1,6),'R',"dustRedstone"}));
		//addRecipe(new ShapedOreRecipe(new ItemStack(TF2weapons.itemAmmoBelt),new Object[]{" IL","IL ","L  ",'I',"ingotIron",'L',"leather"}));
		addRecipe(new ShapedOreRecipe(bonk,new Object[]{"SDS","IWI","SAS",'I',"ingotIron",'A',new ItemStack(TF2weapons.itemTF2,1,6),'W',new ItemStack(Items.water_bucket),'S',new ItemStack(Items.sugar),'D',"dyeYellow"}));
		addRecipe(new ShapedOreRecipe(cola,new Object[]{"SDS","IWI","SAS",'I',"ingotIron",'A',new ItemStack(TF2weapons.itemTF2,1,6),'W',new ItemStack(Items.water_bucket),'S',new ItemStack(Items.sugar),'D',"dyeMagenta"}));
		addRecipe(new ShapedOreRecipe(new ItemStack(TF2weapons.itemSandvich),new Object[]{" B ","LHL"," B ",'B',new ItemStack(Items.bread),'L',new ItemStack(Blocks.tallgrass,1,1),'H',new ItemStack(Items.porkchop)}));
		//addRecipe(new ShapedOreRecipe(new ItemStack(TF2weapons.itemChocolate,2),new Object[]{"CCC","CCC","MII",'C',new ItemStack(Items.dye,3),'M',new ItemStack(Items.milk_bucket),'I',"paper"}));
		addShapelessRecipe(new ItemStack(TF2weapons.itemTF2,1,4),new ItemStack(TF2weapons.itemTF2,1,3),new ItemStack(TF2weapons.itemTF2,1,3),new ItemStack(TF2weapons.itemTF2,1,3));
		addShapelessRecipe(new ItemStack(TF2weapons.itemTF2,1,5),new ItemStack(TF2weapons.itemTF2,1,4),new ItemStack(TF2weapons.itemTF2,1,4),new ItemStack(TF2weapons.itemTF2,1,4));
		addShapelessRecipe(new ItemStack(TF2weapons.itemTF2,3,4),new ItemStack(TF2weapons.itemTF2,1,5));
		addShapelessRecipe(new ItemStack(TF2weapons.itemTF2,3,3),new ItemStack(TF2weapons.itemTF2,1,4));
		addShapelessRecipe(new ItemStack(TF2weapons.itemTF2,1,9),new ItemStack(TF2weapons.itemTF2,1,3),new ItemStack(TF2weapons.itemTF2,1,3));
		addShapelessRecipe(new ItemStack(TF2weapons.itemTF2,1,10),new ItemStack(TF2weapons.itemTF2,1,5),new ItemStack(TF2weapons.itemTF2,1,5),new ItemStack(TF2weapons.itemTF2,1,5));
		addRecipe(new OpenCrateRecipe());
		addRecipe(new RecipeToScrap());
	}
	
	public ShapedRecipes addRecipe(ItemStack stack, Object... recipeComponents)
    {
        String s = "";
        int i = 0;
        int j = 0;
        int k = 0;

        if (recipeComponents[i] instanceof String[])
        {
            String[] astring = ((String[])recipeComponents[i++]);

            for (String s2 : astring)
            {
                ++k;
                j = s2.length();
                s = s + s2;
            }
        }
        else
        {
            while (recipeComponents[i] instanceof String)
            {
                String s1 = (String)recipeComponents[i++];
                ++k;
                j = s1.length();
                s = s + s1;
            }
        }

        Map<Character, ItemStack> map;

        for (map = Maps.<Character, ItemStack>newHashMap(); i < recipeComponents.length; i += 2)
        {
            Character character = (Character)recipeComponents[i];
            ItemStack itemstack = null;

            if (recipeComponents[i + 1] instanceof Item)
            {
                itemstack = new ItemStack((Item)recipeComponents[i + 1]);
            }
            else if (recipeComponents[i + 1] instanceof Block)
            {
                itemstack = new ItemStack((Block)recipeComponents[i + 1], 1, 32767);
            }
            else if (recipeComponents[i + 1] instanceof ItemStack)
            {
                itemstack = (ItemStack)recipeComponents[i + 1];
            }

            map.put(character, itemstack);
        }

        ItemStack[] aitemstack = new ItemStack[j * k];

        for (int l = 0; l < j * k; ++l)
        {
            char c0 = s.charAt(l);

            if (map.containsKey(Character.valueOf(c0)))
            {
                aitemstack[l] = map.get(Character.valueOf(c0)).copy();
            }
            else
            {
                aitemstack[l] = null;
            }
        }

        ShapedRecipes shapedrecipes = new ShapedRecipes(j, k, aitemstack, stack);
        this.recipes.add(shapedrecipes);
        return shapedrecipes;
    }

    /**
     * Adds a shapeless crafting recipe to the the game.
     */
    public void addShapelessRecipe(ItemStack stack, Object... recipeComponents)
    {
        List<ItemStack> list = Lists.<ItemStack>newArrayList();

        for (Object object : recipeComponents)
        {
            if (object instanceof ItemStack)
            {
                list.add(((ItemStack)object).copy());
            }
            else if (object instanceof Item)
            {
                list.add(new ItemStack((Item)object));
            }
            else
            {
                if (!(object instanceof Block))
                {
                    throw new IllegalArgumentException("Invalid shapeless recipe: unknown type " + object.getClass().getName() + "!");
                }

                list.add(new ItemStack((Block)object));
            }
        }

        this.recipes.add(new ShapelessRecipes(stack, list));
    }

    /**
     * Adds an IRecipe to the list of crafting recipes.
     */
    public void addRecipe(IRecipe recipe)
    {
        this.recipes.add(recipe);
    }

    /**
     * Retrieves an ItemStack that has multiple recipes for it.
     */
    @Nullable
    public ItemStack findMatchingRecipe(InventoryCrafting craftMatrix, World worldIn)
    {
        for (IRecipe irecipe : this.recipes)
        {
            if (irecipe.matches(craftMatrix, worldIn))
            {
                return irecipe.getCraftingResult(craftMatrix);
            }
        }

        return null;
    }

    public List<IRecipe> getRecipeList()
    {
        return this.recipes;
    }
}
