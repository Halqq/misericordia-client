package halq.misericordia.fun.executor.modules.render.fallpredict;

import halq.misericordia.fun.utils.Minecraftable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

import javax.swing.text.html.parser.Entity;

public class FallPredictCalcs implements Minecraftable {

    public static BlockPos calcPos() {
        // Obter a posição atual do jogador
        if(mc.player.fallDistance > 0) {
            BlockPos playerPos = new BlockPos(mc.player.posX, mc.player.posY, mc.player.posZ);

            // Obter a altura do bloco "base" do jogador
            double playerBaseHeight = playerPos.getY() - mc.player.eyeHeight;

            // Obter a velocidade atual do jogador
            double playerMotionX = mc.player.motionX;
            double playerMotionY = mc.player.motionY;
            double playerMotionZ = mc.player.motionZ;

            // Calcular a posição prevista de queda
            boolean onGround = false;
            double predictedPosX = mc.player.posX;
            double predictedPosY = mc.player.posY;
            double predictedPosZ = mc.player.posZ;

            while (!onGround && predictedPosY > 0 && predictedPosY >= playerBaseHeight) {
                predictedPosX += playerMotionX;
                predictedPosY += playerMotionY;
                predictedPosZ += playerMotionZ;

                BlockPos predictedBlockPos = new BlockPos(predictedPosX, predictedPosY, predictedPosZ);
                onGround = mc.world.getBlockState(predictedBlockPos.down()).isFullBlock();
            }

            // Verificar se o jogador atingiu o chão
            if (onGround) {
                predictedPosY -= playerMotionY; // Ajustar a posição Y para a posição no chão
            }

            // Arredondar para as coordenadas inteiras mais próximas
            int predictedBlockX = MathHelper.floor(predictedPosX);
            int predictedBlockY = MathHelper.floor(predictedPosY);
            int predictedBlockZ = MathHelper.floor(predictedPosZ);

            // Retornar a posição prevista
            return new BlockPos(predictedBlockX, predictedBlockY, predictedBlockZ);
        }
        return null;
            }

            private static BlockPos getSurfaceBlockPos( int x, int y, int z) {
                for (int i = y; i >= 0; i--) {
                    BlockPos blockPos = new BlockPos(x, i, z);
                    if (mc.world.isAirBlock(blockPos) && !mc.world.isAirBlock(blockPos.down())) {
                        return blockPos.down();
            }
        }
        return new BlockPos(x, 0, z);
    }
}
