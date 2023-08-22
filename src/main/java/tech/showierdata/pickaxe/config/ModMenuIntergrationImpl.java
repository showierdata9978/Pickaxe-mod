package tech.showierdata.pickaxe.config;

import com.google.common.io.Files;
import com.google.gson.GsonBuilder;
import com.mojang.authlib.minecraft.client.ObjectMapper;
import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import dev.isxander.yacl3.api.*;
import dev.isxander.yacl3.api.controller.BooleanControllerBuilder;
import dev.isxander.yacl3.api.controller.ColorControllerBuilder;
import dev.isxander.yacl3.api.controller.EnumControllerBuilder;
import dev.isxander.yacl3.api.controller.IntegerFieldControllerBuilder;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;
import org.jetbrains.annotations.NotNull;
import tech.showierdata.pickaxe.Pickaxe;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Objects;


public class ModMenuIntergrationImpl implements ModMenuApi  {
	public static final String configPath = "config/pickaxe.properties.json";
	final ObjectMapper mapper = new ObjectMapper(new GsonBuilder()
			.registerTypeAdapter(Color.class, new ColorTypeAdapter())
			.create()
	);

	public ModMenuIntergrationImpl() {
	}

	@SuppressWarnings("ResultOfMethodCallIgnored")
	public void saveConfig() {
		Pickaxe.LOGGER.info("Saving config");


		try {
			File file = new File(configPath);
			if (!file.exists()) {
				file.getParentFile().mkdirs();
				file.createNewFile();
			}


			String data = mapper.writeValueAsString(Options.getInstance());


			Files.write(data.getBytes(), file);
		} catch (IOException e) {
			Pickaxe.LOGGER.error("Failed to save config", e);
		}


	}

	public void createGeneralScreen(YetAnotherConfigLib.@NotNull Builder builder) {
		builder.category(ConfigCategory.createBuilder()
				.name(Text.literal("General"))
				.option(Option.<Boolean>createBuilder()
						.name(Text.literal("Enable Mod"))
						.binding(true, () -> Options.getInstance().enabled, e -> {
							if (Pickaxe.getInstance().isInPickaxe()) {
								if (!e) {
									Objects.requireNonNull(MinecraftClient.getInstance().getNetworkHandler()).sendChatCommand("c g");
								} else {
									Objects.requireNonNull(MinecraftClient.getInstance().getNetworkHandler()).sendChatCommand("c l");
								}
							}
							Options.getInstance().enabled = e;
						})
						.controller(BooleanControllerBuilder::create)
						.build()
				)
				.option(Option.<XPBarEnum>createBuilder()

						.name(Text.literal("XP Bar Control"))
						.binding(XPBarEnum.Radiation, () -> Options.getInstance().XPBarType, e -> Options.getInstance().XPBarType = e)
						.controller((opt) -> EnumControllerBuilder.create(opt)
								.enumClass(XPBarEnum.class)
						)
						.build()
				)
				.option(Option.<Boolean>createBuilder()
						.name(Text.literal("Automaticly send /c l"))
						.binding(false, () -> Options.getInstance().AutoCL, e -> Options.getInstance().AutoCL = e)
						.controller(BooleanControllerBuilder::create)
						.build()
				)
				.option(Option.<Boolean>createBuilder()
						.name(Text.literal("Show Lock Icon"))
						.binding(false, () -> Options.getInstance().ShowLockIcon, e -> Options.getInstance().ShowLockIcon = e)
						.controller(BooleanControllerBuilder::create)
						.build()
				)
				.option(Option.<Boolean>createBuilder()
						.name(Text.literal("Hide Players not on pickaxe in tab"))
						.binding(true, () -> Options.getInstance().hideNonPickaxePlayers, e -> Options.getInstance().hideNonPickaxePlayers = e)
						.controller(BooleanControllerBuilder::create)
						.build()
				)

				.build()
		);


	}
	public void createItemConfig(YetAnotherConfigLib.@NotNull Builder builder) {
		builder.category(ConfigCategory.createBuilder()
				.name(Text.literal("Item Rendering"))
				.option(Option.<Integer>createBuilder()
						.name(Text.literal("x"))
						.binding(0, () -> Options.getInstance().itemconfig.x, e -> Options.getInstance().itemconfig.x = e)
						.controller(IntegerFieldControllerBuilder::create)
						.build()
				)
				.option(Option.<Integer>createBuilder()
						.name(Text.literal("y"))
						.binding(0, () -> Options.getInstance().itemconfig.y, e -> Options.getInstance().itemconfig.y = e)
						.controller(IntegerFieldControllerBuilder::create)
						.build()
				)
				.option(Option.<Color>createBuilder()
						.name(Text.literal("Overclocker Icon color"))
						.binding(new Color(0xFF0000), () -> Options.getInstance().itemconfig.overclocker_color, e -> Options.getInstance().itemconfig.overclocker_color = e)
						.controller(ColorControllerBuilder::create)
						.build()
				)
				.option(Option.<Color>createBuilder()
						.name(Text.literal("Sage Icon Color"))
						.binding(new Color(0x000000), () -> Options.getInstance().itemconfig.sage_color, e -> Options.getInstance().itemconfig.sage_color = e)
						.controller(ColorControllerBuilder::create)
						.build()
				)
				.option(Option.<Color>createBuilder()
						.name(Text.literal("Sanded Icon Color"))
						.binding(new Color(0xD9C664), () -> Options.getInstance().itemconfig.sanded_color, e -> Options.getInstance().itemconfig.sanded_color = e)
						.controller(ColorControllerBuilder::create)
						.build()
				)
				.build()
		);
	}

