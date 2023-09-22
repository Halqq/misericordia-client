package halq.misericordia.fun.executor.modules.client;

import halq.misericordia.fun.core.modulecore.Category;
import halq.misericordia.fun.core.modulecore.Module;
import halq.misericordia.fun.executor.settings.SettingBoolean;
import halq.misericordia.fun.executor.settings.SettingInteger;
import halq.misericordia.fun.gui.igui.ClickGuiScreen;
import halq.misericordia.fun.gui.main.GuiMainMenuScreen;
import halq.misericordia.fun.utils.utils.ColorUtil;
import org.lwjgl.input.Keyboard;

import java.awt.*;

/**
 * @author Halq
 * @since 27/05/2023 at 16:54
 */

public class InteligentGui extends Module {

    public static InteligentGui INSTANCE;

    public SettingInteger red = create("Red", 133, 0, 255);
    public SettingInteger green = create("Green", 43, 0, 255);
    public SettingInteger blue = create("Blue", 255, 0, 255);
    public SettingInteger alpha = create("Alpha", 255, 0, 255);
    public SettingBoolean rainbow = create("Rainbow", false);

    public InteligentGui() {
        super("InteligentGui", Category.OTHER);
        if (getKey() == 0) {
            setKey(Keyboard.KEY_RSHIFT);
        }
        INSTANCE = this;
    }

    @Override
    public void onSetting() {
        if (rainbow.getValue()) {
            Color rainbowColor = new Color(ColorUtil.getRainbow(70 * 100, 0, (float) 100 / 100.0f, (float) 100 / 100.0f));
            red.setValue(rainbowColor.getRed());
            green.setValue(rainbowColor.getGreen());
            blue.setValue(rainbowColor.getBlue());
        }
    }

    @Override
    public void onEnable() {
        mc.displayGuiScreen(new ClickGuiScreen());
        setDisabled();
    }
}
