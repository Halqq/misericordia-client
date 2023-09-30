package halq.misericordia.fun.managers.module;

import halq.misericordia.fun.core.modulecore.Category;
import halq.misericordia.fun.core.modulecore.Module;
import halq.misericordia.fun.events.RenderEvent;
import halq.misericordia.fun.executor.modules.client.*;
import halq.misericordia.fun.executor.modules.combat.*;
import halq.misericordia.fun.executor.modules.combat.aimbot.AimBot;
import halq.misericordia.fun.executor.modules.combat.anvilaura.AnvilAura;
import halq.misericordia.fun.executor.modules.combat.trap.AutoTrap;
import halq.misericordia.fun.executor.modules.combat.crystalaura.module.CrystalAuraModule;
import halq.misericordia.fun.executor.modules.combat.holefiller.module.HoleFillerModule;
import halq.misericordia.fun.executor.modules.combat.killaura.KillAura;
import halq.misericordia.fun.executor.modules.combat.surround.Surround;
import halq.misericordia.fun.executor.modules.combat.trap.SelfTrap;
import halq.misericordia.fun.executor.modules.exploits.*;
import halq.misericordia.fun.executor.modules.miscellaneous.*;
import halq.misericordia.fun.executor.modules.movement.*;
import halq.misericordia.fun.executor.modules.render.*;
import halq.misericordia.fun.executor.modules.render.chams.crystalchams.CrystalChamsModule;
import halq.misericordia.fun.executor.modules.render.fallpredict.module.FallPredictModule;
import halq.misericordia.fun.executor.modules.world.Notifier;
import halq.misericordia.fun.utils.Minecraftable;
import halq.misericordia.fun.utils.utils.MessageUtil;
import halq.misericordia.fun.utils.utils.RenderUtil;
import halq.misericordia.fun.executor.modules.render.tracers.Tracers;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent.KeyInputEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * @author accessmodifier364
 * @author halqq
 * @since 24-Nov-2021
 */

public class ModuleManager implements Minecraftable {

    public static ModuleManager INSTANCE;
    private final List<Module> modules;
    private final LinkedHashMap<Class<? extends Module>, Module> modules2;

    public ModuleManager() {
        MinecraftForge.EVENT_BUS.register(this);
        modules = new ArrayList<>();
        modules2 = new LinkedHashMap<>();
        addModule(new InteligentGui());
        addModule(new Colors());
        addModule(new CustomFont());
        addModule(new FullBright());
        addModule(new KillAura());
        addModule(new FakePlayer());
        addModule(new HandChams());
        addModule(new Tracers());
        addModule(new Breadcrumbs());
        addModule(new Surround());
        addModule(new Sprint());
        addModule(new CrystalAuraModule());
        addModule(new NoRender());
        addModule(new Console());
        addModule(new Script());
        addModule(new CrystalChamsModule());
        addModule(new BlockHighlight());
        addModule(new CrossHair());
        addModule(new PacketMine());
        addModule(new Notifier());
        addModule(new Burrow());
        addModule(new AnvilAura());
        addModule(new HoleFillerModule());
        addModule(new AimBot());
        addModule(new AutoWeb());
        addModule(new AutoTrap());
        addModule(new SelfTrap());
        addModule(new WebBreaker());
        addModule(new AutoFrameDupe());
        addModule(new AutoMount());
        addModule(new FakeCreative());
        addModule(new FreeCam());
        addModule(new MultiTask());
        addModule(new PingSpoof());
        addModule(new Timer());
        addModule(new AntiAfk());
        addModule(new AntiHunger());
        addModule(new AutoArmor());
        addModule(new AutoClicker());
        addModule(new AutoGG());
        addModule(new AutoXp());
        addModule(new ChatSuffix());
        addModule(new Spammer());
        addModule(new AntiWeb());
        addModule(new AutoWalk());
        addModule(new CreativeFly());
        addModule(new ElytraFly());
        addModule(new FastSwim());
        addModule(new Jesus());
        addModule(new NoFall());
        addModule(new NoSlow());
        addModule(new ReverseStep());
        addModule(new Velocity());
        addModule(new FallPredictModule());

    }

