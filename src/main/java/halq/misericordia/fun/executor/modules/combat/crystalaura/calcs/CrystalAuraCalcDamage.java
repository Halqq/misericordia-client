package halq.misericordia.fun.executor.modules.combat.crystalaura.calcs;

import halq.misericordia.fun.utils.Minecraftable;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.potion.Potion;
import net.minecraft.util.CombatRules;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.*;
import net.minecraft.world.Explosion;
import net.minecraft.world.World;

import java.util.Arrays;
import java.util.List;

/**
 * @author Halq
 * @since 11/06/2023 at 15:38
 */

public class CrystalAuraCalcDamage implements Minecraftable {
    public static List<Block> valid = Arrays.asList(Blocks.OBSIDIAN, Blocks.BEDROCK, Blocks.ENDER_CHEST, Blocks.ANVIL);

    public static float calculateDamage(World world, double posX, double posY, double posZ, Entity entity, boolean terrain) {
        return calculateDamage(posX, posY, posZ, entity);
    }

    public static float calculateDamage(double posX, double posY, double posZ, Entity entity) {
        float doubleExplosionSize = 12.0f;
        double distancedsize = entity.getDistance(posX, posY, posZ) / 12.0;
        Vec3d vec3d = new Vec3d(posX, posY, posZ);
        double blockDensity = 0.0;
        try {
            blockDensity = entity.world.getBlockDensity(vec3d, entity.getEntityBoundingBox());
        } catch (Exception exception) {
            // empty catch block
        }
        double v = (1.0 - distancedsize) * blockDensity;
        float damage = (int) ((v * v + v) / 2.0 * 7.0 * 12.0 + 1.0);
        double finald = 1.0;
        if (entity instanceof EntityLivingBase) {
            finald = getBlastReduction((EntityLivingBase) entity, getDamageMultiplied(damage), new Explosion(mc.world, null, posX, posY, posZ, 6.0f, false, true));
        }
        return (float) finald;
    }


    public static double getDistance(double p_X, double p_Y, double p_Z, double x, double y, double z) {
        double d0 = p_X - x;
        double d1 = p_Y - y;
        double d2 = p_Z - z;
        return MathHelper.sqrt(d0 * d0 + d1 * d1 + d2 * d2);
    }

    public static Vec3d getInterpolatedAmount(Entity entity, double ticks) {
        return getInterpolatedAmount(entity, ticks, ticks, ticks);
    }

    public static Vec3d getInterpolatedAmount(Entity entity, double x, double y, double z) {
        return new Vec3d(
                (entity.posX - entity.lastTickPosX) * x,
                (entity.posY - entity.lastTickPosY) * y,
                (entity.posZ - entity.lastTickPosZ) * z
        );
    }


    public static float getBlockDensity(final Vec3d vec, final AxisAlignedBB bb) {
        final double d0 = 1.0 / ((bb.maxX - bb.minX) * 2.0 + 1.0);
        final double d2 = 1.0 / ((bb.maxY - bb.minY) * 2.0 + 1.0);
        final double d3 = 1.0 / ((bb.maxZ - bb.minZ) * 2.0 + 1.0);
        final double d4 = (1.0 - Math.floor(1.0 / d0) * d0) / 2.0;
        final double d5 = (1.0 - Math.floor(1.0 / d3) * d3) / 2.0;
        float j2 = 0.0f;
        float k2 = 0.0f;
        for (float f = 0.0f; f <= 1.0f; f += (float) d0) {
            for (float f2 = 0.0f; f2 <= 1.0f; f2 += (float) d2) {
                for (float f3 = 0.0f; f3 <= 1.0f; f3 += (float) d3) {
                    final double d6 = bb.minX + (bb.maxX - bb.minX) * f;
                    final double d7 = bb.minY + (bb.maxY - bb.minY) * f2;
                    final double d8 = bb.minZ + (bb.maxZ - bb.minZ) * f3;
                    if (rayTraceBlocks(new Vec3d(d6 + d4, d7, d8 + d5), vec, false, true, false) == null) {
                        ++j2;
                    }
                    ++k2;
                }
            }
        }
        return j2 / k2;
    }

