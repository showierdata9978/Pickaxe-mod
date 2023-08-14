package tech.showierdata.pickaxe.mixin;


import net.minecraft.client.gui.hud.PlayerListHud;
import net.minecraft.client.network.PlayerListEntry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import tech.showierdata.pickaxe.Pickaxe;
import tech.showierdata.pickaxe.config.Options;

import java.util.ArrayList;
import java.util.List;

@Mixin(PlayerListHud.class)
public class PlayerHudClassMixin {
    @Inject(method = "collectPlayerEntries", at = @At("RETURN"), cancellable = true)
    private void Pickaxe_collectPlayerEntries(CallbackInfoReturnable<List<PlayerListEntry>> cir) {
        ArrayList<PlayerListEntry> ret = new ArrayList<>(cir.getReturnValue());

        if (Pickaxe.getInstance().isInPickaxe() && Options.getInstance().hideNonPickaxePlayers)
            ret.removeIf(p -> p.getScoreboardTeam() == null);

        cir.setReturnValue(ret);
    }
}
