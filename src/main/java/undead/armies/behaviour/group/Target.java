package undead.armies.behaviour.group;

import net.minecraft.world.entity.Mob;
import org.jetbrains.annotations.NotNull;

public class Target
{
    public final Mob mob;
    public final double capability;
    public double requiredCapability;
    public Target(@NotNull final Mob mob)
    {
        this.mob = mob;
        this.capability = GroupUtil.instance.getCapability(mob);
        this.requiredCapability = this.capability;
    }
}
