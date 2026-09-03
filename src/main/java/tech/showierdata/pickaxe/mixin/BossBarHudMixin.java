package tech.showierdata.pickaxe.mixin;

import com.google.common.collect.Iterators;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import tech.showierdata.pickaxe.IBossBarHudMixin;
import tech.showierdata.pickaxe.Pickaxe;
import tech.showierdata.pickaxe.config.Options;

import java.util.Iterator;
import java.util.Map;
import java.util.UUID;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.BossHealthOverlay;
import net.minecraft.client.gui.components.LerpingBossEvent;
@Mixin(BossHealthOverlay.class) //
public class BossBarHudMixin implements IBossBarHudMixin {


	@Final
	@Shadow
    Map<UUID, LerpingBossEvent> events;

    public Map<UUID, LerpingBossEvent> pickaxe_mod$getBossBars() {
		return events;
	}

	@ModifyVariable(method = "render(Lnet/minecraft/client/gui/GuiGraphics;)V", ordinal = 0, at = @At(value = "STORE", ordinal = 0))
	public Iterator<LerpingBossEvent> bossBarFix(Iterator<LerpingBossEvent> var4) {
		if (!Pickaxe.getInstance().isInPickaxe()) { return var4; }

		Pickaxe.getInstance().bossbarFound = false;

		Iterator<LerpingBossEvent> iter = Iterators.filter(var4, (clientBossBar) -> {
			boolean val = !(Options.getInstance().XPBarType.detect(clientBossBar));

			if (!val) //noinspection RedundantSuppression
            {
				Minecraft client = Minecraft.getInstance();
				assert client.player != null;
				client.player.experienceProgress = clientBossBar.getProgress();

                //noinspection SwitchStatementWithTooFewBranches (Looks better)
                switch (Options.getInstance().XPBarType) {
					case Depth:
						int y = -1 * (int)Pickaxe.getInstance().rel_spawn.y;
						client.player.experienceLevel = Math.max(y, 0);
						break;
					default:
						client.player.experienceLevel = Options.getInstance().XPBarType.getBarDetails(clientBossBar);
				}
				Pickaxe.getInstance().bossbarFound = true;
			}
			return val;
		});

        //noinspection ConstantValue
        if (!Pickaxe.getInstance().bossbarFound) {
			Minecraft client = Minecraft.getInstance();
            assert client.player != null;
            client.player.experienceLevel = 0;
		}

		return iter;
	}
}