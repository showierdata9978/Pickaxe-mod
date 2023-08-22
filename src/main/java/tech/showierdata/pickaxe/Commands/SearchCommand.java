package tech.showierdata.pickaxe.Commands;


import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;
import tech.showierdata.pickaxe.Commands.PickaxeCommandManager.CommandBase;
import tech.showierdata.pickaxe.Pickaxe;

import static com.mojang.brigadier.arguments.StringArgumentType.string;
import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.argument;
import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.literal;

public class SearchCommand implements CommandBase {

	
	private JsonArray fetchItems() {
		//send an http request to the api
		//https://pickaxe.monocromeninja.replit.co/json/items
		HttpClient client = HttpClientBuilder.create().build();
		try {
			String body = client.execute(
				new HttpGet("http://pickaxe.monocromeninja.replit.co/json/items")
			).getEntity().toString();

			Gson gson = new Gson();

			return gson.fromJson(body, JsonArray.class);

			
			
		
		} catch (Exception e) {
			Pickaxe.LOGGER.error("",e);
		}
	

		return null;
	}

	private com.google.gson.JsonObject fetchItem(String item) {
		HttpClient client = HttpClientBuilder.create().build();
		try {
			String body = client.execute(
				new HttpGet("http://pickaxe.monochromeninja.repl.co/json/item/" + item)
			).getEntity().toString();

			Gson gson = new Gson();

			return gson.fromJson(body, JsonObject.class);

			
			
		
		} catch (Exception e) {
			Pickaxe.LOGGER.error("",e);
		}
	

		return null;
	}

	public LiteralArgumentBuilder<FabricClientCommandSource> commandResponse(LiteralArgumentBuilder<FabricClientCommandSource> literalText) {
		literalText.then(
			literal("get")
				.then(argument("item", string()))
					.executes(new Command<FabricClientCommandSource>() {
						@Override
						public int run(CommandContext<FabricClientCommandSource> context) {
							String item = context.getArgument("item", String.class);
							JsonObject data = fetchItem(item);
							
							if (data == null) {
								context.getSource().sendFeedback(Text.literal("Data is NULL (Most likely your system did not find the wiki)"));
								return 1;
							}
							JsonArray loreArray = (JsonArray) data.get("lore");
							String[] lore = new String[loreArray.size()];

							for (int i = 0; i < loreArray.size(); i++) {
  							  lore[i] = loreArray.get(i).getAsString();
							}

							MutableText resp = Text.literal(
								"=== " + item  + " ===\n" +
								String.join("\n", lore) + "\n" +
								"Rarity: " + data.get("rarity").getAsString()
							);

							context.getSource().sendFeedback(resp);

							return 1;
						}
					}).build());
				/*.then(literal("refresh")
						.executes(new Command<FabricClientCommandSource>() {
							public int run(CommandContext<FabricClientCommandSource> context) throws CommandSyntaxException {
								data = fetchItems();

							if (data == null) {
								context.getSource().sendFeedback(Text.literal("Data is NULL (Most likely your system did not find the wiki)"));
								return 1;
							}
								return 1;
							}
					}).build()
				);*/

		return literalText;
	}
}
