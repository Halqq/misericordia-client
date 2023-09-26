package halq.misericordia.fun.executor.modules.combat.crystalaura;

import halq.misericordia.fun.managers.text.TextManager;
import halq.misericordia.fun.utils.Minecraftable;
import halq.misericordia.fun.utils.utils.RenderUtil;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;

import java.awt.*;

/**
 * @author Halq
 * @since 10/06/2023 at 15:20
 */

public class CrystalAuraRender implements Minecraftable {

    private static BlockPos previousPos;
    private static BlockPos currentPos;
    private static float renderX;
    private static float renderY;
    private static float renderZ;

    public static void render(BlockPos pos, float dmg, float r, float g, float b, float a) {
        if (currentPos == null || !pos.equals(currentPos)) {
            previousPos = currentPos;
            currentPos = pos;
        }

        if (previousPos == null) {
            renderX = pos.getX();
            renderY = pos.getY();
            renderZ = pos.getZ();
        } else {
            float lerpAmount = 0.2f;
            renderX = lerp(renderX, pos.getX(), lerpAmount);
            renderY = lerp(renderY, pos.getY(), lerpAmount);
            renderZ = lerp(renderZ, pos.getZ(), lerpAmount);
        }

        final AxisAlignedBB bb = new AxisAlignedBB(renderX - mc.getRenderManager().viewerPosX, renderY - mc.getRenderManager().viewerPosY, renderZ - mc.getRenderManager().viewerPosZ, renderX + 1 - mc.getRenderManager().viewerPosX, renderY + (1) - mc.getRenderManager().viewerPosY, renderZ + 1 - mc.getRenderManager().viewerPosZ);

        if (RenderUtil.isInViewFrustrum(new AxisAlignedBB(bb.minX + mc.getRenderManager().viewerPosX, bb.minY + mc.getRenderManager().viewerPosY, bb.minZ + mc.getRenderManager().viewerPosZ, bb.maxX + mc.getRenderManager().viewerPosX, bb.maxY + mc.getRenderManager().viewerPosY, bb.maxZ + mc.getRenderManager().viewerPosZ))) {
            Color color = new Color(r, g, b, a);
            Color color2 = new Color(r, g, b, a / 5.5F);

            RenderUtil.drawGradientFilledBox(bb, color, color2);
            RenderUtil.drawEspOutline(bb, color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha(), 1.5f);
            //drawText3D(pos,"DMG: " + dmg, Color.WHITE.getRGB());
        }
    }

    private static float lerp(float start, float end, float amount) {
        return start + amount * (end - start);
    }

    public static void drawText3D(final BlockPos pos, final String text, int color) {
        if (pos == null || text == null) return;

        GlStateManager.pushMatrix();
        glBillboardDistanceScaled(pos.getX() + 0.5f, pos.getY() + 0.5f, pos.getZ() + 0.5f, mc.player, 1.0f);
        GlStateManager.disableDepth();

        GlStateManager.scale(0.5, 0.5, 0.5);

        FontRenderer fontRenderer = mc.fontRenderer;
        float textWidth = fontRenderer.getStringWidth(text);
        float textPosX = -textWidth / 2.0f;
        float textPosY = 0.0f;
        float textPosZ = 0.0f;

        GlStateManager.translate(textPosX, textPosY, textPosZ);
        TextManager.INSTANCE.drawString(text, 0.0f, 0.0f, color, false, false);

        GlStateManager.popMatrix();
    }

    public static void glBillboard(final float x, final float y, final float z) {
        final float scale = 0.02666667f;
        GlStateManager.translate(x - mc.getRenderManager().renderViewEntity.posX, y - mc.getRenderManager().renderViewEntity.posY, z - mc.getRenderManager().renderViewEntity.posZ);
        GlStateManager.glNormal3f(0.0f, 1.0f, 0.0f);
        GlStateManager.rotate(-mc.player.rotationYaw, 0.0f, 1.0f, 0.0f);
        GlStateManager.rotate(mc.player.rotationPitch, (mc.gameSettings.thirdPersonView == 2) ? -1.0f : 1.0f, 0.0f, 0.0f);
        GlStateManager.scale(-scale, -scale, scale);
    }

    public static void glBillboardDistanceScaled(final float x, final float y, final float z, final EntityPlayer player, final float scale) {
        glBillboard(x, y, z);
        final int distance = (int)player.getDistance(x, y, z);
        float scaleDistance = distance / 2.0f / (2.0f + (2.0f - scale));
        if (scaleDistance < 1.0f) scaleDistance = 1.0f;
        GlStateManager.scale(scaleDistance, scaleDistance, scaleDistance);
    }
}
