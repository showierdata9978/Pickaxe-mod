package tech.showierdata.pickaxe;

import java.util.Optional;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;


public class ModMenuIntergrationImpl implements ModMenuApi  {

    public ConfigScreenFactory<?> getModConfigScreenFactory() {
		return (screen) -> {
			return Pickaxe.getInstance().getConfigScreen(screen);
		};
    }
}
