package halq.misericordia.fun.executor.modules.combat.crystalaura.module;

import halq.misericordia.fun.core.modulecore.Category;
import halq.misericordia.fun.core.modulecore.Module;
import halq.misericordia.fun.events.PacketEvent;
import halq.misericordia.fun.events.RenderEvent;
import halq.misericordia.fun.executor.modules.combat.crystalaura.CrystalAuraPredict;
import halq.misericordia.fun.executor.modules.combat.crystalaura.CrystalAuraRender;
import halq.misericordia.fun.executor.modules.combat.crystalaura.calcs.CrystalAuraCalcPos;
import halq.misericordia.fun.executor.modules.combat.crystalaura.calcs.CrystalAuraCalcs;
import halq.misericordia.fun.executor.settings.*;
import halq.misericordia.fun.utils.utils.TimerUtil;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.network.play.client.CPacketPlayerTryUseItemOnBlock;
import net.minecraft.network.play.client.CPacketUseEntity;
import net.minecraft.network.play.server.SPacketSoundEffect;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.common.FMLLog;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.Arrays;
import java.util.Comparator;

/**
 * @author Halq
 * @since 10/06/2023 at 03:55
 */

public class CrystalAuraModule extends Module implements Runnable {

    public static CrystalAuraModule INSTANCE;
    public static CrystalAuraCalcPos.CalcPos crystalPosCalc = new CrystalAuraCalcPos.CalcPos(BlockPos.ORIGIN, 0);
    public static CrystalAuraModule instance = new CrystalAuraModule();
    static CrystalAuraModule crystalAuraModule;
    private final TimerUtil threadDelay = new TimerUtil();
    public SettingCategory settings = create("CrystalAura", "Break", Arrays.asList("Break", "Place", "Render", "MultiThread", "AutoSwitch", "Rotations", "Misc"), 1);
    public SettingBoolean place = create("Place", true, false);
    public SettingBoolean breakCrystal = create("Break", true, false);
    public SettingBoolean attackPredict = create("AttackPredict", true, false);
    public SettingBoolean autoSwitch = create("AutoSwitch", false, false);
    public SettingBoolean handAnimations = create("HandAnimations", false, false);
    public SettingBoolean rotations = create("Rotate", true, false);
    public SettingBoolean multiPlace = create("MultiPlace", true, false);
    public SettingBoolean multiThread = create("MultiThread", true, false);
    public SettingInteger multiThreadValue = create("MultiThreadValue", 2, 1, 4, false);
    public SettingDouble multiThreadDelay = create("MultiThreadDelay", 0.0, 0, 60, false);
    public SettingBoolean pauseOnGap = create("PauseGap", false, false);
    public SettingBoolean pauseOnXp = create("PauseOnXp", false, false);
    public SettingDouble minHealth = create("MinHealth", 36.0, 0.0, 36.0, false);
    public SettingDouble range = create("Range", 4.0, 0.0, 6.0, false);
    public SettingDouble minDmg = create("MinDmg", 4.0, 0.0, 36.0, false);
    public SettingDouble maxDmg = create("MaxSelfDmg", 0.0, 0.0, 36.0, false);
    public SettingInteger ppt = create("PPT", 2, 0, 10, false);
    public SettingInteger apt = create("APT", 2, 0, 10, false);
    public SettingMode placeMode = create("PlaceMode", "Packet", Arrays.asList("Normal", "Packet"), false);
    public SettingMode breakMode = create("BreakMode", "Packet", Arrays.asList("Normal", "Packet"), false);
    public SettingMode rotateMode = create("RotateMode", "Silent", Arrays.asList("Normal", "Silent"), false);
    public SettingMode autoSwitchMode = create("AutoSwitchMode", "Silent", Arrays.asList("Normal", "Silent"), false);
    public SettingBoolean render = create("Render", true, false);
    public SettingInteger red = create("Red", 0, 0, 255, false);
    public SettingInteger green = create("Green", 130, 0, 255, false);
    public SettingInteger blue = create("Blue", 255, 0, 255, false);
    public SettingInteger alpha = create("Alpha", 255, 0, 255, false);
    EntityPlayer targetPlayer;
    Thread thread;
    BlockPos finalPos;
    float targetDMG;
    long lastPacketTime = 0;



