package halq.misericordia.fun.gui.igui.components.category;

import halq.misericordia.fun.core.modulecore.Category;
import halq.misericordia.fun.core.modulecore.Module;
import halq.misericordia.fun.executor.modules.client.InteligentGui;
import halq.misericordia.fun.gui.igui.components.Component;
import halq.misericordia.fun.gui.igui.components.modoptionstab.ModOptionsTab;
import halq.misericordia.fun.gui.igui.components.module.ModuleComponent;
import halq.misericordia.fun.gui.igui.components.particles.Particles;
import halq.misericordia.fun.managers.module.ModuleManager;
import halq.misericordia.fun.utils.utils.RenderUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.util.ArrayList;

/**
 * @author Halq
 * @since 19/11/2022 at 14:17
 */

public class CategoryComponent implements Component {

    private static final Minecraft mc = Minecraft.getMinecraft();

    public int x;
    public int y;
    public int renderX;
    public int renderY;
    public int width;
    public int closedX;
    public int closedY;
    public int openX;
    public int openY;
    Category category;
    int height;
    boolean drag;
    int dragX;
    int dragY;
    boolean open;
    int moduleYHeight;
    public static boolean modOptionsOpen;
    ArrayList<ModuleComponent> moduleComponents;
    ModOptionsTab modOptionsTab = new ModOptionsTab();
    Module mo;

    public CategoryComponent(Category category, int x, int y) {
        moduleComponents = new ArrayList<>();
        this.category = category;
        this.x = x;
        this.y = y;
        width = 88;
        height = 13;
        int moduleY = y + height;
        open = true;

        for (Module m : ModuleManager.INSTANCE.getModulesInCategory(category)) {
            ModuleComponent moduleComponent = new ModuleComponent(m, this.x, moduleY);
            moduleComponents.add(moduleComponent);
            moduleY += moduleComponent.height;
            moduleYHeight += moduleComponent.height;
        }
        moduleYHeight = moduleY;
    }

    @Override
    public void render(int mouseX, int mouseY) {
        if (drag && !modOptionsOpen) {
            x = dragX + mouseX;
            y = dragY + mouseY;
        }

        if(!modOptionsOpen) {
            modOptionsOpen = mouseX == 0;
        } else {
            modOptionsOpen = mouseX <= 140;
        }

        float dt = mc.getRenderPartialTicks();

        renderX = (int) RenderUtil.lerp(renderX, x, dt);
        renderY = (int) RenderUtil.lerp(renderY, y, dt);

        Gui.drawRect(renderX, renderY, renderX + width, renderY + height, new Color(13, 13, 23, 255).brighter().getRGB());
        RenderUtil.drawLine(renderX, renderY + height, renderX + width, renderY + height, 1.5f, new Color(InteligentGui.INSTANCE.red.getValue().intValue(), InteligentGui.INSTANCE.green.getValue().intValue(), InteligentGui.INSTANCE.blue.getValue().intValue(), InteligentGui.INSTANCE.alpha.getValue().intValue()).getRGB());

        GL11.glPushMatrix();
        GL11.glScalef(0.5f, 0.5f, 0.5f);
        mc.fontRenderer.drawStringWithShadow(category.name(), (renderX + 30) * 2 + 5, (renderY + 2.5f) * 2 + 5, 0xFFFFFFFF);
        GL11.glPopMatrix();

        if (open) {
            for (Module mod : ModuleManager.INSTANCE.getModules()) {
                mo = mod;
            }
            boolean isLastModule = ModuleManager.INSTANCE.getModulesInCategory(mo.getCategory()).indexOf(mo) == ModuleManager.INSTANCE.getModulesInCategory(mo.getCategory()).size() - 1;
            RenderUtil.drawLine(renderX, renderY, renderX, renderY + height, 1.5f, new Color(InteligentGui.INSTANCE.red.getValue().intValue(), InteligentGui.INSTANCE.green.getValue().intValue(), InteligentGui.INSTANCE.blue.getValue().intValue(), InteligentGui.INSTANCE.alpha.getValue().intValue()).getRGB());

            moduleYHeight = 0;

            for (ModuleComponent moduleComponent : moduleComponents) {
                if (moduleComponent.x != renderX) moduleComponent.x = renderX;
                moduleComponent.y = renderY + height + moduleYHeight;
                moduleComponent.render(mouseX, mouseY);
                moduleYHeight += moduleComponent.height;
            }
        }

        if (modOptionsOpen) {
            modOptionsTab.animateLateralTab(true);
        } else {
            modOptionsTab.animateLateralTab(false);
        }

        // Render the tab and its contents
        modOptionsTab.render(mouseX, mouseY);

    }


    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        if (modOptionsOpen) {
            modOptionsTab.mouseClicked(mouseX, mouseY, mouseButton);

        } else {
            if (mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY <= y + height) {
                if (mouseButton == 0) {
                    drag = true;
                    dragX = x - mouseX;
                    dragY = y - mouseY;
                }
                if (mouseButton == 1) {
                    open = !open;
                }
            }

            for (ModuleComponent moduleComponent : moduleComponents) {
                moduleComponent.mouseClicked(mouseX, mouseY, mouseButton);
            }
        }
    }

    @Override
    public void mouseReleased(int mouseX, int mouseY, int state) {
        if (modOptionsOpen) {
            modOptionsTab.mouseReleased(mouseX, mouseY, state);
        } else {
            drag = false;

            for (ModuleComponent moduleComponent : moduleComponents) {
                moduleComponent.mouseReleased(mouseX, mouseY, state);
            }
        }
    }

    public void keyTyped(char typedChar, int keyCode) {
        if (modOptionsOpen) {

        } else {
            for (ModuleComponent moduleComponent : moduleComponents) {
                moduleComponent.keyTyped(typedChar, keyCode);
            }
        }
    }
}
