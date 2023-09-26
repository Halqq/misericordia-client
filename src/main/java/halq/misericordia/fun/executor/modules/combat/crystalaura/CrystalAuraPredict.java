package halq.misericordia.fun.executor.modules.combat.crystalaura;

import halq.misericordia.fun.executor.modules.combat.crystalaura.calcs.CrystalAuraCalcPos;
import halq.misericordia.fun.executor.modules.combat.crystalaura.module.CrystalAuraModule;
import halq.misericordia.fun.utils.Minecraftable;
import net.minecraft.network.play.client.CPacketPlayerDigging;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

/**
 * @author Halq
 * @since 11/06/2023 at 19:30
 */

public class CrystalAuraPredict implements Minecraftable {

    public static Vec3d predictCrystalPosition(CrystalAuraCalcPos.CalcPos calcPos) {
        if (calcPos != null && calcPos.getBlockPos() != null) {
            BlockPos crystalPos = calcPos.getBlockPos();
            double posX = crystalPos.getX() + 0.5;
            double posY = crystalPos.getY() + 1.0;
            double posZ = crystalPos.getZ() + 0.5;
            return new Vec3d(posX, posY, posZ);
        }
        return null;
    }

    public static void caAttackPredict() {

    }
}
