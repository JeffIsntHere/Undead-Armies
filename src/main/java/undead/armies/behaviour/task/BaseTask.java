package undead.armies.behaviour.task;

import org.jetbrains.annotations.NotNull;
import undead.armies.behaviour.Single;

public abstract class BaseTask
{
    @NotNull
    public BaseTask nextTask = this;
    public abstract boolean handleTask(@NotNull final Single single, final Argument argument);
}
