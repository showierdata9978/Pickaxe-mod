package tech.showierdata.pickaxe;

import imgui.ImGui;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.message.v1.ClientReceiveMessageEvents;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.fabricmc.fabric.api.client.screen.v1.Screens;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.ChatFormatting;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.LerpingBossEvent;
import net.minecraft.client.gui.screens.ConnectScreen;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.client.multiplayer.ServerData.Type;
import net.minecraft.client.multiplayer.resolver.ServerAddress;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentUtils;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.contents.TranslatableContents;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.CommonColors;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.glfw.GLFW;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import tech.showierdata.pickaxe.commands.HelpCommandController;
import tech.showierdata.pickaxe.commands.PassthroughCommand;
import tech.showierdata.pickaxe.config.TimerLocation;
import tech.showierdata.pickaxe.config.MDTConfig;
import tech.showierdata.pickaxe.config.Options;
import tech.showierdata.pickaxe.mixin.PlayerHudListMixin;
import tech.showierdata.pickaxe.server.Ad;
import tech.showierdata.pickaxe.server.CommandHelper;
import tech.showierdata.pickaxe.server.Plot;
import tech.showierdata.pickaxe.server.Regexps;
import tech.showierdata.pickaxe.ui.NoteEditor;
import xyz.breadloaf.imguimc.Imguimc;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static tech.showierdata.pickaxe.Constants.HOTBAR_DIFFERENCE;

import com.mojang.blaze3d.platform.InputConstants;

@SuppressWarnings("ReassignedVariable")
public class Pickaxe implements ModInitializer {

	public static final char DIAMOND_CHAR = '◆';
	public static final Logger LOGGER = LoggerFactory.getLogger(String.format("%s", Constants.PICKAXE_STRING));
	public static Pickaxe instance;

	public boolean connectButtonPressed = false;
	public static final CommandHelper commandHelper = CommandHelper.getInstance();

	public boolean adFound = false;
	public boolean bossbarFound = true;


	public static final ResourceLocation COLORS = new ResourceLocation("pickaxe", "textures/gui/colors.png");
	public NoteEditor noteEditor;

	public static Pickaxe getInstance() {
		return instance;
	}

	public static PickaxeCommand[] getCommands() {
		return new PickaxeCommand[] {
				new PickaxeCommand("help",
						"This command! (Added by Pickaxe Mod)",
						new String[]{},
						new HelpCommandController()

                ),
				new PickaxeCommand("lockitem",
						"Locks your held item",
						new String[] {},
						new PassthroughCommand()
				),
				new PickaxeCommand("pay", "Pay's the specified person the amount specified",
						new String[] {"[user]", "[amount]" },
						new PassthroughCommand()
				),
				new PickaxeCommand("up",
						"Teleports you to the surface",
						new String[] {},
						new PassthroughCommand()
				),
				new PickaxeCommand("bpname",
						"Sets the name of the backpack\n     (supports colors from /colors)",
						new String[] {"[name]"},
						new PassthroughCommand()
				),
				new PickaxeCommand("dye",
						"Dyes a leather item to any color",
						new String[] {"[color]"},
						new PassthroughCommand()
				),
				new PickaxeCommand("item",
						"Sends the item you are holding in chat",
						new String[] {"[text]  "},
						new PassthroughCommand()
				),
				new PickaxeCommand("title",
						"Changes the name of a notebook",
						new String[] {"[name]"},
						new PassthroughCommand()
				),
				new PickaxeCommand("backup",
						"Backs your inventory up! Make sure to run often.",
						new String[] { },
						new PassthroughCommand()
				),
				new PickaxeCommand("ach",
						"Opens the achievement menu!",
						new String[] { },
						new PassthroughCommand()
				),
				new PickaxeCommand("trash",
						"Opens a trash can inventory (does the same thing as offhand while in inventory)",
						new String[] { },
						new PassthroughCommand()
				),
				new PickaxeCommand("wah",
						"Opens a shop for your WAH points.",
						new String[] {},
						new PassthroughCommand()
				),
				new PickaxeCommand("pay",
						"Pays a user",
						new String[]{"[username]"},
						new PassthroughCommand()
				),
				new PickaxeCommand("wiki",
						"Sends the link to the wiki (Added by Pickaxe Mod)",
						new String[] {},
						(name, args) -> {
							Minecraft client = Minecraft.getInstance();
                            assert client.player != null;
                            client.player.sendSystemMessage(Component.nullToEmpty("The Wiki is located at " + Constants.WIKI_LOCATION + "!"));
						}
				)

		};

	}

	public PickaxeCommand[] commands = getCommands();

	public Vec3 rel_spawn = new Vec3(0, 0, 0);


	public boolean lastConnectedStatus = false;

