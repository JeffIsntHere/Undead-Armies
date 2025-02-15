package undead.armies.base;

import net.minecraft.world.entity.LivingEntity;

public interface GetTargetType<T extends LivingEntity>
{
    Class<T> targetType();
}
