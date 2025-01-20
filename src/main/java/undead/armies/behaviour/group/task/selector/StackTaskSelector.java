package undead.armies.behaviour.group.task.selector;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import undead.armies.behaviour.group.task.BaseTask;
import undead.armies.behaviour.group.task.Stack;
import undead.armies.behaviour.single.Single;

import java.util.ArrayList;

public class StackTaskSelector extends BaseTaskSelector
{
    public static final double distanceAdder = 1.5d;
    public static final double distanceToBeConsideredAsNotMoving = 1.3d;
    public static final StackTaskSelector instance = new StackTaskSelector();
    @Override
    public BaseTask getSuitableTask(@NotNull final ArrayList<BaseTask> tasks, @NotNull final Single single, @NotNull final LivingEntity target, final int taskIndex)
    {
        if(single.lastPosition.distanceTo(single.currentPosition) >= StackTaskSelector.distanceToBeConsideredAsNotMoving)
        {
            return null;
        }
        BaseTaskSelector.cleanTasks(tasks);
        single.groupStorage.assignedTask = Stack.stack;
        final Vec3 targetPosition = target.position();
        final double distanceToTargetPosition = single.pathfinderMob.position().distanceTo(targetPosition) + distanceAdder;
        for(BaseTask task : tasks)
        {
            if(task.starter.pathfinderMob.position().distanceTo(targetPosition) <= distanceToTargetPosition)
            {
                return task;
            }
        }
        tasks.add(new Stack(single, taskIndex));
        return tasks.get(tasks.size() - 1);
    }
    private StackTaskSelector(){}
}
