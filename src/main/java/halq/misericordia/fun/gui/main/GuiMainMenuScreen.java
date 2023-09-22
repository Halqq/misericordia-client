package halq.misericordia.fun.gui.main;

import halq.misericordia.fun.core.fontcore.CustomFont;
import halq.misericordia.fun.utils.utils.RenderUtil;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

import java.awt.*;

/**
 * @author Halq
 * @since 25/07/2023 at 17:46
 */

public class GuiMainMenuScreen extends GuiScreen {

    private final ResourceLocation backgroundTexture = new ResourceLocation("textures/texture/img_1.png");
    private final ResourceLocation singlePlayerIcon = new ResourceLocation("textures/icons/singleplayer.png");
    private final ResourceLocation multiplayerIcon = new ResourceLocation("textures/icons/multiplayer.png");

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
     //   drawBackground();
        super.drawScreen(mouseX, mouseY, partialTicks);
        Gui.drawRect(0, 0, width, height, new Color(0, 0, 0, 120).getRGB());

        CustomFont cFont = new CustomFont(new Font("sans-serif", Font.BOLD, 30), true, true);

        int x = width / 2;
        int y = height / 2;
        cFont.drawString("Misericordia", x, 10, new Color(255, 255, 255, 255).getRGB());
        RenderUtil.drawRoundedRect(x - 150, y - 40, 300, 80, 10, new Color(1, 1, 1, 140));

        mc.getTextureManager().bindTexture(singlePlayerIcon);
        GL11.glPushMatrix();
        GL11.glTranslatef(x - 150, y - 40, 0.0f);
        GL11.glBegin(7);
        GL11.glTexCoord2f(0.0f, 0.0f);
        GL11.glVertex3f(0.0f, 0.0f, 0.0f);
        GL11.glTexCoord2f(0.0f, 1.0f);
        GL11.glVertex3f(0.0f, 32.0f, 0.0f);
        GL11.glTexCoord2f(1.0f, 1.0f);
        GL11.glVertex3f(32.0f, 32.0f, 0.0f);
        GL11.glTexCoord2f(1.0f, 0.0f);
        GL11.glVertex3f(32.0f, 0.0f, 0.0f);
        GL11.glEnd();
        GL11.glPopMatrix();
    }

    private void drawBackground() {
        mc.getTextureManager().bindTexture(backgroundTexture);
        GL11.glPushMatrix();
        GL11.glTranslatef(0, 0, 0.0f);
        GL11.glBegin(7);
        GL11.glTexCoord2f(0.0f, 0.0f);
        GL11.glVertex3f(0.0f, 0.0f, 0.0f);
        GL11.glTexCoord2f(0.0f, 1.0f);
        GL11.glVertex3f(0.0f, height, 0.0f);
        GL11.glTexCoord2f(1.0f, 1.0f);
        GL11.glVertex3f(width, height, 0.0f);
        GL11.glTexCoord2f(1.0f, 0.0f);
        GL11.glVertex3f(width, 0.0f, 0.0f);
        GL11.glEnd();
        GL11.glPopMatrix();
    }
}
