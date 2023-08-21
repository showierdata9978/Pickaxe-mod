package tech.showierdata.pickaxe.mixin;

import com.google.common.collect.Maps;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.BossBarHud;
import net.minecraft.client.gui.hud.ClientBossBar;
import net.minecraft.entity.boss.BossBar;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import tech.showierdata.pickaxe.IBossBarHudMixin;
import tech.showierdata.pickaxe.Pickaxe;
import tech.showierdata.pickaxe.config.Options;

import java.util.Map;
import java.util.UUID;
import java.util.Iterator;
@Mixin(BossBarHud.class) //
public class BossBarHudMixin implements IBossBarHudMixin {


    @Unique
	public Map<UUID, Text> bossBarNames = Maps.newLinkedHashMap();
    @Final
	@Shadow
    Map<UUID, ClientBossBar> bossBars;

    public Map<UUID, ClientBossBar> pickaxe_mod$getBossBars() {
		return bossBars;
	}


    @Inject(at = @At("HEAD"), method = "renderBossBar*", cancellable = true)
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
            assert client.player != null;
            client.player.experienceProgress = bossBar.getPercent();
			Text name = bossBar.getName();
			bossBarNames.put(bossBar.getUuid(), name);
			bossBar.setName(Text.empty());

			info.cancel();

		} 
	
	}

	@ModifyVariable(method = "render(Lnet/minecraft/client/gui/DrawContext;)V", ordinal = 0, at = @At(value = "INVOKE", target = "java/util/Iterator.next ()Ljava/lang/Object;", ordinal = 0))
	public Iterator<ClientBossBar> bossBarFix(Iterator<ClientBossBar> var4) {
		if (!Pickaxe.getInstance().isInPickaxe()) { return var4; }

		Options options = Options.getInstance();
		Pickaxe.LOGGER.info("The XP Bar is: " + options.XPBarType);
		Pickaxe.LOGGER.info("The variable is: " + var4.next());
		//if (Options.getInstance().XPBarType.detect((BossBar)var4)) {
		//	MinecraftClient client = MinecraftClient.getInstance();
		//	assert client.player != null;
		//	//var4.next();
		//}
		return var4;
	}
}