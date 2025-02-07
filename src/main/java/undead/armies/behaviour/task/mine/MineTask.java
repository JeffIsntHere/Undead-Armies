package undead.armies.behaviour.task.mine;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import undead.armies.Util;
import undead.armies.behaviour.Single;
import undead.armies.behaviour.task.BaseTask;
import undead.armies.behaviour.type.Engineer;

import java.util.ArrayList;
import java.util.HashMap;

public class MineTask extends BaseTask
{
    public static final HashMap<ChunkPos, ArrayList<MineStorage>> tasks = new HashMap<>();
    public static final double minimumDotResult = 0.2f;
    public static final double maxMiningDistance = 5.0d;
    public static int getPositiveOrNegativeOne(final double d)
    {
        if(d < 0)
        {
            return -1;
        }
        return 1;
    }

    public static ArrayList<BlockPos> getBlockPosForSingle(@NotNull Single single, @NotNull Vec3 directionToTarget)
    {
        final int xDirection = MineTask.getPositiveOrNegativeOne(directionToTarget.x);
        final int zDirection = MineTask.getPositiveOrNegativeOne(directionToTarget.z);
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

    public static Vec3 blockPosToVec3(@NotNull final BlockPos blockPos)
    {
        return new Vec3(blockPos.getX(), blockPos.getY(), blockPos.getZ());
    }

    public static MineStorage getMineTask(@NotNull final Single single, @NotNull final Vec3 directionToTarget)
    {
        final ChunkPos currentChunkPos = single.pathfinderMob.chunkPosition();
        final Vec3 direction = directionToTarget.normalize();
        ArrayList<MineStorage> zeroZero = MineTask.tasks.get(currentChunkPos);
        if(zeroZero == null)
        {
            zeroZero = new ArrayList<>();
            zeroZero.add(new MineStorage(MineTask.getBlockPosForSingle(single, direction), direction, single.pathfinderMob.level()));
            MineTask.tasks.put(currentChunkPos, zeroZero);
        }
        final Vec3 singlePosition = single.currentPosition;
        zeroZero.removeIf(mineStorage -> mineStorage.finished);
        for(MineStorage mineStorage : zeroZero)
        {
            if(direction.dot(mineStorage.direction) > MineTask.minimumDotResult && singlePosition.distanceTo(MineTask.blockPosToVec3(mineStorage.current)) < MineTask.maxMiningDistance)
            {
                return mineStorage;
            }
        }
        zeroZero.add(new MineStorage(MineTask.getBlockPosForSingle(single, direction), direction, single.pathfinderMob.level()));
        return zeroZero.getLast();
    }

    public int triggerAfter = 0;
    @Override
    public boolean handleTask(@NotNull Single single)
    {
        this.triggerAfter--;
        if(triggerAfter > 0)
        {
            return false;
        }
        if(Util.isMoving(single))
        {
            return false;
        }
        final LivingEntity target = single.pathfinderMob.getTarget();
        if(target == null || target.position().y + 1 < single.currentPosition.y)
        {
            return false;
        }
        this.triggerAfter = (single.baseType instanceof Engineer) ? 15 : 30;
        MineTask.getMineTask(single, target.position().subtract(single.currentPosition)).tick(single);
        return true;
    }
}
