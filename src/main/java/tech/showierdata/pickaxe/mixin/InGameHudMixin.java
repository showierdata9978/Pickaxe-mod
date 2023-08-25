package tech.showierdata.pickaxe.mixin;


import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import tech.showierdata.pickaxe.Pickaxe;


@Mixin(InGameHud.class)
public abstract class InGameHudMixin {

    /*@ModifyVariable(method = "renderStatusBars", at = @At(value = "STORE", ordinal = 0))
    PlayerEntity modifPlayerEntity(PlayerEntity playerEntity) {
        if (Pickaxe.getInstance().isInPickaxe()) return null;
        return playerEntity;
    }*/

    @ModifyConstant(method = "renderStatusBars", constant = @Constant(intValue = 0, ordinal = 2))
    private int modifyHungerLoop(int zero) {
        if (Pickaxe.getInstance().isInPickaxe()) return 10;
        return zero;
    }

    @SuppressWarnings("InvalidInjectorMethodSignature")
    @ModifyVariable(method = "renderStatusBars", slice = @Slice(from = @At(value = "INVOKE", target = "net/minecraft/entity/player/PlayerEntity.getMaxAir ()I")), at = @At(value = "STORE", ordinal = 0), ordinal = 14)
    private int modifyAirBar(int y) {
        if (Pickaxe.getInstance().isInPickaxe()) return 0;
        return y;
    }

    @Inject(method = "renderMountHealth", at = @At(value = "HEAD"), cancellable = true)
    private void modifyMountBar(DrawContext c, CallbackInfo ci) {
        if (Pickaxe.getInstance().isInPickaxe()) ci.cancel();
    }

    @Inject(at=@At("TAIL"), method = "renderHotbarItem")
    private void renderHotbarIcons(DrawContext context, int x, int y, float f, PlayerEntity player, ItemStack stack, int seed, CallbackInfo ci) {
        Pickaxe.getInstance().renderHotbarIcons(context, x, y, stack);
    }


}
