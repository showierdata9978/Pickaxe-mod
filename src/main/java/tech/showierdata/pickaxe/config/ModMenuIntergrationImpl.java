package tech.showierdata.pickaxe.config;

import java.io.File;
import java.io.IOException;
import java.util.Optional;

import com.google.common.io.Files;
import net.minecraft.client.MinecraftClient;
import com.mojang.authlib.minecraft.client.ObjectMapper;
import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;

import dev.isxander.yacl3.api.ConfigCategory;
import dev.isxander.yacl3.api.Option;
import dev.isxander.yacl3.api.YetAnotherConfigLib;
import dev.isxander.yacl3.api.controller.BooleanControllerBuilder;
import dev.isxander.yacl3.api.controller.EnumControllerBuilder;
import dev.isxander.yacl3.gui.controllers.BooleanController;
import net.minecraft.text.Text;
import tech.showierdata.pickaxe.Pickaxe;
import net.minecraft.client.gui.screen.Screen;


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



	public Screen getConfigScreen(Screen parent) {
    	return YetAnotherConfigLib.createBuilder()
        	    .title(Text.literal("Pickaxe Mod Settings"))
            	.category(ConfigCategory.createBuilder()
                	    .name(Text.literal("General"))
                    	.option(Option.<Boolean>createBuilder()
                        	    .name(Text.literal("Enable Mod"))
                            	.binding(true, () -> Options.getInstance().enabled, e -> { 
										if (Pickaxe.getInstance().isInPickaxe()) {
											if (!e) {
												MinecraftClient.getInstance().getNetworkHandler().sendChatCommand("c g");

											} else {
												MinecraftClient.getInstance().getNetworkHandler().sendChatCommand("c l");
											}
										}
                                		Options.getInstance().enabled = e;
                           		})

                            	.controller(BooleanControllerBuilder::create)
                            	.build()
	                    )
						.option(Option.<XPBarEnum>createBuilder()

								.name(Text.literal("XP Bar Control"))
								.binding(XPBarEnum.Radiation,() -> Options.getInstance().XPBarType, e -> {
									Options.getInstance().XPBarType = e;
								})
								.controller((opt) -> EnumControllerBuilder.create(opt)
									.enumClass(XPBarEnum.class)
								)
								.build()
						)
						.option(Option.<Boolean>createBuilder()
								.name(Text.literal("Automaticly send /c l"))
								.binding(false, () -> Options.getInstance().AutoCL, e -> {
									Options.getInstance().AutoCL = e;
								})
								.controller(BooleanControllerBuilder::create)
								.build()
						)
    	                .build()
        	    )
				.save(this::saveConfig)
				.build()
            	.generateScreen(parent);
	}

    public ConfigScreenFactory<?> getModConfigScreenFactory() {
		return (screen) -> {
			return getConfigScreen(screen);
		};
    }
}