	private static boolean buttonHasText(@NotNull AbstractWidget button, @SuppressWarnings("SameParameterValue") String translationKey) {
		Component content = button.getMessage();
		return content instanceof TranslatableContents tr && tr.getKey().equals(translationKey);
	}

	public boolean isInPickaxe() {
		if (!Options.getInstance().enabled) {
			return false;
		}



		Minecraft client = Minecraft.getInstance();
		if (client.level == null) {
			return false;


		}
		if (client.isLocalServer()) {
			return false;
		}

		// replay mod fix
		if (Objects.isNull(client.getCurrentServer())) {
			return false;
		}

		if (!client.getCurrentServer().ip.endsWith(Constants.SERVER_IP)) {
			return false;
		}

		assert client.player != null;
		Vec3 pos = client.player.position().subtract(Constants.Spawn);
		boolean status = pos.x > -1000 && pos.z > -1000 && pos.x < 1000 && pos.z < 1000;

		if (status && !lastConnectedStatus && Options.getInstance().AutoCL) {
			Objects.requireNonNull(client.getConnection()).sendCommand("c l");
		}

		lastConnectedStatus = status;
		return status;
	}

	private void drawCords(GuiGraphics context, Font renderer) {
			if (!Options.getInstance().showCords) {
				return;
			}

			String[] lines = String.format("X: %d,\nY: %d,\nZ: %d", Math.round(rel_spawn.x), Math.round(rel_spawn.y),
					Math.round(rel_spawn.z)).split("\n");

			// calculate the width of the text
			int width = 0;
			for (String line : lines) {
				width = Math.max(width, renderer.width(line));
			}

			// get the top left corner of the screen
			int x = Minecraft.getInstance().getWindow().getGuiScaledWidth() - width;

			// draw the text
			for (int i = 0; i < lines.length; i++) {
				String line = lines[i];
				int y = 3 + (i * (renderer.lineHeight + 1));
				context.drawString(renderer, line, x - 3, y, 0xFFFFFF);
			}
	}

	@SuppressWarnings("unused")
	public String chatPrefix = "§6[§rPickaxe§6]§r ";



	@Override
	public void onInitialize() {
		// This code runs as soon as Minecraft is in a mod-load-ready state.
		// However, some things (like resources) may still be uninitialized.
		// Proceed with mild caution.
		Pickaxe.instance = this;

		LOGGER.info(String.format("Starting %s....", Constants.PICKAXE_STRING));

		this.noteEditor = new NoteEditor(this);
		register_callbacks();

		// send a get request to https://api.modrinth.com/v2/project/{id|slug}/version


		LOGGER.info(String.format("Finished loading %s...", Constants.PICKAXE_STRING));

		Options.loadConfig();
	}




	// Method to calculate common positioning values
	private BarPositioning calculateBarPositioning(Minecraft client) {
		int xhp = client.getWindow().getGuiScaledWidth() / 2 - 91;
		int ybottom = client.getWindow().getGuiScaledHeight() - 39;
		int hpWidth = Math.round(20 / 2.0f * 18.0f);
		int xhpRight = xhp + hpWidth;
		return new BarPositioning(xhp, ybottom, hpWidth, xhpRight);
	}

	// Utility method for drawing text on the bar
	private void drawTextOnBar(GuiGraphics context, Font renderer, String text, int x, int y, int color) {
		int textWidth = renderer.width(text);
		context.drawString(renderer, text, x - textWidth, y, color);
	}


	// Method to draw the coin bar
	private void drawCoinBar(GuiGraphics context, Font renderer, Minecraft client) {
		Options settings = Options.getInstance();
		if (!settings.hotBarConfig.showCoinsInHotBar) return;
		BarPositioning positioning = calculateBarPositioning(client);

 		if (settings.hotBarConfig.flip) {
			 positioning.ybottom -= HOTBAR_DIFFERENCE;
		}

		try {
			String[] footer = ((PlayerHudListMixin) client.gui.getTabList()).getFooter().getString()
					.split("\n");

			// Get the coins from the footer
			String coins = '⛃' + footer[2].replaceAll("[^0-9.]", "");

			drawTextOnBar(context, renderer, coins, positioning.xhpRight, positioning.ybottom, 0xFFFF00);
		} catch (Exception e) {
			// Handle exception
			String coins = "⛃0 (Error)";
			drawTextOnBar(context, renderer, coins, positioning.xhpRight, positioning.ybottom, 0xFFFF00);
		}
	}

