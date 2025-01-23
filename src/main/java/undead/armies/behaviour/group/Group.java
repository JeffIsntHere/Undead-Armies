package undead.armies.behaviour.group;

import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.LivingEntity;
import undead.armies.behaviour.group.task.BaseTask;
import undead.armies.behaviour.group.task.selector.TaskSelectorStorage;
import undead.armies.behaviour.single.Single;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class Group
{
    public static final ArrayList<Group> groups = new ArrayList<>();
    public static final int setTaskAttempts = 5;
    public final LivingEntity target;
    protected boolean deleted = false;
    protected final ArrayList<TaskSelectorStorage> taskSelectorStorages = new ArrayList<>();
    protected void reprocessTaskTable()
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
            if(single.groupStorage.task.killed)
            {
                this.setTask(single);
            }
            else if(single.groupStorage.task.deleted)
            {
                if (single.groupStorage.task.handleDelete(single))
                {
                    this.setTask(single);
                }
                else
                {
                    final BaseTask baseTask = single.groupStorage.task;
                    if (baseTask.starter != null && !baseTask.starter.pathfinderMob.isDeadOrDying() && baseTask.starter.groupStorage != null)
                    {
                        baseTask.taskSelectorStorage.taskStorage.add(baseTask);
                        baseTask.deleted = false;
                    }
                }
            }
        }
        final BaseTask baseTask = single.groupStorage.task;
        if(baseTask != null)
        {
            if (baseTask.starter != null && baseTask.starter.pathfinderMob.is(single.pathfinderMob))
            {
                baseTask.taskSelectorStorage.taskSelector.tick(baseTask.taskSelectorStorage, single, this.target);
            }
            baseTask.handleTask(single, this.target);
        }
    }
    public Group(LivingEntity target)
    {
        this.target = target;
        GroupUtil.instance.setTaskSelectors(this.taskSelectorStorages);
        this.reprocessTaskTable();
    }
}
