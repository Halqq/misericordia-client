package halq.misericordia.fun.gui.igui.components.particles;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.Random;

import static org.lwjgl.opengl.GL11.*;

/**
 * @author Halq
 * @since 30/07/2023 at 23:32
 */

public class Particle {

    public float x;
    public float y;
    public final float size;
    private final float ySpeed;
    private final float xSpeed;

    Particle(int x, int y) {
        this.x = x;
        this.y = y;
        this.size = genRandom();
        this.ySpeed = 0.0001f;
        this.xSpeed = 0.0001f;
    }

    void connect(float x, float y) {
        connectPoints(getX(), getY(), x, y);
    }

    void interpolation() {
        for (int n = 0; n <= 64; ++n) {
            final float f = n / 64.0f;
            final float p1 = lint1(f);
            final float p2 = lint2(f);

            if (p1 != p2) {
                y -= f;
                x -= f;
            }
        }
    }

    void fall() {
        final Minecraft mc = Minecraft.getMinecraft();
        final ScaledResolution scaledResolution = new ScaledResolution(mc);
        y = (y + ySpeed);
        x = (x + xSpeed);

        if (y > mc.displayHeight)
            y = 1;

        if (x > mc.displayWidth)
            x = 1;

        if (x < 1)
            x = scaledResolution.getScaledWidth();

        if (y < 1)
            y = scaledResolution.getScaledHeight();
    }

    private float genRandom() {
        return (float) (0.3f + Math.random() * (0.6f - 0.3f + 1.0F));
    }

    private float lint1(float f) {
        return ((float) 1.02 * (1.0f - f)) + (f);
    }

    private float lint2(float f) {
        return (float) 1.02 + f * ((float) 1.0 - (float) 1.02);
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    private static void connectPoints(float xOne, float yOne, float xTwo, float yTwo) {
        glPushMatrix();
        glEnable(GL_LINE_SMOOTH);
        glColor4f(255, 0, 0, 255);
        glDisable(GL_TEXTURE_2D);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        glEnable(GL_BLEND);
        glLineWidth(0.5F);
        glBegin(GL_LINES);
        glVertex2f(xOne, yOne);
        glVertex2f(xTwo, yTwo);
        glEnd();
        glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        glDisable(GL_LINE_SMOOTH);
        glEnable(GL_TEXTURE_2D);
        glPopMatrix();
    }

    public static void drawCircle(float x, float y, float radius, int color) {
        float alpha = (color >> 24 & 0xFF) / 255.0F;
        float red = (color >> 16 & 0xFF) / 255.0F;
        float green = (color >> 8 & 0xFF) / 255.0F;
        float blue = (color & 0xFF) / 255.0F;

        glColor4f(red, green, blue, alpha);
        glEnable(GL_BLEND);
        glDisable(GL_TEXTURE_2D);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        glEnable(GL_LINE_SMOOTH);
        glPushMatrix();
        glLineWidth(1F);
        glBegin(GL_POLYGON);
        for (int i = 0; i <= 360; i++)
            glVertex2d(x + Math.sin(i * Math.PI / 180.0D) * radius, y + Math.cos(i * Math.PI / 180.0D) * radius);
        glEnd();
        glPopMatrix();
        glEnable(GL_TEXTURE_2D);
        glDisable(GL_LINE_SMOOTH);
        glColor4f(1F, 1F, 1F, 1F);
    }
}