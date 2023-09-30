package halq.misericordia.fun.executor.modules.combat.holefiller.module;

import halq.misericordia.fun.core.modulecore.Category;
import halq.misericordia.fun.core.modulecore.Module;
import halq.misericordia.fun.executor.modules.combat.holefiller.HoleFillerCalcs;
import halq.misericordia.fun.executor.settings.SettingBoolean;
import halq.misericordia.fun.executor.settings.SettingDouble;
import jdk.nashorn.internal.ir.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.play.client.CPacketPlayerTryUseItemOnBlock;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;

import javax.swing.text.html.parser.Entity;

public class HoleFillerModule extends Module {

    public HoleFillerModule() {
        super("HoleFiller", Category.COMBAT);
    }


    SettingDouble range = create("Range", 4.5, 0, 6);
    SettingBoolean rotate = create("Rotate", true);
    SettingBoolean autoSwitch = create("AutoSwitch", true);
    EntityPlayer target;
    BlockPos pos;

    @Override
    public void onUpdate() {
        for(BlockPos holes : HoleFillerCalcs.getHoles()){
            if(mc.player.getDistance(holes.getX(), holes.getY(), holes.getZ()) <= range.getValue()){
                mc.player.connection.sendPacket(new CPacketPlayerTryUseItemOnBlock(holes, EnumFacing.UP, EnumHand.MAIN_HAND, 0, 0, 0));
            }
        }
    }
}