    public CrystalAuraModule() {
        super("CrystalAura", Category.COMBAT);
        INSTANCE = this;
    }

    @Override
    public void onSetting() {
        CrystalAuraSettings.caSettings();
    }

    @Override
    public void onEnable() {
        crystalPosCalc = new CrystalAuraCalcPos.CalcPos(BlockPos.ORIGIN, 0);
    }

    @SubscribeEvent
    public void onPacketReceive(PacketEvent.PacketReceiveEvent event) {
        if (event.getPacket() instanceof SPacketSoundEffect) {
            final SPacketSoundEffect packet = (SPacketSoundEffect) event.getPacket();
            if (packet.getCategory() == SoundCategory.BLOCKS && packet.getSound() == SoundEvents.ENTITY_GENERIC_EXPLODE) {
                for (Entity e : mc.world.loadedEntityList) {
                    if (e instanceof EntityEnderCrystal) {
                        if (e.getDistance(packet.getX(), packet.getY(), packet.getZ()) <= 6.0f) {
                            e.setDead();
                        }
                    }
                }
            }

            if (place.getValue()) {
                if (packet.getCategory() == SoundCategory.BLOCKS && packet.getSound() == SoundEvents.ENTITY_GENERIC_EXPLODE) {
                    for (Entity e : mc.world.loadedEntityList) {
                        if (e instanceof EntityEnderCrystal) {
                            if (e.getDistance(packet.getX(), packet.getY(), packet.getZ()) <= 6.0f) {
                                e.setDead();
                            }
                        }
                    }
                }
            }

            if (breakCrystal.getValue()) {
                if (packet.getCategory() == SoundCategory.BLOCKS && packet.getSound() == SoundEvents.ENTITY_GENERIC_EXPLODE) {
                    for (Entity e : mc.world.loadedEntityList) {
                        if (e instanceof EntityEnderCrystal) {
                            if (e.getDistance(packet.getX(), packet.getY(), packet.getZ()) <= 6.0f) {
                                e.setDead();
                            }
                        }
                    }
                }
            }
        }

        if (event.getPacket() instanceof CPacketUseEntity) {
            final CPacketUseEntity packet = (CPacketUseEntity) event.getPacket();
            if (packet.getAction() == CPacketUseEntity.Action.ATTACK) {
                if (packet.getEntityFromWorld(mc.world) instanceof EntityEnderCrystal) {
                    if (attackPredict.getValue()) {
                        if (targetPlayer != null) {
                            if (targetPlayer.getDistance(packet.getEntityFromWorld(mc.world)) <= 6.0f) {
                                targetPlayer = null;
                                return;
                            }
                        }
                    }
                    if (breakCrystal.getValue()) {
                        if (multiThread.getValue()) {
                            if (thread == null) {
                                thread = new Thread(this);
                                thread.start();
                            }
                        } else {
                            caAttack();
                        }
                    }
                }
            }
        }
    }

    @Override
    public void onUpdate() {
        for (EntityPlayer player : mc.world.playerEntities) {
            if (player != mc.player) {
                targetPlayer = player;
            }
        }

        if (pauseOnGap.getValue() && isEatingGap() || pauseOnXp.getValue() && isUsingXp() || mc.player.getHealth() <= minHealth.getValue())
            return;

        if (!isEnabled())
            return;

        if (breakCrystal.getValue()) {
            new Thread(() -> {
                synchronized (CrystalAuraModule.this) {
                    for (int i = 0; i < apt.getValue(); i++) {
                        caAttack();
                    }
                }
            }).start();
        }

        if (place.getValue()) {
            caPlace();
        }

        if (attackPredict.getValue()) {
            new Thread(CrystalAuraPredict::caAttackPredict).start();
        }
    }

