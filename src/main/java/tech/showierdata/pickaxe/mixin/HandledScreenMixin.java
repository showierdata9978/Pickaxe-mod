package tech.showierdata.pickaxe.mixin;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import tech.showierdata.pickaxe.Pickaxe;


@Mixin(AbstractContainerScreen.class)
public class HandledScreenMixin {

    @Inject(method = "renderFloatingItem", at = @At("TAIL"))
    private void drawItem(GuiGraphics context, ItemStack stack, int x, int y, String amountText, CallbackInfo ci) {
       Pickaxe.getInstance().renderHotbarIcons(context, x, y, stack);
    }

    @Inject(method = "renderSlot", at = @At("TAIL"))
    private void drawSlot(GuiGraphics context, Slot slot, CallbackInfo ci) {
        ItemStack stack = slot.getItem();
        int x = slot.x;
        int y = slot.y;
        Pickaxe.getInstance().renderHotbarIcons(context, x, y, stack);
    }
}