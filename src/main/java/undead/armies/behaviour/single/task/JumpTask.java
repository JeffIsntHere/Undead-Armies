package undead.armies.behaviour.single.task;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.LavaFluid;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import undead.armies.UndeadArmies;
import undead.armies.Util;
import undead.armies.behaviour.ClosestBlockPos;
import undead.armies.behaviour.single.Single;

public class JumpTask extends BaseTask
{
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
    @Override
    public boolean handleTask(@NotNull Single single)
    {
        if(triggerAfter > single.pathfinderMob.tickCount || single.pathfinderMob.getTarget() == null || single.pathfinderMob.isPathFinding() || single.pathfinderMob.isPassenger() || !single.pathfinderMob.onGround())
        {
            return false;
        }
        UndeadArmies.logger.debug("ticked!");
        triggerAfter = single.pathfinderMob.tickCount + 60;
        final LivingEntity target = single.pathfinderMob.getTarget();
        final Vec3 targetPosition = target.position();
        final double distance = single.currentPosition.distanceTo(targetPosition);
        final ClosestBlockPos closestBlockPos = new ClosestBlockPos(targetPosition);
        final BlockPos startingPoint = single.pathfinderMob.blockPosition();
        final Level level = single.pathfinderMob.level();
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
                            closestBlockPos.add(belowBottom);
                        }
                    }
                    else if(blockIsNotLava(bottomBlockState))
                    {
                        closestBlockPos.add(bottom);
                    }
                }
                else if(blockIsNotLava(belowMiddleBlockState) && level.getBlockState(middle.above()).isEmpty())
                {
                    //?001?
                    closestBlockPos.add(belowMiddle);
                }
            }
            else if(blockIsNotLava(middleBlockState) && level.getBlockState(middle.above()).isEmpty() && level.getBlockState(middle.above(2)).isEmpty())
            {
                //001??
                closestBlockPos.add(middle);
            }
        }
        if(closestBlockPos.closest != null && closestBlockPos.distance < distance)
        {
            single.pathfinderMob.lookAt(target, 180.0f, 180.0f);
            single.pathfinderMob.setDeltaMovement(single.pathfinderMob.getDeltaMovement().add(Util.getThrowDirection(single.currentPosition, new Vec3(closestBlockPos.closest.getX() + 0.5d, closestBlockPos.closest.getY() + 2.0d, closestBlockPos.closest.getZ() + 0.5d), 10, 1.2d, 1.8d)));
        }
        return true;
    }
}
