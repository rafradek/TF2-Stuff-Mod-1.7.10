package rafradek.TF2weapons.upgrade;

import java.util.List;
import java.util.Random;

import cpw.mods.fml.common.registry.VillagerRegistry.IVillageCreationHandler;
import net.minecraft.block.BlockStairs;
import net.minecraft.block.BlockStone;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import net.minecraft.world.gen.structure.StructureBoundingBox;
import net.minecraft.world.gen.structure.StructureComponent;
import net.minecraft.world.gen.structure.StructureVillagePieces;
import net.minecraft.world.gen.structure.StructureVillagePieces.PieceWeight;
import net.minecraft.world.gen.structure.StructureVillagePieces.Start;
import net.minecraft.world.gen.structure.StructureVillagePieces.Village;
import rafradek.TF2weapons.TF2weapons;
import rafradek.TF2weapons.characters.EntitySaxtonHale;

public class MannCoBuilding extends Village{

	private boolean haleSpawned;
	
	public MannCoBuilding(){
		
	}
	
	public MannCoBuilding(Start startPiece, int p5, Random random, StructureBoundingBox structureboundingbox,
			int facing) {
		super(startPiece, p5);
		this.coordBaseMode=facing;
        this.boundingBox = structureboundingbox;
	}

	protected void func_143012_a(NBTTagCompound tagCompound)
    {
        super.func_143012_a(tagCompound);
        tagCompound.setBoolean("HS", this.haleSpawned);
      
    }

    /**
     * (abstract) Helper method to read subclass data from NBT
     */
    protected void func_143011_b(NBTTagCompound tagCompound)
    {
        super.func_143011_b(tagCompound);
        this.haleSpawned=tagCompound.getBoolean("HS");
    }

