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
    public static final float baseWeight = 0.5f;
    public static final double expectedDistanceToPlayer = 5.0d;
    public static final StackTaskSelector instance = new StackTaskSelector();

    @Override
    public BaseTask getSuitableTask(@NotNull final TaskSelectorStorage taskSelectorStorage, @NotNull final Single single, @NotNull final LivingEntity target)
    {
        if(BaseTaskSelector.isMoving(single))
        {
            return null;
        }
        final ArrayList<BaseTask> tasks = taskSelectorStorage.taskStorage;
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
        tasks.add(new Stack(single, taskSelectorStorage));
        return tasks.get(tasks.size() - 1);
    }

    @Override
    public boolean tick(@NotNull TaskSelectorStorage taskSelectorStorage, @NotNull Single single, @NotNull LivingEntity target)
    {
        double sumOfDifferences = 0;
        double targetHeight = target.position().y;
        int numberOfEntries = 0;
        float calculatedWeight = StackTaskSelector.baseWeight;
        for(BaseTask task : taskSelectorStorage.taskStorage)
        {
            if(task.starter != null && task.starter.currentPosition != null)
            {
                sumOfDifferences += targetHeight - task.starter.currentPosition.y;
                numberOfEntries++;
            }
        }
        sumOfDifferences = sumOfDifferences/((double)numberOfEntries)/StackTaskSelector.expectedDistanceToPlayer;
        //positive = majority is below the player. Therefore, it is good to stack.
        //negative = majority is above the player. Therefore, it is less good to stack.
        final float commonFloat = 0.2f + ((float)sumOfDifferences)/10.0f;
        if(sumOfDifferences > 0)
        {
            calculatedWeight+=0.15f;
        }
        if(sumOfDifferences > 1.0f)
        {
            calculatedWeight+=commonFloat;
        }
        if(sumOfDifferences < 0)
        {
            calculatedWeight-=0.15f;
        }
        if(sumOfDifferences < 1.0f)
        {
            calculatedWeight-=commonFloat;
            Math.max(calculatedWeight, 0.1f);
            final int taskStorageSize = taskSelectorStorage.taskStorage.size();
            int amountToBeDeleted = (int) Math.floor(StackTaskSelector.baseWeight - calculatedWeight) * taskStorageSize + 1;
            if(amountToBeDeleted > taskStorageSize)
            {
                amountToBeDeleted = taskStorageSize;
            }
            for(int i = 0; i < amountToBeDeleted; i++)
            {
                taskSelectorStorage.taskStorage.removeLast().killed = true;
            }
        }
        taskSelectorStorage.rawWeight = calculatedWeight;
        return true;
    }

    private StackTaskSelector(){}
}
