package tech.showierdata.pickaxe.features.render;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.hud.ClientBossBar;
import net.minecraft.util.math.Vec3d;
import tech.showierdata.pickaxe.Constants;
import tech.showierdata.pickaxe.IBossBarHudMixin;
import tech.showierdata.pickaxe.Pickaxe;
import tech.showierdata.pickaxe.config.Options;

public class BossBar {

    public static BossBar instance = new BossBar();

    BossBar() {

    }


    public void run(MinecraftClient client) {
        assert client.player != null;
        Vec3d playerPos = client.player.getPos();
        Pickaxe.getInstance().rel_spawn = playerPos.subtract(Constants.Spawn);

        boolean foundRadBossBar = false;

        //noinspection RedundantCast
        for (ClientBossBar bar : ((IBossBarHudMixin) (Object) client.inGameHud.getBossBarHud()).pickaxe_mod$getBossBars()
                .values()) {
            foundRadBossBar = Options.getInstance().XPBarType.detect(bar);
        }

        if (!foundRadBossBar) {
            client.player.experienceProgress = 0;
        }
    }


}
