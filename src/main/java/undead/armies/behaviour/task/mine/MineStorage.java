package undead.armies.behaviour.task.mine;

import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import undead.armies.behaviour.Single;

import java.util.List;

public class MineStorage
{
    public static final float explosionMultiplier = (float) MineTask.blockHealthMultiplier.value;
    public static final float maxExplosionResistance = 100 * MineStorage.explosionMultiplier;
    public final List<BlockPos> blockPoss;
    public final Vec3 direction;
    public final Level level;
    public boolean finished = false;
    protected BlockPos current;
    protected int progress = 0;
    public MineStorage(@NotNull final List<BlockPos> blockPos, @NotNull final Vec3 direction, @NotNull final Level level)
    {
        this.blockPoss = blockPos;
        this.direction = direction;
        this.level = level;
        this.current = blockPos.getFirst();
        this.setCurrent(level);
    }
    public void setCurrent(@NotNull final Level level)
    {
        this.finished = true;
        for(BlockPos blockPos : blockPoss)
        {
            final BlockState nextBlockState = level.getBlockState(blockPos);
            if(nextBlockState.isEmpty() || nextBlockState.getBlock().getExplosionResistance() > MineStorage.maxExplosionResistance || nextBlockState.getBlock() instanceof LiquidBlock)
            {
                continue;
            }
            this.finished = false;
            this.current = blockPos;
        }
    }
    public void tick(@NotNull final Single single)
    {
        this.progress += 1;
        final BlockState blockState = this.level.getBlockState(this.current);
        final float explosionResistance = blockState.getBlock().getExplosionResistance() * MineStorage.explosionMultiplier;
        if(this.finished)
        {
            this.setCurrent(this.level);
            return;
        }
        if(explosionResistance <= this.progress)
        {
            this.progress = 0;
            Block.dropResources(blockState, this.level, this.current);
            this.level.playSound(null, this.current, blockState.getSoundType(this.level, this.current, single.pathfinderMob).getBreakSound(), SoundSource.BLOCKS, 3.0f, 1.0f);
            this.level.setBlock(this.current, Blocks.AIR.defaultBlockState(), 3);
            this.finished = true;
        }
        else
        {
            this.level.playSound(null, this.current, blockState.getSoundType(this.level, this.current, single.pathfinderMob).getHitSound(), SoundSource.BLOCKS, (float)this.progress/explosionResistance * 2.0f + 1.0f, 1.0f);
        }
    }
}
