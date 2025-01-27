package undead.armies.behaviour.group.task.selector;

import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;
import undead.armies.behaviour.group.task.BaseTask;
import undead.armies.behaviour.single.Single;

public abstract class BaseTaskSelector
{
    public abstract BaseTask getSuitableTask(@NotNull final TaskSelectorStorage taskSelectorStorage, @NotNull final Single single, @NotNull final LivingEntity target);
    //the "tick" function is called when the starter of a baseTask is ticked
    //returning true = weights will be recalibrated. (see TaskSelectorStorage & Group.reprocessTaskTable() & Group.setTask(Single single))
    public abstract boolean tick(@NotNull final TaskSelectorStorage taskSelectorStorage, @NotNull final Single single, @NotNull final LivingEntity target);
}
