package tech.showierdata.pickaxe.config;

import java.io.File;
import java.io.IOException;
import java.util.Optional;

import com.google.common.io.Files;
import com.mojang.authlib.minecraft.client.ObjectMapper;
import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;

import dev.isxander.yacl.api.ConfigCategory;
import dev.isxander.yacl.api.LabelOption;
import dev.isxander.yacl.api.Option;
import dev.isxander.yacl.api.YetAnotherConfigLib;
import dev.isxander.yacl.gui.controllers.BooleanController;
import dev.isxander.yacl.gui.controllers.cycling.EnumController;
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
                    	.option(Option.createBuilder(Boolean.class)
                        	    .name(Text.literal("Enable Mod"))
                            	.binding(true, () -> Options.getInstance().enabled, e -> {
                                		Options.getInstance().enabled = e;
                           		})

                            	.controller(BooleanController::new)
                            	.build()
	                    )
						.option(Option.createBuilder(XPBarEnum.class)

								.name(Text.literal("XP Bar Control"))
								.binding(XPBarEnum.Radiation,() -> Options.getInstance().XPBarType, e -> {
									Options.getInstance().XPBarType = e;
								})
								.controller(EnumController<XPBarEnum>::new)
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
