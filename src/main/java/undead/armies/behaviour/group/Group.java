package undead.armies.behaviour.group;

import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.LivingEntity;
import undead.armies.behaviour.group.task.selector.MineTaskSelector;
import undead.armies.behaviour.group.task.selector.StackTaskSelector;
import undead.armies.behaviour.group.task.selector.TaskSelectorStorage;
import undead.armies.behaviour.single.Single;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class Group
{
    public static final ArrayList<Group> groups = new ArrayList<>();
    public static final int setTaskAttempts = 5;
    public static final float tickGroupChance = 0.1f;
    public static Group getGroupThatAttacks(LivingEntity target)
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
        Group.groups.add(new Group(target));
        return Group.groups.get(groupsSize);
    }
    public static GroupStorage getGroupStorageThatAttacks(LivingEntity target)
    {
        final Group group = Group.getGroupThatAttacks(target);
        if(group == null)
        {
            return null;
        }
        return new GroupStorage(group);
    }
    public final LivingEntity target;
    protected boolean deleted = false;
    protected final ArrayList<TaskSelectorStorage> taskSelectorStorages = new ArrayList<>();
    public void reprocessTaskTable()
    {
        float divisor = 0.0f;
        for(TaskSelectorStorage taskSelectorStorage : this.taskSelectorStorages)
        {
            divisor += taskSelectorStorage.rawWeight;
        }
        for(TaskSelectorStorage taskSelectorStorage : this.taskSelectorStorages)
        {
            taskSelectorStorage.weight = (taskSelectorStorage.rawWeight / divisor);
        }
        Collections.sort(this.taskSelectorStorages, new Comparator<TaskSelectorStorage>()
        {
            @Override
            public int compare(TaskSelectorStorage left, TaskSelectorStorage right)
            {
                return left.weight > right.weight ? -1 : (left.weight < right.weight) ? 1 : 0;
            }
        });
    }
    public boolean setTask(final Single single)
    {
        int rerollCounter = 0;
        final RandomSource randomSource = single.pathfinderMob.getRandom();
        final int sizeOfProcessedTasks = this.taskSelectorStorages.size();
        if(sizeOfProcessedTasks != 0)
        {
            final float randomResult = randomSource.nextFloat();
            float cumulative = 0.0f;
            for(int i = 0; i < sizeOfProcessedTasks; i++)
            {
                final TaskSelectorStorage taskSelectorStorage = this.taskSelectorStorages.get(i);
                cumulative += taskSelectorStorage.weight;
                if(cumulative >= randomResult)
                {
                    single.groupStorage.task = taskSelectorStorage.taskSelector.getSuitableTask(taskSelectorStorage, single, this.target);
                    if(single.groupStorage.task != null)
                    {
                        return true;
                    }
                    else
                    {
                        if(rerollCounter > Group.setTaskAttempts)
                        {
                            return false;
                        }
                        rerollCounter++;
                    }
                }
            }
        }
        return false;
    }
    public void doGroupTask(Single single)
    {
        if(this.deleted)
        {
            single.reset();
            return;
        }
        if(this.target.isDeadOrDying())
        {
            if(!this.deleted)
            {
                Group.groups.remove(single.groupStorage.group);
                this.deleted = true;
            }
            single.reset();
            return;
        }
        if(single.groupStorage.task == null)
        {
            this.setTask(single);
        }
        else if(single.groupStorage.task.starter == null || single.groupStorage.task.starter.pathfinderMob.isDeadOrDying() || single.groupStorage.task.starter.groupStorage == null)
        {
            single.groupStorage.task.taskSelectorStorage.taskStorage.remove(single.groupStorage.task);
            single.groupStorage.task.deleted = true;
        }
        if(single.groupStorage.task != null)
        {
            if(single.groupStorage.task.deleted)
            {
                if (single.groupStorage.task.handleDelete(single))
                {
                    this.setTask(single);
                }
                else
                {
                    if (single.groupStorage.task.starter != null && single.groupStorage.task.starter.pathfinderMob.isDeadOrDying())
                    {
                        single.groupStorage.task.taskSelectorStorage.taskStorage.add(single.groupStorage.task);
                        single.groupStorage.task.deleted = false;
                    }
                }
            }
        }
        if(single.groupStorage.task != null)
        {
            single.groupStorage.task.handleTask(single, this.target);
        }
    }
    public Group(LivingEntity target)
    {
        this.target = target;
        this.taskSelectorStorages.add(new TaskSelectorStorage(StackTaskSelector.instance, 0.6f));
        this.taskSelectorStorages.add(new TaskSelectorStorage(MineTaskSelector.instance, 0.4f));
        this.reprocessTaskTable();
    }
}
