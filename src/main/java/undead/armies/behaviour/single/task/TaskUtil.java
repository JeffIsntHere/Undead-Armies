package undead.armies.behaviour.single.task;

import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.NotNull;
import undead.armies.parser.config.type.BooleanType;

import java.util.ArrayList;

public final class TaskUtil
{
    public final static TaskUtil instance = new TaskUtil();
    public BooleanType enableSprintTask = new BooleanType("enable", true);
    public BooleanType enableGrabTask = new BooleanType("enable", true);
    public BooleanType enableJumpTask = new BooleanType("enable", true);
    public ArrayList<BaseTask> getTaskPool()
    {
        final ArrayList<BaseTask> output = new ArrayList<>();
        if(this.enableSprintTask.value)
        {
            output.add(new SprintTask());
        }
        if(this.enableGrabTask.value)
        {
            output.add(new GrabTask());
        }
        if(this.enableJumpTask.value)
        {
            output.add(new JumpTask());
        }
        return output;
    }
    @NotNull
    public Pair<Integer, BaseTask> getTask()
    {
        final ArrayList<BaseTask> taskPool = this.getTaskPool();
        if(taskPool.isEmpty())
        {
            return Pair.of(0, null);
        }
        final BaseTask firstTask = taskPool.getFirst();
        BaseTask currentTask = firstTask;
        for(int i = 1; i < taskPool.size(); i++)
        {
            firstTask.nextTask = taskPool.get(i);
            currentTask = firstTask.nextTask;
        }
        currentTask.nextTask = firstTask;
        return Pair.of(taskPool.size(), firstTask);
    }
    private TaskUtil(){};
}
