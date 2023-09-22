package halq.misericordia.fun.gui.igui.components.module;

import halq.misericordia.fun.core.modulecore.Module;
import halq.misericordia.fun.core.settingcore.Setting;
import halq.misericordia.fun.executor.settings.*;
import halq.misericordia.fun.gui.igui.components.settings.*;
import halq.misericordia.fun.managers.module.ModuleManager;
import halq.misericordia.fun.managers.setting.SettingManager;
import halq.misericordia.fun.gui.igui.components.Component;
import halq.misericordia.fun.executor.modules.client.InteligentGui;
import halq.misericordia.fun.utils.utils.RenderUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.util.ArrayList;

/**
 * @author Halq
 * @since 19/11/2022 at 15:33
 */

public class ModuleComponent implements Component {

    private static final Minecraft mc = Minecraft.getMinecraft();
    public int height;
    public int width;
    public int x;
    public int y;
    ArrayList<BooleanComponent> booleanComponents;
    ArrayList<NumberComponent> numberComponents;
    ArrayList<ModeComponent> modeComponents;
    ArrayList<ColorComponent> colorComponents;
    ArrayList<CategorySettingComponent> categorySettingComponents;
    ArrayList<NumberIntComponent> numberIntComponents;
    ArrayList<SettingScreenComponent> settingScreenComponents;
    Module module;
    KeyBindComponent keyBindComponent;

    boolean open;
    public int buttonHeight;
    int radius = 5;

    public ModuleComponent(Module m, int x, int y) {
        booleanComponents = new ArrayList<>();
        numberComponents = new ArrayList<>();
        modeComponents = new ArrayList<>();
        colorComponents = new ArrayList<>();
        categorySettingComponents = new ArrayList<>();
        numberIntComponents = new ArrayList<>();
        settingScreenComponents = new ArrayList<>();

        module = m;
        height = 13;
        width = 88;
        this.x = x;
        this.y = y;
        int buttonY = y + height;

        keyBindComponent = new KeyBindComponent(this, x, y, module);

        for (Setting s :  SettingManager.INSTANCE.getSettingsInModule(module)) {
            if(s instanceof SettingCategory){
                CategorySettingComponent categorySettingComponent = new CategorySettingComponent(this, x, y + buttonHeight + buttonY, (SettingCategory) s);
                categorySettingComponents.add(categorySettingComponent);
                buttonY += categorySettingComponent.height;
                buttonHeight += categorySettingComponent.height;
            }

                if (s instanceof SettingBoolean) {
                    BooleanComponent booleanComponent = new BooleanComponent(this, x, y + buttonHeight + buttonY, (SettingBoolean) s);
                    booleanComponents.add(booleanComponent);
                    buttonY += booleanComponent.height;
                    buttonHeight += booleanComponent.height;
                }

                if (s instanceof SettingColor) {
                    ColorComponent colorComponent = new ColorComponent(this, x, y + buttonHeight + buttonY, (SettingColor) s);
                    colorComponents.add(colorComponent);
                    buttonY += colorComponent.height;
                    buttonHeight += colorComponent.height;
                }

                if (s instanceof SettingDouble) {
                    NumberComponent numberComponent = new NumberComponent(this, x, y + buttonHeight + buttonY, (SettingDouble) s);
                    numberComponents.add(numberComponent);
                    buttonY += numberComponent.height;
                    buttonHeight += numberComponent.height;
                }

                if (s instanceof SettingInteger) {
                    NumberIntComponent numberIntComponent = new NumberIntComponent(this, x, y + buttonHeight + buttonY, (SettingInteger) s);
                    numberIntComponents.add(numberIntComponent);
                    buttonY += numberIntComponent.height;
                    buttonHeight += numberIntComponent.height;
                }

                if (s instanceof SettingMode) {
                    ModeComponent modeComponent = new ModeComponent(this, x, y + buttonHeight + buttonY, (SettingMode) s);
                    modeComponents.add(modeComponent);
                    buttonY += modeComponent.height;
                    buttonHeight += modeComponent.height;
                }

                if(s instanceof SettingScreen){
                    SettingScreenComponent settingScreenComponent = new SettingScreenComponent(this, x, y + buttonHeight + buttonY, (SettingScreen) s);
                    settingScreenComponents.add(settingScreenComponent);
                    buttonY += settingScreenComponent.height;
                    buttonHeight += settingScreenComponent.height;
                }
            }

        buttonHeight = buttonY;
    }

