package rafradek.TF2weapons.weapons;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;
import net.minecraft.world.ChunkPosition;
import net.minecraft.world.Explosion;
import net.minecraft.world.World;
import rafradek.TF2weapons.TF2weapons;
import rafradek.TF2weapons.message.TF2Message;
import rafradek.TF2weapons.projectiles.EntityProjectileBase;

public class TF2Explosion extends Explosion{

    private Random explosionRNG = new Random();
    public World worldObj;
    /** A list of ChunkPositions of blocks affected by this explosion */
    public List<ChunkPosition> affectedBlockPositions = new ArrayList<ChunkPosition>();
    public HashMap<Entity,Float> affectedEntities = new HashMap<Entity,Float>();
	private Entity directHit;
	/** whether or not the explosion sets fire to blocks around it */
    public boolean isFlaming;
    /** whether or not this explosion spawns smoke particles */
    public boolean isSmoking;
    private final double explosionX;
    private final double explosionY;
    private final double explosionZ;
    private final Entity exploder;
    private final float explosionSize;
    private final Map<EntityPlayer, Vec3> field_77288_k;
    private final Vec3 position;
    public TF2Explosion(World world, Entity exploder,
			double x, double y, double z,
			float size,  Entity direct) {
    	super(world,exploder,x,y,z,size);
    	this.explosionRNG = new Random();
        this.affectedBlockPositions = Lists.newArrayList();
        this.field_77288_k = Maps.newHashMap();
        this.exploder = exploder;
        this.explosionSize = size;
        this.explosionX = x;
        this.explosionY = y;
        this.explosionZ = z;
        this.position = Vec3.createVectorHelper(explosionX, explosionY, explosionZ);
    	this.worldObj=world;
    	this.directHit=direct;
	}

    
	@Override
	public void doExplosionA()
    {
		HashSet<ChunkPosition> hashset = Sets.newHashSet();
        int j;
        int k;
        if(TF2weapons.destTerrain){
        	for (int i = 0; i < 16; ++i)
            {
                for (j = 0; j < 16; ++j)
                {
                    for (k = 0; k < 16; ++k)
                    {
                        if (i == 0 || i == 15 || j == 0 || j == 15 || k == 0 || k == 15)
                        {
                            double d0 = i / 15.0F * 2.0F - 1.0F;
                            double d1 = j / 15.0F * 2.0F - 1.0F;
                            double d2 = k / 15.0F * 2.0F - 1.0F;
                            double d3 = Math.sqrt(d0 * d0 + d1 * d1 + d2 * d2);
                            d0 /= d3;
                            d1 /= d3;
                            d2 /= d3;
                            float f = this.explosionSize * (0.7F + this.worldObj.rand.nextFloat() * 0.6F);
                            double d4 = this.explosionX;
                            double d6 = this.explosionY;
                            double d8 = this.explosionZ;

                            for (float f2 = 0.3F; f > 0.0F; f -= f2 * 0.75F)
                            {
                                int j1 = MathHelper.floor_double(d4);
                                int k1 = MathHelper.floor_double(d6);
                                int l1 = MathHelper.floor_double(d8);
                                Block block = this.worldObj.getBlock(j1, k1, l1);

                                if (block.getMaterial() != Material.air)
                                {
                                    float f3 = this.exploder != null ? this.exploder.func_145772_a(this, this.worldObj, j1, k1, l1, block) : block.getExplosionResistance(this.exploder, worldObj, j1, k1, l1, explosionX, explosionY, explosionZ);
                                    f -= (f3 + 0.3F) * f2;
                                }

                                if (f > 0.0F && (this.exploder == null || this.exploder.func_145774_a(this, this.worldObj, j1, k1, l1, block, f)))
                                {
                                    hashset.add(new ChunkPosition(j1, k1, l1));
                                }

                                d4 += d0 * f2;
                                d6 += d1 * f2;
                                d8 += d2 * f2;
                            }
                        }
                    }
                }
            }
        }
        this.affectedBlockPositions.addAll(hashset);
        float f3 = this.explosionSize * 2.0F;
        j = MathHelper.floor_double(this.explosionX - f3 - 1.0D);
        k = MathHelper.floor_double(this.explosionX + f3 + 1.0D);
        int j1 = MathHelper.floor_double(this.explosionY - f3 - 1.0D);
        int l = MathHelper.floor_double(this.explosionY + f3 + 1.0D);
        int k1 = MathHelper.floor_double(this.explosionZ - f3 - 1.0D);
        int i1 = MathHelper.floor_double(this.explosionZ + f3 + 1.0D);
        List<Entity> list = this.worldObj.getEntitiesWithinAABBExcludingEntity(this.exploder, AxisAlignedBB.getBoundingBox(j, j1, k1, k, l, i1));
        net.minecraftforge.event.ForgeEventFactory.onExplosionDetonate(this.worldObj, this, list, f3);
        Vec3 vec3 = Vec3.createVectorHelper(this.explosionX, this.explosionY, this.explosionZ);
        int livingEntities=0;
        for (Object obj:list){
        	if(obj instanceof EntityLivingBase){
        		livingEntities++;
        	}
        }
        for (int l1 = 0; l1 < list.size(); ++l1)
        {
            Entity entity = list.get(l1);
            if ((entity instanceof EntityLivingBase || TF2weapons.destTerrain)&&TF2weapons.canHit(this.getExplosivePlacedBy(), entity))
            {
	            double d4 = entity.getDistance(this.explosionX, this.explosionY, this.explosionZ) / this.explosionSize*0.5;
	
	            if (d4 <= 0.5D)
	            {
	            	boolean isExploder=this.getExplosivePlacedBy()==entity;
	            	boolean expJump=isExploder&&!entity.onGround&&livingEntities==1;
	            	//System.out.println("jump: "+expJump);
	                double d5 = entity.posX - this.explosionX;
	                double d6 = entity.posY + (isExploder?entity.getEyeHeight():(entity.boundingBox.maxY-entity.boundingBox.minY)/2)- this.explosionY;
	                double d7 = entity.posZ - this.explosionZ;
	                double d9 = MathHelper.sqrt_double(d5 * d5 + d6 * d6 + d7 * d7);
	
	                if (d9 != 0.0D)
	                {
	                    d5 /= d9;
	                    d6 /= d9;
	                    d7 /= d9;
	                    //float explMod=(this.explosionSize/4);
	                    d5 *= 1.55;
	                    d6 *= 1.55;
	                    d7 *= 1.55;
	                    double d10 = this.getBlockDensity(vec3, entity.boundingBox.expand(-0.05f, -0.05f, -0.05f));
	                    double d11 = (1D - d4) * d10;
	                    //System.out.println("multiplier: "+d11+" damage: "+this.dmg);
	                    if(entity==this.directHit){
	                    	d11=1;
	                    }
	                    if(d11==0){
	                    	continue;
	                    }
	                    if(expJump){
	                    	entity.fallDistance-=d6*8-1;
	                    	d5*=1.5;
	                    	d7*=1.5;
	                    }
	                    else{
	                    	entity.fallDistance-=d6*3-1;
	                    }
	                    this.affectedEntities.put(entity, (float) (d11/(expJump?2:1)));
	                    
	                    //double d8 = EnchantmentProtection.getBlastDamageReduction(entity, d11);
	                    //System.out.println("d: "+ d5 * d8+" "+d6 * d8+" "+d7 * d8);
	                    entity.addVelocity(d5, d6, d7);
	                   // entity.addVelocity(2, 2, 2);
	                    if (entity instanceof EntityPlayerMP)
	                    {
	                    	TF2weapons.network.sendTo(new TF2Message.PropertyMessage("ExpJump",true,entity), (EntityPlayerMP) entity);
	                    	entity.getEntityData().setBoolean("ExpJump", true);
	                        this.func_77277_b().put((EntityPlayer) entity, net.minecraft.util.Vec3.createVectorHelper(d5 * d11, d6 * d11, d7 * d11));
	                        //entity.getDataWatcher().updateObject(29, Byte.valueOf((byte) 1));
	                    }
	                }
	            }
            }
        }
    }

