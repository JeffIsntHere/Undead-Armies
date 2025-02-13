package undead.armies.behaviour;

import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.phys.Vec3;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.NotNull;
import undead.armies.base.Resettable;
import undead.armies.behaviour.task.BaseTask;
import undead.armies.behaviour.task.TaskUtil;

public class Single implements Resettable
{
    @NotNull
    public final PathfinderMob pathfinderMob;
    @NotNull
    public BaseTask currentTask;
    public int currentTaskLength;
    @NotNull
    public Vec3 lastPosition;
    @NotNull
    public Vec3 currentPosition;
    public void reset()
    {
        this.lastPosition = pathfinderMob.position();
        this.currentPosition = this.lastPosition;
    }
    protected boolean droppedLoot = false;
    public void tick()
    {
        if(this.pathfinderMob.level().isClientSide)
        {
            return;
        }
        this.currentPosition = pathfinderMob.position();

        final int upperBound = this.currentTaskLength;
        for(int i = 0; i < upperBound; i++)
        {
            final boolean result = this.currentTask.handleTask(this);
            this.currentTask = this.currentTask.nextTask;
            if(result)
            {
                break;
            }
        }

        this.lastPosition = this.currentPosition;
    }
    public Single(final PathfinderMob pathfinderMob)
    {
        this.pathfinderMob = pathfinderMob;
        this.lastPosition = pathfinderMob.position();
        this.currentPosition = this.lastPosition;
        final Pair<Integer, BaseTask> outputValue = TaskUtil.instance.getTask(this);
        this.currentTask = outputValue.getRight();
        this.currentTaskLength = outputValue.getLeft();
    }
}
