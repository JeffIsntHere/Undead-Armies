package undead.armies.behaviour.task;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import undead.armies.UndeadArmies;
import undead.armies.behaviour.ClosestBlockPos;
import undead.armies.behaviour.Single;

public class DismountTask extends BaseTask
{
    protected AttributeInstance passengerSpeed = null;
    public int triggerAfter = 0;
    @Override
    public boolean handleTask(@NotNull Single single)
    {
        triggerAfter--;
        if(triggerAfter > 0)
        {
            return false;
        }
        triggerAfter = 3;
        final Entity vehicle = single.pathfinderMob.getVehicle();
        if(vehicle == null)
        {
            return false;
        }
        final LivingEntity target = single.pathfinderMob.getTarget();
        if(target == null)
        {
            if(!vehicle.onGround())
            {
                return false;
            }
            if(this.passengerSpeed == null)
            {
                this.passengerSpeed = single.pathfinderMob.getAttribute(Attributes.MOVEMENT_SPEED);
                if(this.passengerSpeed == null)
                {
                    return false;
                }
            }
            final AttributeInstance vehicleSpeed = (vehicle instanceof LivingEntity livingEntity) ? livingEntity.getAttribute(Attributes.MOVEMENT_SPEED) : null;
            if(vehicleSpeed == null || vehicleSpeed.getValue() + 0.1f < this.passengerSpeed.getValue())
            {
                single.pathfinderMob.stopRiding();
                return true;
            }
            return false;
        }
        final Vec3 targetPosition = target.position();
        final BlockPos pathFinderMobBlockPos = single.pathfinderMob.blockPosition();
        final ClosestBlockPos closestBlockPos = new ClosestBlockPos(targetPosition);
        final Level level = single.pathfinderMob.level();
        for (int x = -2; x < 2; x++)
        {
            for (int z = -2; z < 2; z++)
            {
                final BlockPos middle = new BlockPos(pathFinderMobBlockPos.getX() + x, pathFinderMobBlockPos.getY(), pathFinderMobBlockPos.getZ() + z);
                final BlockPos aboveMiddle = middle.above();
                final BlockPos top = aboveMiddle.above();
                final BlockState middleBlockState = level.getBlockState(aboveMiddle);
                if (middleBlockState.isEmpty())
                {
                    final BlockPos belowMiddle = middle.below();
                    if(level.getBlockState(aboveMiddle).isEmpty())
                    {
                        if(level.getBlockState(belowMiddle).isEmpty())
                        {
                            final BlockPos bottom = belowMiddle.below();
                            final BlockState bottomBlockState = level.getBlockState(bottom);
                            if(!bottomBlockState.isEmpty() && !(bottomBlockState.getBlock() instanceof LiquidBlock))
                            {
                                closestBlockPos.add(bottom);
                            }
                        }
                        else if(!(level.getBlockState(belowMiddle).getBlock() instanceof LiquidBlock))
                        {
                            closestBlockPos.add(belowMiddle);
                        }
                        final BlockState topBlockState = level.getBlockState(top);
                        if(!topBlockState.isEmpty() && !(topBlockState.getBlock() instanceof LiquidBlock) && level.getBlockState(top.above()).isEmpty() && level.getBlockState(top.above(2)).isEmpty())
                        {
                            closestBlockPos.add(top);
                        }
                    }
                }
                else if(!(middleBlockState.getBlock() instanceof LiquidBlock) && level.getBlockState(aboveMiddle).isEmpty() && level.getBlockState(top).isEmpty())
                {
                    closestBlockPos.add(middle);
                }
            }
        }
        if(closestBlockPos.closest == null)
        {
            return false;
        }
        if(closestBlockPos.closest.getY() < pathFinderMobBlockPos.getY() - 1)
        {
            return false;
        }
        single.pathfinderMob.stopRiding();
        single.pathfinderMob.dismountTo(closestBlockPos.closest.getX() + 0.5d, closestBlockPos.closest.getY() + 1.0d, closestBlockPos.closest.getZ() + 0.5d);
        return true;
    }
}
