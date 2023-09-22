package halq.misericordia.fun.gui.igui.components.settings;

import halq.misericordia.fun.executor.settings.SettingCategory;
import halq.misericordia.fun.gui.igui.components.Component;
import halq.misericordia.fun.gui.igui.components.module.ModuleComponent;
import halq.misericordia.fun.utils.utils.RenderUtil;
import halq.misericordia.fun.executor.modules.client.InteligentGui;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import org.lwjgl.opengl.GL11;

import java.awt.*;

/**
 * @author Halq
 * @since 16/06/2023 at 13:03
 */

public class CategorySettingComponent implements Component {

    private final Minecraft mc = Minecraft.getMinecraft();
    public int x;
    public int y;
    int width;
    public int height;
    SettingCategory setting;
    ModuleComponent parent;
    public boolean notVisible = false;

    public CategorySettingComponent(ModuleComponent parent, int x, int y, SettingCategory setting) {
        this.x = x;
        this.y = y;
        this.width = 88;
        this.height = 13;
        this.parent = parent;
        this.setting = setting;
        boolean notVisible = !setting.getVisible();
    }

    @Override
    public void render(int mouseX, int mouseY) {
        if (setting.getVisible()) {
            Gui.drawRect(x + 2, y, x + width, y + height, new Color(13, 13, 23, 255).getRGB());

            if (mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY <= y + height) {
                Gui.drawRect(x + 2, y, x + width, y + height, new Color(13, 13, 23, 182).brighter().getRGB());

            }

            GL11.glPushMatrix();
            GL11.glScalef(0.5f, 0.5f, 0.5f);
            int fontWidth = mc.fontRenderer.getStringWidth(setting.getValue());
            mc.fontRenderer.drawString(setting.getValue(), (x + 2) * 2 + (width * 2 - fontWidth) / 2, (y + 2) * 2 + 2, -1);
            GL11.glPopMatrix();

            RenderUtil.drawLine(x + 2, y, x + 2, y + height, 1.5f, new Color(InteligentGui.INSTANCE.red.getValue().intValue(), InteligentGui.INSTANCE.green.getValue().intValue(), InteligentGui.INSTANCE.blue.getValue().intValue(), InteligentGui.INSTANCE.alpha.getValue().intValue()).getRGB());
            RenderUtil.drawLine(x + 14, y + height - 4, x + width - 12, y + height - 4, 1.5f, new Color(InteligentGui.INSTANCE.red.getValue().intValue(), InteligentGui.INSTANCE.green.getValue().intValue(), InteligentGui.INSTANCE.blue.getValue().intValue(), InteligentGui.INSTANCE.alpha.getValue().intValue()).getRGB());
        }else {
            height -= 13;
            y = 0;

        }
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        if(setting.getVisible()) {
            if (mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY <= y + height && mouseButton == 0) {
                String[] modes = setting.getModes().toArray(new String[0]);
                ;
                int index = 0;
                for (int i = 0; i < modes.length; i++) {
                    if (modes[i].equalsIgnoreCase(setting.getValue())) {
                        index = i;
                    }
                }
                if (index == modes.length - 1) {
                    setting.setValue(modes[0]);
                } else {
                    setting.setValue(modes[index + 1]);
                }
            }
        }
    }

    @Override
    public void mouseReleased(int mouseX, int mouseY, int state) {}
}
