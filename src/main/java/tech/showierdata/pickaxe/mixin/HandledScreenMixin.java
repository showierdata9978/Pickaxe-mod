package tech.showierdata.pickaxe.mixin;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.Slot;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import tech.showierdata.pickaxe.Pickaxe;


@Mixin(HandledScreen.class)
public class HandledScreenMixin {

    @Inject(method = "drawItem", at = @At("TAIL"))
    private void drawItem(DrawContext context, ItemStack stack, int x, int y, String amountText, CallbackInfo ci) {
       Pickaxe.getInstance().renderHotbarIcons(context, x, y, stack);
    }

    @Inject(method = "drawSlot", at = @At("TAIL"))
    private void drawSlot(DrawContext context, Slot slot, CallbackInfo ci) {
        ItemStack stack = slot.getStack();
        int x = slot.x;
        int y = slot.y;
        Pickaxe.getInstance().renderHotbarIcons(context, x, y, stack);
    }
}