	// Method to draw the forge status
	private void drawForge(GuiGraphics context, Font renderer, Minecraft client) {
		Options settings = Options.getInstance();

		if (!settings.hotBarConfig.showForgeStatus) return;
		BarPositioning positioning = calculateBarPositioning(client);

		if (!settings.hotBarConfig.flip) {
			positioning.ybottom -= HOTBAR_DIFFERENCE;

		}

		try {
			String[] footer = ((PlayerHudListMixin) client.gui.getTabList()).getFooter().getString()
					.split("\n");

			// Get the forge status from the footer
			int forgePos = 4;
			while (!footer[forgePos].contains("Forge")) {
				forgePos++;
			}
			String forge = footer[forgePos].replaceAll("(Forge:|remaining)? *", "");

			int forgeColor = switch (forge) {
				case "Ready" -> 0x00FF00;
				case "FINISHED" -> 0x11DD11;
				default -> 0xaaaaaa;
			};
			drawTextOnBar(context, renderer, forge, positioning.xhpRight, positioning.ybottom, forgeColor);

			String tag = "Forge: ";
			drawTextOnBar(context, renderer, tag, positioning.xhpRight - renderer.width(forge), positioning.ybottom, 0xFFFFFF);
		} catch (Exception e) {
			// Handle exception
			String forge = "Forge: ERROR";
			drawTextOnBar(context, renderer, forge, positioning.xhpRight, positioning.ybottom, 0xFF0000);
		}
	}



