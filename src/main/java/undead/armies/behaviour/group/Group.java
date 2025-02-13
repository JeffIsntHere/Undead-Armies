package undead.armies.behaviour.group;

import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;
import undead.armies.behaviour.Single;

import java.util.HashSet;

//this class is for advanced targeting.
/*
TODO:
Multi targeting:
Somehow detect which entities are supporting the main target, then attack those entities.

Zig Zag Marching:
If the main target is attacking with range, then march into the target with a zigzag pattern.
 */
public class Group
{
    public final LivingEntity mainTarget;
    public final HashSet<LivingEntity> subTargets = new HashSet<>();
    public void tick(@NotNull final Single single)
    {

    }
    @Override
    public int hashCode()
    {
        return this.mainTarget.hashCode();
    }
    @Override
    public boolean equals(Object other)
    {
        if(!(other instanceof Group))
        {
            return false;
        }
        return ((Group) other).mainTarget.equals(this.mainTarget);
    }
    public Group(@NotNull LivingEntity livingEntity)
    {
        this.mainTarget = livingEntity;
    }
}
