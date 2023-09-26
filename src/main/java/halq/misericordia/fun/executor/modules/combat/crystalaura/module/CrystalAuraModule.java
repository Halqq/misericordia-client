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
import net.minecraft.item.Item;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.network.play.client.CPacketPlayerTryUseItemOnBlock;
import net.minecraft.network.play.client.CPacketUseEntity;
import net.minecraft.network.play.server.SPacketSpawnObject;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author Halq
 * @since 10/06/2023 at 03:55
 */

public class CrystalAuraModule extends Module {

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
    public SettingDouble placeRange = create("PlaceRange", 4.0, 0.0, 6.0, false);
    public SettingDouble breakRange = create("BreakRange", 4.0, 0.0, 6.0, false);
    public SettingDouble playerRange = create("PlayerRange", 4.0, 0.0, 6.0, false);

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
    private static int predictID = 0;
    private final ExecutorService executorService = Executors.newFixedThreadPool(4);
    boolean isPredictedBreak = false;

    public CrystalAuraModule() {
        super("CrystalAura", Category.COMBAT);
        INSTANCE = this;
    }

    private List<BlockPos> convertCalcPosToList(CrystalAuraCalcPos.CalcPos calcPos) {
        return Collections.singletonList(calcPos.getBlockPos());
    }

    @Override
    public void onSetting() {
        CrystalAuraSettings.caSettings();
    }

    @Override
    public void onEnable() {
        crystalPosCalc = new CrystalAuraCalcPos.CalcPos(BlockPos.ORIGIN, 0);
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
            startAsyncAttack();
        }

        if (place.getValue()) {
            caPlace();
        }
    }

    private void caPlace() {
        CrystalAuraCalcPos.CalcPos crystalPosCalc = CrystalAuraCalcs.calculatePositions(targetPlayer);
        List<BlockPos> crystalPositions = convertCalcPosToList(crystalPosCalc);

        BlockPos crystalBlockPos = crystalPositions.get(0);
        List<CPacketPlayerTryUseItemOnBlock> packets = new ArrayList<>();


        if (rotations.getValue()) {
            switch (rotateMode.getValue()) {
                case "Silent":
                    rotateSilent(crystalPosCalc.getBlockPos());
                    break;
                case "Normal":
                    rotate(crystalPosCalc.getBlockPos());
            }
        }

        if (crystalBlockPos != BlockPos.ORIGIN) {
            switch (placeMode.getValue()) {
                case "Normal":
                    mc.playerController.processRightClickBlock(mc.player, mc.world, crystalBlockPos, EnumFacing.UP, new Vec3d(0, 0, 0), EnumHand.MAIN_HAND);
                    break;
                case "Packet":
                    packets.add(new CPacketPlayerTryUseItemOnBlock(crystalBlockPos, EnumFacing.UP, EnumHand.MAIN_HAND, 0, 0, 0));
                    sendCrystalPlacementPacketAsync(packets);
                    break;
            }
        }

        finalPos = crystalPosCalc.getBlockPos();
        targetDMG = crystalPosCalc.getTargetDamage();
    }


    private void sendCrystalPlacementPacketAsync(List<CPacketPlayerTryUseItemOnBlock> packets) {
        int batchSize = calculateBatchSize(packets.size());
        int numOfBatches = (int) Math.ceil((double) packets.size() / batchSize);
        boolean handAnimationsValue = handAnimations.getValue();

        for (int i = 0; i < numOfBatches; i++) {
            int startIndex = i * batchSize;
            int endIndex = Math.min((i + 1) * batchSize, packets.size());

            List<CPacketPlayerTryUseItemOnBlock> batch = packets.subList(startIndex, endIndex);

            CompletableFuture.runAsync(() -> {

                for (CPacketPlayerTryUseItemOnBlock packet : batch) {
                    try {
                        mc.player.connection.sendPacket(packet);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        }
        if (handAnimationsValue) {
            mc.player.swingArm(EnumHand.MAIN_HAND);
        }
    }

    private int calculateBatchSize(int packetCount) {
        double desiredPacketRate = 3.0;
        int minimumBatchSize = 2;

        double batchSize = Math.ceil(packetCount / desiredPacketRate);

        return (int) Math.max(minimumBatchSize, batchSize);
    }

    @SubscribeEvent
    public void onPacketReceive(PacketEvent.PacketReceiveEvent event) {
        if (event.getPacket() instanceof SPacketSpawnObject) {
            SPacketSpawnObject packet = (SPacketSpawnObject) event.getPacket();
            if (packet.getType() == 51) {
                if (mc.player.getDistance(packet.getX(), packet.getY(), packet.getZ()) <= 6) {
                    predictID = packet.getEntityID();
                    isPredictedBreak = false;
                }
            }
        }
    }

    @SubscribeEvent
    public void onPacketSend(PacketEvent.PacketSendEvent event) {
        if (event.getPacket() instanceof CPacketUseEntity) {
            CPacketUseEntity packet = (CPacketUseEntity) event.getPacket();
            if (packet.getAction() == CPacketUseEntity.Action.ATTACK) {
                Entity entity = packet.getEntityFromWorld(mc.world);
                if (entity instanceof EntityEnderCrystal && predictID == entity.getEntityId() && !isPredictedBreak) {

                    sendBreakPacketAsync((EntityEnderCrystal) entity);
                    // MessageUtil.sendMessage("Predicted!");
                    isPredictedBreak = true;

                }
            }
        }
    }

    public void startAsyncAttack() {
        EntityEnderCrystal crystal = (EntityEnderCrystal) findNearestCrystal();

        executorService.execute(() -> {
            if (crystal != null) {
                sendBreakPacketAsync(crystal);
            }
        });
    }

    private Entity findNearestCrystal() {
        double closestDistanceSq = Double.MAX_VALUE;
        Entity nearestCrystal = null;

        for (Entity entity : mc.world.loadedEntityList) {
            if (entity instanceof EntityEnderCrystal) {
                double distanceSq = entity.getDistanceSq(mc.player);
                if (distanceSq < closestDistanceSq) {
                    closestDistanceSq = distanceSq;
                    nearestCrystal = entity;
                }
            }
        }

        return nearestCrystal;
    }

    private void breakCrystal(EntityEnderCrystal crystal) {
        mc.player.connection.sendPacket(new CPacketUseEntity(crystal));

        if (handAnimations.getValue()) {
            mc.player.swingArm(getHand());
        }
    }

    private void sendBreakPacketAsync(EntityEnderCrystal crystal) {
        CompletableFuture.runAsync(() -> {
            try {
                breakCrystal(crystal);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
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
    public void onRender3D(RenderEvent event) {
        if (finalPos != BlockPos.ORIGIN && render.getValue() && finalPos != null && targetDMG != 0) {
            CrystalAuraRender.render(finalPos, targetDMG, red.getValue().floatValue() / 255f, green.getValue().floatValue() / 255f, blue.getValue().floatValue() / 255f, alpha.getValue().floatValue() / 255f);
        }
    }
}
