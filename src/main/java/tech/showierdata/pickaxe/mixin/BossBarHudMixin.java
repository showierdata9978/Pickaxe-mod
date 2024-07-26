package tech.showierdata.pickaxe.mixin;

import com.google.common.collect.Iterators;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.hud.BossBarHud;
import net.minecraft.client.gui.hud.ClientBossBar;
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
@Mixin(BossBarHud.class) //
public class BossBarHudMixin implements IBossBarHudMixin {


	@Final
	@Shadow
    Map<UUID, ClientBossBar> bossBars;

    public Map<UUID, ClientBossBar> pickaxe_mod$getBossBars() {
		return bossBars;
	}

	@ModifyVariable(method = "render(Lnet/minecraft/client/gui/DrawContext;)V", ordinal = 0, at = @At(value = "STORE", ordinal = 0))
	public Iterator<ClientBossBar> bossBarFix(Iterator<ClientBossBar> var4) {
		if (!Pickaxe.getInstance().isInPickaxe()) { return var4; }

		Pickaxe.getInstance().bossbarFound = false;

		Iterator<ClientBossBar> iter = Iterators.filter(var4, (clientBossBar) -> {
			boolean val = !(Options.getInstance().XPBarType.detect(clientBossBar));

			if (!val) //noinspection RedundantSuppression
            {
				MinecraftClient client = MinecraftClient.getInstance();
				assert client.player != null;
				client.player.experienceProgress = clientBossBar.getPercent();

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
			MinecraftClient client = MinecraftClient.getInstance();
            assert client.player != null;
            client.player.experienceLevel = 0;
		}

		return iter;
	}
}