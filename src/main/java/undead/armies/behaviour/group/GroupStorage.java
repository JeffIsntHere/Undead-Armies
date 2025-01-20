package undead.armies.behaviour.group;

import undead.armies.base.Resettable;
import undead.armies.behaviour.group.task.BaseTask;

public class GroupStorage implements Resettable
{
    public final Group group;
    public BaseTask task = null;
    public int assignedTask = Task.nothing;
    public GroupStorage(Group group)
    {
        this.group = group;
    }
    @Override
    public void reset()
    {
        this.task = null;
        this.assignedTask = Task.nothing;
    }
}
