package halq.misericordia.fun.executor.modules.combat.holefiller;

import halq.misericordia.fun.utils.Minecraftable;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;

import java.util.ArrayList;
import java.util.List;

public class HoleFillerCalcs implements Minecraftable {

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

    public static List<BlockPos> getHoles() {
        ArrayList<BlockPos> holes = new ArrayList<BlockPos>();
        for (BlockPos pos : getSphere(mc.player.getPosition(), 5, 5, false, true, 0)) {
            if (!IsHole(pos)) continue;
            holes.add(pos);
        }
        return holes;
    }

    private static boolean IsHole(BlockPos blockPos) {
        BlockPos boost = blockPos.add(0, 1, 0);
        BlockPos boost2 = blockPos.add(0, 0, 0);
        BlockPos boost3 = blockPos.add(0, 0, -1);
        BlockPos boost4 = blockPos.add(1, 0, 0);
        BlockPos boost5 = blockPos.add(-1, 0, 0);
        BlockPos boost6 = blockPos.add(0, 0, 1);
        BlockPos boost7 = blockPos.add(0, 2, 0);
        BlockPos boost8 = blockPos.add(0.5, 0.5, 0.5);
        BlockPos boost9 = blockPos.add(0, -1, 0);
        return mc.world.getBlockState(boost).getBlock() == Blocks.AIR
                && (mc.world.getBlockState(boost2).getBlock() == Blocks.AIR)
                && (mc.world.getBlockState(boost7).getBlock() == Blocks.AIR)
                && ((mc.world.getBlockState(boost3).getBlock() == Blocks.OBSIDIAN) || (mc.world.getBlockState(boost3).getBlock() == Blocks.BEDROCK))
                && ((mc.world.getBlockState(boost4).getBlock() == Blocks.OBSIDIAN) || (mc.world.getBlockState(boost4).getBlock() == Blocks.BEDROCK))
                && ((mc.world.getBlockState(boost5).getBlock() == Blocks.OBSIDIAN) || (mc.world.getBlockState(boost5).getBlock() == Blocks.BEDROCK))
                && ((mc.world.getBlockState(boost6).getBlock() == Blocks.OBSIDIAN) || (mc.world.getBlockState(boost6).getBlock() == Blocks.BEDROCK))
                && (mc.world.getBlockState(boost8).getBlock() == Blocks.AIR)
                && ((mc.world.getBlockState(boost9).getBlock() == Blocks.OBSIDIAN) || (mc.world.getBlockState(boost9).getBlock() == Blocks.BEDROCK));
    }
}
