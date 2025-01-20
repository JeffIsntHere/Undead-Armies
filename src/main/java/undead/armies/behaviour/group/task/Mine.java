package undead.armies.behaviour.group.task;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import undead.armies.UndeadArmies;
import undead.armies.behaviour.single.Single;

public class Mine extends BaseTask
{
    public static final int mineAdd = 0;
    public static final int mineRemove = 1;
    public static final double maxMiningDistance = 5.0d;
    public static final float miningProgressToBlastResistanceRatio = 1.51f;
    public final BlockPos mineTarget;
    public final Vec3 mineTargetVec3;
    public int miningProgress = 0;
    @Override
    public void handleTask(@NotNull final Single single, @NotNull final LivingEntity target)
    {
        if(single.groupStorage.assignedTask == Mine.mineAdd)
        {
            this.miningProgress++;
        }
        else
        {
            this.miningProgress--;
        }
        if(single.pathfinderMob.position().distanceTo(this.mineTargetVec3) >= Mine.maxMiningDistance)
        {
            single.pathfinderMob.getNavigation().moveTo(this.mineTargetVec3.x, this.mineTargetVec3.y, this.mineTargetVec3.z, 0.2f);
            return;
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
        if(blockState.isEmpty())
        {
            this.starter = null;
            single.groupStorage.reset();
            return;
        }
        if(this.miningProgress * Mine.miningProgressToBlastResistanceRatio > blockState.getBlock().getExplosionResistance())
        {
            Block.dropResources(blockState, single.pathfinderMob.level(), this.mineTarget);
            single.pathfinderMob.level().setBlock(this.mineTarget, Blocks.AIR.defaultBlockState(), 3);
            this.starter = null;
            single.groupStorage.reset();
        }
    }
    @Override
    public boolean handleDelete(@NotNull Single single)
    {
        if(this.starter == null)
        {
            return true;
        }
        this.starter = single;
        return false;
    }
    public Mine(@NotNull final Single starter, final int taskIndex, @NotNull final BlockPos mineTarget)
    {
        super(starter, taskIndex);
        this.mineTarget = mineTarget;
        this.mineTargetVec3 = new Vec3(mineTarget.getX(), mineTarget.getY(), mineTarget.getZ());
        UndeadArmies.logger.debug("created mine task to break block at " + mineTarget);
    }
}
