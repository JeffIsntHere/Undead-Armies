package undead.armies.behaviour.single.task;

import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.NotNull;

public final class TaskUtil
{
    public final static TaskUtil instance = new TaskUtil();
    @NotNull
    public Pair<Integer, BaseTask> getTask()
    {
        return Pair.of(1, new SprintTask());
    }
    private TaskUtil(){};
}
