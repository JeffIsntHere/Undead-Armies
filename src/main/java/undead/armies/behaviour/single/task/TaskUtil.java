package undead.armies.behaviour.single.task;

import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.NotNull;

public final class TaskUtil
{
    public final static TaskUtil instance = new TaskUtil();
    @NotNull
    public Pair<Integer, BaseTask> getTask()
    {
        final BaseTask firstTask = new SprintTask();
        firstTask.nextTask = new GrabTask();
        firstTask.nextTask.nextTask = new JumpTask();
        firstTask.nextTask.nextTask.nextTask = firstTask;
        return Pair.of(3, firstTask);
    }
    private TaskUtil(){};
}
