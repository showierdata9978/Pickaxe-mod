package tech.showierdata.pickaxe.mixin;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import net.minecraft.client.sound.Sound;
import net.minecraft.util.math.floatprovider.FloatSupplier;
import tech.showierdata.pickaxe.Pickaxe;

@Mixin(Sound.class)
public class SoundMixin {
    @Shadow
    private @Final FloatSupplier volume;

    @SuppressWarnings("MixinAnnotationTarget")
    @Redirect(method = "getVolume", at = @At("FIELD"))
    public FloatSupplier muteAds(Sound sound) {
        if (Pickaxe.getInstance().adFound) {
            Pickaxe.getInstance().adFound = false;
            return arg1 -> 0;
        }
        return volume;
    }
}
