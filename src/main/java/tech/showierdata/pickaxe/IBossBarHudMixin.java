package tech.showierdata.pickaxe;

import java.util.Map;
import java.util.UUID;

import net.minecraft.client.gui.hud.ClientBossBar;

public interface IBossBarHudMixin {
	public Map<UUID, ClientBossBar> getBossBars();
}
