package tech.showierdata.pickaxe;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import org.lwjgl.glfw.GLFW;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;

import dev.isxander.yacl3.gui.AbstractWidget;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.fabricmc.fabric.api.client.screen.v1.Screens;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.ClientBossBar;
import net.minecraft.client.gui.screen.ConnectScreen;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.network.ServerAddress;
import net.minecraft.client.network.ServerInfo;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableTextContent;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;

import static net.minecraft.server.command.CommandManager.*;

import tech.showierdata.pickaxe.Commands.PickaxeCommandManager;
import tech.showierdata.pickaxe.config.Options;
import tech.showierdata.pickaxe.mixin.PlayerHudListMixin;
import tech.showierdata.pickaxe.server.CommandHelper;

public class Pickaxe implements ModInitializer {
	// This logger is used to write text to the console and the log file.
	// It is considered best practice to use your mod id as the logger's name.
	// That way, it's clear which mod wrote info, warnings, and errors.
	public static final char DIAMOND_CHAR = '◆';
	public static final Logger LOGGER = LoggerFactory.getLogger(String.format("%s", Constants.PICKAXE_STRING));
	public static Pickaxe instence;

	public boolean connectButtenPressed = false;
	public static final CommandHelper commandHelper = CommandHelper.getInstance();

	public static Pickaxe getInstance() {
		return instence;
	}

	public static PickaxeCommand[] getCommands() {
		return new PickaxeCommand[] {
				new PickaxeCommand("help",
						"This command!",
						true,
						new String[] {}),
				new PickaxeCommand("itemlock", "Locks your held item", new String[] {}),
				new PickaxeCommand("pay", "Pay's the specified person the amount specified", new String[] {
						"[user]",
						"[ammount]"
				}),
				new PickaxeCommand("up", "Teleports you to the surface", new String[] {}),
				new PickaxeCommand("bpname", "Sets the name of the backpack\n     (supports colors from /colors)",
						new String[] {
								"[name]"
						}),
				new PickaxeCommand("dye", "Dyes a leather item to any color", new String[] {
						"[color]"
				})
		};

	}

	public static HashMap<String, PickaxeCommand> getHandledCommands() {
		PickaxeCommand[] commands = getCommands();
		HashMap<String, PickaxeCommand> ret = new HashMap<>();
		for (PickaxeCommand command : commands) {
			if (command.handled) {
				ret.put(command.name, command);
			}
		}
		return ret;
	}

	public Connection conn;

	public Vec3d rel_spawn = new Vec3d(0, 0, 0);

	public ArrayList<UUID> removed_bossbars = new ArrayList<UUID>();

	public boolean lastConnectedStatus = false;

	public boolean isInPickaxe() {
		if (!Options.getInstance().enabled) {
			return false;
		}

		MinecraftClient client = MinecraftClient.getInstance();
		if (client.world == null) {
			return false;
		}
		if (client.isInSingleplayer()) {
			return false;
		}
		if (!client.getCurrentServerEntry().address.endsWith(Constants.SERVER_IP)) {
			return false;
		}

		Vec3d pos = client.player.getPos().subtract(Constants.Spawn);
		boolean status = pos.x > -1000 && pos.z > -1000 && pos.x < 1000 && pos.z < 1000;

		if (status && !lastConnectedStatus && Options.getInstance().AutoCL) {
			client.getNetworkHandler().sendChatCommand("c l");
		}

		lastConnectedStatus = status;
		return status;
	}

	@Override
	public void onInitialize() {
		// This code runs as soon as Minecraft is in a mod-load-ready state.
		// However, some things (like resources) may still be uninitialized.
		// Proceed with mild caution.
		Pickaxe.instence = this;

		LOGGER.info(String.format("Starting %s....", Constants.PICKAXE_STRING));

		PickaxeCommandManager commandManager = PickaxeCommandManager.getInstance();
		ClientCommandRegistrationCallback.EVENT.register(new ClientCommandRegistrationCallback() {
			public void register(CommandDispatcher<FabricClientCommandSource> dispatcher,
					CommandRegistryAccess registryAccess) {
				commandManager.register(dispatcher);
			}
		});
		register_callbacks();

		// send a get request to https://api.modrinth.com/v2/project/{id|slug}/version

		/*
		 * try {
		 * conn = DriverManager.getConnection("jdbc:sqlite:pickaxe.sqlite.db");
		 * if (conn == null) {
		 * throw new SQLException("DB Connection is NULL");
		 * }
		 * } catch (SQLException e) {
		 * LOGGER.error("Could not connect to mod DB.", e);
		 * }
		 */

		LOGGER.info(String.format("Finished loading %s...", Constants.PICKAXE_STRING));

	}

