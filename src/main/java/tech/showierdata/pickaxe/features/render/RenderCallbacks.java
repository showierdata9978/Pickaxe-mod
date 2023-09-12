package tech.showierdata.pickaxe.features.render;

import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.fabricmc.fabric.api.client.screen.v1.Screens;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ConnectScreen;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.network.ServerAddress;
import net.minecraft.client.network.ServerInfo;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.text.Texts;
import net.minecraft.util.Colors;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import tech.showierdata.pickaxe.Constants;
import tech.showierdata.pickaxe.Pickaxe;
import tech.showierdata.pickaxe.config.CCTLocation;
import tech.showierdata.pickaxe.config.Options;
import tech.showierdata.pickaxe.mixin.PlayerHudListMixin;

import java.util.ArrayList;
import java.util.List;

public class RenderCallbacks {
    public static RenderCallbacks instance = new RenderCallbacks();
    public double chestTimer = 0;
    public boolean connectButtenPressed = false;

    RenderCallbacks() {

    }

    public void register() {
        HudRenderCallback.EVENT.register((context, tickDelta) -> {

            MinecraftClient client = MinecraftClient.getInstance();
            TextRenderer renderer = client.textRenderer; // ignore

            boolean inPickaxe = Pickaxe.getInstance().isInPickaxe();
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
                                Pickaxe.LOGGER.info("Joining Pickaxe...");

                                MinecraftClient mc = MinecraftClient.getInstance();
                                ServerAddress address = ServerAddress.parse(Constants.NODE_IP);
                                ServerInfo serverInfo = new ServerInfo("Diamondfire", Constants.SERVER_IP, false);

                                RenderCallbacks.instance.connectButtenPressed = true; // Just incase java is odd, and connectButtonPressed = true is odd
                                ConnectScreen.connect(screen, mc, address, serverInfo, false);


                            })
                            .position(screen.width / 2 + 104, y)
                            .size(20, 20)
                            .build());
                }


            }
        });
    }


    private void drawCoords(DrawContext context, TextRenderer renderer) {
        Pickaxe pickaxe = Pickaxe.getInstance();
        String[] lines = String.format("X: %d,\nY: %d,\nZ: %d", Math.round(pickaxe.rel_spawn.x), Math.round(pickaxe.rel_spawn.y),
                Math.round(pickaxe.rel_spawn.z)).split("\n");

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
}
