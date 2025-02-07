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
import undead.armies.Util;
import undead.armies.misc.RENAMELATER;
import undead.armies.behaviour.Single;

import java.util.ArrayDeque;

public class JumpTask extends BaseTask
{
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
    protected static boolean blockIsNotLava(final BlockState blockState)
    {
        return (!(blockState.getBlock() instanceof LiquidBlock liquidBlock) || !(liquidBlock.fluid instanceof LavaFluid));
    }
    protected static boolean blockIsGood(final BlockPos blockPos, final Level level)
    {
        final BlockState blockState = level.getBlockState(blockPos);
        return !blockState.isEmpty() && blockIsNotLava(blockState);
    }
    protected int triggerAfter = 0;
    protected final ArrayDeque<BlockPos> blockPosMemory = new ArrayDeque<>();
    protected void addToClosestBlockPosIfNotLastBlockPos(final RENAMELATER RENAMELATER, final BlockPos blockPos)
    {
        for(BlockPos lastBlockPos : this.blockPosMemory)
        {
            if(lastBlockPos.equals(blockPos))
            {
                return;
            }
        }
        RENAMELATER.add(blockPos);
    }
    @Override
    public boolean handleTask(@NotNull Single single)
    {
        if(triggerAfter > single.pathfinderMob.tickCount || single.pathfinderMob.getTarget() == null || single.pathfinderMob.isPathFinding() || single.pathfinderMob.isPassenger() || !single.pathfinderMob.onGround())
        {
            return false;
        }
        triggerAfter = single.pathfinderMob.tickCount + 20;
        final LivingEntity target = single.pathfinderMob.getTarget();
        final BlockPos startingPoint = single.pathfinderMob.blockPosition();
        final Level level = single.pathfinderMob.level();
        final RENAMELATER RENAMELATER = new RENAMELATER(target.blockPosition(), level, startingPoint);
        for(Vec3i vec3i : locationTable)
        {
            final BlockPos middle = startingPoint.offset(vec3i);
            final BlockState middleBlockState = level.getBlockState(middle);
            if(middleBlockState.isEmpty())
            {
                //??0??
                final BlockPos belowMiddle = middle.below();
                final BlockState belowMiddleBlockState = level.getBlockState(belowMiddle);
                if(belowMiddleBlockState.isEmpty())
                {
                    //??00?
                    final BlockPos bottom = belowMiddle.below();
                    final BlockState bottomBlockState = level.getBlockState(bottom);
                    if(bottomBlockState.isEmpty())
                    {
                        final BlockPos belowBottom = bottom.below();
                        if(blockIsGood(bottom.below(), level))
                        {
                            this.addToClosestBlockPosIfNotLastBlockPos(RENAMELATER, belowBottom);
                        }
                    }
                    else if(blockIsNotLava(bottomBlockState))
                    {
                        this.addToClosestBlockPosIfNotLastBlockPos(RENAMELATER, bottom);
                    }
                }
                else if(blockIsNotLava(belowMiddleBlockState) && level.getBlockState(middle.above()).isEmpty())
                {
                    //?001?
                    this.addToClosestBlockPosIfNotLastBlockPos(RENAMELATER, belowMiddle);
                }
            }
            else if(blockIsNotLava(middleBlockState) && level.getBlockState(middle.above()).isEmpty() && level.getBlockState(middle.above(2)).isEmpty())
            {
                //001??
                this.addToClosestBlockPosIfNotLastBlockPos(RENAMELATER, middle);
            }
        }
        final int blockPosMemorySize = blockPosMemory.size();
        if(RENAMELATER.closest != null)
        {
            blockPosMemory.add(startingPoint.below());
            single.pathfinderMob.lookAt(target, 180.0f, 180.0f);
            single.pathfinderMob.setDeltaMovement(single.pathfinderMob.getDeltaMovement().add(Util.getThrowVelocity(single.currentPosition, new Vec3(RENAMELATER.closest.getX() + 0.5d, RENAMELATER.closest.getY() + 1.0d, RENAMELATER.closest.getZ() + 0.5d), 5.0f, 0.5f)));
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
