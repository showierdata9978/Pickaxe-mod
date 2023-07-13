package tech.showierdata.pickaxe.mixin;

import net.minecraft.util.math.random.Random;

import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.entity.player.PlayerEntity;

import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.HungerManager;
import net.minecraft.registry.tag.FluidTags;
import net.minecraft.text.Text;
import net.minecraft.util.math.MathHelper;
import tech.showierdata.pickaxe.Pickaxe;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;

@Mixin(InGameHud.class)
public abstract class InGameHudMixin {
    /* 

    @Shadow
    abstract int getHeartCount(LivingEntity entity);

    @Shadow
    abstract PlayerEntity getCameraPlayer();

    @Shadow
    abstract void renderHealthBar(MatrixStack matrices, PlayerEntity player, int x, int y, int lines,
            int regeneratingHeartIndex, float maxHealth, int lastHealth, int health, int absorption, boolean blinking);

    @Shadow
    abstract LivingEntity getRiddenEntity();

    @Shadow
    private final Random random = Random.create();

    @Shadow
    @SuppressWarnings("all")
    private static final Identifier ICONS = new Identifier("textures/gui/icons.png");;
    @Shadow
    private int ticks;

    @Nullable
    @Shadow
    private Text overlayMessage;

    @Shadow
    public float vignetteDarkness = 1.0f;

    @Nullable
    @Shadow
    private Text title;
    @Nullable
    @Shadow
    private Text subtitle;
    @Shadow
    private int lastHealthValue;
    @Shadow
    private int renderHealthValue;

    @Shadow
    private long lastHealthCheckTime;
    @Shadow
    private long heartJumpEndTick;
    @Shadow
    private int scaledWidth;
    @Shadow
    private int scaledHeight;

    @Shadow
    MinecraftClient client;*/

