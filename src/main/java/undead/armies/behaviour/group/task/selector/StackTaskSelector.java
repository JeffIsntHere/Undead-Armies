package undead.armies.behaviour.group.task.selector;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;
import undead.armies.behaviour.group.task.BaseTask;
import undead.armies.behaviour.group.task.Stack;
import undead.armies.behaviour.single.Single;

import java.util.ArrayList;

public class StackTaskSelector extends BaseTaskSelector
{
    public static final double distanceToTargetPositionAdder = 2.0d;
    public static final StackTaskSelector instance = new StackTaskSelector();
    @Override
    public BaseTask getSuitableTask(final ArrayList<BaseTask> tasks, final Single single, final LivingEntity target, final int taskSelectorIndex)
    {
        if(!single.pathfinderMob.getNavigation().isStuck() && !single.pathfinderMob.getNavigation().isDone())
        {
            return null;
        }
        single.groupStorage.assignedTask = Stack.stack;
        tasks.removeIf(baseTask ->
        {
            if(baseTask.starter == null)
            {
                return true;
            }
            if(baseTask.starter.pathfinderMob.isDeadOrDying())
            {
                baseTask.deleted = true;
                return true;
            }
            return false;
        });
        final Vec3 targetPosition = target.position();
        final double distanceToTargetPosition = single.pathfinderMob.position().distanceTo(targetPosition) + StackTaskSelector.distanceToTargetPositionAdder;
        for(BaseTask task : tasks)
        {
            if(task.starter.pathfinderMob.position().distanceTo(targetPosition) <= distanceToTargetPosition)
            {
                return task;
            }
        }
        tasks.add(new Stack(single, taskSelectorIndex));
        return tasks.get(tasks.size() - 1);
    }
}
