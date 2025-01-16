package undead.armies.behaviour.group.task.selector;

import undead.armies.behaviour.group.task.BaseTask;
import undead.armies.behaviour.single.Single;

import java.util.ArrayList;

public abstract class BaseTaskSelector
{
    public BaseTask pickSuitableTask(final ArrayList<BaseTask> tasks, final Single single)
    {
        if(tasks.size() > 0)
        {
            return tasks.get(0);
        }
        return null;
    }
}
