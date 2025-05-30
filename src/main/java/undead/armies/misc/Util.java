package undead.armies.misc;

import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.projectile.ThrownPotion;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import undead.armies.behaviour.Single;

public final class Util
{
    public static final float distanceToBeConsideredAsMoving = 0.7f;
    public static final double movementSlowDownConstant = 0.15d; //or 15% according to minecraft wiki.
    public static final double movementSpeedUpConstant = 0.20d; //or 20% according to minecraft wiki.
    public static boolean isMoving(@NotNull final Single single)
    {
        final MobEffectInstance slowDown = single.pathfinderMob.getEffect(MobEffects.MOVEMENT_SLOWDOWN);
        final int slowDownAmplifier = (slowDown == null) ? 0 : slowDown.getAmplifier();
        final MobEffectInstance speedUp = single.pathfinderMob.getEffect(MobEffects.MOVEMENT_SPEED);
        final int speedUpAmplifier = (speedUp == null) ? 0 : speedUp.getAmplifier();
        return single.lastPosition.distanceTo(single.position()) >= Util.distanceToBeConsideredAsMoving * 1.0d/(1.0d + Util.movementSlowDownConstant * slowDownAmplifier) * (1 + Util.movementSpeedUpConstant * speedUpAmplifier);
    }
    public static Vec3 getThrowVelocity(final Vec3 start, final Vec3 end, final float divisor, final float y)
    {
        final Vec3 direction = end.subtract(start);
        return new Vec3(direction.x / divisor, y, direction.z / divisor);
    }
    public static void throwPotion(final LivingEntity livingEntity, final LivingEntity target, ItemStack itemStack, final float velocity, final float accuracy)
    {
        final Level level = livingEntity.level();
        level.playSound(null, livingEntity.getX(), livingEntity.getY(), livingEntity.getZ(), SoundEvents.SPLASH_POTION_THROW, SoundSource.NEUTRAL, 1.0f, 1.0f);
        if(!livingEntity.level().isClientSide())
        {
            ThrownPotion thrownPotion = new ThrownPotion(livingEntity.level(), livingEntity);
            thrownPotion.setItem(itemStack);
            final Vec3 movementDelta = target.getDeltaMovement();
            final double x = target.getX() + movementDelta.x - livingEntity.getX();
            final double z = target.getZ() + movementDelta.z - livingEntity.getZ();
            final double distance = Math.sqrt(x*x + z*z);
            thrownPotion.shoot(x,distance/velocity * 0.5 ,z,velocity, accuracy);
            level.addFreshEntity(thrownPotion);
        }
    }
    public static void makeEntityLookAtBlockPos(final LivingEntity livingEntity, final BlockPos blockPos)
    {
        double x = (blockPos.getX() + 0.5f) - livingEntity.getX();
        double y = (blockPos.getY() + 0.5f) - livingEntity.getY();
        double z = (blockPos.getZ() + 0.5f) - livingEntity.getZ();
        double xZLen = Math.sqrt(x*x + z*z);
        float xRot = (float)(-(Mth.atan2(y, xZLen) * 180.0F / (float)Math.PI));
        float yRot = (float)(Mth.atan2(z, x) * 180.0F / (float)Math.PI) - 90.0F;
        livingEntity.setXRot(xRot);
        livingEntity.setYRot(yRot);
    }
    public static final ItemStack air = new ItemStack(Items.AIR);
    public static final ItemStack redWool = new ItemStack(Items.RED_WOOL);
    public static final ItemStack greenWool = new ItemStack(Items.GREEN_WOOL);
    public static void glow(final Mob mob, final int duration)
    {
        mob.addEffect(new MobEffectInstance(MobEffects.GLOWING, duration));
    }
    public static void clearHeldItem(final Mob mob)
    {
        mob.setItemInHand(InteractionHand.MAIN_HAND, Util.air);
    }
    public static void holdItem(final Mob mob, final ItemStack itemStack)
    {
        mob.setItemInHand(InteractionHand.MAIN_HAND, itemStack);
    }
}
