package halq.misericordia.fun.mixin.mixins;

import io.netty.channel.ChannelHandlerContext;
import halq.misericordia.fun.core.eventcore.MisericordiaEvents;
import halq.misericordia.fun.events.PacketEvent;
import halq.misericordia.fun.utils.utils.TimerUtil;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.common.MinecraftForge;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = {NetworkManager.class})
public class MixinNetworkManager {

    TimerUtil timer = new TimerUtil();

    @Inject(method = "sendPacket(Lnet/minecraft/network/Packet;)V", at = @At("HEAD"), cancellable = true)
    public void onPacketSend(Packet<?> packet, CallbackInfo ci) {
        PacketEvent.PacketSendEvent event = new PacketEvent.PacketSendEvent(packet, MisericordiaEvents.Stage.PRE);
        MinecraftForge.EVENT_BUS.post(event);

        if (event.isCanceled())
            ci.cancel();
    }

    @Inject(method = "channelRead0", at = @At("HEAD"), cancellable = true)
    public void onPacketReceive(ChannelHandlerContext chc, Packet<?> packet, CallbackInfo ci) {
        PacketEvent.PacketReceiveEvent event = new PacketEvent.PacketReceiveEvent(packet, MisericordiaEvents.Stage.PRE);
        MinecraftForge.EVENT_BUS.post(event);

        if (event.isCanceled())
            ci.cancel();
    }

    @Inject(method = "closeChannel", at = @At("HEAD"))
    public void preCloseChannel(ITextComponent message, CallbackInfo callbackInfo) {
        timer.reset();
    }
}
