package tech.showierdata.pickaxe;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.message.v1.ClientReceiveMessageEvents;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.fabricmc.fabric.api.client.screen.v1.Screens;
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
import net.minecraft.item.ItemStack;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.text.Texts;
import net.minecraft.text.TranslatableTextContent;
import net.minecraft.util.Colors;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.glfw.GLFW;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tech.showierdata.pickaxe.Commands.PickaxeCommandManager;
import tech.showierdata.pickaxe.config.CCTLocation;
import tech.showierdata.pickaxe.config.Options;
import tech.showierdata.pickaxe.mixin.PlayerHudListMixin;
import tech.showierdata.pickaxe.server.Ad;
import tech.showierdata.pickaxe.server.CommandHelper;
import tech.showierdata.pickaxe.server.Plot;
import tech.showierdata.pickaxe.server.Regexs;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

@SuppressWarnings("ReassignedVariable")
public class Pickaxe implements ModInitializer {
	// This logger is used to write text to the console and the log file.
	// It is considered best practice to use your mod id as the logger's name.
	// That way, it's clear which mod wrote info, warnings, and errors.
	public static final char DIAMOND_CHAR = '◆';
	public static final Logger LOGGER = LoggerFactory.getLogger(String.format("%s", Constants.PICKAXE_STRING));
	public static Pickaxe instence;

	public boolean connectButtenPressed = false;
	public double chestTimer = 0;
	public static final CommandHelper commandHelper = CommandHelper.getInstance();

	public boolean adFound = false;

	public Text prevMessage;

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

	public Vec3d rel_spawn = new Vec3d(0, 0, 0);


	public boolean lastConnectedStatus = false;

	private static boolean buttonHasText(@NotNull ClickableWidget button, @SuppressWarnings("SameParameterValue") String translationKey) {
		Text content = button.getMessage();
		return content instanceof TranslatableTextContent tr && tr.getKey().equals(translationKey);
	}

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
		if (!Objects.requireNonNull(client.getCurrentServerEntry()).address.endsWith(Constants.SERVER_IP)) {
			return false;
		}

		assert client.player != null;
		Vec3d pos = client.player.getPos().subtract(Constants.Spawn);
		boolean status = pos.x > -1000 && pos.z > -1000 && pos.x < 1000 && pos.z < 1000;

		if (status && !lastConnectedStatus && Options.getInstance().AutoCL) {
			Objects.requireNonNull(client.getNetworkHandler()).sendChatCommand("c l");
		}

		lastConnectedStatus = status;
		return status;
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

	@SuppressWarnings("unused")
	public String chatPrefix = "§6[§rPickaxe§6]§r ";



	@Override
	public void onInitialize() {
		// This code runs as soon as Minecraft is in a mod-load-ready state.
		// However, some things (like resources) may still be uninitialized.
		// Proceed with mild caution.
		Pickaxe.instence = this;

		LOGGER.info(String.format("Starting %s....", Constants.PICKAXE_STRING));

		PickaxeCommandManager commandManager = PickaxeCommandManager.getInstance();
		ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> commandManager.register(dispatcher));
		register_callbacks();

		// send a get request to https://api.modrinth.com/v2/project/{id|slug}/version


		LOGGER.info(String.format("Finished loading %s...", Constants.PICKAXE_STRING));

