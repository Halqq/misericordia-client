package halq.misericordia.fun.executor.modules.combat.aimbot;

import halq.misericordia.fun.core.modulecore.Category;
import halq.misericordia.fun.core.modulecore.Module;
import halq.misericordia.fun.executor.settings.SettingBoolean;
import halq.misericordia.fun.executor.settings.SettingDouble;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBow;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

import java.util.ArrayList;
import java.util.List;

public class AimBot extends Module {

    SettingDouble range = create("Range", 10, 0.0, 30.0);
    SettingBoolean onlyBow = create("OnlyBow", true);
    public AimBot() {
        super("AimBot", Category.COMBAT);
    }

    EntityPlayer target;

    @Override
    public void onUpdate() {

        for (EntityPlayer player : mc.world.playerEntities) {
            if (player != mc.player) {
                target = player;
            }
        }

        if (target != null && mc.player.getDistance(target) <= range.getValue()){
            if(onlyBow.getValue()) {
                if (mc.player.getHeldItem(EnumHand.MAIN_HAND).getItem() instanceof ItemBow) {
                    mc.player.rotationYaw = getRotations(target)[0];
                    mc.player.rotationPitch = getRotations(target)[1];
                }
            } else {
                mc.player.rotationYaw = getRotations(target)[0];
                mc.player.rotationPitch = getRotations(target)[1];
            }
        }
    }

    public float[] getRotations(EntityPlayer target) {
        double x = target.posX - mc.player.posX;
        double y = target.posY + target.getEyeHeight() - (mc.player.posY + mc.player.getEyeHeight());
        double z = target.posZ - mc.player.posZ;
        double dist = Math.sqrt(x * x + z * z);
        float yaw = (float) Math.toDegrees(Math.atan2(z, x)) - 90.0f;
        float pitch = (float) (-Math.toDegrees(Math.atan2(y, dist)));
        return new float[]{yaw, pitch};
    }
}
