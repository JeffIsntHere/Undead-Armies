package undead.armies.behaviour.group.task;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.WaterFluid;
import undead.armies.UndeadArmies;
import undead.armies.behaviour.group.Task;
import undead.armies.behaviour.single.Single;

public class Stack extends BaseTask
{
    public static final int stack = 1;
    public static final int dismount = 2;
    public static final int hitCounterToMakeStackingMobsFall = 3;
    public static final float hitDamageToMakeStackingMobsFall = 3.0f;
    public static final float minimumDistanceToStack = 3.0f;
    public Stack(final Single single)
    {
        super(single);
    }
    @Override
    public void handleTask(final Single single, final LivingEntity target)
    {
        UndeadArmies.logger.debug("doing task! " + single.currentTask);
        switch (single.currentTask)
        {
            case Stack.stack ->
            {
                if(single.pathfinderMob.is(super.starter.pathfinderMob) || single.pathfinderMob.isPathFinding())
                {
                    return;
                }
                if(super.starter.pathfinderMob.distanceTo(single.pathfinderMob) <= Stack.minimumDistanceToStack)
                {
                    super.starter.pathfinderMob.startRiding(single.pathfinderMob);
                    super.starter.currentTask = Stack.dismount;
                    super.starter = single;
                    return;
                }
                single.pathfinderMob.getNavigation().moveTo(super.starter.pathfinderMob, 0.2f);
            }
            case Stack.dismount ->
            {
                UndeadArmies.logger.debug("task " + single.currentTask + " b");
                final BlockPos pathFinderMobBlockPos = single.pathfinderMob.blockPosition();
                final int pathFinderMobHeight = (int)Math.ceil(single.pathfinderMob.getEyeHeight());
                final Level level = single.pathfinderMob.level();
                for(int x = -2; x < 2; x++)
                {
                    for(int z = -2; z < 2; z++)
                    {
                        for(int y = -1; y < 2; y++)
                        {
                            BlockPos blockPos = new BlockPos(pathFinderMobBlockPos.getX() + x, pathFinderMobBlockPos.getY() + y, pathFinderMobBlockPos.getZ() + z);
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
                                UndeadArmies.logger.debug("success!");
                                single.pathfinderMob.stopRiding();
                                single.pathfinderMob.dismountTo(blockPos.getX() + 0.5d, blockPos.getY() + 1.0d, blockPos.getZ() + 0.5d);
                                if(!target.isDeadOrDying())
                                {
                                    single.pathfinderMob.getNavigation().moveTo(target, 0.2f);
                                }
                                single.currentTask = Task.nothing;
                            }
                            UndeadArmies.logger.debug("finished task! ");
                        }
                    }
                }
            }
        }
    }
}
