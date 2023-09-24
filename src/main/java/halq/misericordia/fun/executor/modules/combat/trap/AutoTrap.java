package halq.misericordia.fun.executor.modules.combat.trap;

import com.mojang.realmsclient.gui.ChatFormatting;
import halq.misericordia.fun.core.modulecore.Category;
import halq.misericordia.fun.core.modulecore.Module;
import halq.misericordia.fun.executor.settings.SettingBoolean;
import halq.misericordia.fun.executor.settings.SettingDouble;
import halq.misericordia.fun.executor.settings.SettingMode;
import halq.misericordia.fun.utils.Minecraftable;
import halq.misericordia.fun.utils.utils.MessageUtil;
import net.minecraft.block.Block;
import net.minecraft.block.BlockObsidian;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.network.play.client.CPacketHeldItemChange;
import net.minecraft.network.play.client.CPacketPlayerTryUseItemOnBlock;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

import java.util.Arrays;

public class AutoTrap extends Module {

    SettingMode placeMode = create("PlaceMode", "Packet", Arrays.asList("Packet", "Normal"));
    SettingMode trapMode = create("TrapMode", "Full", Arrays.asList("Full", "Face", "Surround"));
    SettingBoolean rotate = create("Rotate", true);
    SettingDouble range = create("Range", 4.0, 0, 5);
    SettingBoolean disable = create("AutoDisable", false);
    SettingMode switchMode = create("AutoSwitchMode", "Normal", Arrays.asList("Normal", "Silent"));
    SettingBoolean autoSwitch = create("AutoSwitch", true);
    int oldSlot;
    EntityPlayer target;


    public AutoTrap() {
        super("AutoTrap", Category.COMBAT);
    }

    @Override
    public void onEnable() {
        if (autoSwitch.getValue()) {
            oldSlot = mc.player.inventory.currentItem;
        }
    }

    @Override
    public void onUpdate() {
        BlockPos[] fullTrap = getTrapMode();
        BlockPos targetPos = null;

        for (EntityPlayer entity : Minecraftable.mc.world.playerEntities) {
            if (entity != Minecraftable.mc.player) {
                target = entity;
            }
        }

        if (target != null) {
            if (mc.player.getDistance(target) <= range.getValue()) {
                if (findObsidianSlot() != -1) {
                    if (autoSwitch.getValue()) {
                        autoSwitch();
                    }

                    if (rotate.getValue()) {
                        Minecraftable.mc.player.rotationYaw = getRotations(target)[0];
                        Minecraftable.mc.player.rotationPitch = getRotations(target)[1];
                    }
                    for (BlockPos blockPos : fullTrap) {
                        targetPos = new BlockPos(
                                target.posX + blockPos.getX(),
                                target.posY + blockPos.getY(),
                                target.posZ + blockPos.getZ());
                        if (targetPos != null) {
                            switch (placeMode.getValue()) {
                                case "Packet":
                                    mc.player.connection.sendPacket(new CPacketPlayerTryUseItemOnBlock(targetPos, EnumFacing.UP, EnumHand.MAIN_HAND, 0, 0, 0));
                                    break;
                                case "Normal":
                                    mc.playerController.processRightClickBlock(mc.player, mc.world, targetPos, EnumFacing.UP, new Vec3d(0, 0, 0), EnumHand.MAIN_HAND);
                                    break;
                            }
                        }
                    }
                } else {
                    MessageUtil.sendMessage(ChatFormatting.DARK_PURPLE + "AutoTrap: " + ChatFormatting.GRAY + "No obsidian in hotbar! Disabling...");
                    this.setDisabled();
                }
            }

            if (disable.getValue()) {
                this.setDisabled();
            }
        }
    }

    public float[] getRotations(EntityPlayer target) {
        double x = target.posX - mc.player.posX;
        double y = target.posY + target.getEyeHeight() - (mc.player.posY + mc.player.getEyeHeight());
        double z = target.posZ - mc.player.posZ;
        double dist = Math.sqrt(x * x + z * z);
        float yaw = (float) Math.toDegrees(Math.atan2(z, x)) - 90.0f;
        float pitch = (float) (-Math.toDegrees(Math.atan2(y, dist)));
        return new float[]{yaw, pitch};
    }

    public void autoSwitch() {
        if (findObsidianSlot() != -1) {
            if (switchMode.getValue().equalsIgnoreCase("Normal")) {
                swapToHotbarSlot(findObsidianSlot());
            } else if (switchMode.getValue().equalsIgnoreCase("Silent")) {
                mc.player.connection.sendPacket(new CPacketHeldItemChange(findObsidianSlot()));
            }
        }
    }

    private int findObsidianSlot() {
        for (int i = 0; i < 9; i++) {
            if (mc.player.inventory.getStackInSlot(i).getItem() instanceof ItemBlock) {
                Block block = ((ItemBlock) mc.player.inventory.getStackInSlot(i).getItem()).getBlock();
                if (block instanceof BlockObsidian)
                    return i;
            }
        }
        return -1;
    }

    private void swapToHotbarSlot(int slot) {
        mc.player.inventory.currentItem = slot;
        mc.playerController.updateController();
    }

    @Override
    public void onDisable() {
        if (autoSwitch.getValue()) {
            swapToHotbarSlot(oldSlot);
        }
    }

    public BlockPos[] getTrapMode(){
        switch (trapMode.getValue()){
            case "Full":
                return TrapPositions.Full;
            case "Face":
                return TrapPositions.Face;
            case "Surround":
                return TrapPositions.Surround;
        }
        return TrapPositions.Full;
    }
}

