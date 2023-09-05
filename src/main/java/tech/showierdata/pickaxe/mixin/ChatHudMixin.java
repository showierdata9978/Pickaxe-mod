package tech.showierdata.pickaxe.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Slice;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.ChatHud;
import net.minecraft.client.gui.hud.MessageIndicator;
import tech.showierdata.pickaxe.config.Options;

@Mixin(ChatHud.class)
public abstract class ChatHudMixin {

    @ModifyVariable(method = "render", 
        slice = @Slice(from = @At(
            value = "INVOKE",
            target = "net/minecraft/client/gui/hud/ChatHudLine$Visible.indicator ()Lnet/minecraft/client/gui/hud/MessageIndicator",
            ordinal = 0
        )),
        at = @At(value = "STORE", ordinal = 0))
    MessageIndicator hideBarIndicator(MessageIndicator prev, DrawContext context, int tick, int mouseX, int mouseY) {
        // Return null to skip drawing the bar, if enabled
        return (Options.getInstance().remove_chat_bar)? null : prev;
    }
}
