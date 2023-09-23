package halq.misericordia.fun.gui.igui;

import halq.misericordia.fun.core.modulecore.Category;
import halq.misericordia.fun.gui.guibars.ClickBar;
import halq.misericordia.fun.gui.guibars.RenderBar;
import halq.misericordia.fun.gui.igui.components.category.CategoryComponent;
import halq.misericordia.fun.gui.igui.components.modoptionstab.ModOptionsTab;
import halq.misericordia.fun.gui.igui.components.particles.Particles;
import net.minecraft.client.gui.GuiScreen;
import org.lwjgl.input.Mouse;

import java.io.IOException;
import java.util.ArrayList;

/**
 * @author Halq
 * @since 19/11/2022 at 14:16
 */

public class ClickGuiScreen extends GuiScreen {

    public static ClickGuiScreen INSTANCE;
    private static final Particles particles = new Particles(240);

    ArrayList<CategoryComponent> components;
    int screenWidth = width;
    int screenHeight = height;

    public ClickGuiScreen() {
        components = new ArrayList<>();
        INSTANCE = this;

        int x = 5;
        int y = 10;
        for (Category c : Category.values()) {
            CategoryComponent component = new CategoryComponent(c, x, y);
            components.add(component);
            component.closedX = x;
            component.closedY = y;
            component.openX = x;
            component.openY = y;
            x += component.width + 3;
        }
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        super.drawDefaultBackground();
        for (CategoryComponent c : components) {
            c.render(mouseX, mouseY);
        }
        RenderBar.render(mouseX, mouseY, 1);
        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        for (CategoryComponent c : components) {
            c.mouseClicked(mouseX, mouseY, mouseButton);
        }
        ClickBar.mouseClicked(mouseX, mouseY, mouseButton);
        super.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    protected void mouseReleased(int mouseX, int mouseY, int state) {
        for (CategoryComponent c : components) {
            c.mouseReleased(mouseX, mouseY, state);
        }
        super.mouseReleased(mouseX, mouseY, state);
    }

    public void updateScreen() {
        int scrollWheel = Mouse.getDWheel();
        //from gui of aurora (made for me)
        for (CategoryComponent cF : components) {
            if (scrollWheel < 0) {
                cF.y = cF.y - 10;
            } else if (scrollWheel > 0) {
                cF.y = cF.y + 10;
            }
        }
    }

    @Override
    public void initGui() {
        for (CategoryComponent c : components) {
            c.x = c.closedX;
            c.y = c.closedY;
            c.renderY = 100000;
        }
    }

    @Override
    public void onGuiClosed() {
        for (CategoryComponent c : components) {
            c.closedX = c.x;
            c.closedY = c.y;
            c.renderY = 0;
        }
    }

    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        for (CategoryComponent c : components) {
            if(CategoryComponent.modOptionsOpen){
                ModOptionsTab.INSTANCE.keyTyped(typedChar, keyCode);
            } else {
                c.keyTyped(typedChar, keyCode);
            }
        }
        super.keyTyped(typedChar, keyCode);
    }
}
