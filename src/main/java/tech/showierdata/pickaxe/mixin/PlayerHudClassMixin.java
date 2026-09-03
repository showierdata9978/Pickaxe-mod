package tech.showierdata.pickaxe.mixin;


import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import tech.showierdata.pickaxe.Pickaxe;
import tech.showierdata.pickaxe.config.Options;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.gui.components.PlayerTabOverlay;
import net.minecraft.client.multiplayer.PlayerInfo;

@Mixin(PlayerTabOverlay.class)
public class PlayerHudClassMixin {
    @Inject(method = "getPlayerInfos", at = @At("RETURN"), cancellable = true)
    private void Pickaxe_collectPlayerEntries(CallbackInfoReturnable<List<PlayerInfo>> cir) {
        ArrayList<PlayerInfo> ret = new ArrayList<>(cir.getReturnValue());

        if (Pickaxe.getInstance().isInPickaxe() && Options.getInstance().hideNonPickaxePlayers)
            ret.removeIf(p -> p.getTeam() == null);

        cir.setReturnValue(ret);
    }
}
