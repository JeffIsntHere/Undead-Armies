package undead.armies.behaviour.group.task;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import undead.armies.behaviour.single.Single;

public class Mine extends BaseTask
{
    public static final int mineAdd = 0;
    public static final int mineRemove = 1;
    public static final double maxMiningDistance = 5.0d;
    public static final float miningProgressToBlastResistanceRatio = 0.3f;
    public BlockPos mineTarget;
    public int miningProgress = 0;
    public boolean taskIsDone = false;
    public Mine(Single starter, final int taskSelectorIndex)
    {
        super(starter, taskSelectorIndex);
        this.mineTarget = starter.pathfinderMob.blockPosition();
    }

    @Override
    public void handleTask(final Single single, final LivingEntity target)
    {
        if(single.groupStorage == null)
        {
            return;
        }
        if(this.taskIsDone)
        {
            single.groupStorage.resetGroupStorage();
            return;
        }
        if(this.deleted)
        {
            this.starter = single;
            this.addBackToGroup();
        }
        if(single.groupStorage.assignedTask == Mine.mineAdd)
        {
            this.miningProgress++;
        }
        else
        {
            this.miningProgress--;
        }
        if(this.miningProgress < 1)
        {
            single.groupStorage.assignedTask = Mine.mineAdd;
            if(this.miningProgress < 0)
            {
                this.miningProgress = 0;
            }
        }
        else
        {
            single.groupStorage.assignedTask = Mine.mineRemove;
        }
        final BlockState blockState = single.pathfinderMob.level().getBlockState(this.mineTarget);
        if(this.miningProgress * Mine.miningProgressToBlastResistanceRatio > blockState.getBlock().getExplosionResistance())
        {
            Block.dropResources(blockState, single.pathfinderMob.level(), this.mineTarget);
            this.starter = null;
            this.taskIsDone = true;
            single.groupStorage.resetGroupStorage();
        }
    }
}
