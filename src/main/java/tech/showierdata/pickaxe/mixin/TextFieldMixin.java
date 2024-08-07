package tech.showierdata.pickaxe.mixin;

import net.minecraft.client.gui.widget.TextFieldWidget;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import tech.showierdata.pickaxe.Pickaxe;

@Mixin(TextFieldWidget.class)
public class TextFieldMixin {
    @Inject(method = "isActive", at = @At("HEAD"), cancellable = true)
    protected void write(CallbackInfoReturnable<Boolean> cir) {
       if (Pickaxe.getInstance().noteEditor.isFocused) {
           cir.setReturnValue(false);
           cir.cancel();
       }
    }

}
