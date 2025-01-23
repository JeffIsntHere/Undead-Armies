package undead.armies;

import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.projectile.ThrownPotion;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public final class Util
{
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
    public static void glow(final Mob mob, final int duration)
    {
        mob.addEffect(new MobEffectInstance(MobEffects.GLOWING, duration));
    }
}
