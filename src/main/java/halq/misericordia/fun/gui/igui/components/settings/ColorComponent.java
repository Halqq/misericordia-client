package halq.misericordia.fun.gui.igui.components.settings;

import halq.misericordia.fun.core.settingcore.Setting;
import halq.misericordia.fun.executor.modules.client.CustomFont;
import halq.misericordia.fun.executor.settings.SettingColor;
import halq.misericordia.fun.executor.settings.SettingDouble;
import halq.misericordia.fun.gui.igui.components.Component;
import halq.misericordia.fun.gui.igui.components.module.ModuleComponent;
import halq.misericordia.fun.managers.module.ModuleManager;
import halq.misericordia.fun.managers.text.TextManager;
import halq.misericordia.fun.utils.utils.ColorUtil;
import halq.misericordia.fun.utils.utils.RenderUtil;
import halq.misericordia.fun.executor.modules.client.InteligentGui;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.io.IOException;
import java.text.DecimalFormat;

import static org.lwjgl.opengl.GL11.*;

/**
 * @author Halq
 * @since 21/11/2022 at 17:44
 * pasted from aurora client (i owner for them)
 */

public class ColorComponent implements Component {

    private final Minecraft mc = Minecraft.getMinecraft();
    public int x;
    public int y;
    public int height;
    int width;
    boolean open;
    int redsliderwidth = 0;
    int greensliderwidth = 0;
    int bluesliderwidth = 0;
    int alphasliderwidth = 0;
    boolean redDragging;
    boolean greenDragging;
    boolean blueDragging;
    boolean alphaDragging;
    SettingColor setting;
    ModuleComponent parent;
    static ResourceLocation alphaslide = new ResourceLocation("textures/texture/alphaslide.png");

    public ColorComponent(ModuleComponent parent, int x, int y, SettingColor setting) {
        this.x = x;
        this.y = y;
        this.width = 88;
        this.height = 13;
        this.parent = parent;
        this.setting = setting;
    }

    private void updateRedSlider(int mouseX) {
        int diff = Math.min((width - 5), Math.max(0, mouseX - (x + 4)));

        int min = 0;
        int max = 255;

        redsliderwidth =  (setting.getValue().getRed() * (width - 5) / max);

        if (redDragging) {
            if (diff == 0) {
                setting.setRed(min);
            } else {
                setting.setRed(min + (max - min) * diff / (width - 5));
            }
        }
    }

    private void updateGreenSlider(int mouseX) {
        int diff = Math.min((width - 5), Math.max(0, mouseX - (x + 4)));

        int min = 0;
        int max = 255;

        greensliderwidth =  (setting.getValue().getGreen() * (width - 5) / max);

        if (greenDragging) {
            if (diff == 0) {
                setting.setGreen(min);
            } else {
                setting.setGreen(min + (max - min) * diff / (width - 5));
            }
        }
    }

    private void updateBlueSlider(int mouseX) {
        int diff = Math.min((width - 5), Math.max(0, mouseX - (x + 4)));

        int min = 0;
        int max = 255;

        bluesliderwidth =  (setting.getValue().getBlue() * (width - 5) / max);

        if (blueDragging) {
            if (diff == 0) {
                setting.setBlue(min);
            } else {
                setting.setBlue(min + (max - min) * diff / (width - 5));
            }
        }
    }

    private void updateAlphaSlider(int mouseX) {
        int diff = Math.min((width - 5), Math.max(0, mouseX - (x + 4)));

        int min = 0;
        int max = 255;

        alphasliderwidth =  (setting.getValue().getAlpha() * (width - 5) / max);

        if (alphaDragging) {
            if (diff == 0) {
                setting.setAlpha(min);
            } else {
                setting.setAlpha(min + (max - min) * diff / (width - 5));
            }
        }
    }