    public static RayTraceResult rayTraceBlocks(Vec3d vec31, Vec3d vec32, boolean stopOnLiquid, boolean ignoreBlockWithoutBoundingBox, boolean returnLastUncollidableBlock) {
        if (!Double.isNaN(vec31.x) && !Double.isNaN(vec31.y) && !Double.isNaN(vec31.z)) {
            if (!Double.isNaN(vec32.x) && !Double.isNaN(vec32.y) && !Double.isNaN(vec32.z)) {
                int i = MathHelper.floor(vec32.x);
                int j = MathHelper.floor(vec32.y);
                int k = MathHelper.floor(vec32.z);
                int l = MathHelper.floor(vec31.x);
                int i1 = MathHelper.floor(vec31.y);
                int j1 = MathHelper.floor(vec31.z);
                BlockPos blockpos = new BlockPos(l, i1, j1);
                IBlockState iblockstate = mc.world.getBlockState(blockpos);
                Block block = iblockstate.getBlock();

                if (!valid.contains(block)) {
                    block = Blocks.AIR;
                    iblockstate = Blocks.AIR.getBlockState().getBaseState();
                }

                if ((!ignoreBlockWithoutBoundingBox || iblockstate.getCollisionBoundingBox(mc.world, blockpos) != Block.NULL_AABB) && block.canCollideCheck(iblockstate, stopOnLiquid)) {
                    RayTraceResult raytraceresult = iblockstate.collisionRayTrace(mc.world, blockpos, vec31, vec32);

                    if (raytraceresult != null) {
                        return raytraceresult;
                    }
                }

                RayTraceResult raytraceresult2 = null;
                int k1 = 200;

                while (k1-- >= 0) {
                    if (Double.isNaN(vec31.x) || Double.isNaN(vec31.y) || Double.isNaN(vec31.z)) {
                        return null;
                    }

                    if (l == i && i1 == j && j1 == k) {
                        return returnLastUncollidableBlock ? raytraceresult2 : null;
                    }

                    boolean flag2 = true;
                    boolean flag = true;
                    boolean flag1 = true;
                    double d0 = 999.0D;
                    double d1 = 999.0D;
                    double d2 = 999.0D;

                    if (i > l) {
                        d0 = (double) l + 1.0D;
                    } else if (i < l) {
                        d0 = (double) l + 0.0D;
                    } else {
                        flag2 = false;
                    }

                    if (j > i1) {
                        d1 = (double) i1 + 1.0D;
                    } else if (j < i1) {
                        d1 = (double) i1 + 0.0D;
                    } else {
                        flag = false;
                    }

                    if (k > j1) {
                        d2 = (double) j1 + 1.0D;
                    } else if (k < j1) {
                        d2 = (double) j1 + 0.0D;
                    } else {
                        flag1 = false;
                    }

                    double d3 = 999.0D;
                    double d4 = 999.0D;
                    double d5 = 999.0D;
                    double d6 = vec32.x - vec31.x;
                    double d7 = vec32.y - vec31.y;
                    double d8 = vec32.z - vec31.z;

                    if (flag2) {
                        d3 = (d0 - vec31.x) / d6;
                    }

                    if (flag) {
                        d4 = (d1 - vec31.y) / d7;
                    }

                    if (flag1) {
                        d5 = (d2 - vec31.z) / d8;
                    }

                    if (d3 == -0.0D) {
                        d3 = -1.0E-4D;
                    }

                    if (d4 == -0.0D) {
                        d4 = -1.0E-4D;
                    }

                    if (d5 == -0.0D) {
                        d5 = -1.0E-4D;
                    }

                    EnumFacing enumfacing;

                    if (d3 < d4 && d3 < d5) {
                        enumfacing = i > l ? EnumFacing.WEST : EnumFacing.EAST;
                        vec31 = new Vec3d(d0, vec31.y + d7 * d3, vec31.z + d8 * d3);
                    } else if (d4 < d5) {
                        enumfacing = j > i1 ? EnumFacing.DOWN : EnumFacing.UP;
                        vec31 = new Vec3d(vec31.x + d6 * d4, d1, vec31.z + d8 * d4);
                    } else {
                        enumfacing = k > j1 ? EnumFacing.NORTH : EnumFacing.SOUTH;
                        vec31 = new Vec3d(vec31.x + d6 * d5, vec31.y + d7 * d5, d2);
                    }

                    l = MathHelper.floor(vec31.x) - (enumfacing == EnumFacing.EAST ? 1 : 0);
                    i1 = MathHelper.floor(vec31.y) - (enumfacing == EnumFacing.UP ? 1 : 0);
                    j1 = MathHelper.floor(vec31.z) - (enumfacing == EnumFacing.SOUTH ? 1 : 0);
                    blockpos = new BlockPos(l, i1, j1);
                    IBlockState iblockstate1 = mc.world.getBlockState(blockpos);
                    Block block1 = iblockstate1.getBlock();

                    if (!valid.contains(block1)) {
                        block1 = Blocks.AIR;
                        iblockstate1 = Blocks.AIR.getBlockState().getBaseState();
                    }

                    if (!ignoreBlockWithoutBoundingBox || iblockstate1.getMaterial() == Material.PORTAL || iblockstate1.getCollisionBoundingBox(mc.world, blockpos) != Block.NULL_AABB) {
                        if (block1.canCollideCheck(iblockstate1, stopOnLiquid)) {

                            return iblockstate1.collisionRayTrace(mc.world, blockpos, vec31, vec32);
                        } else {
                            raytraceresult2 = new RayTraceResult(RayTraceResult.Type.MISS, vec31, enumfacing, blockpos);
                        }
                    }
                }

                return returnLastUncollidableBlock ? raytraceresult2 : null;
            } else {
                return null;
            }
        } else {
            return null;
        }
    }

    public static float getBlastReduction(EntityLivingBase entity, float damage, Explosion explosion) {
        if (entity instanceof EntityPlayer) {
            EntityPlayer ep = (EntityPlayer) entity;
            DamageSource ds = DamageSource.causeExplosionDamage(explosion);
            damage = CombatRules.getDamageAfterAbsorb(damage, (float) ep.getTotalArmorValue(),
                    (float) ep.getEntityAttribute(SharedMonsterAttributes.ARMOR_TOUGHNESS).getAttributeValue());

            int k = EnchantmentHelper.getEnchantmentModifierDamage(ep.getArmorInventoryList(), ds);
            float f = MathHelper.clamp(k, 0.0F, 20.0F);
            damage *= 1.0F - f / 25.0F;

            if (entity.isPotionActive(Potion.getPotionById(11))) {
                damage -= damage / 4;
            }
            return damage;
        }

        damage = CombatRules.getDamageAfterAbsorb(damage, (float) entity.getTotalArmorValue(),
                (float) entity.getEntityAttribute(SharedMonsterAttributes.ARMOR_TOUGHNESS).getAttributeValue());
        return damage;
    }

    private static float getDamageMultiplied(float damage) {
        int diff = mc.world.getDifficulty().getId();
        return damage * (diff == 0 ? 0.0f : (diff == 2 ? 1.0f : (diff == 1 ? 0.5f : 1.5f)));
    }
}
