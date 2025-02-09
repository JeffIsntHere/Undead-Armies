package undead.armies.misc.blockcast.offset;

import net.minecraft.core.BlockPos;
import org.jetbrains.annotations.NotNull;

public class YMinus implements Base
{
    public static final Base instance = new YMinus();
    @Override
    public BlockPos offset(@NotNull BlockPos blockPos)
    {
        return new BlockPos(blockPos.getX(), blockPos.getY() - 1, blockPos.getZ());
    }
}
