package undead.armies.behaviour.group.task.selector;

import undead.armies.behaviour.group.task.BaseTask;

import java.util.ArrayList;

public class TaskSelectorStorage
{
    public final BaseTaskSelector taskSelector;
    public final ArrayList<BaseTask> taskStorage = new ArrayList<>();
    public float rawWeight;
    public float weight = 0.0f;
    public void cleanTaskStorage()
    {
        taskStorage.removeIf(baseTask -> {
            if(baseTask.killed || baseTask.starter.pathfinderMob.isDeadOrDying() || baseTask.deleted)
            {
                baseTask.deleted = true;
                return true;
            }
            return false;
        });
    }
    public TaskSelectorStorage(final BaseTaskSelector taskSelector, final float rawWeight)
    {
        this.taskSelector = taskSelector;
        this.rawWeight = rawWeight;
    }
}
