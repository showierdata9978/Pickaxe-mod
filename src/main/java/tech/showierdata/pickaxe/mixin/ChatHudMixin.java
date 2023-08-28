package tech.showierdata.pickaxe.mixin;

import java.util.List;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.text.Text;
import tech.showierdata.pickaxe.hook.ChatHudHook;
import tech.showierdata.pickaxe.hook.IChatHudHook;
import net.minecraft.client.gui.hud.ChatHud;
import net.minecraft.client.gui.hud.ChatHudLine;
import net.minecraft.client.gui.hud.MessageIndicator;
import net.minecraft.network.message.MessageSignatureData;

@Mixin(ChatHud.class)
public abstract class ChatHudMixin implements IChatHudHook {
    /*
     * Uses an interface to take references to the ChatHud
     * That interface connects to the hook which allows changes to be made to the Chat Hud outside of Mixin
     */
    @Unique
    private final ChatHudHook chatHudHook = new ChatHudHook(this);

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
        /*
         * If we are refreshing, it's probably our own doing
         * Don't want to cause and infinite loop :O
         */
        if (refreshing) return message;

        return chatHudHook.compactChatMessage(message);
    }

    @Inject(method = "clear", at = @At("RETURN"))
    private void onClear(boolean clearHistory, CallbackInfo info) {
        this.chatHudHook.clear();
    }

    @Override
    public List<ChatHudLine> getMessages() {
        return this.messages;
    }

    @Override
    public void refreshMessages() {
        this.reset();
    }

    @Override
    public void clear() {
        this.clear(false);
    }

}
