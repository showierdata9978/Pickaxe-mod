package tech.showierdata.pickaxe;

import java.util.Map;
import java.util.UUID;
import net.minecraft.client.gui.components.LerpingBossEvent;

public interface IBossBarHudMixin {
	Map<UUID, LerpingBossEvent> pickaxe_mod$getBossBars();
}
