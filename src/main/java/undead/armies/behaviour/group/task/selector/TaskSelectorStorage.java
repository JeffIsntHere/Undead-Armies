package undead.armies.behaviour.group.task.selector;

import undead.armies.behaviour.group.task.BaseTask;

import java.util.ArrayList;

public class TaskSelectorStorage
{
    public final BaseTaskSelector taskSelector;
    public float rawWeight;
    public float weight = 0.0f;
    public TaskSelectorStorage(final BaseTaskSelector taskSelector, final float rawWeight)
    {
        this.taskSelector = taskSelector;
        this.rawWeight = rawWeight;
    }
}