    @ModifyArg(method = "renderStatusBars", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/DrawContext;drawTexture(Lnet/minecraft/util/Identifier;IIIIII)V", ordinal = 0 + 3), index = 5)
    private int modifyHungerQuad1(int value) {
        if (Pickaxe.getInstance().isInPickaxe()) {
            return 0;
        }// set the width to 0
        return value;
    }
    
    @ModifyArg(method = "renderStatusBars", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/DrawContext;drawTexture(Lnet/minecraft/util/Identifier;IIIIII)V", ordinal = 0 + 3), index =  6)
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

    /**
     * @param matrices
     * @param ci
     * 
     * @since 6/26/2023
     * @reason overwriting hunger bar
     * @author ShowierData9978
     */
    /*
    @Overwrite
    private void renderStatusBars(DrawContext context) {
        PlayerEntity playerEntity = this.getCameraPlayer();
        if (playerEntity != null) {
            int i = MathHelper.ceil(playerEntity.getHealth());
            boolean bl = this.heartJumpEndTick > (long) this.ticks
                    && (this.heartJumpEndTick - (long) this.ticks) / 3L % 2L == 1L;
            long l = Util.getMeasuringTimeMs();
            if (i < this.lastHealthValue && playerEntity.timeUntilRegen > 0) {
                this.lastHealthCheckTime = l;
                this.heartJumpEndTick = (long) (this.ticks + 20);
            } else if (i > this.lastHealthValue && playerEntity.timeUntilRegen > 0) {
                this.lastHealthCheckTime = l;
                this.heartJumpEndTick = (long) (this.ticks + 10);
            }

            if (l - this.lastHealthCheckTime > 1000L) {
                this.lastHealthValue = i;
                this.renderHealthValue = i;
                this.lastHealthCheckTime = l;
            }

            this.lastHealthValue = i;
            int j = this.renderHealthValue;
            this.random.setSeed((long) (this.ticks * 312871));
            HungerManager hungerManager = playerEntity.getHungerManager();
            int k = hungerManager.getFoodLevel();
            int m = this.scaledWidth / 2 - 91;
            int n = this.scaledWidth / 2 + 91;
            int o = this.scaledHeight - 39;
            float f = Math.max((float) playerEntity.getAttributeValue(EntityAttributes.GENERIC_MAX_HEALTH),
                    (float) Math.max(j, i));
            int p = MathHelper.ceil(playerEntity.getAbsorptionAmount());
            int q = MathHelper.ceil((f + (float) p) / 2.0F / 10.0F);
            int r = Math.max(10 - (q - 2), 3);
            int s = o - (q - 1) * r - 10;
            int t = o - 10;
            int u = playerEntity.getArmor();
            int v = -1;
            if (playerEntity.hasStatusEffect(StatusEffects.REGENERATION)) {
                v = this.ticks % MathHelper.ceil(f + 5.0F);
            }

            this.client.getProfiler().push("armor");

            int x;
            for (int w = 0; w < 10; ++w) {
                if (u > 0) {
                    x = m + w * 8;
                    if (w * 2 + 1 < u) {
                        context.drawTexture(ICONS, x, s, 34, 9, 9, 9);
                    }

                    if (w * 2 + 1 == u) {
                        context.drawTexture(ICONS, x, s, 25, 9, 9, 9);
                    }

                    if (w * 2 + 1 > u) {
                        context.drawTexture(ICONS, x, s, 16, 9, 9, 9);
                    }
                }
            }

            this.client.getProfiler().swap("health");
            this.renderHealthBar(context, playerEntity, m, o, r, v, f, i, j, p, bl);
            LivingEntity livingEntity = this.getRiddenEntity();
            x = this.getHeartCount(livingEntity);
            int y;
            int z;
            int aa;
            int ab;
            int ac;
            if (x == 0 /* PICKAXE / && !Pickaxe.getInstance().isInPickaxe()) {
                this.client.getProfiler().swap("food");

                for (y = 0; y < 10; ++y) {
                    z = o;
                    aa = 16;
                    ab = 0;
                    if (playerEntity.hasStatusEffect(StatusEffects.HUNGER)) {
                        aa += 36;
                        ab = 13;
                    }

                    if (playerEntity.getHungerManager().getSaturationLevel() <= 0.0F && this.ticks % (k * 3 + 1) == 0) {
                        z = o + (this.random.nextInt(3) - 1);
                    }

                    ac = n - y * 8 - 9;
                    context.drawTexture(ICONS, ac, z, 16 + ab * 9, 27, 9, 9);
                    if (y * 2 + 1 < k) {
                        context.drawTexture(ICONS, ac, z, aa + 36, 27, 9, 9);
                    }

                    if (y * 2 + 1 == k) {
                        context.drawTexture(ICONS, ac, z, aa + 45, 27, 9, 9);
                    }
                }

                t -= 10;
            } else if (Pickaxe.getInstance().isInPickaxe() && x == 0) { // Pickaxe start
                MinecraftClient client = MinecraftClient.getInstance();
                TextRenderer renderer = client.textRenderer;
                this.client.getProfiler().swap("food");

                try {
                    String[] footer = ((PlayerHudListMixin) client.inGameHud.getPlayerListHud()).getFooter().getString()
                            .split("\n");

                    // get the coins from the footer
                    String coins = '⛃' + footer[2].replaceAll("[^0-9\\.]", "");

                    // Calculate the hunger bar values
                    int xhp = client.getWindow().getScaledWidth() / 2 - 91;
                    int ybottom = client.getWindow().getScaledHeight() - 39;

                    // Define the height of the hunger bar

                    // Calculate the health and hunger bar widths
                    int hpWidth = Math.round(20 / 2.0f * 18.0f);

                    // Calculate the x-coordinate of the right edge of the health bar
                    int xhpRight = xhp + hpWidth;

                    // Draw the custom hunger bar

                    // Draw the coins value
                    int coinsWidth = renderer.getWidth(coins);
                    context.drawTextWithShadow(renderer, coins.toString(), xhpRight - coinsWidth, ybottom, 0xFFFF00);
                } catch (Exception e) {
                    Pickaxe.LOGGER.error("Error while drawing custom hunger bar", e);

                    // Draw a empty hunger bar with 0 coins
                    // Calculate the hunger bar values
                    int xhp = client.getWindow().getScaledWidth() / 2 - 91;
                    int ybottom = client.getWindow().getScaledHeight() - 39;

                    // Define the height of the hunger bar

                    // Calculate the health and hunger bar widths
                    int hpWidth = Math.round(20 / 2.0f * 18.0f);

                    // Calculate the x-coordinate of the right edge of the health bar
                    int xhpRight = xhp + hpWidth;

                    // Draw the custom hunger bar
                    String coins = '⛃' + "0 (Error)";
                    int coinsWidth = renderer.getWidth(coins);
                    context.drawTextWithShadow(renderer, coins.toString(), xhpRight - coinsWidth, ybottom, 0xFFFF00);

                }
                t -= 10;
            } // Pickaxe end

            this.client.getProfiler().swap("air");
            y = playerEntity.getMaxAir();
            z = Math.min(playerEntity.getAir(), y);
            if (playerEntity.isSubmergedIn(FluidTags.WATER) || z < y) {
                aa = this.getHeartRows(x) - 1;
                t -= aa * 10;
                ab = MathHelper.ceil((double) (z - 2) * 10.0 / (double) y);
                ac = MathHelper.ceil((double) z * 10.0 / (double) y) - ab;

                for (int ad = 0; ad < ab + ac; ++ad) {
                    if (ad < ab) {
                        context.drawTexture(ICONS, n - ad * 8 - 9, t, 16, 18, 9, 9);
                    } else {
                        context.drawTexture(ICONS, n - ad * 8 - 9, t, 25, 18, 9, 9);
                    }
                }
            }

            this.client.getProfiler().pop();
        }

    }*/

    @Shadow
    abstract int getHeartRows(int x);

    @Shadow
    abstract public void renderHealthBar(DrawContext context,
            PlayerEntity player,
            int x,
            int y,
            int lines,
            int regeneratingHeartIndex,
            float maxHealth,
            int lastHealth,
            int health,
            int absorption,
            boolean blinking);
}
