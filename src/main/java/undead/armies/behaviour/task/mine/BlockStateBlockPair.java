package undead.armies.behaviour.task.mine;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Objects;

public class BlockStateBlockPair
{
    final BlockState left;
    final Block right;
    public BlockStateBlockPair(final BlockState left, final Block right)
    {
        this.left = left;
        this.right = right;
    }

    @Override
    public boolean equals(Object obj)
    {
        if(obj instanceof BlockStateBlockPair blockStateBlockPair)
        {
            return this.hashCode() == blockStateBlockPair.hashCode();
        }
        return false;
    }

    @Override
    public int hashCode()
    {
        if(this.left == null)
        {
            return Objects.hashCode(this.right);
        }
        return Objects.hashCode(this.left);
    }
}
