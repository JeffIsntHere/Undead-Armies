package undead.armies.behaviour.group;

import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.LivingEntity;
import undead.armies.UndeadArmies;
import undead.armies.behaviour.group.task.BaseTask;
import undead.armies.behaviour.group.task.selector.TaskSelectorStorage;
import undead.armies.behaviour.single.Single;

import java.util.ArrayList;

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
        this.taskSelectorStorages.sort((left, right) -> Float.compare(right.weight, left.weight));
    }
    public boolean setTask(final Single single)
    {
        int reRollCounter = 0;
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
                        if(reRollCounter > Group.setTaskAttempts)
                        {
                            return false;
                        }
                        reRollCounter++;
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
            Group.groups.remove(single.groupStorage.group);
            this.deleted = true;
            single.reset();
            return;
        }
        if((single.groupStorage.task == null || single.groupStorage.task.killed) && !this.setTask(single))
        {
            return;
        }
        else if(single.groupStorage.task.starter.pathfinderMob.isDeadOrDying() || single.groupStorage.task.starter.groupStorage == null)
        {
            single.groupStorage.task.taskSelectorStorage.taskStorage.remove(single.groupStorage.task);
            single.groupStorage.task.deleted = true;
        }
        final BaseTask lastBaseTask = single.groupStorage.task;
        if(lastBaseTask.deleted)
        {
            if (lastBaseTask.handleDelete(single))
            {
                this.setTask(single);
            }
            else
            {
                final BaseTask baseTask = single.groupStorage.task;
                if (!baseTask.starter.pathfinderMob.isDeadOrDying() && baseTask.starter.groupStorage != null)
                {
                    baseTask.taskSelectorStorage.taskStorage.add(baseTask);
                    baseTask.deleted = false;
                }
            }
        }
        final BaseTask baseTask = single.groupStorage.task;
        if(baseTask != null && baseTask.handleTask(single, this.target) && baseTask.taskSelectorStorage.taskSelector.tick(baseTask.taskSelectorStorage, single, this.target))
        {
            this.reprocessTaskTable();
        }
    }
    public Group(LivingEntity target)
    {
        this.target = target;
        GroupUtil.instance.setTaskSelectors(this.taskSelectorStorages);
        this.reprocessTaskTable();
    }
}
