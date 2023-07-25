package tech.showierdata.pickaxe.Commands;

import static com.mojang.brigadier.arguments.StringArgumentType.*;
import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.*;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;

import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;

public class PickaxeCommandManager {
	static PickaxeCommandManager  INSTANCE = null;
	
	public static CommandBase[] getCommands() {
		return new CommandBase[]{
			new SearchCommand(),
		};
	}

	PickaxeCommandManager() {
		INSTANCE = this;
	};
	
	public void register(CommandDispatcher<FabricClientCommandSource> dispatcher) {
		LiteralArgumentBuilder<FabricClientCommandSource> literalText = literal("pickaxe");
		
		for (CommandBase cmd: getCommands()) {
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
