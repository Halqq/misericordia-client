package halq.misericordia.fun.gui.igui.components.modoptionstab;

import halq.misericordia.fun.gui.igui.ClickGuiScreen;
import halq.misericordia.fun.gui.igui.components.Component;
import halq.misericordia.fun.gui.igui.components.category.CategoryComponent;
import halq.misericordia.fun.gui.igui.components.modoptionstab.options.ColorOptions;
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
    Options currentOption = Options.Menu;
    public static ModOptionsTab INSTANCE;

    public ModOptionsTab() {
        this.openX = ClickGuiScreen.INSTANCE.width - 140;
        this.closedX = ClickGuiScreen.INSTANCE.width;
        this.currentX = closedX;
        INSTANCE = this;
    }

    @Override
    public void render(int mouseX, int mouseY) {
        float dt = mc.getRenderPartialTicks();
        currentX = (int) RenderUtil.lerp(currentX, open ? closedX : openX, dt);
        Gui.drawRect(currentX, 0, currentX + 140, ClickGuiScreen.INSTANCE.height, new Color(0, 0, 0, 200).getRGB());

        switch (currentOption) {
            case Menu:
                mc.fontRenderer.drawString("Mod Options", currentX + 5, 5, new Color(255, 255, 255, 255).getRGB());

                if (mouseX >= 0 && mouseX <= 140 && mouseY >= 29 && mouseY <= 30 + mc.fontRenderer.FONT_HEIGHT) {
                    Gui.drawRect(currentX, 29, currentX + 140, 30 + mc.fontRenderer.FONT_HEIGHT, new Color(255, 255, 255, 100).getRGB());
                }

                mc.fontRenderer.drawString("Color", currentX + 60, 30, new Color(255, 255, 255, 255).getRGB());
                break;
            case COLORS:
                mc.fontRenderer.drawString("Colors", currentX + 5, 5, new Color(255, 255, 255, 255).getRGB());
                mc.fontRenderer.drawString("<", currentX + 130, 5, new Color(255, 255, 255, 255).getRGB());

                if (mouseX >= currentX + 127 && mouseX <= currentX + 130 + mc.fontRenderer.getStringWidth("<") + 10 && mouseY >= 3 && mouseY <= 5 + mc.fontRenderer.FONT_HEIGHT + 3) {
                    Gui.drawRect(currentX + 127, 3, currentX + 130 + mc.fontRenderer.getStringWidth("<") + 5, 5 + mc.fontRenderer.FONT_HEIGHT + 3, new Color(255, 255, 255, 100).getRGB());
                }

                if (mouseX >= currentX && mouseX <= currentX + 140 && mouseY >= 29 && mouseY <= 30 + mc.fontRenderer.FONT_HEIGHT && !ColorOptions.COLORSYNC.bool) {
                    Gui.drawRect(currentX, 29, currentX + 140, 30 + mc.fontRenderer.FONT_HEIGHT, new Color(255, 255, 255, 100).getRGB());
                } else if (ColorOptions.COLORSYNC.bool) {
                    Gui.drawRect(currentX, 29, currentX + 140, 30 + mc.fontRenderer.FONT_HEIGHT, new Color(0, 255, 0, 100).getRGB());
                }

                if (mouseX >= currentX && mouseX <= currentX + 140 && mouseY >= 39 && mouseY <= 40 + mc.fontRenderer.FONT_HEIGHT && !ColorOptions.RAINBOW.bool) {
                    Gui.drawRect(currentX, 39, currentX + 140, 40 + mc.fontRenderer.FONT_HEIGHT, new Color(255, 255, 255, 100).getRGB());
                } else if (ColorOptions.RAINBOW.bool) {
                    Gui.drawRect(currentX, 39, currentX + 140, 40 + mc.fontRenderer.FONT_HEIGHT, new Color(0, 255, 0, 100).getRGB());
                }

                if (mouseX >= currentX && mouseX <= currentX + 140 && mouseY >= 49 && mouseY <= 50 + mc.fontRenderer.FONT_HEIGHT && !ColorOptions.RAINBOWSPEED.bool) {
                    Gui.drawRect(currentX, 49, currentX + 140, 50 + mc.fontRenderer.FONT_HEIGHT, new Color(255, 255, 255, 100).getRGB());
                }

                mc.fontRenderer.drawString("ColorSync (all modules)", currentX + 5, 30, new Color(255, 255, 255, 255).getRGB());
                mc.fontRenderer.drawString("Rainbow", currentX + 5, 40, new Color(255, 255, 255, 255).getRGB());
                mc.fontRenderer.drawString("Rainbow Speed   " + ColorOptions.RAINBOWSPEED.value, currentX + 5, 50, new Color(255, 255, 255, 255).getRGB());
                break;
        }
    }

    public void animateLateralTab(boolean open) {
        this.open = open;
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        switch (currentOption) {
            case Menu:
                if (mouseX >= 0 && mouseX <= 140 && mouseY >= 29 && mouseY <= 30 + mc.fontRenderer.FONT_HEIGHT) {
                    currentOption = Options.COLORS;
                }
                break;

            case COLORS:
                if (mouseX >= currentX + 127 && mouseX <= currentX + 130 + mc.fontRenderer.getStringWidth("<") + 10 && mouseY >= 3 && mouseY <= 5 + mc.fontRenderer.FONT_HEIGHT + 3) {
                    currentOption = Options.Menu;
                }

                if (mouseX >= currentX && mouseX <= currentX + 140 && mouseY >= 29 && mouseY <= 30 + mc.fontRenderer.FONT_HEIGHT && !ColorOptions.COLORSYNC.bool) {
                    ColorOptions.COLORSYNC.bool = true;
                } else if (mouseX >= currentX && mouseX <= currentX + 140 && mouseY >= 29 && mouseY <= 30 + mc.fontRenderer.FONT_HEIGHT && ColorOptions.COLORSYNC.bool) {
                    ColorOptions.COLORSYNC.bool = false;
                }

                if (mouseX >= currentX && mouseX <= currentX + 140 && mouseY >= 39 && mouseY <= 40 + mc.fontRenderer.FONT_HEIGHT && !ColorOptions.RAINBOW.bool) {
                    ColorOptions.RAINBOW.bool = true;
                } else if (mouseX >= currentX && mouseX <= currentX + 140 && mouseY >= 39 && mouseY <= 40 + mc.fontRenderer.FONT_HEIGHT && ColorOptions.RAINBOW.bool) {
                    ColorOptions.RAINBOW.bool = false;
                }

                if (mouseX >= currentX && mouseX <= currentX + 140 && mouseY >= 49 && mouseY <= 50 + mc.fontRenderer.FONT_HEIGHT && !ColorOptions.RAINBOWSPEED.typing) {
                    ColorOptions.RAINBOWSPEED.typing = true;
                } else if (mouseX >= currentX && mouseX <= currentX + 140 && mouseY >= 49 && mouseY <= 50 + mc.fontRenderer.FONT_HEIGHT && ColorOptions.RAINBOWSPEED.typing) {
                    ColorOptions.RAINBOWSPEED.typing = false;
                }

                break;
        }
    }

    @Override
    public void mouseReleased(int mouseX, int mouseY, int state) {

    }

    public void keyTyped(char typedChar, int keyCode) {
        switch (currentOption) {
            case COLORS:
                if (ColorOptions.RAINBOWSPEED.typing) {
                    String value = ColorOptions.RAINBOWSPEED.value;

                    if (keyCode == 28) {
                        ColorOptions.RAINBOWSPEED.typing = false;
                    } else if (keyCode == 14 && value.length() > 0) {
                        value = value.substring(0, value.length() - 1);
                    } else if (keyCode >= 2 && keyCode <= 11) {
                        value += typedChar;
                    }

                    ColorOptions.RAINBOWSPEED.value = value;
                }
                break;
        }
    }
}
