package undead.armies.behaviour.group.task;

import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;
import undead.armies.behaviour.group.task.selector.TaskSelectorStorage;
import undead.armies.behaviour.single.Single;

public abstract class BaseTask
{
    @NotNull
    public Single starter; //starter.groupStorage may be null!
    public boolean deleted = false;
    //killed = forced delete.
    public boolean killed = false;
    public final TaskSelectorStorage taskSelectorStorage;
    //true = tick taskSelectorStorage.
    //false = do nothing
    public abstract boolean handleTask(@NotNull final Single single, @NotNull final LivingEntity target);
    //true = give me new task.
    //false = check me again, if starter isn't null add me back!
    public boolean handleDelete(@NotNull final Single single)
    {
        return true;
    }
    public void splitTask(@NotNull final Single starter) {}
    public void mergeTask(@NotNull final Single starter) {}
    public BaseTask(@NotNull final Single starter, final TaskSelectorStorage taskSelectorStorage)
    {
        this.starter = starter;
        this.taskSelectorStorage = taskSelectorStorage;
    }
}
