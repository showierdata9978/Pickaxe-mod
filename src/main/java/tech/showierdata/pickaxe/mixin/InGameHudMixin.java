package tech.showierdata.pickaxe.mixin;


import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import tech.showierdata.pickaxe.Pickaxe;
import tech.showierdata.pickaxe.config.Options;
import tech.showierdata.pickaxe.config.XPBarEnum;


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

    @ModifyArg(method = "renderExperienceBar",
        slice = @Slice(
            from = @At(
                value = "INVOKE",
                target = "net/minecraft/client/network/ClientPlayerEntity.getNextLevelExperience ()I")
        ),
        at = @At(
            value = "INVOKE",
            target = "net/minecraft/client/gui/DrawContext.drawTexture (Lnet/minecraft/util/Identifier;IIIIII)V"
        ),
        allow = 2)
    Identifier swapIcons(Identifier prev, int x, int y, int u, int v, int width, int height) {
        XPBarEnum xp = Options.getInstance().XPBarType;
        if (!Pickaxe.getInstance().isInPickaxe()) return prev;
        if (xp == XPBarEnum.Suit_Charge) return new Identifier("pickaxe", "textures/gui/yellow.png");
        if (xp == XPBarEnum.Depth) {
            if (Pickaxe.getInstance().rel_spawn.y < -30) return new Identifier("pickaxe", "textures/gui/purple.png");
            return new Identifier("pickaxe", "textures/gui/red.png");
        }
        return prev;
    }
}