    public boolean readyPlayed = false;
	public boolean nowPlayed = false;
	private void drawMDT(GuiGraphics context, Font renderer) {

		List<Component> texts = new ArrayList<>();
		texts.add(Component.literal("Moon Door:").setStyle(Style.EMPTY.withColor(0x33CCFF)));
		StringBuilder sb = new StringBuilder();
		Minecraft client = Minecraft.getInstance();

		boolean soundEnabled = Options.getInstance().mdtConfig.soundEnabled;

		int time = Options.getInstance().mdtConfig.getMoonDoorTime();

		Style color = Style.EMPTY.withColor((time <= MDTConfig.MOON_WINDOW)? (time <= 0)? ChatFormatting.RED : ChatFormatting.AQUA : ChatFormatting.WHITE);

		if (time <= 0) { 
			sb.append("NOW");
			if (soundEnabled && !nowPlayed) {
				client.getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.ENDER_CHEST_OPEN, 1, 1));
				nowPlayed = true;
			}
		} else {
			if (time <= MDTConfig.MOON_WINDOW) {
				sb.append("READY ");
				if (soundEnabled && !readyPlayed) {
					// Played twice because it is quiet and volume doesn't work
					client.getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.BEACON_DEACTIVATE, 1, 1f));
					client.getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.BEACON_DEACTIVATE, 1, 1f));
					readyPlayed = true;
				}
			} else {
				time -= MDTConfig.MOON_WINDOW;
				readyPlayed = false;
				nowPlayed = false;
			}

			if (time >= 60) {
				sb.append(time / 60);
				sb.append("m ");
			}
			sb.append(time % 60);
			sb.append("s");
		}
		texts.add(Component.literal(sb.toString()).setStyle(color));
		
		TimerLocation mdtLoc = Options.getInstance().mdtConfig.location;

		int y = 5;

		if (mdtLoc == TimerLocation.BOTTOMLEFT) {
			y = client.getWindow().getGuiScaledHeight() - client.font.lineHeight - 5;
		}

		context.drawString(renderer, ComponentUtils.formatList(texts, Component.literal(" ")), 5, y, CommonColors.WHITE);

	}

	private void register_callbacks() {
		// @up keybind
		KeyMapping upKeybind = KeyBindingHelper.registerKeyBinding(
				new KeyMapping("key.pickaxe.up", InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_R, "category.pickaxe.keybinds"));

		KeyMapping notesKeybind = KeyBindingHelper.registerKeyBinding(
				new KeyMapping("key.pickaxe.notes", InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_N, "category.pickaxe.keybinds")
		);

		ClientTickEvents.END_CLIENT_TICK.register(client -> {
			if (!isInPickaxe()) {
				return;
			}

			assert client.player != null;
			Vec3 playerPos = client.player.position();
			rel_spawn = playerPos.subtract(Constants.Spawn);

			boolean foundRadBossBar = false;

			//noinspection RedundantCast
			for (LerpingBossEvent bar : ((IBossBarHudMixin) (Object) client.gui.getBossOverlay()).pickaxe_mod$getBossBars()
					.values()) {
				foundRadBossBar = Options.getInstance().XPBarType.detect(bar);
			}

			if (!foundRadBossBar) {
				client.player.experienceProgress = 0;
			}


			while (upKeybind.consumeClick()) {
				client.player.connection.sendChat("@up");
			}

			if (notesKeybind.consumeClick()) {
				notesKeybind.setDown(false); // prevent spam
				noteEditor.flip();
			}
		});


		HudRenderCallback.EVENT.register((context, tickDelta) -> {

			Minecraft client = Minecraft.getInstance();
			Font renderer = client.font; // ignore
			
			boolean inPickaxe = isInPickaxe();
			Options options = Options.getInstance();

			if (!inPickaxe) return;

			try {
				drawCords(context, renderer);
			} catch (Exception e) {
				Pickaxe.LOGGER.error("Error while drawing cords", e);
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

			if (options.mdtConfig.enabled) try {
				drawMDT(context, renderer);
			} catch (Exception e) {
				Pickaxe.LOGGER.error("Error while drawing Mood Door UI", e);
			}
		});


		ScreenEvents.AFTER_INIT.register(new ResourceLocation("pickaxe", "button"), (client, screen, scaledWidth, scaledHeight) -> {
			 	if (screen instanceof TitleScreen) {

					final List<AbstractWidget> buttons = Screens.getButtons(screen);

					int index = 0;
					int y = screen.height / 4 + 24;
					for (int i = 0; i < buttons.size(); i++) {
						AbstractWidget button = buttons.get(i);
						if (Pickaxe.buttonHasText(button, "menu.multiplayer") && button.visible) {
							index = i + 1;
							y = button.getY();
						}

					}

					if (FabricLoader.getInstance().isModLoaded("recode")) {
						y += 24;
					}
					if (index != -1) {
						Screens.getButtons(screen).add(Button.builder(Component.literal("⛏"), (btn) -> {
							LOGGER.info("Joining Pickaxe...");

							Minecraft mc = Minecraft.getInstance();
							ServerAddress address = ServerAddress.parseString(Constants.NODE_IP);
							ServerData serverInfo = new ServerData("DiamondFire", Constants.SERVER_IP, Type.OTHER);

							Pickaxe.getInstance().connectButtonPressed = true; // Just in case java is odd, and connectButtonPressed = true is odd
							ConnectScreen.startConnecting(screen, mc, address, serverInfo, false);
							

						})
						.pos(screen.width / 2 + 104, y)
						.size(20, 20)
						.build());
					}


				}
		});

		ClientReceiveMessageEvents.ALLOW_GAME.register((message, overlay) -> {
			Plot plot = Regexps.getLocateDetails(message.getString());
			if (plot != null) {
                Pickaxe.LOGGER.info("Located plot: {}", plot.name);
				return true;
			}
			Ad ad = Regexps.getAdDetails(message.getString());
			if (ad != null) {
				if (!Pickaxe.getInstance().isInPickaxe()) return true;
				Pickaxe.LOGGER.info(String.format("An ad was skipped!: %s by %s", ad.plot.name, ad.plot.owner));
				Pickaxe.getInstance().adFound = true;
				return false;
			}
			return true;
		});

	}

	public void renderHotbarIcons(GuiGraphics context, int x, int y, ItemStack stack) {
		Minecraft client = Minecraft.getInstance();
		Options options = Options.getInstance();
		if (this.isInPickaxe()) {
			// Calculate the position for the text icon above the item icon
			int textOffsetX = x + options.itemconfig.x;
			int textOffsetY = y - options.itemconfig.y; // You can adjust the offset based on your preference

			boolean debug = false;
			//noinspection ConstantValue
			if (debug) {
				try {
					Pickaxe.LOGGER.info(String.format("%s: %s", Objects.requireNonNull(stack.getTagElement("PublicBukkitValues")).getString("hypercube:id"), stack.getOrCreateTag().getAsString()));
				} catch (Exception ignored) {

				}
			}
			// Draw the item quantity as text above the item icon
			String text = "";
			Color color = new Color(0xFFFFFF);
			if (stack.getOrCreateTag().contains("PublicBukkitValues") && Options.getInstance().ShowLockIcon) {

				if (Objects.requireNonNull(stack.getTagElement("PublicBukkitValues")).getDouble("hypercube:sanded") == 1.0d) {
					text = "▒";
					color = options.itemconfig.sanded_color;
				}

                switch ((int) Objects.requireNonNull(stack.getTagElement("PublicBukkitValues")).getDouble("hypercube:recomb")) {
                    case Constants.MANUAL_OVERCLOCK_VALUE, Constants.NATURAL_OVERCLOCK_VALUE -> {
                        text = "⛨";
						color = options.itemconfig.overclocker_color;
                    }
                    case Constants.MANUAL_SAGE_VALUE, Constants.NATURAL_SAGE_VALUE -> {
                        text = "⯫"; // Hermit star
						color = options.itemconfig.sage_color;
                    }
                }


				if (Objects.requireNonNull(stack.getTagElement("PublicBukkitValues")).getFloat("hypercube:nodrop") == 1.0d) {
					text = "⚓";
					color = new Color(0x808080);
				}
			}
			context.drawString(client.font, text, textOffsetX, textOffsetY, color.getRGB(), false); // You can set the color (0xFFFFFF for white)

		}

	}
} 