package undead.armies.behaviour.group.task;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import undead.armies.UndeadArmies;
import undead.armies.Util;
import undead.armies.base.GetSingle;
import undead.armies.behaviour.group.task.selector.TaskSelectorStorage;
import undead.armies.behaviour.single.Single;

import java.util.ArrayList;
import java.util.List;

public class Stack extends BaseTask
{
    public static final int stack = 0;
    public static final int dismount = 1;
    public static final float minimumDistanceToStack = 3.0f;

    @Override
    public boolean handleTask(@NotNull final Single single, @NotNull final LivingEntity target)
    {
        switch (single.groupStorage.assignedTask)
        {
            case Stack.stack ->
            {
                if(super.starter.pathfinderMob.is(single.pathfinderMob))
                {
                    Util.holdItem(single.pathfinderMob, Util.redWool);
                    return true;
                }
                if(super.starter.pathfinderMob.position().distanceTo(single.currentPosition) <= Stack.minimumDistanceToStack)
                {
                    super.starter.pathfinderMob.startRiding(single.pathfinderMob);
                    super.starter.groupStorage.assignedTask = Stack.dismount;
                    super.starter = single;
                }
                else
                {
                    single.pathfinderMob.getNavigation().moveTo(super.starter.pathfinderMob, 0.2f);
                }
            }
            case Stack.dismount ->
            {
                if(single.pathfinderMob.getVehicle() == null)
                {
                    this.splitTask(single);
                    return false;
                }
                final Vec3 targetPosition = target.position();
                final BlockPos pathFinderMobBlockPos = single.pathfinderMob.blockPosition();
                final ArrayList<BlockPos> validPositions = new ArrayList<>();
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
                                        validPositions.add(bottom);
                                    }
                                }
                                else if(!(level.getBlockState(belowMiddle).getBlock() instanceof LiquidBlock))
                                {
                                    validPositions.add(belowMiddle);
                                }
                                final BlockState topBlockState = level.getBlockState(top);
                                if(!topBlockState.isEmpty() && !(topBlockState.getBlock() instanceof LiquidBlock) && level.getBlockState(top.above()).isEmpty() && level.getBlockState(top.above(2)).isEmpty())
                                {
                                    validPositions.add(top);
                                }
                            }
                        }
                        else if(!(middleBlockState.getBlock() instanceof LiquidBlock) && level.getBlockState(aboveMiddle).isEmpty() && level.getBlockState(top).isEmpty())
                        {
                            validPositions.add(middle);
                        }
                    }
                }
                if(validPositions.isEmpty())
                {
                    return false;
                }
                BlockPos closest = validPositions.removeFirst();
                double closestDistance = targetPosition.distanceTo(new Vec3(closest.getX(), closest.getY(), closest.getZ()));
                for(BlockPos blockPos : validPositions)
                {
                    final double currentDistance = targetPosition.distanceTo(new Vec3(blockPos.getX(), blockPos.getY(), blockPos.getZ()));
                    if(currentDistance < closestDistance)
                    {
                        closest = blockPos;
                        closestDistance = currentDistance;
                    }
                }
                if(closestDistance > single.pathfinderMob.distanceTo(target))
                {
                    return false;
                }
                single.pathfinderMob.stopRiding();
                single.pathfinderMob.dismountTo(closest.getX() + 0.5d, closest.getY() + 1.0d, closest.getZ() + 0.5d);
                if(!target.isDeadOrDying())
                {
                    single.pathfinderMob.getNavigation().moveTo(target, 0.2f);
                }
                if(single.pathfinderMob.getPassengers().isEmpty())
                {
                    single.groupStorage.reset();
                }
                else
                {
                    single.groupStorage.assignedTask = Stack.stack;
                    this.splitTask(single);
                }
            }
        }
        return false;
    }

    @Override
    public boolean handleDelete(@NotNull Single single)
    {
        if(single.pathfinderMob.getVehicle() == null)
        {
            single.groupStorage.assignedTask = Stack.stack;
            super.starter = single;
        }
        return false;
    }

    protected void setPassengersTaskTo(@NotNull final Entity passenger, @NotNull final BaseTask newTask)
    {
        Entity vehicle = passenger;
        while(vehicle.isPassenger())
        {
            vehicle = vehicle.getVehicle();
            if (vehicle instanceof GetSingle getSingle && getSingle.getSingle().groupStorage != null)
            {
                getSingle.getSingle().groupStorage.task = newTask;
                getSingle.getSingle().groupStorage.assignedTask = Stack.dismount;
            }
        }
    }

    @Override
    public void splitTask(@NotNull Single single)
    {
        if(super.starter == single)
        {
            return;
        }
        super.taskSelectorStorage.taskStorage.add(new Stack(single, super.taskSelectorStorage));
        single.groupStorage.task = super.taskSelectorStorage.taskStorage.getLast();
        this.setPassengersTaskTo(single.pathfinderMob, single.groupStorage.task);
        Util.glow(single.pathfinderMob, 40);
        Util.holdItem(single.pathfinderMob, Util.greenWool);
    }

    @Override
    public void mergeTask(@NotNull BaseTask baseTask)
    {
        if(this == baseTask)
        {
            return;
        }
        baseTask.killed = true;
        final Single single = baseTask.starter;
        single.groupStorage.task = this;
        single.groupStorage.assignedTask = Stack.dismount;
        this.setPassengersTaskTo(single.pathfinderMob, this);
        Entity theFinalPassenger = super.starter.pathfinderMob;
        while(true)
        {
            List<Entity> nextPassengers = theFinalPassenger.getPassengers();
            if(nextPassengers.isEmpty())
            {
                break;
            }
            theFinalPassenger = nextPassengers.get(0);
        }
        single.pathfinderMob.startRiding(theFinalPassenger);
        Util.glow(single.pathfinderMob, 40);
    }
    public Stack(@NotNull final Single starter, final TaskSelectorStorage taskSelectorStorage)
    {
        super(starter, taskSelectorStorage);
    }
}
