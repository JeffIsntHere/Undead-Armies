package undead.armies.behaviour.group.task.selector;

import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;
import undead.armies.behaviour.group.task.BaseTask;
import undead.armies.behaviour.single.Single;

import java.util.ArrayList;

public abstract class BaseTaskSelector
{
    public static float distanceToBeConsideredAsMoving = 0.7f;
    public static void cleanTasks(@NotNull final ArrayList<BaseTask> tasks)
    {
        tasks.removeIf(
                baseTask ->
                {
                    if(baseTask.starter == null || baseTask.starter.pathfinderMob.isDeadOrDying() || baseTask.starter.groupStorage == null)
                    {
                        baseTask.deleted = true;
                        return true;
                    }
                    return false;
                });
    }
    public static boolean isMoving(@NotNull final Single single)
    {
        return single.lastPosition.distanceTo(single.currentPosition) >= BaseTaskSelector.distanceToBeConsideredAsMoving;
    }
    public abstract BaseTask getSuitableTask(@NotNull final TaskSelectorStorage taskSelectorStorage, @NotNull final Single single, @NotNull final LivingEntity target);
    //the "tick" function is called when the starter of a baseTask is ticked
    //returning true = weights will be recalibrated. (see TaskSelectorStorage & Group.reprocessTaskTable() & Group.setTask(Single single))
    public abstract boolean tick(@NotNull final TaskSelectorStorage taskSelectorStorage, @NotNull final Single single, @NotNull final LivingEntity target);
}
