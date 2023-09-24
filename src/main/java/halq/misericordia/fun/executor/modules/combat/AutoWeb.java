package halq.misericordia.fun.executor.modules.combat;

import halq.misericordia.fun.core.modulecore.Category;
import halq.misericordia.fun.core.modulecore.Module;
import halq.misericordia.fun.events.RenderEvent;
import halq.misericordia.fun.executor.modules.combat.trap.TrapPositions;
import halq.misericordia.fun.executor.settings.SettingBoolean;
import halq.misericordia.fun.executor.settings.SettingDouble;
import halq.misericordia.fun.executor.settings.SettingMode;
import halq.misericordia.fun.utils.utils.RenderUtil;
import net.minecraft.block.Block;
import net.minecraft.block.BlockWeb;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemBlock;
import net.minecraft.network.play.client.CPacketHeldItemChange;
import net.minecraft.network.play.client.CPacketPlayerTryUseItemOnBlock;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

import java.util.Arrays;

public class AutoWeb extends Module {

    public AutoWeb() {
        super("AutoWeb", Category.COMBAT);
    }

    SettingMode mode = create("Mode", "Trap", Arrays.asList("Trap", "Feet"));
    SettingMode autoSwitchMode = create("AutoSwitchMode", "Normal", Arrays.asList("Normal", "Silent"));
    SettingMode placeMode = create("PlaceMode", "Packet", Arrays.asList("Packet", "Normal"));
    SettingDouble range = create("Range", 4, 0.0, 6);
    SettingBoolean autoSwitch = create("AutoSwitch", true);
    SettingBoolean rotate = create("Rotate", true);
    SettingBoolean disable = create("Disable", true);
    SettingBoolean render = create("Render", true);
    SettingDouble red = create("Red", 190.0, 0, 255);
    SettingDouble green = create("Green", 0.0, 0, 255);
    SettingDouble blue = create("Blue", 255.0, 0, 255);
    SettingDouble alpha = create("Alpha", 61.0, 0, 255);
    SettingDouble blocksPerTick = create("BlocksPerTick", 4.0, 0, 8);
    EntityPlayer target;
    BlockPos placePos;
    boolean canRender;
    int oldSlot;
    int blocksInThisTick;

    @Override
    public void onEnable(){
        if (autoSwitch.getValue()) {
            oldSlot = mc.player.inventory.currentItem;
        }
    }

