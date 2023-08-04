package tech.showierdata.pickaxe.mixin;


import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import tech.showierdata.pickaxe.Pickaxe;


@Mixin(InGameHud.class)
public abstract class InGameHudMixin {


    @ModifyArg(method = "renderStatusBars", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/DrawContext;drawTexture(Lnet/minecraft/util/Identifier;IIIIII)V", ordinal = 3), index = 5)
    private int modifyHungerQuad1(int value) {
        if (Pickaxe.getInstance().isInPickaxe()) {
            return 0;
        }// set the width to 0
        return value;
    }

    @ModifyArg(method = "renderStatusBars", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/DrawContext;drawTexture(Lnet/minecraft/util/Identifier;IIIIII)V", ordinal = 3), index = 6)
    private int modifyHungerQuad2(int value) {
        if (Pickaxe.getInstance().isInPickaxe()) {
            return 0;
        }// set the width to 0
        return value;
    }

    @ModifyArg(method = "renderStatusBars", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/DrawContext;drawTexture(Lnet/minecraft/util/Identifier;IIIIII)V", ordinal = 1 + 3), index = 5)
    private int modifyHungerQuad3(int value) {
        if (Pickaxe.getInstance().isInPickaxe()) return 0; // set the width to 0
        return value;
    }

        @ModifyArg(method = "renderStatusBars", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/DrawContext;drawTexture(Lnet/minecraft/util/Identifier;IIIIII)V", ordinal = 1 + 3), index = 6)
    private int modifyHungerQuad4(int value) {
        if (Pickaxe.getInstance().isInPickaxe()) return 0; // set the width to 0
        return value;
    }



    @ModifyArg(method = "renderStatusBars", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/DrawContext;drawTexture(Lnet/minecraft/util/Identifier;IIIIII)V", ordinal = 3 + 3), index = 5)
    private int modifyHungerQuad5(int value) {
        if (Pickaxe.getInstance().isInPickaxe()) return 0; // set the width to 0
        return value;
    }   
    
    @ModifyArg(method = "renderStatusBars", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/DrawContext;drawTexture(Lnet/minecraft/util/Identifier;IIIIII)V", ordinal = 3 + 3), index = 6)
    private int modifyHungerQuad6(int value) {
        if (Pickaxe.getInstance().isInPickaxe()) return 0; // set the width to 0
        return value;
    }
    @Inject(at=@At("TAIL"), method = "renderHotbarItem")
    private void renderHotbarIcons(DrawContext context, int x, int y, float f, PlayerEntity player, ItemStack stack, int seed, CallbackInfo ci) {
        Pickaxe.getInstance().renderHotbarIcons(context, x, y, stack);
    }


}
