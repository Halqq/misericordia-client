package halq.misericordia.fun.executor.modules.client;

import halq.misericordia.fun.core.modulecore.Category;
import halq.misericordia.fun.core.modulecore.Module;
import halq.misericordia.fun.executor.settings.SettingBoolean;
import halq.misericordia.fun.gui.console.ConsoleScreen;

/**
 * @author Halq
 * @since 17/06/2023 at 15:13
 */

public class Console extends Module {

    public static Console INSTANCE;
    public SettingBoolean blur = create("Blur", true);

    public Console() {
        super("Console", Category.OTHER);
        INSTANCE = this;
    }

    @Override
    public void onEnable() {
        mc.displayGuiScreen(new ConsoleScreen());
        setDisabled();
    }
}