    @Override
    public void onUpdate() {
        for (EntityPlayer player : mc.world.playerEntities) {
            if (player != mc.player) {
                target = player;
            }
        }

        if (rotate.getValue()) {
            mc.player.rotationYaw = getRotations(target)[0];
            mc.player.rotationPitch = getRotations(target)[1];
        }

        if (autoSwitch.getValue()) {
            autoSwitch();
        }

        switch (mode.getValue()) {
            case "Trap":
                for (BlockPos pos : TrapPositions.Full) {
                    if (blocksInThisTick >= blocksPerTick.getValue()) {
                        break; // Já atingimos o limite de blocos a serem colocados neste tick
                    }
                    placePos = new BlockPos(target.posX + pos.getX(), target.posY + pos.getY(), target.posZ + pos.getZ());
                    switch (placeMode.getValue()) {
                        case "Packet":
                            mc.player.connection.sendPacket(new CPacketPlayerTryUseItemOnBlock(placePos, EnumFacing.UP, EnumHand.MAIN_HAND, 0, 0, 0));
                            canRender = true;
                            break;
                        case "Normal":
                            mc.playerController.processRightClickBlock(mc.player, mc.world, placePos, EnumFacing.UP, new Vec3d(0, 0, 0), EnumHand.MAIN_HAND);
                            canRender = true;
                            break;
                    }
                    blocksInThisTick++;
                }
                break;
            case "Feet":
                placePos = new BlockPos(target.posX, target.posY - 1, target.posZ);
                break;
        }

        if (mc.player.getDistance(target) <= range.getValue() && placePos != null) {
            switch (placeMode.getValue()) {
                case "Packet":
                    mc.player.connection.sendPacket(new CPacketPlayerTryUseItemOnBlock(placePos, EnumFacing.UP, EnumHand.MAIN_HAND, 0, 0, 0));
                    canRender = true;
                    break;
                case "Normal":
                    mc.playerController.processRightClickBlock(mc.player, mc.world, placePos, EnumFacing.UP, new Vec3d(0, 0, 0), EnumHand.MAIN_HAND);
                    canRender = true;
                    break;
            }
        }

        if(disable.getValue()){
            this.setDisabled();
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
        if (findWebSlot() != -1) {
            if (autoSwitchMode.getValue().equalsIgnoreCase("Normal")) {
                swapToHotbarSlot(findWebSlot());
            } else if (autoSwitchMode.getValue().equalsIgnoreCase("Silent")) {
                mc.player.connection.sendPacket(new CPacketHeldItemChange(findWebSlot()));
            }
        }
    }

    private int findWebSlot() {
        for (int i = 0; i < 9; i++) {
            if (mc.player.inventory.getStackInSlot(i).getItem() instanceof ItemBlock) {
                Block block = ((ItemBlock) mc.player.inventory.getStackInSlot(i).getItem()).getBlock();
                if (block instanceof BlockWeb)
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
    public void onRender3D(RenderEvent event) {
        if (target != null && canRender) {
            if (render.getValue()) {
                switch (mode.getValue()) {
                    case "Trap":
                        for (BlockPos pos : TrapPositions.Full) {
                            placePos = new BlockPos(target.posX + pos.getX(), target.posY + pos.getY(), target.posZ + pos.getZ());
                            if (mc.world.getBlockState(placePos).getBlock() != Blocks.WEB) {
                                AxisAlignedBB bb = new AxisAlignedBB(placePos.getX() - mc.getRenderManager().viewerPosX, placePos.getY() - mc.getRenderManager().viewerPosY, placePos.getZ() - mc.getRenderManager().viewerPosZ, placePos.getX() + 1 - mc.getRenderManager().viewerPosX, placePos.getY() + 1 - mc.getRenderManager().viewerPosY, placePos.getZ() + 1 - mc.getRenderManager().viewerPosZ);
                                if (RenderUtil.isInViewFrustrum(new AxisAlignedBB(bb.minX + mc.getRenderManager().viewerPosX, bb.minY + mc.getRenderManager().viewerPosY, bb.minZ + mc.getRenderManager().viewerPosZ, bb.maxX + mc.getRenderManager().viewerPosX, bb.maxY + mc.getRenderManager().viewerPosY, bb.maxZ + mc.getRenderManager().viewerPosZ))) {
                                    RenderUtil.drawESP(bb, red.getValue().floatValue(), green.getValue().floatValue(), blue.getValue().floatValue(), alpha.getValue().floatValue());
                                }
                            }
                        }
                        break;
                    case "Feet":
                        placePos = new BlockPos(target.posX, target.posY, target.posZ);
                        AxisAlignedBB bb = new AxisAlignedBB(placePos.getX() - mc.getRenderManager().viewerPosX, placePos.getY() - mc.getRenderManager().viewerPosY, placePos.getZ() - mc.getRenderManager().viewerPosZ, placePos.getX() + 1 - mc.getRenderManager().viewerPosX, placePos.getY() + 1 - mc.getRenderManager().viewerPosY, placePos.getZ() + 1 - mc.getRenderManager().viewerPosZ);
                        if (RenderUtil.isInViewFrustrum(new AxisAlignedBB(bb.minX + mc.getRenderManager().viewerPosX, bb.minY + mc.getRenderManager().viewerPosY, bb.minZ + mc.getRenderManager().viewerPosZ, bb.maxX + mc.getRenderManager().viewerPosX, bb.maxY + mc.getRenderManager().viewerPosY, bb.maxZ + mc.getRenderManager().viewerPosZ))) {
                            RenderUtil.drawESP(bb, red.getValue().floatValue(), green.getValue().floatValue(), blue.getValue().floatValue(), alpha.getValue().floatValue());
                        }
                        break;
                }
            }
        }
    }

    @Override
    public void onDisable(){
        canRender = false;
        placePos = null;
        if (autoSwitch.getValue()) {
            swapToHotbarSlot(oldSlot);
        }
    }
}
