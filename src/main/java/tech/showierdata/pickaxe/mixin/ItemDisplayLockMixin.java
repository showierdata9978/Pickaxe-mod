package tech.showierdata.pickaxe.mixin;


/*
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.render.LightmapTextureManager;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.text.TextColor;
import net.pickmod.PickMod;
import tech.showierdata.pickaxe.config.Options;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.hud.ChatHud;
import net.minecraft.client.toast.SystemToast;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Objects;


@Mixin(ItemRenderer.class)
public class ItemDisplayLockMixin {
    @Shadow
    public float zOffset;
    @Inject(at = @At("TAIL"), method = "renderGuiItemOverlay(Lnet/minecraft/client/font/TextRenderer;Lnet/minecraft/item/ItemStack;IILjava/lang/String;)V")
    public void renderGuiItemOverlay(TextRenderer renderer, ItemStack stack, int x, int y, String countLabel, CallbackInfo ci) {
        if (stack.getOrCreateNbt().contains("PublicBukkitValues") && Options.getInstance().ShowLockIcon) {
            if (Objects.requireNonNull(stack.getSubNbt("PublicBukkitValues")).getFloat("hypercube:nodrop") == 1.0d) {
                MatrixStack matrixStack = new MatrixStack();
                matrixStack.translate(0.0, 0.0, zOffset + 200.0f);
                VertexConsumerProvider.Immediate immediate = VertexConsumerProvider.immediate(Tessellator.getInstance().getBuffer());
                renderer.draw("⚓", (float)(x), (float)(y), Objects.requireNonNull(TextColor.parse("dark_gray")).getRgb(), true, matrixStack.peek().getPositionMatrix(), immediate, false, 0, LightmapTextureManager.MAX_LIGHT_COORDINATE);
                immediate.draw();
            }
        }
    }
}*/