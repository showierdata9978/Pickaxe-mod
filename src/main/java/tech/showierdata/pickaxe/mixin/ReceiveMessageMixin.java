package tech.showierdata.pickaxe.mixin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.hud.ChatHud;
import net.minecraft.client.gui.hud.ChatHudLine;
import net.minecraft.client.gui.hud.MessageIndicator;
import net.minecraft.client.util.ChatMessages;
import net.minecraft.network.message.MessageSignatureData;
import net.minecraft.text.MutableText;
import net.minecraft.util.math.MathHelper;
import net.minecraft.text.Text;

import java.util.List;

import tech.showierdata.pickaxe.Pickaxe;
import tech.showierdata.pickaxe.config.Options;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ChatHud.class)
public class ReceiveMessageMixin {
    @Shadow @Final private List<ChatHudLine.Visible> visibleMessages;

    @Inject(
            method = "addMessage(Lnet/minecraft/text/Text;Lnet/minecraft/network/message/MessageSignatureData;Lnet/minecraft/client/gui/hud/MessageIndicator;)V",
            at = @At("HEAD"))
    public void addMessage(Text message, MessageSignatureData signature, MessageIndicator indicator, CallbackInfo ci) {
        if (!Options.getInstance().hideSpam) return;
        if (!Pickaxe.getInstance().isInPickaxe()) return;

        var client = MinecraftClient.getInstance();
        var chat = client.inGameHud.getChatHud();
        var range = 10;
        var history = visibleMessages.size() >= range ? visibleMessages.subList(0, range) : visibleMessages;
        if (history.isEmpty()) return;

        var maxTextLength = MathHelper.floor(chat.getWidth() / chat.getChatScale());
        var splitLines = ChatMessages.breakRenderedChatMessageLines(
               message, maxTextLength, client.textRenderer);

        var spamCounter = 1;
        var lineMatchCount = 0;

        for (int i = history.size() - 1; i >= 0; i--) {
            var previous =  history.get(i).content();

            if (lineMatchCount <= splitLines.size() - 1) {
                String next = orderedTextToString(splitLines.get(lineMatchCount));

                if (lineMatchCount < splitLines.size() - 1) {
                    if (getDifference(previous, next) <= 0) lineMatchCount++;
                    else lineMatchCount = 0;

                    continue;
                }

                if (!previous.startsWith(next)) {
                    lineMatchCount = 0;
                    continue;
                }

                if (i > 0 && lineMatchCount == splitLines.size() - 1) {
                    var appended = (previous + orderedTextToString(history.get(i - 1).content()))
                            .substring(next.length());

                    if (appended.startsWith(" [x") && appended.endsWith("]")) {
                        var previousCounter = appended.substring(3, appended.length() - 1);

                        try {
                            spamCounter += Integer.parseInt(previousCounter);
                            lineMatchCount++;
                            continue;
                        } catch (NumberFormatException ignored) {}
                    }
                }

                if (previous.length() == next.length()) spamCounter++;
                else {
                    var appended = previous.substring(next.length());
                    if (!appended.startsWith(" [x") || !appended.endsWith("]")) {
                        lineMatchCount = 0;
                        continue;
                    }

                    var previousCounter = appended.substring(3, appended.length() - 1);
                    try {
                        spamCounter += Integer.parseInt(previousCounter);
                    } catch (NumberFormatException ex) {
                        lineMatchCount = 0;
                        continue;
                    }
                }
            }

            if (i + lineMatchCount >= i) {
                history.subList(i, i + lineMatchCount + 1).clear();
            }
            lineMatchCount = 0;
        }

        if (spamCounter > 1) ((MutableText) message).append(" §8[§cx" + spamCounter + "§8]");
    }
}
