package tech.showierdata.pickaxe;

import net.minecraft.client.gui.hud.ClientBossBar;

import java.util.Map;
import java.util.UUID;

public interface IBossBarHudMixin {
	Map<UUID, ClientBossBar> pickaxe_mod$getBossBars();
}
