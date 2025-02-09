package undead.armies.behaviour.task;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.LavaFluid;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import undead.armies.misc.BlockUtil;
import undead.armies.misc.Util;
import undead.armies.misc.ClosestUnobstructedBlock;
import undead.armies.behaviour.Single;
import undead.armies.misc.PathfindingTracker;

import java.util.ArrayDeque;

public class JumpTask extends BaseTask
{
    public static int cooldown = 20;
    public static int maxMemorySize = 10;
    public static final Vec3i[] locationTable = new Vec3i[]{
            new Vec3i(2,0,0),
            new Vec3i(2,0,1),
            new Vec3i(2,0,-1),
            new Vec3i(3,0,0),
            new Vec3i(3,0,1),
            new Vec3i(3,0,-1),
            new Vec3i(2,0,2),

            new Vec3i(0,0,2),
            new Vec3i(1,0,2),
            new Vec3i(-1,0,2),
            new Vec3i(0,0,3),
            new Vec3i(1,0,3),
            new Vec3i(-1,0,3),
            new Vec3i(2,0,-2),

            new Vec3i(-2,0,0),
            new Vec3i(-2,0,1),
            new Vec3i(-2,0,-1),
            new Vec3i(-3,0,0),
            new Vec3i(-3,0,1),
            new Vec3i(-3,0,-1),
            new Vec3i(-2,0,-2),

            new Vec3i(0,0,-2),
            new Vec3i(1,0,-2),
            new Vec3i(-1,0,-2),
            new Vec3i(0,0,-3),
            new Vec3i(1,0,-3),
            new Vec3i(-1,0,-3),
            new Vec3i(-2,0,2)
    };
    protected int triggerAfter = 0;
    protected final ArrayDeque<BlockPos> blockPosMemory = new ArrayDeque<>();
    protected void addToClosestBlockPosIfNotLastBlockPos(final ClosestUnobstructedBlock ClosestUnobstructedBlock, final BlockPos blockPos)
    {
        for(BlockPos lastBlockPos : this.blockPosMemory)
        {
            if(lastBlockPos.equals(blockPos))
            {
                return;
            }
        }
        ClosestUnobstructedBlock.add(blockPos);
    }
    protected PathfindingTracker pathfindingTracker = new PathfindingTracker(JumpTask.cooldown);
    @Override
    public boolean handleTask(@NotNull Single single)
    {
        this.pathfindingTracker.tick();
        if(this.triggerAfter > single.pathfinderMob.tickCount)
        {
            return false;
        }
        this.triggerAfter = single.pathfinderMob.tickCount + cooldown;
        if(single.pathfinderMob.isPassenger() || !single.pathfinderMob.onGround() || !this.pathfindingTracker.tick(single))
        {
            return false;
        }
        this.pathfindingTracker.hasAttemptedPathfinding = false;
        final LivingEntity target = this.pathfindingTracker.target;
        final BlockPos startingPoint = single.pathfinderMob.blockPosition();
        final Level level = single.pathfinderMob.level();
        final ClosestUnobstructedBlock ClosestUnobstructedBlock = new ClosestUnobstructedBlock(target.blockPosition(), level, startingPoint);
        for(Vec3i vec3i : locationTable)
        {
            final BlockPos middle = startingPoint.offset(vec3i);
            final BlockState middleBlockState = level.getBlockState(middle);
            if(middleBlockState.isEmpty())
            {
                final BlockPos belowMiddle = middle.below();
                final BlockState belowMiddleBlockState = level.getBlockState(belowMiddle);
                if(belowMiddleBlockState.isEmpty())
                {
                    final BlockPos bottom = belowMiddle.below();
                    final BlockState bottomBlockState = level.getBlockState(bottom);
                    if(bottomBlockState.isEmpty())
                    {
                        final BlockPos belowBottom = bottom.below();
                        if(BlockUtil.blockIsGood(bottom.below(), level))
                        {
                            this.addToClosestBlockPosIfNotLastBlockPos(ClosestUnobstructedBlock, belowBottom);
                        }
                    }
                    else if(BlockUtil.blockIsNotLava(bottomBlockState))
                    {
                        this.addToClosestBlockPosIfNotLastBlockPos(ClosestUnobstructedBlock, bottom);
                    }
                }
                else if(BlockUtil.blockIsNotLava(belowMiddleBlockState) && level.getBlockState(middle.above()).isEmpty())
                {
                    this.addToClosestBlockPosIfNotLastBlockPos(ClosestUnobstructedBlock, belowMiddle);
                }
            }
            else if(BlockUtil.blockIsNotLava(middleBlockState) && level.getBlockState(middle.above()).isEmpty() && level.getBlockState(middle.above(2)).isEmpty())
            {
                this.addToClosestBlockPosIfNotLastBlockPos(ClosestUnobstructedBlock, middle);
            }
        }
        final int blockPosMemorySize = blockPosMemory.size();
        if(ClosestUnobstructedBlock.closest != null)
        {
            blockPosMemory.add(startingPoint.below());
            single.pathfinderMob.lookAt(target, 180.0f, 180.0f);
            single.pathfinderMob.setDeltaMovement(Util.getThrowVelocity(single.currentPosition, new Vec3(ClosestUnobstructedBlock.closest.getX() + 0.5d, ClosestUnobstructedBlock.closest.getY() + 1.0d, ClosestUnobstructedBlock.closest.getZ() + 0.5d), 5.0f, 0.5f));
        }
        else if(blockPosMemorySize > 0)
        {
            blockPosMemory.removeFirst();
        }
        if(blockPosMemorySize > JumpTask.maxMemorySize)
        {
            blockPosMemory.removeFirst();
        }
        return true;
    }
}
