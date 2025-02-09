package undead.armies.misc;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import undead.armies.UndeadArmies;
import undead.armies.misc.blockcast.BlockRayCast;

public class ClosestUnobstructedBlock
{
    public final BlockPos position;
    public final Level level;
    public final BlockPos renameLater;
    public BlockPos closest = null;
    protected double distance = Double.MAX_VALUE;
    public ClosestUnobstructedBlock(@NotNull final BlockPos position, @NotNull final Level level, @NotNull final BlockPos renameLater)
    {
        this.position = position;
        this.level = level;
        this.renameLater = renameLater.above();
    }
    public void add(@NotNull final BlockPos blockPos)
    {
        final double blockPosDistance = this.position.distSqr(blockPos);
        if(blockPosDistance > this.distance)
        {
            return;
        }
        UndeadArmies.logger.debug(this.renameLater.toString());
        final BlockRayCast BlockRayCast = new BlockRayCast(this.level, this.renameLater, blockPos.above());
        BlockState blockState = BlockRayCast.stopWhenHit();
        while(blockState != null)
        {
            if(blockState.getBlock() instanceof LiquidBlock)
            {
                blockState = BlockRayCast.stopWhenHit();
                continue;
            }
            return;
        }
        this.distance = blockPosDistance;
        this.closest = blockPos;
    }
    public double distance()
    {
        return Math.sqrt(this.distance);
    }
}
