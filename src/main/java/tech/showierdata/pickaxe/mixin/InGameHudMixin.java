package tech.showierdata.pickaxe.mixin;


import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.Slice;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import tech.showierdata.pickaxe.Pickaxe;
import tech.showierdata.pickaxe.config.Options;


@Mixin(InGameHud.class)
public abstract class InGameHudMixin {


    @ModifyConstant(method = "renderStatusBars", constant = @Constant(intValue = 0, ordinal = 2))
    private int modifyHungerLoop(int zero) {
        if (Pickaxe.getInstance().isInPickaxe()) return 10;
        return zero;
    }

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

    @Redirect(method = "renderExperienceBar",
        slice = @Slice(
            from = @At(
                value = "INVOKE",
                target = "net/minecraft/client/network/ClientPlayerEntity.getNextLevelExperience ()I")
        ),
        at = @At(
            value = "INVOKE",
            target = "net/minecraft/client/gui/DrawContext.drawGuiTexture (Lnet/minecraft/util/Identifier;IIIIIIII)V"
        ),
        allow = 1)
    void swapIcons(DrawContext context, Identifier ICONS, int i, int j, int k, int l, int x, int y, int width, int height) {
        if (!Pickaxe.getInstance().isInPickaxe()) {
            context.drawGuiTexture(ICONS, i, j, k, l, x, y, width, height);
            return;
        }
        renderNewExperienceBar(context, x, y, width, false);
    }

    @Redirect(method = "renderExperienceBar",
        slice = @Slice(
            from = @At(
                value = "INVOKE",
                target = "net/minecraft/client/network/ClientPlayerEntity.getNextLevelExperience ()I")
        ),
        at = @At(
            value = "INVOKE",
            target = "net/minecraft/client/gui/DrawContext.drawGuiTexture (Lnet/minecraft/util/Identifier;IIII)V"
        ),
        allow = 1)
    void swapIcons(DrawContext context, Identifier ICONS, int x, int y, int width, int height) {
        if (!Pickaxe.getInstance().isInPickaxe()) {
            context.drawGuiTexture(ICONS, x, y, width, height);
            return;
        }
        renderNewExperienceBar(context, x, y, width, true);
    }

    @Unique
    void renderNewExperienceBar(DrawContext context, int x, int y, int width, boolean isBG) {
        int v = (isBG)? 0 : 5;
        switch (Options.getInstance().XPBarType) {
            case Depth:
                v += 20;
                if (Pickaxe.getInstance().rel_spawn.y > -30) v += 10;
                break;
            case Suit_Charge:
                v += 40;
                break;
            case O2:
                v += 10;
                break;
        }
        context.drawTexture(Pickaxe.COLORS, x, y, 0, v, width, 5, 182, 50);
    }

    @ModifyConstant(method = "renderExperienceBar", constant = @Constant(intValue = 8453920))
    int changeLevelColor(int prev) {
        if (!Pickaxe.getInstance().isInPickaxe()) return prev;
        return switch (Options.getInstance().XPBarType) {
            case O2 -> 0x33CCFF;
            case Depth -> {
                if (Pickaxe.getInstance().rel_spawn.y < -30) yield 0xCC33FF;
                yield 0xFF0000;
            }
            case Suit_Charge -> 0xFFCC00;
            default -> prev;
        };
    }

    @SuppressWarnings("InvalidInjectorMethodSignature")
    @ModifyVariable(method = "renderExperienceBar",
        slice = @Slice(
            from = @At(
                value = "FIELD",
                target = "net/minecraft/client/network/ClientPlayerEntity.experienceLevel : I"
            )),
        at = @At(value = "STORE", ordinal = 0),
        ordinal = 0)
    String changeLevelString(String prev) {
        if (!Pickaxe.getInstance().isInPickaxe()) return prev;
        if (prev.equals("0")) prev = "";
        return switch (Options.getInstance().XPBarType) {
            case Suit_Charge -> "⚡" + prev;
            case Radiation -> "☢" + prev;
            //return "◌" + prev; // I dunno if I want it
            default -> prev;
        };
    }

    @Redirect(
        method = "renderExperienceBar",
        slice = @Slice(
            from = @At(
                value = "INVOKE",
                target = "net/minecraft/util/profiler/Profiler.pop ()V",
                ordinal = 0
            )
        ),
        at = @At(
            value = "FIELD",
            target = "net/minecraft/client/network/ClientPlayerEntity.experienceLevel : I",
            ordinal = 0
        )
    )
    int displayWithZero(ClientPlayerEntity clientPlayerEntity) {
        if (!Pickaxe.getInstance().isInPickaxe()) return clientPlayerEntity.experienceLevel;
        return 1;
    }
}
