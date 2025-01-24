package undead.armies.behaviour.group.task;

import net.minecraft.core.BlockPos;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.WaterFluid;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import undead.armies.UndeadArmies;
import undead.armies.Util;
import undead.armies.base.GetSingle;
import undead.armies.behaviour.group.task.selector.TaskSelectorStorage;
import undead.armies.behaviour.single.Single;

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
                single.pathfinderMob.removeAllEffects();
                Util.glow(single.pathfinderMob, 20);
                if(single.pathfinderMob.is(super.starter.pathfinderMob))
                {
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
                single.pathfinderMob.removeAllEffects();
                final Vec3 targetPosition = target.position();
                Vec3 direction = targetPosition.subtract(single.currentPosition);
                final BlockPos pathFinderMobBlockPos = single.pathfinderMob.blockPosition();
                final int pathFinderMobHeight = (int)Math.ceil(single.pathfinderMob.getEyeHeight());
                final Level level = single.pathfinderMob.level();
                final double distance = targetPosition.distanceTo(single.pathfinderMob.position());

                for(int x = -2; x < 2; x++)
                {
                    for(int z = -2; z < 2; z++)
                    {
                        for(int y = -2; y < 2; y++)
                        {
                            BlockPos blockPos = new BlockPos(pathFinderMobBlockPos.getX() + x, pathFinderMobBlockPos.getY() + y, pathFinderMobBlockPos.getZ() + z);
                            if(targetPosition.distanceTo(new Vec3(blockPos.getX(), blockPos.getY(), blockPos.getZ())) + 1.0d >= distance)
                            {
                                continue;
                            }
                            {
                                final BlockState blockState = level.getBlockState(blockPos);
                                if (blockState.isEmpty() || blockState.getBlock() instanceof LiquidBlock)
                                {
                                    continue;
                                }
                            }
                            boolean success = true;
                            for(int i = 0; i < pathFinderMobHeight; i++)
                            {
                                final BlockPos blockPosAtY = blockPos.atY(blockPos.getY() + i + 1);
                                if(level.getBlockState(blockPosAtY).isEmpty() || (level.getBlockState(blockPosAtY).getBlock() instanceof LiquidBlock liquidBlock && liquidBlock.fluid instanceof WaterFluid))
                                {
                                    continue;
                                }
                                success = false;
                                break;
                            }
                            if(success)
                            {
                                UndeadArmies.logger.debug("dist " + targetPosition.distanceTo(new Vec3(blockPos.getX(), blockPos.getY(), blockPos.getZ())) + " dist: " + distance);
                                single.pathfinderMob.stopRiding();
                                single.pathfinderMob.dismountTo(blockPos.getX() + 0.5d, blockPos.getY() + 1.0d, blockPos.getZ() + 0.5d);
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
                                    this.splitTask(single);
                                }
                            }
                        }
                    }
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

    protected void changeTaskForPassengers(final BaseTask baseTask, final Entity entity)
    {
        if(!(entity instanceof PathfinderMob))
        {
            return;
        }
        if(entity instanceof GetSingle getSingle)
        {
            if(getSingle.getSingle().groupStorage == null)
            {
                return;
            }
            getSingle.getSingle().groupStorage.task = baseTask;
            for(Entity passenger : entity.getPassengers())
            {
                this.changeTaskForPassengers(baseTask, passenger);
            }
        }
    }

    @Override
    public void splitTask(@NotNull Single single)
    {
        single.groupStorage.task = new Stack(single, this.taskSelectorStorage);
        single.groupStorage.assignedTask = Stack.stack;
        this.changeTaskForPassengers(single.groupStorage.task, single.pathfinderMob);
    }

    @Override
    public void mergeTask(@NotNull Single single)
    {
        this.changeTaskForPassengers(single.groupStorage.task, this.starter.pathfinderMob);
        Entity theFinalPassenger = starter.pathfinderMob;
        while(true)
        {
            List<Entity> nextPassengers = theFinalPassenger.getPassengers();
            if(nextPassengers.isEmpty())
            {
                break;
            }
            theFinalPassenger = nextPassengers.get(0);
            Util.glow((PathfinderMob) theFinalPassenger, 60);
        }
        single.pathfinderMob.startRiding(theFinalPassenger);
        single.groupStorage.assignedTask = Stack.dismount;
    }
    public Stack(@NotNull final Single starter, final TaskSelectorStorage taskSelectorStorage)
    {
        super(starter, taskSelectorStorage);
    }
}
