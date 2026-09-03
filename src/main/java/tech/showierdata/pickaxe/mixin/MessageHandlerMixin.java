package tech.showierdata.pickaxe.mixin;


import com.mojang.authlib.GameProfile;
import net.minecraft.client.multiplayer.chat.ChatListener;
import net.minecraft.network.chat.ChatType;
import net.minecraft.network.chat.PlayerChatMessage;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import tech.showierdata.pickaxe.Pickaxe;
import tech.showierdata.pickaxe.config.Options;
import tech.showierdata.pickaxe.server.Plot;
import tech.showierdata.pickaxe.server.Regexps;

@SuppressWarnings("UnusedMixin")
@Mixin(ChatListener.class)
public class MessageHandlerMixin {
	@Inject(at = @At("HEAD"), method = "handlePlayerChatMessage", cancellable = true)
    private void onGameMessage(PlayerChatMessage message, GameProfile sender, ChatType.Bound params, CallbackInfo info) {
		if (Regexps.isLocateCommand(message.decoratedContent().getString())) {
			//if (Pickaxe.commandHelper.getLastSentCommand().equals("locate")) {
			Pickaxe.commandHelper.clearLastSentCommand();
			Plot plot = Regexps.getLocateDetails(message.decoratedContent().getString());
			//pickaxe.currentPlot = plot;
			assert plot != null;
            Pickaxe.LOGGER.info("Located plot: {}", plot.name);
			info.cancel(); // stop the message from being shown to the player.
			//}
		}
		if (Regexps.isPlotAd(message.decoratedContent().getString())) {
			if (!Options.getInstance().hide_plot_ads) return;
			if (!Pickaxe.getInstance().isInPickaxe()) return;

			Pickaxe.LOGGER.info("Plot ad found >:(");

			info.cancel(); // hide the ad :)
		}
	}
}
