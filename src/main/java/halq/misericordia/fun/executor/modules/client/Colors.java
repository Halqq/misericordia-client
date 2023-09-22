package halq.misericordia.fun.executor.modules.client;

import halq.misericordia.fun.core.modulecore.Category;
import halq.misericordia.fun.core.modulecore.Module;
import halq.misericordia.fun.executor.settings.SettingBoolean;
import halq.misericordia.fun.executor.settings.SettingInteger;
import halq.misericordia.fun.utils.utils.ColorUtil;

import java.awt.*;

/**
 * @author accessmodifier364
 * @since 25-Nov-2021
 */

public class Colors extends Module {

    public SettingInteger red = create("Red", 127, 0, 255);
    public SettingInteger green = create("Green", 0, 0, 255);
    public SettingInteger blue = create("Blue", 127, 0, 255);
    public SettingInteger alpha = create("Alpha", 128, 0, 255);
    SettingBoolean rainbow = create("Rainbow", true);
    SettingInteger saturation = create("Saturation", 60, 1, 100);
    SettingInteger brightness = create("Brightness", 100, 1, 100);
    SettingInteger speed = create("Speed", 40, 1, 100);

    public Colors() {
        super("Colors", Category.OTHER);
    }

    @Override
    public void onUpdate() {
        if (rainbow.getValue()) {
            Color color = new Color(ColorUtil.getRainbow(speed.getValue() * 100, 0, (float) saturation.getValue() / 100.0f, (float) brightness.getValue() / 100.0f));
            red.setValue(color.getRed());
            green.setValue(color.getGreen());
            blue.setValue(color.getBlue());
        }
    }

    public Color getColor() {
        return new Color(red.getValue(), green.getValue(), blue.getValue(), alpha.getValue());
    }
}