		Options.loadConfig();
	}

	private void drawCoinBar(DrawContext context, TextRenderer renderer, MinecraftClient client) {

			try {
				String[] footer = ((PlayerHudListMixin) client.inGameHud.getPlayerListHud()).getFooter().getString()
						.split("\n");

				// get the coins from the footer
				String coins = '⛃' + footer[2].replaceAll("[^0-9.]", "");

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
				context.drawTextWithShadow(renderer, coins, xhpRight - coinsWidth, ybottom, 0xFFFF00);
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
				String coins = "⛃0 (Error)";
				int coinsWidth = renderer.getWidth(coins);
				context.drawTextWithShadow(renderer, coins, xhpRight - coinsWidth, ybottom, 0xFFFF00);
			}
	}

	private void drawForge(DrawContext context, TextRenderer renderer, MinecraftClient client) {

			try {
				String[] footer = ((PlayerHudListMixin) client.inGameHud.getPlayerListHud()).getFooter().getString()
						.split("\n");

				// get the forge from the footer
				int forgePos = 4;
				while (!footer[forgePos].contains("Forge")) {
					forgePos++;
				}
				String forge = footer[forgePos].replaceAll("(Forge:|remaining)? *", "");

				// Calculate the hunger bar values
				int xhp = client.getWindow().getScaledWidth() / 2 - 91;
				int ybottom = client.getWindow().getScaledHeight() - 39;

				// Define the height of the hunger bar

				// Calculate the health and hunger bar widths
				int hpWidth = Math.round(20 / 2.0f * 18.0f);

				// Calculate the x-coordinate of the right edge of the health bar
				int xhpRight = xhp + hpWidth;

				// Draw the custom hunger bar

				// Draw the forge value
				int forgeWidth = renderer.getWidth(forge);
				int forgeColor = 0x000000;
				//noinspection EnhancedSwitchMigration
				switch (forge) {
					case "Ready":
						forgeColor = 0x00FF00;
						break;
					case "FINISHED":
						forgeColor = 0x11DD11;
						break;
					default:
						forgeColor = 0xaaaaaa;
						break;
				}
				context.drawTextWithShadow(renderer, forge, xhpRight - forgeWidth, ybottom - 11, forgeColor);

				String tag = "Forge: ";
				int tagWidth = renderer.getWidth(tag);
				context.drawTextWithShadow(renderer, tag, xhpRight - forgeWidth - tagWidth, ybottom - 11, 0xFFFFFF);
			} catch (Exception e) {
				Pickaxe.LOGGER.error("Error while drawing custom hunger bar (Forge)", e);

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
				String forge = "Forge: ERROR";
				int forgeWidth = renderer.getWidth(forge);
				context.drawTextWithShadow(renderer, forge, xhpRight - forgeWidth, ybottom - 11, 0xFF0000);
			}
	}

	private void drawCCT(DrawContext context, TextRenderer renderer) {
            List<Text> texts = new ArrayList<>();
            texts.add(Text.literal("Pickaxe Chest:").setStyle(Style.EMPTY.withColor(0xD27D2D)));
            StringBuilder sb = new StringBuilder();
		MinecraftClient client = MinecraftClient.getInstance();
		if ((int) chestTimer == 0) sb.append("READY");
            else {
                if (chestTimer >= 60) {
					sb.append((int) (chestTimer / 60));
                    sb.append("m ");
                }
			sb.append((int) (chestTimer % 60));
                sb.append("s");
            }
		texts.add(Text.literal(sb.toString()).setStyle(Style.EMPTY.withColor(((int) chestTimer == 0) ? Formatting.GREEN : Formatting.WHITE)));
		int y = 5;

		if (Options.getInstance().cctconfig.location == CCTLocation.BOTTEMRIGHT) {
			y = client.getWindow().getScaledHeight() - client.textRenderer.fontHeight - 5;
		}

		context.drawTextWithShadow(renderer, Texts.join(texts, Text.literal(" ")), 5, y, Colors.WHITE);
	}

	private void register_callbacks() {
		// @up keybind
		KeyBinding keyBinding = KeyBindingHelper.registerKeyBinding(
				new KeyBinding("key.pickaxe.up", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_R, "category.pickaxe.keybinds"));

		ClientTickEvents.END_CLIENT_TICK.register(client -> {
			if (!isInPickaxe()) {
				return;
			}

			assert client.player != null;
			Vec3d playerPos = client.player.getPos();
			rel_spawn = playerPos.subtract(Constants.Spawn);

			boolean foundRadBossBar = false;

			//noinspection RedundantCast
			for (ClientBossBar bar : ((IBossBarHudMixin) (Object) client.inGameHud.getBossBarHud()).pickaxe_mod$getBossBars()
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
		});


		HudRenderCallback.EVENT.register((context, tickDelta) -> {

			MinecraftClient client = MinecraftClient.getInstance();
			TextRenderer renderer = client.textRenderer; // ignore
			
			boolean inPickaxe = isInPickaxe();
			Options options = Options.getInstance();

			if (options.cctconfig.enabled && (inPickaxe || options.cctconfig.enabledOutsidePickaxe)) try {
				drawCCT(context, renderer);
			} catch (Exception e) {
				Pickaxe.LOGGER.error("Error while drawing CCT", e);
			}

			if (!inPickaxe) return;

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

			try {
				drawForge(context, renderer, client);
			} catch (Exception e) {
				Pickaxe.LOGGER.error("Error while drawing forge UI", e);
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
						Screens.getButtons(screen).add(ButtonWidget.builder(Text.literal("⛏"), (btn) -> {
							LOGGER.info("Joining Pickaxe...");

							MinecraftClient mc = MinecraftClient.getInstance();
									ServerAddress address = ServerAddress.parse(Constants.NODE_IP);
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

		ClientReceiveMessageEvents.ALLOW_GAME.register((message, overlay) -> {
			Plot plot = Regexs.getLocateDetails(message.getString());
			if (plot != null) {
				Pickaxe.LOGGER.info("Located plot: " + plot.name);
				return true;
			}
			Ad ad = Regexs.getAdDetails(message.getString());
			if (ad != null) {
				if (!Pickaxe.getInstance().isInPickaxe()) return true;
				Pickaxe.LOGGER.info(String.format("An ad was skipped!: %s by %s, %s", ad.plot.name, ad.plot.owner, ad.desc));
				Pickaxe.getInstance().adFound = true;
				return false;
			}
			return true;
		});

	}

	public void renderHotbarIcons(DrawContext context, int x, int y, ItemStack stack) {
		MinecraftClient client = MinecraftClient.getInstance();
		Options options = Options.getInstance();
		if (this.isInPickaxe()) {
			// Calculate the position for the text icon above the item icon
			int textOffsetX = x + options.itemconfig.x;
			int textOffsetY = y - options.itemconfig.y; // You can adjust the offset based on your preference

			boolean debug = false;
			//noinspection ConstantValue
			if (debug) {
				try {
					Pickaxe.LOGGER.info(String.format("%s: %s", Objects.requireNonNull(stack.getSubNbt("PublicBukkitValues")).getString("hypercube:id"), stack.getOrCreateNbt().asString()));
				} catch (Exception ignored) {

				}
			}
			// Draw the item quantity as text above the item icon
			String text = "";
			Color color = new Color(0xFFFFFF);
			if (stack.getOrCreateNbt().contains("PublicBukkitValues") && Options.getInstance().ShowLockIcon) {

				if (Objects.requireNonNull(stack.getSubNbt("PublicBukkitValues")).getDouble("hypercube:sanded") == 1.0d) {
					text = "▒";
					color = options.itemconfig.sanded_color;
				}

                switch ((int) Objects.requireNonNull(stack.getSubNbt("PublicBukkitValues")).getDouble("hypercube:recomb")) {
                    case Constants.MANUAL_OVERCLOCK_VALUE, Constants.NATRAL_OVERCLOCK_VALUE -> {
                        text = "⛨";
						color = options.itemconfig.overclocker_color;
                    }
                    case Constants.MANUAL_SAGE_VALUE, Constants.NATRAL_SAGE_VALUE -> {
                        text = "⯫"; // Hermit star
						color = options.itemconfig.sage_color;
                    }
                }


				if (Objects.requireNonNull(stack.getSubNbt("PublicBukkitValues")).getFloat("hypercube:nodrop") == 1.0d) {
					text = "⚓";
					color = new Color(0x808080);
				}
			}
			context.drawText(client.textRenderer, text, textOffsetX, textOffsetY, color.getRGB(), false); // You can set the color (0xFFFFFF for white)

		}

	}
} 