	private void drawCoords(DrawContext context, TextRenderer renderer) {
			String[] lines = String.format("X: %d,\nY: %d,\nZ: %d", Math.round(rel_spawn.x), Math.round(rel_spawn.y),
					Math.round(rel_spawn.z)).split("\n");

			// calculate the width of the text
			int width = 0;
			for (String line : lines) {
				width = Math.max(width, renderer.getWidth(line));
			}

			// get the top left corner of the screen
			int x = MinecraftClient.getInstance().getWindow().getScaledWidth() - width;

			// draw the text
			for (int i = 0; i < lines.length; i++) {
				String line = lines[i];
				int y = 3 + (i * (renderer.fontHeight + 1));
				context.drawTextWithShadow(renderer, line, x - 3, y, 0xFFFFFF);
			}
	}


	
	private void drawCoinBar(DrawContext context, TextRenderer renderer, MinecraftClient client) {
		
			try {
				String[] footer = ((PlayerHudListMixin) client.inGameHud.getPlayerListHud()).getFooter().getString()
						.split("\n");

				// get the coins from the footer
				String coins = '⛃' + footer[2].replaceAll("[^0-9\\.]", "");

				// Calculate the hunger bar values
				int xhp = client.getWindow().getScaledWidth() / 2 - 91;
				int ybottom = client.getWindow().getScaledHeight() - 39;

				// Define the height of the hunger bar

				// Calculate the health and hunger bar widths
				int hpWidth = Math.round(20 / 2.0f * 18.0f);

				// Calculate the x-coordinate of the right edge of the health bar
				int xhpRight = xhp + hpWidth;

				// Draw the custom hunger bar

				// Draw the coins value
				int coinsWidth = renderer.getWidth(coins);
				context.drawTextWithShadow(renderer, coins.toString(), xhpRight - coinsWidth, ybottom, 0xFFFF00);
			} catch (Exception e) {
				Pickaxe.LOGGER.error("Error while drawing custom hunger bar", e);

				// Draw a empty hunger bar with 0 coins
				// Calculate the hunger bar values
				int xhp = client.getWindow().getScaledWidth() / 2 - 91;
				int ybottom = client.getWindow().getScaledHeight() - 39;

				// Define the height of the hunger bar

				// Calculate the health and hunger bar widths
				int hpWidth = Math.round(20 / 2.0f * 18.0f);

				// Calculate the x-coordinate of the right edge of the health bar
				int xhpRight = xhp + hpWidth;

				// Draw the custom hunger bar
				String coins = '⛃' + "0 (Error)";
				int coinsWidth = renderer.getWidth(coins);
				context.drawTextWithShadow(renderer, coins.toString(), xhpRight - coinsWidth, ybottom, 0xFFFF00);

			}
	}



	private void register_callbacks() {
		// @up keybind
		KeyBinding keyBinding = KeyBindingHelper.registerKeyBinding(
				new KeyBinding("key.pickaxe.up", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_R, "category.pickaxe.keybinds"));

		ClientTickEvents.END_CLIENT_TICK.register(new ClientTickEvents.EndTick() {
			public void onEndTick(MinecraftClient client) {
				if (!isInPickaxe()) {
					return;
				}

				Vec3d playerPos = client.player.getPos();
				Vec3d pos = playerPos.subtract(Constants.Spawn);
				rel_spawn = pos;

				boolean foundRadBossBar = false;

				for (ClientBossBar bar : ((IBossBarHudMixin) (Object) client.inGameHud.getBossBarHud()).getBossBars()
						.values()) {
					foundRadBossBar = Options.getInstance().XPBarType.detect(bar);
				}

				if (!foundRadBossBar) {
					client.player.experienceProgress = 0;
				}

				//disable keybind in all areas except main mine; mesa mine; and sputtrooms
				while (keyBinding.wasPressed()) {
						client.player.networkHandler.sendChatMessage("@up");
				
				
				}
			}
		});
		

		HudRenderCallback.EVENT.register((context, tickDelta) -> {

			if (!isInPickaxe()) {
				return;
			}

			MinecraftClient client = MinecraftClient.getInstance();
			TextRenderer renderer = client.textRenderer; // ignore

			try {
				drawCoords(context, renderer);
			} catch (Exception e) {
				Pickaxe.LOGGER.error("Error while drawing coords", e);
			}

			try {
				drawCoinBar(context, renderer, client);
			} catch (Exception e) {
				Pickaxe.LOGGER.error("Error while drawing coin bar", e);
			}

	
		});
	


		ScreenEvents.AFTER_INIT.register(new Identifier("pickaxe", "button"), (client, screen, scaledWidth, scaledHeight) -> {
			 	if (screen instanceof TitleScreen) {
	
					final List<ClickableWidget> buttons = Screens.getButtons(screen);

					int index = 0;
					int y = screen.height / 4 + 24;
					for (int i = 0; i < buttons.size(); i++) {
						ClickableWidget button = buttons.get(i);
						if (Pickaxe.buttonHasText(button, "menu.multiplayer") && button.visible) {
							index = i + 1;		
							y = button.getY();
						}
						
					}
					
					if (FabricLoader.getInstance().isModLoaded("recode")) {
						y += 24;
					}
					if (index != -1) {
						Screens.getButtons(screen).add(ButtonWidget.builder(Text.literal("\u26CF"), (btn) -> {
							LOGGER.info("Joining Pickaxe...");

							MinecraftClient mc = MinecraftClient.getInstance();
							ServerAddress address = ServerAddress.parse(Constants.SERVER_IP);
							ServerInfo serverInfo = new ServerInfo("Diamondfire", Constants.SERVER_IP, false);

							Pickaxe.getInstance().connectButtenPressed = true; // Just incase java is odd, and connectButtonPressed = true is odd
							ConnectScreen.connect(screen, mc, address, serverInfo, false);
							
							
						})
						.position(screen.width / 2 + 104, y)
						.size(20, 20)
						.build());
					}
   

				}
		});

		CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
			
		});

	}
	private static boolean buttonHasText(ClickableWidget button, String translationKey) {
			Text content = button.getMessage();
			return content instanceof TranslatableTextContent  tr && tr.getKey().equals(translationKey);
		}

} 