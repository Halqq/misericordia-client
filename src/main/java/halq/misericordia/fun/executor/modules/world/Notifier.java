package halq.misericordia.fun.executor.modules.world;

import com.mojang.realmsclient.gui.ChatFormatting;
import halq.misericordia.fun.core.modulecore.Category;
import halq.misericordia.fun.core.modulecore.Module;
import halq.misericordia.fun.events.PacketEvent;
import halq.misericordia.fun.executor.settings.SettingBoolean;
import halq.misericordia.fun.utils.utils.MessageUtil;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.play.server.SPacketBlockBreakAnim;
import net.minecraft.network.play.server.SPacketEntityStatus;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

/**
 * @author Halq
 * @since 02/07/2023 at 17:55
 */

public class Notifier extends Module {

    public static Notifier INSTANCE;
    SettingBoolean amor = create("Armor", true);
    SettingBoolean totempop = create("TotemPop", true);
    SettingBoolean totempopplayer = create("TotemPopOtherPlayer", false, totempop.getValue());
    SettingBoolean surround = create("Surround", true);
    //make predict id
    public Notifier() {
        super("Notifier", Category.WORLD);
        INSTANCE = this;
    }

    @Override
    public void onUpdate() {
        if(amor.getValue()){
            if(mc.player.inventory.armorItemInSlot(3).getItemDamage() == 10){
                MessageUtil.sendMessage(ChatFormatting.RED + "Your helmet is 10%!");
            }

            if(mc.player.inventory.armorItemInSlot(2).getItemDamage() == 10){
                MessageUtil.sendMessage(ChatFormatting.RED + "Your chestplate is 10%!");
            }

            if(mc.player.inventory.armorItemInSlot(1).getItemDamage() == 10){
                MessageUtil.sendMessage(ChatFormatting.RED + "Your leggings are 10%!");
            }

            if(mc.player.inventory.armorItemInSlot(0).getItemDamage() == 10){
                MessageUtil.sendMessage(ChatFormatting.RED + "Your boots are 10%!");
            }
        }
    }

    @SubscribeEvent
    public void onPacketReceive(PacketEvent.PacketReceiveEvent event) {

        if(event.getPacket() instanceof SPacketEntityStatus){
            SPacketEntityStatus packet = (SPacketEntityStatus) event.getPacket();
            if(packet.getOpCode() == 35 && totempop.getValue()){
                if(packet.getEntity(mc.world) == mc.player){
                    MessageUtil.sendMessage(ChatFormatting.RED + "You popped a totem!");
                }else if(totempopplayer.getValue()){
                    EntityPlayer player = (EntityPlayer) packet.getEntity(mc.world);
                    MessageUtil.sendMessage(ChatFormatting.GREEN + player.getName() + " popped a totem!");
                }
            }
        }
    }
}