    /**
     * Does the second part of the explosion (sound, particles, drop spawn)
     */
	 @Override
	public void doExplosionB(boolean spawnParticles)
	    {
		 this.worldObj.playSoundEffect(this.explosionX, this.explosionY, this.explosionZ, "random.explode", 4.0F, (1.0F + (this.worldObj.rand.nextFloat() - this.worldObj.rand.nextFloat()) * 0.2F) * 0.7F);

	        if (this.explosionSize >= 2.0F && this.isSmoking)
	        {
	            this.worldObj.spawnParticle("hugeexplosion", this.explosionX, this.explosionY, this.explosionZ, 1.0D, 0.0D, 0.0D);
	        }
	        else
	        {
	            this.worldObj.spawnParticle("largeexplode", this.explosionX, this.explosionY, this.explosionZ, 1.0D, 0.0D, 0.0D);
	        }


	        if (this.isSmoking && TF2weapons.destTerrain)
	        {
	        	for (ChunkPosition chunkposition : this.affectedBlockPositions)
	            {
	        		int i = chunkposition.chunkPosX;
	                int j = chunkposition.chunkPosY;
	                int k = chunkposition.chunkPosZ;
	                Block block = this.worldObj.getBlock(i, j, k);

	                if (spawnParticles)
	                {
	                    double d0 = i + this.worldObj.rand.nextFloat();
	                    double d1 = j + this.worldObj.rand.nextFloat();
	                    double d2 = k + this.worldObj.rand.nextFloat();
	                    double d3 = d0 - this.explosionX;
	                    double d4 = d1 - this.explosionY;
	                    double d5 = d2 - this.explosionZ;
	                    double d6 = MathHelper.sqrt_double(d3 * d3 + d4 * d4 + d5 * d5);
	                    d3 = d3 / d6;
	                    d4 = d4 / d6;
	                    d5 = d5 / d6;
	                    double d7 = 0.5D / (d6 / this.explosionSize + 0.1D);
	                    d7 = d7 * (this.worldObj.rand.nextFloat() * this.worldObj.rand.nextFloat() + 0.3F);
	                    d3 = d3 * d7;
	                    d4 = d4 * d7;
	                    d5 = d5 * d7;
	                    this.worldObj.spawnParticle("explode", (d0 + this.explosionX) / 2.0D, (d1 + this.explosionY) / 2.0D, (d2 + this.explosionZ) / 2.0D, d3, d4, d5);
	                    this.worldObj.spawnParticle("smoke", d0, d1, d2, d3, d4, d5);
	                }

	                if (block.getMaterial() != Material.air)
	                {
	                    if (block.canDropFromExplosion(this))
	                    {
	                        block.dropBlockAsItemWithChance(this.worldObj, i,j,k, this.worldObj.getBlockMetadata(i, j, k), 1.0F / this.explosionSize, 0);
	                    }

	                    block.onBlockExploded(this.worldObj, i,j,k, this);
	                }
	            }
	        }

	        if (this.isFlaming)
	        {
	            for (ChunkPosition chunkposition : this.affectedBlockPositions)
	            {
	                int i = chunkposition.chunkPosX;
	                int j = chunkposition.chunkPosY;
	                int k = chunkposition.chunkPosZ;
	                Block block = this.worldObj.getBlock(i, j, k);
	                Block block1 = this.worldObj.getBlock(i, j - 1, k);

	                if (block.getMaterial() == Material.air && block1.func_149730_j() && this.explosionRNG.nextInt(3) == 0)
	                {
	                    this.worldObj.setBlock(i, j, k, Blocks.fire);
	                }
	            }
	        }
	    }
    /*public float getBlockDensity(Vec3 p_72842_1_, AxisAlignedBB p_72842_2_)
    {
        double d0 = 1.0D / ((p_72842_2_.maxX - p_72842_2_.minX) * 2.0D + 1.0D);
        double d1 = 1.0D / ((p_72842_2_.maxY - p_72842_2_.minY) * 2.0D + 1.0D);
        double d2 = 1.0D / ((p_72842_2_.maxZ - p_72842_2_.minZ) * 2.0D + 1.0D);

        if (d0 >= 0.0D && d1 >= 0.0D && d2 >= 0.0D)
        {
            int i = 0;
            int j = 0;

            for (float f = 0.0F; f <= 1.0F; f = (float)(f + d0))
            {
                for (float f1 = 0.0F; f1 <= 1.0F; f1 = (float)(f1 + d1))
                {
                    for (float f2 = 0.0F; f2 <= 1.0F; f2 = (float)(f2 + d2))
                    {
                        double d3 = p_72842_2_.minX + (p_72842_2_.maxX - p_72842_2_.minX) * f;
                        double d4 = p_72842_2_.minY + (p_72842_2_.maxY - p_72842_2_.minY) * f1;
                        double d5 = p_72842_2_.minZ + (p_72842_2_.maxZ - p_72842_2_.minZ) * f2;
                        MovingObjectPosition mop=this.worldObj.rayTraceBlocks(Vec3.createVectorHelper(d3, d4, d5), p_72842_1_, false, true, false);
                        IBlockState state=this.worldObj.getBlockState(mop.getBlockPos());
                        if (mop == null||state.getBlock()==Blocks.SNOW_LAYER)
                        {
                            ++i;
                        }

                        ++j;
                    }
                }
            }

            return (float)i / (float)j;
        }
        else
        {
            return 0.0F;
        }
    }*/
    public float getBlockDensity(Vec3 origin, AxisAlignedBB p_72842_2_)
    {
        double d0 = 1.0D / ((p_72842_2_.maxX - p_72842_2_.minX) * 2.0D + 1.0D);
        double d1 = 1.0D / ((p_72842_2_.maxY - p_72842_2_.minY) * 2.0D + 1.0D);
        double d2 = 1.0D / ((p_72842_2_.maxZ - p_72842_2_.minZ) * 2.0D + 1.0D);
        List<AxisAlignedBB> collisionBoxes=this.worldObj.getCollidingBoundingBoxes(exploder, p_72842_2_.expand(1, 1, 1));
        if (d0 >= 0.0D && d1 >= 0.0D && d2 >= 0.0D)
        {
            int i = 0;
            int j = 0;

            for (float f = 0.0F; f <= 1.0F; f = (float)(f + d0))
            {
                for (float f1 = 0.0F; f1 <= 1.0F; f1 = (float)(f1 + d1))
                {
                    for (float f2 = 0.0F; f2 <= 1.0F; f2 = (float)(f2 + d2))
                    {
                        double d3 = p_72842_2_.minX + (p_72842_2_.maxX - p_72842_2_.minX) * f;
                        double d4 = p_72842_2_.minY + (p_72842_2_.maxY - p_72842_2_.minY) * f1;
                        double d5 = p_72842_2_.minZ + (p_72842_2_.maxZ - p_72842_2_.minZ) * f2;
                        Vec3 start=Vec3.createVectorHelper(d3, d4, d5);
                        boolean free=true;
                        for(AxisAlignedBB box:collisionBoxes){
                        	if(box.calculateIntercept(start,origin)!=null){
                        		free=false;
                        		break;
                        	}
                        }
                        //MovingObjectPosition mop=//this.worldObj.getCo(Vec3.createVectorHelper(d3, d4, d5), p_72842_1_, false, true, false);
                        if (free)
                        {
                            ++i;
                        }

                        ++j;
                    }
                }
            }
            //System.out.println("Explosion power: "+((float)i/j));
            return (float)i / (float)j;
        }
        else
        {
            return 0.0F;
        }
    }
    @Override
	public Map<EntityPlayer, Vec3> func_77277_b()
    {
        return this.field_77288_k;
    }

    
    /**
     * Returns either the entity that placed the explosive block, the entity that caused the explosion or null.
     */
    @Override
	public EntityLivingBase getExplosivePlacedBy()
    {
        return ((EntityProjectileBase)this.exploder).shootingEntity;
    }

    public void func_180342_d()
    {
        this.affectedBlockPositions.clear();
    }

    public List<ChunkPosition> func_180343_e()
    {
        return this.affectedBlockPositions;
    }

    public Vec3 getPosition(){ return this.position; }
}