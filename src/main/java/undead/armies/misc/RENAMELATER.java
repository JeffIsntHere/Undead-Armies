package undead.armies.misc;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import undead.armies.UndeadArmies;

public class RENAMELATER
{
    public final BlockPos position;
    public final Level level;
    public final BlockPos renameLater;
    public BlockPos closest = null;
    protected double distance = Double.MAX_VALUE;
    public RENAMELATER(@NotNull final BlockPos position, @NotNull final Level level, @NotNull final BlockPos renameLater)
    {
        this.position = position;
        this.level = level;
        this.renameLater = renameLater;
    }
    public void add(@NotNull final BlockPos blockPos)
    {
        final double blockPosDistance = this.position.distSqr(blockPos);
        if(blockPosDistance > this.distance)
        {
            return;
        }
        UndeadArmies.logger.debug(this.renameLater.toString());
        final RENAMELATER2 RENAMELATER2 = new RENAMELATER2(this.renameLater, blockPos.above());
        int counter = 0;
        do
        {
            RENAMELATER2.traverse();
            final BlockState blockState = level.getBlockState(RENAMELATER2.current);
            if(!blockState.isEmpty() && !(blockState.getBlock() instanceof LiquidBlock))
            {
                return;
            }
            counter++;
        }
        while(counter < RENAMELATER2.length);
        this.distance = blockPosDistance;
        this.closest = blockPos;
    }
    public double distance()
    {
        return Math.sqrt(this.distance);
    }
}
