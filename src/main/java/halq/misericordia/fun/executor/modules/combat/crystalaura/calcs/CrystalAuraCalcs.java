package halq.misericordia.fun.executor.modules.combat.crystalaura.calcs;

import halq.misericordia.fun.executor.modules.combat.crystalaura.module.CrystalAuraModule;
import halq.misericordia.fun.utils.Minecraftable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;

import java.util.ArrayList;
import java.util.List;

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
        CrystalAuraCalcPos.CalcPos posToReturn = new CrystalAuraCalcPos.CalcPos(BlockPos.ORIGIN, 0.5f);
        if(target != null){
        for (BlockPos pos : getSphere((new BlockPos(Math.floor(mc.player.posX), Math.floor(mc.player.posY), Math.floor(mc.player.posZ))), CrystalAuraModule.INSTANCE.range.getValue().intValue(), (int) CrystalAuraModule.INSTANCE.range.getValue().intValue(), false, true, 0)) {
            float targetDamage = CrystalAuraCalcDamage.calculateDamage(mc.world, pos.getX() + 0.5, pos.getY() + 1, pos.getZ() + 0.5, target, true);
            float selfDamage = CrystalAuraCalcDamage.calculateDamage(mc.world, pos.getX() + 0.5, pos.getY() + 1, pos.getZ() + 0.5, mc.player, true);
            if (canPlaceCrystal(pos, true)) {
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
                if (mc.world.getBlockState(blockPos).getBlock() != Blocks.BEDROCK && mc.world.getBlockState(blockPos).getBlock() != Blocks.OBSIDIAN) {
                    return false;
                }
                if (mc.world.getBlockState(boost).getBlock() != Blocks.AIR) {
                    return false;
                }
                if (!specialEntityCheck) {
                    return mc.world.getEntitiesWithinAABB(Entity.class, new AxisAlignedBB(boost)).isEmpty();
                }
                for (Entity entity : mc.world.getEntitiesWithinAABB(Entity.class, new AxisAlignedBB(boost))) {
                    if (entity instanceof EntityEnderCrystal) continue;
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
}
