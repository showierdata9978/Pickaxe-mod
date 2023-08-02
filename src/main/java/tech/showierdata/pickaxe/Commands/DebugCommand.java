package tech.showierdata.pickaxe.Commands;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import tech.showierdata.pickaxe.BackupHandler;

import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.literal;

public class DebugCommand implements PickaxeCommandManager.CommandBase {

	Boolean commandRunning = false;

	public DebugCommand() {
		super();

		ClientTickEvents.END_CLIENT_TICK.register(this::endTick);
	}

	@Override
	public LiteralArgumentBuilder<FabricClientCommandSource> commandResponse(LiteralArgumentBuilder<FabricClientCommandSource> builder) {
		return builder.then(literal("debug").executes(context -> {
			//open users inventory
			MinecraftClient client = MinecraftClient.getInstance();
			assert client.player != null;
			client.setScreen(new InventoryScreen(client.player));


			//wait for next frame 
			commandRunning = true;


			return 1;
		}).build());
	}

	private void endTick(MinecraftClient client) {
		if (!commandRunning) return;
		BackupHandler backupHandler = new BackupHandler(client);

		assert client.player != null;
		backupHandler.handleInventory(client.player.getInventory());

		//deregsiter
		commandRunning = false;


	}
}
