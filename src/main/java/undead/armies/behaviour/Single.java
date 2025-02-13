package undead.armies.behaviour;

import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.phys.Vec3;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.NotNull;
import undead.armies.base.Resettable;
import undead.armies.behaviour.task.BaseTask;
import undead.armies.behaviour.task.TaskUtil;
import undead.armies.misc.Util;

public class Single implements Resettable
{
    @NotNull
    public final PathfinderMob pathfinderMob;
    @NotNull
    public BaseTask currentTask;
    public int currentTaskLength;
    @NotNull
    public Vec3 lastPosition;
    public void reset()
    {
        this.lastPosition = pathfinderMob.position();
    }
    /*
    stores common arguments or checks to an int. [number = bit position]
    0 = has target (1)
    1 = is moving (2)
    2 = on ground (4)
    3 = is passenger (8)
    4 = is vehicle (16)
     */
    public int arguments;
    //mix into this to add your own arguments
    public void updateArguments()
    {
        this.arguments = 0;
        if(this.pathfinderMob.getTarget() != null)
        {
            this.arguments |= 1;
        }
        if(Util.isMoving(this))
        {
            this.arguments |= 2;
        }
        if(this.pathfinderMob.onGround())
        {
            this.arguments |= 4;
        }
        if(this.pathfinderMob.isPassenger())
        {
            this.arguments |= 8;
        }
        if(this.pathfinderMob.isVehicle())
        {
            this.arguments |= 16;
        }
    }
    public Vec3 position()
    {
        return this.pathfinderMob.position();
    }
    public void tick()
    {
        if(this.pathfinderMob.level().isClientSide)
        {
            return;
        }
        this.updateArguments();
        final int upperBound = this.currentTaskLength;
        for(int i = 0; i < upperBound; i++)
        {
            final boolean result = this.currentTask.handleTask(this, this.arguments);
            this.currentTask = this.currentTask.nextTask;
            if(result)
            {
                break;
            }
        }
        this.lastPosition = pathfinderMob.position();
    }
    public Single(final PathfinderMob pathfinderMob)
    {
        this.pathfinderMob = pathfinderMob;
        this.lastPosition = pathfinderMob.position();
        final Pair<Integer, BaseTask> outputValue = TaskUtil.instance.getTask(this);
        this.currentTask = outputValue.getRight();
        this.currentTaskLength = outputValue.getLeft();
    }
}
