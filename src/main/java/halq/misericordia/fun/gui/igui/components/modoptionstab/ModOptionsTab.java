package halq.misericordia.fun.gui.igui.components.modoptionstab;

import halq.misericordia.fun.gui.igui.ClickGuiScreen;
import halq.misericordia.fun.gui.igui.components.Component;
import halq.misericordia.fun.gui.igui.components.category.CategoryComponent;
import halq.misericordia.fun.utils.utils.RenderUtil;
import net.minecraft.client.gui.Gui;

import java.awt.*;

import static halq.misericordia.fun.utils.Minecraftable.mc;

/**
 * @author Halq
 * @since 30/07/2023 at 17:42
 */

public class ModOptionsTab implements Component {

    boolean open = CategoryComponent.modOptionsOpen;
    public int x;
    private final int openX;
    private final int closedX;
    private int currentX;

    public ModOptionsTab() {
        this.openX = ClickGuiScreen.INSTANCE.width - 140;
        this.closedX = ClickGuiScreen.INSTANCE.width;
        this.currentX = closedX;
    }

    @Override
    public void render(int mouseX, int mouseY) {
        float dt = mc.getRenderPartialTicks();
        currentX = (int) RenderUtil.lerp(currentX, open ? closedX : openX, dt);

        Gui.drawRect(currentX, 0, currentX + 140, ClickGuiScreen.INSTANCE.height, new Color(0, 0, 0, 200).getRGB());
    }

    public void animateLateralTab(boolean open) {
        this.open = open;
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {

    }

    @Override
    public void mouseReleased(int mouseX, int mouseY, int state) {

    }
}
