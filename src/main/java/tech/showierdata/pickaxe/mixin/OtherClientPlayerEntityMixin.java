package tech.showierdata.pickaxe.mixin;


import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import tech.showierdata.pickaxe.Constants;
import tech.showierdata.pickaxe.config.Options;
import tech.showierdata.pickaxe.config.POI;

@Mixin(PlayerRenderer.class)
public class OtherClientPlayerEntityMixin {
    @Inject(method = "render(Lnet/minecraft/client/player/AbstractClientPlayer;FFLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V", at = @At("HEAD"), cancellable = true)
    private void pickaxe$render(@NotNull AbstractClientPlayer abstractClientPlayerEntity, float f, float g, PoseStack matrixStack, MultiBufferSource vertexConsumerProvider, int i, CallbackInfo ci) {
        if (!Options.getInstance().enable_poi) return;

        if (abstractClientPlayerEntity.isLocalPlayer()) {
            return;
        }

        Vec3 pos = new Vec3(abstractClientPlayerEntity.getX(), abstractClientPlayerEntity.getY(), abstractClientPlayerEntity.getZ()).subtract(Constants.Spawn);


        for (POI j : Options.getInstance().pois) {
            if (pos.closerThan(j.getPosition(), 5)) {
                ci.cancel();
                return;
            }
        }

    }
}