    public void caPlace() {
        crystalPosCalc = CrystalAuraCalcs.calculatePositions(targetPlayer);
        long currentTime = System.currentTimeMillis();
        int currentPing = getPing();
        int maxDelay = calculateMaxDelay(currentPing);

        if (rotations.getValue()) {
            switch (rotateMode.getValue()) {
                case "Silent":
                    rotateSilent(crystalPosCalc.getBlockPos());
                    break;
                case "Normal":
                    rotate(crystalPosCalc.getBlockPos());
            }
        }

        if (crystalPosCalc.getBlockPos() != BlockPos.ORIGIN) {
            switch (placeMode.getValue()) {
                case "Normal":
                    mc.playerController.processRightClickBlock(mc.player, mc.world, crystalPosCalc.getBlockPos(), EnumFacing.UP, new Vec3d(0, 0, 0), EnumHand.MAIN_HAND);
                    break;
                case "Packet":
                        mc.player.connection.sendPacket(new CPacketPlayerTryUseItemOnBlock(crystalPosCalc.getBlockPos(), EnumFacing.UP, EnumHand.MAIN_HAND, 0, 0, 0));
                        if (handAnimations.getValue()) {
                            mc.player.swingArm(getHand());
                        }
                        lastPacketTime = currentTime;
                    break;
            }
        }
        finalPos = crystalPosCalc.getBlockPos();
        targetDMG = crystalPosCalc.getTargetDamage();
    }

    private int getPing() {
        int ping = -1;

        if (mc.player != null && mc.player.connection != null) {
            ping = mc.player.connection.getPlayerInfo(mc.player.getUniqueID()).getResponseTime();
        }

        return ping;
    }

    private int calculateMaxDelay(int currentPing) {
        int maxDelay = 0;

        if (currentPing >= 0) {
            if(currentPing < 25){
                maxDelay = 50;
            } else if (currentPing < 50) {
                maxDelay = 100;
            } else if (currentPing < 100) {
                maxDelay = 150;
            } else {
                maxDelay = 300;
            }
        }

        return maxDelay;
    }

    public void switchToSlot(int slot) {
        mc.player.inventory.currentItem = slot;
        mc.playerController.updateController();
    }

    public void switchSlot() {
        for (int i = 0; i < 9; i++) {
            if (mc.player.inventory.getStackInSlot(i).getItem().getIdFromItem(mc.player.inventory.getStackInSlot(i).getItem()) == Item.getIdFromItem(Items.END_CRYSTAL)) {
                switchToSlot(i);
                break;
            }
        }
    }

    public void caAttack() {
        final EntityEnderCrystal crystal = (EntityEnderCrystal) mc.world.loadedEntityList.stream().filter(entity -> entity instanceof EntityEnderCrystal).min(Comparator.comparing(c -> mc.player.getDistance(c))).orElse(null);

        if (crystal != null) {
            switch (breakMode.getValue()) {
                case "Normal":
                    mc.playerController.attackEntity(mc.player, crystal);
                    break;
                case "Packet":
                    mc.player.connection.sendPacket(new CPacketUseEntity(crystal));
                    if (handAnimations.getValue()) {
                        mc.player.swingArm(getHand());
                    }

            }
        }
    }

    @SubscribeEvent
    public void onPacketSend(PacketEvent.PacketSendEvent event) {
        if (event.getPacket() instanceof CPacketUseEntity) {
            CPacketUseEntity packet = (CPacketUseEntity) event.getPacket();
            if (packet.getAction() == CPacketUseEntity.Action.ATTACK) {
                if (packet.getEntityFromWorld(mc.world) instanceof EntityEnderCrystal) {
                    EntityEnderCrystal crystal = (EntityEnderCrystal) packet.getEntityFromWorld(mc.world);
                    assert crystal != null;
                    if (crystal.getDistance(mc.player) <= 6.0f) {
                        crystal.setDead();
                        event.setCanceled(true);
                    }
                }
            }
        }

        if (event.getPacket() instanceof CPacketUseEntity) {
            if (((CPacketUseEntity) event.getPacket()).getAction() == CPacketUseEntity.Action.ATTACK) {
                if (((CPacketUseEntity) event.getPacket()).getEntityFromWorld(mc.world) instanceof EntityEnderCrystal) {
                    if (mc.player.getDistance(((CPacketUseEntity) event.getPacket()).getEntityFromWorld(mc.world)) <= 6.0f) {
                        event.setCanceled(true);
                    }
                }
            }
        }

        if (event.getPacket() instanceof CPacketPlayerTryUseItemOnBlock) {
            CPacketPlayerTryUseItemOnBlock packet = (CPacketPlayerTryUseItemOnBlock) event.getPacket();
            if (packet.getHand() == EnumHand.MAIN_HAND && packet.getPos().getY() > 1 && packet.getPos().getY() < 255 && mc.player.getHeldItemMainhand().getItem() == Items.END_CRYSTAL) {
                if (packet.getDirection() == EnumFacing.UP) {
                    if (packet.getPos().getX() == finalPos.getX() && packet.getPos().getZ() == finalPos.getZ()) {
                        if (packet.getPos().getY() == finalPos.getY() + 1) {
                            mc.player.swingArm(EnumHand.MAIN_HAND);
                        }
                        if (place.getValue()) {
                            mc.player.connection.sendPacket(new CPacketPlayerTryUseItemOnBlock(packet.getPos(), packet.getDirection(), EnumHand.MAIN_HAND, packet.getFacingX(), packet.getFacingY(), packet.getFacingZ()));
                        }
                    }
                }
            }
        }

        if (event.getPacket() instanceof CPacketPlayerTryUseItemOnBlock && place.getValue()) {
            CPacketPlayerTryUseItemOnBlock packet = (CPacketPlayerTryUseItemOnBlock) event.getPacket();
            if (packet.getHand() == EnumHand.MAIN_HAND && packet.getPos().getY() > 1 && packet.getPos().getY() < 255 && mc.player.getHeldItemMainhand().getItem() == Items.END_CRYSTAL) {
                event.setCanceled(true);
            }
        }
    }

