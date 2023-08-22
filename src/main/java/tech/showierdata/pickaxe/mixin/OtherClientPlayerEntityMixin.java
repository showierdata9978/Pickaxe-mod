package tech.showierdata.pickaxe.mixin;


import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.PlayerEntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import tech.showierdata.pickaxe.Constants;
import tech.showierdata.pickaxe.config.Options;
import tech.showierdata.pickaxe.config.POI;

@Mixin(PlayerEntityRenderer.class)
public class OtherClientPlayerEntityMixin {
    @Inject(method = "render(Lnet/minecraft/client/network/AbstractClientPlayerEntity;FFLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V", at = @At("HEAD"), cancellable = true)
    private void pickaxe$render(@NotNull AbstractClientPlayerEntity abstractClientPlayerEntity, float f, float g, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, CallbackInfo ci) {
        if (!Options.getInstance().enable_poi) return;

        if (abstractClientPlayerEntity.isMainPlayer()) {
            return;
        }

        Vec3d pos = new Vec3d(abstractClientPlayerEntity.getX(), abstractClientPlayerEntity.getY(), abstractClientPlayerEntity.getZ()).subtract(Constants.Spawn);


        for (POI j : Options.getInstance().pois) {
            if (pos.isInRange(j.getPosition(), 5)) {
                ci.cancel();
                return;
            }
        }

    }
}
