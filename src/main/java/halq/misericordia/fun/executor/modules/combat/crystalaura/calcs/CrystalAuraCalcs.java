package halq.misericordia.fun.executor.modules.combat.crystalaura.calcs;

import halq.misericordia.fun.executor.modules.combat.crystalaura.module.CrystalAuraModule;
import halq.misericordia.fun.utils.Minecraftable;
import net.minecraft.block.state.IBlockState;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.potion.Potion;
import net.minecraft.util.CombatRules;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.Explosion;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Halq
 * @since 10/06/2023 at 04:23
 */

public class CrystalAuraCalcs implements Minecraftable {

    public static List<BlockPos> getSphere(BlockPos loc, float r, int h, boolean hollow, boolean sphere, int plus_y) {
        ArrayList<BlockPos> circleblocks = new ArrayList<BlockPos>();
        int cx = loc.getX();
        int cy = loc.getY();
        int cz = loc.getZ();
        int x = cx - (int) r;
        while ((float) x <= (float) cx + r) {
            int z = cz - (int) r;
            while ((float) z <= (float) cz + r) {
                int y = sphere ? cy - (int) r : cy;
                while (true) {
                    float f;
                    float f2 = f = sphere ? (float) cy + r : (float) (cy + h);
                    if (!((float) y < f)) break;
                    double dist = (cx - x) * (cx - x) + (cz - z) * (cz - z) + (sphere ? (cy - y) * (cy - y) : 0);
                    if (!(!(dist < (double) (r * r)) || hollow && dist < (double) ((r - 1.0f) * (r - 1.0f)))) {
                        BlockPos l = new BlockPos(x, y + plus_y, z);
                        circleblocks.add(l);
                    }
                    ++y;
                }
                ++z;
            }
            ++x;
        }
        return circleblocks;
    }

    public static CrystalAuraCalcPos.CalcPos calculatePositions(EntityPlayer target) {
        CrystalAuraModule crystalAuraModule = CrystalAuraModule.INSTANCE;
        double playerRangeValue = crystalAuraModule.playerRange.getValue();
        double placeRangeValue = crystalAuraModule.placeRange.getValue();
        float maxDamage = 0.0f;
        CrystalAuraCalcPos.CalcPos posToReturn = new CrystalAuraCalcPos.CalcPos(BlockPos.ORIGIN, 0.5f);

        if (target != null && mc.player.getDistance(target) <= playerRangeValue) {
            double playerPosX = mc.player.posX;
            double playerPosY = mc.player.posY;
            double playerPosZ = mc.player.posZ;
            BlockPos playerPos = new BlockPos((int) playerPosX, (int) playerPosY, (int) playerPosZ);
            int placeRangeValueInt = (int) placeRangeValue;
            List<BlockPos> spherePositions = getSphere(playerPos, placeRangeValueInt, placeRangeValueInt, false, true, 0);

            double placeRangeSquare = square(placeRangeValue);

            for (BlockPos pos : spherePositions) {
                double posXPlusHalf = pos.getX() + 0.5;
                double posYPlusOne = pos.getY() + 1.0;
                double posZPlusHalf = pos.getZ() + 0.5;

                if (mc.player.getDistanceSq(posXPlusHalf, posYPlusOne, posZPlusHalf) > placeRangeSquare) {
                    continue;
                }

                if (!canPlaceCrystal(pos, true)) {
                    continue;
                }

                float targetDamage = calculateDamage(mc.world, posXPlusHalf, posYPlusOne, posZPlusHalf, target, true);
                float selfDamage = calculateDamage(mc.world, posXPlusHalf, posYPlusOne, posZPlusHalf, mc.player, true);

                if (selfDamage <= crystalAuraModule.maxDmg.getValue() && targetDamage > maxDamage) {
                    maxDamage = targetDamage;
                    posToReturn = new CrystalAuraCalcPos.CalcPos(pos, targetDamage);
                }
            }
        }

        return posToReturn;
    }

    private static boolean canPlaceCrystal(BlockPos blockPos, boolean specialEntityCheck) {
        BlockPos boost = blockPos.add(0, 1, 0);
        BlockPos boost2 = blockPos.add(0, 2, 0);

        try {
            IBlockState blockState = mc.world.getBlockState(blockPos);
            IBlockState boostBlockState = mc.world.getBlockState(boost);
            IBlockState boost2BlockState = mc.world.getBlockState(boost2);

            if (blockState.getBlock() != Blocks.BEDROCK && blockState.getBlock() != Blocks.OBSIDIAN) {
                return false;
            }

            if (boostBlockState.getBlock() != Blocks.AIR || boost2BlockState.getBlock() != Blocks.AIR) {
                return false;
            }

            List<Entity> entitiesAtBoost = mc.world.getEntitiesWithinAABB(Entity.class, new AxisAlignedBB(boost));
            List<Entity> entitiesAtBoost2 = mc.world.getEntitiesWithinAABB(Entity.class, new AxisAlignedBB(boost2));

            if (!specialEntityCheck) {
                return entitiesAtBoost.isEmpty() && entitiesAtBoost2.isEmpty();
            }

            for (Entity entity : entitiesAtBoost) {
                if (entity instanceof EntityEnderCrystal) {
                    continue;
                }
                return false;
            }

            for (Entity entity : entitiesAtBoost2) {
                if (entity instanceof EntityEnderCrystal) {
                    continue;
                }
                return false;
            }
        } catch (Exception ignored) {
            return false;
        }

        return true;
    }

    public static double square(double input) {
        return input * input;
    }

    public static float calculateDamage(World world, double posX, double posY, double posZ, Entity entity, boolean terrain) {
        return calculateDamage(posX, posY, posZ, entity);
    }

    public static float calculateDamage(double posX, double posY, double posZ, Entity entity) {
        float doubleExplosionSize = 12.0f;
        double distance = entity.getDistance(posX, posY, posZ);
        double distancedSize = distance / doubleExplosionSize;
        Vec3d vec3d = new Vec3d(posX, posY, posZ);
        double blockDensity = 0.0;

        try {
            blockDensity = entity.world.getBlockDensity(vec3d, entity.getEntityBoundingBox());
        } catch (Exception ex) {
            // Log the exception if needed
        }

        double v = (1.0 - distancedSize) * blockDensity;
        float damage = (float) ((v * v + v) / 2.0 * 7.0 * doubleExplosionSize + 1.0);
        double finald = 1.0;

        if (entity instanceof EntityLivingBase) {
            float damageMultiplied = getDamageMultiplied(damage);
            Explosion explosion = new Explosion(mc.world, null, posX, posY, posZ, 6.0f, false, true);
            finald = getBlastReduction((EntityLivingBase) entity, damageMultiplied, explosion);
        }

        return (float) finald;
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
