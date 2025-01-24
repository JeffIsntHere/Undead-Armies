package undead.armies.behaviour.group.task.selector;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import undead.armies.behaviour.group.task.BaseTask;
import undead.armies.behaviour.group.task.Mine;
import undead.armies.behaviour.single.Single;

import java.util.ArrayList;

public class MineTaskSelector extends BaseTaskSelector
{
    public static final MineTaskSelector instance = new MineTaskSelector();
    public static final float baseWeight = 0.5f;
    public static final double maxDistanceFromTask = 12.0d;

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
    public BaseTask getSuitableTask(@NotNull final TaskSelectorStorage taskSelectorStorage, @NotNull final Single single, @NotNull final LivingEntity target)
    {
        if(BaseTaskSelector.isMoving(single))
        {
            return null;
        }
        final ArrayList<BaseTask> tasks = taskSelectorStorage.taskStorage;
        taskSelectorStorage.cleanTaskStorage();
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
        tasks.add(new Mine(single, taskSelectorStorage, blockPos));
        return tasks.get(tasks.size() - 1);
    }

    @Override
    public boolean tick(@NotNull TaskSelectorStorage taskSelectorStorage, @NotNull Single single, @NotNull LivingEntity target)
    {
        double sumOfDifferences = 0;
        double targetHeight = target.position().y;
        int numberOfEntries = 0;
        float calculatedWeight = StackTaskSelector.baseWeight;
        for(BaseTask task : taskSelectorStorage.taskStorage)
        {
            sumOfDifferences += targetHeight - task.starter.currentPosition.y;
            numberOfEntries++;
        }
        sumOfDifferences = sumOfDifferences/((double)numberOfEntries)/StackTaskSelector.expectedDistanceToPlayer;
        //positive = majority is below the player. Therefore, it is good to mine
        //negative = majority is above the player. Therefore, it is bad to mine.
        final float commonFloat = 0.2f + ((float)sumOfDifferences)/10.0f;
        if(sumOfDifferences < 0)
        {
            calculatedWeight+=0.15f;
        }
        if(sumOfDifferences < -1.0f)
        {
            calculatedWeight+=commonFloat;
        }
        if(sumOfDifferences > 0)
        {
            calculatedWeight-=0.15f;
        }
        if(sumOfDifferences > 1.0f)
        {
            calculatedWeight-=commonFloat;
            Math.max(calculatedWeight, 0.1f);
            final int taskStorageSize = taskSelectorStorage.taskStorage.size();
            int amountToBeDeleted = (int) Math.floor(MineTaskSelector.baseWeight - calculatedWeight) * taskStorageSize + 1;
            if(amountToBeDeleted > taskStorageSize)
            {
                amountToBeDeleted = taskStorageSize;
            }
            for(int i = 0; i < amountToBeDeleted; i++)
            {
                taskSelectorStorage.taskStorage.removeLast().killed = true;
            }
        }
        taskSelectorStorage.rawWeight = calculatedWeight;
        return true;
    }

    private MineTaskSelector(){}
}
