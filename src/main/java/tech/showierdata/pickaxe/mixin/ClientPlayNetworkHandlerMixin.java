package tech.showierdata.pickaxe.mixin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.packet.s2c.play.GameJoinS2CPacket;
import net.minecraft.text.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import tech.showierdata.pickaxe.Constants;
import tech.showierdata.pickaxe.Pickaxe;
import tech.showierdata.pickaxe.PickaxeCommand;

import java.util.*;


@Mixin(ClientPlayNetworkHandler.class)
public abstract class ClientPlayNetworkHandlerMixin  {

	@Unique
	private static final PickaxeCommand[] PickCommands = Pickaxe.getCommands();

    @Unique
	private boolean joinedGame = false;

	@SuppressWarnings("SameParameterValue")
	@Shadow
    public abstract void sendChatCommand(String message);

	@Unique
	private boolean inLoop = false;

	@Inject(at = @At("TAIL"), method = "onGameJoin")
	public void onGameJoin(GameJoinS2CPacket packet, CallbackInfo info) {
		Pickaxe pickaxe = Pickaxe.getInstance();

		//sleep for 5 seconds

		if (pickaxe.connectButtonPressed) {
			joinedGame = true;
			pickaxe.connectButtonPressed = false;
		}
	}

	@Inject(at = @At("HEAD"), method = "tick")
	private void onTick(CallbackInfo info) {
		if (joinedGame) {
			joinedGame = false;
			this.sendChatCommand("join " + Constants.PLOT_ID);
		}
	}
	
	@Inject(at = @At("HEAD"), method = "sendChatMessage", cancellable = true)
	private void sendMessage(String chatText, CallbackInfo info) {
		MinecraftClient client = MinecraftClient.getInstance();
		if (!Pickaxe.getInstance().isInPickaxe()) {
			return;
		}
		if (chatText.startsWith("@") && !inLoop) {

			List<String> command = List.of(chatText.substring(1).split(" ")); // Removes the "@"
			assert client.player != null;

			inLoop = true;

			for (PickaxeCommand pickaxeCommand : Pickaxe.getInstance().commands) {
				if (!Objects.equals(pickaxeCommand.name, command.get(0))) {
					continue;
				}


				pickaxeCommand.handler.use(command.get(0), command.subList(1, command.size()));
			}

			inLoop = false;

			info.cancel();
			
		}
	}
}
