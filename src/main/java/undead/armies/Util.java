package undead.armies;

import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ThrownPotion;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public final class Util
{
    public static void throwPotion(final LivingEntity livingEntity, final LivingEntity target, ItemStack itemStack)
    {
        final Level level = livingEntity.level();
        level.playSound(null, livingEntity.getX(), livingEntity.getY(), livingEntity.getZ(), SoundEvents.SPLASH_POTION_THROW, SoundSource.NEUTRAL, 1.0f, 1.0f);
        if(!livingEntity.level().isClientSide())
        {
            ThrownPotion thrownPotion = new ThrownPotion(livingEntity.level(), livingEntity);
            thrownPotion.setItem(itemStack);
            final double x = target.getX() - livingEntity.getX();
            final double y = target.getY() - livingEntity.getY();
            final double z = target.getZ() - livingEntity.getZ();
            final double distance = Math.sqrt(x*x + y*y);
            thrownPotion.shoot(x,y + distance * 0.2,z,0.75f, 8.0f);
            level.addFreshEntity(thrownPotion);
        }
    }
}
