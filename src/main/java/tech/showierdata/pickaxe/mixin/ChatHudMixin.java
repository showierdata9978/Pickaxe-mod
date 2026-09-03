package tech.showierdata.pickaxe.mixin;

import java.util.List;
import java.util.ListIterator;
import java.util.function.Function;
import net.minecraft.client.GuiMessage;
import net.minecraft.client.GuiMessageTag;
import net.minecraft.client.gui.components.ChatComponent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MessageSignature;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import tech.showierdata.pickaxe.config.MsgStackConfig;
import tech.showierdata.pickaxe.config.Options;
import tech.showierdata.pickaxe.server.Regexps;

@Mixin(ChatComponent.class)
public abstract class ChatHudMixin {
    @Unique
    private Component prevText = null;
    @Unique
    private int count = 1;

    @Shadow
    @Final
    private List<GuiMessage> allMessages;

    @Shadow
    public abstract void rescaleChat();

    
    @ModifyVariable(
        method = "addMessage(Lnet/minecraft/network/chat/Component;Lnet/minecraft/network/chat/MessageSignature;ILnet/minecraft/client/GuiMessageTag;Z)V",
        at = @At("HEAD"),
        argsOnly = true
    )
    private Component stackMessages(Component message, Component parameterMessage, MessageSignature data, int ticks, GuiMessageTag indicator, boolean refreshing) {
        if (!Options.getInstance().msgStackConfig.enabled) return message;
        
        /*
         * If we are refreshing, it's probably our own doing
         * Don't want to cause and infinite loop :O
         */
        if (refreshing) return message;

        // Timestamps are removed to compare texts (otherwise none would match)
        Component withoutTimestamps = Regexps.removeTimestamps(message);

        Component prevMessage = prevText;
        prevText = withoutTimestamps;

        // Return if this is new message.
        if (!withoutTimestamps.equals(prevMessage)) {
            count = 1;
            return message;
        }

        // Get current loadout
        MsgStackConfig stack = Options.getInstance().msgStackConfig;

        // Iterate and remove
        ListIterator<GuiMessage> iterator = allMessages.listIterator();
        while (iterator.hasNext()) {
            GuiMessage chatHudLine = iterator.next();

            // Undo changes
            Component contentWithoutOccurrences = stack.removeStackMods(chatHudLine.content());
            Component textWithoutOccurrences = stack.removeStackMods(message);

            // Test if they are equal
            if (contentWithoutOccurrences.equals(textWithoutOccurrences)) {
                iterator.remove();
                rescaleChat();

                break; // Found the instance, we're done here
            }
        }

        this.count++;
        return message.copy().append(" " + stack.getBorderString(count));
    }

    @Inject(method = "clearMessages", at = @At("RETURN"))
    private void onClear(boolean clearHistory, CallbackInfo info) {
        prevText = null;
        count = 1;
    }
}
