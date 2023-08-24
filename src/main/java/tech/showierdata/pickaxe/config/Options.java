package tech.showierdata.pickaxe.config;


import com.google.common.io.Files;
import com.google.gson.GsonBuilder;
import com.mojang.authlib.minecraft.client.ObjectMapper;
import tech.showierdata.pickaxe.Pickaxe;

import java.awt.*;
import java.io.File;
import java.io.IOException;

public class Options {
	public boolean enabled = true;
	public XPBarEnum XPBarType = XPBarEnum.Radiation;
	public boolean AutoCL = false;
	public boolean ShowLockIcon = false;
	private static final ObjectMapper mapper = new ObjectMapper(new GsonBuilder()
			.registerTypeAdapter(Color.class, new ColorTypeAdapter())
			.create()
	);
	public final ItemConfig itemconfig;
	public final CCTConfig cctconfig;
	public boolean hideNonPickaxePlayers = true;
	public POI[] pois = POI.values();
	public boolean enable_poi = true;
	public boolean auto_gg = false;
	static Options INSTANCE;


	public Options() { // all default vals
		this.itemconfig = new ItemConfig();
		this.cctconfig = new CCTConfig();
		INSTANCE = this;
	}

	public static Options getInstance() {
		if (INSTANCE == null) {
			INSTANCE = new Options();
		}
		return INSTANCE;
	}

	public static void loadConfig() {
		File file = new File(ModMenuIntergrationImpl.configPath);

		if (file.exists()) {
			try {
				String data = new String(Files.toByteArray(file));


				Options options = mapper.readValue(data, Options.class);

				Options.setInstance(options);

			} catch (IOException e) {
				Pickaxe.LOGGER.error("Failed to load config", e);
			}
		}
	}
	public static void setInstance(Options instance) {
		INSTANCE = instance;
	}

}
