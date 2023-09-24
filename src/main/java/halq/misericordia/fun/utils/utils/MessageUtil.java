package halq.misericordia.fun.utils.utils;

import com.mojang.realmsclient.gui.ChatFormatting;
import halq.misericordia.fun.gui.console.core.ConsoleAPI;
import halq.misericordia.fun.managers.command.CommandManager;
import halq.misericordia.fun.core.modulecore.Module;
import halq.misericordia.fun.utils.Minecraftable;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentBase;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.event.HoverEvent;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author accessmodifier364
 * @since 24-Nov-2021
 */

public class MessageUtil implements Minecraftable {

    private String name;
    private String[] commands;

    public static String getWatermark() {
        return "\u00a7+" + CommandManager.INSTANCE.getClientMessage();
    }

    public static void toggleMessage(Module module) {
        if (module.getName().equals("ClickGui") || module.getName().equals("HUD")) return;

        if (module.isEnabled()) {
            sendOverwriteMessage("[" + ChatFormatting.DARK_RED + "Misericordia" + ChatFormatting.GRAY + "] " + ChatFormatting.GRAY + module.getName() + " toggled " + ChatFormatting.GREEN + "on" + ChatFormatting.GRAY + ".", 7183);
        }
        else{
            sendOverwriteMessage("[" +  ChatFormatting.DARK_RED + "Misericordia" + ChatFormatting.GRAY + "] " +  ChatFormatting.GRAY + module.getName() + " toggled " + ChatFormatting.RED + "off" + ChatFormatting.GRAY + ".", 7183);
        }

    }

    public static void sendClientMessage(String message) {
        if (mc.player != null) {
            final ITextComponent itc = new TextComponentString(message).setStyle(new Style().setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new TextComponentString("Easter egg"))));
            mc.ingameGUI.getChatGUI().printChatMessageWithOptionalDeletion(itc, 6937);
        }
        ConsoleAPI.log(message);
    }

    public static void sendMessage(String message) {
        sendSilentMessage("[" + ChatFormatting.DARK_RED + "Misericordia" + ChatFormatting.GRAY + "] " + ChatFormatting.RESET + ChatFormatting.GRAY + message);
    }

    public static void sendSilentMessage(String message) {
        if (mc.player == null) return;
        mc.player.sendMessage(new ChatMessage(message));
        ConsoleAPI.log(message);
    }

    public static void sendOverwriteMessage(String message, int id) {
        TextComponentString component = new TextComponentString(message);
        mc.ingameGUI.getChatGUI().printChatMessageWithOptionalDeletion(component, id);
        ConsoleAPI.log(message);
    }

    public static void sendRainbowMessage(String message) {
        StringBuilder stringBuilder = new StringBuilder(message);
        stringBuilder.insert(0, "\u00a7+");
        mc.player.sendMessage(new ChatMessage(stringBuilder.toString()));
    }

    public String getName() {
        return name;
    }

    public String[] getCommands() {
        return commands;
    }

    public static class ChatMessage extends TextComponentBase {
        private final String text;

        public ChatMessage(String text) {
            Pattern pattern = Pattern.compile("&[0123456789abcdefrlosmk]");
            Matcher matcher = pattern.matcher(text);
            StringBuffer stringBuffer = new StringBuffer();
            while (matcher.find()) {
                String replacement = "\u00a7" + matcher.group().substring(1);
                matcher.appendReplacement(stringBuffer, replacement);
            }
            matcher.appendTail(stringBuffer);
            this.text = stringBuffer.toString();
        }

        public String getUnformattedComponentText() {
            return text;
        }

        public ITextComponent createCopy() {
            return new ChatMessage(text);
        }
    }

}
