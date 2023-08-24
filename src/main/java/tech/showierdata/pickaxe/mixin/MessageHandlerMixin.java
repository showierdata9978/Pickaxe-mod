package tech.showierdata.pickaxe.mixin;

import com.mojang.authlib.GameProfile;
import net.minecraft.client.network.message.MessageHandler;
import net.minecraft.network.message.MessageType;
import net.minecraft.network.message.SignedMessage;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import tech.showierdata.pickaxe.Pickaxe;
import tech.showierdata.pickaxe.config.Options;
import tech.showierdata.pickaxe.server.Plot;
import tech.showierdata.pickaxe.server.Regexs;

@SuppressWarnings("UnusedMixin")
@Mixin(MessageHandler.class)
public class MessageHandlerMixin {
	@Inject(at = @At("HEAD"), method = "onChatMessage", cancellable = true)
    private void onChatMessage(@NotNull SignedMessage message, GameProfile sender, MessageType.Parameters params, CallbackInfo info) {
		if (Regexs.isLocateCommand(message.getContent().getString())) {
			if (Pickaxe.commandHelper.getLastSentCommand().equals("locate")) {
				Pickaxe.commandHelper.clearLastSentCommand();
				Plot plot = Regexs.getPlotDetails(message.getContent().getString());
				/*pickaxe.currentPlot = plot;*/
				assert plot != null;
				Pickaxe.LOGGER.info("Located plot: " + plot.name);
				info.cancel(); // stop the message from being shown to the player.
			}
		}
		if (Regexs.isPlotAd(message.getContent().getString())) {
			if (!Options.getInstance().hide_plot_ads) return;
			if (!Pickaxe.getInstance().isInPickaxe()) return;

			info.cancel(); // hide the ad :)
		}
	}
}
