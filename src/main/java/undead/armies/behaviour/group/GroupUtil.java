package undead.armies.behaviour.group;

import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;
import undead.armies.behaviour.group.task.selector.MineTaskSelector;
import undead.armies.behaviour.group.task.selector.StackTaskSelector;
import undead.armies.behaviour.group.task.selector.TaskSelectorStorage;
import undead.armies.behaviour.single.Single;

import java.util.ArrayList;

public final class GroupUtil
{
    public static final GroupUtil instance = new GroupUtil();
    //mix into this if you want to add your own tasks.
    public void setTaskSelectors(@NotNull final ArrayList<TaskSelectorStorage> taskSelectorStorages)
    {
        taskSelectorStorages.add(new TaskSelectorStorage(StackTaskSelector.instance, StackTaskSelector.baseWeight));
        taskSelectorStorages.add(new TaskSelectorStorage(MineTaskSelector.instance, MineTaskSelector.baseWeight));
    }
    //mix into this if you want to use your own Group implementation.
    protected Group createGroup(LivingEntity target, Single requester)
    {
        return new Group(target);
    }
    //mix into this if you want to use your own GroupStorage implementation.
    protected GroupStorage createGroupStorage(Group group, Single requester)
    {
        return new GroupStorage(group);
    }
    public Group getGroupThatAttacks(LivingEntity target, Single requester)
    {
        if(target == null)
        {
            return null;
        }
        for(Group group : Group.groups)
        {
            if(group.target.is(target))
            {
                return group;
            }
        }
        final int groupsSize = Group.groups.size();
        Group.groups.add(this.createGroup(target, requester));
        return Group.groups.get(groupsSize);
    }
    public GroupStorage getGroupStorageThatAttacks(LivingEntity target, Single requester)
    {
        final Group group = this.getGroupThatAttacks(target, requester);
        if(group == null)
        {
            return null;
        }
        return this.createGroupStorage(group, requester);
    }
    private GroupUtil(){}
}
