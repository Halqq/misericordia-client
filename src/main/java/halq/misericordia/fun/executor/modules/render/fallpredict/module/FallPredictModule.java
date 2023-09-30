package halq.misericordia.fun.executor.modules.render.fallpredict.module;

import halq.misericordia.fun.core.modulecore.Category;
import halq.misericordia.fun.core.modulecore.Module;
import halq.misericordia.fun.events.RenderEvent;
import halq.misericordia.fun.executor.modules.render.fallpredict.FallPredictCalcs;
import halq.misericordia.fun.utils.utils.RenderUtil;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;

public class FallPredictModule extends Module {

    public FallPredictModule() {
        super("FallPredict", Category.RENDER);
    }

    @Override
    public void onRender3D(RenderEvent event){
        BlockPos fallPos = FallPredictCalcs.calcPos();

        if(fallPos != null) {
            final AxisAlignedBB bb = new AxisAlignedBB(fallPos.x - mc.getRenderManager().viewerPosX, fallPos.y - mc.getRenderManager().viewerPosY, fallPos.z - mc.getRenderManager().viewerPosZ, fallPos.x + 1 - mc.getRenderManager().viewerPosX, fallPos.y + (1) - mc.getRenderManager().viewerPosY, fallPos.z + 1 - mc.getRenderManager().viewerPosZ);

            if (RenderUtil.isInViewFrustrum(new AxisAlignedBB(bb.minX + mc.getRenderManager().viewerPosX, bb.minY + mc.getRenderManager().viewerPosY, bb.minZ + mc.getRenderManager().viewerPosZ, bb.maxX + mc.getRenderManager().viewerPosX, bb.maxY + mc.getRenderManager().viewerPosY, bb.maxZ + mc.getRenderManager().viewerPosZ))) {
                RenderUtil.drawESP(bb, 0, 255, 0, 255);
            }
        }
    }
}
