package undead.armies.misc.blockcast.offset;

import net.minecraft.core.BlockPos;
import org.jetbrains.annotations.NotNull;

public class NoOffset implements Base
{
    public static final Base instance = new NoOffset();
    @Override
    public BlockPos offset(@NotNull BlockPos blockPos)
    {
        return blockPos;
    }
}
