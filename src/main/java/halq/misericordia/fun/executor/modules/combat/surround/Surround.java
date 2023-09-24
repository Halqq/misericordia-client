package halq.misericordia.fun.executor.modules.combat.surround;

import halq.misericordia.fun.core.modulecore.Category;
import halq.misericordia.fun.core.modulecore.Module;
import halq.misericordia.fun.events.RenderEvent;
import halq.misericordia.fun.executor.settings.SettingBoolean;
import halq.misericordia.fun.executor.settings.SettingDouble;
import halq.misericordia.fun.executor.settings.SettingMode;
import net.minecraft.block.Block;
import net.minecraft.block.BlockObsidian;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemBlock;
import net.minecraft.network.play.client.CPacketPlayerTryUseItemOnBlock;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

import java.util.Arrays;
import java.util.List;

/**
 * @author Halq
 * @since 06/06/2023 at 18:57
 */
public class Surround extends Module {
    SettingBoolean rotate = create("Rotate", true);
    SettingBoolean center = create("AutoCenter", true);
    SettingBoolean jumpDisable = create("JumpDisable", true);
    SettingBoolean render = create("Render", true);
    SettingDouble blocksPerTick = create("BlocksPerTick", 4.0, 0, 8);
    SettingDouble delay = create("Delay", 0.0, 0, 10);
    SettingBoolean autodis = create("AutoDisable", true);

    SettingDouble red = create("Red", 190.0, 0, 255);
    SettingDouble green = create("Green", 0.0, 0, 255);
    SettingDouble blue = create("Blue", 255.0, 0, 255);
    SettingDouble alpha = create("Alpha", 255.0, 0, 255);
    SettingMode mode = create("PlaceMode", "Packet", Arrays.asList("Packet", "Normal"));

    private boolean isEnabled;
    private double startY;
    private long lastPlacementTime;
    private int placedBlocks;
    private boolean shouldCenter;

    public Surround() {
        super("Surround", Category.COMBAT);

        this.isEnabled = false;
        this.startY = 0.0;
        this.lastPlacementTime = 0;
        this.placedBlocks = 0;
        this.shouldCenter = center.getValue();
    }

    @Override
    public void onEnable() {
        this.isEnabled = true;
        this.startY = mc.player.posY;
        this.lastPlacementTime = 0;
        this.placedBlocks = 0;
        this.shouldCenter = center.getValue();
    }

    @Override
    public void onUpdate() {
        double desiredDelay = delay.getValue() * 1000;
        double desiredBlocksPerTick = blocksPerTick.getValue();

        if (System.currentTimeMillis() - lastPlacementTime < desiredDelay)
            return;

        List<BlockPos> surroundPositions = SurroundPos.getSurroundPositions();

        int blocksPerTickInt = (int) desiredBlocksPerTick;
        int blocksPlaced = 0;

        for (BlockPos pos : surroundPositions) {
            if (blocksPlaced >= blocksPerTickInt)
                break;

            if (mc.world.isAirBlock(pos.down()) && isPlaceable(pos.down())) {
                placeBlock(pos.down());
                blocksPlaced++;
                placedBlocks++;
                lastPlacementTime = System.currentTimeMillis();
            }

            if (isPlaceable(pos)) {
                placeBlock(pos);
                blocksPlaced++;
                placedBlocks++;
                lastPlacementTime = System.currentTimeMillis();
            }
        }

        if (shouldCenter) {
            centerPlayer();
            shouldCenter = false;
        }

        if (jumpDisable.getValue() && mc.gameSettings.keyBindJump.isKeyDown()) {
            setDisabled();
        }

        if (autodis.getValue()) {
            setDisabled();
        }
    }

    private boolean isPlaceable(BlockPos pos) {
        return mc.world.isAirBlock(pos) && mc.world.getBlockState(pos.down()).isFullBlock();
    }

    private void placeBlock(BlockPos pos) {
        int oldSlot = mc.player.inventory.currentItem;
        int obsidianSlot = findObsidianSlot();

        for (EnumFacing faces : EnumFacing.values()) {
            final BlockPos offset = pos.offset(faces);
            final Block block = mc.world.getBlockState(offset).getBlock();

            if (block != Blocks.AIR) {
                final EnumFacing facing = faces.getOpposite();

                float facingX = 0.5f;
                float facingY = 0.5f;
                float facingZ = 0.5f;

                if (obsidianSlot != -1) {
                    swapToHotbarSlot(obsidianSlot);

                    if (rotate.getValue()) {
                        float[] rotations = getRotations(pos);
                        mc.player.rotationYaw = rotations[0];
                        mc.player.rotationPitch = rotations[1];
                    }

                    switch (mode.getValue()) {
                        case "Packet":
                            mc.player.connection.sendPacket(new CPacketPlayerTryUseItemOnBlock(offset, facing, EnumHand.MAIN_HAND, facingX, facingY, facingZ));
                            break;
                        case "Normal":
                            mc.playerController.processRightClickBlock(mc.player, mc.world, offset, facing, new Vec3d(facingX, facingY, facingZ), EnumHand.MAIN_HAND);
                            break;
                    }

                    if (mc.player.inventory.currentItem != oldSlot) {
                        swapToHotbarSlot(oldSlot);
                    }
                }
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

    private float[] getRotations(BlockPos pos) {
        double xDiff = pos.getX() + 0.5 - mc.player.posX;
        double yDiff = (pos.getY() + 0.5) * 0.9 - (mc.player.posY + mc.player.getEyeHeight());
        double zDiff = pos.getZ() + 0.5 - mc.player.posZ;
        double distance = Math.sqrt(xDiff * xDiff + zDiff * zDiff);

        float yaw = (float) Math.toDegrees(-Math.atan2(xDiff, zDiff));
        float pitch = (float) -Math.toDegrees(Math.atan2(yDiff, distance));

        return new float[]{yaw, pitch};
    }

    private void centerPlayer() {
        double playerX = mc.player.posX;
        double playerZ = mc.player.posZ;
        double blockX = Math.floor(playerX) + 0.5;
        double blockZ = Math.floor(playerZ) + 0.5;

        mc.player.setPosition(blockX, mc.player.posY, blockZ);
    }

    @Override
    public void onRender3D(RenderEvent event) {
        if (!render.getValue())
            return;

        for (BlockPos pos : SurroundPos.getSurroundPositions()) {
            SurroundRender.renderSurround(isPlaceable(pos), pos, red.getValue().floatValue() / 255, green.getValue().floatValue() / 255, blue.getValue().floatValue() / 255, alpha.getValue().floatValue() / 255);
        }
    }
}
