package undead.armies.behaviour.single.task.mine;

import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import undead.armies.behaviour.single.Single;

import java.util.List;

public class MineStorage
{
    public final List<BlockPos> blockPoss;
    public final Vec3 direction;
    public final Level level;
    protected BlockPos current;
    protected int progress = 0;
    public MineStorage(@NotNull final List<BlockPos> blockPos, @NotNull final Vec3 direction, @NotNull final Level level)
    {
        this.blockPoss = blockPos;
        this.direction = direction;
        this.level = level;
        this.current = blockPoss.getFirst();
    }
    public void tick(@NotNull final Single single)
    {
        this.progress += single.baseType.getHitPower();
        final BlockState blockState = level.getBlockState(this.current);
        final float explosionResistance = blockState.getBlock().getExplosionResistance();
        if(explosionResistance <= this.progress)
        {
            this.progress = 0;
            Block.dropResources(blockState, level, this.current);
            level.playSound(null, this.current, blockState.getSoundType(level, this.current, single.pathfinderMob).getBreakSound(), SoundSource.BLOCKS, 3.0f, 1.0f);
            for(BlockPos blockPos : blockPoss)
            {
                final BlockState nextBlockState = level.getBlockState(blockPos);
                if(nextBlockState.isEmpty() || nextBlockState.getBlock() instanceof LiquidBlock)
                {
                    continue;
                }
                this.current = blockPos;
            }

        }
        else
        {
            level.playSound(null, this.current, blockState.getSoundType(level, this.current, single.pathfinderMob).getHitSound(), SoundSource.BLOCKS, (float)this.progress/explosionResistance * 2.0f + 1.0f, 1.0f);
        }
    }
}
