package undead.armies.behaviour.group;

import undead.armies.behaviour.group.task.BaseTask;

public class GroupStorage
{
    public final Group group;
    public BaseTask task = null;
    public int assignedTask = Task.nothing;
    public GroupStorage(Group group)
    {
        this.group = group;
    }
    public void resetGroupStorage()
    {
        this.task = null;
        this.assignedTask = Task.nothing;
    }
}
