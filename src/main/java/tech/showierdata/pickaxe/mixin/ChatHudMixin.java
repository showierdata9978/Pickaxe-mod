package tech.showierdata.pickaxe.mixin;

import java.util.List;
import java.util.ListIterator;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.text.Text;
import tech.showierdata.pickaxe.config.Options;
import tech.showierdata.pickaxe.server.Regexs;
import net.minecraft.client.gui.hud.ChatHud;
import net.minecraft.client.gui.hud.ChatHudLine;
import net.minecraft.client.gui.hud.MessageIndicator;
import net.minecraft.network.message.MessageSignatureData;

@Mixin(ChatHud.class)
public abstract class ChatHudMixin {
    @Unique
    private Text prevText = null;
    @Unique
    private int count = 1;

    @Shadow @Final private List<ChatHudLine> messages;

    @Shadow
    public abstract void reset();

    @Shadow
    public abstract void clear(boolean clearHistory);

    @ModifyVariable(
        method = "addMessage(Lnet/minecraft/text/Text;Lnet/minecraft/network/message/MessageSignatureData;ILnet/minecraft/client/gui/hud/MessageIndicator;Z)V",
        at = @At("HEAD"),
        argsOnly = true
    )
    private Text stackMessages(Text message, Text parameterMessage, MessageSignatureData data, int ticks, MessageIndicator indicator, boolean refreshing) {
        if (!Options.getInstance().messageStack) return message;
        
        /*
         * If we are refreshing, it's probably our own doing
         * Don't want to cause and infinite loop :O
         */
        if (refreshing) return message;

        return compactChatMessage(message);
    }

    public void removeMessage(Text originalMessage) {
        ListIterator<ChatHudLine> iterator = messages.listIterator();
        while (iterator.hasNext()) {
            ChatHudLine chatHudLine = iterator.next();

            // Remove previously stacked versions too
            Text contentWithoutOccurrences = Regexs.removeStackMods(chatHudLine.content());
            Text textWithoutOccurrences = Regexs.removeStackMods(originalMessage);

            if (contentWithoutOccurrences.equals(textWithoutOccurrences)) {
                iterator.remove();
                reset();

                return;
            }
        }
    }

    public Text compactChatMessage(Text message) {
        // Timestamps are removed to compare texts (otherwise none would match)
        Text withoutTimestamps = Regexs.removeTimestamps(message);

        Text prevMessage = prevText;
        prevText = withoutTimestamps;

        // Return if this is new message.
        if (!withoutTimestamps.equals(prevMessage)) {
            count = 1;
            return message;
        }

        removeMessage(message);

        this.count++;
        return message.copy().append(String.format(" §8[§bx%s§8]", count));
    }

    @Inject(method = "clear", at = @At("RETURN"))
    private void onClear(boolean clearHistory, CallbackInfo info) {
        prevText = null;
        count = 1;
    }
}
