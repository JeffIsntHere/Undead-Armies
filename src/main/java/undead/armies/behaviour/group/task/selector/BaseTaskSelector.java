package undead.armies.behaviour.group.task.selector;

import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;
import undead.armies.behaviour.group.task.BaseTask;
import undead.armies.behaviour.single.Single;

import java.util.ArrayList;

public abstract class BaseTaskSelector
{
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
    public abstract BaseTask getSuitableTask(@NotNull final TaskSelectorStorage taskSelectorStorage, @NotNull final Single single, @NotNull final LivingEntity target);
    //the "tick" function is called when the starter of a baseTask is ticked
    public void tick(@NotNull final TaskSelectorStorage taskSelectorStorage, @NotNull final Single single, @NotNull final LivingEntity target) {}
}
