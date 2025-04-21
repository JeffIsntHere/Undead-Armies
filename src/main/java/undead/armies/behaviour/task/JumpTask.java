package undead.armies.behaviour.task;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import undead.armies.behaviour.task.argument.Argument;
import undead.armies.behaviour.task.argument.Situation;
import undead.armies.misc.BlockUtil;
import undead.armies.misc.Util;
import undead.armies.misc.ClosestUnobstructedBlock;
import undead.armies.behaviour.Single;
import undead.armies.misc.PathfindingTracker;
import undead.armies.parser.config.type.BooleanType;
import undead.armies.parser.config.type.NumberType;

import java.util.ArrayDeque;

public class JumpTask extends BaseTask
{
    public static final NumberType cooldown = new NumberType("cooldown", "cooldown for each jump attempt.", 20);
    public static final BooleanType disableMovementCheck = new BooleanType("disableMovementCheck", "disabling this = undead mobs will bunny hop to their target.", false);
    public static final NumberType maxMemorySize = new NumberType("maxMemorySize", "setting this number to less than 10 makes the undead mobs more prone to getting stuck in spirals.", 10);
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
    protected PathfindingTracker pathfindingTracker = new PathfindingTracker(JumpTask.cooldown.value);
    @Override
    public boolean handleTask(@NotNull Single single, final Argument argument)
    {
        this.pathfindingTracker.tick();
        if(this.triggerAfter > single.pathfinderMob.tickCount)
        {
            return false;
        }
        final LivingEntity target = single.pathfinderMob.getTarget();
        this.triggerAfter = single.pathfinderMob.tickCount + JumpTask.cooldown.value;
        if((argument.value & 8) == 8 || (argument.value & 4) == 0)
        {
            return false;
        }
        if(JumpTask.disableMovementCheck.value)
        {
            if((argument.value & 1) == 0)
            {
                return false;
            }
        }
        else if(!this.pathfindingTracker.tick(single))
        {
            return false;
        }
        this.pathfindingTracker.hasAttemptedPathfinding = false;
        final BlockPos startingPoint = single.pathfinderMob.blockPosition();
        final Level level = single.pathfinderMob.level();
        final ClosestUnobstructedBlock ClosestUnobstructedBlock = new ClosestUnobstructedBlock(target.blockPosition(), level, startingPoint);
        for(Vec3i vec3i : locationTable)
        {
            final BlockPos middle = startingPoint.offset(vec3i);
            final BlockState middleBlockState = level.getBlockState(middle);
            if(middleBlockState.isAir())
            {
                final BlockPos belowMiddle = middle.below();
                final BlockState belowMiddleBlockState = level.getBlockState(belowMiddle);
                if(belowMiddleBlockState.isAir())
                {
                    final BlockPos bottom = belowMiddle.below();
                    final BlockState bottomBlockState = level.getBlockState(bottom);
                    if(bottomBlockState.isAir())
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
                else if(BlockUtil.blockIsNotLava(belowMiddleBlockState) && level.getBlockState(middle.above()).isAir())
                {
                    this.addToClosestBlockPosIfNotLastBlockPos(ClosestUnobstructedBlock, belowMiddle);
                }
            }
            else if(BlockUtil.blockIsNotLava(middleBlockState) && level.getBlockState(middle.above()).isAir() && level.getBlockState(middle.above(2)).isAir())
            {
                this.addToClosestBlockPosIfNotLastBlockPos(ClosestUnobstructedBlock, middle);
            }
        }
        final int blockPosMemorySize = blockPosMemory.size();
        if(ClosestUnobstructedBlock.closest != null)
        {
            blockPosMemory.add(startingPoint.below());
            single.pathfinderMob.lookAt(target, 180.0f, 180.0f);
            single.pathfinderMob.setDeltaMovement(Util.getThrowVelocity(single.position(), new Vec3(ClosestUnobstructedBlock.closest.getX() + 0.5d, ClosestUnobstructedBlock.closest.getY() + 1.0d, ClosestUnobstructedBlock.closest.getZ() + 0.5d), 5.0f, 0.5f));
        }
        else if(blockPosMemorySize > 0)
        {
            blockPosMemory.removeFirst();
        }
        if(blockPosMemorySize > JumpTask.maxMemorySize.value)
        {
            blockPosMemory.removeFirst();
        }
        return true;
    }
    @Override
    public int situationScore(@NotNull Single single, final Situation situation)
    {
        int score = 0;
        if((situation.value & 1) == 1)
        {
            score++;
        }
        if((situation.value & 2) == 0)
        {
            score++;
        }
        return score;
    }
}
