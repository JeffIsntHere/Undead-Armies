package undead.armies.misc;

import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;
import undead.armies.behaviour.Single;

public class PathfindingTracker
{
    public boolean hasAttemptedPathfinding = false;
    public int notPathfindingTicks = 0;
    public LivingEntity target = null;
    public final int maxNotPathfindingTicks;
    public PathfindingTracker(final int maxNotPathfindingTicks)
    {
        this.maxNotPathfindingTicks = maxNotPathfindingTicks;
    }
    public void tick()
    {
        this.notPathfindingTicks++;
    }
    public boolean tick(final @NotNull Single single)
    {
        final LivingEntity target = single.pathfinderMob.getTarget();
        if(target != this.target)
        {
            this.hasAttemptedPathfinding = false;
            this.target = single.pathfinderMob.getTarget();
            return false;
        }
        if(this.target == null)
        {
            return false;
        }
        if(single.pathfinderMob.isPathFinding() || this.notPathfindingTicks > this.maxNotPathfindingTicks)
        {
            this.hasAttemptedPathfinding = true;
        }
        if(!this.hasAttemptedPathfinding || single.pathfinderMob.isPathFinding())
        {
            return false;
        }
        this.notPathfindingTicks = 0;
        return true;
    }
}
