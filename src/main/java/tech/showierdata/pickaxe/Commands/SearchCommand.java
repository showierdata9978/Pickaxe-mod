package tech.showierdata.pickaxe.Commands;


import tech.showierdata.pickaxe.Pickaxe;
import tech.showierdata.pickaxe.Commands.PickaxeCommandManager.CommandBase;
import tech.showierdata.pickaxe.GUI.WikiScreen;

import static com.mojang.brigadier.arguments.StringArgumentType.*;
import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.*;

import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;
import com.google.gson.Gson;

import com.google.gson.JsonArray;
import net.minecraft.client.MinecraftClient;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.fabricmc.loader.impl.lib.gson.JsonReader;
import net.minecraft.command.argument.ArgumentTypes;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;

public class SearchCommand implements CommandBase {
	

	public LiteralArgumentBuilder<FabricClientCommandSource> commandResponse(LiteralArgumentBuilder<FabricClientCommandSource> literalText) {
		literalText.then(
			literal("wiki")
					.executes(new Command<FabricClientCommandSource>() {
						@Override
						public int run(CommandContext<FabricClientCommandSource> context) throws CommandSyntaxException {

							
							MinecraftClient client = MinecraftClient.getInstance();

							client.setScreen(new WikiScreen());


							return 1;
						}
					}).build());
				

		return literalText;
	}
}
