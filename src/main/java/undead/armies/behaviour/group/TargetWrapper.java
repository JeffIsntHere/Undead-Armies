package undead.armies.behaviour.group;

import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class TargetWrapper
{
    public final LivingEntity target;
    public final UUID uuid;
    public TargetWrapper(final @NotNull LivingEntity target)
    {
        this.target = target;
        this.uuid = target.getUUID();
    }
    @Override
    public int hashCode()
    {
        return this.uuid.hashCode();
    }
    @Override
    public boolean equals(Object other)
    {
        if(!(other instanceof LivingEntity))
        {
            return false;
        }
        return ((LivingEntity) other).getUUID().equals(this.uuid);
    }
}