	public void createCCTConfig(YetAnotherConfigLib.@NotNull Builder builder) {
		builder.category(ConfigCategory.createBuilder()
			.name(Text.literal("Chest Cooldown Timer"))
			.option(Option.<Boolean>createBuilder()
				.name(Text.literal("Enabled"))
				.binding(true, () -> Options.getInstance().cctconfig.enabled, e -> Options.getInstance().cctconfig.enabled = e)
				.controller(BooleanControllerBuilder::create)
				.build()
			)
			.option(Option.<Boolean>createBuilder()
				.name(Text.literal("Play Sound When Chest Ready"))
				.binding(false, () -> Options.getInstance().cctconfig.soundEnabled, e -> Options.getInstance().cctconfig.soundEnabled = e)
				.controller(BooleanControllerBuilder::create)
				.build()
			)
			.option(Option.<Boolean>createBuilder()
				.name(Text.literal("Function Outside Pickaxe"))
				.binding(false, () -> Options.getInstance().cctconfig.enabledOutsidePickaxe, e -> Options.getInstance().cctconfig.enabledOutsidePickaxe = e)
				.controller(BooleanControllerBuilder::create)
				.build()
			)
				.option(Option.<CCTLocation>createBuilder()
						.name(Text.literal("CCT Location"))
						.binding(CCTLocation.TOPRIGHT, () -> Options.getInstance().cctconfig.location, e -> Options.getInstance().cctconfig.location = e)
						.controller((opt) -> EnumControllerBuilder.create(opt)
								.enumClass(CCTLocation.class)
						)

						.build()
				)

			.build()
		);
	}

	private void createPOIConfig(YetAnotherConfigLib.@NotNull Builder builder) {
		builder.category(ConfigCategory.createBuilder()
				.name(Text.literal("POI Config"))
				.option(Option.<Boolean>createBuilder()
						.name(Text.literal("Enable POI Hiding"))
						.binding(true, () -> Options.getInstance().enable_poi, e -> Options.getInstance().enable_poi = e)
						.controller(BooleanControllerBuilder::create)
						.build()
				)
				.option(ListOption.<POI>createBuilder()
						.name(Text.literal("POIS"))
						.description(OptionDescription.of(Text.literal("POI Config")))
						.binding(List.of(POI.values()), () -> List.of(Options.getInstance().pois), e -> Options.getInstance().pois = e.toArray(new POI[0]))
						.controller(opt -> {
							return EnumControllerBuilder.create(opt)
									.enumClass(POI.class);

						})
						.initial(POI.SPAWN)
						.build()
				)
				.build());
	}

	public Screen getConfigScreen(Screen parent) {
    	YetAnotherConfigLib.Builder builder =  YetAnotherConfigLib.createBuilder()
        	    .title(Text.literal("Pickaxe Mod Settings"));

		createGeneralScreen(builder);
		createItemConfig(builder);
		createCCTConfig(builder);
		createPOIConfig(builder);

		return builder.save(this::saveConfig)
				.build()
            	.generateScreen(parent);

    }

    public ConfigScreenFactory<?> getModConfigScreenFactory() {
		return this::getConfigScreen;
    }
}
