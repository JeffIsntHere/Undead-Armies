package undead.armies.behaviour.task;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import undead.armies.UndeadArmies;
import undead.armies.misc.BlockUtil;
import undead.armies.misc.ClosestUnobstructedBlock;
import undead.armies.behaviour.Single;
import undead.armies.parser.config.type.NumberType;

public class DismountTask extends BaseTask
{
    public static final Vec3i[] locationTable = new Vec3i[]{
            new Vec3i(1,0,0),
            new Vec3i(1,0,1),
            new Vec3i(1,0,-1),
            new Vec3i(-1,0,0),
            new Vec3i(-1,0,1),
            new Vec3i(-1,0,-1),
            new Vec3i(0,0,-1),
            new Vec3i(0,0,1),

            new Vec3i(2,0,0),
            new Vec3i(2,0,1),
            new Vec3i(2,0,-1),
            new Vec3i(-2,0,0),
            new Vec3i(-2,0,1),
            new Vec3i(-2,0,-1),

            new Vec3i(0,0,2),
            new Vec3i(1,0,2),
            new Vec3i(-1,0,2),
            new Vec3i(0,0,-2),
            new Vec3i(1,0,-2),
            new Vec3i(-1,0,-2),
    };
    public static NumberType cooldown = new NumberType("cooldown", "cooldown for each attempt at dismounting.",3);
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
        triggerAfter = DismountTask.cooldown.value;
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
        final BlockPos pathFinderMobBlockPos = single.pathfinderMob.blockPosition();
        final Level level = single.pathfinderMob.level();
        final ClosestUnobstructedBlock ClosestUnobstructedBlock = new ClosestUnobstructedBlock(target.blockPosition(), level, pathFinderMobBlockPos.above());
        for(Vec3i vec3i : DismountTask.locationTable)
        {
            final BlockPos middle = pathFinderMobBlockPos.offset(vec3i);
            final BlockPos aboveMiddle = middle.above();
            final BlockPos belowMiddle = middle.below();
            final BlockPos above = aboveMiddle.above();
            final BlockState middleBlockState = level.getBlockState(middle);
            UndeadArmies.logger.debug(middle.toString());
            if(BlockUtil.blockIsGood(middleBlockState))
            {
                if(BlockUtil.blockIsAirOrNotLava(above, level))
                {
                    //??X?0
                    final BlockState aboveMiddleBlockState = level.getBlockState(aboveMiddle);
                    if(BlockUtil.blockIsGood(aboveMiddleBlockState) && BlockUtil.blockIsAirOrNotLava(above.above(), level))
                    {
                        ClosestUnobstructedBlock.add(aboveMiddle);
                    }
                    else if(aboveMiddleBlockState.isAir())
                    {
                        ClosestUnobstructedBlock.add(middle);
                    }
                }
            }
            else if(middleBlockState.isAir())
            {
                final BlockState belowMiddleBlockState = level.getBlockState(belowMiddle);
                if(BlockUtil.blockIsGood(belowMiddleBlockState) && BlockUtil.blockIsAirOrNotLava(aboveMiddle, level))
                {
                    ClosestUnobstructedBlock.add(belowMiddle);
                    continue;
                }
                final BlockPos below = belowMiddle.below();
                if(belowMiddleBlockState.isAir() && BlockUtil.blockIsGood(below, level))
                {
                    ClosestUnobstructedBlock.add(below);
                }
            }
        }
        if(ClosestUnobstructedBlock.closest == null)
        {
            return false;
        }
        single.pathfinderMob.stopRiding();
        single.pathfinderMob.dismountTo(ClosestUnobstructedBlock.closest.getX() + 0.5d, ClosestUnobstructedBlock.closest.getY() + 1.0d, ClosestUnobstructedBlock.closest.getZ() + 0.5d);
        return true;
    }
}
