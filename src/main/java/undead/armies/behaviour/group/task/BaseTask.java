package undead.armies.behaviour.group.task;

import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;
import undead.armies.behaviour.group.task.selector.TaskSelectorStorage;
import undead.armies.behaviour.single.Single;

public abstract class BaseTask
{
    public Single starter; //stater.groupStorage may be null!
    public boolean deleted = false;
    public final TaskSelectorStorage taskSelectorStorage;
    public abstract void handleTask(@NotNull final Single single, @NotNull final LivingEntity target);
    //true = give me new task.
    //false = check me again, if starter isn't null add me back!
    public boolean handleDelete(@NotNull final Single single)
    {
        return true;
    }
    public void splitTask(@NotNull final Single starter) {}
    public BaseTask(@NotNull final Single starter, final TaskSelectorStorage taskSelectorStorage)
    {
        this.starter = starter;
        this.taskSelectorStorage = taskSelectorStorage;
    }
}
