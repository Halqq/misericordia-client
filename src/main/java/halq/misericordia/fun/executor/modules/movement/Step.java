package halq.misericordia.fun.executor.modules.movement;

import halq.misericordia.fun.core.modulecore.Category;
import halq.misericordia.fun.core.modulecore.Module;
import halq.misericordia.fun.utils.Minecraftable;

/**
 * @author Halq
 * @since 10/06/2023 at 03:50
 */

public class Step extends Module {
    
        public static Step INSTANCE;
        public Step() {
            super("Step", Category.MOVEMENT);
            INSTANCE = this;
        }
    
        @Override
        public void onUpdate() {

        }
}