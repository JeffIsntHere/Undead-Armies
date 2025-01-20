package undead.armies.behaviour.group.task.selector;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import undead.armies.UndeadArmies;
import undead.armies.behaviour.group.task.BaseTask;
import undead.armies.behaviour.group.task.Mine;
import undead.armies.behaviour.single.Single;

import java.util.ArrayList;

public class MineTaskSelector extends BaseTaskSelector
{
    public static final MineTaskSelector instance = new MineTaskSelector();
    public static final double maxDistanceFromTask = 12.0d;
    public static final double distanceToBeConsideredAsNotMoving = 1.0d;
    protected static int getPositiveOrNegativeOne(final double d)
    {
        if(d < 0)
        {
            return -1;
        }
        return 1;
    }
    public static ArrayList<BlockPos> getBlockPosForSingle(@NotNull Single single, @NotNull Vec3 directionToTarget)
    {
        final int xDirection = MineTaskSelector.getPositiveOrNegativeOne(directionToTarget.x);
        final int zDirection = MineTaskSelector.getPositiveOrNegativeOne(directionToTarget.z);
        final int yDirection;
        final int x;
        final int y;
        final int z;
        final ArrayList<BlockPos> output = new ArrayList<>();
        if(directionToTarget.y < 0)
        {
            final BlockPos blockPos = single.pathfinderMob.blockPosition();
            yDirection = -1;
            x = blockPos.getX();
            y = blockPos.getY();
            z = blockPos.getZ();
        }
        else
        {
            final BlockPos blockPos = single.pathfinderMob.blockPosition().atY(single.pathfinderMob.getBlockY() + 1);
            yDirection = 1;
            x = blockPos.getX();
            y = blockPos.getY();
            z = blockPos.getZ();
            output.add(new BlockPos(x, y + 2, z));
            output.add(new BlockPos(x + xDirection, y + 2, z));
            output.add(new BlockPos(x, y + 2, z + zDirection));
            output.add(new BlockPos(x + xDirection, y + 2, z + zDirection));
        }
        //2*2 cube.
        output.add(new BlockPos(x, y + yDirection, z));
        output.add(new BlockPos(x + xDirection, y + yDirection, z));
        output.add(new BlockPos(x, y + yDirection, z + zDirection));
        output.add(new BlockPos(x + xDirection, y + yDirection, z + zDirection));
        output.add(new BlockPos(x, y, z));
        output.add(new BlockPos(x + xDirection, y, z));
        output.add(new BlockPos(x, y, z + zDirection));
        output.add(new BlockPos(x + xDirection, y, z + zDirection));
        return output;
    }
    @Override
    public BaseTask getSuitableTask(@NotNull final ArrayList<BaseTask> tasks, @NotNull final Single single, @NotNull final LivingEntity target, @NotNull final int taskIndex)
    {
        if(single.lastPosition.distanceTo(single.currentPosition) >= MineTaskSelector.distanceToBeConsideredAsNotMoving)
        {
            return null;
        }
        BaseTaskSelector.cleanTasks(tasks);
        final Vec3 position = single.pathfinderMob.position();
        for(BaseTask baseTask : tasks)
        {
            if(baseTask instanceof Mine mine)
            {
                if(position.distanceTo(mine.mineTargetVec3) <= MineTaskSelector.maxDistanceFromTask)
                {
                    return baseTask;
                }
            }
        }
        final Vec3 directionToTarget = target.position().subtract(position);
        final ArrayList<BlockPos> blockPos = MineTaskSelector.getBlockPosForSingle(single, directionToTarget);
        if(blockPos.isEmpty())
        {
            return null;
        }
        tasks.add(new Mine(single, taskIndex, blockPos));
        return tasks.get(tasks.size() - 1);
    }
    private MineTaskSelector(){}
}
