package halq.misericordia.fun.executor.modules.movement;

import halq.misericordia.fun.core.modulecore.Category;
import halq.misericordia.fun.core.modulecore.Module;

public class NoClip extends Module {

    public NoClip() {
        super("NoClip", Category.MOVEMENT);
    }

    @Override
    public void onUpdate() {
        mc.player.noClip = true;
    }

    @Override
    public void onDisable() {
        mc.player.noClip = false;
    }
}
