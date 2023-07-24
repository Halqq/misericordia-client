package halq.misericordia.fun.gui.igui.components.category;

import halq.misericordia.fun.executor.modules.client.InteligentGui;
import halq.misericordia.fun.executor.settings.SettingBoolean;
import halq.misericordia.fun.executor.settings.SettingScreen;
import halq.misericordia.fun.gui.igui.components.Component;
import halq.misericordia.fun.gui.igui.components.module.ModuleComponent;
import halq.misericordia.fun.utils.utils.RenderUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import org.lwjgl.opengl.GL11;

import java.awt.*;

/**
 * @author Halq
 * @since 23/06/2023 at 18:57
 */

public class SettingScreenComponent implements Component {

    private final Minecraft mc = Minecraft.getMinecraft();
    public int x;
    public int y;
    public int height;
    int width;
    SettingScreen setting;
    ModuleComponent moduleComponent;
    public boolean notVisible = false;

    public SettingScreenComponent(ModuleComponent moduleComponent, int x, int y, SettingScreen setting) {
        this.moduleComponent = moduleComponent;
        this.x = x;
        this.y = y;
        this.setting = setting;
        width = 88;
        height = 13;
        boolean notVisible = !setting.getVisible();
    }

    @Override
    public void render(int mouseX, int mouseY) {
        if (setting.getVisible()) {
            height = 13;
            Gui.drawRect(x + 2, y, x + width, y + height, new Color(13, 13, 23, 255).getRGB());
            Gui.drawRect(x + 4, y + 2, x + height + 2, y + height - 2, new Color(13, 13, 23, 182).brighter().getRGB());

            if (mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY <= y + height) {
                Gui.drawRect(x + 2, y, x + width, y + height, new Color(13, 13, 23, 182).brighter().getRGB());
            }

            GL11.glPushMatrix();
            GL11.glScalef(0.5f, 0.5f, 0.5f);
            mc.fontRenderer.drawStringWithShadow(setting.getName(), (this.x + 20) * 2 + 5, (this.y + 2.5f) * 2 + 5, -1);
            GL11.glPopMatrix();

            RenderUtil.drawLine(x + 2, y, x + 2, y + height, 1.5f, new Color(InteligentGui.INSTANCE.red.getValue().intValue(), InteligentGui.INSTANCE.green.getValue().intValue(), InteligentGui.INSTANCE.blue.getValue().intValue(), InteligentGui.INSTANCE.alpha.getValue().intValue()).getRGB());
        } else {
            height -= 13;
            y = 0;
            moduleComponent.buttonHeight -= 13;
        }
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        if (setting.getVisible()) {
            if (mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY <= y + height && mouseButton == 0) {
                mc.displayGuiScreen(setting.getScreen());
            }
        }
    }

    @Override
    public void mouseReleased(int mouseX, int mouseY, int state) {
    }
}
