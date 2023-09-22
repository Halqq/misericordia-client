package halq.misericordia.fun.executor.modules.combat.aimbot;

import halq.misericordia.fun.core.modulecore.Category;
import halq.misericordia.fun.core.modulecore.Module;
import halq.misericordia.fun.executor.settings.SettingBoolean;
import halq.misericordia.fun.executor.settings.SettingColor;
import halq.misericordia.fun.executor.settings.SettingScreen;

import java.awt.*;

/**
 * @author Halq
 * @since 23/06/2023 at 17:55
 */

public class AimBotModule extends Module {

    SettingScreen screen = create("screen", new AimbotScreen(), true);
    SettingBoolean head = create("head", true, false);
    SettingBoolean chest = create("body", false, false);
    SettingBoolean legs = create("legs", false, false);
    SettingColor color = create("color", new Color(255, 0, 0, 255), false);

    public static AimBotModule INSTANCE;

    public AimBotModule() {
        super("Aimbot", Category.COMBAT);
        INSTANCE = this;
    }

    @Override
    public void onSetting() {
        if (head.getValue()) {
            chest.setValue(false);
            legs.setValue(false);
        } else if (chest.getValue()) {
            head.setValue(false);
            legs.setValue(false);
        } else if (legs.getValue()) {
            head.setValue(false);
            chest.setValue(false);
        }
    }

    @Override
    public void onDisable() {
        mc.displayGuiScreen(null);
    }
}