    @Override
    public void render(int mouseX, int mouseY) {
        boolean isLastModule = ModuleManager.INSTANCE.getModulesInCategory(module.getCategory()).indexOf(module) == ModuleManager.INSTANCE.getModulesInCategory(module.getCategory()).size() - 1;
        Color moduleColor = new Color(InteligentGui.INSTANCE.red.getValue().intValue(), InteligentGui.INSTANCE.green.getValue().intValue(), InteligentGui.INSTANCE.blue.getValue().intValue(), InteligentGui.INSTANCE.alpha.getValue().intValue());

        Gui.drawRect(x, y, x + width, y + 5, new Color(13, 13, 23, 255).getRGB());

        if (isLastModule) {
            RenderUtil.drawRoundedRect(x, y, width, 13, radius, new Color(13, 13, 23, 255));
        } else {
            Gui.drawRect(x, y, x + width, y + 13, new Color(13, 13, 23, 255).getRGB());
        }

        if (mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY <= y + 13) {
            if (isLastModule) {
                RenderUtil.drawRoundedRect(x, y, width, 13, radius, new Color(13, 13, 23, 182).brighter());
            } else {
                Gui.drawRect(x, y, x + width, y + 13, new Color(13, 13, 23, 182).brighter().getRGB());
            }
        }

        if (module.isEnabled()) {
            if (isLastModule) {
                Gui.drawRect(x, y, x + width, y + 5, moduleColor.getRGB());
                RenderUtil.drawRoundedRect(x, y, width, 13, radius, moduleColor);
                if (mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY <= y + 13) {
                    RenderUtil.drawRoundedRect(x, y, width, 13, radius, moduleColor.darker());
                }
            } else {
                Gui.drawRect(x, y, x + width, y + 13, moduleColor.getRGB());
                if (mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY <= y + 13) {
                    Gui.drawRect(x, y, x + width, y + 13, moduleColor.darker().getRGB());
                }
            }
        }

        GL11.glPushMatrix();
        GL11.glScalef(0.5f, 0.5f, 0.5f);
        mc.fontRenderer.drawStringWithShadow(module.getName(), (this.x + 2) * 2 + 5, (this.y + 2.5f) * 2 + 5, -1);
        GL11.glPopMatrix();

        //draw vertical line in left side
        if(isLastModule){
            RenderUtil.drawLine(x, y, x, y + 7,1.5f, moduleColor.getRGB());
        } else {
            RenderUtil.drawLine(x, y, x, y + 13,1.5f, moduleColor.getRGB());
        }

        if (open) {
            buttonHeight = 0;
            height = 13;
            radius = 0;

            RenderUtil.drawLine(x, y, x, y + 13,1.5f, moduleColor.getRGB());
            RenderUtil.drawLine(x - 0.3f, y + 13, x + 2.5f, y + 13, 1.5f, new Color(InteligentGui.INSTANCE.red.getValue().intValue(), InteligentGui.INSTANCE.green.getValue().intValue(), InteligentGui.INSTANCE.blue.getValue().intValue(), InteligentGui.INSTANCE.alpha.getValue().intValue()).getRGB());

            for (CategorySettingComponent sc : categorySettingComponents) {
                sc.x = x;
                sc.y = y + height + buttonHeight;
                sc.render(mouseX, mouseY);
                height += sc.height;
            }

            for (BooleanComponent sc : booleanComponents) {
                if (!sc.notVisible) {
                    sc.x = x;
                    sc.y = y + height + buttonHeight;
                    sc.render(mouseX, mouseY);
                    height += 13;
                } else {
                    sc.x = x;
                    sc.y = 0;
                    sc.height = 0;
                }
            }

            for (ColorComponent nc : colorComponents) {
                    nc.x = x;
                    nc.y = y + height + buttonHeight;
                    nc.render(mouseX, mouseY);
                    height += nc.height;
            }

            for (NumberComponent nc : numberComponents) {
                if (!nc.notVisible) {
                    nc.x = x;
                    nc.y = y + height + buttonHeight;
                    nc.render(mouseX, mouseY);
                    height += 13;
                } else {
                    nc.x = x;
                    nc.y = 0;
                    nc.height = 0;
                }
            }

            for(NumberIntComponent nc : numberIntComponents) {
            	if(!nc.notVisible) {
            		nc.x = x;
            		nc.y = y + height + buttonHeight;
            		nc.render(mouseX, mouseY);
            		height += 13;
            	} else {
            		nc.x = x;
            		nc.y = 0;
            		nc.height = 0;
            	}
            }

            for (SettingScreenComponent sc : settingScreenComponents) {
            		sc.x = x;
            		sc.y = y + height + buttonHeight;
            		sc.render(mouseX, mouseY);
            		height += 13;
            }

            for (ModeComponent mc : modeComponents) {
                if(!mc.notVisible) {
                    mc.x = x;
                    mc.y = y + height + buttonHeight;
                    mc.render(mouseX, mouseY);
                    height += 13;
                } else {
                    mc.x = x;
                    mc.y = 0;
                    mc.height = 0;
                }
            }

            keyBindComponent.x = x;
            keyBindComponent.y = y + height + buttonHeight;
            keyBindComponent.render(mouseX, mouseY);
            height += 13;
        } else {
            buttonHeight = 0;
            height = 13;
            radius = 5;
        }
    }


    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        if (mouseX > x && mouseX < x + width && mouseY > y && mouseY < y + 13) {
            if (mouseButton == 0) {
                module.toggle();
            }
            if (mouseButton == 1) {
                open = !open;
            }
        }

