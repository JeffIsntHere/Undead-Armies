package undead.armies.behaviour.task;

import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.NotNull;
import undead.armies.behaviour.Single;
import undead.armies.misc.LinkedList;
import undead.armies.parser.config.type.BooleanType;

import java.util.ArrayList;

public final class TaskUtil
{
    public final static TaskUtil instance = new TaskUtil();
    public BooleanType enableSprintTask = new BooleanType("enable", true);
    public BooleanType enableGrabTask = new BooleanType("enable", true);
    public BooleanType enableJumpTask = new BooleanType("enable", true);
    public BooleanType enableStackTask = new BooleanType("enable", true);
    public BooleanType enableMineTask = new BooleanType("enable",true);
    public BooleanType enableDismountTask = new BooleanType("enable",true);
    //mix into this to add your own tasks!
    public ArrayList<BaseTask> finalizeTaskPool( @NotNull final ArrayList<BaseTask> output)
    {
        return output;
    }
    public ArrayList<BaseTask> getTaskPool(@NotNull final Single single)
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
        if(this.enableStackTask.value)
        {
            output.add(new StackTask());
        }
        if(this.enableMineTask.value)
        {
            output.add(new MineTask());
        }
        if(this.enableDismountTask.value)
        {
            output.add(new DismountTask());
        }
        return this.finalizeTaskPool(output);
    }
    @NotNull
    public Pair<Integer, BaseTask> getTask(@NotNull final Single single)
    {
        final ArrayList<BaseTask> taskPool = this.getTaskPool(single);
        if(taskPool.isEmpty())
        {
            return Pair.of(0, null);
        }
        final BaseTask firstTask = taskPool.getFirst();
        BaseTask currentTask = firstTask;
        for(int i = 1; i < taskPool.size(); i++)
        {
            currentTask.nextTask = taskPool.get(i);
            currentTask = currentTask.nextTask;
        }
        currentTask.nextTask = firstTask;
        return Pair.of(taskPool.size(), firstTask);
    }
    private TaskUtil(){};
}
