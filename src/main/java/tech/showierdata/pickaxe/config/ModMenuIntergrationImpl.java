package tech.showierdata.pickaxe.config;

import com.google.common.io.Files;
import com.mojang.authlib.minecraft.client.ObjectMapper;
import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import dev.isxander.yacl3.api.ConfigCategory;
import dev.isxander.yacl3.api.Option;
import dev.isxander.yacl3.api.YetAnotherConfigLib;
import dev.isxander.yacl3.api.controller.BooleanControllerBuilder;
import dev.isxander.yacl3.api.controller.EnumControllerBuilder;
import dev.isxander.yacl3.api.controller.IntegerFieldControllerBuilder;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;
import org.jetbrains.annotations.NotNull;
import tech.showierdata.pickaxe.Pickaxe;

import java.io.File;
import java.io.IOException;
import java.util.Objects;


public class ModMenuIntergrationImpl implements ModMenuApi  {
	public static final String configPath = "config/pickaxe.properties.json";
	public ModMenuIntergrationImpl() {
		File file = new File(configPath);

		if (file.exists()) {
			try {
				String data = new String(Files.toByteArray(file));

				ObjectMapper mapper = ObjectMapper.create();

				Options options = mapper.readValue(data, Options.class);

				Options.setInstance(options);

			} catch (IOException e) {
				Pickaxe.LOGGER.error("Failed to load config", e);
			}
		}
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

			
			ObjectMapper mapper = ObjectMapper.create();

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
				.build()
		);
	}

	public Screen getConfigScreen(Screen parent) {
    	YetAnotherConfigLib.Builder builder =  YetAnotherConfigLib.createBuilder()
        	    .title(Text.literal("Pickaxe Mod Settings"));

		createGeneralScreen(builder);
		createItemConfig(builder);

		return builder.save(this::saveConfig)
				.build()
            	.generateScreen(parent);

    }

    public ConfigScreenFactory<?> getModConfigScreenFactory() {
		return this::getConfigScreen;
    }
}
