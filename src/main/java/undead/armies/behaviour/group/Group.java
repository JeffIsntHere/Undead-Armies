package undead.armies.behaviour.group;

import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.LivingEntity;
import undead.armies.UndeadArmies;
import undead.armies.behaviour.group.task.BaseTask;
import undead.armies.behaviour.group.task.selector.BaseTaskSelector;
import undead.armies.behaviour.group.task.selector.StackTaskSelector;
import undead.armies.behaviour.single.Single;

import java.util.ArrayList;

public class Group
{
    public static ArrayList<Group> groups = new ArrayList<>();
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
    public final LivingEntity target;
    protected boolean deleted = false;
    protected final ArrayList<Float> unNormalizedWeights = new ArrayList<>();
    protected final ArrayList<Float> normalizedWeights = new ArrayList<>();
    protected final ArrayList<ArrayList<BaseTask>> taskStorages = new ArrayList<>();
    protected final ArrayList<BaseTaskSelector> taskSelectors = new ArrayList<>();
    public Group(LivingEntity target)
    {
        this.target = target;
        this.registerTask(StackTaskSelector.instance, 1.0f);
    }
    public BaseTask getTask(final Single single)
    {
        return taskSelectors.get(single.taskStorage).pickSuitableTask(this.taskStorages.get(single.taskStorage), single);
    }
    public void setTaskStorage(final Single single)
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
                    single.taskStorage = i;
                    return;
                }
            }
        }
    }
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
    public void registerTask(BaseTaskSelector baseTaskSelector, float weight)
    {
        this.taskSelectors.add(baseTaskSelector);
        this.unNormalizedWeights.add(weight);
        this.taskStorages.add(new ArrayList<>());
        this.reprocessTaskTable();
    }
    public void doGroupTask(Single single)
    {
        if(this.target.isDeadOrDying())
        {
            if(!this.deleted)
            {
                Group.groups.remove(single.group);
                this.deleted = true;
            }
            if(single.baseTask == null)
            {
                single.group = null;
                single.taskStorage = Task.nothing;
                single.currentTask = Task.nothing;
                return;
            }
            single.baseTask.handleCleanUp(single, this.target);
            return;
        }
        if(single.taskStorage == Task.nothing)
        {
            this.setTaskStorage(single);
        }
        if(single.baseTask == null)
        {
            single.baseTask = this.getTask(single);
        }
        if(single.baseTask != null)
        {
            single.baseTask.handleTask(single, this.target);
        }
    }
}
