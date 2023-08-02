package tech.showierdata.pickaxe.mixin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.packet.s2c.play.GameJoinS2CPacket;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import tech.showierdata.pickaxe.Constants;
import tech.showierdata.pickaxe.Pickaxe;
import tech.showierdata.pickaxe.PickaxeCommand;

import java.util.ArrayList;
import java.util.HashMap;



@Mixin(ClientPlayNetworkHandler.class)
public abstract class ClientPlayNetworkHandlerMixin  {
	private static final PickaxeCommand[] PickCommands = Pickaxe.getCommands();
	private static final HashMap<String, PickaxeCommand> PickHandledCommands = Pickaxe.getHandledCommands();

	@SuppressWarnings("SameParameterValue")
	@Shadow
	abstract void sendChatCommand(String message);

	private boolean joinedGame = false;

	@Inject(at = @At("TAIL"), method = "onGameJoin")
	public void onGameJoin(GameJoinS2CPacket packet, CallbackInfo info) {
		Pickaxe pickaxe = Pickaxe.getInstance();

		//sleep for 5 seconds

		if (pickaxe.connectButtenPressed) {
			joinedGame = true;
			pickaxe.connectButtenPressed = false;
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
		if (chatText.startsWith("@")) {

			String command = chatText.substring(1); // Removes the "@"
			assert client.player != null;


			if (!PickHandledCommands.containsKey(command)) {
				return;
			}


			if (command.equals("help")) {
				ArrayList<String> s = new ArrayList<>();
				for (PickaxeCommand c : PickCommands) {
					s.add(
							"@" + c.name +
									" " +
									String.join(" ", c.arguments) +
									"\n    " +
									c.data
					);
				}
				client.player.sendMessage(Text.literal(
						"-- Help --\n" + String.join("\n", s)
				));
			}

			info.cancel();
			
			

			
		}
	}

}
