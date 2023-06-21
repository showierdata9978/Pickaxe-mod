package tech.showierdata.mixin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.text.Text;
import tech.showierdata.Pickaxe;
import tech.showierdata.PickaxeCommand;

import java.util.ArrayList;
import java.util.HashMap;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;



@Mixin(ClientPlayNetworkHandler.class)
public class ClientPlayNetworkHandlerMixin {
	private static final PickaxeCommand[] PickCommands = Pickaxe.getCommands();
	private static final HashMap<String, PickaxeCommand> PickHandledCommands = Pickaxe.getHandledCommands();




	
	@Inject(at = @At("HEAD"), method = "sendChatMessage", cancellable = true)
	private void sendMessage(String chatText, CallbackInfo info) {
		if (!Pickaxe.getInstance().isInPickaxe()) {
			return;
		}
		if (chatText.startsWith("@")) {

			String command = chatText.substring(1); // Removes the "@"

			if (!PickHandledCommands.containsKey(command)) {
				return;
			}


			switch (command) {
				case "help":
					ArrayList<String> s = new ArrayList<String>();
					for (PickaxeCommand c: PickCommands) {
						s.add(
							"@" + c.name + 
							" " + 
							String.join(" ", c.arguments) + 
							"\n    " + 
							c.data
						);
					}
					MinecraftClient.getInstance().player.sendMessage(Text.literal(
						"-- Help --\n" + String.join("\n", s)
					));
			}

			info.cancel();
			
			

			
		}
	}
}