    public boolean isEatingGap() {
        return mc.player.getHeldItemMainhand().getItem() == Items.GOLDEN_APPLE && mc.player.isHandActive();
    }

    public boolean isUsingXp() {
        return mc.player.getHeldItemMainhand().getItem() == Items.EXPERIENCE_BOTTLE && mc.player.isHandActive();
    }

    public EnumHand getHand() {
        if (mc.player.getHeldItemMainhand().getItem() == Items.END_CRYSTAL) {
            return EnumHand.MAIN_HAND;
        } else if (mc.player.getHeldItemOffhand().getItem() == Items.END_CRYSTAL) {
            return EnumHand.OFF_HAND;
        } else {
            return EnumHand.MAIN_HAND;
        }
    }

    private void rotateSilent(BlockPos blockPos) {
        if (rotations.getValue()) {
            float[] angles = getRotations(blockPos);
            mc.player.connection.sendPacket(new CPacketPlayer.Rotation(angles[0], angles[1], mc.player.onGround));
        }
    }

    private void rotate(BlockPos blockPos) {
        float[] rotations = getRotations(blockPos);
        mc.player.rotationYaw = rotations[0];
        mc.player.rotationPitch = rotations[1];
    }

    private float[] getRotations(BlockPos pos) {
        double xDiff = pos.getX() + 0.5 - mc.player.posX;
        double yDiff = (pos.getY() + 0.5) * 0.9 - (mc.player.posY + mc.player.getEyeHeight());
        double zDiff = pos.getZ() + 0.5 - mc.player.posZ;
        double distance = Math.sqrt(xDiff * xDiff + zDiff * zDiff);

        float yaw = (float) Math.toDegrees(-Math.atan2(xDiff, zDiff));
        float pitch = (float) -Math.toDegrees(Math.atan2(yDiff, distance));

        return new float[]{yaw, pitch};
    }

    @Override
    public void run() {
        if (multiThread.getValue()) {
            newThread();
        }
    }

    public void newThread() {
        for (int i = 0; i <= multiThreadValue.getValue(); i++) {
            if (threadDelay.passedMs(multiThreadDelay.getValue().longValue())) {
                if (thread == null || thread.getState() == Thread.State.TERMINATED) {
                    thread = new Thread(() -> {
                        synchronized (CrystalAuraModule.this) {
                            caAttack();
                        }
                    });
                    thread.setName("CrystalAura-" + i);
                    thread.setPriority(Thread.MAX_PRIORITY);
                    thread.start();
                    threadDelay.reset();
                }
            }
        }
    }

    @Override
    public void onRender3D(RenderEvent event) {
        if (finalPos != BlockPos.ORIGIN && render.getValue() && finalPos != null && targetDMG != 0) {
            CrystalAuraRender.render(finalPos, targetDMG, red.getValue().floatValue() / 255f, green.getValue().floatValue() / 255f, blue.getValue().floatValue() / 255f, alpha.getValue().floatValue() / 255f);
        }
    }
}
