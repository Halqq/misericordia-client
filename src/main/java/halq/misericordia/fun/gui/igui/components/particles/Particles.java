package halq.misericordia.fun.gui.igui.components.particles;

import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static halq.misericordia.fun.gui.igui.components.particles.Particle.drawCircle;

/**
 * @author Halq
 * @since 30/07/2023 at 23:34
 */

@SideOnly(Side.CLIENT)
public class Particles {

    private static final List<Particle> particles = new ArrayList<>();
    private static int amount;
    private static int prevWidth;
    private static int prevHeight;

    public Particles(final int amount) {
        Particles.amount = amount;
    }

    public void draw(final int mouseX, final int mouseY) {
        checkForScreenResize();

        for (final Particle particle : particles) {
            updateParticlePosition(particle);
            connectNearbyParticles(particle, mouseX, mouseY);
            drawParticle(particle);
        }
    }

    private static void checkForScreenResize() {
        if (particles.isEmpty() || prevWidth != Minecraft.getMinecraft().displayWidth || prevHeight != Minecraft.getMinecraft().displayHeight) {
            particles.clear();
            createParticles();
        }

        prevWidth = Minecraft.getMinecraft().displayWidth;
        prevHeight = Minecraft.getMinecraft().displayHeight;
    }

    private static void createParticles() {
        final Random random = new Random();

        for (int i = 0; i < amount; i++)
            particles.add(new Particle(random.nextInt(Minecraft.getMinecraft().displayWidth), random.nextInt(Minecraft.getMinecraft().displayHeight)));
    }

    private static void updateParticlePosition(Particle particle) {
        particle.fall();
        particle.interpolation();
    }

    private static void connectNearbyParticles(Particle particle, int mouseX, int mouseY) {
        int range = 30;
        final boolean mouseOver = (mouseX >= particle.getX() - range) && (mouseY >= particle.getY() - range) &&
                (mouseX <= particle.getX() + range) && (mouseY <= particle.getY() + range);

        if (mouseOver) {
            for (Particle connectable : particles) {
                if (isConnectable(particle, connectable, range)) {
                    particle.connect(connectable.getX(), connectable.getY());
                }
            }
        }
    }

    private static boolean isConnectable(Particle particle, Particle connectable, int range) {
        return (connectable.getX() > particle.getX() && connectable.getX() - particle.getX() < range
                && particle.getX() - connectable.getX() < range)
                && (connectable.getY() > particle.getY() && connectable.getY() - particle.getY() < range
                || particle.getY() > connectable.getY() && particle.getY() - connectable.getY() < range);
    }

    private static void drawParticle(Particle particle) {
        drawCircle(particle.getX(), particle.getY(), particle.size, new Color(255, 0, 0, 255).getRGB());
    }
}