    @Override
    public void render(int mouseX, int mouseY) {

        Gui.drawRect(x + 2, y, x + width, y + height, new Color(13, 13, 23, 255).getRGB());
        GL11.glPushMatrix();
        GL11.glScalef(0.5f, 0.5f, 0.5f);
        mc.fontRenderer.drawStringWithShadow(setting.getName(), (x + 5) * 2, (y + 4) * 2, -1);
        GL11.glPopMatrix();

        if(open) {
            height = 110;
            updateRedSlider(mouseX);
            updateGreenSlider(mouseX);
            updateBlueSlider(mouseX);
            updateAlphaSlider(mouseX);

            RenderUtil.drawLine(x + 8, y + 13, x + width - 8, y + 13, 1f, new Color(InteligentGui.INSTANCE.red.getValue().intValue(), InteligentGui.INSTANCE.green.getValue().intValue(), InteligentGui.INSTANCE.blue.getValue().intValue(), InteligentGui.INSTANCE.alpha.getValue().intValue()).getRGB());

            //red slider
            Gui.drawRect(x + 4, y + 17, x + (redsliderwidth - 4) + 8, y + 25, new Color(255, 13, 23, 255).getRGB());
            String displayval = "Red: " + setting.getValue().getRed();
            GL11.glPushMatrix();
            GL11.glScalef(0.5f, 0.5f, 0.5f);
            mc.fontRenderer.drawStringWithShadow(displayval, (x + 5) * 2, (y + 20) * 2, -1);
            GL11.glPopMatrix();

            //green slider
            Gui.drawRect(x + 4, y + 27, x + (greensliderwidth - 4) + 8, y + 35, new Color(13, 255, 23, 255).getRGB());
            String displayval2 = "Green: " + setting.getValue().getGreen();
            GL11.glPushMatrix();
            GL11.glScalef(0.5f, 0.5f, 0.5f);
            mc.fontRenderer.drawStringWithShadow(displayval2, (x + 5) * 2, (y + 30) * 2, -1);
            GL11.glPopMatrix();

            //blue slider
            Gui.drawRect(x + 4, y + 37, x + (bluesliderwidth - 4) + 8, y + 45, new Color(13, 13, 255, 255).getRGB());
            String displayval3 = "Blue: " + setting.getValue().getBlue();
            GL11.glPushMatrix();
            GL11.glScalef(0.5f, 0.5f, 0.5f);
            mc.fontRenderer.drawStringWithShadow(displayval3, (x + 5) * 2, (y + 40) * 2, -1);
            GL11.glPopMatrix();

            // alpha slider
            Gui.drawRect(x + 4, y + 47, x + (alphasliderwidth - 4) + 8, y + 55, new Color(InteligentGui.INSTANCE.red.getValue(), InteligentGui.INSTANCE.green.getValue(), InteligentGui.INSTANCE.blue.getValue(), InteligentGui.INSTANCE.alpha.getValue()).getRGB());
            String displayval4 = "Alpha: " + setting.getValue().getAlpha();
            GL11.glPushMatrix();
            GL11.glScalef(0.5f, 0.5f, 0.5f);
            mc.fontRenderer.drawStringWithShadow(displayval4, (x + 5) * 2, (y + 50) * 2, -1);
            GL11.glPopMatrix();

            Gui.drawRect(x + 5, y + 60, x + 20, y + 75, new Color(setting.getValue().getRed(), setting.getValue().getGreen(), setting.getValue().getBlue(), setting.getValue().getAlpha()).getRGB());

            GL11.glPushMatrix();
            GL11.glScalef(0.5f, 0.5f, 0.5f);
            mc.fontRenderer.drawStringWithShadow("R:" + setting.getValue().getRed(), (x + 50) * 2, (y + 60) * 2, -1);
            mc.fontRenderer.drawStringWithShadow("G:" + setting.getValue().getGreen(), (x + 50)  * 2, (y + 65) * 2, -1);
            mc.fontRenderer.drawStringWithShadow("B:" + setting.getValue().getBlue(), (x + 50)  * 2, (y + 70) * 2, -1);
            mc.fontRenderer.drawStringWithShadow("A:" + setting.getValue().getAlpha(), (x + 50)  * 2, (y + 75) * 2, -1);
            String hexValue = Integer.toHexString(new Color(setting.getValue().getRed(), setting.getValue().getGreen(), setting.getValue().getBlue(), setting.getValue().getAlpha()).getRGB()).substring(2).toUpperCase();
            mc.fontRenderer.drawStringWithShadow("Hex: #" + hexValue, (x + 50)  * 2, (y + 80) * 2, -1);
            GL11.glPopMatrix();
        } else {
            height = 13;
        }

        RenderUtil.drawLine(x + 2, y, x + 2, y + height, 1.5f, new Color(InteligentGui.INSTANCE.red.getValue().intValue(), InteligentGui.INSTANCE.green.getValue().intValue(), InteligentGui.INSTANCE.blue.getValue().intValue(), InteligentGui.INSTANCE.alpha.getValue().intValue()).getRGB());
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        if(mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY <= y + 14) {
            open = !open;
        }

        if(mouseX >= x + 4 && mouseX <= x + width && mouseY >= y + 17 && mouseY <= y + 25) {
            redDragging = true;
        }

        if(mouseX >= x + 4 && mouseX <= x + width && mouseY >= y + 27 && mouseY <= y + 35) {
            greenDragging = true;
        }

        if(mouseX >= x + 4 && mouseX <= x + width && mouseY >= y + 37 && mouseY <= y + 45) {
            blueDragging = true;
        }

        if(mouseX >= x + 4 && mouseX <= x + width && mouseY >= y + 47 && mouseY <= y + 55) {
            alphaDragging = true;
        }
    }


    @Override
    public void mouseReleased(int mouseX, int mouseY, int state) {
        redDragging = false;
        greenDragging = false;
        blueDragging = false;
        alphaDragging = false;
    }
}