package undead.armies.behaviour.single.task;

import org.jetbrains.annotations.NotNull;
import undead.armies.behaviour.single.Single;

public abstract class BaseTask
{
    @NotNull
    public BaseTask nextTask = this;
    public abstract boolean handleTask(@NotNull final Single single);
}
