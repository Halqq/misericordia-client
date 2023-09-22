package halq.misericordia.fun.gui.igui.components.settings;

import halq.misericordia.fun.executor.modules.client.InteligentGui;
import halq.misericordia.fun.executor.settings.SettingInteger;
import halq.misericordia.fun.gui.igui.components.Component;
import halq.misericordia.fun.gui.igui.components.module.ModuleComponent;
import halq.misericordia.fun.utils.utils.RenderUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.text.DecimalFormat;

/**
 * @author Halq
 * @since 15/06/2023 at 19:31
 */

public class NumberIntComponent implements Component {

    private final Minecraft mc = Minecraft.getMinecraft();
    public int x;
    public int y;
    public int height;
    int width;
    int sliderwidth = 0;
    SettingInteger setting;
    boolean dragging;
    ModuleComponent parent;
    public boolean notVisible = false;

    public NumberIntComponent(ModuleComponent parent, int x, int y, SettingInteger setting) {
        this.x = x;
        this.y = y;
        this.width = 88;
        this.height = 13;
        this.parent = parent;
        this.setting = setting;
        boolean notVisible = !setting.getVisible();
    }

    private void updateSlider(int mouseX) {
        int diff = Math.min(width, Math.max(0, mouseX - x));

        int min = setting.getMinValue();
        int max = setting.getMaxValue();

        sliderwidth =  (setting.getValue() * width / max);

        if (dragging) {
            if (diff == 0) {
                setting.setValue(min);
            } else {
                setting.setValue(min + (max - min) * diff / width);
            }
        }
    }

    @Override
    public void render(int mouseX, int mouseY) {
        if (setting.getVisible()) {
            height = 13;

            updateSlider(mouseX);
            Gui.drawRect(x + 2, y, x + width, y + height, new Color(13, 13, 23, 255).getRGB());

            if (mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY <= y + height) {
                Gui.drawRect(x + 2, y, x + width, y + height, new Color(13, 13, 23, 182).brighter().getRGB());
            }

            GL11.glPushMatrix();
            GL11.glScaled(0.5, 0.5, 0.5);
            Gui.drawRect((this.x + 2) * 2 + 20, (this.y + 2) * 2 + 5, (this.x + 2) * 2 + 21 + sliderwidth, (this.y + 2) * 2 + 5 + height, new Color(InteligentGui.INSTANCE.red.getValue().intValue(), InteligentGui.INSTANCE.green.getValue().intValue(), InteligentGui.INSTANCE.blue.getValue().intValue(), InteligentGui.INSTANCE.alpha.getValue().intValue()).getRGB());
            DecimalFormat df = new DecimalFormat("#.#");
            String displayval = df.format(setting.getValue());
            mc.fontRenderer.drawStringWithShadow(displayval + "", (this.x + 2) * 2 + 120, (this.y + 3) * 2 + 5, new Color(255, 255, 255, 255).getRGB());
            mc.fontRenderer.drawStringWithShadow(setting.getName(), (this.x + 2) * 2 + 22, (this.y + 3) * 2 + 5, new Color(255, 255, 255, 255).getRGB());
            GL11.glPopMatrix();

            RenderUtil.drawLine(x + 2, y, x + 2, y + height, 1.5f, new Color(InteligentGui.INSTANCE.red.getValue().intValue(), InteligentGui.INSTANCE.green.getValue().intValue(), InteligentGui.INSTANCE.blue.getValue().intValue(), InteligentGui.INSTANCE.alpha.getValue().intValue()).getRGB());
        } else {
            height -= 13;
            y = 0;
            parent.buttonHeight -= 13;
        }
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        if(!setting.getVisible()) return;
        if (mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY <= y + height && mouseButton == 0) {
            dragging = true;
        }
    }

    @Override
    public void mouseReleased(int mouseX, int mouseY, int state) {
        if(!setting.getVisible()) return;
        dragging = false;
    }
}
