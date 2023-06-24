package tech.showierdata.pickaxe.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.mojang.authlib.GameProfile;

import net.minecraft.client.network.message.MessageHandler;
import net.minecraft.network.message.MessageType;
import net.minecraft.network.message.SignedMessage;
import tech.showierdata.pickaxe.Pickaxe;
import tech.showierdata.pickaxe.server.Plot;
import tech.showierdata.pickaxe.server.Regexs;

@Mixin(MessageHandler.class)
public class MessageHandlerMixin {
	@Inject(at = @At("HEAD"), method = "onChatMessage", cancellable = true)
	private void onChatMessage(SignedMessage message, GameProfile sender, MessageType.Parameters params,CallbackInfo info) {
		if (Regexs.isLocateCommand(message.getContent().getString())) {
			Pickaxe pickaxe = Pickaxe.getInstance();
			if (Pickaxe.commandHelper.getLastSentCommand().equals("locate")) {
				Pickaxe.commandHelper.clearLastSentCommand();
				Plot plot = Regexs.getPlotDetails(message.getContent().getString());
				pickaxe.currentPlot = plot;
				Pickaxe.LOGGER.info("Located plot: " + plot.name);
				info.cancel(); // stop the message from being shown to the player.
			}
		}
	}
}
