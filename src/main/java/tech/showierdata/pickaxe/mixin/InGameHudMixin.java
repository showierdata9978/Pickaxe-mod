package tech.showierdata.pickaxe.mixin;


import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import tech.showierdata.pickaxe.Pickaxe;


@Mixin(InGameHud.class)
public abstract class InGameHudMixin {

    @ModifyConstant(method = "renderStatusBars(Lnet/minecraft/client/gui/DrawContext;)V", constant = @Constant(intValue = 0, ordinal = 2, log = true))
    private int modifyHungerLoop(int zero) {
        if (Pickaxe.getInstance().isInPickaxe()) return 9;
        return zero;
    }


    @Inject(at=@At("TAIL"), method = "renderHotbarItem")
    private void renderHotbarIcons(DrawContext context, int x, int y, float f, PlayerEntity player, ItemStack stack, int seed, CallbackInfo ci) {
        Pickaxe.getInstance().renderHotbarIcons(context, x, y, stack);
    }


}
