package undead.armies.behaviour.task;

import org.jetbrains.annotations.NotNull;
import undead.armies.behaviour.Single;
import undead.armies.behaviour.task.argument.Argument;
import undead.armies.behaviour.task.argument.Situation;

public abstract class BaseTask
{
    @NotNull
    public BaseTask nextTask = this;
    public abstract boolean handleTask(@NotNull final Single single, final Argument argument);
    //to return a proper score, see Strategy.
    public abstract int situationScore(@NotNull final Single single, final Situation situation);
    public void append(final BaseTask nextTask)
    {
        nextTask.nextTask = this.nextTask;
        this.nextTask = nextTask;
    }
}
