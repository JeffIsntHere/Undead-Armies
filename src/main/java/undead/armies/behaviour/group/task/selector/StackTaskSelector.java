package undead.armies.behaviour.group.task.selector;

import undead.armies.behaviour.group.Task;
import undead.armies.behaviour.group.task.BaseTask;
import undead.armies.behaviour.group.task.Stack;
import undead.armies.behaviour.single.Single;

import java.util.ArrayList;

public class StackTaskSelector extends BaseTaskSelector
{
    public static final StackTaskSelector instance = new StackTaskSelector();
    @Override
    public BaseTask pickSuitableTask(final ArrayList<BaseTask> tasks, final Single single)
    {
        if(tasks.size() == 0)
        {
            tasks.add(new Stack(single));
        }
        if(single.currentTask == Task.nothing)
        {
            single.currentTask = Stack.stack;
        }
        return tasks.get(0);
    }
}
