package halq.misericordia.fun.gui.igui.components.settings;

import halq.misericordia.fun.core.modulecore.Module;
import halq.misericordia.fun.executor.modules.client.InteligentGui;
import halq.misericordia.fun.gui.igui.components.Component;
import halq.misericordia.fun.gui.igui.components.module.ModuleComponent;
import halq.misericordia.fun.managers.module.ModuleManager;
import halq.misericordia.fun.utils.utils.RenderUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import java.awt.*;

/**
 * @author Halq
 * @since 15/06/2023 at 22:15
 */

public class KeyBindComponent implements Component {

    private final Minecraft mc = Minecraft.getMinecraft();
    public int x;
    public int y;
    public int height;
    int width;
    ModuleComponent moduleComponent;
    boolean listening = false;
    Module module;

    public KeyBindComponent(ModuleComponent moduleComponent, int x, int y, Module module) {
        this.moduleComponent = moduleComponent;
        this.module = module;
        this.x = x;
        this.y = y;
        width = 88;
        height = 13;
        listening = false;
    }

    @Override
    public void render(int mouseX, int mouseY) {
        boolean isLastModule = ModuleManager.INSTANCE.getModulesInCategory(module.getCategory()).indexOf(module) == ModuleManager.INSTANCE.getModulesInCategory(module.getCategory()).size() - 1;

        Gui.drawRect(x + 2, y, x + width , y + 5, new Color(13, 13, 23, 255).getRGB());

        if (isLastModule) {
            RenderUtil.drawRoundedRect(x + 2 , y, width - 2 , 13, 5, new Color(13, 13, 23, 255));
        } else {
            Gui.drawRect(x + 2, y, x + width , y + 5, new Color(13, 13, 23, 255).getRGB());
            Gui.drawRect(x + 2 , y, x + width, y + 13, new Color(13, 13, 23, 255).getRGB());
        }

        if (mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY <= y + 13) {
            if (isLastModule) {
                Gui.drawRect(x + 2, y, x + width, y + 5, new Color(13, 13, 23, 182).brighter().getRGB());
                RenderUtil.drawRoundedRect(x + 2, y, width - 2, 13, 5, new Color(13, 13, 23, 182).brighter());
            } else {
                Gui.drawRect(x + 2, y, x + width, y + 13, new Color(13, 13, 23, 182).brighter().getRGB());
            }
        }

        GL11.glPushMatrix();
        GL11.glScalef(0.5f, 0.5f, 0.5f);
        mc.fontRenderer.drawStringWithShadow("Keybind: ", (this.x + 20) * 2 - 20, (this.y + 2.5f) * 2 + 5, -1);
        String keyName = listening ? "Listening..." : module.getKeyName();
        mc.fontRenderer.drawStringWithShadow(keyName, (this.x + 20) * 2 + 50, (this.y + 2.5f) * 2 + 5, -1);
        GL11.glPopMatrix();

        if(isLastModule){
            RenderUtil.drawLine(x + 2, y, x + 2, y + 7,1.5f, new Color(InteligentGui.INSTANCE.red.getValue().intValue(), InteligentGui.INSTANCE.green.getValue().intValue(), InteligentGui.INSTANCE.blue.getValue().intValue(), InteligentGui.INSTANCE.alpha.getValue().intValue()).getRGB());
        } else {
            RenderUtil.drawLine(x + 2, y, x + 2, y + 13,1.5f, new Color(InteligentGui.INSTANCE.red.getValue().intValue(), InteligentGui.INSTANCE.green.getValue().intValue(), InteligentGui.INSTANCE.blue.getValue().intValue(), InteligentGui.INSTANCE.alpha.getValue().intValue()).getRGB());
        }
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        if (mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY <= y + height && mouseButton == 0) {
            listening = !listening;
        }
    }

    public void keyTyped(char typedChar, int keyCode) {
        if (listening) {
            if (keyCode != 0 && keyCode != Keyboard.KEY_ESCAPE) {
                if (keyCode == Keyboard.KEY_DELETE) module.setKey(Keyboard.KEY_NONE);
                else module.setKey(keyCode);
            }
            listening = false;
        }
    }

    @Override
    public void mouseReleased(int mouseX, int mouseY, int state) {

    }
}
