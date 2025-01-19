package undead.armies.behaviour.group.task.selector;

import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;
import undead.armies.behaviour.group.task.BaseTask;
import undead.armies.behaviour.single.Single;

import java.util.ArrayList;

public abstract class BaseTaskSelector
{
    public BaseTask getSuitableTask(@NotNull final ArrayList<BaseTask> tasks, @NotNull final Single single, @NotNull final LivingEntity target, final int taskSelectorIndex)
    {
        if(tasks.size() > 0)
        {
            return tasks.get(0);
        }
        return null;
    }
    public void tick(@NotNull final ArrayList<BaseTask> tasks, @NotNull final LivingEntity target) {}
}
