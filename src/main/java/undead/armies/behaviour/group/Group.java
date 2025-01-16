package undead.armies.behaviour.group;

import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.WaterFluid;
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
    protected boolean deleted = false;
    protected final ArrayList<TaskWithChance> unprocessedTasks = new ArrayList<>();
    protected final ArrayList<Object> taskStorage = new ArrayList<>();
    protected final ArrayList<Float> taskTable = new ArrayList<>();
    protected float divisor = 1.0f;
    public Group(LivingEntity target)
    {
        UndeadArmies.logger.debug("created Group!");
        this.target = target;
        this.addTask(new TaskWithChance(Task.stack, 1.0f));
    }
    public void setTask(final Single single)
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
                    return;
                }
            }
            UndeadArmies.logger.debug("failed to pick task!!");
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
        if(this.target.isDeadOrDying())
        {
            if(!this.deleted)
            {
                Group.groups.remove(single.group);
                this.deleted = true;
            }
            single.group = null;
        }
        if(single.currentTask == Task.nothing)
        {
            this.setTask(single);
        }
        UndeadArmies.logger.debug("doing task! " + single.currentTask);
        switch(single.currentTask)
        {
            case Task.nothing -> {}
            case Task.grab -> {
                //impl later.
            }
            case Task.mine -> {
                //impl later.
            }
            case Task.stack -> {
                UndeadArmies.logger.debug("task " + single.currentTask + " a");
                if(single.pathfinderMob.isPathFinding())
                {
                    UndeadArmies.logger.debug("finished task! ");
                    return;
                }
                UndeadArmies.logger.debug("task " + single.currentTask + " b");
                if(this.taskStorage.get(single.taskStorage) == null)
                {
                    UndeadArmies.logger.debug("finished task! ");
                    this.taskStorage.set(single.taskStorage, new Stack(single));
                    return;
                }
                if(((Stack) this.taskStorage.get(single.taskStorage)).legs.pathfinderMob.distanceTo(single.pathfinderMob) <= Stack.minimumDistanceToStack)
                {
                    UndeadArmies.logger.debug("task " + single.currentTask + " c");
                    ((Stack) this.taskStorage.get(single.taskStorage)).legs.pathfinderMob.startRiding(single.pathfinderMob);
                    ((Stack) this.taskStorage.get(single.taskStorage)).legs.currentTask = Task.dismount;
                    ((Stack) this.taskStorage.get(single.taskStorage)).legs = single;
                }
                else
                {
                    UndeadArmies.logger.debug("task " + single.currentTask + " d");
                    single.pathfinderMob.getNavigation().moveTo(((Stack) this.taskStorage.get(single.taskStorage)).legs.pathfinderMob, 0.15f);
                }
                UndeadArmies.logger.debug("finished task! ");
                //issue: func at ServerLevel.tickNonPassenger causes stack overflow.
            }
            case Task.dismount ->
            {
                UndeadArmies.logger.debug("task " + single.currentTask + " a");
                if(this.taskStorage.get(single.taskStorage) == null)
                {
                    single.currentTask = Task.nothing;
                    UndeadArmies.logger.debug("finished task! ");
                    return;
                }
                UndeadArmies.logger.debug("task " + single.currentTask + " b");
                final BlockPos pathFinderMobBlockPos = single.pathfinderMob.blockPosition();
                final int pathFinderMobHeight = (int)Math.ceil(single.pathfinderMob.getEyeHeight());
                final Level level = single.pathfinderMob.level();
                for(int x = -1; x < 1; x++)
                {
                    for(int z = -1; z < 1; z++)
                    {
                        for(int y = 0; y < 2; y++)
                        {
                            BlockPos blockPos = new BlockPos(pathFinderMobBlockPos.getX() + x, pathFinderMobBlockPos.getY() + y, pathFinderMobBlockPos.getZ() + z);
                            {
                                final BlockState blockState = level.getBlockState(blockPos);
                                if (blockState.isEmpty() || blockState.getBlock() instanceof LiquidBlock)
                                {
                                    continue;
                                }
                            }
                            boolean success = true;
                            for(int i = 0; i < pathFinderMobHeight; i++)
                            {
                                final BlockPos blockPosAtY = blockPos.atY(blockPos.getY() + i + 1);
                                if(level.getBlockState(blockPosAtY).isEmpty() || (level.getBlockState(blockPosAtY).getBlock() instanceof LiquidBlock liquidBlock && liquidBlock.fluid instanceof WaterFluid))
                                {
                                    continue;
                                }
                                success = false;
                                break;
                            }
                            if(success)
                            {
                                UndeadArmies.logger.debug("success!");
                                single.pathfinderMob.stopRiding();
                                single.pathfinderMob.dismountTo(blockPos.getX() + 0.5d, blockPos.getY() + 1.0d, blockPos.getZ() + 0.5d);
                                single.currentTask = Task.nothing;
                            }
                            UndeadArmies.logger.debug("finished task! ");
                        }
                    }
                }
            }
        }
    }
}
