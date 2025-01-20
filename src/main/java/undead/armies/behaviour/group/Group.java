package undead.armies.behaviour.group;

import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.LivingEntity;
import undead.armies.behaviour.group.task.BaseTask;
import undead.armies.behaviour.group.task.selector.BaseTaskSelector;
import undead.armies.behaviour.group.task.selector.StackTaskSelector;
import undead.armies.behaviour.group.task.selector.TickableTaskSelector;
import undead.armies.behaviour.single.Single;

import java.util.ArrayList;

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
    protected final ArrayList<Float> unNormalizedWeights = new ArrayList<>();
    protected final ArrayList<Float> normalizedWeights = new ArrayList<>();
    protected final ArrayList<ArrayList<BaseTask>> taskStorages = new ArrayList<>();
    protected final ArrayList<BaseTaskSelector> taskSelectors = new ArrayList<>();
    public void reprocessTaskTable()
    {
        this.normalizedWeights.clear();
        float divisor = 0.0f;
        for(float weight : this.unNormalizedWeights)
        {
            divisor += weight;
        }
        for(float weight : this.unNormalizedWeights)
        {
            this.normalizedWeights.add(weight/divisor);
        }
    }
    public void addTaskSelector(BaseTaskSelector baseTaskSelector, float weight)
    {
        this.taskSelectors.add(baseTaskSelector);
        this.unNormalizedWeights.add(weight);
        this.taskStorages.add(new ArrayList<>());
        this.reprocessTaskTable();
    }
    public boolean setTask(final Single single)
    {
        for(int reRollAttempt = 0; reRollAttempt < this.setTaskAttempts; reRollAttempt++)
        {
            final RandomSource randomSource = single.pathfinderMob.getRandom();
            final int sizeOfProcessedTasks = this.normalizedWeights.size();
            if(sizeOfProcessedTasks != 0)
            {
                final float randomResult = randomSource.nextFloat();
                float cumulative = 0.0f;
                for(int i = 0; i < sizeOfProcessedTasks; i++)
                {
                    cumulative += normalizedWeights.get(i);
                    if(cumulative >= randomResult)
                    {
                        single.groupStorage.task = this.taskSelectors.get(i).getSuitableTask(this.taskStorages.get(i), single, this.target, i);
                        return true;
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
        else if(single.groupStorage.task.starter == null || single.groupStorage.task.starter.pathfinderMob.isDeadOrDying())
        {
            this.taskStorages.get(single.groupStorage.task.taskIndex).remove(single.groupStorage.task);
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
                        this.taskStorages.get(single.groupStorage.task.taskIndex).add(single.groupStorage.task);
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
        //this.addTaskSelector(MineTaskSelector.instance, 0.3f);
        this.addTaskSelector(StackTaskSelector.instance, 0.7f);
    }
}