    private void addModule(Module module) {
        modules.add(module);
        modules2.put(module.getClass(), module);
    }

    public List<Module> getModules() {
        return modules;
    }

    public List<Module> getModulesInCategory(Category category) {
        List<Module> mods = new ArrayList<>();
        for (Module module : modules) {
            if (module.getCategory() == category) {
                mods.add(module);
            }
        }
        return mods;
    }

    public Module getModule(String name) {
        for (Module module : modules) {
            if (module.getName().equals(name)) {
                return module;
            }
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    public <T extends Module> T getModule(Class<T> clazz) { //IDK if is better create an instance method inside every module.
        return (T) modules2.get(clazz);
    }

    @SubscribeEvent
    public void onKey(KeyInputEvent event) {
        if (Keyboard.getEventKeyState()) {
            for (Module module : modules) {
                if (module.getKey() == Keyboard.getEventKey()) {
                    module.toggle();
                    MessageUtil.toggleMessage(module);
                }
            }
        }
    }

    public static void onEnable() {
        for (Module module : INSTANCE.modules) {
            MessageUtil.toggleMessage(module);
        }
    }

    public static void onUpdate() {
        for (Module m : INSTANCE.modules) {
            try {
                if (m.isEnabled())
                    m.onUpdate();
            } catch (Exception exception) {
                exception.printStackTrace();
            }
        }
    }
    @SubscribeEvent
    public void onTick(TickEvent.ClientTickEvent event) {
        if (ModuleManager.mc.player != null)
            if (ModuleManager.mc.world != null) {
                modules.stream().filter(Module::isEnabled).forEach(Module::onUpdate);
                for (Module m : INSTANCE.modules) {
                    m.onSetting();
                }
            }

        }

    @SubscribeEvent
    public final void onRender3D(RenderWorldLastEvent event) {
        mc.profiler.startSection("aurora");
        mc.profiler.startSection("setup");
        GlStateManager.disableTexture2D();
        GlStateManager.enableBlend();
        GlStateManager.disableAlpha();
        GlStateManager.tryBlendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, 1, 0);
        GlStateManager.shadeModel(GL11.GL_SMOOTH);
        GlStateManager.disableDepth();
        GlStateManager.glLineWidth(1f);
        Vec3d pos = getInterpolatedPos(mc.player, event.getPartialTicks());
        RenderEvent eventRender = new RenderEvent(RenderUtil.INSTANCE, pos, event.getPartialTicks());
        eventRender.resetTranslation();
        mc.profiler.endSection();
        for (Module modules : modules) {
            if (modules.isEnabled()) {
                mc.profiler.startSection(modules.getName());
                modules.onRender3D(eventRender);
                mc.profiler.endSection();
            }
        }
        mc.profiler.startSection("release");
        GlStateManager.glLineWidth(1f);
        GlStateManager.shadeModel(GL11.GL_FLAT);
        GlStateManager.disableBlend();
        GlStateManager.enableAlpha();
        GlStateManager.enableTexture2D();
        GlStateManager.enableDepth();
        GlStateManager.enableCull();
        RenderUtil.releaseGL();
        mc.profiler.endSection();
        mc.profiler.endSection();
    }

    public ArrayList<Module> get(Predicate<Module> predicate) {
        return modules.stream().filter(predicate).collect(Collectors.toCollection(ArrayList::new));
    }

    @SubscribeEvent
    public void onRender2D(RenderGameOverlayEvent.Text text) {
        ModuleManager.INSTANCE.get(Module::isEnabled).forEach(Module::onRender2D);
    }

    public Vec3d getInterpolatedPos(Entity entity, double ticks) {
        return new Vec3d(entity.lastTickPosX, entity.lastTickPosY, entity.lastTickPosZ).add(process(entity, ticks, ticks, ticks));
    }

    public Vec3d process(Entity entity, double x, double y, double z) {
        return new Vec3d(
                (entity.posX - entity.lastTickPosX) * x,
                (entity.posY - entity.lastTickPosY) * y,
                (entity.posZ - entity.lastTickPosZ) * z);
    }

}