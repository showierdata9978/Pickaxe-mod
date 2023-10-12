package tech.showierdata.pickaxe.mixin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.s2c.play.GameJoinS2CPacket;
import net.minecraft.network.packet.s2c.play.GameMessageS2CPacket;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.collection.DefaultedList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import tech.showierdata.pickaxe.Constants;
import tech.showierdata.pickaxe.Pickaxe;
import tech.showierdata.pickaxe.PickaxeCommand;
import tech.showierdata.pickaxe.config.Options;

import java.util.*;


@Mixin(ClientPlayNetworkHandler.class)
public abstract class ClientPlayNetworkHandlerMixin  {

	@Unique
	private static final PickaxeCommand[] PickCommands = Pickaxe.getCommands();
	@Unique
	private static final HashMap<String, PickaxeCommand> PickHandledCommands = Pickaxe.getHandledCommands();

    @Unique
	private boolean joinedGame = false;

	@SuppressWarnings("SameParameterValue")
	@Shadow
    public abstract void sendChatCommand(String message);

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

	@Inject(method = "onGameMessage", at = @At("HEAD"))
	private void onGameMessage(GameMessageS2CPacket packet, CallbackInfo ci) {
		Pickaxe pickaxe = Pickaxe.getInstance();
		if (packet.content().getString().matches("^\\[.\\] You found a chest!$") && pickaxe.isInPickaxe()) {
			double chestTimer = 1000;
			MinecraftClient mc = MinecraftClient.getInstance();
			assert mc.player != null;
			DefaultedList<ItemStack> armor = mc.player.getInventory().armor;
			for (int i = 0; i < 4; i++) {
				try {
					String id = Objects.requireNonNull(Objects.requireNonNull(armor.get(i).getNbt())).getCompound("PublicBukkitValues").getString("hypercube:id");
					if (id.startsWith("treasure")) {
						if (id.contains("_seeker")) chestTimer *= 0.9;
						else chestTimer *= 0.8;
					}
				} catch (NullPointerException ignored) {}
			}
			pickaxe.chestTimer = chestTimer;
			Timer timer = new Timer();
			timer.scheduleAtFixedRate(new TimerTask() {
				@Override
				public void run() {
					if ((int) (--pickaxe.chestTimer) == 0) {
						if (Options.getInstance().cctconfig.soundEnabled) mc.getSoundManager().play(PositionedSoundInstance.master(SoundEvents.BLOCK_CHEST_LOCKED, 1, 1));
						timer.cancel();
						timer.purge();
					}
				}
			}, 1000, 1000);
		}
	}
}
