package tech.showierdata.pickaxe;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

import org.lwjgl.glfw.GLFW;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.hud.ClientBossBar;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.text.Text;
import net.minecraft.util.math.Vec3d;

import tech.showierdata.pickaxe.config.Options;
import tech.showierdata.pickaxe.mixin.PlayerHudListMixin;
import tech.showierdata.pickaxe.server.CommandHelper;

public class Pickaxe implements ModInitializer {
	// This logger is used to write text to the console and the log file.
	// It is considered best practice to use your mod id as the logger's name.
	// That way, it's clear which mod wrote info, warnings, and errors.
    public static final char PICKAXE_EMOJI = '⛏';
	public static final char DIAMOND_CHAR = '◆';
	public static final Logger LOGGER = LoggerFactory.getLogger(String.format("%c", PICKAXE_EMOJI));
	public static final Marker FATAL = MarkerFactory.getMarker("FATAL");
	public static Pickaxe instence;
	public static final Vec3d Pickaxe_Spawn = new Vec3d( 7085, 200, 4115);

	public static final CommandHelper commandHelper = CommandHelper.getInstance();
	public static Pickaxe getInstance() {
		return instence;
	}
	public static PickaxeCommand[] getCommands() {
		return new PickaxeCommand[]{
			new PickaxeCommand("help", 
				"This command!", 
				true,
				new String[]{}
			),
			new PickaxeCommand("itemlock", "Locks your held item", new String[]{}),
			new PickaxeCommand("pay", "Pay's the specified person the amount specified", new String[]{
				"[user]",
				"[ammount]"
			}),
			new PickaxeCommand("up", "Teleports you to the surface", new String[]{}),
			new PickaxeCommand("bpname", "Sets the name of the backpack\n     (supports colors from /colors)", new String[]{
				"[name]"
			}),
			new PickaxeCommand("dye", "Dyes a leather item to any color", new String[]{
				"[color]"
			})
		};
		
	}


	public static HashMap<String, PickaxeCommand> getHandledCommands() {
		PickaxeCommand[] commands = getCommands();
		HashMap<String, PickaxeCommand> ret = new HashMap<>();
		for (PickaxeCommand command: commands) {
			if (command.handled) {
				ret.put(command.name, command);
			}
		}
		return ret;
	}

	public Connection conn;

	public Vec3d rel_spawn =  new Vec3d(0, 0,0);
	

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
    	if (!client.getCurrentServerEntry().address.endsWith("mcdiamondfire.com")) {
        	return false;
   	 	}

		
    	Vec3d pos = client.player.getPos().subtract(Pickaxe_Spawn);
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

		LOGGER.info(String.format("Starting %c mod....", PICKAXE_EMOJI));

		register_callbacks();

		// send a get request to https://api.modrinth.com/v2/project/{id|slug}/version

			
		
		/*try {
			conn = DriverManager.getConnection("jdbc:sqlite:pickaxe.sqlite.db");
			if (conn == null) {
				throw new SQLException("DB Connection is NULL");
			}
		} catch (SQLException  e) {
			LOGGER.error("Could not connect to mod DB.", e);
		}*/

		LOGGER.info(String.format("Finished loading %c....", PICKAXE_EMOJI));

	}
	
	

	private void register_callbacks() {
		// @up keybind
		KeyBinding keyBinding = KeyBindingHelper.registerKeyBinding(new KeyBinding("key.pickaxe.up", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_R, "category.pickaxe.keybinds"));

		ClientTickEvents.END_CLIENT_TICK.register(new ClientTickEvents.EndTick() {
		    public void onEndTick(MinecraftClient client) {
        		if (!isInPickaxe()) {
					return;
				}

        		Vec3d playerPos = client.player.getPos();
		        Vec3d pos = playerPos.subtract(Pickaxe_Spawn);
        		rel_spawn = pos;

				boolean foundRadBossBar = false;

				for (ClientBossBar bar : ((IBossBarHudMixin) (Object) client.inGameHud.getBossBarHud()).getBossBars().values()) {
					foundRadBossBar = Options.getInstance().XPBarType.detect(bar);
				}
				
				if (!foundRadBossBar) {
					client.player.experienceProgress = 0;
				}

				while (keyBinding.wasPressed()) {
					client.player.networkHandler.sendChatMessage("@up");
				}
			}
		});
		
		HudRenderCallback.EVENT.register((matrixStack, tickDelta) -> {

			if (!isInPickaxe()) {
				return;
			}

			MinecraftClient client = MinecraftClient.getInstance();
			TextRenderer renderer = client.textRenderer; //ignore
			
			String[] lines = String.format("X: %d,\nY: %d,\nZ: %d", Math.round(rel_spawn.x), Math.round(rel_spawn.y), Math.round(rel_spawn.z)).split("\n");


			//calculate the width of the text
			int width = 0;
			for (String line : lines) {
    			width = Math.max(width, renderer.getWidth(line));
			}

			//get the top left corner of the screen
			int x = MinecraftClient.getInstance().getWindow().getScaledWidth() - width;

			//draw the text
			for (int i = 0; i < lines.length; i++) {
    			String line = lines[i];
   		 		int y = 3 + (i * (renderer.fontHeight + 1));
    			renderer.drawWithShadow(matrixStack, line, x - 3, y, 0xFFFFFF);
			}
			//get the coins from the PlayerHud
			try {
				String[] footer = ((PlayerHudListMixin) client.inGameHud.getPlayerListHud()).getFooter().getString().split("\n");

				//get the coins from the footer
				String coins = '⛃' + footer[2].replaceAll("[^0-9\\.]", "");


				// Calculate the hunger bar values
				int xhp = client.getWindow().getScaledWidth() / 2 - 91;
				int ybottom = client.getWindow().getScaledHeight() - 39;

				// Define the height of the hunger bar
				int hungerHeight = 10;

				// Calculate the health and hunger bar widths
				int hpWidth = Math.round(20 / 2.0f * 18.0f);

				// Calculate the x-coordinate of the right edge of the health bar
				int xhpRight = xhp + hpWidth;

				// Draw the custom hunger bar
				DrawableHelper.fill(matrixStack, xhp+(hpWidth/2)  , ybottom-1, xhpRight+3, ybottom + hungerHeight-1, 0xFFFF0000);



				// Draw the coins value
				int coinsWidth = renderer.getWidth(coins);
				renderer.drawWithShadow(matrixStack, coins, xhpRight - coinsWidth, ybottom, 0xFFFF00);
			} catch (Exception e) {
				LOGGER.error("Error while drawing custom hunger bar", e);

				// Draw a empty hunger bar with 0 coins
				// Calculate the hunger bar values
				int xhp = client.getWindow().getScaledWidth() / 2 - 91;
				int ybottom = client.getWindow().getScaledHeight() - 39;

				// Define the height of the hunger bar
				int hungerHeight = 10;

				// Calculate the health and hunger bar widths
				int hpWidth = Math.round(20 / 2.0f * 18.0f);

				// Calculate the x-coordinate of the right edge of the health bar
				int xhpRight = xhp + hpWidth;

				// Draw the custom hunger bar
				DrawableHelper.fill(matrixStack, xhp+(hpWidth/2)  , ybottom-1, xhpRight+3, ybottom + hungerHeight-1, 0xFFFF0000);
				String coins = '⛃' + "0 (Error)";
				int coinsWidth = renderer.getWidth(coins);
				renderer.drawWithShadow(matrixStack, coins, xhpRight - coinsWidth, ybottom, 0xFFFF00);

			}
		});
	}
}