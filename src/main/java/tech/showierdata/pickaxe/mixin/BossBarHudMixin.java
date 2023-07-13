package tech.showierdata.pickaxe.mixin;

import net.minecraft.client.gui.hud.ClientBossBar;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.BossBarHud;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.boss.BossBar;
import net.minecraft.text.Text;
import tech.showierdata.pickaxe.IBossBarHudMixin;
import tech.showierdata.pickaxe.Pickaxe;

import java.util.Map;
import java.util.Set;
import java.util.UUID;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.google.common.collect.Maps;

import net.minecraft.client.MinecraftClient;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;

import tech.showierdata.pickaxe.config.Options;
@Mixin(BossBarHud.class) //
public class BossBarHudMixin implements IBossBarHudMixin {

	
	@Shadow
	private Map<UUID, ClientBossBar> bossBars;
	public Map<UUID, Text> bossBarNames = Maps.newLinkedHashMap();

	public Map<UUID, ClientBossBar> getBossBars() {
		return bossBars;
	}



	

	@Inject(at = @At("HEAD"), method = "renderBossBar", cancellable = true)
	private void renderBossBar(DrawContext context, int x, int y, BossBar bossBar, CallbackInfo info) {
		for (UUID unamed_bar: bossBarNames.keySet()) {
			ClientBossBar bar = bossBars.get(unamed_bar);
			Text name = bossBarNames.get(unamed_bar);

			//check if the bar is not named

			if (bar == null) {
				continue;
			}

			if (bar.getName().equals(Text.empty())) {
				bar.setName(name);
			}

			bossBarNames.remove(unamed_bar);
			
			
		}
		
		if (!Pickaxe.getInstance().isInPickaxe()) {
			return;
		}






		MinecraftClient client = MinecraftClient.getInstance();

		//set the users xp percent to the boss bar labled "Radiation"
	
		if (Options.getInstance().XPBarType.detect(bossBar)) {
			client.player.experienceProgress = bossBar.getPercent();
			Text name = bossBar.getName();
			bossBarNames.put(bossBar.getUuid(), name);
			bossBar.setName(Text.empty());

			info.cancel();

		} 
	
	}
}