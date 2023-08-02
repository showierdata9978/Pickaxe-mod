package tech.showierdata.pickaxe.Commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;

import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.literal;

public class PickaxeCommandManager {
	static PickaxeCommandManager  INSTANCE = null;

	PickaxeCommandManager() {
		INSTANCE = this;
	}

	public static CommandBase[] getCommands() {
		return new CommandBase[]{
			new SearchCommand(),
				new DebugCommand()
		};
	}

	public void register(CommandDispatcher<FabricClientCommandSource> dispatcher) {
		LiteralArgumentBuilder<FabricClientCommandSource> literalText = literal("pickaxe");

		for (CommandBase cmd : getCommands()) {
			literalText = cmd.commandResponse(literalText);
		}

		dispatcher.register(literalText);
		
	}

	public static PickaxeCommandManager getInstance() {
		if (INSTANCE == null) {
			INSTANCE = new PickaxeCommandManager();
		}
		return INSTANCE;
	}

	public interface CommandBase {
		LiteralArgumentBuilder<FabricClientCommandSource> commandResponse(LiteralArgumentBuilder<FabricClientCommandSource> builder);
	}
}
