package undead.armies.behaviour.group.task.selector;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import undead.armies.behaviour.group.task.BaseTask;
import undead.armies.behaviour.group.task.Mine;
import undead.armies.behaviour.single.Single;

import java.util.ArrayList;

public class MineTaskSelector extends BaseTaskSelector
{
    public static final MineTaskSelector instance = new MineTaskSelector();
    public static final double maxDistanceFromTask = 12.0d;
    public static final double minDistanceToBreakAtDirectionPow2 = 0.01d;
    protected static int getPositiveOrNegativeOne(final double d)
    {
        if(d < 0)
        {
            return -1;
        }
        return 1;
    }
    @Override
    public BaseTask getSuitableTask(@NotNull final ArrayList<BaseTask> tasks, @NotNull final Single single, @NotNull final LivingEntity target, @NotNull final int taskIndex)
    {
        if(!single.pathfinderMob.getNavigation().isStuck() && !single.pathfinderMob.getNavigation().isDone())
        {
            return null;
        }
        BaseTaskSelector.cleanTasks(tasks);
        single.groupStorage.assignedTask = Mine.mineAdd;
        final Vec3 position = single.pathfinderMob.position();
        for(BaseTask baseTask : tasks)
        {
            if(baseTask instanceof Mine mine)
            {
                if(position.distanceTo(new Vec3(mine.mineTarget.getX(), mine.mineTarget.getY(), mine.mineTarget.getZ())) <= MineTaskSelector.maxDistanceFromTask)
                {
                    return baseTask;
                }
            }
        }
        final Vec3 directionToTarget = target.position().subtract(position);
        final BlockPos singleBlockPos = single.pathfinderMob.blockPosition();
        final double xPow2 = directionToTarget.x * directionToTarget.x;
        final double zPow2 = directionToTarget.z * directionToTarget.z;
        final BlockPos blockPos;
        final Level level = single.pathfinderMob.level();
        final int positiveOrNegativeOneForY = MineTaskSelector.getPositiveOrNegativeOne(directionToTarget.y);
        if(xPow2 < MineTaskSelector.minDistanceToBreakAtDirectionPow2 && zPow2 < MineTaskSelector.minDistanceToBreakAtDirectionPow2)
        {
            BlockPos currentBlockPos = singleBlockPos.atY(singleBlockPos.getY() + positiveOrNegativeOneForY);
            while(level.getBlockState(currentBlockPos).isEmpty())
            {
                currentBlockPos = currentBlockPos.atY(currentBlockPos.getY() + positiveOrNegativeOneForY);
            }
            blockPos = currentBlockPos;
        }
        else if(xPow2 > zPow2)
        {
            blockPos = new BlockPos(singleBlockPos.getX() + MineTaskSelector.getPositiveOrNegativeOne(directionToTarget.x), singleBlockPos.getY() + positiveOrNegativeOneForY, singleBlockPos.getZ());
        }
        else
        {
            blockPos = new BlockPos(singleBlockPos.getX(), singleBlockPos.getY() + positiveOrNegativeOneForY, singleBlockPos.getZ() + MineTaskSelector.getPositiveOrNegativeOne(directionToTarget.z));
        }
        if(single.pathfinderMob.level().getBlockState(blockPos).isEmpty())
        {
            final BlockPos blockPosAtY = blockPos.atY(blockPos.getY() + 1);
            if(!single.pathfinderMob.level().getBlockState(blockPosAtY).isEmpty())
            {
                tasks.add(new Mine(single, taskIndex, blockPosAtY));
            }
            else
            {
                tasks.add(new Mine(single, taskIndex, blockPos.atY(blockPos.getY() - 1)));
            }
        }
        else
        {
            tasks.add(new Mine(single, taskIndex, blockPos));
        }
        return tasks.get(tasks.size() - 1);
    }
    private MineTaskSelector(){}
}