        for (CategorySettingComponent sc : categorySettingComponents) {
            sc.mouseClicked(mouseX, mouseY, mouseButton);
        }

        for (BooleanComponent sc : booleanComponents) {
            sc.mouseClicked(mouseX, mouseY, mouseButton);
        }

        for (ColorComponent sc : colorComponents) {
            sc.mouseClicked(mouseX, mouseY, mouseButton);
        }

        for (NumberComponent nc : numberComponents) {
            nc.mouseClicked(mouseX, mouseY, mouseButton);
        }

        for(NumberIntComponent nc : numberIntComponents) {
        	nc.mouseClicked(mouseX, mouseY, mouseButton);
        }

        for (ModeComponent mc : modeComponents) {
            mc.mouseClicked(mouseX, mouseY, mouseButton);
        }

        for (SettingScreenComponent ssc : settingScreenComponents) {
            ssc.mouseClicked(mouseX, mouseY, mouseButton);
        }

        if (open) {
            keyBindComponent.mouseClicked(mouseX, mouseY, mouseButton);
        }
    }

    @Override
    public void mouseReleased(int mouseX, int mouseY, int state) {
        for (NumberComponent sc : numberComponents) {
            sc.mouseReleased(mouseX, mouseY, state);
        }

        for(NumberIntComponent nc : numberIntComponents) {
        	nc.mouseReleased(mouseX, mouseY, state);
        }

        for (ColorComponent sc : colorComponents) {
            sc.mouseReleased(mouseX, mouseY, state);
        }
    }

    public void keyTyped(char typedChar, int keyCode) {
        if (open) {
            keyBindComponent.keyTyped(typedChar, keyCode);
        }
    }
}
