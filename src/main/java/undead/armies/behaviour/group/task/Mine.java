package undead.armies.behaviour.group.task;

import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import undead.armies.behaviour.group.task.selector.TaskSelectorStorage;
import undead.armies.behaviour.single.Single;

import java.util.ArrayList;

public class Mine extends BaseTask
{
    public static final double maxMiningDistance = 3.0d;
    public static final float blastResistanceToHitPointRatio = 8;
    protected final ArrayList<BlockPos> mineTargets;
    protected BlockPos mineTarget;
    public final Vec3 mineTargetVec3;
    public int miningProgress = 0;
    @Override
    public boolean handleTask(@NotNull final Single single, @NotNull final LivingEntity target)
    {
        if(single.pathfinderMob.position().distanceTo(this.mineTargetVec3) >= Mine.maxMiningDistance)
        {
            single.pathfinderMob.getNavigation().moveTo(this.mineTargetVec3.x, this.mineTargetVec3.y, this.mineTargetVec3.z, 0.2f);
            return this.starter.pathfinderMob.is(single.pathfinderMob);
        }
        this.miningProgress+=1;
        final Level level = single.pathfinderMob.level();
        final BlockState blockState = level.getBlockState(this.mineTarget);
        if(blockState.isEmpty())
        {
            if(this.mineTargets.isEmpty())
            {
                this.killed = true;
                return this.starter.pathfinderMob.is(single.pathfinderMob);
            }
            this.mineTarget = this.mineTargets.removeLast();
            this.miningProgress = 0;
            return this.starter.pathfinderMob.is(single.pathfinderMob);
        }
        final float requiredMiningProgress = blockState.getBlock().getExplosionResistance() * Mine.blastResistanceToHitPointRatio;
        level.playSound(null, this.mineTarget, blockState.getSoundType(level, this.mineTarget, single.pathfinderMob).getHitSound(), SoundSource.BLOCKS, (float)this.miningProgress/requiredMiningProgress * 2.0f + 1.0f, 1.0f);
        if(this.miningProgress > requiredMiningProgress)
        {
            Block.dropResources(blockState, level, this.mineTarget);
            level.setBlock(this.mineTarget, Blocks.AIR.defaultBlockState(), 3);
            level.playSound(null, this.mineTarget, blockState.getSoundType(level, this.mineTarget, single.pathfinderMob).getBreakSound(), SoundSource.BLOCKS, 3.0f, 1.0f);
        }
        return this.starter.pathfinderMob.is(single.pathfinderMob);
    }
    @Override
    public boolean handleDelete(@NotNull Single single)
    {
        if(this.killed)
        {
            return true;
        }
        this.starter = single;
        return false;
    }
    public Mine(@NotNull final Single starter, final TaskSelectorStorage taskSelectorStorage, @NotNull final ArrayList<BlockPos> mineTargets)
    {
        super(starter, taskSelectorStorage);
        this.mineTargets = mineTargets;
        this.mineTarget = this.mineTargets.removeLast();
        this.mineTargetVec3 = starter.currentPosition;
    }
}
