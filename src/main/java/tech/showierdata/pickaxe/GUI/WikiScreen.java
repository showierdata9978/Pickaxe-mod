package tech.showierdata.pickaxe.GUI;

import net.fabricmc.fabric.api.client.screen.v1.Screens;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.NbtTextContent;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import tech.showierdata.pickaxe.Pickaxe;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.StringNbtReader;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import net.minecraft.registry.Registries;

public class WikiScreen extends Screen {
	static List<ItemStack> items = loadItems();
	
	public WikiScreen() {
		super(Text.literal("Wiki"));
		registerClickables();
	}



	//list with items
	public static List<ItemStack> loadItems() {
		Pickaxe.LOGGER.info("Loading Items");
		//send a GET request to the wiki api
				//send an http request to the api
		//https://pickaxe.monocromeninja.replit.co/json/items
		JsonArray data;
		CloseableHttpClient client = HttpClients.custom()
			.disableContentCompression()
			.build();
		try {

			HttpGet request = new HttpGet("https://pickaxe.showierdata.xyz/json/items");

			CloseableHttpResponse response = client.execute(request);
			HttpEntity entity = response.getEntity();

			String body = EntityUtils.toString(entity, Charset.forName("UTF-8").name());

			Gson gson = new Gson();

			data = gson.fromJson(body, JsonArray.class);
			
		} catch (IllegalStateException e) {
			Pickaxe.LOGGER.error("I can't find this error anywhere\n{}", e);
			return new ArrayList<ItemStack>();
		} catch (Exception e) {
			Pickaxe.LOGGER.error("",e);
			return new ArrayList<ItemStack>();
		}
		if (data == null) {
			Pickaxe.LOGGER.error("Data is NULL");
			return new ArrayList<ItemStack>();
		}
		List<ItemStack> items = new ArrayList<ItemStack>();

		for (int i = 0; i < data.size(); i++) {
			JsonObject item = data.get(i).getAsJsonObject();

			Item convertedItem = Registries.ITEM.get(new Identifier(item.get("id").getAsString()));

			/*
			 * {
			    "name": string,
    			"id": string,	
			    "enchanted": boolean,
			    "rarity": string,
 			    "path": string,
    			"lore": string[] or null
			}
			 */


			ItemStack itemStack = new ItemStack(convertedItem, 1);

			itemStack.setCustomName(Text.of(item.get("name").getAsString()));

			if (item.get("enchanted").getAsBoolean()) {
				itemStack.addEnchantment(Registries.ENCHANTMENT.get(new Identifier("minecraft:protection")), 1);
			}

			List<JsonElement> lore = null;
			NbtList loreTag = new NbtList();
			//add lore
			try {
				lore = item.get("lore").getAsJsonArray().asList();

				for (int o = 0; o < lore.size(); o++) {

					String loreLine = lore.get(o).getAsString();

					try {
						NbtElement loreNbtElement = StringNbtReader.parse("{\"text\":\"" + loreLine + "\"}");
						loreTag.add(loreNbtElement);
					} catch (Exception e) {
						Pickaxe.LOGGER.error("",e);


					}
				}
			} catch (IllegalStateException e) {
				//Pickaxe.LOGGER.error("{} has no lore\n{}", item.get("name").getAsString(), e);
				// Juz ignore it :)
			}

			itemStack.getOrCreateNbt().put("display", loreTag);

			items.add(itemStack);

			 


		}

		Pickaxe.LOGGER.info("Loaded Items");
		return items;

	}

	public void registerClickables() {
		Screens.getButtons(this).add(ButtonWidget.builder(Text.literal("Reload Items"), (buttonWidget) -> {
			WikiScreen.items = loadItems();
		}).build());
	}

	@Override
	public void render(DrawContext context, int mouseX, int mouseY, float delta) {
		super.render(context, mouseX, mouseY, delta);
		//render background

		this.renderBackground(context);

		// Reload Items Button
		
	

	}

}