    /**
     * second Part of Structure generating, this for example places Spiderwebs, Mob Spawners, it closes
     * Mineshafts at the end, it adds Fences...
     */
    @Override
	public boolean addComponentParts(World worldIn, Random randomIn, StructureBoundingBox structureBoundingBoxIn)
    {
        if (this.field_143015_k < 0)
        {
            this.field_143015_k = this.getAverageGroundLevel(worldIn, structureBoundingBoxIn);

            if (this.field_143015_k < 0)
            {
                return true;
            }

            this.boundingBox.offset(0, this.field_143015_k - this.boundingBox.maxY + 15, 0);
        }

        /*IBlockState stone = Blocks.STONE.getDefaultState().withProperty(BlockStone.VARIANT, BlockStone.EnumType.ANDESITE_SMOOTH);
        IBlockState iblockstate1 = this.getBiomeSpecificBlockState(Blocks.OAK_STAIRS.getDefaultState().withProperty(BlockStairs.FACING, EnumFacing.WEST));
        IBlockState iblockstate2 = this.getBiomeSpecificBlockState(Blocks.OAK_STAIRS.getDefaultState().withProperty(BlockStairs.FACING, EnumFacing.NORTH));
        IBlockState wood = this.getBiomeSpecificBlockState(Blocks.PLANKS.getDefaultState());
        IBlockState upgradeStation = this.getBiomeSpecificBlockState(TF2weapons.blockUpgradeStation.getDefaultState().withProperty(BlockUpgradeStation.HOLDER, false));
        IBlockState upgradeStationHolder = this.getBiomeSpecificBlockState(TF2weapons.blockUpgradeStation.getDefaultState().withProperty(BlockUpgradeStation.HOLDER, true));
        IBlockState iblockstate5 = this.getBiomeSpecificBlockState(Blocks.LOG.getDefaultState());
        IBlockState iblockstate6 = this.getBiomeSpecificBlockState(Blocks.OAK_FENCE.getDefaultState())*/
        this.fillWithBlocks(worldIn, structureBoundingBoxIn, 0, 1, 0, 13, 16, 10, Blocks.air, Blocks.air, false);
        this.fillWithBlocks(worldIn, structureBoundingBoxIn, 0, 0, 2, 13, 15, 10, Blocks.double_stone_slab,Blocks.air, false);
        this.fillWithBlocks(worldIn, structureBoundingBoxIn, 1, 0, 3, 12, 0, 9, Blocks.planks, Blocks.planks, false);
        this.fillWithBlocks(worldIn, structureBoundingBoxIn, 1, 4, 3, 12, 4, 9, Blocks.double_stone_slab, Blocks.double_stone_slab, false);
        this.fillWithBlocks(worldIn, structureBoundingBoxIn, 0, 2, 3, 0, 3, 9, Blocks.glass_pane, Blocks.glass_pane, false);
        this.fillWithBlocks(worldIn, structureBoundingBoxIn, 13, 2, 3, 13, 3, 9, Blocks.glass_pane, Blocks.glass_pane, false);
        this.fillWithBlocks(worldIn, structureBoundingBoxIn, 1, 2, 2, 12, 3, 2, Blocks.glass_pane, Blocks.glass_pane, false);
        this.fillWithBlocks(worldIn, structureBoundingBoxIn, 1, 2, 10, 12, 3, 10, Blocks.glass_pane, Blocks.glass_pane, false);
        this.fillWithBlocks(worldIn, structureBoundingBoxIn, 5, 0, 2, 8, 3, 2, Blocks.planks, Blocks.planks, false);
        this.fillWithBlocks(worldIn, structureBoundingBoxIn, 6, 1, 2, 7, 2, 2, Blocks.air, Blocks.air, false);
        this.placeBlockAtCurrentPosition(worldIn, Blocks.oak_stairs, this.getMetadataWithOffset(Blocks.oak_stairs,3) ,6, 0, 1,structureBoundingBoxIn);
        this.placeBlockAtCurrentPosition(worldIn, Blocks.oak_stairs, this.getMetadataWithOffset(Blocks.oak_stairs,3) ,7, 0, 1,structureBoundingBoxIn);
        this.placeBlockAtCurrentPosition(worldIn, Blocks.glowstone,0, 7, 4, 6, structureBoundingBoxIn);
        this.placeBlockAtCurrentPosition(worldIn, Blocks.glowstone,0, 4, 4, 6, structureBoundingBoxIn);
        this.placeBlockAtCurrentPosition(worldIn, Blocks.glowstone,0, 10, 4, 6, structureBoundingBoxIn);
        this.fillWithBlocks(worldIn, structureBoundingBoxIn, 1, 1, 5, 1, 2, 7, TF2weapons.blockUpgradeStation, TF2weapons.blockUpgradeStation, false);
        this.fillWithBlocks(worldIn, structureBoundingBoxIn, 12, 1, 5, 12, 2, 7, TF2weapons.blockUpgradeStation, TF2weapons.blockUpgradeStation, false);
        this.placeBlockAtCurrentPosition(worldIn, TF2weapons.blockUpgradeStation,this.getMetadataWithOffset(Blocks.dispenser, 5)+8, 1, 1, 6, structureBoundingBoxIn);
        this.placeBlockAtCurrentPosition(worldIn, TF2weapons.blockUpgradeStation,this.getMetadataWithOffset(Blocks.dispenser, 4)+8, 12, 1, 6, structureBoundingBoxIn);
        
        /*this.fillWithBlocks(worldIn, structureBoundingBoxIn, 0, 4, 0, 9, 4, 6, iblockstate, iblockstate, false);
        this.fillWithBlocks(worldIn, structureBoundingBoxIn, 0, 5, 0, 9, 5, 6, Blocks.STONE_SLAB.getDefaultState(), Blocks.STONE_SLAB.getDefaultState(), false);
        this.fillWithBlocks(worldIn, structureBoundingBoxIn, 1, 5, 1, 8, 5, 5, Blocks.AIR.getDefaultState(), Blocks.AIR.getDefaultState(), false);
        this.fillWithBlocks(worldIn, structureBoundingBoxIn, 1, 1, 0, 2, 3, 0, iblockstate3, iblockstate3, false);
        this.fillWithBlocks(worldIn, structureBoundingBoxIn, 0, 1, 0, 0, 4, 0, iblockstate5, iblockstate5, false);
        this.fillWithBlocks(worldIn, structureBoundingBoxIn, 3, 1, 0, 3, 4, 0, iblockstate5, iblockstate5, false);
        this.fillWithBlocks(worldIn, structureBoundingBoxIn, 0, 1, 6, 0, 4, 6, iblockstate5, iblockstate5, false);
        this.setBlockState(worldIn, iblockstate3, 3, 3, 1, structureBoundingBoxIn);
        this.fillWithBlocks(worldIn, structureBoundingBoxIn, 3, 1, 2, 3, 3, 2, iblockstate3, iblockstate3, false);
        this.fillWithBlocks(worldIn, structureBoundingBoxIn, 4, 1, 3, 5, 3, 3, iblockstate3, iblockstate3, false);
        this.fillWithBlocks(worldIn, structureBoundingBoxIn, 0, 1, 1, 0, 3, 5, iblockstate3, iblockstate3, false);
        this.fillWithBlocks(worldIn, structureBoundingBoxIn, 1, 1, 6, 5, 3, 6, iblockstate3, iblockstate3, false);
        this.fillWithBlocks(worldIn, structureBoundingBoxIn, 5, 1, 0, 5, 3, 0, iblockstate6, iblockstate6, false);
        this.fillWithBlocks(worldIn, structureBoundingBoxIn, 9, 1, 0, 9, 3, 0, iblockstate6, iblockstate6, false);
        this.fillWithBlocks(worldIn, structureBoundingBoxIn, 6, 1, 4, 9, 4, 6, iblockstate, iblockstate, false);
        this.setBlockState(worldIn, Blocks.IRON_BARS.getDefaultState(), 9, 2, 5, structureBoundingBoxIn);
        this.setBlockState(worldIn, Blocks.IRON_BARS.getDefaultState(), 9, 2, 4, structureBoundingBoxIn);
        this.fillWithBlocks(worldIn, structureBoundingBoxIn, 7, 2, 4, 8, 2, 5, Blocks.AIR.getDefaultState(), Blocks.AIR.getDefaultState(), false);
        this.setBlockState(worldIn, iblockstate, 6, 1, 3, structureBoundingBoxIn);
        this.setBlockState(worldIn, Blocks.FURNACE.getDefaultState(), 6, 2, 3, structureBoundingBoxIn);
        this.setBlockState(worldIn, Blocks.FURNACE.getDefaultState(), 6, 3, 3, structureBoundingBoxIn);
        this.setBlockState(worldIn, Blocks.DOUBLE_STONE_SLAB.getDefaultState(), 8, 1, 1, structureBoundingBoxIn);
        this.setBlockState(worldIn, Blocks.glass_pane, 0, 2, 2, structureBoundingBoxIn);
        this.setBlockState(worldIn, Blocks.GLASS_PANE.getDefaultState(), 0, 2, 4, structureBoundingBoxIn);
        this.setBlockState(worldIn, Blocks.GLASS_PANE.getDefaultState(), 2, 2, 6, structureBoundingBoxIn);
        this.setBlockState(worldIn, Blocks.GLASS_PANE.getDefaultState(), 4, 2, 6, structureBoundingBoxIn);
        this.setBlockState(worldIn, iblockstate6, 2, 1, 4, structureBoundingBoxIn);
        this.setBlockState(worldIn, Blocks.WOODEN_PRESSURE_PLATE.getDefaultState(), 2, 2, 4, structureBoundingBoxIn);
        this.setBlockState(worldIn, iblockstate3, 1, 1, 5, structureBoundingBoxIn);
        this.setBlockState(worldIn, iblockstate1, 2, 1, 5, structureBoundingBoxIn);
        this.setBlockState(worldIn, iblockstate2, 1, 1, 4, structureBoundingBoxIn);*/

       /* if (!this.hasMadeChest && structureBoundingBoxIn.isVecInside(new BlockPos(this.getXWithOffset(5, 5), this.getYWithOffset(1), this.getZWithOffset(5, 5))))
        {
            this.hasMadeChest = true;
            this.generateChest(worldIn, structureBoundingBoxIn, randomIn, 5, 1, 5, LootTableList.CHESTS_VILLAGE_BLACKSMITH);
        }*/
        for (int k = 0; k < 10; ++k)
        {
            for (int j = 0; j < 13; ++j)
            {
                this.clearCurrentPositionBlocksUpwards(worldIn, j, 17, k, structureBoundingBoxIn);
                this.func_151554_b(worldIn, Blocks.double_stone_slab,0, j, -1, k, structureBoundingBoxIn);
            }
        }

        if(!this.haleSpawned){
	        EntitySaxtonHale shopkeeper=new EntitySaxtonHale(worldIn);
	        shopkeeper.setLocationAndAngles(this.getXWithOffset(7, 5), this.getYWithOffset(1), this.getZWithOffset(7, 5), 0.0F, 0.0F);
	        this.haleSpawned=true;
	        worldIn.spawnEntityInWorld(shopkeeper);
        }
        return true;
    }

    protected int getVillagerType(int currentVillagerProfession)
    {
        return 3;
    }

	
	public static class CreationHandler implements IVillageCreationHandler{
		@Override
		public PieceWeight getVillagePieceWeight(Random random, int i) {
			return new StructureVillagePieces.PieceWeight(MannCoBuilding.class, 15, 1);
		}

		@Override
		public Class<?> getComponentClass() {
			// TODO Auto-generated method stub
			return MannCoBuilding.class;
		}
		
		@Override
		public Object buildComponent(PieceWeight villagePiece, Start startPiece, List pieces, Random random, int p1,
				int p2, int p3, int p4, int p5) {
			StructureBoundingBox structureboundingbox = StructureBoundingBox.getComponentToAddBoundingBox(p1, p2, p3, 0, 0, 0, 13, 16, 10, p4);
	        return canVillageGoDeeper(structureboundingbox) && StructureComponent.findIntersecting(pieces, structureboundingbox) == null ? new MannCoBuilding(startPiece, p5, random, structureboundingbox, p4) : null;
		}
		
	}
}
