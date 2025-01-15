package undead.armies.behaviour.group;

import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.LivingEntity;
import undead.armies.UndeadArmies;
import undead.armies.behaviour.TaskWithChance;
import undead.armies.behaviour.group.storage.Stack;
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
    protected final ArrayList<TaskWithChance> unprocessedTasks = new ArrayList<>();
    protected final ArrayList<Object> taskStorage = new ArrayList<>();
    protected final ArrayList<Float> taskTable = new ArrayList<>();
    protected float divisor = 1.0f;
    public Group(LivingEntity target)
    {
        this.target = target;
        this.addTask(new TaskWithChance(Task.stack, 1.0f));
    }
    public void setTask(Single single)
    {
        final RandomSource randomSource = single.pathfinderMob.getRandom();
        final int sizeOfProcessedTasks = this.taskTable.size();
        if(sizeOfProcessedTasks != 0)
        {
            final float randomResult = randomSource.nextFloat();
            float cumulative = 0.0f;
            for(int i = 0; i < sizeOfProcessedTasks; i++)
            {
                cumulative += taskTable.get(i);
                if(cumulative >= randomResult)
                {
                    single.currentTask = unprocessedTasks.get(i).task;
                    single.taskStorage = i;
                }
            }
        }
    }
    public void reprocessTaskTable()
    {
        this.taskTable.clear();
        float divisor = 0.0f;
        for(TaskWithChance task : this.unprocessedTasks)
        {
            divisor += task.chance;
        }
        for(TaskWithChance task : this.unprocessedTasks)
        {
            this.taskTable.add(task.chance/divisor);
        }
    }
    public void addTask(TaskWithChance taskWithChance)
    {
        this.unprocessedTasks.add(taskWithChance);
        this.taskStorage.add(null);
        this.reprocessTaskTable();
    }
    public void removeTask(int index)
    {
        this.unprocessedTasks.remove(index);
        this.taskStorage.remove(index);
        this.reprocessTaskTable();
    }
    public void doGroupTask(Single single)
    {
        if(single.currentTask == Task.nothing)
        {
            this.setTask(single);
        }
        UndeadArmies.logger.debug("Doing Task:" + single.currentTask);
        switch(single.currentTask)
        {
            case Task.grab -> {
                //impl later.
            }
            case Task.mine -> {
                //impl later.
            }
            case Task.stack -> {
                if(this.taskStorage.get(single.taskStorage) == null)
                {
                    this.taskStorage.set(single.taskStorage, new Stack(single));
                    return;
                }
                if(((Stack) this.taskStorage.get(single.taskStorage)).legs.pathfinderMob.distanceTo(single.pathfinderMob) <= Stack.minimumDistanceToStack)
                {
                    ((Stack) this.taskStorage.get(single.taskStorage)).legs.pathfinderMob.startRiding(single.pathfinderMob);
                    ((Stack) this.taskStorage.get(single.taskStorage)).legs = single;
                }
                else
                {
                    single.pathfinderMob.getNavigation().moveTo(((Stack) this.taskStorage.get(single.taskStorage)).legs.pathfinderMob, 0.15f);
                }
            }
        }
    }
}
