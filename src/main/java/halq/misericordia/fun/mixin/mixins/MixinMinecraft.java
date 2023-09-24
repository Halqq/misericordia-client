package halq.misericordia.fun.mixin.mixins;

import halq.misericordia.fun.gui.main.GuiMainMenuScreen;
import halq.misericordia.fun.managers.config.ConfigManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiListWorldSelection;
import net.minecraft.client.gui.GuiMainMenu;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiWorldSelection;
import net.minecraft.crash.CrashReport;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * @author accessmodifier364
 * @since 24-Nov-2021
 */

@Mixin(value = {Minecraft.class})
public class MixinMinecraft {

    @Inject(method = "crashed", at = @At("HEAD"))
    public void crashed(CrashReport crash, CallbackInfo callbackInfo) {
        ConfigManager.INSTANCE.saveConfigs();
    }

    @Inject(method = "shutdown", at = @At("HEAD"))
    public void shutdown(CallbackInfo callbackInfo) {
        ConfigManager.INSTANCE.saveConfigs();
    }

    @Inject(method={"runTick()V"}, at={@At(value="RETURN")})
    private void runTick(CallbackInfo callbackInfo) {
        if (Minecraft.getMinecraft().currentScreen instanceof GuiMainMenu) {
           // Minecraft.getMinecraft().displayGuiScreen((GuiScreen)new GuiMainMenuScreen());

        }
    }

    @Inject(method={"displayGuiScreen"}, at={@At(value="HEAD")})
    private void displayGuiScreen(GuiScreen screen, CallbackInfo ci) {
        if (screen instanceof GuiMainMenu) {
          //  this.displayGuiScreen(new GuiMainMenuScreen(), ci);
        }
    }
}